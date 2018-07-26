package com.amt.amtdata;

/**
 * IPTV内部代码使用。存放IPTV自己使用的节点。IPTV内部数据全部统一使用此类的常量。
 * Created by DonWZ on 2017/3/28.
 */

public class IPTVData extends ServiceCfg{
    /**IPTV的LOG开关*/
    public static final String IPTV_LOG_ENABLE = "iptvlog_enable";
    /**是否自动保存EPG*/
    public static final String IPTV_SAVEEPG_ENABLE = "saveepg_enable";
    /**IPTV退出时的音量大小*/
    public static final String IPTV_Exit_VOLUME = "Service/ServiceInfo/Exit_Volume";
    /**判断程序是否首次进入IPTV*/
    public static final String IPTV_IsFirstEnter = "Service/ServiceInfo/isFirstEnter";
    /**可支持的流控制协议,如RTSP,IGMPv2*/
    public static final String Config_Support_Video_Protocols = "Service/ServiceInfo/config_SupportProtocols";
    /**可支持支持的传输层协议,如UDP,TCP,RTP,HTTP等*/
    public static final String Config_Support_Transport_Protocols = "Service/ServiceInfo/config_TransportProtocols";
    /**支持的传输层控制协议,如RTCP等*/
    public static final String Config_Support_Transport_CTL_Protocols = "Service/ServiceInfo/config_TransportCTLProtocols";
    /**支持的流封装协议，如MPEG2-TS等*/
    public static final String Config_Plextype = "Service/ServiceInfo/config_Plextype";
    /**记录当前开机是否是待机唤醒过程的。用于融合终端开机延时的判断。如果是待机唤醒的，不延时。否则延时*/
    public static final String IPTV_SCREEN_OFF_BOOT = "Service/ScreenOFF";
    /**支持的音频解码标准*/
    public static final String Config_AUDIOSTANDARDS = "Service/ServiceInfo/config_AudioStandards";
    /**支持的视频解码标准*/
    public static final String Config_VideoStandards = "Service/ServiceInfo/config_VideoStandards";
    /**记录IPTV上一次成功请求的EPGDomain地址，用于应急机制使用*/
    public static final String IPTV_Last_EPGDomain = "Service/Last/EPGDomain";

    /**实时媒体速率范围1－起始以100kbps 为单位例如：10 表示1Mbps,13 表示1.3Mbps默认值：16*/
    public static final String IPTV_CONFIG_PARAM_BITRATER1 = "Service/ServiceInfo/BitRateR1";
    /**实时媒体速率范围1－结束以100kbps 为单位例如： 10 表示1Mbps13 表示1.3Mbps9999 表示不限速默认值：9999*/
    public static final String IPTV_CONFIG_PARAM_BITRATER2 = "Service/ServiceInfo/BitRateR2";
    /**实时媒体速率范围2－起始以100kbps 为单位默认值：14*/
    public static final String IPTV_CONFIG_PARAM_BITRATER3 = "Service/ServiceInfo/BitRateR3";
    /** 实时媒体速率范围2－起始以100kbps 为单位默认值：14*/
    public static final String IPTV_CONFIG_PARAM_BITRATER4 = "Service/ServiceInfo/BitRateR4";
    /**实时媒体速率范围3－起始以100kbps 为单位默认值：12*/
    public static final String IPTV_CONFIG_PARAM_BITRATER5 = "Service/ServiceInfo/BitRateR5";
    /**实时媒体速率范围3－结束以100kbps 为单位默认值：14*/
    public static final String IPTV_CONFIG_PARAM_HDBITRATER1 = "Service/ServiceInfo/HD_BitRateR1";
    /**实时媒体速率范围4－起始以100kbps 为单位默认值：8*/
    public static final String IPTV_CONFIG_PARAM_HDBITRATER2 = "Service/ServiceInfo/HD_BitRateR2";
    /**实时媒体速率范围4－结束以100kbps 为单位默认值：12*/
    public static final String IPTV_CONFIG_PARAM_HDBITRATER3 = "Service/ServiceInfo/HD_BitRateR3";
    /**实时媒体速率范围5－起始以100kbps 为单位默认值：0*/
    public static final String IPTV_CONFIG_PARAM_HDBITRATER4 = "Service/ServiceInfo/HD_BitRateR4";
    /**实时媒体速率范围5－结束以100kbps 为单位默认值：8*/
    public static final String IPTV_CONFIG_PARAM_HDBITRATER5 = "Service/ServiceInfo/HD_BitRateR5";
    /**丢包率范围3－结束以0.01%为单位默认值：20*/
    public static final String IPTV_CONFIG_PARAM_HDPKGLOSTR1 = "Service/ServiceInfo/HD_PacketsLostR1";
    /**丢包率范围4－起始以0.01%为单位默认值：20*/
    public static final String IPTV_CONFIG_PARAM_HDPKGLOSTR2 = "Service/ServiceInfo/HD_PacketsLostR2";
    /**丢包率范围4－结束以0.01%为单位默认值：50*/
    public static final String IPTV_CONFIG_PARAM_HDPKGLOSTR3 = "Service/ServiceInfo/HD_PacketsLostR3";
    /**丢包率范围5－起始以0.01%为单位默认值：50*/
    public static final String IPTV_CONFIG_PARAM_HDPKGLOSTR4 = "Service/ServiceInfo/HD_PacketsLostR4";
    /** 丢包率范围5－结束以0.01%为单位默认值：9999*/
    public static final String IPTV_CONFIG_PARAM_HDPKGLOSTR5 = "Service/ServiceInfo/HD_PacketsLostR5";
    /**丢包率范围1 起始以0.01%为单位例如： 1 表示0.01%5 表示0.05%,默认值：0*/
    public static final String IPTV_CONFIG_PARAM_PKGLOSTR1 = "Service/ServiceInfo/PacketsLostR1";
    /**丢包率范围1 结束以0.01%为单位例如： 1 表示0.01%5 表示0.05%9999 表示最大*/
    public static final String IPTV_CONFIG_PARAM_PKGLOSTR2 = "Service/ServiceInfo/PacketsLostR2";
    /**丢包率范围2－起始以0.01%为单位默认值：0*/
    public static final String IPTV_CONFIG_PARAM_PKGLOSTR3 = "Service/ServiceInfo/PacketsLostR3";
    /**丢包率范围2－结束以0.01%为单位默认值：10*/
    public static final String IPTV_CONFIG_PARAM_PKGLOSTR4 = "Service/ServiceInfo/PacketsLostR4";
    /**丢包率范围3－起始以0.01%为单位默认值：10*/
    public static final String IPTV_CONFIG_PARAM_PKGLOSTR5 = "Service/ServiceInfo/PacketsLostR5";
    /**记录基本地址如：四色键地址*/
    public static final String IPTV_BASE_URL = "Service/ServiceInfo/BaseUrl/";

    public static final String IPTV_EPG_GROUP_NMB = "Service/ServiceInfo/EPGGroupNMB";

    public static final String IPTV_USER_GROUP_NMB = "Service/ServiceInfo/UserGroupNMB";

}
