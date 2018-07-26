package com.amt.utils;

import android.text.TextUtils;
import android.util.Log;


/**
 * 统一log工具。本程序所有log输出都需要通过此类完成，以方便统一控制LOG开关
 * Created by DonWZ on 2016-10-20
 */
public class ALOG {
	/**LOG开关*/
	public static boolean DEBUG = true;
	/**私密打印开关，用于业务数据和一些加密数据打印。在正式版本中要关闭此打印*/
	public static boolean SECRET_DEBUG = false;

	public static final String TAG = "AMTIPTV";

	public static void info(String message) {
		info("", message, null);
	}

	public static void info(String tag, String message) {
		info(tag,message, null);
	}

	public static void info(String message, Throwable thr) {
		info("", message, thr);
	}

	public static void info(String tag, String message, Throwable thr) {
		if (DEBUG) {
			if(!TextUtils.isEmpty(tag)){
				message = tag+" > "+message;
			}
			Log.i(TAG, /*time()+" "+*/message, thr);
		}
	}

	public static void debug(String message) {
		debug("", message, null);
	}

	public static void debug(String tag, String message) {
		debug(tag, message, null);
	}

	public static void debug(String message, Throwable thr) {
		debug("", message, thr);
	}

	public static void debug(String tag, String message, Throwable thr) {
		if (DEBUG) {
			if(!TextUtils.isEmpty(tag)){
				message = tag+" > "+message;
			}
			Log.d(TAG, /*time()+" "+*/message, thr);
		}
	}

	public static void error(Throwable thr) {
		error("", "", thr);
	}

	public static void error(String message) {
		error("", message, null);
	}
	public static void error(String tag, String message) {
		error(tag, message, null);
	}
	public static void error(String tag,Throwable thr){
		error(tag,"",thr);
	}
	public static void error(String tag, String message, Throwable thr) {
		if (DEBUG) {
			if(!TextUtils.isEmpty(tag)){
				message = tag+" > "+message;
			}
			Log.e(TAG, /*time()+" "+*/message, thr);
		}
	}

	public static void warn(String tag, String message) {
		warn(tag, message, null);
	}

	public static void warn(String tag, Throwable thr) {
		warn(tag, "", thr);
	}

	public static void warn(String tag, String message, Throwable thr) {
		if (DEBUG) {
			if(!TextUtils.isEmpty(tag)){
				message = tag+" > "+message;
			}
			Log.w(TAG, /*time()+" "+*/message, thr);
		}
	}

	public static void secretLog(String message){
		secretLog("",message);
	}

	/**私密打印，用于打印业务数据信息和加密信息*/
	public static void secretLog(String tag,String message){
		if(SECRET_DEBUG){
			if(!TextUtils.isEmpty(tag)){
				message = tag + " > " +message;
			}
			Log.i(TAG,message);
		}
	}
//	private static String time(){
//		String timeStr = "";
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//		timeStr = sdf.format(new Date(System.currentTimeMillis()));
//		return timeStr;
//	}
}
