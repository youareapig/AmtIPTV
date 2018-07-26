package com.SyMedia.SyDebug;

import android.app.ActivityManagerNative;
import android.app.AlarmManager;
import android.app.IActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.KeyEvent;

import com.SyMedia.webkit.SyWebView;
import com.amt.amtdata.AmtDataManager;
import com.amt.amtdata.IPTVData;
import com.amt.app.IptvApp;
import com.amt.config.Config;
import com.amt.net.NetConnectManager;
import com.amt.player.entity.LiveChannel;
import com.amt.player.entity.LiveChannelHelper;
import com.amt.utils.ALOG;
import com.amt.utils.APKHelper;
import com.amt.utils.DeviceInfo;
import com.amt.utils.NetConnnectEditor;
import com.amt.utils.USBHelper;
import com.amt.utils.Utils;
import com.amt.utils.powermanager.AmtPowerManager;
import com.amt.webview.WebViewManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.TimeZone;

/***
 * 远程控制模块
 * 
 * @author zhs
 * 
 */
public class RemoteDebug {
	static final int MSG_REMOTEDEBUG_START = 1;
	static final int MSG_REMOTEDEBUG_STOP = 2;
	static final int MSG_REMOTEDEBUG_CAPTURESTOP = 3;
	static final int MSG_REMOTEDEBUG_CONNECTMANAGER = 4;
	private static boolean isRDLog = false;
	private static boolean cancelLoad = false;
	private static boolean stoping = false;
	private static boolean isCapturePicture = false;
	private static Picture picture;
	private static boolean NeedRemoteDebug = false;
	public static boolean isDebuging = false;
	private static String strManagerIP;
	private static Handler handler = null;
	private static final String TAG = "RemoteDebug";

	public RemoteDebug() {

	}

	static public boolean IsRDLog() {
		return isRDLog;
	}

	/**
	 * 加载运维工具，远程
	 * 
	 * @param isRemote
	 *            true:远程，需要外网服务器， false:局域网
	 * @param strIP
	 *            反向连接的IP
	 */
	static public void load(boolean isRemote, String strIP) {
		load(isRemote);
		strManagerIP = strIP;
		handler.sendEmptyMessage(MSG_REMOTEDEBUG_CONNECTMANAGER);
	}

