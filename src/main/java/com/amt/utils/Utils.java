package com.amt.utils;

import android.app.ActivityManager;
import android.os.Bundle;
import android.util.Log;

import com.amt.app.IptvApp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * 公共工具类。
 * @author DonWZ
 *
 * 2017-2-28
 */
public class Utils {

	private static final String TAG = "Utils";
	
	/**
	 * 执行root命令。（慎用！！！！！！）
	 * @param cmd 命令
	 */
	public static void execRootCmd(String cmd){
		ALOG.info(TAG, "execRootCmd > "+cmd);
		nvRunSystemCmd(cmd);
	}
	
	/**
	 * NTP同步
	 * @param ntp NTP服务器地址
	 */
	public static void startNtpUpdate(String ntp){
		ALOG.info(TAG,"startNtpUpdate > "+ntp);
		nvStartNtpUpdate(ntp);
	}
	
	/**
	 * 抓网络包
	 * @param cmd 抓包后的保存路径+文件名
	 * @param port 端口
	 * @param ip IP
	 * @param size 抓包大小，单位为字节
	 * @param time 抓包时间 ，单位是秒。时间到了自动停止。
	 */
	public static void startTcpdump(String cmd, String port, String ip, int size, int time){
		ALOG.info(TAG,String.format("startTcpdump > cmd : %s, port : %s, ip : %s, size : %d,time : %d",cmd,port,ip,size,time));
		nvStartTcpdump(cmd, port, ip, size, time);
	}
	
	/**
	 * 强制停止抓包。
	 */
	public static void stopTcpdump(){
		ALOG.info(TAG,"stopTcpdump!!");
		nvStopTcpdump();
	};
	
	/**
	 * 加密数据。加密算法从原来的libCTC_MediaControl.so 转移到libamtUtils.so
	 * @param data
	 * @return
	 */
	public static String EncyptData(String data){
		return nvEncyptData(data);
	}
	/**
	 * 解密数据。加密算法从原来的libCTC_MediaControl.so 转移到libamtUtils.so
	 * @param data
	 * @return
	 */
	public static String DecyptData(String data){
		return nvDecyptData(data);
	}

	/**
	 * 沿用以前解密ttf和dat、fix等文件的接口。
	 * @param bt
	 * @return
	 */
	public static String NvDecode(byte[] bt){
		return nvDecode(bt);
	}
	/**
	 * 获取数据。扩展接口。可用于获取解密秘钥等私密数据。
	 * @param key
	 * @return
	 */
	public static String GetValue(String key){
		return nvGetString(key);
	}

	/**
	 * 获取当前日期时间(yyyy-MM-dd HH:mm:ss.SSS)
	 * @return
	 */
	public static String getCurDataTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
		return sdf.format(new Date());
	}

	/**
	 * 获取当前日期时间(yyyyMMdd)
	 * @return
	 */
	public static String getCurData() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
		return sdf.format(new Date());
	}



	/**
	 * 得到当前置顶程序的包名
	 *
	 * @return
	 */
	public static String getTopPackageName() {
		try {
			ActivityManager am = (ActivityManager) IptvApp.app.getSystemService(IptvApp.app.ACTIVITY_SERVICE);
			List<ActivityManager.RunningTaskInfo> tasksInfo = am.getRunningTasks(1);
			if (tasksInfo != null && tasksInfo.size() > 0) {
				return tasksInfo.get(0).topActivity.getPackageName();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 打印出bundle内的参数
	 * @param bundle
	 */
	public static String viewBundle(Bundle bundle){
		if(bundle == null){
			return "{ }";
		}
		try {
			Set<String> keySet = bundle.keySet();
			StringBuilder extraStringBuilder = new StringBuilder();
			extraStringBuilder.append("{");
			for(String key : keySet) {
				Object value = bundle.get(key);
				extraStringBuilder.append(" "+key+":"+value+";");
			}
			String extraStr = extraStringBuilder.toString();
			if(extraStr.contains(";")){
				extraStr = extraStr.substring(0,extraStr.lastIndexOf(";"));
			}
			extraStr = extraStr + "}";
			return extraStr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "{ }";
	}

	//=========================== 以下本地方法调用的是libamtUtils.so  ==============================
	private static native void nvRunSystemCmd(String cmd); //慎用！慎用！慎用！
	private static native void nvStartNtpUpdate(String ntp); //ntp同步接口
	private static native void nvStartTcpdump(String cmd, String port, String ip, int size, int time); //抓包
	private static native void nvStopTcpdump(); //停止抓包
	private static native String nvEncyptData(String data);
	private static native String nvDecyptData(String data);
	private static native String nvDecode(byte[] bt);
	private static native String nvGetString(String key);

}
