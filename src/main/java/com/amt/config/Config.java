package com.amt.config;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;

import com.amt.amtdata.AmtDataManager;
import com.amt.amtdata.IPTVData;
import com.amt.app.IPTVActivity;
import com.amt.app.IptvApp;
import com.amt.net.NetConnectManager;
import com.amt.player.iptvplayer.IPTVPlayer;
import com.amt.utils.ALOG;
import com.amt.utils.DeviceInfo;
import com.amt.utils.USBHelper;
import com.android.smart.terminal.iptvNew.R;

import java.text.SimpleDateFormat;

/**
 * 用于存放全局配置，如ttf读取出来的配置项
 * Created by DonWZ on  2016-9-5
 */
public class Config {

	/**是否自动保存服务端文件,警告：仅在开发调试时使用，影响运行速度*/
	public static boolean isAutoSaveWebPage = false;
	/**webCore debug级别*/
	public static int webCoreLevel = 0;
	/**v8 debug级别*/
	public static int v8SpinnerLevel = 0;
	/**网管APK 的包名*/
	public static String PACKAGENAME_TR069 ="com.androidmov.tr069";

	//=============== 以下是读取的DroidSans.ttf的配置项 =====
	/**平台类型。省份拼音首字母+运营商拼音首字母。如山东联通：SDLT*/
	public static final String PLATTYPE = DroidSans.getConfigStr("PLATTYPE");
	/**运营商,0:电信 1:联通*/
	public static final int OPERATORS = DroidSans.getConfigInt("OPERATORS");
	/**用户组。此用户组为成都卓影、深圳速影公司内部用户组，用于鉴别客户身份，统计发货数量*/
	public static final String USERGROUP = DroidSans.getConfigStr("USERGROUP");
	/**程序有效日期。当不限制日期时（如正式发货版本），配置文件应当默认为00000D01155BCCB624D0189AADD518(2099-12-31)。*/
	public static final String VALIDTIME = DroidSans.getConfigStr("VALIDTIME");
	/**是否为后台自动认证*/
	public static final boolean BackgroundAuth = DroidSans.getConfigBoolean("BackgroundAuth");
	/**APK绑定的机顶盒型号，如果不是配置的型号则退出,多个以";"隔开*/
	public static final String VALIDMODEL = DroidSans.getConfigStr("VALIDMODEL");
	/**融合终端型号。多个以";"隔开*/
	public static final String FUSIONMODEL = DroidSans.getConfigStr("FUSIONMODEL");
	/**是否开启远程DBUEG模式*/
	public static final boolean isRemoteDebug = DroidSans.getConfigBoolean("isRemoteDebug");
	/**开机时是否检查零配置状态。如果打开，且零配置状态为0，则开机卡85%*/
	public static final boolean CheckZeroSettings = DroidSans.getConfigBoolean("CheckZeroSettings");
	/**是否是真待机。*/
	public static final boolean isRealStandby = true;
	//=============== 以上是读取的DroidSans.ttf的配置项 =====
	private static final String TAG = "Config";
	/**当前运行版本的时间限制。值应为VALIDTIME解密后的值*/
	public static String timeBox = null;
	/**是否显示时间限制信息。只有日期小于2099-12-31时才显示，大于这个日期视为无时间限制版本，正式商用版本*/
	public static boolean showTimeBoxInfo = false;

	/**配置播放器是否支持多实例*/
	public static final boolean IS_PIPPLAYER = false;

	/**长按切台时处理模式：普通模式，长按时不切台，视频正常播放，只跳频道数字，按键抬起时切到最后一次进入的频道*/
	public static final int CHANNEL_MODE_NORMOL = 1;
	/**长按切台时处理模式：应时模式，每接收到一次切台按键，就切一次台*/
	public static final int CHANNEL_MODE_SEASONABLE = 2;
	/**长按切台时处理模式：暂停模式，长按时将当前视频静帧（只调用leavechannel，不调用stop和joinchannel），当按键抬起时调用joinchannel*/
	public static final int CHANNEL_MODE_PAUSE = 3;
	public static final int CHANNEL_MODE = CHANNEL_MODE_NORMOL;
	/**FEC限制型号*/
	public static final String FEC_MODEL = DroidSans.getConfigStr("FECMODEL");;