	static public void load(boolean isRemote) {
		ALOG.debug(TAG,"load > isRemote" + isRemote + ",iscancel:" + cancelLoad);
		if (handler == null) {
			handler = new Handler() {
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case MSG_REMOTEDEBUG_START:
						String strUser = AmtDataManager.getString(IPTVData.IPTV_Account,"");
						Start(strUser, Config.PLATTYPE, NeedRemoteDebug);
						break;
					case MSG_REMOTEDEBUG_STOP:
						Stop();
						cancelLoad = false;
						stoping = false;
						break;
					case MSG_REMOTEDEBUG_CAPTURESTOP:
						ALOG.info(TAG,"stop capture picture");
						WebViewManager.getManager().setPictureListener(null);
						isCapturePicture = false;
						picture = null;
						break;
					case MSG_REMOTEDEBUG_CONNECTMANAGER:
						ConnectManager(strManagerIP);
						break;
					}
				}
			};
		}

		NeedRemoteDebug = isRemote;
		if (cancelLoad) {
			return;
		}
		handler.sendEmptyMessage(MSG_REMOTEDEBUG_START);
		isDebuging = true;
	}

	static public void Unload() {
		if (stoping) {
			cancelLoad = true;
			return;
		}
		if (handler == null)
			return;
		stoping = true;
		handler.sendEmptyMessage(MSG_REMOTEDEBUG_STOP);
		isRDLog = false;
		isDebuging = false;
	}

	static public void Log(int level, int type, String strMsg) {
		if (isRDLog) {
			SendLog(level, type, strMsg);
		}
	}

	static private byte[] CapturePicture(float x, float y) {
		byte[] result = null;

		if (x < 0.1 && y < 0.1) {
			ALOG.info(TAG,"stop capture picture");
			WebViewManager.getManager().setPictureListener(null);
			isCapturePicture = false;
			picture = null;
			return null;
		}
		handler.removeMessages(MSG_REMOTEDEBUG_CAPTURESTOP);
		if (!isCapturePicture) {
			WebViewManager.getManager().setPictureListener(new SyWebView.PictureListener() {
				@Override
				public void onNewPicture(SyWebView syWebView, Picture picture) {
					RemoteDebug.picture = picture;
				}
			});
			isCapturePicture = true;
		}
		Picture pic = picture;
		if (pic == null){
			WebViewManager.getManager().capturePicture();
		}
		int width = pic.getWidth();
		int height = pic.getHeight();
		if (width > 0 && height > 0) {
			Matrix matrix = new Matrix();
			matrix.setScale(x, y);
			Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bmp);
			pic.draw(canvas);
			Bitmap bmp2 = Bitmap.createBitmap(bmp, 0, 0, width, height, matrix, true);
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				// String fileName = Config.USBDIRECTORY +
				// System.currentTimeMillis() + ".jpeg";
				// FileOutputStream fos = new FileOutputStream(fileName);
				if (out != null) {
					// ALOG.info("bmp compress before");
					bmp2.compress(Bitmap.CompressFormat.JPEG, 90, out);
					result = out.toByteArray();
					out.close();
				}
				// Toast.makeText(getApplicationContext(), "截图成功，文件名是：" +
				// fileName, Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		handler.sendEmptyMessageDelayed(MSG_REMOTEDEBUG_CAPTURESTOP, 5000);
		return result;
	}

	static private void setNotify(String strName, String strValue) {
		ALOG.debug(TAG,"setNotify->strName:" + strName + ",strValue:" + strValue);
		if (strName.equalsIgnoreCase("LOG")) {// LOG开关
			// Config.DEBUG = strValue.equalsIgnoreCase("true");
			if (strValue != null)
				isRDLog = strValue.equalsIgnoreCase("true");
			else
				isRDLog = false;
		} else if (strName.equalsIgnoreCase("SAVEEPG")) {// 保存ＥＰＧ开关
			WebViewManager.getManager().updateEPGPath(strValue);
		} else if (strName.equalsIgnoreCase("RELOGIN")) {// 重新认证
			IptvApp.authManager.startAuthService();
		} else if (strName.equalsIgnoreCase("KEY")) {// 模拟按键，以","分隔的IPTV键值
			String strKeys[] = strValue.split(",");
			for (int i = 0; i < strKeys.length; i++) {
				int keyCode = Integer.parseInt(strKeys[i]);
				WebViewManager.getManager().sendKeyToWeb(new KeyEvent(keyCode,KeyEvent.ACTION_DOWN));
			}
		} else if (strName.equalsIgnoreCase("RestoreFact")) {// 恢复出厂设置
			AmtPowerManager.restoreFatory();
//			Intent intent = new Intent("android.intent.action.IPTV.DATA");
//			intent.putExtra("Type", 1001);// 1000:网络相关，1001:恢复出厂，1002:重启机顶盒
//			IptvApp.app.sendBroadcast(intent);
		} else if (strName.equalsIgnoreCase("Sys_Upgrade")) {// 系统升级命令
//			IptvApp.settingInterface.SystemUpgrade(strValue);
		} else if (strName.equalsIgnoreCase("Reboot")) {
//			Intent intent = new Intent("android.intent.action.IPTV.DATA");
//			intent.putExtra("Type", 1002);// 1000:网络相关，1001:恢复出厂，1002:重启机顶盒
//			IptvApp.app.sendBroadcast(intent);
			AmtPowerManager.reboot();
		} else if (strName.equalsIgnoreCase("Resolution")) {// 分辨率设置20150703
			if (!TextUtils.isEmpty(strValue)) {
				Intent intent = new Intent("android.intent.action.IPTV.DATA");
				intent.putExtra("Type", 1003);// 1000:网络相关，1001:恢复出厂，1002:重启机顶盒,1003:视频输出格式设置(PAL/NTSC/分辨率)
				// 480i 50HZ
				// 576i 50HZ
				// 720P 50HZ
				// 720P 60HZ
				// 1080i 50HZ
				// 1080i 60HZ
				// 1080P 50HZ
				// 1080P 60HZ
				// 2160P 24HZ
				// 2160P 30HZ
				intent.putExtra("Resolution", strValue.replace(" ", ""));
				IptvApp.app.sendBroadcast(intent);
			}
		}
	}

	static private void setValue(String strName, String strValue) {
		try {
			ALOG.info(TAG,"setValue->strName:" + strName +",strValue:" + strValue);
			if (strName.equalsIgnoreCase("SetConfig")) {
				try {
					// strValue数据格式如：[{name:"Service/ServiceInfo/PPPOEUserName",value:"1111111"},{name:"Service/ServiceInfo/PPPOEPassword",value:"2222222"}]
					JSONArray ja = new JSONArray(strValue);
					boolean isAuthChanged = false;
					for (int i = 0; i < ja.length(); i++) {
						org.json.JSONObject obj = ja.getJSONObject(i);
						String sName = obj.get("name").toString();
						String sValue = obj.get("value").toString();
						if(!isAuthChanged
								&& (// 认证信息是否改变
								sName.equalsIgnoreCase(IPTVData.IPTV_Account)
										|| sName.equalsIgnoreCase(IPTVData.IPTV_Password)
										|| sName.equalsIgnoreCase(IPTVData.IPTV_AuthURL))){
							isAuthChanged = true;
						}
						if("Service/ServiceInfo/IptvTimeZoneId".equalsIgnoreCase(sName)){
							AlarmManager mAlarmManager = (AlarmManager) IptvApp.app.getSystemService(Context.ALARM_SERVICE);
							mAlarmManager.setTimeZone(sValue);
						}else if("Resolution".equalsIgnoreCase(sName)){
							// TODO 分辨率设置
							if (!TextUtils.isEmpty(sValue)) {
								Intent intent = new Intent("android.intent.action.IPTV.DATA");
								intent.putExtra("Type", 1003);
								Integer iValue = 0;
								try {
									iValue = Integer.valueOf(sValue);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
								intent.putExtra("Resolution", iValue);
								sendBroadCastToSetting(intent);
							}
						}else if("Service/ServiceInfo/VideoMode".equalsIgnoreCase(sName)){
							//TODO 屏显模式
							Intent intent = new Intent("android.intent.action.IPTV.DATA");
							intent.putExtra("Type", 1006);
							Integer iValue = 0;
							try {
								iValue = Integer.valueOf(sValue);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							intent.putExtra("Service/ServiceInfo/VideoMode", iValue);
							sendBroadCastToSetting(intent);
						}else if ("Service/ServiceInfo/AudioMode".equalsIgnoreCase(sName)) {
							// TODO 声音模式
							Intent intent = new Intent("android.intent.action.IPTV.DATA");
							intent.putExtra("Type", 1005);
							Integer iValue = 0;
							try {
								iValue = Integer.valueOf(sValue);
							} catch (Exception ex) {
								ex.printStackTrace();
							}
							intent.putExtra("Service/ServiceInfo/AudioMode", iValue);
							sendBroadCastToSetting(intent);
						}else if ("Language".equalsIgnoreCase(sName)){
							if (!TextUtils.isEmpty(sValue)) {
								try {
									IActivityManager iam = ActivityManagerNative.getDefault();
									Configuration config = iam.getConfiguration();
									if (sValue.equals("1")) {
										config.locale = Locale.ENGLISH;
										AmtDataManager.putString("Service/ServiceInfo/ChooseLanguage","1","RemoteDebug");
									} else if (sValue.equals("0")) {
										config.locale = Locale.SIMPLIFIED_CHINESE;
										AmtDataManager.putString("Service/ServiceInfo/ChooseLanguage","0","RemoteDebug");
									}
									ActivityManagerNative.getDefault().updateConfiguration(config);
								} catch (RemoteException e) {
									e.printStackTrace();
								}
							}
						}else if ("Install".equalsIgnoreCase(sName)) {
							//TODO 安装应用[{name:\"Install\",value:\"%s\"}] value值为完整路径
							//IptvApp.app.bindSTBTerminalBaseClientService();
							//IptvApp.app.getmSTBTerminalBaseClient().sendCmd(STBTerminalBaseClient.INSTALL_STB_APP,sValue);
						} else if ("UnInstall".equalsIgnoreCase(sName)) {
							//TODO 卸载应用[{name:\"UnInstall\",value:\"%s\"}]　value对应应用的包名
//							IptvApp.app.bindSTBTerminalBaseClientService();
//							IptvApp.app.getmSTBTerminalBaseClient().sendCmd(STBTerminalBaseClient.DELETE_STB_APP,sValue);
						} else if ("ClearCache".equalsIgnoreCase(sName)) {
							//TODO 清除缓存[{name:\"ClearCache\",value:\"%s\"}]　value对应应用的包名
//							IptvApp.app.bindSTBTerminalBaseClientService();
//							IptvApp.app.getmSTBTerminalBaseClient().sendCmd(STBTerminalBaseClient.CLEAR_CACHE_STB_APP,sValue);
						} else if ("ClearProcess".equalsIgnoreCase(sName)) {
//							IptvApp.app.bindSTBTerminalBaseClientService();
//							IptvApp.app.getmSTBTerminalBaseClient().sendCmd(STBTerminalBaseClient.STOP_STB_ALL_APPS,sValue);
						} else if ("PlayUrl".equalsIgnoreCase(sName)) {// 通知播放地址
							Intent intent = new Intent("android.intent.action.IPTV.DATA");
							intent.putExtra("Type", 1010);// 1010:通知播放器播放
							intent.putExtra("Value", sValue);// 播放地址
							sendBroadCastToSetting(intent);
						}else if("Service/ServiceInfo/config_HeartBeat".equals(sName)) {
							AmtDataManager.putString(IPTVData.Config_ITMS_HeartBeat_Interval,sValue,"RemoteDebug");
						}else  if("Service/ServiceInfo/TransmissionMode".equals(sName)){
							AmtDataManager.putString(IPTVData.Config_TRANSPORT_MODE,sValue,"RemoteDebug");
						}
						else if("Service/ServiceInfo/ConnectModeString".equalsIgnoreCase(sName)){
							int connectType = Integer.valueOf(sValue);
							String newConnetType = NetConnectManager.DHCP;
							switch (connectType){
								case 0://pppoe
									newConnetType = NetConnectManager.PPPOE;
									break;
								case 1:// dhcp / dhcp+ / ipoe
									newConnetType = NetConnectManager.DHCPPlUS;
									break;
								case 2://静态方式
									newConnetType = NetConnectManager.MANUAL;
									break;
							}
							NetConnnectEditor netInfo = NetConnnectEditor.init(IptvApp.app);
							netInfo.setImmediately(true);
							netInfo.setConnectType(newConnetType);
						}else{
							AmtDataManager.putString(sName, sValue, "RemoteDebug");
							NetConnnectEditor netInfo = NetConnnectEditor.init(IptvApp.app);
							if(IPTVData.Config_PPPOEUserName.equalsIgnoreCase(sName)){
								netInfo.setImmediately(true);
								netInfo.setPPPOEUserName(sValue);
							}else if(IPTVData.Config_PPPOEPassword.equalsIgnoreCase(sName)){
								netInfo.setImmediately(true);
								netInfo.setPPPOEPassword(sValue);
							}else if(IPTVData.Config_DHCPUserName.equalsIgnoreCase(sName)){
								netInfo.setImmediately(true);
								netInfo.setDHCPUserName(sValue);
							}else if(IPTVData.Config_DHCPPassword.equalsIgnoreCase(sName)){
								netInfo.setImmediately(true);
								netInfo.setDHCPPassword(sValue);
							}else if(IPTVData.Config_Static_IpAddress.equalsIgnoreCase(sName)){
								netInfo.setImmediately(true);
								netInfo.setmIpAddress(sValue);
							}else if(IPTVData.Config_Static_Mask.equalsIgnoreCase(sName)){
								netInfo.setImmediately(true);
								netInfo.setmMask(sValue);
							}else if(IPTVData.Config_Static_Gateway.equalsIgnoreCase(sName)){
								netInfo.setImmediately(true);
								netInfo.setmGateWay(sValue);
							}else if(IPTVData.Config_Static_DNS.equalsIgnoreCase(sName)){
								netInfo.setImmediately(true);
								netInfo.setmDNS(sValue);
							}else if(IPTVData.Config_Static_DNSBackup.equalsIgnoreCase(sName)){
								netInfo.setImmediately(true);
								netInfo.setmDNS2(sValue);
							}
						}
					}
					//业务数据发生改变，在已经认证成功的情况下，发起重新认证
					if (isAuthChanged && IptvApp.authManager.isAuth && APKHelper.isIptvTop(IptvApp.app)) {
						IptvApp.authManager.startAuthService();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void sendBroadCastToSetting(Intent intent){
		if(intent != null){
			ALOG.info(TAG,"sendBroadCastToSetting > Action : "+intent.getAction());
			ALOG.info(TAG,"sendBroadCastToSetting > Extra : "+ Utils.viewBundle(intent.getExtras()));
			IptvApp.app.sendBroadcast(intent);
		}
	}

	public static String getValue(String strName, String strParam) {
		ALOG.info(TAG,"getValue > "+strName+", "+strParam);
		try {
			if (strName == null)
				return null;
			if (strName.equalsIgnoreCase("GetSTBInfo")) {// 获取机顶盒信息
				String str = "机顶盒型号:";
				str += DeviceInfo.MODEL;
				str += "\r\n";
				str += "STBID:";
				str += DeviceInfo.STBID;
				str += "\r\n";
				str += "软件版本:";
				str += APKHelper.getAppVersionName(IptvApp.app,IptvApp.app.getPackageName());
				str += "\r\n";
				str += "MAC地址:";
				str += DeviceInfo.MAC;
				str += "\r\n";
				str += "接入方式:";
				str += IptvApp.mNetManager.getConnectType();
				str += "\r\n";
				str += "IP地址:";
				str += getIp();
				str += "\r\n";
				str += "默认网关:";
				str += IptvApp.mNetManager.getGateWay();
				str += "\r\n";
				str += "DNS地址:";
				str += IptvApp.mNetManager.getDns1();
				str += "\r\n";
				str += "业务帐号:";
				str += AmtDataManager.getString(IPTVData.IPTV_Account,"");
				ALOG.info(TAG,"getValue > result : "+str);
				return str;
				/*
				 * 机顶盒型号: STBID: 软件版本: 编译日期: 浏览器版本: 硬件版本: MAC地址: 接入方式: IP地址:
				 * 默认网关: DNS地址: 业务帐号: CA类型: 芯片ID:
				 */
			} else if (strName.equalsIgnoreCase("GetServerInfo")) {// 获取服务器信息
				String str;
				str = "主认证地址:";
				str += AmtDataManager.getString(IPTVData.IPTV_AuthURL,"");
				str += "\r\n";
				str += "备认证地址:";
				str += AmtDataManager.getString(IPTVData.IPTV_AuthURLBackup,"");
				str += "\r\n";
				str += "当前EPG地址:";
				str += AmtDataManager.getString(IPTVData.IPTV_EPGDomain,"");
				str += "\r\n";
				str += "升级服务器:";
				str += AmtDataManager.getString(IPTVData.Config_UpgradeDomain,"");
				str += "\r\n";
				str += "NTP:";
				str += AmtDataManager.getString(IPTVData.IPTV_NTPDomain,"");
				str += "\r\n";
				str += "当前时区:";
				str += TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT);//"+8:00";
				str += "\r\n";
				str += "当前时间:";
				str += (new Timestamp(System.currentTimeMillis())).toString();
				str += "\r\n";
				str += "Qos日志服务:";
				str += AmtDataManager.getString(IPTVData.Config_Performance_LogServerUrl,"");
				str += "\r\n";
				str += "终端网管地址:";
				str += AmtDataManager.getString(IPTVData.Config_ITMS_ServiceUrl,"");
				str += "\r\n";
				str += "始终同步服务器地址";
				str += AmtDataManager.getString(IPTVData.IPTV_NTPDomain,"");
				ALOG.info(TAG,"getValue > result : "+str);
				return str;
				/*
				 * 主认证地址: 备认证地址: 当前EPG地址: 升级服务器: NTP: 当前时区: 当前时间: Qos日志服务:
				 * 终端网管地址: TVMS地址:
				 */
			} else if (strName.equalsIgnoreCase("GetChannelList")) {// 获取频道列表
				// 频道号 时移　频道URL 时移URL
				ALOG.info(TAG, "GetChannelList enter");
				StringBuilder strResult = new StringBuilder();
				SparseArray<LiveChannel> channelList = LiveChannelHelper.getInstance().getChannelInfo();
				if(channelList!=null && channelList.size() > 0){
					for(int i=0;i<channelList.size();i++){
						int key = channelList.keyAt(i);
						LiveChannel channel = channelList.get(key);
						strResult.append("{UserChannelID=\"");
						strResult.append(channel.getUserChannelID());
						strResult.append("\",ChannelName=\"");
						strResult.append(channel.getChannelName());
						strResult.append("\",TimeShift=\"");
						strResult.append(channel.getTimeShift());
						strResult.append("\",ChannelURL=\"");
						strResult.append(channel.getChannelURL());
						strResult.append("\",TimeShiftURL=\"");
						strResult.append(channel.getTimeShiftURL());
						strResult.append("\"}\r\n");
					}
				}
				ALOG.info(TAG, "GetChannelList leave");
				return strResult.toString();
			} else if (strName.equalsIgnoreCase("GetConfig") && strParam != null) {
				String[] strArr = strParam.split(",");
				String strResult = "";
				for (int i = 0; i < strArr.length; i++) {
					ALOG.info(TAG,"GetConfig > KEY : " +strArr[i]);
					// 系统升级下载地址
					if ("Sys_GetSavePath".equalsIgnoreCase(strArr[i])) {
						strResult += getSystemPaht();
					} else if("Service/ServiceInfo/ConnectModeString".equals(strArr[i])){
						String connectType = IptvApp.mNetManager.getConnectType();
						String  connectMode = "1";
						if("dhcp".equalsIgnoreCase(connectType) || "dhcp+".equalsIgnoreCase(connectType)|| "ipoe".equalsIgnoreCase(connectType)){
							connectMode = "1";
						}else if("manual".equalsIgnoreCase(connectType)|| "static".equalsIgnoreCase(connectType)){
							connectMode = "2";
						}else if("pppoe".equalsIgnoreCase(connectType)){
							connectMode = "0";
						}
						strResult += connectMode;
					}else if("Service/ServiceInfo/IpAddress".equalsIgnoreCase(strArr[i])){
						strResult += IptvApp.mNetManager.getIp();
					}else if("Service/ServiceInfo/NetMask".equalsIgnoreCase(strArr[i])){
						strResult += IptvApp.mNetManager.getMask();
					}else if("Service/ServiceInfo/DefaultGate".equalsIgnoreCase(strArr[i])){
						strResult += IptvApp.mNetManager.getGateWay();
					}else if("Service/ServiceInfo/DNS".equalsIgnoreCase(strArr[i])){
						strResult += IptvApp.mNetManager.getDns1();
					}else if("Service/ServiceInfo/SecondNDS".equalsIgnoreCase(strArr[i])){
						strResult += IptvApp.mNetManager.getDns2();
					}else if("Resolution".equalsIgnoreCase(strArr[i])){
						strResult += getContentProviderObject("Resolution",IptvApp.app);
					}else if("Service/ServiceInfo/AudioMode".equalsIgnoreCase(strArr[i])){
						strResult += getContentProviderObject("Service/ServiceInfo/AudioMode",IptvApp.app);
					}else if("Service/ServiceInfo/VideoMode".equalsIgnoreCase(strArr[i])){
						strResult += getContentProviderObject("Service/ServiceInfo/VideoMode",IptvApp.app);
					}else if("Service/ServiceInfo/TransmissionMode".equalsIgnoreCase(strArr[i])){
						String tempValue = AmtDataManager.getString(IPTVData.Config_TRANSPORT_MODE,"");
						String value = "";
						if (tempValue.equalsIgnoreCase("0")) {
							value = String.valueOf("key:0 isselect:1 value:MP2T/TCP,key:1 isselect:0 value:MP2T/UDP,key:2 isselect:0 value:MP2T/RTP/TCP,key:3 isselect:0 value:MP2T/RTP/UDP");
						} else if (tempValue.equalsIgnoreCase("1")) {
							value = String.valueOf("key:0 isselect:0 value:MP2T/TCP,key:1 isselect:1 value:MP2T/UDP,key:2 isselect:0 value:MP2T/RTP/TCP,key:3 isselect:0 value:MP2T/RTP/UDP");
						} else if (tempValue.equalsIgnoreCase("2")) {
							value = String.valueOf("key:0 isselect:0 value:MP2T/TCP,key:1 isselect:0 value:MP2T/UDP,key:2 isselect:1 value:MP2T/RTP/TCP,key:3 isselect:0 value:MP2T/RTP/UDP");
						} else if (tempValue.equalsIgnoreCase("3")) {
							value = String.valueOf("key:0 isselect:0 value:MP2T/TCP,key:1 isselect:0 value:MP2T/UDP,key:2 isselect:0 value:MP2T/RTP/TCP,key:3 isselect:1 value:MP2T/RTP/UDP");
						} else {
							value = String.valueOf("key:0 isselect:1 value:MP2T/TCP,key:1 isselect:0 value:MP2T/UDP,key:2 isselect:0 value:MP2T/RTP/TCP,key:3 isselect:0 value:MP2T/RTP/UDP");
						}
						strResult += value;
					}else if ("Language".equalsIgnoreCase(strArr[i])){
						String defaultLanguage = "";
						String value = "";
						IActivityManager iam = ActivityManagerNative.getDefault();
						Configuration config;
						try {
							config = iam.getConfiguration();
							defaultLanguage = config.locale.getLanguage();
						} catch (RemoteException e) {
							e.printStackTrace();
						}
						if (defaultLanguage.equalsIgnoreCase("zh")) {
							value = String.valueOf("key:0 isselect:1 value:中文,key:1 isselect:0 value:English");
						} else {
							value = String.valueOf("key:0 isselect:0 value:中文,key:1 isselect:1 value:English");
						}
						strResult += value;
					}
					//获取网管心跳周期。这个节点是老节点，看名字容易有歧义，所以都改了节点名称了，针对远程工具兼容一下。
					else if("Service/ServiceInfo/config_HeartBeat".equals(strArr[i])){
						strResult += AmtDataManager.getString(IPTVData.Config_ITMS_HeartBeat_Interval,"");
					}else if("Service/ServiceInfo/IptvTimeZoneId".equals(strArr[i])){
						strResult += TimeZone.getDefault().getID();
					}else{
						strResult += Config.getValue(strArr[i]);
					}
					strResult += "\r\n";
				}
				ALOG.info(TAG,"getValue > strResult : "+strResult);
				return strResult;
			}
			return null;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	// 封装类 IPTV获取设置参数列表 add by wss 20160721
	static private String getContentProviderObject(String selection, Context con) {
		Uri uri = Uri.parse("content://debugToolConfig/config");
		String name = "";
		try {
			Cursor cursor = con.getContentResolver().query(uri, null,
					selection, null, null);
			if(cursor != null){
				ALOG.error("RemoteDebug", "count=" + cursor.getCount());
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					name = cursor.getString(1);
					cursor.moveToNext();
				}
				cursor.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		ALOG.info(TAG,"getContentProviderObject > key : "+selection+", value : "+name);
		return name;
	}

	private static String getIp() {
		String strIP = IptvApp.mNetManager.getIp();
		return strIP;
	}

	public static String getSystemPaht() {
		ALOG.debug(TAG,"--getSystemPaht--");
		String srt = "/cache/recovery/last_update.zip";
		File savaFile = new File(srt);
		savaFile.mkdirs();

		if (!savaFile.exists())
			savaFile.mkdir();
		if (!savaFile.canWrite() || !savaFile.canRead()) {
			ALOG.debug(TAG,"savaFile:---" + savaFile + ",canWrite:" + savaFile.canWrite() + ",canRead:" + savaFile.canRead());
			srt= USBHelper.usbPath+"/last_update.zip";
		}
		return srt;
	}

	static private native void SendLog(int level, int type, String strMsg);

	static private native void Start(String strUser, String strPlat, boolean isRemote);

	static private native void Stop();

	static private native void ConnectManager(String strIP);
}
