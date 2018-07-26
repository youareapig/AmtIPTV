package com.amt.auth;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.View;

import com.amt.auth.view.AuthUIController;
import com.amt.player.PlayerMediator;
import com.amt.utils.ALOG;
import com.amt.utils.mainthread.MainThreadSwitcher;

import java.util.List;


/**
 * Created by liaoyn on 2017/3/6.
 * 管理整个认证
 */


public class AuthManager {
    /*** 网络变化类型事件*/
    public static final int NETEVENT_TYPE_TRAFFIC=1;
    /*** 网络端口变化类型事件*/
    public static final int NETEVENT_TYPE_CONNECTIVITY=2;
    /***全局使用认证状态，此认证状态只有在Service认证完成后才更新*/
    public static  boolean isAuth=false;
    /***
     * 是否开启后台认证，默认false关闭
     */
    public static boolean AuthBackground = false;
    private static String TAG="AuthManager";
    private static Context mcontext;
    private static View mview;
    private static AuthManager authManager = new AuthManager();
    private  Intent authinten;
    private AuthInteriorInterface authInteriorInterface;//认证服务需要的内部接口
    public static  AuthUIController aucller;
    public AuthenticationService.ServicAuthInterior servicAuthCallback;//认证服务对外接口
    public AuthExternalInterface authExternalInterface;//认证对外接口


    private AuthManager(){
        authinten=new Intent();
        authinten.setAction("com.amt.authservice");
    }
    public static synchronized AuthManager init(Context context){

        ALOG.debug(TAG,"AuthManager init context:"+context);
        mcontext=context;
        if (authManager == null) {
            authManager = new AuthManager();
        }
        return authManager;
    }

    /**前台认证需要设置view并初始化进度条*/
    public void setAuthView(Context context,View view) {
        ALOG.debug(TAG,"setAuthView");
        mcontext=context;
        mview = view;
       // AuthUIController.mapPercent(true);
        aucller =new AuthUIController(mcontext, mview);//初始化认证ui  AuthUIController.getUIcontroller(mcontext, mview);
        AuthExternalState.init();//初始化认证对外接口
        startAuthService();//启动认证服务
        ALOG.debug("AuthManager setAuthView:" + view + ":context:" + mcontext);


    }

    /**
     * 启动认证服务
     * @param
     */
    public void startAuthService() {
        isAuth=false;//启动服务是复位一下认证状态为false
        if(isServiceRunning()) {
            stopService();
        }
        if (PlayerMediator.mainPlayer != null) {
            PlayerMediator.mainPlayer.stop();
        }
        mcontext.bindService(authinten, conn, Service.BIND_AUTO_CREATE);
        mcontext.startService(authinten);

    }
    private ServiceConnection conn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ALOG.debug(TAG,"onServiceConnected");
            servicAuthCallback = (AuthenticationService.ServicAuthInterior) service;
            authInteriorInterface = servicAuthCallback;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            ALOG.debug(TAG,"onServiceDisconnected");
        }
    };

    /**
     * 停止认证服务
     */
    public void stopService(){
        ALOG.debug(TAG,"stopService:"+authinten);

        if(servicAuthCallback!=null) {
            servicAuthCallback.interruptThread();
        }
        if (isServiceRunning()){
            mcontext.unbindService(conn);
            mcontext.stopService(authinten);

        }
    }

    /**
     * 认证成功更新状态
     */
    public void onAuthSuceed(){
        ALOG.debug(TAG,"onAuthSuceed");
        //如果已经认证成功了，不再重复通知成功回调。
        if(isAuth){
            ALOG.debug("Current Auth-status is true,do nothing...");
            return;
        }
        if(authInteriorInterface!=null) {
            authInteriorInterface.onAuthSuceed();
        }
        if(authExternalInterface!=null){
            MainThreadSwitcher.runOnMainThreadAsync(new Runnable() {
                @Override
                public void run() {
                    //弹出连接不上平台错误弹窗
                    try {
                        authExternalInterface.onSuccess("AuthenticationOk");
                    }catch (Exception e){
                        ALOG.debug("onSuccess catch error-->"+e);
                    }
                }
            });

        }

    }

    /**
     * 隐藏进度条
     */
    public void hiddenAuthUI(){

        ALOG.debug(TAG,"hiddenAuthUI");
        if (servicAuthCallback != null) {
            servicAuthCallback.hiddenAuthUI();
        }
    }

    /**
     * 更新100%进度条
     */
    public void updatePercent100(){

        if(servicAuthCallback!=null) {
            servicAuthCallback.updatePercent100();
        }

    }


    /**
     * 判断认证服务在运行
     * @return
     */
    private boolean isServiceRunning(){

        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) mcontext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (!(serviceList.size()>0)) {
            return isRunning;
        }
        for (int i=0; i < serviceList.size(); i++) {
            ALOG.debug(TAG,"getClassName:"+serviceList.get(i).service.getClassName());
            if (serviceList.get(i).service.getClassName().equals("com.amt.auth.AuthenticationService")) {
                isRunning = true;
                break;
            }
        }
        ALOG.debug(TAG,"isRunning:"+isRunning);
        return isRunning;
    }

    /***
     * 判断是否重新认证，如果不重新认证，还在认证中，就唤醒线程继续认证
     */
    public void checkAuth(String state) {
        ALOG.debug(TAG,"checkAuth:"+state);
        if("onPause".equals(state)){//iptv onpause 做的处理
            if (servicAuthCallback != null) {
                ALOG.debug(TAG,"onPause>>isAuth:" + isAuth);
                if (!isAuth) {
                   servicAuthCallback.interruptThread();
                }
            }
        } else if("onResume".equals(state)) {//iptv onResume 做的处理
            //认证数据是否发生过变化,如果发生变化重新认证
            if (AuthData.isDataChanged) {
                AuthData.isDataChanged = false;//复位为false
                if (PlayerMediator.mainPlayer != null) {
                    PlayerMediator.mainPlayer.stop();
                }
                startAuthService();
            } else {
                if (servicAuthCallback != null) {
                    ALOG.debug(TAG,"onResume>>isAuth:" + isAuth);
                    if (!isAuth) {
                        servicAuthCallback.authThreadNotify();
                    }
                }
            }
        }
    }

    /**
     * 对外的网络状态提示方法
     * @param netEventType
     * @param isUp
     */
    public void onNetChanged(int netEventType, boolean isUp) {

        if (authInteriorInterface != null) {
            authInteriorInterface.onNetChanged(netEventType,isUp);
        }
    }





}
