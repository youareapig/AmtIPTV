package com.amt.utils.powermanager;

/**
 * Created by DJF on 2017/6/20.
 */

import android.text.TextUtils;
import android.webkit.URLUtil;

import com.amt.app.IptvApp;
import com.amt.auth.AuthData;
import com.amt.utils.ALOG;
import com.amt.utils.NetUtils.HttpUtils;
import com.amt.utils.NetUtils.NetCallback;

/**
 *注销或模式切换IPTV
 *是否是正常的IPTV程序退出,进入待机不是退出(false)
 */
public class AmtLogOut {

    // 是否调用了logout
    public static boolean isCallLogout = false;
    private static String TAG="AmtLogOut";
    //获取webview实例
     public static void sendLogOut(String action,final NetCallback callback){
         ALOG.debug("sendLogOut");
         if (!IptvApp.authManager.isAuth){
             ALOG.debug(TAG, "IPTV is unAuth");
             return;
         }
         //是否已经调用了Logout
         if (isCallLogout){
             ALOG.debug(TAG, "---already call logout, please do not repeat request----");
             return;
         }

         //检查Logout条件是否充分
         if (!URLUtil.isNetworkUrl(AuthData.authurl) && TextUtils.isEmpty(AuthData.userID)){
         }
         //拼接Logout需要的地址
         String logout_url= String.format("%s?UserID=%s&Action=" + action + "", AuthData.authurl, AuthData.userID);
         ALOG.debug("logout_url-->"+logout_url);
         HttpUtils.get(logout_url, new NetCallback() {
             @Override
             public void onSuccess(String result) {
                 if(callback != null){
                     callback.onSuccess(result);
                 }
             }

             @Override
             public void onFail(String error) {
                 if(callback != null){
                     callback.onFail(error);
                 }
             }

             @Override
             public void on302Moved(String location) {

             }
         });
     }
}
