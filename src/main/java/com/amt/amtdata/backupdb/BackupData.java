package com.amt.amtdata.backupdb;

/**
 * Created by DonWZ on 2017/1/24.
 */

public class BackupData {
    public String id="0";
    public String key="";
    /**新数据（当前数据）*/
    public String curData="";
    /**当前数据的更新日期，yyyy-MM-dd HH:mm:ss.SSS格式*/
    public String modifyDate="";
    /**当前数据的修改进程的包名*/
    public String mender="";
    /**上次更新的数据*/
    public String lastData="";
    /**上次更新的日期，格式同modifyDate*/
    public String lastModifyDate="";
    /**上次更新的进程包名*/
    public String lastMender="";

    public BackupData(){}

    public BackupData(String id, String key, String curData, String modifyDate, String mender, String lastData, String lastModifyDate, String lastMender){
        this.id = id;
        this.key = key;
        this.curData = curData;
        this.modifyDate = modifyDate;
        this.mender = mender;
        this.lastData = lastData;
        this.lastModifyDate = lastModifyDate;
        this.lastMender = lastMender;
    }

    @Override
    public String toString() {
        return "BakeUpData[id:"+id+",key:"+key+",curData:"+curData+",modifyDate:"+modifyDate+",mender:"+mender+",lastData:"+lastData+",lastModifyDate:"+lastModifyDate+",lastMender:"+lastMender+"]";
    }
}
