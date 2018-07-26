package com.amt.webview;

import android.content.Context;

import com.SyMedia.webkit.SyWebResourceResponse;
import com.SyMedia.webkit.SyWebView;
import com.SyMedia.webkit.SyWebViewClient;
import com.amt.app.IptvApp;
import com.amt.utils.ALOG;


public class IPTVWebViewClient extends SyWebViewClient{
	
	private Context mContext = null;
	
	public IPTVWebViewClient(Context context){
		mContext = context;
	}

	@Override
	public SyWebResourceResponse shouldInterceptRequest(SyWebView webview, String url) {
		if(url.contains("favicon.ico")){
			try {
				SyWebResourceResponse response = new SyWebResourceResponse("image/png", "UTF-8", null);
				return response;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return super.shouldInterceptRequest(webview, url);
	}



	@Override
	public boolean shouldOverrideUrlLoading(SyWebView webview, String url) {
//		ALOG.info(((IPTVWebView)webview).DEBUG_TAG, "shouldOverrideUrlLoading > url:"+url);
		//不要使用loadurl，否则导致浏览器内部window状态错误
//		webview.loadUrl(url);
		super.shouldOverrideUrlLoading(webview,url);
		return false;
	}

	/**
	 * Notify the host application that a page has finished loading. 
	 * This method is called only for main frame. 
	 * When onPageFinished() is called, the rendering picture may not be updated yet. 
	 * To get the notification for the new Picture, 
	 * use WebView.PictureListener.onNewPicture(android.webkit.WebView, android.graphics.Picture).
	 * <br> 此方法只会在main frame加载完成时触发。
	 */
	@Override
	public void onPageFinished(SyWebView view, String url) {
		super.onPageFinished(view, url);

		ALOG.info(((IPTVWebView)view).DEBUG_TAG, "onPageFinished > url:"+url);
	}

	/**
	 * Notify the host application that a page has started loading. 
	 * This method is called once for each main frame load so a page with iframes 
	 * or framesets will call onPageStarted one time for the main frame. 
	 * This also means that onPageStarted will not be called when the contents 
	 * of an embedded frame changes, i.e. clicking a link whose target is an iframe.
	 * <br>此方法只在一个main frame加载的时候被触发，对于一个包含多个iframe和frameset的main frame来说，
	 * 它的生命周期内只会被触发一次，其内部的框架发生改变时将不会触发此方法。
	 */
	@Override
	public void onPageStarted(SyWebView webview, String url, android.graphics.Bitmap favicon) {
		ALOG.info(((IPTVWebView)webview).DEBUG_TAG, "onPageStarted > url:"+url);
		//若页面使用window.open(url,name)指定了一个新webview的名字来创建新窗口的话，再次执行此代码，
		//不会触发 WebChromeClient.onCreateWindow方法。所以需要在这里检查一下，
		//触发onPageStarted的webview是不是当前top可视的webview。若不是，则将此webview重新压入栈顶，并设置为可视的。
		IPTVWebView currentWeb = WebViewManager.getManager().getCurrentWebview();
		if(!currentWeb.getWebTag().equals(((IPTVWebView)webview).getWebTag())){
			ALOG.info(((IPTVWebView)webview).DEBUG_TAG, "bring web top ,webtag : "+((IPTVWebView)webview).getWebTag());
			WebViewManager.getManager().bringTop((IPTVWebView) webview);
		}
	}

	@Override
	public void onReceivedError(SyWebView view, int errorCode, String description, String failingUrl) {
		ALOG.info(((IPTVWebView)view).DEBUG_TAG, "onReceivedError > url:"+failingUrl+", errorCode:"+errorCode+", description:"+description);
		if(IptvApp.authManager.isAuth){
			//目前webcore有自动加载本地错误页面，如果他们屏蔽了自动加载，以下代码才打开。
//			ALOG.info("show local error page!! ");
//			WebViewManager.getManager().getCurrentWebview().loadUrl("file:///android_asset/weberror/pageerr.jsp?code=10071");
		}else{
			IptvApp.authManager.authExternalInterface.onFail("0025","","");
		}
	}
}
