package com.SyMedia.SyDebug;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.amt.utils.ALOG;

/**
 * Created by DonWZ on 2017/6/21.
 */
public class RemoteBroadCastReceiver {

    private static String TAG= "RemoteBroadCastReceiver";
    private static RemoteReceiver receiver;

    public static void initRemoteReceiver(Context context){
        if(context!=null){
            receiver = new RemoteReceiver();
            IntentFilter filter = new IntentFilter("androidmov.settings.remote.maintain");
            context.registerReceiver(receiver,filter);
        }
    }

    public static void releaseRemoteReceiver(Context context){
        if(context != null && receiver != null){
            context.unregisterReceiver(receiver);
            receiver = null;
        }
    }

    static class RemoteReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            ALOG.info(TAG,"onReceive > "+intent.getAction());
            ALOG.info(TAG,"onReceive > extra : "+intent.getExtras());
            if("androidmov.settings.remote.maintain".equals(intent.getAction())){
                boolean open_remote = intent.getBooleanExtra("open_remote", false);
                String ip_remote = intent.getStringExtra("ip_remote");// 反向链接的IP
                ALOG.debug("open_remote:" + open_remote + ",ip_remote:" + ip_remote);
                if (TextUtils.isEmpty(ip_remote)){
                    ip_remote = null;
                }
                if (open_remote) {
                    RemoteDebug.load(false, ip_remote);
                } else{
                    RemoteDebug.Unload();
                }
            }
        }
    }

}
