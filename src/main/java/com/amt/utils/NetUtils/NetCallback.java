package com.amt.utils.NetUtils;

/**
 * Created by DonWZ on 2017/9/20.
 */

public class NetCallback {
    public void onSuccess(String result) {}
    public void onFail(String error) {}
    public void on302Moved(String location) {this.onSuccess(location);}
}
