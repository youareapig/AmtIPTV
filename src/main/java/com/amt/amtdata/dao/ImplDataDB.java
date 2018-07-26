package com.amt.amtdata.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.amt.config.Config;
import com.amt.utils.ALOG;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 数据库方案。
 * Created by DonWZ on 2016/12/21.
 */

public class ImplDataDB implements IDataInterface {

    private IptvDBHelper dbHelper;
    private SQLiteDatabase readDB;
    private SQLiteDatabase writeDB;
    /**
     * 用于存放数据库已有的ikey字段
     */
    ArrayList<String> keys = new ArrayList<String>();
    private Context mContext;

    public ImplDataDB(Context context) {
        //TODO 不能将dbHelper初始化的代码放在这里，避免在IPTV进程一起来的时候aidl被其他应用绑定上立刻创建数据库文件。
        mContext = context;
    }

    /**
     * 初始化查询数据库已有的ikey字段所有值，用于setValue接口判断该insert还是update
     */
    public void initKeys() {
        if (dbHelper == null) {
            initDBHelper();
        }
        String sql = "select " + IptvDBHelper.COL_KEY + " from " + IptvDBHelper.IPTV_DB_NAME;
        try {
            Cursor mCursor = readDB.rawQuery(sql, null);
            if (mCursor != null) {
                while (mCursor.moveToNext()) {
                    String key = mCursor.getString(0);
                    keys.add(key);
                }
                mCursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initDBHelper() {
        dbHelper = new IptvDBHelper(mContext);
        readDB = dbHelper.getReadableDatabase();
        writeDB = dbHelper.getWritableDatabase();
    }

    @Override
    public String getString(String key, String defValue) {
        if (keys.isEmpty()) {
            initKeys();
        }
        if (readDB == null) {
            initDBHelper();
        }
        Cursor mCursor = readDB.rawQuery(IptvDBHelper.SQL_QUERY, new String[]{key});
        if (mCursor != null && mCursor.moveToFirst()) {
            String value = mCursor.getString(mCursor.getColumnIndex(IptvDBHelper.COL_VALUE));
            ALOG.info("getvalue from db > key : " + key + ", value : " + value);
            if (TextUtils.isEmpty(value)) {
                value = defValue;
            }
            mCursor.close();
            return value;
        }
        return defValue;
    }

    @Override
    public int putString(String key, String value) {
        int result = -1;
        if (keys.isEmpty()) {
            initKeys();
        }
        if (readDB == null) {
            initDBHelper();
        }
        ContentValues values = new ContentValues();
        values.put(IptvDBHelper.COL_KEY, key);
        values.put(IptvDBHelper.COL_VALUE, value);
        //判断下是insert还是update
        if (keys.contains(key)) {
            ALOG.info("wz === update ! key : " + key + ",value :" + value);
            try {
                writeDB.update(IptvDBHelper.IPTV_DB_NAME, values, IptvDBHelper.COL_KEY + "= ?", new String[]{key});
                result = 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ALOG.info("wz === insert ! key : " + key + ",value :" + value);
            try {
                writeDB.insert(IptvDBHelper.IPTV_DB_NAME, null, values);
                keys.add(key);
                result = 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public int getInt(String key, int devValue) {
        int result = devValue;
        try {
            result = Integer.valueOf(getString(key, "0"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public int putInt(String key, int value) {
        return putString(key, String.valueOf(value));
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        boolean result = defaultValue;
        try {
            int value = Integer.valueOf(getString(key, "0"));
            result = value != 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public int putBoolean(String key, boolean value) {
        return putString(key, value ? "1" : "0");
    }


    @Override
    public int putStringBatch(ContentValues values) {
        ALOG.info("info", "putStringBatch > DB!!!");
        int result = 0;
        if (keys.isEmpty()) {
            initKeys();
        }
        if (readDB == null) {
            initDBHelper();
        }
        writeDB.beginTransaction();
        try {
            Iterator it = values.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                String value = values.getAsString(key);
                String sql = "";
                if (keys.contains(key)) {
                    //update
                    sql = "update " + IptvDBHelper.IPTV_DB_NAME + " set " + IptvDBHelper.COL_VALUE + "='" + value + "' where " + IptvDBHelper.COL_KEY + "='" + key + "'";
                } else {
                    //insert
                    sql = "insert into " + IptvDBHelper.IPTV_DB_NAME + "(" + IptvDBHelper.COL_KEY + "," + IptvDBHelper.COL_VALUE + ") values ('" + key + "','" + value + "')";
                    keys.add(key);
                }
                ALOG.info("", "wz === putStringBatch > execSql : " + sql);
                writeDB.execSQL(sql);
            }
            writeDB.setTransactionSuccessful();
            result = 1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writeDB.endTransaction();
        }
        return result;
    }

    @Override
    public String getFilePath() {
        return "/data/data/"+mContext.getPackageName()+"/databases/"+IptvDBHelper.IPTV_DB_NAME+".db";
    }
}
