package com.amt.utils.NetUtils.upload;


import com.amt.utils.NetUtils.MyOkHttp;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base request body with param
 * Created by Kelvin young on 2017/7/17.
 */

public abstract class OkHttpRequestBuilderHasParam<T extends OkHttpRequestBuilderHasParam> extends OkHttpRequestBuilder<T> {

    protected Map<String, String> mParams;

    public OkHttpRequestBuilderHasParam(MyOkHttp myOkHttp) {
        super(myOkHttp);
    }

    /**
     * set Map params
     * @param params
     * @return
     */
    public T params(Map<String, String> params) {
        this.mParams = params;
        return (T) this;
    }

    /**
     * add param
     * @param key param key
     * @param val param val
     * @return
     */
    public T addParam(String key, String val) {
        if (this.mParams == null)
        {
            mParams = new LinkedHashMap<String,String>();
        }
        mParams.put(key, val);
        return (T) this;
    }
}
