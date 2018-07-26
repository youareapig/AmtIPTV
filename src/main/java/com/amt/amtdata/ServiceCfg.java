package com.amt.amtdata;

/**
 * 用于存放IPTV业务数据的节点常量(使用老版本的节点值，适用于兼容现网老版本的盒子)。
 * 数据节点分为 IPTV数据，Config其他数据。此类可通用于IPTV、设置、网管，用于统一各个模块的节点值
 * Created by DonWZ on 2017/1/25.
 */

public class ServiceCfg {

    //========================== IPTV 使用的数据，以IPTV_开头。start==========================
    /**IPTV帐号*/
    public static final String IPTV_Account = "Service/ServiceInfo/IPTVaccount";
    /**IPTV密码*/
    public static final String IPTV_Password = "Service/ServiceInfo/IPTVpassword";
    /**IPTV主认证地址*/
    public static final String IPTV_AuthURL = "Service/ServiceInfo/IPTVauthURL";
    /**IPTV备认证地址*/
    public static final String IPTV_AuthURLBackup = "Service/ServiceInfo/SecondIPTVauthURL";
    /**IPTV认证之后接收的UserToken*/
    public static final String IPTV_UserToken = "Service/UserToken";
    /**NTP地址*/
    public static final String IPTV_NTPDomain = "Service/ServiceInfo/ServerNTPUrl";
    /**备用NTP地址*/
    public static final String IPTV_NTPDomainBackup = "Service/ServiceInfo/ServerNTPBackupUrl";
    /**EPG首页地址（老版本没有这个字段）*/
    public static final String IPTV_EPGDomain = "Service/EPGDomain";
    /**备用EPG首页地址（老版本没有这个字段）*/
    public static final String IPTV_EPGDomainBackup = "Service/EPGDomainBackup";
    /**零配置成功失败 1-成功，0-失败*/
    public static final String IPTV_ZEROSETTING_STATUS = "Service/ServiceInfo/ZeroSettingsStatus";
    /**记录上一次播放的频道号。*/
    public static final String IPTV_LastchannelID = "Service/LastchannelID";
    /**IPTV进入模式。0:EPG首页 1:直播*/
    public static final String IPTV_EnterMode = "Service/EnterMode";
    //========================== IPTV 使用的数据，以IPTV_开头。end ==========================


    //========================== 设置apk使用的数据，有些数据是设置修改，供其他模块读取，如IPTV、MediaControl,网管，系统等 start ==========================
    /**DHCP用户名*/
    public static final String Config_DHCPUserName = "Service/ServiceInfo/DHCPUserName";
    /**DHCP密码*/
    public static final String Config_DHCPPassword = "Service/ServiceInfo/DHCPPassword";
    /**PPPOE用户名*/
    public static final String Config_PPPOEUserName = "Service/ServiceInfo/PPPOEUserName";
    /**PPPOE密码*/
    public static final String Config_PPPOEPassword = "Service/ServiceInfo/PPPOEPassword";
    /**是否开启DHCP+*/
    public static final String Config_DHCPEnable= "Service/ServiceInfo/DHCPEnable";
    /**升级服务器用户名*/
    public static final String Config_Upgrade_USER = "Service/ServiceInfo/config_Upgrade_UserName";
    /**升级服务器密码*/
    public static final String Config_Upgrade_PASSWD = "Service/ServiceInfo/config_Upgrade_Password";
    /**升级服务主地址*/
    public static final String Config_UpgradeDomain = "Service/ServiceInfo/UpgradeDomain";
    /**升级服务备用地址*/
    public static final String Config_UpgradeDomainBackup = "Service/ServiceInfo/UpgradeDomainBackup";
    /**静态设置的IP地址。用户或网管设置了静态IP后保存使用。不作为当前盒子的IP依据*/
    public static final String Config_Static_IpAddress = "Service/ServiceInfo/IpAddress";
    /**静态设置的子网掩码*/
    public static final String Config_Static_Mask = "Service/ServiceInfo/NetMask";
    /**静态设置的默认网关*/
    public static final String Config_Static_Gateway = "Service/ServiceInfo/DefaultGate";
    /**静态设置的DNS*/
    public static final String Config_Static_DNS = "Service/ServiceInfo/DNS";
    /**静态设置的备DNS*/
    public static final String Config_Static_DNSBackup = "Service/ServiceInfo/SecondNDS";
    /**传输方式
     * <li>0 MP2T/TCP</li>
     * <li>1 MP2T/UDP</li>
     * <li>2 MP2T/RTP/TCP</li>
     * <li>3 MP2T/RTP/UDP</li>
     */
    public static final String Config_TRANSPORT_MODE = "Service/ServiceInfo/TransportMode";
    /**停止播放后是否保留最后一帧，0为冻结最后一帧，1为擦除最后一帧*/
    public static final String Config_ERASE_LASTFRAME = "Service/ServiceInfo/EraseLastFrame";
    /**是否启用网管0:不启用，1启用*/
    public static final String Config_ITMS_Enable = "Service/ServiceInfo/Config_WebMaster";
    /**是否启用心跳0:不启用，1启用*/
    public static final String Config_ITMS_HeartBeat_Enable = "Service/ITMS/HeartBeat_Enable";
    /**网管设置中的心跳周期*/
    public static final String Config_ITMS_HeartBeat_Interval = "Service/ITMS/HeartBeat_Interval";
    /**网管地址*/
    public static final String Config_ITMS_ServiceUrl = "Service/ServiceInfo/config_WebmasterUrl";
    /**注册网管用户用户名*/
    public static final String Config_MANAGESERVER_USER = "Service/ServiceInfo/config_UserName";
    /**注册网管用户密码*/
    public static final String Config_MANAGESERVER_PASSWD = "Service/ServiceInfo/config_Password";
    /**CPE用户名*/
    public static final String Config_CPE_USERID = "Service/ServiceInfo/config_CPEUser";
    /**CPE密码*/
    public static final String Config_CPE_PASSWD = "Service/ServiceInfo/config_CPEPassword";
    /**IPTV认证的超时时间。用在网管控制IPTV双中心过程中的超时时间*/
    public static final String Config_AuthUrl_Timeout = "Service/ServiceInfo/AuthTimeOut";
    /**IPTV认证地址请求次数。用在网管控制IPTV双中心过程中的重试次数*/
    public static final String Config_AuthUrl_Request_Count = "Service/ServiceInfo/AuthResCnt";
    /**平台类型。0：华为  1：中兴*/
    public static final String Config_PlatForm = "Service/PlatForm"; // 老版本的节点名称为： plattype
    /**市场区域。如四川:sc 山东联通：SDLT等*/
    public static final String Config_Area = "Service/PLATTYPE";
    /**是否强制升级，强制升级是不比较版本号升级（降级）；0-不强制升级；1-强制升级*/
    public static final String Config_Upgrade_Force = "Service/ServiceInfo/ForceUpgrade";
    /**升级结果 0--失败，1--成功*/
    public static final String Config_Upgrade_Result = "Service/ServiceInfo/UpgradeResult";
    /**QOS服务地址*/
    public static final String Config_QosServerUrl = "Service/ServiceInfo/config_QosServerUrl";
    /**一键信息收集开关（联通的信息收集项）*/
    public static final String Config_Device_Log_Enable = "Service/ServiceInfo/SaveDeviceInfoLock";
    /**IPTV配置文件的目录和文件名*/
    public static final String Config_IptvConfigPath = "Service/IptvConfigPath";
    /**网管下发WIFI是否可用*/
    public static final String Config_LANDevice_Enable = "Service/LAN/LANDevice/Enable";
    /**网管下发WIFI ssid*/
    public static final String Config_LANDevice_SSID = "Service/LAN/LANDevice/SSID";
    /***/
    public static final String Config_LANDevice_Keypass = "Service/LAN/LANDevice/KeyPassphrase";

