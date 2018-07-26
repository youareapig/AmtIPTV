package com.amt.webview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Picture;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.AbsoluteLayout;
import android.widget.RelativeLayout;

import com.SyMedia.webkit.SyWebView;
import com.amt.app.IPTVAvtivityView;
import com.amt.config.Config;
import com.amt.utils.ALOG;
import com.amt.utils.ResolutionHelper;
import com.amt.utils.USBHelper;
import com.amt.utils.keymap.EPGKey;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

/**
 * webview管理器。操作webview通过此管理器操作。此类不应涉及业务，只负责管理webview的创建、销毁，参数设置，
 * 按键发送，页面加载，显示与隐藏，多个webview管理等。
 * 业务放到webview的回调里，如WebChromeClient、IPTVWebCustomClient、WebViewClient。
 * @author DonWZ
 *
 * 2016-11-11
 */
public class WebViewManager {
	private static final String TAG = "WebView";
	/**同时存放webview的最大实例数量*/
	private static final int MAX_WEBVIEW = 2;
	
	private static WebViewManager instance = new WebViewManager();
	
	private WebViewManager(){}
	
	public static WebViewManager getManager(){
		return instance;
	}
	/**根据默认tag创建的webview对象的计数。不能作为当前同时存在的webview数量。若要获取当前同时存在的webview数量，通过getWebViewSize()获取。*/
	private int defaultCount = 0;
	/**存放webview和webtag的map表。用于根据webtag快速地找到对应的webview对象。*/
	private HashMap<String,IPTVWebView> mWebViewMap = new HashMap<String,IPTVWebView>();
	/**存放webview的堆栈。栈顶的webview即当前使用的webview。按键下发将会下发到处于栈顶的webview对象*/
	private Stack<IPTVWebView> mWebStack = new Stack<IPTVWebView>();
	/**webview控件容器。用于内部管理多个webview层级关系，以及创建webview时自动添加到容器里等功能*/
	private RelativeLayout mWebviewLayout;

	private IPTVAvtivityView iptvView;
	/**记录手动保存EPG的次数，用于保存EPG时建立对应的目录*/
	private static int saveEpgIndex=0;

	/**
	 * 创建一个IPTVWebView对象。
	 * @param context
	 * @param webTag webView对象的标签，可通过这个标签索引对应的webView对象。如果传入一个已存在的tag,将返回已存在的webview对象。
	 * @return
	 */
	public IPTVWebView createWebView(Context context,String webTag){
		ALOG.debug("createWebView--webtag:"+webTag);
		if(TextUtils.isEmpty(webTag) && mWebStack.size() >= MAX_WEBVIEW){
			ALOG.warn(TAG, "Create WebView failed! The stack is full.");
			return null;
		}
		if (!mWebViewMap.isEmpty() && mWebViewMap.get(webTag) != null) {
			ALOG.info(TAG, "createWebView > There is a existing instance with webtag " + webTag + ", return this.");
			return mWebViewMap.get(webTag);
		}
		if(TextUtils.isEmpty(webTag)){
			webTag = TAG + ++defaultCount;
		}	
		ALOG.info(TAG, "createWebView > webTag : " +webTag);
		IPTVWebView webview  = new IPTVWebView(context,webTag);
		mWebViewMap.put(webTag, webview);
		mWebStack.push(webview);
		initWebSettings(webview);
		ALOG.info(TAG, "webview size : "+ mWebViewMap.size());
		return webview;
	}

