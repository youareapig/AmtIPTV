package com.amt.webview;

import android.content.Context;
import android.os.Message;
import android.view.View;

import com.SyMedia.webkit.SyConsoleMessage;
import com.SyMedia.webkit.SyJsPromptResult;
import com.SyMedia.webkit.SyJsResult;
import com.SyMedia.webkit.SyWebChromeClient;
import com.SyMedia.webkit.SyWebView;
import com.amt.app.IPTVAvtivityView;
import com.amt.utils.ALOG;

public class IPTVWebChromeClient extends SyWebChromeClient {
	
	private Context mContext = null;
	private IPTVAvtivityView activityView;
	private int lastProgress = 0;//记录上次更新的进度，打印出来，方便看LOG

	public IPTVWebChromeClient(Context context){
		mContext = context;
	}
	public IPTVWebChromeClient(Context context, IPTVAvtivityView activityView){
		mContext = context;
		this.activityView = activityView;
	}
	

	@Override
	public void onCloseWindow(SyWebView webview) {
		ALOG.info(((IPTVWebView)webview).DEBUG_TAG, "onCloseWindow tag:" + ((IPTVWebView)webview).DEBUG_TAG );
		WebViewManager.getManager().onCloseWebWindow(webview);
		super.onCloseWindow(webview);
	}

	/**
	 * 
	 * @param isDialog : 是否是小窗口。
	 * @param isUserGesture : 是否是用户操作。
	 * @param resultMsg : 当一个新的WebView被创建时这个值被传递给他，resultMsg.obj是一个WebViewTransport的对象，
	 * 							它被用来传送给新创建的WebView，使用方法：
	 *							WebView.WebViewTransport.setWebView(WebView)
	 * @return boolean :这个方法如果返回true，代表这个主机应用会创建一个新的窗口，否则应该返回fasle。如果你返回了false，但是依然发送resulMsg会导致一个未知的结果。
	 */
	@Override
	public boolean onCreateWindow(SyWebView webview, boolean isDialog, boolean isUserGesture, Message resultMsg) {
		ALOG.info(((IPTVWebView)webview).DEBUG_TAG, "onCreateWindow > isDialog:" + isDialog + ", isUserGesture:" + isUserGesture);
		try {
			SyWebView.WebViewTransport transport = (SyWebView.WebViewTransport) resultMsg.obj;
			IPTVWebView multiWeb = WebViewManager.getManager().onCreateWebWindow(mContext);
			if(multiWeb!=null){
				transport.setWebView(multiWeb);
				resultMsg.sendToTarget();
				return true;
			}else{
				ALOG.info(((IPTVWebView)webview).DEBUG_TAG, "create new webview failed!");
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
//		return super.onCreateWindow(webview, isDialog, isUserGesture, resultMsg);
	}
	

	@Override
	public boolean onJsAlert(SyWebView view, String url, String message, SyJsResult result) {
		ALOG.info(((IPTVWebView)view).DEBUG_TAG, "Alert > message:" + message+" ,url:" + url);
		// 必须要调用confirm或cancel，否则屏蔽alert对话框后，页面不响应按键。
		result.cancel();
		return true;
	}

	@Override
	public boolean onJsConfirm(SyWebView view, String arg1, String arg2, SyJsResult arg3) {
		ALOG.info(((IPTVWebView)view).DEBUG_TAG, "onJsConfirm > arg1:" + arg1 + ", arg2:" + arg2);
		return super.onJsConfirm(view, arg1, arg2, arg3);
	}

	@Override
	public void onHideCustomView() {
		super.onHideCustomView();
	}

	@Override
	public void onShowCustomView(View view, CustomViewCallback callback) {
		super.onShowCustomView(view, callback);
	}

	@Override
	public boolean onConsoleMessage(SyConsoleMessage consoleMessage) {
		ALOG.error("weberror",consoleMessage.message()+" at "+consoleMessage.sourceId()+":"+consoleMessage.lineNumber());
		return super.onConsoleMessage(consoleMessage);
	}

	public void onProgressChanged(SyWebView view, int newProgress) {
		ALOG.info(((IPTVWebView)view).DEBUG_TAG, "onProgressChanged > newProgress : " + newProgress+", lastProgress : "+lastProgress);
		lastProgress = newProgress >= 100 ? 0 : newProgress;
		if (activityView != null) {
			if (newProgress > 0 && newProgress < 80) {
				activityView.showLoading(true);
			}else if(newProgress >= 80){
				activityView.showLoading(false);
			}
		}
	}

	@Override
	public void onReceivedTitle(SyWebView view, String title) {
		ALOG.info(((IPTVWebView)view).DEBUG_TAG, "onReceivedTitle > title:" + title);
	}

}