	/**
	 * 初始化DroidSans.ttf配置项，初始化部分业务数据
	 */
	public static void init(){
		ALOG.SECRET_DEBUG = AmtDataManager.getBoolean(IPTVData.IPTV_LOG_ENABLE,true);
		ALOG.info("SECRET_DEBUG > "+ALOG.SECRET_DEBUG);
		isAutoSaveWebPage = AmtDataManager.getBoolean(IPTVData.IPTV_SAVEEPG_ENABLE,false);
		setConfigInXML();
		//20171208 add by wenzong 设置打印开关给MediaControl。1:打开LOG输出   0:关闭LOG输出
		IPTVPlayer.setValue("setLogEnable","1");
	}

	/**
	 * 初始化业务数据。仅在IPTV首次启动时执行。
	 */
	private static void setConfigInXML(){
		boolean isFirtStart = AmtDataManager.getBoolean(IPTVData.IPTV_IsFirstEnter,true);
		if(isFirtStart) {
			Log.i(ALOG.TAG, "start initxml");
			ContentValues values = new ContentValues();
			values.put(IPTVData.Config_ITMS_HeartBeat_Interval, "3600");
			values.put(IPTVData.Config_ITMS_HeartBeat_Enable, "1");
			values.put(IPTVData.Config_Support_Video_Protocols, "RTSP,IGMPv2");
			values.put(IPTVData.Config_Support_Transport_Protocols, "UDP,TCP,RTP,HTTP");
			values.put(IPTVData.Config_Support_Transport_CTL_Protocols, "RTCP");
			values.put(IPTVData.Config_Plextype, "MPEG2-TS");
			values.put(IPTVData.Config_AUDIOSTANDARDS, "MPEG1-Part3-Layer2,MPEG1-Part3-Layer3,MPEG2-Part3-Layer2,MPEG2-Part3-Layer3,MP3-Surround,DOLBY-AC3");
			values.put(IPTVData.Config_VideoStandards, "H.264");
			values.put(IPTVData.Config_Performance_Log_Interval, "3600");
			values.put(IPTVData.Config_Performance_Record_Interval, "3600");
			values.put(IPTVData.Config_MONITORINGINTERVAL, "657925");
			values.put(IPTVData.IPTV_CONFIG_PARAM_BITRATER1, "100");
			values.put(IPTVData.IPTV_CONFIG_PARAM_BITRATER2, "96");
			values.put(IPTVData.IPTV_CONFIG_PARAM_BITRATER3, "92");
			values.put(IPTVData.IPTV_CONFIG_PARAM_BITRATER4, "88");
			values.put(IPTVData.IPTV_CONFIG_PARAM_BITRATER5, "88");
			values.put(IPTVData.IPTV_CONFIG_PARAM_HDBITRATER1, "100");
			values.put(IPTVData.IPTV_CONFIG_PARAM_HDBITRATER2, "96");
			values.put(IPTVData.IPTV_CONFIG_PARAM_HDBITRATER3, "92");
			values.put(IPTVData.IPTV_CONFIG_PARAM_HDBITRATER4, "88");
			values.put(IPTVData.IPTV_CONFIG_PARAM_HDBITRATER5, "88");
			values.put(IPTVData.IPTV_CONFIG_PARAM_HDPKGLOSTR1, "0");
			values.put(IPTVData.IPTV_CONFIG_PARAM_HDPKGLOSTR2, "1");
			values.put(IPTVData.IPTV_CONFIG_PARAM_HDPKGLOSTR3, "1");
			values.put(IPTVData.IPTV_CONFIG_PARAM_HDPKGLOSTR4, "3");
			values.put(IPTVData.IPTV_CONFIG_PARAM_HDPKGLOSTR5, "255");
			values.put(IPTVData.IPTV_CONFIG_PARAM_PKGLOSTR1, "0");
			values.put(IPTVData.IPTV_CONFIG_PARAM_PKGLOSTR2, "1");
			values.put(IPTVData.IPTV_CONFIG_PARAM_PKGLOSTR3, "1");
			values.put(IPTVData.IPTV_CONFIG_PARAM_PKGLOSTR4, "3");
			values.put(IPTVData.IPTV_CONFIG_PARAM_PKGLOSTR5, "255");
			values.put(IPTVData.Config_LogDuration, "0");
			values.put(IPTVData.Config_LANDevice_SSID, "AndroidAP");
			values.put(IPTVData.Config_LANDevice_Keypass, "2cdcc78cf16f");
			values.put(IPTVData.Config_LANDevice_AuthMode, "WPA2 PSK");
			values.put(IPTVData.Config_LANDevice_Enable, "0");
			AmtDataManager.putStringBatch(values);
			AmtDataManager.putBoolean(IPTVData.IPTV_IsFirstEnter, false, "");
			//写入配置文件版本号为1.初始版本
			AmtDataManager.updateConfigFileVersion(1);
			Log.i(ALOG.TAG, "end  initxml");
		}
	}

