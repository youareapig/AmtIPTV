package com.amt.app;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.SyMedia.SyDebug.RemoteBroadCastReceiver;
import com.amt.amtdata.AmtDataManager;
import com.amt.amtdata.IPTVData;
import com.amt.auth.AuthData;
import com.amt.auth.AuthManager;
import com.amt.config.Config;
import com.amt.net.NetConnectManager;
import com.amt.net.NetWorkProxy;
import com.amt.tr069.TR069Manager;
import com.amt.utils.ALOG;
import com.amt.utils.CUBootLogHelper;
import com.amt.utils.FileHelper;
import com.amt.utils.IptvResidentService;
import com.amt.utils.SystemPropHelper;
import com.amt.utils.USBHelper;
import com.amt.utils.Utils;
import com.amt.utils.keymap.KeyHelper;
import com.amt.utils.powermanager.StandbyBroadcastReceived;

import java.io.File;

//  ┏┓　　　┏┓
//┏┛┻━━━┛┻┓
//┃　　　　　　　┃
//┃　　　━　　　┃
//┃　┳┛　┗┳　┃
//┃　　　　　　　┃
//┃　　　┻　　　┃
//┃　　　　　　　┃
//┗━┓　　　┏━┛
//   ┃　　　┃   神兽保佑
//   ┃　　　┃   代码无BUG！
//   ┃　　　┗━━━┓
//   ┃　　　　　　　┣┓
//   ┃　　　　　　　┏┛
//   ┗┓┓┏━┳┓┏┛
//     ┃┫┫　┃┫┫
//     ┗┻┛　┗┻┛


public class IptvApp extends Application implements Thread.UncaughtExceptionHandler {
	/**提供全局Context变量*/
	public static IptvApp app = null;
	public static final String TAG = "IptvApp";
	/**网络模块管理器对象。获取网络状态通过这里获取。*/
	public static NetConnectManager mNetManager;
	public static AuthManager authManager;
	public static TR069Manager mTR069;
	/**网络监听业务实现*/
	public NetWorkProxy mNetWorkProxy;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(ALOG.TAG,"=================== IPTV Application onCreate ==================");
		Thread.setDefaultUncaughtExceptionHandler(this);
		app = this;
		System.loadLibrary("amtUtils");//加载libamtUtils.so文件。
		//初始化数据模块
		AmtDataManager.init(this);
		mNetManager = NetConnectManager.getManager(this);
		mNetManager.setCheckWifi(true);
		authManager=AuthManager.init(this);
		mTR069 = TR069Manager.init(this);
		System.loadLibrary("CTC_MediaControl");// 加载libIPTVPlayer.so文件
		new AsyncTask<Object,Object,Object>(){
			@Override
			protected Object doInBackground(Object... params) {
				//TODO 将启动过程中的耗时操作放入这里
				Config.init();
				AuthData.init();
				KeyHelper.initKeyXml(app);
				deleteWebviewCacheFiles();
				//读取Usb路径
				USBHelper.updateUsbPath();
				RemoteBroadCastReceiver.initRemoteReceiver(app);
				return null;
			}
		}.execute("");
		mNetWorkProxy =new NetWorkProxy(app);
		//注册待机锁广播
		StandbyBroadcastReceived.StandbyBroadcastReceived(this);
		//开机信息收集开关。一般联通项目可能会用到。
		if("true".equals(SystemPropHelper.getProp("persist.sys.bootlog.star", "false"))){
			CUBootLogHelper.notifyDeviceLog(true,USBHelper.usbPath+CUBootLogHelper.PATH_BOOTINFO);
		}
	}

	/**
	 * 删除webview缓存文件
	 */
	private void deleteWebviewCacheFiles(){
		try {
			String path = getFilesDir().getParent();
			File caches = new File(path + "/app_webview");
			if (caches.exists()) {
				FileHelper.deleteDir(caches);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		try {
			if (ex != null) {
				Log.e(ALOG.TAG, "AppError:", ex);
				ex.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			exitIptv();
		}
	}

	public static void exitIptv(){
		authManager.stopService();
		//停止心跳服务 djf
		IptvResidentService.stopService(IptvApp.app);
		RemoteBroadCastReceiver.releaseRemoteReceiver(app);
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	/**
	 * 获取零配置状态。
	 * @return true:零配置完成   false：未做零配置
	 */
	public static boolean checkZeroStatus(){
		return "1".equals(AmtDataManager.getString(IPTVData.IPTV_ZEROSETTING_STATUS,""));
	}
	
}
