package com.amt.net;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.amt.utils.ALOG;
import com.amt.utils.Utils;

import java.util.Set;

/**
 * 接收系统发送的网络广播
 * Created by DonWZ on 2016-9-7
 */
public class NetWorkReceiver extends BroadcastReceiver {

	public static final String ETHERNET_STATE_CHANGED_ACTION = "android.net.ethernet.ETHERNET_STATE_CHANGE";
	public static final String PPPOE_STATE_CHANGED_ACTION ="PPPOE_STATE_CHANGED";
	public static final String EXTRA_ETHERNET_STATE= "ethernet_state";
	public static final String EXTRA_PPPOE_STATE= "pppoe_state";
	public static final String EXTRA_PPPOE_ERRMSG = "pppoe_errmsg";	
	
	private NetWorkListener mNetListener = null;

	private NetConnectManager mNetConnectManager;
	
	/**
	 * 注册网络监听器
	 * @param netListener
	 */
	public void setNetWorkListener(NetWorkListener netListener){
		mNetListener = netListener;
	}

	public void setNetConnectManager(NetConnectManager netManager){
		mNetConnectManager = netManager;
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		String action=intent.getAction();
		ALOG.info("onReceive > action : "+action);
		ALOG.info("onReceive > extra :"+ Utils.viewBundle(intent.getExtras()));
		if("android.net.conn.CONNECTIVITY_CHANGE".equals(action)){
			handleConnectivityChange(context, intent);
		}
		else if(ETHERNET_STATE_CHANGED_ACTION.equals(action)
				|| PPPOE_STATE_CHANGED_ACTION.equals(action)){
			EthernetAndPppoeHelper.handleNetInfo(intent,mNetListener);
		}
	}
	/**
	 * 处理网络连接状态变化广播。用于监听网络变化。基础逻辑为只检测eth0和ppp0两个有线网络的状态，wifi的不处理。
	 * IPTV需要检测物理网线的拔插状态，此广播不能满足这个需求
	 * <br>后面要做成分区域单独处理，比如四川要求有线无线共存，并且无线的状态也纳入IPTV检测范围；其他区域也有很多特殊要求。
	 * @param intent
	 */
	private void handleConnectivityChange(Context context,Intent intent){
		Bundle bundle = intent.getExtras();
		ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		int netType = bundle.getInt(ConnectivityManager.EXTRA_NETWORK_TYPE);
		if(netType == ConnectivityManager.TYPE_WIFI){
			handleWifiConnectivity(cm);
		}else if(mNetListener!=null) {
			//网线未插上时，libamtUtils.so会通知过来，所以这里接收到广播时就不必再通知了，这里只做网络相关的通知。
			if (mNetConnectManager.isEthLinkUp()) {
				NetworkInfo actNetWork = cm.getNetworkInfo(netType);
				ALOG.info( "handleConnectivityChange > actNetWork is null : " + (actNetWork == null));
				//还需要检测网线是否已插上。因为网线状态变化通知可能比此广播更快。
				//如果当前没网络，并且已经检测到网线拔掉了，就不再回调网络异常了，网线监听会通知。
				if ((actNetWork == null || !actNetWork.isConnected()) && !mNetConnectManager.isEthLinkUp()) {
					ALOG.info( "handleConnectivityChange > isPhyLinkUp is up : " + mNetConnectManager.isEthLinkUp());
					mNetListener.onNetDisConnect(NetConnectManager.NETTYPE_LINK,null);
				} else if (actNetWork != null && actNetWork.isConnected()) {
					ALOG.info( "handleConnectivityChange > isConnected : true");
					mNetListener.onNetConnected(NetConnectManager.NETTYPE_LINK,null);
				}
			}
		}
	}
	/**
	 * 处理wifi网络变化的广播
	 * @param cm
	 */
	private void handleWifiConnectivity(ConnectivityManager cm) {
		NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetWorkExtra wifiExtra = new NetWorkExtra();
		wifiExtra.netType = NetConnectManager.NETTYPE_WIFI;
		if (wifiInfo == null) {
			wifiExtra.status = NetWorkExtra.STATUS_UNKNOWN;
		} else if (wifiInfo.isConnected()) {
			// WIFI连接上
			wifiExtra.status = NetWorkExtra.STATUS_CONNECTED;
		} else {
			wifiExtra.status = NetWorkExtra.STATUS_DISCONNECTED;
		}
		if (mNetListener != null) {
			mNetListener.onNetInfoExtra(wifiExtra);
		}
	}
}