	/**
	 * 创建一个webview对象，并自动添加到webview容器layout里,让其显示出来。
	 * 调用此接口前，需设置webview容器：{@link #setWebViewContainer(RelativeLayout)}
	 * @param context
	 * @param webTag
     * @return
     */
	public IPTVWebView createWebviewAutoAdd(Context context, String webTag){
		if(mWebviewLayout == null){
			ALOG.info("createWebviewAutoAdd > WebView container is null!");
			return null;
		}
		IPTVWebView webView = createWebView(context,webTag);
		AbsoluteLayout.LayoutParams webParam = new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.MATCH_PARENT, AbsoluteLayout.LayoutParams.MATCH_PARENT, 0, 0);
		mWebviewLayout.addView(webView,webParam);
		bringTop(webView);
		return webView;
	}

	public void setIptvView(IPTVAvtivityView iptvView){
		this.iptvView = iptvView;
	}

	/**
	 * 向WebViewManager添加 webview的容器。用于Manager自动置顶焦点webview，达到自动管理的目的
	 * @param webLayout
     */
	public void setWebViewContainer(RelativeLayout webLayout){
		mWebviewLayout = webLayout;
	}

	/**
	 * 获取当前正在使用、可视的webiew对象
	 * @return
	 */
	public IPTVWebView getCurrentWebview(){
		if (!mWebStack.isEmpty()) {
			return mWebStack.peek();
		} else {
			return null;
		}
	}

	/**
	 * 根据webtag获取对应的webview对象。获取到的webview可能不在栈顶，不可见的。
	 * @param webTag
	 * @return
     */
	public IPTVWebView getWebView(String webTag){
		if(TextUtils.isEmpty(webTag)){
			ALOG.info(TAG, "getWebView > The webTag is null!");
			return null;
		}
		if (!mWebViewMap.isEmpty()) {
			return mWebViewMap.get(webTag);
		}
		ALOG.error(TAG, "getWebView fialed! the HashMap is empty!");
		return null;
	}

	/**
	 * 隐藏所有webview
	 */
	public void hidenWebView() {
		Iterator<String> it = mWebViewMap.keySet().iterator();
		while (it.hasNext()) {
			IPTVWebView webView = mWebViewMap.get(it.next());
			if (webView != null) {
				webView.setVisibility(View.INVISIBLE);
			}
		}
	}

	/**
	 * 显示最顶层的webview
	 */
	public void showWebView(){
		if(!mWebViewMap.isEmpty()){
			getCurrentWebview().setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 获取当前的webview对象的数量。
	 * @return
     */
	public int getWebViewSize(){
		int webSize = mWebStack.size();
		ALOG.info(TAG, "getWebViewSize > "+webSize);
		return webSize;
	}
	
	/**
	 * 发送按键到webview。统一接口，发送按键到页面必须使用此接口！
	 * @param keyEvt
	 * @return
	 */
	public boolean sendKeyToWeb(KeyEvent keyEvt){
		if(keyEvt == null){
			return false;
		}
		IPTVWebView currentWeb = getCurrentWebview();
		if(currentWeb!=null){
			return currentWeb.dispatchKeyEvent(keyEvt);
		}else{
			ALOG.error(TAG, "sendKeyToWeb failed! There is no webview instance!");
		}
		return false;
	}
	
	/**
	 * 初始化webview设置。
	 * @param web
	 */
	private void initWebSettings(IPTVWebView web){
		web.setBackgroundColor(Color.TRANSPARENT);
		web.getSettings().setJavaScriptEnabled(true);
		web.getSettings().setPluginsEnabled(true);
		web.getSettings().setUseWideViewPort(false);
		web.getSettings().setLoadWithOverviewMode(true);
		web.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		web.getSettings().setUserAgentString("webkit;Resolution(PAL,720P,1080P)");
		//默认不支持多窗口。
//		web.getSettings().setSupportMultipleWindows(true);
		web.setHorizontalScrollbarOverlay(false);
		web.setHorizontalFadingEdgeEnabled(false);
		web.setHorizontalScrollBarEnabled(false);
		web.setScrollBarStyle(SyWebView.SCROLLBARS_OUTSIDE_OVERLAY);
		web.setVerticalScrollbarOverlay(false);
		web.setVerticalFadingEdgeEnabled(false);
		web.setVerticalScrollBarEnabled(false);
		// 禁用焦点，在有文本框的时候如果可以设置焦点，页面中会不正常，还有可能会出现输入法
		web.setFocusable(false);
		web.setFocusableInTouchMode(false);

		web.getCustom().setIPTVDebug(ALOG.DEBUG);
		web.getCustom().setProjectPlat(Config.PLATTYPE);
		updateEPGPath();
		//设置#键的EPG键值，让webcore监听#键呼出输入法（需要焦点在input输入框内）。
		web.getCustom().setNumSignKey(EPGKey.POUND);
		web.getCustom().SetEPGSize(ResolutionHelper.epgWidth,ResolutionHelper.epgHeight);
	}
	
	/**
	 * 将指定webview对象重新压入栈顶,置顶该webview，让其可见，并将之前可见的webview隐藏。
	 * @param webview
	 * @return
	 */
	public boolean bringTop(IPTVWebView webview){
		if(mWebStack.isEmpty()){
			return false;
		}
		if(webview == null){
			return false;
		}
		if(mWebviewLayout == null){
			ALOG.info("bringTop > mWebviewLayout == null");
			return false;
		}
		IPTVWebView currentWeb = getCurrentWebview();
		if (!currentWeb.getWebTag().equals(webview.getWebTag())) {
			IPTVWebView tagWeb = mWebStack.push(mWebStack.remove(mWebStack.indexOf(webview)));
			tagWeb.setVisibility(View.VISIBLE);
			mWebviewLayout.bringChildToFront(tagWeb);
			currentWeb.setVisibility(View.GONE);
			return true;
		}
		return false;
	}

	public boolean bringTop(String webTag){
		return bringTop(mWebViewMap.get(webTag));
	}
	
	/**
	 * 页面js接口新开一个web窗口的回调方法。本方法由WebChromeClient.onCreateWindow回调调用。
	 * @param context
	 * @return 创建的webview对象
	 */
	public IPTVWebView onCreateWebWindow(Context context){
		IPTVWebView multiWeb = createWebviewAutoAdd(context,null);
		if(multiWeb!=null){
			multiWeb.setWebViewClient(new IPTVWebViewClient(context));
			multiWeb.setWebChromeClient(new IPTVWebChromeClient(context));
			multiWeb.setWebCustomClient(new IPTVWebCustomClient(iptvView));
			multiWeb.setOnError(new IPTVWebviewListener());
		}
		return multiWeb;
	}
	/**
	 * 页面关闭一个当前web窗口的回调方法。本方法由WebChromeClient.onCloseWindow回调调用。
	 * @param webview
	 */
	public void onCloseWebWindow(SyWebView webview){
		if(!mWebStack.isEmpty()){
			IPTVWebView topWeb = mWebStack.peek();
			try {
				String closeWebTag = ((IPTVWebView)webview).getWebTag();
				if(topWeb.getWebTag().equals(closeWebTag)){
					mWebStack.pop();
					mWebViewMap.remove(closeWebTag);
					if(mWebviewLayout!=null){
						try{
							mWebviewLayout.removeView((IPTVWebView)webview);
							mWebviewLayout.bringChildToFront(getCurrentWebview());
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}else{
					ALOG.error(TAG, "Close webview failed! closeWeb.tag : " + closeWebTag + ", top web.tag : " + topWeb.getTag());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			ALOG.error(TAG, "onCloseWebWindow > webStack is empty.");
		}
	}

	public void setInitialScale(int x,int y){
		if(getCurrentWebview()!=null){
			getCurrentWebview().setInitialScale(x,y);
		}
	}

	/**
	 * 加载新页面。自动停止当前正在加载的页面，清除当前画面。
	 * @param url
     */
	public void stopAndLoadNewUrl(String url){
		if(getCurrentWebview()!=null){
			stopLoading();
			clearWebview();
			getCurrentWebview().loadUrl(url);
		}
	}

	public void stopLoading(){
		if(getCurrentWebview() !=null){
			getCurrentWebview().stopLoading();
		}
	}

	/**
	 * 清空webview视图画面。在某些场景下，需要清空，防止看到之前的页面
	 */
	public void clearWebview(){
		IPTVWebView currentWeb = getCurrentWebview();
		if(currentWeb!=null){
//			currentWeb.stopAndLoadNewUrl("about:blank");
			currentWeb.clearView();
		}
	}

	/**
	 * 主动保存EPG
	 */
	public void saveEPGPage() {
		++saveEpgIndex;
		String jsCode = "javascript:if(typeof(SaveDocument) == 'undefined'){var SaveDocument = function (win) {" +
				"try {var doc = win.document;var url = doc.location.href;" +
				"EPGMain.SaveEPGDocument(doc.documentElement.outerHTML, url,"+saveEpgIndex+");" +
				"for ( var i = 0; i < win.frames.length; i++) {" +
				"SaveDocument(win.frames[i]);}} catch (e) {}}} SaveDocument(window);";
		if(getCurrentWebview() != null){
			getCurrentWebview().evaluateJavascript(jsCode,null);
		}
	}

	/**
	 * 更新EPG保存的目录
	 */
	public void updateEPGPath(){
		if(Config.isAutoSaveWebPage && getCurrentWebview() !=null){
			getCurrentWebview().getCustom().setAutoSaveEPG(USBHelper.usbPath+"/EPG/all/");
		}
	}
	/**
	 * 更新EPG保存的目录
	 */
	public void updateEPGPath(String epgPath){
		if(Config.isAutoSaveWebPage && getCurrentWebview() !=null){
			getCurrentWebview().getCustom().setAutoSaveEPG(epgPath);
		}
	}

	/**
	 * 更新输入法输入的内容到页面上
	 * @param value
     */
	public void updateIputText(String value,int selection){
		ALOG.info("updateIputText > "+value +", selection : "+selection);
		if(getCurrentWebview()!=null){
			String jsCode = "var currentFocus = Navigation.getCurrentElement();currentFocus.value = \""+value+"\";"
					+"if(typeof(currentFocus.selectionStart)== 'number' && typeof(currentFocus.selectionEnd) == 'number'){" +
					"currentFocus.selectionStart = currentFocus.selectionEnd = "+selection+";}";
			getCurrentWebview().evaluateJavascript(jsCode,null);
		}
	}

	/**
	 * 截图webview画面（远程工具使用）
	 */
	public void capturePicture(){
		if(getCurrentWebview() != null ){
			getCurrentWebview().capturePicture();
		}
	}

	/**
	 * 设置截图监听（远程工具使用）
	 * @param pictureListener
     */
	public void setPictureListener(SyWebView.PictureListener pictureListener){
		if(getCurrentWebview()!=null){
			getCurrentWebview().setPictureListener(pictureListener);
		}
	}
}
