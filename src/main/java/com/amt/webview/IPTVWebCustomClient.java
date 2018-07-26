package com.amt.webview;


import com.amt.app.IPTVAvtivityView;
import com.amt.app.IptvApp;
import com.amt.auth.AuthManager;
import com.amt.utils.ALOG;
import com.amt.utils.PageBrowseManager;
import com.amt.utils.ResolutionHelper;

import org.json.JSONObject;

/**
 * webcore事件回调。实现基础业务。若要实现特殊业务，重写内部的方法
 * @author DonWZ
 *
 * 2016-11-15
 */
public class IPTVWebCustomClient {
	
	private IPTVAvtivityView iptvView;

	public IPTVWebCustomClient(IPTVAvtivityView iptvView){
		this.iptvView = iptvView;
	}
	/**
	 * libsywebviewchromium.so直接回调此函数通知浏览器事件。
	 * @param web IPTVWebView对象
	 * @param type 消息类型
	 * @param message 消息码 
	 * @param url 当前页面url
	 * @param jsonStr 加载状态，当加载错误时会返回错误码.默认为0
	 */
	public void onBroswerMessage(IPTVWebView web,int type, String message,String url ,String jsonStr){
		ALOG.info(web.DEBUG_TAG, "onBroswerMessage > type:"+type+", message:"+message+", url:"+url+", jsonStr:"+jsonStr);
	}

	/**
	 * 通知页面尺寸变化
	 * @param web 
	 * @param w
	 * @param h
	 */
	public void onPageViewSizeChange(final IPTVWebView web,int w, int h){
		ALOG.info(web.DEBUG_TAG, "onPageViewSizeChange > "+w+" : "+h+", iptvView is null :"+(iptvView==null));
		if(iptvView!=null){
			iptvView.onPageViewSizeChanged(w,h);
		}
	}
	/**
	 * 页面onLoad事件通知
	 * @param web 
	 * @param url 精确url。当前获得焦点的frame或者main frame的url
	 */
	public void onLoad(IPTVWebView web,String url,String jsonArgs){
		if("about:blank".equals(url)){
			return;
		}
		ALOG.info(web.DEBUG_TAG,"onLoad > "+url+", args : "+jsonArgs);
	}
	/**
	 * 页面onUnLoad事件通知
	 * @param web 
	 * @param url 精确url。当前获得焦点的frame或者main frame的url
	 */
	public void onUnLoad(IPTVWebView web,String url,String jsonArgs){
		if("about:blank".equals(url)){
			return;
		}
		try {
			JSONObject json = new JSONObject(jsonArgs);
			if ("0".equals(json.getString("hasonunload")) && "1".equals(json.getString("isfocusframe"))) {
				PageBrowseManager.getManager().doStopVideo();
			}
		} catch (Exception e) {
		}
		ALOG.info(web.DEBUG_TAG,"onUnLoad > "+url+", args : "+jsonArgs);
	}

	public void pushData(int flag, String arg1, String arg2){
		if(iptvView!=null){
		iptvView.onPushData(flag,arg1,arg2);
	}
}

	/**
	 * 弹出软件盘
	 * @param x 输入框x坐标
	 * @param y 输入框Y坐标
	 * @param cursorStart 选中光标起始位置
	 * @param cursorEnd 选中光标结束位置
	 * @param value 输入框原本的文本信息
	 * @param url 输入框所在页面。用于判断是否需要弹软键盘。
	 */
	public void shouldShowInputMethod(int x,int y,int cursorStart,int cursorEnd ,String value,String url){
//		ALOG.info("WebCustomClient > shouldShowInputMethod! y : "+y+", selection : "+selection+", message : "+message+", url : "+url);
		if(iptvView!=null){
			y = y > ResolutionHelper.epgHeight ? ResolutionHelper.epgHeight : y;
			iptvView.showInputMethod(value,cursorStart,cursorEnd,y);
		}
	}

	/**
	 * 运行游戏APK
	 * @param w
	 * @param h
	 * @param strJad
	 * @param strJar
	 * @param strParam
	 */
	public void RunGame(int w, int h, String strJad, String strJar, String strParam){
		if(iptvView!=null){
			iptvView.runGame(w,h,strJad,strJar,strParam);
		}
	}
	
	public void onPageLoadStart(IPTVWebView web,String url,String jsonArgs){
		ALOG.info(web.DEBUG_TAG, "onPageLoadStart > "+url+", args : "+jsonArgs);
	}
	
	public void onPageLoadFinished(IPTVWebView web,String url,String jsonArgs){
		ALOG.info(web.DEBUG_TAG, "onPageLoadFinished > "+url+", args : "+jsonArgs);
		try {
			JSONObject json = new JSONObject(jsonArgs);
//			ALOG.info("systemevent_pagefinish--->pendinglocation: " + json.getString("pendinglocation")
//					+ ", isAuth: " + AuthManager.isAuth);
			if ("0".equals(json.getString("pendinglocation")) /*&& !AuthManager.isAuth*/) {
				ALOG.info("systemevent_pagefinish--->need close AuthPic!!!");
				ALOG.info("Auth authManager.onPageLoadFinished()");
				IptvApp.authManager.hiddenAuthUI();
			}
		} catch (Exception e) {
		}
	}
}
