package com.amt.net;

/**
 * 网络状态监听器
 * Created by DonWZ on 2016-9-5
 */
public interface NetWorkListener {
	/**
	 * 网络连接成功。当{@link NetConnectManager#isCheckWifi()} 为true时，wifi连接成功也通过此函数回调。否则通过
	 * {@link #onNetInfoExtra(NetWorkExtra)}回调
	 * @param netType 网络连接类型。
	 * {@link NetConnectManager#NETTYPE_LINK}: 有线网络
	 * {@link NetConnectManager#NETTYPE_WIFI}: 无线网络
	 * @param networkExtra 网络信息。如pppoe拨号状态等。扩展用
	 */
	void onNetConnected(int netType, NetWorkExtra networkExtra);
	/**
	 * 网络断开。当{@link NetConnectManager#isCheckWifi()} 为true时，wifi连接失败也通过此函数回调。否则通过
	 * {@link #onNetInfoExtra(NetWorkExtra)}回调
	 * @param netType 网络连接类型。
	 * {@link NetConnectManager#NETTYPE_LINK}: 有线网络
	 * {@link NetConnectManager#NETTYPE_WIFI}: 无线网络
	 * @param networkExtra 网络信息。如pppoe拨号状态等。扩展用
	 */
	void onNetDisConnect(int netType, NetWorkExtra networkExtra);
	/**
	 * 网线插上。此回调仅代表网线拔插状态，不能代表网络连接状态。
	 * 若获取网络连接状态，以{@link #onNetConnected(int,NetWorkExtra)}、{@link #onNetDisConnect(int, NetWorkExtra)}和
	 * {@link NetConnectManager#isNetworkConnected()}为准
	 */
	void onPhyLinkUp();
	/**
	 * 网线拔掉。此回调仅代表网线拔插状态，不能代表网络连接状态。
	 * 若获取网络连接状态，以{@link #onNetConnected(int,NetWorkExtra)}、{@link #onNetDisConnect(int, NetWorkExtra)}和
	 * {@link NetConnectManager#isNetworkConnected()}为准
	 */
	void onPhyLinkDown();
	/**
	 * 其他网络事件，如dhcp鉴权的实时状态，pppoe拨号的实时状态，wifi状态改变信息
	 * @param networkExtra  通过networkExtra携带具体信息
	 */
	void onNetInfoExtra(NetWorkExtra networkExtra);
}
