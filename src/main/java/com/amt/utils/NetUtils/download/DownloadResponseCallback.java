package com.amt.utils.NetUtils.download;

import java.io.File;

/**
 * Download callback
 * Created by Kelvin young on 2017/6/9.
 */

public abstract class DownloadResponseCallback {
    public void onStart(long totalBytes){}
    public void onCancel(){}
    public void onFinish(File file){}
    public void onProgress(long currentBytes, long totalBytes){}
    public void onFail(String error){}
}
