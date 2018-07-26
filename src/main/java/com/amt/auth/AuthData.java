package com.amt.auth;

import android.os.RemoteException;

import com.amt.amtdata.AmtDataManager;
import com.amt.amtdata.IDataCallBack;
import com.amt.amtdata.IPTVData;
import com.amt.utils.ALOG;

/**
 * Created by DonWZ on 2017/5/12.
 */

public class AuthData {

    public static String userID = "";
    public static String password = "";
    public static String authurl = "";
    public static String authurlBackup = "";

    /**标识认证数据是否发生过变化。用在IPTV onResume里作为是否重新认证的判断。使用过后需要重置*/
    public static boolean isDataChanged = false;

    public static void init(){
        userID = AmtDataManager.getString(IPTVData.IPTV_Account,""); //测试数据
        password = AmtDataManager.getString(IPTVData.IPTV_Password, ""); //测试数据
        authurl = AmtDataManager.getString(IPTVData.IPTV_AuthURL, ""); //测试数据
        authurlBackup = AmtDataManager.getString(IPTVData.IPTV_AuthURLBackup, ""); //测试数据
        AmtDataManager.setDataCallback("IPTVSelf_AuthData", new IDataCallBack.Stub() {
            @Override
            public void dataChanged(String key, String oldValue, String newValue) throws RemoteException {
                ALOG.info("AuthData >> dataChanged ! [ "+key+": old > "+oldValue+", new > "+newValue+" ]");
                if(IPTVData.IPTV_Account.equals(key)){
                    /**
                     *  2018.03.15 add by xw 认证成功的情况下，认证数据发出变化，才重置isDataChanged的值.
                     *  (认证的时候零配置下发认证数据，如果设置isDataChanged的值为true，进入页面后跳到其他应用再返回时，
                     *  进入onResume方法中，检测到该isDataChanged为true，则会重新启动认证服务，显示进度条)
                     */
                    if (AuthManager.isAuth){
                        isDataChanged = true;
                    }
                    userID = newValue;
                }else if(IPTVData.IPTV_Password.equals(key)){
                    if (AuthManager.isAuth){
                        isDataChanged = true;
                    }
                    password = newValue;
                }else if(IPTVData.IPTV_AuthURL.equals(key)){
                    if (AuthManager.isAuth){
                        isDataChanged = true;
                    }
                    authurl = newValue;
                }else if(IPTVData.IPTV_AuthURLBackup.equals(key)){
                    if (AuthManager.isAuth){
                        isDataChanged = true;
                    }
                    authurlBackup = newValue;
                }
            }
        });
    }
}
