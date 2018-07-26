package com.amt.webview;

import com.SyMedia.webkit.SyWebView;
import com.amt.utils.ALOG;

import org.apache.http.HttpHost;

import java.net.Socket;

/**
 * Created by DonWZ on 2017/6/5.
 */

public class IPTVWebviewListener implements SyWebView.OnWebViewListener{



    @Override
    public void onBindSocketToEth(Socket socket) {

    }

    @Override
    public void onError(int i, String s, String s1) {
        ALOG.error("OnWebViewListener>OnError>"+i+", "+s+", "+s1);
    }

    @Override
    public void onOpenHttpConnection(HttpHost httpHost) {

    }

    @Override
    public Object onPullData(int i, Object... objects) {
        return null;
    }

    @Override
    public void onPushData(int i, Object... objects) {

    }
}
