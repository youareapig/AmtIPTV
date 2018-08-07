package com.amt.auth;


import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.amt.app.IptvApp;
import com.amt.auth.view.AuthUIController;
import com.amt.config.Config;
import com.amt.net.NetConnectManager;
import com.amt.utils.ALOG;
import com.amt.utils.APKHelper;
import com.amt.utils.DeviceInfo;
import com.amt.utils.mainthread.MainThreadSwitcher;
import com.amt.webview.IPTVWebView;
import com.amt.webview.WebViewManager;
import com.amt.app.IPTVActivity;
import com.android.smart.terminal.iptvNew.R;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/**
 * Created by lyn on 2017/3/27.
 * 后台认证
 */

public class AuthenticationService extends Service {


    private static String TAG = "AuthenticationService";
    private Thread authThread;
    private Handler handler;
    private boolean isCancleEvent = false;
    private IPTVWebView iptvWebView = WebViewManager.getManager().getWebView(IPTVActivity.WEBTAG_IPTV);
    private final long Time = 5000;
    /***用于保存当前进度条值状态，认证过程中，进入其他apk的时候，返回后用此保存的状态继续认证*/
    private int saveAuthCondition = 0;
    private ProgressBarInterface progressBarInterface;
    /**
     * 默认正在认证，此状态只用于此Service。
     */
    private boolean authState = false;
    private static int authCondition = AuthUIController.SHOWPIC_NET_LinkUp;//进度条默认值
    private boolean isBackgroundAuth = false;//是否后台认证，这里先默认false，后面完善取这个状态值

    ServicAuthInterior servicAuthCallback = new ServicAuthInterior();

    /***
     * 实现认证接口，通过继承Binder 把此内部类能用于Servic外调用
     */
    public class ServicAuthInterior extends Binder implements AuthInteriorInterface {

        /**
         * @param netEventType 网络事件类型。 1： 网络变化  2：网线/wifi 端口变化
         * @param isUp         <li>当netEventType 为1时，true代表网络连接成功，false代表网络断开连接
         */
        @Override
        public void onNetChanged(int netEventType, boolean isUp) {
            ALOG.debug(TAG, "onNetChanged:" + netEventType + "==isup:" + isUp);
            switch (netEventType) {
                case AuthManager.NETEVENT_TYPE_TRAFFIC://网络拔插事件
                    if (!isUp) {

                        saveAuthCondition = authCondition = AuthUIController.SHOWPIC_LinkDown_ERROR;//10E
                        iptvWebView.stopLoading();
                    } else {
                        saveAuthCondition = authCondition = AuthUIController.SHOWPIC_NET_LinkUp;//10
                        isCancleEvent = true;

                    }
                    authThreadNotify();
                    break;
                case AuthManager.NETEVENT_TYPE_CONNECTIVITY://网络连接事件
                    if (!isUp) {
                        saveAuthCondition = authCondition = AuthUIController.SHOWPIC_DisConnect_ERROR;

                    } else {
                        saveAuthCondition = authCondition = AuthUIController.SHOWPIC_NET_Connected;//50
                        isCancleEvent = true;

                    }
                    authThreadNotify();
                    break;
            }
        }

