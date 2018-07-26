package com.amt.auth;
import android.content.Intent;

import com.amt.app.IptvApp;
import com.amt.dialog.AmtDialogManager;
import com.amt.player.iptvplayer.IPTVPlayer;
import com.amt.utils.ALOG;
import com.amt.utils.IptvResidentService;
import com.amt.utils.mainthread.MainThreadSwitcher;
import com.amt.utils.powermanager.AmtLogOut;
import com.amt.app.IPTVActivity;
import com.android.smart.terminal.iptvNew.R;

/**
 * 认证对外接口，处理认证成功和认证失败
 * Created by lyn on 2017/6/12.
 */

public class AuthExternalState {

    private static String TAG="AuthExternalState";

    public static void init(){

        if(IptvApp.authManager.authExternalInterface!=null){
            ALOG.debug(TAG,"return");
            return;
        }
        IptvApp.authManager.authExternalInterface=new AuthExternalInterface() {
            @Override
            public void onSuccess(Object... args) {
                ALOG.debug(TAG,"authExternalInterface:onSuccess: args:"+args);

                //重置logout标记位，isCallLogout与isAuth成对 一次认证一次退出
                AmtLogOut.isCallLogout=false;
                //IPTV发送成功认证广播
                Intent intentSCTY =new Intent("com.amt.IPTV_AUTH_SUCCES");
                IPTVActivity.context.sendBroadcast(intentSCTY);
                try {
                    //启动心跳包
                    IptvResidentService.startSerivce(IPTVActivity.context);
                }catch (Exception e){
                    ALOG.info(TAG,e);
                }

            }

            @Override
            public void onFail(String errorCode, String strMsg, Object... args) {
                ALOG.info(TAG,"authExternalInterface:onFail:"+errorCode+"  && msg-->"+strMsg);
                IPTVPlayer.setValue("authFaild",AuthData.authurl);
                MainThreadSwitcher.runOnMainThreadAsync(new Runnable() {
                    @Override
                    public void run() {
                        //弹出连接不上平台错误弹窗
                        try {
                            IPTVActivity.mDialogManager.showIPTVErrorDialog(AmtDialogManager.ERROR_CODE_0025
                                    ,R.string.error_title_0025
                                    ,R.string.hfdx_error_suggest_0025);
                        }catch (Exception e){
                            ALOG.debug("onFail catch error-->"+e);
                        }
                    }
                });
            }
        };
    }

}
