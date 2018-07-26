package com.amt.utils.NetUtils.upload;


import com.amt.utils.ALOG;
import com.amt.utils.mainthread.MainThreadSwitcher;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Override okhttp's Callback
 * Created by Kelvin young on 2017/7/17.
 */
public class MyCallback implements Callback {

    private IResponseHandler mResponseHandler;

    public MyCallback(IResponseHandler responseHandler) {
        mResponseHandler = responseHandler;
    }

    @Override
    public void onFailure(Call call, final IOException e) {
        ALOG.debug("onFailure : " + e);

        MainThreadSwitcher.runOnMainThreadAsync(new Runnable() {
            @Override
            public void run() {
                mResponseHandler.onFailure(0, e.toString());
            }
        });
    }

    @Override
    public void onResponse(Call call, final Response response) {
        if(response.isSuccessful()) {
            mResponseHandler.onSuccess(response);
        } else {
            ALOG.debug("onResponse fail status=" + response.code());

            MainThreadSwitcher.runOnMainThreadAsync(new Runnable() {
                @Override
                public void run() {
                    mResponseHandler.onFailure(response.code(), "fail status=" + response.code());
                }
            });
        }
    }
}
