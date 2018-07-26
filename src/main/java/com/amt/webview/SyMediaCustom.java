/*
 * Copyright (C) 2011-2012 IPTV Android中间件/终端软件
 *
 * 深圳市速影科技有限公司
 *
 * http://www.softstb.com/
 *
 */

package com.amt.webview;

import com.amt.app.IptvApp;
import com.amt.config.Config;
import com.amt.utils.ALOG;

/**
 * 2017年重构IPTV改造。新版SyMediaCustom。
 */
public class SyMediaCustom {

	//=========================start 浏览器注册接口常量===================
	/**设置alpha值。*/
	public static final String WEBSET_ALPHA = "set_webAlpha";
	/**设置是否自动拉伸。*/
	public static final String WEBSET_AUTOSCALE = "set_autoScale";
	/**设置透明。 */
	public static final String WEBSET_CLEARMODE = "set_clearMode";
	/**获取http信息。*/
	public static final String WEBSET_GET_HTTPINFO = "set_getHttpInfo";
	/**设置EPG保存目录。*/
	public static final String WEBSET_SAVE_EPGPATH = "set_saveEpgPath";
	/**设置浏览器的navigator.platform对象值。*/
	public static final String WEBSET_NAV_PLATFORM = "set_navigatorPlatform";
	/**设置浏览器的navigator.appName对象值。*/
	public static final String WEBSET_NAV_APPNAME = "set_navigatorAppName";
	/**设置浏览器的navigator.appVersion对象值。*/
	public static final String WEBSET_NAV_APPVERSION = "set_navigatorAppVersion";
	/**设置浏览器的navigator.cpuClass对象值。*/
	public static final String WEBSET_NAV_CPUCLASS = "set_navigatorCpuClass";
	/**保存执行js报错的信息的文件路径。*/
	public static final String WEBSET_JSERROR_PATH = "set_saveJsErrorPath";
	/**设置app安装路径。*/
	public static final String WEBSET_APP_PATH = "set_appPath";
	/**设置项目区域如：'SC',‘HF’,'GD'。*/
	public static final String WEBSET_PROJECT_PLAT = "set_projectPlat";
	/**设置webcore自行读取fix文件。*/
	public static final String WEBSET_WEBFIX = "set_webFix";
	/**给浏览器增加自定义的默认css样式。*/
	public static final String WEBSET_DEFAULTCSS = "set_defaultCss";
	/**设置cpu类型，用以区分905或hisi等芯片。*/
	public static final String WEBSET_CPU_TYPE = "set_cupType";

	/**浏览器LOG开关控制，为0时关闭所有模块打印，不为0时打开日志，且可设置级别*/
	public static final String WEBSET_LOG_CHROMIUM = "set_chromiumLevel";
	/**浏览器net模块的等级打印(需要ChromiumDebugLevel同时为非0)，为0时关闭该模块级别打印#*/
	public static final String WEBSET_LOG_NET = "set_netLevel";
	/**浏览器WebCore模块的等级打印(需要ChromiumDebugLevel同时为非0)，为0时关闭该模块级别打印*/
	public static final String WEBSET_LOG_WEBCORE = "set_webcoreLevel";
	/**浏览器v8引擎的LOG开关(需要ChromiumDebugLevel同时为非0),0关闭，1开启*/
	public static final String WEBSET_LOG_V8 = "set_debugV8";
	/**设置IPTV的LOG开关。webcore的网页请求、按键下发打印，需要与IPTV的LOG开关保持一致*/
	public static final String WEBSET_LOG_IPTV = "set_iptvJavaDebug";
	/**更新输入法输入的文本内容*/
	public static final String WEBSET_INPUT = "set_inputElementContent";
	//=========================end 浏览器注册接口常量===================

	//=======================start 浏览器回调接口常量===================
	/**浏览器消息回调类型 ：按键消息*/
	public static final int TYPE_KEY_EVENT = 1000;
	/**浏览器消息回调类型 ：系统消息*/
	public static final int TYPE_SYSTEM_EVENT = 2000;
	/**浏览器消息回调类型 ：页面消息*/
	public static final int TYPE_LOAD_EVENT = 3000;
	/**浏览器消息:页面开始请求（精确url，非最顶层url）*/
	public static final String WEBMSG_PAGE_START = "systemevent_pagestart";
	/**浏览器消息:页面加载完成（精确url，非最顶层url）*/
	public static final String WEBMSG_PAGE_FINISH = "systemevent_pagefinish";
	public static final String WEBMSG_ONLOAD = "loadevent_onload";
	public static final String WEBMSG_ONUNLOAD = "loadevent_onunload";

