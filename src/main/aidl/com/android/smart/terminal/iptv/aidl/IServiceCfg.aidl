package com.android.smart.terminal.iptv.aidl;

interface IServiceCfg{  
    boolean putString(String key,String value);
    boolean putInt(String key,int value);
    boolean putBoolean(String key,boolean value);

    String getString(String key,String defValue);
    int getInt(String key,int defValue); 
    boolean getBoolean(String key,boolean defValue);     
    
    String getChannelUrl();
} 