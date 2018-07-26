package com.amt.utils.NetUtils;



import com.amt.utils.NetUtils.download.DownloadBuilder;
import com.amt.utils.NetUtils.upload.UploadBuilder;

import okhttp3.OkHttpClient;

/**
 * OkHttp extend object
 * Created by Kelvin young on 2017/6/9.
 */

public class MyOkHttp {
    private static OkHttpClient mOkHttpClient;
    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    /**
     * construct
     */
    public MyOkHttp()
    {
        this(null);
    }

    /**
     * construct
     * @param okHttpClient custom okhttpclient
     */
    public MyOkHttp(OkHttpClient okHttpClient)
    {
        if(mOkHttpClient == null) {
            synchronized (MyOkHttp.class) {
                if (mOkHttpClient == null) {
                    if (okHttpClient == null) {
                        mOkHttpClient = new OkHttpClient();
                    } else {
                        mOkHttpClient = okHttpClient;
                    }
                }
            }
        }
    }

    /*Builder function support extend in the future*/
    public DownloadBuilder download() {
        return new DownloadBuilder(this);
    }

    public UploadBuilder upload(){
        return new UploadBuilder(this);
    }
}
