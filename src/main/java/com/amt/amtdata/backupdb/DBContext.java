package com.amt.amtdata.backupdb;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;

/**
 * 用于备份数据库使用。主要固定了备份数据库的文件位置。在HISI芯片7.0版本上发现，文件位置会大概率指向到 /data/user/0/com.....里，而导致挂掉。
 * Created by DonWZ on 2017/4/13.
 */

public class DBContext extends ContextWrapper{


    public DBContext(Context base) {
        super(base);
    }

    @Override
    public File getDatabasePath(String name) {
        String databasePath = "/data/data/"+getApplicationContext().getPackageName()+"/backup/"+name;
        if(!databasePath.endsWith(".db")){
            databasePath += ".db";
        }
        File backUpFile = new File(databasePath);
        if(!backUpFile.getParentFile().exists()){
            try{
                backUpFile.getParentFile().mkdirs();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return backUpFile;
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
        return super.openOrCreateDatabase(name, mode, factory);
    }

    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
        SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
        return database;
    }
}
