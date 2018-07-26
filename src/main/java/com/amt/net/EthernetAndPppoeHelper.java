package com.amt.net;

import android.content.Intent;

import com.amt.utils.ALOG;

/**
 * Created by DonWZ on 2017/1/12.
 * 专门处理PppoeManager.PPPOE_STATE_CHANGED_ACTION以及 EthernetManager.ETHERNET_STATE_CHANGED_ACTION广播
 */

public class EthernetAndPppoeHelper {

    public static final int EVENT_PHY_LINK_UP = 18;
    public static final int EVENT_PHY_LINK_DOWN = 19;
    // ethernet
    public static final int EVENT_DHCP_CONNECT_SUCCESSED = 10;
    public static final int EVENT_DHCP_CONNECT_FAILED = 11;
    public static final int EVENT_DHCP_DISCONNECT_SUCCESSED = 12;
    public static final int EVENT_DHCP_DISCONNECT_FAILED = 13;
    public static final int EVENT_DHCP_AUTORECONNECTING = 30;
    // static
    public static final int EVENT_STATIC_CONNECT_SUCCESSED = 14;
    public static final int EVENT_STATIC_CONNECT_FAILED = 15;
    public static final int EVENT_STATIC_DISCONNECT_SUCCESSED = 16;
    public static final int EVENT_STATIC_DISCONNECT_FAILED = 17;
    // pppoe
    public static final int EVENT_PPPOE_CONNECT_SUCCESSED = 0;
    public static final int EVENT_PPPOE_CONNECT_FAILED = 1;
    public static final int EVENT_PPPOE_CONNECT_FAILED_AUTH_FAIL = 2;
    public static final int EVENT_PPPOE_CONNECTING = 3;
    public static final int EVENT_PPPOE_DISCONNECT_SUCCESSED = 4;
    public static final int EVENT_PPPOE_DISCONNECT_FAILED = 5;
    public static final int EVENT_PPPOE_AUTORECONNECTING = 6;
    public static final int PPPOE_CONNECT_RESULT_UNKNOWN = 10;
    public static final int PPPOE_CONNECT_RESULT_CONNECT = 11;
    public static final int PPPOE_CONNECT_RESULT_CONNECTING = 12;
    public static final int PPPOE_CONNECT_RESULT_DISCONNECTING = 13;
    public static final int PPPOE_CONNECT_RESULT_DISCONNECT = 14;
    public static final int PPPOE_CONNECT_RESULT_SERVER_DISCONNECT = 15;
    public static final int PPPOE_CONNECT_RESULT_AUTH_FAIL = 16;
    public static final int PPPOE_CONNECT_RESULT_CONNECT_FAIL = 17;

