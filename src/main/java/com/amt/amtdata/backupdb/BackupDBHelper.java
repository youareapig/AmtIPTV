package com.amt.amtdata.backupdb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.amt.utils.ALOG;

/**
 * IPTV备份数据库，备份IPTV业务数据，记录当前值，上次更新的值自己更新日期、更新的进程ID等信息。
 * Created by DonWZ on 2017/1/23.
 */

class BackupDBHelper extends SQLiteOpenHelper {

    public static final String IPTV_DB_NAME_BAKE = "iptv_backup";

    protected static final String COL_ID = "_id";
    protected static final String COL_KEY = "key";
    /**当前值*/
    protected static final String COL_VALUE = "value";
    /**当前值的更新日期*/
    protected static final String COL_VALUE_MODIFY_DATE = "modify_date";
    /**当前值的修改者（一般记录进程的id名）*/
    protected static final String COL_VALUE_MENDER = "mender";
    /**上次保存的值。作为备份数据*/
    protected static final String COL_LAST_VALUE = "last_value";
    /**上次更新数据的日期*/
    protected static final String COL_LAST_MODIFY_DATE = "last_modify_date";
    /**上次更新数据的进程id*/
    protected static final String COL_LAST_MENDER = "last_mender";

    public BackupDBHelper(Context context) {
        super(new DBContext(context), IPTV_DB_NAME_BAKE, null, 1);
    }

    public BackupDBHelper(Context context, String name, int version) {
        super(new DBContext(context), name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ALOG.info("BackupDBHelper onCreate!!");
        String sql = "create table "+IPTV_DB_NAME_BAKE+"( "
                +COL_ID+" INTEGER primary key autoincrement , " +
                COL_KEY+ " TEXT NOT NULL UNIQUE, " +
                COL_VALUE+" TEXT, " +
                COL_VALUE_MODIFY_DATE+" TEXT, " +
                COL_VALUE_MENDER+" TEXT, " +
                COL_LAST_VALUE+" TEXT, " +
                COL_LAST_MODIFY_DATE+" TEXT, " +
                COL_LAST_MENDER+" TEXT); ";
        ALOG.info("BackupDBHelper > onCrate! sql:"+sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ALOG.info("BackupDBHelper onUpgread! oldVersion : "+oldVersion+", newVersion : "+newVersion);
    }
}
