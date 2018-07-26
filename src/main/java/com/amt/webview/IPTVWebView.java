package com.amt.webview;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.SyMedia.webkit.SyWebView;
import com.amt.utils.ALOG;

/**
 * 继承SyWebView，内部存放一个SyMediaCustom对象:custom，在构造方法里初始化custom实例。SyWebView和SyMediaCustom保持一对一关系。
 * @author DonWZ
 *
 * 2016-11-15
 */
public class IPTVWebView extends SyWebView{
	
	/**SyMediaCustom对象，在AmtWebView构造方法创建*/
	private SyMediaCustom custom;
	public String DEBUG_TAG ="";
	private String webTag = "";

	//访问权限设置为protected，外部创建AmtWebView只能通过WebViewManager.creatWebView()来创建
	protected IPTVWebView(Context arg0,String tag) {
		super(arg0);
		if(!TextUtils.isEmpty(tag)){
			webTag = tag;
			DEBUG_TAG = webTag;
		}
		custom = new SyMediaCustom(this);
		SetProperty("WebCustom", "" + custom.getNativeClass());
	}

	public SyMediaCustom getCustom(){
		return custom;
	}

	public String getWebTag(){
		return webTag;
	};
	
	@Override
	public void loadUrl(String url) {
		super.loadUrl(url);
//		if (url.toLowerCase().startsWith("http") || url.toLowerCase().startsWith("file")){
			ALOG.info(DEBUG_TAG, "loadUrl : "+url);
//		}
	}

	@Override
	public void stopLoading() {
		super.stopLoading();
		ALOG.info(DEBUG_TAG,"stopLoading...");
	}

	public void setWebCustomClient(IPTVWebCustomClient customClient){
		if(customClient!=null && custom!=null){
			custom.setSyWebCustomClient(customClient);
		}
	}
	
	/**
	 * 设置SyMediaCustom对象，使底层libsywebviewchromium.so将webview和custom绑定
	 * @param arg0 固定值：WebCustom
	 * @param arg1 ： SyMediaCustom.getNativeClass()的值
	 */
	@Override
	public boolean SetProperty(String arg0, String arg1) {
		ALOG.info(DEBUG_TAG, "SetProperty > "+arg0+" :　"+arg1);
		return super.SetProperty(arg0, arg1);
	}
	
	@Override
	public void onResume() {
		ALOG.info(DEBUG_TAG, "onResume");
		super.onResume();
	}
	
	@Override
	public void resumeTimers() {
		ALOG.info(DEBUG_TAG, "resumeTimers");
		super.resumeTimers();
	}
	
	@Override
	public void onPause() {
		ALOG.info(DEBUG_TAG, "onPause");
		super.onPause();
	}
	@Override
	public void pauseTimers() {
		ALOG.info(DEBUG_TAG, "pauseTimers");
		super.pauseTimers();
	}
	
	@Override
	public void setVisibility(int visibility) {
		switch (visibility) {
		case View.INVISIBLE:
			ALOG.info(DEBUG_TAG, "setVisibility > INVISIBLE");
			break;
		case View.VISIBLE:
			ALOG.info(DEBUG_TAG, "setVisibility > VISIBLE");
			break;
		case View.GONE:
			ALOG.info(DEBUG_TAG, "setVisibility > GONE");
			break;
		}

		super.setVisibility(visibility);
	}
	
	@Override
	public void setBackgroundColor(int arg0) {
		ALOG.info(DEBUG_TAG, "setBackgroundColor > "+arg0);
		super.setBackgroundColor(arg0);
	}
	
}
