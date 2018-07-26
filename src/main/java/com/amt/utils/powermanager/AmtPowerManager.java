package com.amt.utils.powermanager;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.amt.app.IPTVActivity;
import com.amt.utils.ALOG;
import com.amt.utils.ChipType;
import com.amt.utils.DeviceInfo;

import java.lang.reflect.Field;

/**
 * Created by DJF on 2017/6/14.
 *
 * IPTV电源管理类
 *
 */

public class AmtPowerManager {
    private static PowerManager.WakeLock wakeLock;
    private static String TAG = "AmtPowerManager";

    /**
     * 电源解锁，允许待机/关机
     */
    public static void WakeUnlock() {
        try {
            ALOG.debug(TAG, "wakeLock release " + wakeLock);
            if (wakeLock != null) {
                try {
                    wakeLock.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                wakeLock = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 电源加锁，不允许待机
     */
    private static String hisi_PowerManagerPath = "android.os.PowerManager";
    private static String hisi_WAKELOCK = "SUSPEND_WAKE_LOCK";

    public static void WakeLock() {
        //Wake lock level
        int power_wakeLock = 1;
        try{
            if (wakeLock == null) {
                PowerManager powerManager = (PowerManager) IPTVActivity.context.getSystemService(Context.POWER_SERVICE);
                if (DeviceInfo.chipType == ChipType.HISI_3798) {
                    power_wakeLock = reflect(hisi_PowerManagerPath,hisi_WAKELOCK);
                } else {
                    power_wakeLock = PowerManager.PARTIAL_WAKE_LOCK;
                }
                wakeLock = powerManager.newWakeLock(power_wakeLock,IPTVActivity.context.getClass().getCanonicalName());

                wakeLock.acquire();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 系统重启
     */
    public static void reboot() {
        ALOG.debug(TAG,"reboot");
        try {
            PowerManager pm = (PowerManager) IPTVActivity.context.getSystemService(Context.POWER_SERVICE);
            pm.reboot("normal_reboot");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    /**
     * 通过反射拿去Int类型常量
     *
     * @param classname
     * @param paramsname
     * @return
     */
    public static int reflect(String classname, String paramsname) {
        ALOG.debug(TAG, "reflect");
        int result;
        try {
            Class clazz = Class.forName(classname);
            Field field = clazz.getField(paramsname);
            result = field.getInt(clazz);
            ALOG.debug("-->" + field.getName() + " &&-->  " + result);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result=-999;
            ALOG.debug("error -->" + e);
        }
        return result;

    }

    /**
     * 恢复出厂
     */
    public static void restoreFatory(){
        //发送恢复出厂广播
        IPTVActivity.context.sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));

    }




}
