package com.amt.net;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.text.TextUtils;

import com.amt.utils.ALOG;
import com.amt.utils.RunTimeUtils;
import com.amt.utils.mainthread.MainThreadSwitcher;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 用于监听网络状态、获取网络状态的管理器。外部若想获取网络状态、传递监听对象，都使用此类。
 * <br>需要设置监听addNetWorkListener
 * Created by DonWZ on 2016-9-5
 */
public class NetConnectManager {

	public static final String DHCP = "dhcp";
	/**某些项目中，这种方式名字为ipoe*/
	public static final String DHCPPlUS = "dhcp+";
	public static final String PPPOE = "pppoe";
	/**静态IP方式(static)*/
	public static final String MANUAL = "manual";
	/**未知类型。在wifi情况下可以用此方式*/
	public static final String UNKNOWN = "unknown";
	/**标识网络连接方式的系统数据库字段名*/
	public static final String CONNECTTYPE = "nowConnect";
	/**wifi网络*/
	public static final int NETTYPE_WIFI = 1;
	/**有线网络*/
	public static final int NETTYPE_LINK = 2;

	private static NetWorkListener mNetWorkListener = null;
	private static Context mContext;
	private static final String STATUS_UP = "up";
	private static final String STATUS_DOWN = "down";
	/**当前网络是否连接上。私有变量，作为当前是否有网络的依据。*/
	private String mNetWorkStatus = "";
	/**网线是否插上。私有变量。*/
	private String mNetLinkStatus = "";

	private static ConnectivityManager mConnectivityManager = null;
	
	private ArrayList<NetWorkListener> mListenerList = new ArrayList<NetWorkListener>();
	
	private static NetConnectManager mInstanse = new NetConnectManager();
	//网络状态是否包含WIFI的状态。可外部配置包含wifi的状态
	private boolean isCheckWifi =true;

	private NetNative mNetNative;
	//还是动态注册广播接收者，到了Android 7.0 ，静态注册的网络广播接收器已经接收不到了，需要动态注册。
	private NetWorkReceiver mNetReceiver;
	/**循环检测IP的次数*/
	private int checkIpCount = 0;
	/**循环检测IP阶段是否结束。如果在循环检测阶段，不处理系统广播。此为适配融合终端的方案*/
	private boolean isLoopOver =true;
	/**循环检测IP的timer*/
	private Timer checkIptimer;


	private NetConnectManager(){
		mNetWorkListener = new NetConnectListener();
		mNetNative = NetNative.init();
		mNetNative.setNetListener(mNetWorkListener);
		mNetReceiver = new NetWorkReceiver();
		mNetReceiver.setNetWorkListener(mNetWorkListener);
		mNetReceiver.setNetConnectManager(this);
	}

	/**
	 * NetConnectManager设计成单例模式，用于直接和NetWorkReciever交互，保证和NetWorkReciever的1对1关系
	 * @param context
	 * @return NetConnectManager
	 */
	public synchronized static NetConnectManager getManager(Context context){
		ALOG.debug("NetConnectManager getManager");
		mContext = context;
		if(mConnectivityManager == null && mContext !=null){
			mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		}
		return mInstanse;
	}