    public static void handleNetInfo(Intent intent, NetWorkListener listener) {

        if (listener == null) {
            ALOG.error("","handleHISINetInfo > the NetWorkListener is null... do nothing");
            return;
        }
        String action = intent.getAction();
        int state = -1;
        NetWorkExtra netExtra = new NetWorkExtra();
        if (NetWorkReceiver.ETHERNET_STATE_CHANGED_ACTION.equals(action)) {
            state = intent.getIntExtra(NetWorkReceiver.EXTRA_ETHERNET_STATE, state);
            ALOG.info( "EthernetDhcpReceiver---message:" + state);
            switch (state) {
                case EVENT_DHCP_CONNECT_SUCCESSED:
                    ALOG.info( "EthernetDhcpReceiver----->EVENT_DHCP_CONNECT_SUCCESSED");
                    netExtra.status = NetWorkExtra.STATUS_CONNECTED;
                    netExtra.connectType = NetConnectManager.DHCPPlUS;
                    listener.onNetConnected(NetConnectManager.NETTYPE_LINK,netExtra);
                    break;
                case EVENT_DHCP_CONNECT_FAILED:// 0013-DHCP服务器没有响应,0014-未知的网络连接错误
                    ALOG.info( "EthernetDhcpReceiver----->EVENT_DHCP_CONNECT_FAILED");
                    netExtra.status = NetWorkExtra.STATUS_DISCONNECTED;
                    netExtra.connectType = NetConnectManager.DHCPPlUS;
                    if(state == 13){
                        netExtra.description = NetWorkExtra.MSG_DHCPPLUS_TIMEOUT;
                    }else if(state == 14){
                        netExtra.description = NetWorkExtra.MSG_ERROR_UNKNOWN;
                    }
                    listener.onNetDisConnect(NetConnectManager.NETTYPE_LINK,netExtra);
                    break;
                case EVENT_DHCP_DISCONNECT_SUCCESSED:
                    ALOG.info( "EthernetDhcpReceiver----->EVENT_DHCP_DISCONNECT_SUCCESSED:" + EVENT_DHCP_DISCONNECT_SUCCESSED);
//                    listener.onNetDisConnect(NetConnectManager.NETTYPE_LINK, "");
                    break;
                case EVENT_DHCP_AUTORECONNECTING:
                    ALOG.info( "EthernetDhcpReceiver----->EVENT_DHCP_AUTORECONNECTING:");
                    break;
                case EVENT_PHY_LINK_UP:
                    ALOG.info( "EthernetDhcpReceiver----->EVENT_PHY_LINK_UP:");
                    listener.onPhyLinkUp();
//                    NetNative.init().onReceivedLinkBroad("up");
                    break;
                case EVENT_PHY_LINK_DOWN:
                    ALOG.info( "EthernetDhcpReceiver----->EVENT_PHY_LINK_DOWN:");
                    listener.onPhyLinkDown();
//                    NetNative.init().onReceivedLinkBroad("down");
                    break;
                case EVENT_STATIC_CONNECT_SUCCESSED:
                    ALOG.info("EthernetDhcpReceiver----->EVENT_STATIC_CONNECT_SUCCESSED:");
                    netExtra.status = NetWorkExtra.STATUS_CONNECTED;
                    netExtra.connectType = NetConnectManager.MANUAL;
                    listener.onNetConnected(NetConnectManager.NETTYPE_LINK,netExtra);
                    break;
                case EVENT_STATIC_CONNECT_FAILED:
                    ALOG.info("EthernetDhcpReceiver----->EVENT_STATIC_CONNECT_FAILED:");
                    netExtra.status = NetWorkExtra.STATUS_CONNECTED;
                    netExtra.connectType = NetConnectManager.MANUAL;
                    listener.onNetDisConnect(NetConnectManager.NETTYPE_LINK,netExtra);
                    break;
            }
        } else if (NetWorkReceiver.PPPOE_STATE_CHANGED_ACTION.equals(action)) {
            state = intent.getIntExtra(NetWorkReceiver.EXTRA_PPPOE_STATE, state);
            String pppoeErroMsg = intent.getStringExtra(NetWorkReceiver.EXTRA_PPPOE_ERRMSG);
            switch (state) {
                case EVENT_PPPOE_CONNECT_SUCCESSED:
                    ALOG.info( "PppoeReceiver----->EVENT_CONNECT_SUCCESSED");
                    netExtra.status = NetWorkExtra.STATUS_CONNECTED;
                    netExtra.connectType = NetConnectManager.PPPOE;
                    listener.onNetConnected(NetConnectManager.NETTYPE_LINK,netExtra);
                    break;
                case EVENT_PPPOE_CONNECT_FAILED: // 1 ,0007,0008-ADSL拨号超时没有响应
                    ALOG.info( "PppoeReceiver----->EVENT_CONNECT_FAILED");
                    ALOG.info( "PppoeReceiver----->Erro msg : " + pppoeErroMsg);
                    netExtra.status = NetWorkExtra.STATUS_DISCONNECTED;
                    netExtra.connectType = NetConnectManager.PPPOE;
                    netExtra.description = NetWorkExtra.MSG_PPPOE_ADSL_TIMEOUT;
                    listener.onNetDisConnect(NetConnectManager.NETTYPE_LINK,netExtra);
                    break;
                case EVENT_PPPOE_CONNECT_FAILED_AUTH_FAIL:// 0006-ADSL账号或者密码有误
                    ALOG.info( "PppoeReceiver----->EVENT_CONNECT_FAILED_AUTH_FAIL");
                    ALOG.info( "PppoeReceiver----->Erro msg : " + pppoeErroMsg);
                    netExtra.status = NetWorkExtra.STATUS_DISCONNECTED;
                    netExtra.connectType = NetConnectManager.PPPOE;
                    netExtra.description = NetWorkExtra.MSG_PPPOE_AUTH_FAILED;
                    listener.onNetDisConnect(NetConnectManager.NETTYPE_LINK, netExtra);
                    break;
                case EVENT_PPPOE_CONNECTING:
                    ALOG.info( "PppoeReceiver----->EVENT_CONNECTING");
                    break;
                case EVENT_PPPOE_DISCONNECT_SUCCESSED:
                    ALOG.info( "PppoeReceiver----->EVENT_DISCONNECT_SUCCESSED");
//                    listener.onNetDisConnect(NetConnectManager.NETTYPE_LINK, "");
                    break;
                case EVENT_PPPOE_AUTORECONNECTING:
                    ALOG.info( "PppoeReceiver----->EVENT_AUTORECONNECTING");
                    break;
            }
        }

    }
}
