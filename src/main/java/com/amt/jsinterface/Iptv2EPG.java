package com.amt.jsinterface;

import android.text.TextUtils;

import com.SyMedia.webkit.SyJavascriptInterface;
import com.amt.app.IPTVAvtivityView;
import com.amt.config.Config;
import com.amt.player.PlayerJSInterface;
import com.amt.player.PlayerMediator;
import com.amt.utils.ALOG;
import com.amt.utils.FileHelper;
import com.amt.utils.USBHelper;
import com.amt.webview.IPTVWebView;
import com.amt.webview.WebViewManager;
import com.amt.app.IPTVActivity;
import com.android.org.sychromium.content.browser.JavascriptInterface;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zw 20170516
 */

public class Iptv2EPG {
    private static final  Iptv2EPG iptv2epg = new Iptv2EPG();
    private IPTVWebView webview;
    public Utility utility;
    private PlayerJSInterface playerJSInterface;
    public Authentication authInfo;
    private STBAppManager stbAppManager;
    private IPTVAvtivityView iptvView;
    private ExecutorService mThreadPool;
    private static final String TAG ="Iptv2EPG";

    public static  Iptv2EPG getIptv2EPG(){
        return iptv2epg;
    }

    private Iptv2EPG(){}

    public void addJsObject() {
        webview = WebViewManager.getManager().getWebView(IPTVActivity.WEBTAG_IPTV);
        utility = new Utility();
        authInfo = new Authentication();
        stbAppManager = new STBAppManager();
        PlayerMediator.mainPlayer.setEventObject(utility);
        playerJSInterface = PlayerJSInterface.getInstance();
        webview.addJavascriptInterface(iptv2epg, "EPGMain");
        webview.addJavascriptInterface(utility, "Utility");
        webview.addJavascriptInterface(stbAppManager, "STBAppManager");
        webview.addJavascriptInterface(playerJSInterface, "IPTVPlayer");
        webview.addJavascriptInterface(authInfo, "Authentication");
    }

    public void setIptvView(IPTVAvtivityView iptvView){
        this.iptvView = iptvView;
    }

    /**
     *  供页面上调用的弹输入法的接口。在某些特殊场景下，页面上无法使用input标签，但需要弹输入法，可让页面调用此接口。
     *  调用示例代码：
     *
     *  //获取编辑框的top坐标信息。obj为编辑框对象
     *  var c = obj.getBoundingClientRect();
     *  var nTop = c.top + (obj.ownerDocument.defaultView.pageYOffset);
     *  //获取文本框光标位置
     *  var selectionStart = obj.selectionStart ? obj.selectionStart : 0;
     *  ////获取文本框光标结束位置
     *  var selectionEnd = obj.selectionEnd ? obj.selectionEnd : selectionStart;
     *  //传递文本框原始值，光标位置，文本框top坐标
     *  EPGMain.showIMF(obj.value,selectionStart,selectionEnd,nTop);
     *
     * @param value 输入框原本的文字内容
     * @param selectionStart 光标起始位置
     * @param selectionEnd 光标结束位置
     * @param nTop 输入框的top坐标
     */
    @JavascriptInterface
    @SyJavascriptInterface
    public void showIMF(String value,int selectionStart,int selectionEnd, int nTop){
        if(iptvView!=null){
            iptvView.showInputMethod(value,selectionStart,selectionEnd,nTop);
        }
    }

    /**
     * 兼容原来老IPTV 的接口。(不建议使用)
     * @param message
     * @deprecated
     */
    @JavascriptInterface
    @SyJavascriptInterface
    public void showIMF2(String message){
        if(iptvView!=null){
            int selection = message == null ? 0 : message.length();
            iptvView.showInputMethod(message,selection,selection,0);
        }
    }

    /**
     * 页面上打印的接口。兼容原来老IPTV 的接口。不建议用此接口。推荐用console.log()
     * @deprecated
     * @param message
     */
    @JavascriptInterface
    @SyJavascriptInterface
    public void LogMsg(String message){
        ALOG.info("MSG",message);
    }

    /**
     * 手动保存页面数据的方法，供页面调用。
     * @param strHtml
     * @param strUrl
     */
    @JavascriptInterface
    @SyJavascriptInterface
    public void SaveEPGDocument(final String strHtml, final String strUrl,final int saveEpgIndex){
        ALOG.info(TAG,"SaveEPGDocument > url : "+strUrl);
        if(Config.isAutoSaveWebPage){
            if(TextUtils.isEmpty(strUrl) || TextUtils.isEmpty(strHtml)|| "about:blank".equals(strUrl)){
                return;
            }
            //TODO 用线程池来保存文件
            if(mThreadPool == null){
                mThreadPool = Executors.newFixedThreadPool(5);
            }
            mThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        String epgPath = USBHelper.usbPath+"/EPG/"+ saveEpgIndex;
                        File epgFile = new File(epgPath);
                        if(!epgFile.exists()){
                            epgFile.mkdirs();
                        }
                        String pageName = "1.htm";
                        String pagePath = new URL(strUrl).getPath();
                        if(pagePath.indexOf("/") >= 0 ){
                            pageName = pagePath.substring(pagePath.lastIndexOf("/") + 1);
                        }
                        String pageHtml = "<!-- AMT IPTV > request url:" + strUrl + "-->\n" + strHtml;

                        ALOG.info("SaveEPGDocument > file : "+epgPath+"/"+pageName);
                        FileHelper.writeFile(pageHtml,epgPath+"/"+pageName);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }
    }

}