	/**
	 * 开始循环检测网络，若检测网络连通（有IP时），回调网络事件；循环期间不处理系统广播和内核驱动的通知。add djf 20170428
	 * @param loopTime 循环检测的时间，单位为毫秒
	 */
	public void startCheckLoop(final long loopTime) {
		ALOG.info("startCheckLoop");
		isLoopOver =false;
		checkIptimer = new Timer();
		checkIptimer.schedule(new TimerTask() {
			@Override
			public void run() {
				// 执行内容
				checkIpCount++;
				if (isValidIpAddress(getIp())) {
					ALOG.info("Netloop IPTV get IP-->" + getIp());
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							isLoopOver = true;
							mNetWorkStatus = "";
							mNetWorkListener.onNetConnected(NETTYPE_LINK, null);
						}
					}, 3 * 1000);
					checkIptimer.cancel();
					checkIptimer = null;
					checkIpCount = 0;
				}
				if (checkIpCount > loopTime / 1000) {
					ALOG.info("Netloop IPTV get IP is null!!!");
					isLoopOver = true;

					if (!isEthLinkUp()) {
						ALOG.debug("djf-->onPhyLinkDown");
						//重置网线物理状态  djf 20180117
						mNetLinkStatus = "";
						mNetWorkListener.onPhyLinkDown();
					}else {
						ALOG.debug("djf-->onNetDisConnect");
						mNetWorkStatus = "";
						mNetWorkListener.onNetDisConnect(NETTYPE_LINK, null);
					}
					mNetWorkListener.onNetDisConnect(NETTYPE_LINK, null);
					checkIptimer.cancel();
					checkIptimer = null;
					checkIpCount = 0;
				}
			}
		}, 0, 1000);
	}

	/**
	 * 验证是否是有效IP地址。 0.0.0.0 和 127.0.0.1不是合法IP。
	 * @param ipAddress
	 * @return
	 */
	protected static boolean isValidIpAddress(String ipAddress){
		ALOG.info("isValidIpAddress-->"+ipAddress);
		if(TextUtils.isEmpty(ipAddress)){
			return false;
		}
		String num = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";
		String regex = "^" + num + "\\." + num + "\\." + num + "\\." + num + "$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(ipAddress);
		boolean isMatch = matcher.matches();
		return isMatch && !ipAddress.startsWith("0.") && !"127.0.0.1".equals(ipAddress);
	}

	/**
	 * 注册网络广播监听器。需在主Activity onCreate里注册。
	 */
	public void registReceiver(){
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
		filter.addAction("android.net.ethernet.ETHERNET_STATE_CHANGE");
		filter.addAction("PPPOE_STATE_CHANGED");
		if(mContext!=null){
			mContext.registerReceiver(mNetReceiver, filter);
		}
	}

	/**
	 * 取消注册网络广播监听器。需在主Activity onDestroy里取消注册。
	 */
	public void unRegistReceiver(){
		if(mContext!=null){
			mContext.unregisterReceiver(mNetReceiver);
		}
	}

	/**
	 * 设置网络监听器。存放在list集合中
	 * @param netWorkListener
	 */
	public void addNetWorkListener(NetWorkListener netWorkListener){
		if(netWorkListener!=null){
			mListenerList.add(netWorkListener);
		}
	}
	
	/**
	 * 检测是否有可用网络(只检查eth0端口和wifi(isCheckWifi == true)的状态)
	 * @return
	 */
    public boolean isNetworkConnected() {
    	boolean isNetworkConnected = false;
		//我们的libamtUtils.so向IptvService注册了监听，间接监听的网卡的消息，所以网络事件回调比系统framework层快，有时候从framework拿网络状态就会是错误的。
		//所以优先以我们内部记录的状态为准。
		if(STATUS_UP.equals(mNetWorkStatus)||STATUS_DOWN.equals(mNetWorkStatus)){
			isNetworkConnected = STATUS_UP.equals(mNetWorkStatus);
		}else{
			boolean isEthernetConnected = isNetworkConnected(NETTYPE_LINK);
			if(isEthLinkUp() && isCheckWifi()){
				isNetworkConnected = isEthernetConnected || isWifiConnected();
			}else if(isEthLinkUp() && !isCheckWifi()){
				isNetworkConnected = isEthernetConnected;
			}else if(!isEthLinkUp() && isCheckWifi()){
				isNetworkConnected = isWifiConnected();
			}else if(!isEthLinkUp() && !isCheckWifi()){
				isNetworkConnected = false;
			}
			mNetWorkStatus = isNetworkConnected ? STATUS_UP : STATUS_DOWN;
		}
        ALOG.info("isNetworkConnected > "+isNetworkConnected+", isCheckWifi : "+isCheckWifi);
        return isNetworkConnected;
    }

    /**
     * 获取指定的网络类型是否有网络连接.
     * @param netType 网络类型。如wifi或有线等{@link NetConnectManager#NETTYPE_LINK}、
     * {@link NetConnectManager#NETTYPE_WIFI}
     * @return
     */
    public boolean isNetworkConnected(int netType){
		boolean isNetworkConnected = false;
		switch(netType){
			case NETTYPE_LINK:
				// 当网线被拔掉时，马上从ConnectivityManager拿到的状态还未及时更新，所以需要再判断下有线是否插上的
				if(!isEthLinkUp()){
					isNetworkConnected = false;
				}else{
					NetworkInfo netInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
					ALOG.info("info","get Ethernet networkInfo ,is Ethernet networkinfo null ? > "+(netInfo==null?"true":"false"));
					if (netInfo != null) {
						isNetworkConnected = netInfo.isConnected();
					}
				}
				break;
			case NETTYPE_WIFI:
				NetworkInfo netInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
				ALOG.info("info","get WIFI networkInfo ,is wifi networkinfo null ? > "+(netInfo==null?"true":"false"));
				if (netInfo != null) {
					isNetworkConnected = netInfo.isConnected();
				}
				break;
		}
		ALOG.info( "isNetworkConnected > " + isNetworkConnected + ", netType : "+netType+", isCheckWifi : " + isCheckWifi);
		return isNetworkConnected;
    }

	/**
	 * 获取当前的网络连接类型。
	 * @return 返回 {@link NetConnectManager#DHCP}、{@link NetConnectManager#DHCPPlUS}、
	 * {@link NetConnectManager#MANUAL}、{@link NetConnectManager#PPPOE}之一
     */
	public String getConnectType(){
		String connectType = Settings.System.getString(mContext.getContentResolver(),CONNECTTYPE);
		return connectType;
	}

	/**
	 * 获取IP
	 * @return 以点号隔开。如192.168.1.1
	 */
	public String getIp(){
//		String ifname = "eth0";
//		if(PPPOE.equals(getConnectType())){
//			ifname = "ppp0";
//		}
//		String ip = getIp(ifname);
//		//如果 有线网络的IP为空，且需要检查wifi情况，则获取 wifi的IP。
//		//获取wifi的IP有两种方式，一种是通过NetNative直接从wlan0端口拿，另一种是通过WifiManager获取。
//		if(TextUtils.isEmpty(ip) && isCheckWifi()){
//			ip = getIp("wlan0");
//		}
//		return ip;
		// 20170517 modify by wenzong 更新获取方案，优先从eth0拿IP，如果拿不到IP，再去ppp0拿。
		String[] ipAddress = { getIp("eth0"), "eth0" };
		if (!isValidIpAddress(ipAddress[0])) {
			ipAddress[0] = getIp("ppp0");
			ipAddress[1] = "ppp0";
		}
		if (!isValidIpAddress(ipAddress[0]) && isCheckWifi()) {
			ipAddress[0] = getIp("wlan0");
			ipAddress[1] = "wlan0";
		}
		ALOG.debug("NetConnectManager > getIp > " + ipAddress[0] + ", ifname : " + ipAddress[1]);
		return ipAddress[0];
	}

	/**
	 * 获取IP
	 * @param ifname 网卡名称
	 * @return 以点号隔开。如192.168.1.1
     */
	public String getIp(String ifname){
		return mNetNative.getIpAddress(ifname);
	}

	/**
	 * 获取MAC
	 * @return 以冒号分隔开。如00:3b:5c:3b:2e:3f
     */
	public String getMac(){
		return mNetNative.getMac();
	}

	/**
	 * 获取MAC
	 * @return 没有冒号，大写格式。如:003B5C3B2E3F
     */
	public String getMacFormat(){
		String mac = mNetNative.getMac();
		try {
			if (!TextUtils.isEmpty(mac))
				mac = mac.toUpperCase().replace(":", "").replace(".", "").replace("-", "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		ALOG.debug("MAC:"+mac);
		return mac;
	}

	public String getMask(){
		return mNetNative.getMask();
	}

	public String getGateWay(){
		return mNetNative.getGateWay();
	}

	public boolean isEthLinkUp(){
		boolean isLinkUp = false;
		if (STATUS_UP.equalsIgnoreCase(mNetLinkStatus)
				|| STATUS_DOWN.equalsIgnoreCase(mNetLinkStatus)) {
			isLinkUp = STATUS_UP.equalsIgnoreCase(mNetLinkStatus);
		} else {
			//有些芯片在PPPOE网络连接方式下，也是走的eth0网卡。实际上ppp0端口也是从eth0虚拟出来的，现在默认有线状态获取都从eth0端口获取
//			isLinkUp = mNetNative.isEthLinkUp(getConnectType());
			isLinkUp = mNetNative.isEthLinkUp(NetNative.IFNAME_ETH0);
			mNetLinkStatus = isLinkUp ? STATUS_UP : STATUS_DOWN;
		}
		return isLinkUp;
	}

	public String getDns1(){
		return RunTimeUtils.execCmd("getprop net.dns1");
	}

	public String getDns2(){
		return RunTimeUtils.execCmd("getprop net.dns2");
	}

    /**
     * wifi网络是否连接上
     * @return
     */
    public boolean isWifiConnected(){
		if (isCheckWifi) {
			return isNetworkConnected(NETTYPE_WIFI);
		} else {
			return false;
		}
    }

	/**
	 * NetConnectManager内部实现的监听器，用于NetConnectManager和NetWorkReciver之间的回调，保证1对1关系。
	 * 回调的实现为遍历外部添加进来的listener对象，逐一回调
	 * @author DonWZ
	 *
	 * 2016-9-18
	 */
	class NetConnectListener implements NetWorkListener{

		@Override
		public synchronized void onNetConnected(final int netType,final NetWorkExtra netExtra) {
//			ALOG.info("onNetConnected >>> netType : "+netType);
			if (!isLoopOver) {
				ALOG.info("NetConnectManager >> isNetLooping");
				return;
			}
			if (!STATUS_UP.equals(mNetWorkStatus)){
				mNetWorkStatus = STATUS_UP;
				if(!mListenerList.isEmpty()){
					MainThreadSwitcher.runOnMainThreadAsync(new Runnable() {
						@Override
						public void run() {
							for (NetWorkListener listener : mListenerList) {
								listener.onNetConnected(netType,netExtra);
							}
						}
					});
				}
			}
		}

		@Override
		public synchronized void onNetDisConnect(final int netType, final NetWorkExtra netExtra) {
//			ALOG.info("onNetDisConnect >>> netType : "+netType);
			if (!isLoopOver) {
				ALOG.info("NetConnectManager >> isNetLooping");
				return;
			}
			if(!STATUS_DOWN.equals(mNetWorkStatus)){
				mNetWorkStatus = STATUS_DOWN;
				if (!mListenerList.isEmpty()) {
					MainThreadSwitcher.runOnMainThreadAsync(new Runnable() {
						@Override
						public void run() {
							for (final NetWorkListener listener : mListenerList) {
								listener.onNetDisConnect(netType, netExtra);
							}
						}
					});
				}
			}
		}

		@Override
		public synchronized void onPhyLinkUp() {
//			ALOG.info("onPhyLinkUp >>> ");
			if (!isLoopOver) {
				ALOG.info("NetConnectManager >> isNetLooping");
				return;
			}
			if(!STATUS_UP.equals(mNetLinkStatus)){
				mNetLinkStatus = STATUS_UP;
				if (!mListenerList.isEmpty()) {
					MainThreadSwitcher.runOnMainThreadAsync(new Runnable() {
						@Override
						public void run() {
							for (final NetWorkListener listener : mListenerList) {
								listener.onPhyLinkUp();
							}
						}
					});
				}
			}
		}

		@Override
		public synchronized void onPhyLinkDown() {
//			ALOG.info("onPhyLinkDown >>> ");
			if (!isLoopOver) {
				ALOG.info("NetConnectManager >> isNetLooping");
				return;
			}
			if(!isCheckWifi()|| (isCheckWifi() && !isWifiConnected())){
				mNetWorkStatus = STATUS_DOWN;
			}
			ALOG.info("onPhyLinkDown >> STATUS_DOWN:"+STATUS_DOWN+" >>>mNetLinkStatus:"+mNetLinkStatus+"--mListenerList："+mListenerList.isEmpty());
			if(!STATUS_DOWN.equals(mNetLinkStatus)){
				mNetLinkStatus = STATUS_DOWN;
				if (!mListenerList.isEmpty()) {
					MainThreadSwitcher.runOnMainThreadAsync(new Runnable() {
						@Override
						public void run() {
							for (final NetWorkListener listener : mListenerList) {
								listener.onPhyLinkDown();
							}
						}
					});
				}
			}
		}

		@Override
		public synchronized void onNetInfoExtra(final NetWorkExtra networkExtra) {
//			ALOG.info("onNetInfoExtra >>");
			if (!isLoopOver) {
				ALOG.info("NetConnectManager >> isNetLooping");
				return;
			}
			if (!mListenerList.isEmpty()) {
				if (networkExtra.netType == NETTYPE_WIFI && isCheckWifi()) {
					MainThreadSwitcher.runOnMainThreadAsync(new Runnable() {
						@Override
						public void run() {
							boolean isWIfiConnected = isWifiConnected();
							ALOG.info("isWIFI connectd : " + isWIfiConnected);
							// 如果有线网络没有连接上，则回调wifi的连接状态。
							// 反之，如果有线网络是连接上的，忽略wifi的变化
							if (isWIfiConnected) {
								onNetConnected(NETTYPE_WIFI,networkExtra);
							} else {
								onNetDisConnect(NETTYPE_WIFI, networkExtra);
							}
						}
					});
				}else{
					MainThreadSwitcher.runOnMainThreadAsync(new Runnable() {
						@Override
						public void run() {
							for (NetWorkListener listener : mListenerList) {
								listener.onNetInfoExtra(networkExtra);
							}
						}
					});
				}
			}
		}
	}
	
	public void setCheckWifi(boolean flag){
		isCheckWifi = flag;
	}
	/**
	 * IPTV的网络状态是否需要包含WIFI状态。
	 * @return isCheckWifi ：
	 * 若为true，且有线网络没有插上，
	 * 则以WIFI状态为当前IPTV使用的网络状态。若有线无线都存在，则应该优先以有线网络为准。
	 * 若为false，则只以有线网络为准
	 */
	public boolean isCheckWifi(){
		return isCheckWifi;
	}

}