    public static final String Config_LANDevice_AuthMode = "Service/LAN/LANDevice/WIFIAuthenticationMode";

    public static final String Config_LANDevice_BeaconType = "Service/LAN/LANDevice/BeaconType";

    public static final String Config_WIFI_ON_OFF = "Service/LAN/LANDevice/WPAAuthenticationMode";
    //========================== 设置apk使用的数据，有些数据是设置修改，供其他模块读取，如Player,网管，系统等 end ==========================

    //================== MediaControl使用的数据，可能需要IPTV来预置。start ============================
    /**视频性能日志上传地址、*/
    public static final String Config_Performance_LogServerUrl="1.LogServerUrl";
    /**备用视频性能日志上传地址*/
    public static final String Config_Performance_LogServerUrl_Backup="2.LogServerUrl";
    /**日志统计时长*/
    public static final String Config_LogDuration = "Service/ServiceInfo/Logduration";
    /**性能监测参数文件上报间隔*/
    public static final String Config_Performance_Log_Interval = "Service/ServiceInfo/config_LoginTerval";
    /**性能监测统计参数的记录周期*/
    public static final String Config_Performance_Record_Interval = "Service/ServiceInfo/config_RecordInterval";
    /**丢包率、丢帧率、媒体流带宽统计周期*/
    public static final String Config_MONITORINGINTERVAL = "Service/ServiceInfo/config_MonitoringInterval";
    /**日志服务器*/
    public static final String Config_LogServer = "Service/ServiceInfo/LogServer";
    //================== MediaControl使用的数据，可能需要IPTV来预置。end ============================

    //================== 网管和IPTV公用的数据  start=====================================
    /**开机第一阶段图片URL*/
    public static final String Config_BootPicURL = "Service/ITMS_LOGO/BootPicURL";
    /**开机第一阶段图片是否显示*/
    public static final String Config_BootPicURL_Enable = "Service/ITMS_LOGO/BootPicURL_Enable";
    /**开机第一阶段图片更新结果*/
    public static final String Config_BootPicURL_Result = "Service/ITMS_LOGO/BootPicURL_Result";
    /**开机第一阶段图片显示时间*/
    public static final String Config_BootPicURL_Time = "Service/ITMS_LOGO/BootPicURL_Time";
    /**开机第二阶段图片URL*/
    public static final String Config_StartPicURL = "Service/ITMS_LOGO/StartPicURL";
    /**开机第二阶段图片是否显示*/
    public static final String Config_StartPicURL_Enable = "Service/ITMS_LOGO/StartPicURL_Enable";
    /**开机第二阶段图片更新结果*/
    public static final String Config_StartPicURL_Result = "Service/ITMS_LOGO/StartPicURL_Result";
    /**开机第二阶段图片显示时间*/
    public static final String Config_StartPicURL_Time = "Service/ITMS_LOGO/StartPicURL_Time";
    /**认证图片URL*/
    public static final String Config_AuthenticatePicURL = "Service/ITMS_LOGO/AuthenticatePicURL";
    /**认证图片是否显示*/
    public static final String Config_AuthenticatePicURL_Enable = "Service/ITMS_LOGO/AuthenticatePicURL_Enable";
    /**认证图片更新结果*/
    public static final String Config_AuthenticatePicURL_Result = "Service/ITMS_LOGO/AuthenticatePicURL_Result";
    /**认证图片显示时间*/
    public static final String Config_AuthenticatePicURL_Time = "Service/ITMS_LOGO/AuthenticatePicURL_Time";
    //================== 网管和IPTV公用的数据  end=====================================
}
