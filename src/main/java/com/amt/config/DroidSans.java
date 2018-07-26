package com.amt.config;

import android.text.TextUtils;
import android.util.Log;

import com.amt.app.IptvApp;
import com.amt.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * DroidSans.ttf读取类
 * Created by DonWZ on 2017/4/28.
 */

public class DroidSans {
    /**缓存的ttf配置json对象*/
    private static JSONObject configJson;
    static{
        // TODO 读取DroidSans.ttf配置文件，将配置项读取到内存，如DEBUG、LOGTOFILE等配置。
        InputStream is = null;
        try {
            is = IptvApp.app.getResources().getAssets().open("ITV/DroidSans.ttf");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (is != null) {
            try {
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int nLen = 0;
                while ((nLen = is.read(buffer)) != -1) {
                    os.write(buffer, 0, nLen);
                }
                String strCfg = Utils.NvDecode(os.toByteArray());
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                strCfg = strCfg.replaceAll("##.*##", "");
                configJson = new JSONObject(strCfg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getConfigStr(String key){
        if(configJson!=null && !TextUtils.isEmpty(key)){
            if(configJson.has(key)){
                try {
                    return configJson.getString(key);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    public static boolean getConfigBoolean(String key){
        if(configJson!=null && !TextUtils.isEmpty(key)){
            if(configJson.has(key)){
                try {
                    return configJson.getBoolean(key);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static int getConfigInt(String key){
        if(configJson!=null && !TextUtils.isEmpty(key)){
            if(configJson.has(key)){
                try {
                    return configJson.getInt(key);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return -1;
    }
}
