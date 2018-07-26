package com.amt.utils.keymap;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Xml;
import android.webkit.URLUtil;

import com.amt.jsinterface.Iptv2EPG;
import com.amt.utils.ALOG;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zw on 2017/7/6.
 */

public class GlobalKeyHelper {
    private static Map<String, GlobalKeyBean> mapGlobalKey = null;
    private static final String TAG = "GlobalKeyHelper";

    /**
     * 开始解析全局键值表
     *
     * @param xml
     */
    public static void init(final String xml) {
        ALOG.info(TAG, "init GlobalKey xml.");
        if (TextUtils.isEmpty(xml)) {
            ALOG.info(TAG, "xml is null or empty return!");
            return;
        }
        mapGlobalKey = new HashMap<String, GlobalKeyBean>();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                InputStream is = new ByteArrayInputStream(xml.getBytes());
                XmlPullParser xmpp = Xml.newPullParser();
                try {
                    xmpp.setInput(is, "UTF-8");
                    int eventType = xmpp.getEventType();
                    GlobalKeyBean bean = null;//new GlobalKeyBean();
                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        switch (eventType) {
                            case XmlPullParser.START_DOCUMENT:
                                break;
                            case XmlPullParser.START_TAG:
                                String nodeName = xmpp.getName();
                                String text = xmpp.nextText();
                                if("response_define".equals(nodeName)){
                                    bean = new GlobalKeyBean();
                                }else if ("key_name".equals(nodeName)) {
                                    if(bean!=null){
                                        bean.setKeyName(text);
                                    }
                                } else if ("response_type".equals(nodeName)) {
                                    if(bean!=null){
                                        bean.setResponseType(text);
                                    }
                                } else if ("key_code".equals(nodeName)) {
                                    if(bean!=null){
                                        bean.setKeyCode(text);
                                    }
                                } else if ("service_url".equals(nodeName)) {
                                    if(bean!=null){
                                        bean.setServiceUrl(text);
                                    }
                                } else if ("event_type".equals(nodeName)) {
                                    if(bean!=null){
                                        bean.setEventType(text);
                                    }
                                }
                                break;
                            case XmlPullParser.END_TAG:
                                String nodeName2 = xmpp.getName();
                                if("response_define".equals(nodeName2)){
                                    if (!TextUtils.isEmpty(bean.getKeyName())) {
                                        mapGlobalKey.put(bean.getKeyName(), bean);
                                    } else {
                                        mapGlobalKey.put(bean.getKeyCode(), bean);
                                    }
                                }
                                break;
                        }
                        eventType = xmpp.next();
                    }
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取快捷键url，如：四色键
     *
     * @param keycode
     */
    public static String getQuickEnterUrl(int keycode) {
        String serviceUrl = "";
        String keyName = "";
        if (keycode == EPGKey.LIVE) {
            keyName = "KEY_RED";
        } else if (keycode == EPGKey.TVOD) {
            keyName = "KEY_GREEN";
        } else if (keycode == EPGKey.VOD) {
            keyName = "KEY_YELLOW";
        } else if (keycode == EPGKey.INFO) {
            keyName = "KEY_BLUE";
        }
        if (mapGlobalKey!=null && mapGlobalKey.size() > 0) {
            GlobalKeyBean bean = mapGlobalKey.get(keyName);
            if (bean != null) {
                serviceUrl = bean.getServiceUrl();
                if (!TextUtils.isEmpty(serviceUrl)) {// 地址不为空
                    if (!URLUtil.isNetworkUrl(serviceUrl)) {// 但又不是一个http地址,则认为是一个相对地址,需要补全前面部分
                        String strEPGDomain = Iptv2EPG.getIptv2EPG().authInfo.CTCGetConfig("EPGDomain");
                        if (!TextUtils.isEmpty(strEPGDomain)) {
                            try {
                                URL url = new URL(strEPGDomain);
                                serviceUrl = url.getProtocol() + "://" + (url.getAuthority()) + (serviceUrl.startsWith("/") ? serviceUrl : "/" + serviceUrl);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        ALOG.info(TAG, "getQuickEnterUrl--->serviceUrl: " + serviceUrl);
        return serviceUrl;
    }
}
