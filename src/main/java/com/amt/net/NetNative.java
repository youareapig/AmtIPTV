package com.amt.net;

import android.text.TextUtils;

import com.amt.utils.ALOG;

import java.io.FileInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by DonWZ on 2017/1/12.
 * <p>
 * 网络模块本地JNI交互类。
 */

public class NetNative {
    /**
     * 网口名称：eth0(走DHCP、DHCP+/IPOE、STATIC网络方式。有线)
     */
    public static final String IFNAME_ETH0 = "eth0";
    /**
     * 网口名称：ppp0(走PPPOE拨号上网方式。有线)
     */
    public static final String IFNAME_PPP0 = "ppp0";
    /**
     * 网口名称：wlan0（wifi）
     */
    public static final String IFNAME_WLAN0 = "wlan0";
    /**默认有效端口，获取IP、MAC等将从默认端口里获取*/
    private String DefaultIFName = IFNAME_ETH0;

    /**
     * 标识当前网线是否插上。实际使用过程中，难免会多次获取网线状态，
     * 所以将状态放在内存里，避免每次都从so或者文件获取状态，以减小性能消耗
     */
//    private String ethLinkStatus = "";
    private static final String TAG="NetNative";
    private static final String STATUS_UP = "up";
    private static final String STATUS_DOWN = "down";

