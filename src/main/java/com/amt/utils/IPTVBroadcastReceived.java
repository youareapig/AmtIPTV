package com.amt.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.amt.player.iptvplayer.IPTVPlayerBase;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by DJF on 2017/11/22.
 * 添加通用静态广播注册类
 */
public class IPTVBroadcastReceived extends BroadcastReceiver{

    private static String TAG="IPTVBroadcastReceived";
    @Override
    public void onReceive(Context context, Intent intent) {
        String action =intent.getAction();
        ALOG.info(TAG,"IPTVBroadcastReceived onReceive > action : "+action);
        //开机广播
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)){
            bootCompleted();
        }
    }

    /**
     * 处理开机广播
     */
    private void bootCompleted() {
        ALOG.info(TAG,"bootCompleted");

        Timer timer =new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                //收到开机五分钟后 给底层设置验证
                IPTVPlayerBase.setValue("Verification", "");

            }
        };
        timer.schedule(task,5 * 60 * 1000);
    }

}