	//=======================end 浏览器回调接口常量===================

	int mNativeClass;

	private IPTVWebCustomClient mCustomClient;
	private IPTVWebView mWebView;

	public SyMediaCustom(IPTVWebView webview) {
		mWebView = webview;
		nativeInit();
		// cpuClass返回浏览器硬件系统的 CPU等级。
		nativeSetCmdCustom(WEBSET_NAV_CPUCLASS, android.os.Build.CPU_ABI);
		// 把apk路径传给so,so需要拿assets中的文件
		setAppPath(IptvApp.app.getApplicationInfo().sourceDir);
		//设置webcore自己去读fix文件
		nativeSetCmdCustom(WEBSET_WEBFIX,"assets/ITV/"+ Config.PLATTYPE+".fix");
		//设置使用新的播放器接口
		nativeSetCmdCustom("set_newPlayerInterface","1");
	}
	
	public void setSyWebCustomClient(IPTVWebCustomClient customClient){
		mCustomClient = customClient;
	}
	
	public int getNativeClass(){
		return mNativeClass;
	}

	/**
	 * 设置EPG保存的目录。
	 * @param epgPath
     */
	public void setAutoSaveEPG(String epgPath){
		ALOG.info("SyMediaCustom > setAutoSaveEPG : "+epgPath);
		nativeSetCmdCustom(WEBSET_SAVE_EPGPATH,epgPath);
	}

	/**
	 * 设置IPTV的安装目录
	 * @param appPath
     */
	public void setAppPath(String appPath){
		nativeSetCmdCustom(WEBSET_APP_PATH,appPath);
	}

	/**
	 * 同步IPTV的LOG 开关给webcore。
	 * @param flag
     */
	public void setIPTVDebug(boolean flag){
		ALOG.info("SyMediaCustom > setIPTVDebug : "+flag);
		nativeSetCmdCustom(WEBSET_LOG_IPTV,flag ? "1" : "0");
	}

	public void setWebsetAlpha(int alpha) {
		nativeSetCmdCustom(WEBSET_ALPHA, String.valueOf(alpha));
	}

	/**
	 * 设置Webcore的LOG打印级别。
	 * @param logLevel 0：关闭。 大于0:打开
     */
	public void setWebcoreDebug(int logLevel){
		ALOG.info("SyMediaCustom > setWebcoreDebug : "+logLevel);
		nativeSetCmdCustom(WEBSET_LOG_WEBCORE,String.valueOf(logLevel));
	}
	/**
	 * 设置V8的LOG打印级别。
	 * @param logLevel 0：关闭。 大于0:打开
	 */
	public void setV8Debug(int logLevel){
		ALOG.info("SyMediaCustom > setV8Debug : "+logLevel);
		nativeSetCmdCustom(WEBSET_LOG_V8,String.valueOf(logLevel));
	}

	/**
	 * 设置#键的键值，webcore需要监听是否在输入框内按了#键，以判断是否需要呼出输入法
	 * @param keyCode
     */
	public void setNumSignKey(int keyCode){
		nativeSetCmdCustom("set_numSignKeyCode",String.valueOf(keyCode));
	}

	/**
	 * 设置项目市场名称。如:四川电信：SC  山东联通：SDLT
	 * @param projectPlat
     */
	public void setProjectPlat(String projectPlat){
		nativeSetCmdCustom(WEBSET_PROJECT_PLAT,projectPlat);
	}

	/**
	 * 设置视频区域。此接口会告诉浏览器，当前页面的小视频区域在哪个位置。浏览器会在排版时智能判断是否需要挖洞，以透传视频画面。
	 * @param x
	 * @param y
	 * @param w
     * @param h
     */
	public void setVideoPosition(int x, int y, int w, int h) {
		nativeSetVideoPosition(x, y, w, h);
	}


	/**
	 * 通知webcore当前的页面大小，以确保有element的位置计算的正确性，不加以限定会根据webview的大小来计算
	 * @param w
	 * @param h
     */
	public void SetEPGSize(int w, int h) {
		nativeSetEPGSize(w, h);
	}


