package com.amt.utils.NetUtils.upload;

import okhttp3.Response;

/**
 * Created by Kelvin young on 2017/7/17.
 */
public interface IResponseHandler {

    void onSuccess(Response response);

    void onFailure(int statusCode, String error_msg);

    void onProgress(long currentBytes, long totalBytes);
}
