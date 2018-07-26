package com.amt.utils;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.amt.utils.heartbeat.EPGHeartBeat;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
/**
 * Created by DJF on 2017/6/8.
 * IPTV常驻型服务
 */
public class IptvResidentService extends Service {

	private static String TAG="IptvResidentService";
	private EPGHeartBeat epgHeartBeat =new EPGHeartBeat();
	private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
	public IptvResidentService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		ALOG.debug(TAG,"onBind");
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		ALOG.debug(TAG,"onCreate");

		super.onCreate();
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		ALOG.debug(TAG,"onStartCommand");
		epgHeartBeat.runnable.run();
		return START_NOT_STICKY;
	}



	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		ALOG.debug(TAG,"onDestroy");
		//停止心跳线程
		epgHeartBeat.stopHeartBeat();
		super.onDestroy();
	}


	private static String IptvResidentService_name="com.amt.utils.IptvResidentService";
	/**
	 * 开启常驻型服务
	 * @param mcontenxt
     */
	public static void startSerivce(Context mcontenxt){
		ALOG.debug(TAG,"startSerivce IptvResidentService");
		//如果在已经运行先停止
		if (isServiceRunning(mcontenxt,IptvResidentService_name)){
			stopService(mcontenxt);
		}
		Intent intent  =new Intent(mcontenxt,IptvResidentService.class);
		mcontenxt.startService(intent);
	}

	/**
	 * 停止常驻型服务
	 * @param mcontenxt
     */
	public static void stopService(Context mcontenxt){
		ALOG.debug(TAG,"stopService IptvResidentService");
		Intent intent  =new Intent(mcontenxt,IptvResidentService.class);
		mcontenxt.stopService(intent);
	}

	public static boolean isServiceRunning(Context mContext,String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager)
				mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList
				= activityManager.getRunningServices(30);
		if (!(serviceList.size()>0)) {
			return false;
		}
		for (int i=0; i<serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}

}
