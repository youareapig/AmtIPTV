package com.amt.amtdata.backupdb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.amt.amtdata.AmtDataManager;
import com.amt.utils.ALOG;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * IPTV 备份数据库管理器。提供数据备份、还原数据、获取备份数据的接口
 * Created by DonWZ on 2017/1/24.
 */

public class BackupDBManager {

    private SQLiteDatabase readDB;
    private SQLiteDatabase writeDB;
    private ExecutorService mThreadPool;
    private static BackupDBManager instance ;
    /**用于存放数据库已有的key字段*/
    ArrayList<String> keys = new ArrayList<String>();
    private Context mContext;

    public synchronized static BackupDBManager getBackManager(Context context){
        if(instance == null){
            instance = new BackupDBManager(context);
        }
        return instance;
    }

    private BackupDBManager(Context context){
        mContext = context;
        BackupDBHelper bakeDbHelper = new BackupDBHelper(context);
        readDB = bakeDbHelper.getReadableDatabase();
        writeDB = bakeDbHelper.getWritableDatabase();
        initKeys();
        //考虑到可能会有短时间内重复写某数据的可能性，选择使用newSingleThreadExecutor线程池，按照顺序执行，保证存入的备份数据是最后更新的。
        mThreadPool = Executors.newSingleThreadExecutor();
    }

    /**
     * 初始化查询数据库已有的ikey字段所有值，用于setValue接口判断该insert还是update
     */
    private void initKeys(){
        if(readDB!=null){
            String sql = "select "+ BackupDBHelper.COL_KEY+" from "+ BackupDBHelper.IPTV_DB_NAME_BAKE;
            try{
                Cursor mCursor = readDB.rawQuery(sql,null);
                if(mCursor!=null){
                    while(mCursor.moveToNext()){
                        String key = mCursor.getString(0);
                        keys.add(key);
                    }
                    mCursor.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }


    /***
     * 获取备份数据
     * @param key
     * @return
     */
    public BackupData getBackupData(String key){
        BackupData bakeData = getData(key);
        ALOG.info("getBackupData > "+bakeData.toString());
        return bakeData;
    }

    /**
     * 备份数据
     * @param key
     * @param newValue
     * @param mender  修改进程的包名
     */
    public void backupData(final String key,final String newValue,final String mender){
        if(TextUtils.isEmpty(key)){
            return;
        }
//        ALOG.info("bakeup Data ! key : "+key+", newValue : "+newValue);
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                String modifyDate = sdf.format(new Date(System.currentTimeMillis()));
                BackupData newBakeData = new BackupData();
                newBakeData.key = key;
                newBakeData.curData = newValue;
                newBakeData.modifyDate = modifyDate;
                newBakeData.mender = mender;
                doBackup(newBakeData);
            }
        });
    }

    /**
     * 批量备份数据
     * @param values
     */
    public void backupData(final ContentValues values){
        if(values == null || values.size() == 0){
            return;
        }
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                Iterator it = values.keySet().iterator();
                BackupData backupData = new BackupData();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                String mender = mContext.getPackageName();
                while(it.hasNext()){
                    String key = (String)it.next();
                    String value = values.getAsString(key);
                    backupData.key = key;
                    backupData.curData = value;
                    String modifyDate = sdf.format(new Date(System.currentTimeMillis()));
                    backupData.modifyDate = modifyDate;
                    backupData.mender = mender;
                    doBackup(backupData);
                    ALOG.debug(AmtDataManager.TAG,"backupDataBatch>"+key+":"+value);
                }
            }
        });
    }

    private void doBackup(BackupData data){
        if(data == null){
            return;
        }
        if(keys.contains(data.key)){
            //若备份数据库已经有这个字段的数据了，先把原来的备份数据取出来
            BackupData lastBakeData = getData(data.key);
            if(lastBakeData!=null){
                data.id = lastBakeData.id;
                //如果新存入的值和当前值是一样的，则只更新当前值的更新日期，上次更新的信息保持不变
                if(data.curData.equals(lastBakeData.curData)){
                    data.lastData = lastBakeData.lastData;
                    data.lastModifyDate = lastBakeData.lastModifyDate;
                    data.lastMender = lastBakeData.lastMender;
                }else{
                    data.lastData = lastBakeData.curData;
                    data.lastModifyDate = lastBakeData.modifyDate;
                    data.lastMender = lastBakeData.mender;
                }
            }
        }
        if(putData(data)){
            ALOG.debug(AmtDataManager.TAG,"bake up data success! "+data.toString());
        }else{
            ALOG.debug(AmtDataManager.TAG,"bake up data failed!");
        }
    }

    private boolean putData(BackupData bakeData){
        boolean isSucces = false;
        //先查询备份数据库有没有key字段的数据，判断是insert还是update
        if(bakeData!=null && writeDB!=null){
            ContentValues values = new ContentValues();
            values.put(BackupDBHelper.COL_KEY,bakeData.key);
            values.put(BackupDBHelper.COL_VALUE,bakeData.curData);
            values.put(BackupDBHelper.COL_VALUE_MODIFY_DATE,bakeData.modifyDate);
            values.put(BackupDBHelper.COL_VALUE_MENDER,bakeData.mender);
            values.put(BackupDBHelper.COL_LAST_VALUE,bakeData.lastData);
            values.put(BackupDBHelper.COL_LAST_MODIFY_DATE,bakeData.lastModifyDate);
            values.put(BackupDBHelper.COL_LAST_MENDER,bakeData.lastMender);
            if(keys.contains(bakeData.key)){
                writeDB.update(BackupDBHelper.IPTV_DB_NAME_BAKE,values, BackupDBHelper.COL_KEY+"=?",new String[]{bakeData.key});
                isSucces = true;
            }else{
                writeDB.insert(BackupDBHelper.IPTV_DB_NAME_BAKE,null,values);
                isSucces = true;
                keys.add(bakeData.key);
            }
        }
        return isSucces;
    }

    private BackupData getData(String key){
        BackupData bakeData = null;
        if(readDB!=null){
            String querySql = "select * from "+ BackupDBHelper.IPTV_DB_NAME_BAKE+" where "+ BackupDBHelper.COL_KEY+"=?";
            Cursor mCursor = readDB.rawQuery(querySql,new String[]{key});
            if(mCursor!=null && mCursor.moveToFirst()){
                String id = String.valueOf(mCursor.getInt(mCursor.getColumnIndex(BackupDBHelper.COL_ID)));
                String curData = mCursor.getString(mCursor.getColumnIndex(BackupDBHelper.COL_VALUE));
                String modifyDate = mCursor.getString(mCursor.getColumnIndex(BackupDBHelper.COL_VALUE_MODIFY_DATE));
                String mender = mCursor.getString(mCursor.getColumnIndex(BackupDBHelper.COL_VALUE_MENDER));
                String lastData = mCursor.getString(mCursor.getColumnIndex(BackupDBHelper.COL_LAST_VALUE));
                String lastModifyDate = mCursor.getString(mCursor.getColumnIndex(BackupDBHelper.COL_LAST_MODIFY_DATE));
                String lastMender = mCursor.getString(mCursor.getColumnIndex(BackupDBHelper.COL_LAST_MENDER));
                bakeData = new BackupData(id,key,curData,modifyDate,mender,lastData,lastModifyDate,lastMender);
            }
            mCursor.close();
        }
        return bakeData;
    }

}
