package com.amt.utils.powermanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.amt.app.IPTVActivity;
import com.amt.app.IptvApp;
import com.amt.utils.ALOG;
import com.amt.utils.IptvResidentService;
import com.amt.utils.NetUtils.NetCallback;
import com.amt.webview.WebViewManager;

/**
 * Created by DJF on 2017/6/15.
 */
public class StandbyBroadcastReceived extends BroadcastReceiver{

    //是否为亮屏
    public static boolean isScreenOn=true;
    private static String logOutAction="Logout";
    private static String TAG="StandbyBroadcastReceived";
    public static boolean isNeedKill=false; //在待机唤醒后作用于自杀的标志
    //待机锁广播
    public static StandbyBroadcastReceived broadcastWakelock = new StandbyBroadcastReceived();
    /**
     * 待机锁需要动态注册
     */
    public static void StandbyBroadcastReceived(Context mcontext){
        ALOG.info(TAG,"initAmtPowerManager");
        IntentFilter filter =new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SHUTDOWN);
        mcontext.registerReceiver(broadcastWakelock,filter);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        {
            String action =intent.getAction();
            ALOG.info(TAG,"onReceive > action : "+action);
            //收到待机唤醒的广播
            if (Intent.ACTION_SCREEN_ON.equals(action)){
                screenOn();
            }
            //收到待机广播
            if (Intent.ACTION_SCREEN_OFF.equals(action)){
                screenOff();
            }
            //收到关机广播
            if (Intent.ACTION_SHUTDOWN.equals(action)){
                shutDown();
            }

        }
    }


    /**
     * 处理待机唤醒事件的方法
     */
    public static void screenOn(){
        isScreenOn = true;
    }

    /**
     * 处理待机事件
     */
    private static void screenOff() {
        isScreenOn = false;
        //未认证的情况下，不拿待机锁，不发送logout，直接待机
        if(!IptvApp.authManager.isAuth){
            ALOG.info(TAG,"screenOff , isAuth : false .. do nothing...");
            return;
        }
        AmtPowerManager.WakeLock();
        //发送logOut
        AmtLogOut.sendLogOut(logOutAction, new NetCallback() {
            @Override
            public void onSuccess(String result) {
                ALOG.info("send logout completed .. result:"+result);
                IptvResidentService.stopService(IPTVActivity.context);
                IptvApp.authManager.isAuth=false;
                //清空webview，防止开机时出现webview最后一个画面
                WebViewManager.getManager().clearWebview();
                AmtPowerManager.WakeUnlock();
                //如果是假待机，需要杀掉IPTV自身，保证唤醒后能正常重新认证
                isNeedKill=true;
                ALOG.info("screenOff hiddenAuthUI");
                IptvApp.authManager.hiddenAuthUI();
            }

            @Override
            public void onFail(String error) {
                onSuccess(error);
            }
        });
    }

    /**
     * 处理关机事件
     */
    private static void shutDown() {

    }


}