	/***********************************   start  JNI函数   ***************************************/

	/**本类构造方法里调用，用于向webcore底层注册本类的内存指针信息*/
	private native void nativeInit();
	private native void nativeSetEPGSize(int w, int h);
	private native void nativeSetVideoPosition(int x, int y, int w, int h);
	/**向webcore设置参数信息 */
	private native void nativeSetCmdCustom(String key,String value);
	/***********************************   end  JNI函数   ***************************************/
	/********************************   start  webcore回调函数   ********************************/
	/**
	 * webcore回调函数。推数据。
	 * @param flag
	 * @param arg1
     * @param arg2
     */
	private void pushData(int flag, String arg1, String arg2) {
		if(mCustomClient!=null){
			mCustomClient.pushData(flag,arg1,arg2);
		}
	}

	/**
	 * webcore回调函数。弹出软件盘
	 * @param x 输入框x坐标
	 * @param y 输入框Y坐标
	 * @param cursorStart 选中光标起始位置
	 * @param cursorEnd 选中光标结束位置
	 * @param value 输入框原本的文本信息
	 * @param url 输入框所在页面。用于判断是否需要弹软键盘。
	 */
	private void ShowInputMethod(int x,int y,int cursorStart,int cursorEnd ,String value,String url) {
		ALOG.info(mWebView.DEBUG_TAG,"ShowInputMethod > y:"+y+", cursorStart :"+cursorStart+", cursorEnd : "+cursorEnd+", value : "+value);
		if(mCustomClient!=null){
			mCustomClient.shouldShowInputMethod(x,y,cursorStart,cursorEnd,value,url);
		}
	}

	/**
	 * webcore回调函数。运行JVM游戏APK。
	 * @param w
	 * @param h
	 * @param strJad
	 * @param strJar
     * @param strParam
     */
	private void RunGame(int w, int h, String strJad, String strJar, String strParam) {
		if(mCustomClient!=null){
			mCustomClient.RunGame(w,h,strJad,strJar,strParam);
		}
	}

	/**
	 * webcore回调函数。通知页面尺寸变化。接口原型不能随意更改！！！
	 * @param w
	 * @param h
	 */
	public void onPageViewSizeChange(int w, int h) {
		if(mCustomClient!=null){
			mCustomClient.onPageViewSizeChange(mWebView, w, h);
		}
	}
	/**
	 * webcore回调函数。底层libsywebviewchromium.so直接回调此函数通知。勿删！！！
	 * http://192.168.2.90/broswerMessage.html 临时API查询文档
	 * @param type 消息类型，
	 * @param message 消息码
	 * @param url 当前页面url
	 * @param jsonStr 浏览器消息json字符串。每种不同类型的消息，都封装了各种不同的json。
	 */
	private void onBroswerMessage(int type, String message,String url ,String jsonStr) {
//		ALOG.info(mWebView.DEBUG_TAG, "onBroswerMessage > type:"+type+", message:"+message+", url:"+url+", jsonStr:"+jsonStr);
		if(mCustomClient == null){
			ALOG.debug(mWebView.DEBUG_TAG, "CustomClient is null. do nothing");
			return ;
		}
		switch (type) {
		case TYPE_SYSTEM_EVENT:
			if(WEBMSG_PAGE_START.equals(message)){
				//页面开始请求，就是离开上一个页面，所以通知离开页面的函数
				mCustomClient.onPageLoadStart(mWebView, url,jsonStr);
				return;
			}else if(WEBMSG_PAGE_FINISH.equals(message)){
				//页面请求完成，就是新的页面正在或已经渲染完成，所以回调页面显示的函数。
				mCustomClient.onPageLoadFinished(mWebView, url,jsonStr);
				return;
			}
			break;
		case TYPE_LOAD_EVENT:
			if(WEBMSG_ONLOAD.equals(message)){
				mCustomClient.onLoad(mWebView, url,jsonStr);
				return;
			}else if(WEBMSG_ONUNLOAD.equals(message)){
				mCustomClient.onUnLoad(mWebView, url,jsonStr);
				return;
			}
			break;
		}
		mCustomClient.onBroswerMessage(mWebView,type,message,url,jsonStr);
	}
	/********************************   end  webcore回调函数   *********************************/
}
