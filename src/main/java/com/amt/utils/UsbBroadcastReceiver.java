package com.amt.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.amt.webview.WebViewManager;

import java.util.Set;

/**
 * Created by DJF on 2017/6/6.
 */
public class UsbBroadcastReceiver extends BroadcastReceiver{
    /**
     * Broadcast Action:  A sticky broadcast for USB state change events when in device mode.
     *
     */
    public static final String ACTION_USB_STATE =
            "android.hardware.usb.action.USB_STATE";

    /**
     * Broadcast Action:  A broadcast for USB device attached event.
     *
     */
    public static final String ACTION_USB_DEVICE_ATTACHED =
            "android.hardware.usb.action.USB_DEVICE_ATTACHED";

    /**
     * Broadcast Action:  A broadcast for USB device detached event.
     *
     */
    public static final String ACTION_USB_DEVICE_DETACHED =
            "android.hardware.usb.action.USB_DEVICE_DETACHED";

    private String TAG="UsbBroadcast";
    @Override
    public void onReceive(Context context, Intent intent) {

        // TODO Auto-generated method stub
        String action=intent.getAction();
        ALOG.debug(TAG,"action-->"+action);
        ALOG.debug(TAG,"onReceive > extra :"+Utils.viewBundle(intent.getExtras()));

        //接受到Usb ATTACHED信息 更新usb路径
        if (ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            ALOG.debug(TAG,"USB ATTACHED");
            //更新Usb目录
            USBHelper.updateUsbPath();
            WebViewManager.getManager().updateEPGPath();
        }

        //接收到Usb DETACHED信息  情况路径
        if (ACTION_USB_DEVICE_DETACHED.equals(action)) {
            ALOG.debug(TAG,"USB DETACHED");
            //更新Usb目录
            USBHelper.usbPath = USBHelper.getUsbDirectory();
            WebViewManager.getManager().updateEPGPath();
        }
    }
}
