package com.amt.amtdata.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.amt.utils.ALOG;

/**
 * Created by DonWZ on 2016/12/21.
 * IPTV私有数据库，用于存放IPTV私有数据。
 */

public class IptvDBHelper extends SQLiteOpenHelper {

    private String tableName ;

    public static final String COL_ID= "id";
    public static final String COL_KEY = "ikey";
    public static final String COL_VALUE = "ivalue";
    public static final String COL_DES= "description";
    public static final String IPTV_DB_NAME = "iptvdb";

    public static final String SQL_QUERY = "select * from "+IPTV_DB_NAME+" where "
            +COL_KEY+"= ?";

    public IptvDBHelper(Context context) {
        super(context, IPTV_DB_NAME, null, 1);
        tableName = IPTV_DB_NAME;
    }

    public IptvDBHelper(Context context, String name,int version) {
        super(context, name, null, version);
        tableName = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ALOG.info("IptvDBHelper onCreate!!");
        String sql = "create table "+tableName+"( "
                +COL_ID+" INTEGER primary key autoincrement , " +
                COL_KEY+ " TEXT NOT NULL UNIQUE, " +
                COL_VALUE+" TEXT, " +
                COL_DES+" TEXT); ";
        ALOG.info("onCrate! sql:"+sql);
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ALOG.info("IptvDBHelper onUpgread! oldVersion : "+oldVersion+", newVersion : "+newVersion);
    }
}
