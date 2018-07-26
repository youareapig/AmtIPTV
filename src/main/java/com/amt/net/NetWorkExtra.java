package com.amt.net;

/**
 * 网络状态事件抽象类，用于存放网络信息,如网络类型{@link #netType}（有线或无线），
 * 网络接入方式{@link #connectType}（dhcp、dhcp+、pppoe、static），网络状态{@link #status}、消息描述{@link #description}等。
 * 在使用监听{@link NetWorkListener#onNetInfoExtra(NetWorkExtra)}接口时传递此对象，规范数据传递的格式 。
 * 若后期需要扩展数据，直接扩展此实体类的属性
 * Created by DonWZ on 2016-9-7
 */
public class NetWorkExtra {
	/**连接成功*/
	public static final int STATUS_CONNECTED = 11;
	/**断开连接*/
	public static final int STATUS_DISCONNECTED = 12;
	/**未知状态*/
	public static final int STATUS_UNKNOWN = 13;

	/**PPPOE拨号失败（ADSL账号或密码错误）*/
	public static final String MSG_PPPOE_AUTH_FAILED = "pppoe_auth_failed";
	/**PPPOE拨号失败（ADSL拨号超时没有响应）*/
	public static final String MSG_PPPOE_ADSL_TIMEOUT = "pppoe_auth_timeout";
	/**DHCP+/IPOE 鉴权超时。DHCP服务器无响应*/
	public static final String MSG_DHCPPLUS_TIMEOUT = "ipoe_auth_timeout";
	/**未知的网络连接错误*/
	public static final String MSG_ERROR_UNKNOWN = "net_error_unknown";

	/**
	 * 网络接入方式。一般为{@link NetConnectManager#DHCPPlUS}、{@link NetConnectManager#DHCP}、
	 * {@link NetConnectManager#PPPOE}/{@link NetConnectManager#MANUAL}四种。网络接入方式
	 */
	public String connectType = "";
	/**网络类型，分有线、无线两种。默认有线*/
	public int netType = NetConnectManager.NETTYPE_LINK;
	/**网络状态*/
	public int status = STATUS_UNKNOWN;
	/**
	 * 网络事件描述。如DHCP鉴权失败的具体错误信息、错误代码等
	 * 可选值为{@link #MSG_DHCPPLUS_TIMEOUT}、{@link #MSG_ERROR_UNKNOWN}、{@link #MSG_PPPOE_ADSL_TIMEOUT}
	 * 、{@link #MSG_PPPOE_AUTH_FAILED}等。后期要扩展的话，建议新建常量，使用常量来规范值的格式，不要随意写入任意字符串。
	 */
	public String description = "";

}
