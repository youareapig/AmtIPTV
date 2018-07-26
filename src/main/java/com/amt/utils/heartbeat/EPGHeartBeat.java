package com.amt.utils.heartbeat;

import android.os.Handler;
import android.text.TextUtils;

import com.amt.amtdata.AmtDataManager;
import com.amt.amtdata.IPTVData;
import com.amt.jsinterface.Iptv2EPG;
import com.amt.app.IptvApp;
import com.amt.utils.ALOG;
import com.amt.utils.DeviceInfo;
import com.amt.utils.NetUtils.HttpUtils;
import com.amt.utils.NetUtils.NetCallback;

import java.net.URL;
import java.util.HashMap;

import okhttp3.Headers;

/**
 * Created by DJF on 2017/6/8.
 * 发送心跳包线程
 */
public class EPGHeartBeat{

    private String TAG="EPGHearBeat";
    //心跳计数器
    private static int heartbeatcount=0;
    private String platform;
    private String UserStatus;
    private String STBType;
    private String STBVersion;
    private String EPGDomain;
    private String STBID;
    private String ChannelVer;
    private String SessionID;
    String UserValid = null;// 用户校验标志
    private int NextCallInterval = 900;//上报时间间隔
    //地址
    String httpUrl;
    //请求的参数
    HashMap<String, String> lstParameters = new HashMap<String, String>();
    //心跳返回的数据
    HashMap<String, Object> heartbit;
    private Handler handler =new Handler();
    public EPGHeartBeat() {
        // TODO Auto-generated constructor stub
    }
    public Runnable runnable = new Runnable() {

        @Override
        public void run() {

            try {
                obtainData();
                ALOG.debug("MainActivity.djf--"+NextCallInterval);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("exception...");
            }
        }
    };
    public void obtainData(){
        heartbeatcount++;
        ALOG.debug(TAG,"Hreat beat start--"+heartbeatcount);
        if (!IptvApp.authManager.isAuth){
            ALOG.debug(TAG,"IPTV Auth is false，cannot send HeartBeatPackage");
            return;
        }

        //add by zw 20170922 华为平台才有心跳
        platform = AmtDataManager.getString(IPTVData.Config_PlatForm,"0");

        if ("0".equalsIgnoreCase(platform)){
            //获取数据
            UserStatus = Iptv2EPG.getIptv2EPG().authInfo.CTCGetConfig("UserStatus");
            EPGDomain = Iptv2EPG.getIptv2EPG().authInfo.CTCGetConfig("EPGDomain");
            ChannelVer=Iptv2EPG.getIptv2EPG().authInfo.CTCGetConfig("ChannelVer");
            SessionID =Iptv2EPG.getIptv2EPG().authInfo.CTCGetConfig("SessionID");
            STBID= DeviceInfo.STBID;
            STBType = DeviceInfo.MODEL;
            STBVersion = DeviceInfo.HardwareVersion;
            if (!TextUtils.isEmpty(EPGDomain)) {
                try {
                    URL url = new URL(EPGDomain);
                    EPGDomain = (url.getAuthority());
                } catch (Exception e) {
                }
            }
            //当EPGDomain为空停止心跳
            ALOG.debug(TAG,"EPGDomain-->"+EPGDomain);
            if (TextUtils.isEmpty(EPGDomain) || "null".equalsIgnoreCase(EPGDomain)) {
                ALOG.debug(TAG,"EPGDomain is empty");
                return;
            }
            httpUrl = String.format("http://%s/EPG/jsp/GetHeartBit?UserStatus=%s&ChannelVer=%s&STBID=%s&STBType=%s&Version=%s",
                    EPGDomain, UserStatus, ChannelVer, STBID,
                    java.net.URLEncoder.encode(STBType),
                    java.net.URLEncoder.encode(STBVersion));
            //发起心跳请求
            Headers headers =new Headers.Builder().add("Cookie","JSESSIONID=" +SessionID).build();
            HttpUtils.get(httpUrl, headers,netCallback);

        }
    }

    /**
     * 创建心跳回调
     */
    private NetCallback netCallback =new NetCallback() {
        @Override
        public void onSuccess(String result) {
            ALOG.debug(TAG,"onSuccess--"+result);
            if (TextUtils.isEmpty(result)){
                ALOG.debug(TAG,"result is null");
                return;
            }
             heartbit = getHeartbit(result);

            //用户是或否合法
            if (heartbit.containsKey("UserValid")) {
                UserValid = heartbit.get("UserValid").toString();
            }
            //获取上报时间间隔
            if (heartbit.containsKey("NextCallInterval")) {
                try {
                    NextCallInterval = Integer.parseInt(heartbit.get("NextCallInterval").toString());
                    ALOG.debug(TAG,"NextCallInterval  -->"+NextCallInterval);
                } catch (Exception e) {
                    ALOG.debug(TAG,"error-->NextCallInterval");
                    NextCallInterval = 900;
                }
            }

            if (UserValid == null || NextCallInterval == -1) {
                String[] strLines = result.split("\r");
                for (int i = 0; i < strLines.length; i++) {
                    String[] str = strLines[i].trim().split("=");
                    if (str.length > 1) {
                        if ("UserValid".equals(str[0])) {
                            UserValid = str[1];
                        } else if ("NextCallInterval".equals(str[0])) {
                            NextCallInterval = Integer.parseInt(str[1]);
                        }
                    }
                }
            }
            if ("false".equalsIgnoreCase(UserValid)) {
                ALOG.debug(TAG,"EPGHeartbeat-->UserValid==false");
                //showDialog djf
            }
            //重新设置下一个心跳时间
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable,NextCallInterval * 1000);
            ALOG.debug(TAG,"EPGHearBeat onSuccess ");
        }
        @Override
        public void onFail(String error) {
            ALOG.debug(TAG,"EPGHearBeat onFail");
            ALOG.debug(TAG,"SessionID is Timeout");
        }
    };


    /**
     * 获取Hearbit参数集合
     *
     * @param result
     * @return
     */
    private HashMap<String, Object> getHeartbit(String result) {
        HashMap<String, Object> heartbit = new HashMap<String, Object>();
        String[] requestText = result.split("\\[\\w+\\]");
        if (requestText != null && requestText.length > 0)
            for (String text : requestText) {
                if (text.toLowerCase().indexOf("uservalid") >= 0) {
                    String[] heartbitParams = text.split("\\s");

                    for (String param : heartbitParams) {
                        String[] paramValues = param.split("=");
                        if (paramValues.length == 2) {
                            heartbit.put(paramValues[0].trim(), paramValues[1].trim());
                        }
                    }
                }
            }
        ALOG.debug(TAG,"getHeartbit-->"+heartbit.toString());
        return heartbit;
    }


    /**
     * 停止心跳循环线程
     */
    public void stopHeartBeat(){
        ALOG.debug(TAG,"stopHeartBeat");
        handler.removeCallbacks(runnable);
    }

}
