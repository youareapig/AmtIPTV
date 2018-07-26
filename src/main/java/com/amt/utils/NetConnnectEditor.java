package com.amt.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.amt.amtdata.AmtDataManager;
import com.amt.amtdata.IPTVData;
import com.amt.amtdata.ServiceCfg;
import com.amt.app.IptvApp;
import com.amt.net.NetConnectManager;

import java.util.Timer;
import java.util.TimerTask;


/**
 * 网络操作工具类。用于网管和远程运维工具以修改网络方式和网络账号。
 * 有重启生效和立即生效两种方式，重启生效是发送广播给系统，需要系统对接，立即生效是发送广播给设置，需要设置对接。
 * @author zw
 * 2017-07-25
 */
public class NetConnnectEditor {

	public static final String TAG = "NetConnnectEditor";
	public static final String ACTION_ITMS_SET_NETMODE = "IPTV.ITMS.SetToSystem.ConnectType";
	public static final String ACTION_ITMS_SET_NETACCOUNT = "IPTV.ITMS.SetToSystem.NetAccount";
	/**发给设置处理的广播*/
	public static final String ACTION_SET_NETMODE_SETTINGS = "android.intent.action.IPTV.DATA";
	private static NetConnnectEditor instance ;
	
	private Context mContext;
	/**针对网络相关的修改，是否是立即生效*/
	private boolean immediately = false;

	/**
	 * 设置针对网络相关的修改是否是立即生效。如果是立即生效，将发送广播给设置处理，如果是重启生效，将发送广播给系统处理。
	 * @param immediately
     */
	public void setImmediately(boolean immediately){
		this.immediately = immediately;
	}

	public synchronized static NetConnnectEditor init(Context context){
		if (instance == null) {
			instance = new NetConnnectEditor(context);
		}
		return instance;
	}
	private NetConnnectEditor(Context context){
		this.mContext = context;
	}
	
	private String connectType;
	private String dhcpEnable;
	private String mIpAddress;
	private String mMask;
	private String mGateWay;
	private String mDNS;
	private String mDNS2;

	private Timer timer;

	public void setConnectType(String connectType) {
		this.connectType = connectType;
		if(immediately){
			sendBroadCastDelay(1000);
		}else{
			if("dhcp".equalsIgnoreCase(connectType)
					||"dhcp+".equalsIgnoreCase(connectType)
					|| "ipoe".equalsIgnoreCase(connectType)
					||"pppoe".equalsIgnoreCase(connectType)){
				sendBroadcast(mContext);
			}else if(isStaticDataCompletion()){
				sendBroadcast(mContext);
			}
		}
	}

