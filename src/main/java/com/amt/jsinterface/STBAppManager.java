package com.amt.jsinterface;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.SyMedia.webkit.SyJavascriptInterface;
import com.amt.app.IPTVActivity;
import com.amt.app.IptvApp;
import com.amt.utils.ALOG;
import com.amt.utils.APKHelper;
import com.amt.utils.Utils;
import com.android.org.sychromium.content.browser.JavascriptInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by DonWZ on 2017/6/8.
 */

public class STBAppManager {

    private static final String TAG = "STBAppManager";

    @JavascriptInterface
    @SyJavascriptInterface
    public void startAppByIntent(String intentMessage) {
        ALOG.info(TAG,"startAppByIntent > "+intentMessage);
        startActivityByIntent(intentMessage);
    }

    /**
     * 启动某个APP
     * @param packageName
     */
    @JavascriptInterface    @SyJavascriptInterface
    public void startAppByName(String packageName) {
        ALOG.info(TAG,"startAppByName > packageName");
        startActivityByName(packageName);
    }
    /**
     * 启动某个APP
     * @param intentMessage
     */
    @JavascriptInterface
    @SyJavascriptInterface
    public void startActivityByIntent(String intentMessage) {
        ALOG.info(TAG,"startActivityByIntent > "+intentMessage);
        try{
            JSONObject intentJsonObject = new JSONObject(intentMessage);
            int intentType = intentJsonObject.getInt("intentType");
            Intent appIntent = null;
            switch (intentType){
                case 0://显式调用，通过包类来启动应用。
                    String appName = intentJsonObject.getString("appName");
                    String className = intentJsonObject.getString("className");
                    if (TextUtils.isEmpty(className)) {
                        PackageManager packageManager = IptvApp.app.getPackageManager();
                        appIntent = packageManager.getLaunchIntentForPackage(appName);
                    } else {
                        appIntent = new Intent(Intent.ACTION_MAIN);
                        ComponentName cn = new ComponentName(appName, className);
                        appIntent.setComponent(cn);
                    }
                    break;
                case 1://隐式调用，通过action来启动应用。
                    String action = intentJsonObject.getString("action");
                    appIntent = new Intent(action);// action形式
                    break;
            }
            if(appIntent!=null){
                if(intentJsonObject.has("extra")){
                    JSONArray extra = null;
                    try{
                        extra = intentJsonObject.getJSONArray("extra");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    if(extra != null){
                        String key = "";
                        for (int i = 0; i < extra.length(); i++) {
                            JSONObject obj = extra.getJSONObject(i);
                            Iterator it = obj.keys();
                            String extraKey = "";
                            String extraValue = "";
                            for (int j = 0; j < obj.length(); j++) {
                                key = (String) it.next();
                                if("name".equalsIgnoreCase(key) || "key".equalsIgnoreCase(key)  || "value".equalsIgnoreCase(key)){
                                    if("name".equalsIgnoreCase(key) || "key".equalsIgnoreCase(key) ){
                                        extraKey = obj.getString(key);
                                    }else{
                                        extraValue = obj.getString(key);
                                    }
                                    if(!TextUtils.isEmpty(extraKey) && !TextUtils.isEmpty(extraValue)){
                                        appIntent.putExtra(extraKey, extraValue);
//                                        ALOG.debug("startActivityByIntent put extra! : extraKey:"+extraKey+", extraValue:"+extraValue);
                                    }
                                }else{
//                                    ALOG.debug("startActivityByIntent put extra! : extraKey:"+key+", extraValue:"+obj.getString(key));
                                    appIntent.putExtra(key, obj.getString(key));
                                }
                            }
                        }
                    }
                }
                appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ALOG.info(TAG,"startActivityByIntent > intent extra : "+ Utils.viewBundle(appIntent.getExtras()));
                IPTVActivity.context.startActivity(appIntent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 启动某个APP
     * @param packageName
     */
    @JavascriptInterface
    @SyJavascriptInterface
    public void startActivityByName(String packageName) {
        ALOG.info(TAG,"startActivityByName > packageName");
        try {
            PackageManager packageManager = IptvApp.app.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                IPTVActivity.context.startActivity(intent);
            } else {
                ALOG.error("APK is not installed!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断某个APP是否已安装
     * @param packageName
     * @return
     */
    @JavascriptInterface
    @SyJavascriptInterface
    public boolean isAppInstalled(String packageName) {
        boolean isAppIstalled  = APKHelper.isAppInstalled(IptvApp.app,packageName);
        ALOG.info(TAG,"isAppInstalled > "+packageName +": "+isAppIstalled);
        return isAppIstalled;
    }

    /**
     * 获取APP的软件版本号
     * @param packageName
     * @return
     */
    @JavascriptInterface
    @SyJavascriptInterface
    public String getAppVersion(String packageName){
        String version = APKHelper.getAppVersionName(IptvApp.app,packageName);
        ALOG.info(TAG,"getAppVersion > "+packageName+" : "+version);
        return version;
    }

    /**
     * 重启某个APP
     * @param appPackageName
     */
    @JavascriptInterface
    @SyJavascriptInterface
    public void restartAppByName(String appPackageName) {
        ALOG.info(TAG,"restartAppByName > "+appPackageName);
        Intent intent = IptvApp.app.getPackageManager()
                .getLaunchIntentForPackage(appPackageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        IPTVActivity.context.startActivity(intent);
    }

    /**
     * 下载某个APP
     * @param downloadUrl
     * @return
     */
    @JavascriptInterface
    @SyJavascriptInterface
    public boolean downloadApp(String downloadUrl){
        return installApp(downloadUrl);
    }
    /**
     * 下载某个APP
     * @param downloadUrl
     * @return
     */
    @JavascriptInterface
    @SyJavascriptInterface
    public boolean installApp(String downloadUrl){
        if(TextUtils.isEmpty(downloadUrl)){
            return false;
        }
        try {
            String apkName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String filePath = "/data/local"+apkName;
            ALOG.info(TAG,"downloadApp > url :"+downloadUrl+", filename : "+filePath);
            APKHelper.dowloadApkAndInstall(downloadUrl,filePath,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

}