        /**
         * @param notdataType 通知类型：1：拉起设置， 2：弹出提示框
         */
        @Override
        public void onNoDataMsg(int notdataType) {
            switch (notdataType) {
                case 1:
                    //拉起设置
                    APKHelper.goSettings(IPTVActivity.context);
                    break;
                case 2:
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //弹出提示框
                            IPTVActivity.mDialogManager.showIPTVErrorDialog("警告！！！"
                                    , R.string.error_title_datainsufficiency
                                    , R.string.error_suggest_datainsufficiency);
                        }
                    });
                    break;
            }

        }

        /***
         *
         *  认证通知:认证成功
         */
        @Override
        public void onAuthSuceed() {
            ALOG.debug(TAG, "onAuthSuceed");
            //更新认证状态
            authState = true;
            IptvApp.authManager.isAuth = true;//此状态全局使用。

            IptvApp.authManager.stopService();
        }

        /**
         * 隐藏进度条
         */
        public void hiddenAuthUI() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressBarInterface.Hidden();//隐藏进度条
                }
            });
        }

        /***
         *
         *  更新100%进度
         */
        public void updatePercent100() {
            ALOG.debug(TAG, "onAuthSuceed");
            authCondition = AuthUIController.SHOWPIC_AUTH_OK;
            //更新100进度条
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressBarInterface.updatePercent(AuthUIController.SHOWPIC_AUTH_OK);
                }
            });
        }


        /**
         * 用于唤醒线程
         */
        public void authThreadNotify() {
            ALOG.debug(TAG, "authCondition:" + authCondition + " saveAuthCondition:" + saveAuthCondition);
            authState = false;
            authCondition = saveAuthCondition;
            if (authThread.isAlive()) {
                synchronized (authThread) {
                    authThread.notify();
                }
            } else {
                authThread = new Thread(new CheckProgressBarState());
                authThread.start();
            }
        }

        /**
         * 可用于Service外暂停线程
         *
         * @param time
         */
        public void authThreadWait(long time) {
            ALOG.debug(TAG, "authThreadWait:" + time);
            synchronized (authThread) {
                try {
                    if (time > 0) {
                        authThread.wait(time);
                    } else {
                        authThread.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 认证进入其他apk时退出线程，避免后台继续认证
         */
        public void interruptThread() {
            ALOG.debug(TAG, "interruptThread:" + saveAuthCondition);
            authState = true;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ALOG.debug(TAG, "onCreate");
        authState = false;
        authCondition = AuthUIController.SHOWPIC_NET_LinkUp;
        handler = new Handler();
        progressBarInterface = AuthManager.aucller;
        //注册网络监听
        IptvApp.mNetManager.addNetWorkListener(IptvApp.app.mNetWorkProxy);
        IptvApp.mNetManager.registReceiver();
        IptvApp.mNetManager.startCheckLoop(60 * 1000);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ALOG.debug(TAG, "onStartCommand");
        //开线程监控进度条状态
        authThread = new Thread(new CheckProgressBarState());
        authThread.start();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        ALOG.debug(TAG, "Binded:" + servicAuthCallback);
        return servicAuthCallback;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        ALOG.debug(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ALOG.debug(TAG, "onDestroy");
        authState = true;//退出线程
    }

    /***
     * 内部类用于循环检测认证状态，进行进度条更新
     */
    class CheckProgressBarState implements Runnable {
        @Override
        public void run() {
            while (!authState) {//如果没有认证成功一直循环
                ALOG.debug(TAG, "authCondition:" + authCondition + "-isCancleEvent:" + isCancleEvent);
                isCancleEvent = false;
                saveAuthCondition = authCondition;
                switch (authCondition) {//进度值
                    case AuthUIController.SHOWPIC_NET_LinkUp:
                        updateNetLinkUpPercent();
                        break;
                    case AuthUIController.SHOWPIC_NET_Connected:
                        updateNetconnectedPercent();
                        break;
                    case AuthUIController.SHOWPIC_AUTH_Data:
                        updateAuthdataPercent();
                        break;
                    case AuthUIController.SHOWPIC_AUTH_Connected:
                        updateAuthconnectedPercent();
                        break;
                    case AuthUIController.SHOWPIC_AUTH_OK:
                        authok();
                        break;
                    case AuthUIController.SHOWPIC_LinkDown_ERROR:
                    case AuthUIController.SHOWPIC_DisConnect_ERROR:
                        updateNeterrorPercent(authCondition);
                        break;
                    case AuthUIController.SHOWPIC_AUTH_Data_ERROR:
                    case AuthUIController.SHOWPIC_AUTH_Connected_ERROR:
                        updateAutherrorPercent(authCondition);
                        break;

                }
                try {
                    ALOG.debug(TAG, "sleep 1000");
                    authThread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            ALOG.debug(TAG, "----OK");
        }
    }

    /**
     * 认证成功更新状态隐藏进度条
     */
    private void authok() {
        ALOG.debug(TAG, "atuthok");


    }

    /**
     * 更新网络拔插进度条 10%
     */
    private void updateNetLinkUpPercent() {
        ALOG.debug(TAG, "updateNetLinkUpPercent");
        boolean isEthUp = DeviceInfo.isFusionTerminal ? true : IptvApp.mNetManager.isEthLinkUp();
        //add by zw 20170828 当wifi连接上的情况下，isEthUp为true
        if (IptvApp.mNetManager.isWifiConnected() && !IptvApp.mNetManager.isNetworkConnected(NetConnectManager.NETTYPE_LINK)) {
            isEthUp = true;
        }
        if (isEthUp) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressBarInterface.updatePercent(AuthUIController.SHOWPIC_NET_LinkUp);//更新进度条
                    //    authCondition=AuthUIController.SHOWPIC_NET_Connected;
                }
            });
        } else {//authCondition=AuthUIController.SHOWPIC_LinkDown_ERROR;

        }
    }

    /**
     * 更新网络连接进度条 50%
     */
    private void updateNetconnectedPercent() {
        ALOG.debug(TAG, "updateNetconnectedPercent");
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressBarInterface.updatePercent(AuthUIController.SHOWPIC_NET_Connected);//更新进度条

            }
        });
        boolean isNetOk = IptvApp.mNetManager.isNetworkConnected();
        if (isNetOk) {

            authCondition = AuthUIController.SHOWPIC_AUTH_Data;
        } else {
            // authCondition=AuthUIController.SHOWPIC_DisConnect_ERROR;
        }
    }

    /**
     * 更新认证数据进度条 80%
     */
    private void updateAuthdataPercent() {
        ALOG.debug(TAG, "updateAuthdataPercent");
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressBarInterface.updatePercent(AuthUIController.SHOWPIC_AUTH_Data);//更新进度条
            }
        });
        if (Config.CheckZeroSettings && !IptvApp.checkZeroStatus()) {
            authCondition = AuthUIController.SHOWPIC_AUTH_Data_ERROR;
        } else {
            authCondition = AuthUIController.SHOWPIC_AUTH_Connected;
        }
    }

    /**
     * 更新认证进度条 85%
     */
    private void updateAuthconnectedPercent() {
        ALOG.debug(TAG, "updateAuthconnectedPercent");
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressBarInterface.updatePercent(AuthUIController.SHOWPIC_AUTH_Connected);//更新进度条
            }
        });
        //认证成功后关闭当前dialog
        IPTVActivity.mDialogManager.dismissAMTDialog();
        final String url = authLogin();//对认证地址进行测试连接，拼接认证
        ALOG.debug("url:" + url);
        if (!TextUtils.isEmpty(url)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //authCondition = AuthUIController.SHOWPIC_AUTH_Connected;
                    iptvWebView.stopLoading();
                    iptvWebView.loadUrl(url);//加载认证url
                }
            });
            servicAuthCallback.authThreadWait(0);
        } else {
            MainThreadSwitcher.runOnMainThreadAsync(new Runnable() {
                @Override
                public void run() {
                    //弹出连接不上平台错误弹窗
                    try {
                        IptvApp.authManager.authExternalInterface.onFail("0025", "Auth faild", "");//认证失败
                    } catch (Exception e) {
                        ALOG.debug("onFail catch error-->" + e);
                    }
                }
            });
            authCondition = AuthUIController.SHOWPIC_AUTH_Connected_ERROR;
        }
    }

    /**
     * 更新网络错误10-50进度条
     *
     * @param value
     */
    private void updateNeterrorPercent(int value) {
        ALOG.debug(TAG, "updateNeterrorPercent:" + value);
        if (AuthUIController.SHOWPIC_LinkDown_ERROR == value) {
            final boolean isEthUp = DeviceInfo.isFusionTerminal ? true : IptvApp.mNetManager.isEthLinkUp();
            if (isEthUp) {
                authCondition = AuthUIController.SHOWPIC_NET_LinkUp;
            } else {
                ALOG.debug(TAG, "=====" + (SystemClock.uptimeMillis()));
                if (SystemClock.uptimeMillis() < Time) {
                    servicAuthCallback.authThreadWait(Time);
                }
                if (isCancleEvent) {
                    return;
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBarInterface.updatePercent(AuthUIController.SHOWPIC_LinkDown_ERROR);//更新进度条
                        authCondition = AuthUIController.SHOWPIC_LinkDown_ERROR;
                        ALOG.debug(TAG, "delayed:isEthUp:" + authCondition + isEthUp);
                    }
                });
                servicAuthCallback.authThreadWait(0);
            }
        } else if (AuthUIController.SHOWPIC_DisConnect_ERROR == value) {
            boolean isNetOk = IptvApp.mNetManager.isNetworkConnected();
            if (isNetOk) {
                authCondition = AuthUIController.SHOWPIC_NET_Connected;
            } else {
                ALOG.debug(TAG, "=====" + (SystemClock.uptimeMillis()));
                if (SystemClock.uptimeMillis() < Time) {
                    servicAuthCallback.authThreadWait(Time);
                }
                if (isCancleEvent) {
                    return;
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressBarInterface.updatePercent(AuthUIController.SHOWPIC_DisConnect_ERROR);//更新进度条
                        authCondition = AuthUIController.SHOWPIC_DisConnect_ERROR;
                        ALOG.debug(TAG, "delayed:isEthUp:" + authCondition);
                    }
                });
                servicAuthCallback.authThreadWait(0);
            }
        }
        ALOG.debug(TAG, "showerror:" + authCondition);
    }

    /**
     * 更新认证错误80-85进度条
     *
     * @param value
     */
    private void updateAutherrorPercent(int value) {
        final int values = value;
        ALOG.debug(TAG, "updateAutherrorPercent:" + values);
        if (AuthUIController.SHOWPIC_AUTH_Data_ERROR == values || AuthUIController.SHOWPIC_AUTH_Connected_ERROR == values) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    progressBarInterface.updatePercent(values);//更新进度条
                    authCondition = values;
                }
            });
            servicAuthCallback.authThreadWait(0);
        }

    }

    /**
     * 认证
     *
     * @return
     */
    public String authLogin() {

        String strurl = "";
        if (checkData()) {
            AsyncTask<Object, Object, Object> asyncTask = new AsyncTask<Object, Object, Object>() {

                @Override
                protected Object doInBackground(Object... params) {
                    boolean isCanConnect = false;
                    String url = "";
                    String authurl = AuthData.authurl;
                    String authurlBackup = AuthData.authurlBackup;
                    String mUserID = AuthData.userID;
                    if (TextUtils.isEmpty(authurl)) {
                        return "";
                    }
                    //主备认证地址都3次访问
                    for (int i = 0; i < 6; i++) {
                        if (i == 3) {
                            if (isConnect(authurlBackup)) {
                                isCanConnect = true;
                                url = authurlBackup;
                                break;
                            } else {
                                isCanConnect = false;
                                break;
                            }
                        }
                        if (isConnect(authurl)) {
                            url = authurl;
                            isCanConnect = true;
                            break;
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                    if (!isCanConnect) {
                        url = "";

                        ALOG.debug(TAG, "====isConnect:Certification address unable to connect");
                    }
                    if (!TextUtils.isEmpty(url)) {
                        url = String.format("%s?UserID=%s&Action=%s", url, mUserID, "Login");

                    }
                    return url;
                }
            };

            try {
                strurl = (String) asyncTask.execute("执行认证").get();
                ALOG.debug(TAG, "--str:" + strurl);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            //数据不完整，拉起设置或者弹出提示框
            MainThreadSwitcher.runOnMainThreadAsync(new Runnable() {
                @Override
                public void run() {
                    //弹出连接不上平台错误弹窗
                    try {
                        servicAuthCallback.onNoDataMsg(2);
                    } catch (Exception e) {
                        ALOG.debug("error-->" + e);
                    }
                }
            });

        }
        return strurl;
    }

    /**
     * 认证前检测数据是否完整，账号及密码，认证地址
     *
     * @return
     */
    private static boolean checkData() {
        ALOG.debug(TAG, "checkData:");
        ALOG.secretLog(TAG, "AuthData.userID->" + AuthData.userID);
        ALOG.secretLog(TAG, "AuthData.password->" + AuthData.password);
        ALOG.secretLog(TAG, "AuthData.authurl->" + AuthData.authurl);
        if (TextUtils.isEmpty(AuthData.userID) || TextUtils.isEmpty(AuthData.password) || TextUtils.isEmpty(AuthData.authurl)) {
            ALOG.debug(TAG, "checkData-->false");
            return false;
        }
        ALOG.debug(TAG, "checkData-->true");
        return true;
    }

    /**
     * url是否能请求ok
     *
     * @param url
     * @return
     */
    private static boolean isConnect(String url) {
        if (TextUtils.isEmpty(url) || !URLUtil.isNetworkUrl(url)) {
            ALOG.debug(TAG, "--url==" + url);
            return false;
        }
        URL urls = null;
        int code = 0;
        HttpURLConnection httpURLConnection = null;
        try {
            urls = new URL(url);
            httpURLConnection = (HttpURLConnection) urls.openConnection();
            httpURLConnection.setInstanceFollowRedirects(false);//不重定向
            httpURLConnection.setConnectTimeout(3 * 1000);
            httpURLConnection.setReadTimeout(3 * 1000);
            code = httpURLConnection.getResponseCode();
            try {
                if (httpURLConnection.getURL().getFile().toLowerCase().indexOf("sessiontimeout.jsp") > 0) {
                    code = -9999;
                }
            } catch (Exception e) {
                ALOG.debug(TAG, "--isconnect-1-err:" + e.toString());
            }

        } catch (Exception e) {
            ALOG.debug(TAG, "--isconnect-2-err:" + e.toString());
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        ALOG.debug(TAG, "--url:" + url + "--code:" + code);
        if (code == httpURLConnection.HTTP_OK || code == httpURLConnection.HTTP_MOVED_TEMP) {

            return true;
        }
        return false;
    }
}