	/**
	 * 延时发送广播。运维工具过来的数据会立即生效，由于运维工具下发参数比较奇葩，
	 * 不一定会下发所有需要的数据，所以延时2秒等待运维工具下发完数据后，从IPTV的xml里拿需要的数据给设置。
	 * @param delayMs
     */
	private void sendBroadCastDelay(long delayMs){
		ALOG.info("setConnectType > timer is null : "+(timer == null));
		if(timer == null){
			timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					sendBroadcast(mContext);
					timer = null;
				}
			},delayMs);
		}
	}

	public void setDHCPEnable(String dhcpEnable) {
		this.dhcpEnable = dhcpEnable;
	}

	public void setmIpAddress(String mIpAddress) {
		this.mIpAddress = mIpAddress;
		if(immediately){
			setConnectType(NetConnectManager.MANUAL);
		}else if(isStaticDataCompletion()){
			sendBroadcast(mContext);
		}
		AmtDataManager.putString(ServiceCfg.Config_Static_IpAddress, mIpAddress, "");
	}
	public void setmMask(String mMask) {
		this.mMask = mMask;
		if(immediately){
			setConnectType(NetConnectManager.MANUAL);
		}else if(isStaticDataCompletion()){
			sendBroadcast(mContext);
		}
		AmtDataManager.putString(ServiceCfg.Config_Static_Mask, mMask, "");
	}
	public void setmGateWay(String mGateWay) {
		this.mGateWay = mGateWay;
		if(immediately){
			setConnectType(NetConnectManager.MANUAL);
		}else if(isStaticDataCompletion()){
			sendBroadcast(mContext);
		}
		AmtDataManager.putString(ServiceCfg.Config_Static_Gateway, mGateWay, "");
	}
	public void setmDNS(String mDNS) {
		this.mDNS = mDNS;
		if(immediately){
			setConnectType(NetConnectManager.MANUAL);
		}else if(isStaticDataCompletion()){
			sendBroadcast(mContext);
		}
		AmtDataManager.putString(ServiceCfg.Config_Static_DNS, mDNS, "");
	}
	public void setmDNS2(String dns) {
		this.mDNS2 = dns;
		if(immediately){
			setConnectType(NetConnectManager.MANUAL);
		}else if(isStaticDataCompletion()){
			sendBroadcast(mContext);
		}
		AmtDataManager.putString(ServiceCfg.Config_Static_DNSBackup, mDNS2, "");
	}

	private void sendBroadcast(Context context) {
		if(immediately){
			//直接发广播给设置。
			Intent intentToSetting = new Intent(ACTION_SET_NETMODE_SETTINGS);
			intentToSetting.putExtra("Type", 1000);// 1000:网络相关
			int connectMode = 1;
			if("dhcp".equalsIgnoreCase(connectType)){
				connectMode = 1;
			}else if("dhcp+".equalsIgnoreCase(connectType)||"ipoe".equalsIgnoreCase(connectType)){
				connectMode = 3;
				intentToSetting.putExtra("NetUserID",AmtDataManager.getString(IPTVData.Config_DHCPUserName,DHCPUserName));
				intentToSetting.putExtra("NetPwd",AmtDataManager.getString(IPTVData.Config_DHCPPassword,DHCPPassword));
			}else if("static".equalsIgnoreCase(connectType)||"manual".equalsIgnoreCase(connectType)){
				connectMode = 2;
				String ip = AmtDataManager.getString(IPTVData.Config_Static_IpAddress,mIpAddress);
				ip = TextUtils.isEmpty(ip) ? IptvApp.mNetManager.getIp() : ip;
				String mask = AmtDataManager.getString(IPTVData.Config_Static_Mask, mMask);
				mask = TextUtils.isEmpty(mask) ? IptvApp.mNetManager.getMask() : mask;
				String gateWay = AmtDataManager.getString(IPTVData.Config_Static_Gateway, mGateWay);
				gateWay = TextUtils.isEmpty(gateWay) ? IptvApp.mNetManager.getGateWay() : gateWay;
				String dns = AmtDataManager.getString(IPTVData.Config_Static_DNS, mDNS);
				dns = TextUtils.isEmpty(dns) ? IptvApp.mNetManager.getDns1() : dns;
				String dns2 = AmtDataManager.getString(IPTVData.Config_Static_DNSBackup, mDNS2);
				dns2 = TextUtils.isEmpty(dns2) ? IptvApp.mNetManager.getDns2() : dns2;
				intentToSetting.putExtra("IP",ip);
				intentToSetting.putExtra("Mask",mask);
				intentToSetting.putExtra("Gateway",gateWay);
				intentToSetting.putExtra("DNS",dns);
				intentToSetting.putExtra("DNS2",dns2);
			}else if("pppoe".equalsIgnoreCase(connectType)){
				connectMode = 0;
				intentToSetting.putExtra("NetUserID",AmtDataManager.getString(IPTVData.Config_PPPOEUserName,PPPOEUserName));
				intentToSetting.putExtra("NetPwd",AmtDataManager.getString(IPTVData.Config_PPPOEPassword,PPPOEPassword));
			}
			intentToSetting.putExtra("Mode", connectMode);
			ALOG.info(TAG,"send Broadcast to Settings > Action : "+intentToSetting.getAction());
			ALOG.info(TAG,"send Broadcast to Settings > Extra : "+Utils.viewBundle(intentToSetting.getExtras()));
			context.sendBroadcast(intentToSetting);
			immediately = false;
		}else{
			//直接发广播给系统。
			Intent intentToSystem = new Intent(ACTION_ITMS_SET_NETMODE);
			connectType = "static".equalsIgnoreCase(connectType) ? "manual" : connectType;
			connectType = "ipoe".equalsIgnoreCase(connectType) ? "dhcp+" : connectType;
			intentToSystem.putExtra("ConnectType", connectType);
			intentToSystem.putExtra("DHCPEnable", dhcpEnable);
			if("manual".equals(connectType)){//静态IP
				intentToSystem.putExtra("IpAddress", mIpAddress);
				intentToSystem.putExtra("Mask", mMask);
				intentToSystem.putExtra("GateWay", mGateWay);
				intentToSystem.putExtra("DNS", mDNS);
				ALOG.info(TAG, "IpAddress : " + mIpAddress + ", Mask : " + mMask + ", GateWay : " + mGateWay + ", DNS : " + mDNS);
			}
			ALOG.info(TAG,"send Broadcast to System > Action : "+intentToSystem.getAction());
			ALOG.info(TAG,"send Broadcast to System > Extra : "+Utils.viewBundle(intentToSystem.getExtras()));
			context.sendBroadcast(intentToSystem);
		}
		mIpAddress = "";
		mMask = "";
		mGateWay = "";
		mDNS = "";
		mDNS2 = "";
	}
	
	private boolean isStaticDataCompletion() {
		if(TextUtils.isEmpty(connectType)
				||TextUtils.isEmpty(mDNS)
				||TextUtils.isEmpty(mGateWay)
				||TextUtils.isEmpty(mMask)
				||TextUtils.isEmpty(mIpAddress)){
			return false;
		}else{
			return true;
		}
	}

	private String DHCPUserName;
	private String DHCPPassword;
	private String PPPOEUserName;
	private String PPPOEPassword;

	public void setDHCPUserName(String dhcpUserName) {
		DHCPUserName = dhcpUserName;
		if (!TextUtils.isEmpty(DHCPPassword)) {
			if (immediately) {
				setConnectType(NetConnectManager.DHCPPlUS);
			} else {
				sendNetAccountInfoBroadcast();
			}
		}
	}

	public void setDHCPPassword(String dhcpPassword) {
		DHCPPassword = dhcpPassword;
		if (!TextUtils.isEmpty(DHCPUserName)) {
			if (immediately) {
				setConnectType(NetConnectManager.DHCPPlUS);
			} else {
				sendNetAccountInfoBroadcast();
			}
		}
	}

	public void setPPPOEUserName(String pppoeUserName) {
		PPPOEUserName = pppoeUserName;
		if (!TextUtils.isEmpty(PPPOEPassword)) {
			if (immediately) {
				setConnectType(NetConnectManager.PPPOE);
			} else {
				sendNetAccountInfoBroadcast();
			}
		}
	}

	public void setPPPOEPassword(String pppoePassword) {
		PPPOEPassword = pppoePassword;
		if (!TextUtils.isEmpty(PPPOEUserName)) {
			if (immediately) {
				setConnectType(NetConnectManager.PPPOE);
			} else {
				sendNetAccountInfoBroadcast();
			}
		}
	}

	/**
	 * 发送广播给系统，系统自己存储网络账号，下次重启后生效
	 */
	private void sendNetAccountInfoBroadcast() {
		Intent intent = new Intent(ACTION_ITMS_SET_NETACCOUNT);
		if(!TextUtils.isEmpty(DHCPUserName)){
			intent.putExtra("DHCPUserName", DHCPUserName);
		}
		if(!TextUtils.isEmpty(DHCPPassword)){
			intent.putExtra("DHCPPassword", DHCPPassword);
		}
		if(!TextUtils.isEmpty(PPPOEUserName)){
			intent.putExtra("PPPOEUserName", PPPOEUserName);
		}
		if(!TextUtils.isEmpty(PPPOEPassword)){
			intent.putExtra("PPPOEPassword", PPPOEPassword);
		}
		ALOG.info(TAG,"send Broadcast to System > Action : "+intent.getAction());
		ALOG.info(TAG,"send Broadcast to System > Extra : "+Utils.viewBundle(intent.getExtras()));
		mContext.sendBroadcast(intent);
		DHCPUserName = "";
		DHCPPassword = "";
		PPPOEUserName = "";
		PPPOEPassword = "";
	}
}