    private NetNative() {
        try {
            new Thread() {
                @Override
                public void run() {
                    nvInitAmtUtils();
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static NetNative instance = new NetNative();

    public static NetNative init() {
        return instance;
    }

    private NetWorkListener mNetWorkListener;

    protected void setNetListener(NetWorkListener netWorkListener) {
        mNetWorkListener = netWorkListener;
    }

    /**
     * 获取IP。（默认获取eth0端口的IP）
     *
     * @return
     */
    protected String getIpAddress() {
       return getIpAddress(DefaultIFName);
    }

    /**
     * 根据网络端口名获取IP
     *
     * @param netName
     * @return
     */
    protected String getIpAddress(String netName) {
        return nvGetIP(netName);

    }

    /**
     * 获取mac。默认获取eth0的mac地址
     *
     * @return
     */
    protected String getMac() {
        return getMac(DefaultIFName);
    }

    /**
     * 获取mac。(不推荐使用。MAC与业务绑定关系强，一般都获取eht0的MAC，推荐使用{@link #getMac()}接口。
     * 此接口仅在非常特殊的需求下需要获取其他网口MAC时才使用)
     *
     * @param netName 网口名称。因为业务上会以mac作为机顶盒的唯一标识，是固定不变的，所以一般获取eth0网口。
     * @return
     */
    protected String getMac(String netName) {
        String mac=nvGetMac(netName);
        ALOG.info(TAG,"mac-->"+mac+"   && netName-->"+netName);
        return mac;
    }

    protected String getMask() {
        return getMask(DefaultIFName);
    }

    protected String getMask(String netName) {
        return nvGetMask(netName);
    }

    protected String getGateWay() {
        return getGateWay(DefaultIFName);
    }

    protected String getGateWay(String netName) {
        String gateWay = nvGetGateWay(netName);
        ALOG.info("getGateWay > "+gateWay+", ifname : "+netName);
        return nvGetGateWay(netName);
    }

    /**
     * 网线是否插上
     * @param ifName 网卡名称。如eth0,ppp0,wlan0等
     * @return
     */
    protected boolean isEthLinkUp(String ifName) {
        boolean isEthUp = false;
        if(TextUtils.isEmpty(ifName)){
            ifName = IFNAME_ETH0;
        }
        String linkStatus = nvIsNetLinkUp(ifName);
//            ALOG.info("info", "isEthLinkUp from so> " + linkStatus);
        if (STATUS_UP.equalsIgnoreCase(linkStatus)
                || STATUS_DOWN.equalsIgnoreCase(linkStatus)) {
            isEthUp = STATUS_UP.equalsIgnoreCase(linkStatus);
        } else {
            isEthUp = STATUS_UP.equalsIgnoreCase(getEthLinkStatusByFile());
        }
//        ALOG.info("info", "isEthLinkUp > " + isEthUp);
        return isEthUp;
    }

    /**
     * 获取IP
     *
     * @param ifname 网口名称。
     * @return 以点号分隔，如192.168.1.1
     */
    private native String nvGetIP(String ifname);

    /**
     * 获取MAC
     *
     * @param ifname 网口名称。因为业务上会以mac作为机顶盒的唯一标识，是固定不变的，所以一般获取eth0网口。
     * @return 以冒号分隔开。如00:3b:5c:3b:2e:3f
     */
    private native String nvGetMac(String ifname);

    /**
     * 获取子网掩码
     *
     * @param ifname 网口名称。
     * @return
     */
    private native String nvGetMask(String ifname);

    /**
     * 获取默认网关
     *
     * @param ifname 网口名称。
     * @return
     */
    private native String nvGetGateWay(String ifname);

    /**
     * 网线是否插上。
     *
     * @return down：网线未插入。 up：网线插入
     */
    private native String nvIsNetLinkUp(String ifname);

    /**
     * 初始化,so向IptvService本地服务注册回调函数。耗时操作，需要放在线程里执行
     */
    private native void nvInitAmtUtils();

//    private native void nvRunSystemCmd(String cmd);

    /**
     * 从文件获取eth0网口状态。
     *
     * @return up:eth0可用（网线插上）  down:eth0不可用（网线未插上）
     */
    public static String getEthLinkStatusByFile() {
        String path = "sys/class/net/eth0/operstate";
        FileInputStream fis = null;
        String status = "";
        try {
            fis = new FileInputStream(path);
            byte[] b = new byte[16];
            int len = fis.read(b);
            status = new String(b, 0, len).trim();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        ALOG.info("getEthLinkStatusByFile >>> "+status);
        return status;
    }

    /**
     * 此接口是native调用java层的回调，原型不能随意修改
     *
     * @param msgType 消息类型，如network表示网络事件
     * @param netName 网卡名，如eth0,eth1,ppp0,wlan0等
     * @param status  状态 如up、down等
     * @param arg1    (扩展) 如 新的ip等
     */
    private static void onNotifyMessage(String msgType, String netName, String status, String arg1) {
        ALOG.info("onNetLinkChanged > msgType : " + msgType + ", netName : " + netName + ", status : " + status + ", arg1 : " + arg1);
        if (instance != null && instance.mNetWorkListener != null) {
            //网线相关的提示
            if ("netlink".equalsIgnoreCase(msgType) && IFNAME_ETH0.equalsIgnoreCase(netName)) {
                if (STATUS_UP.equalsIgnoreCase(status)) {
//                    ALOG.info("info","wz === onNotifyMessage >>>> onPhyLinkUp !!!!!");
                    instance.mNetWorkListener.onPhyLinkUp();
                } else if (STATUS_DOWN.equalsIgnoreCase(status)) {
//                    ALOG.info("info","wz === onNotifyMessage >>>> onPhyLinkDown !!!!!");
                    instance.mNetWorkListener.onPhyLinkDown();
                }
            }else if("network".equals(msgType) && (netName.startsWith("eth")||netName.startsWith("ppp"))){
                //网络相关的提示
                if(STATUS_UP.equalsIgnoreCase(status) && NetConnectManager.isValidIpAddress(arg1)){
                    String ip = arg1;
                    instance.mNetWorkListener.onNetConnected(NetConnectManager.NETTYPE_LINK, null);
                }else if (STATUS_DOWN.equalsIgnoreCase(status)){
                    instance.mNetWorkListener.onNetDisConnect(NetConnectManager.NETTYPE_LINK, null);
                }
            }
        }
    }


}