	/**
	 * 验证机顶盒型号，不合法则退出程序。注：必须由项目经理确认添加型号，不得随意听从客户要求添加！！！
	 */
	public static void checkSTB() {
		Log.i(ALOG.TAG,"==== STB MODEL : "+android.os.Build.MODEL);
		if (!TextUtils.isEmpty(VALIDMODEL)) {// 有配置的情况下,则去和系统MODEL匹配
			String[] models = VALIDMODEL.split(";");
			if (models != null && models.length > 0) {
				for (int i = 0; i < models.length; i++) {
					if (!TextUtils.isEmpty(models[i]))
						if ((android.os.Build.MODEL).equals(models[i])) {
							return;//匹配通过，直接返回，不做任何处理。
						}
				}
			}
		}
		Log.i(ALOG.TAG,"TWGDH");
		IPTVActivity.mDialogManager.showCheckSTBError("警告！！！"
				, R.string.error_title_vaildversion
				,R.string.error_suggest_vaildversion);

	}

	/**
	 * 底层有些数据通过这里获取，主要是有些节点兼容以前的要特殊处理下
	 * @param key
	 * @return
     */
	public static String getValue(String key){
		String value = "";
		if("STBID".equals(key)){
			value = DeviceInfo.STBID;
		}else if("PlatType".equals(key)){
			value = PLATTYPE;
		}else if("USBDIRECTORY".equals(key)){
			value = USBHelper.usbPath;
		}else if("VaildTime".equals(key)){
			value = VALIDTIME;
		}else if("ApkPath".equals(key)||"LibPath".equals(key)){
			value = "/data/data/"+ IptvApp.app.getPackageName()+"/";
		}else if("Service/ServiceInfo/ConnectModeString".equals(key)){
			value = IptvApp.mNetManager.getConnectType();
		}else if ("WifiOrEth".equalsIgnoreCase(key)){
			if (IptvApp.mNetManager.isWifiConnected() && !IptvApp.mNetManager.isNetworkConnected(NetConnectManager.NETTYPE_LINK)){
				value="1";
			}else {
				value="0";
			}
		}
		else if(TextUtils.isEmpty(value)){
			value = AmtDataManager.getString(key,"");
		}
		ALOG.info(TAG,"getValue > key : "+key+", value : "+value);
		return value;
	}

	public static int setValue(String key, final String value) {
		int result = 0;
		ALOG.info(TAG,"setValue > key : "+key+", value : "+value);
		//20171102 add by wenzong mediacontrol解析了时间限制后，吧时间返回给我们，我们根据时间提示版本有效期
		// （如果日期是2099年的，就说明不带时间限制，是正式商用版本，不能提示任何信息）
		if("VaildTime".equalsIgnoreCase(key)){
			timeBox = value;
			try{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				long validTime = sdf.parse(value).getTime();
				long targetTime = sdf.parse("2099-12-31").getTime();
				if(validTime < targetTime){
					//是带有时间限制的版本，需要弹一个Toast提示。
					showTimeBoxInfo = true;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return result;
	}

}
