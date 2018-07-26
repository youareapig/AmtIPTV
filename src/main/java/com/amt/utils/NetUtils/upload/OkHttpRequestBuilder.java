package com.amt.utils.NetUtils.upload;



import com.amt.utils.NetUtils.MyOkHttp;

import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.Request;

/**
 * Base request body without param
 * Created by Kelvin young on 2017/7/17.
 */
public abstract class OkHttpRequestBuilder<T extends OkHttpRequestBuilder> {
    protected String mUrl;
    protected Object mTag;
    protected Map<String, String> mHeaders;

    protected MyOkHttp mMyOkHttp;

    /**
     * Run Async
     * @param responseHandler Custom callback
     */
    abstract void enqueue(final IResponseHandler responseHandler);

    public OkHttpRequestBuilder(MyOkHttp myOkHttp) {
        mMyOkHttp = myOkHttp;
    }

    /**
     * set url
     * @param url url
     * @return
     */
    public T url(String url)
    {
        this.mUrl = url;
        return (T) this;
    }

    /**
     * set tag
     * @param tag tag
     * @return
     */
    public T tag(Object tag)
    {
        this.mTag = tag;
        return (T) this;
    }

    /**
     * set headers
     * @param headers headers
     * @return
     */
    public T headers(Map<String, String> headers)
    {
        this.mHeaders = headers;
        return (T) this;
    }

    /**
     * set one header
     * @param key header key
     * @param val header val
     * @return
     */
    public T addHeader(String key, String val)
    {
        if (this.mHeaders == null)
        {
            mHeaders = new LinkedHashMap<String,String>();
        }
        mHeaders.put(key, val);
        return (T) this;
    }

    //append headers into builder
    protected void appendHeaders(Request.Builder builder, Map<String, String> headers) {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (headers == null || headers.isEmpty()) return;

        for (String key : headers.keySet()) {
            headerBuilder.add(key, headers.get(key));
        }
        builder.headers(headerBuilder.build());
    }
}
