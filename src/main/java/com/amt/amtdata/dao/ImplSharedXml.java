package com.amt.amtdata.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import com.amt.utils.ALOG;

import java.util.Iterator;

/**
 * SharedPreferenc xml方案
 * Created by DonWZ on 2017/1/13.
 */

public class ImplSharedXml implements IDataInterface{

    public static final String CFG_PREFS_NAME = "iptv_prefs";
    SharedPreferences mSharedPref;
    private Context mContext;
    public ImplSharedXml(Context context){
        mContext = context;
        mSharedPref = context.getSharedPreferences(CFG_PREFS_NAME,Context.MODE_PRIVATE);
    }

    @Override
    public String getString(String key, String defValue) {
        String value = defValue;
        if(mSharedPref!=null){
            value = mSharedPref.getString(key,defValue);
        }
        return value;
    }

    @Override
    public int putString(String key, String value) {
        int result = 0;
        if(mSharedPref!=null){
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putString(key,value);
            result = editor.commit()?1:0;
        }
        return result;
    }

    @Override
    public int getInt(String key, int defValue) {
        int value = defValue;
        if(mSharedPref!=null){
            value = mSharedPref.getInt(key,defValue);
        }
        return value;
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        boolean value = false;
        if(mSharedPref!=null){
            value = mSharedPref.getBoolean(key,defaultValue);
        }
        return value;
    }

    @Override
    public int putBoolean(String key, boolean value) {
        int result = 0;
        if(mSharedPref!=null){
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putBoolean(key,value);
            result = editor.commit()?1:0;
        }
        return result;
    }

    @Override
    public int putInt(String key, int value) {
        int result = 0;
        if(mSharedPref!=null){
            SharedPreferences.Editor editor = mSharedPref.edit();
            editor.putInt(key,value);
            result = editor.commit()?1:0;
        }
        return result;
    }

    @Override
    public int putStringBatch(ContentValues values) {
        ALOG.info("","wz === putStringBatch > xml!!!");
        int result = 0;
        if(mSharedPref!=null){
            try{
                SharedPreferences.Editor editor = mSharedPref.edit();
                Iterator it = values.keySet().iterator();
                while(it.hasNext()){
                    String key = (String)it.next();
                    String value = values.getAsString(key);
                    editor.putString(key,value);
                }
                result = editor.commit()?1:0;
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public String getFilePath() {
        return "/data/data/"+mContext.getPackageName()+"/shared_prefs/"+CFG_PREFS_NAME+".xml";
    }

}
