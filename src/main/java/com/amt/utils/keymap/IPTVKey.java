package com.amt.utils.keymap;

/**
 * Created by DonWZ on 2017/5/2.
 */

public class IPTVKey {
    /**键值名称*/
    protected String keyName;
    /**Android 键值*/
    protected int androidCode;
    /**EPG键值*/
    protected int iptvCode;

    protected IPTVKey(String keyName,int androidCode,int iptvCode){
        this.keyName = keyName;
        this.androidCode = androidCode;
        this.iptvCode = iptvCode;
    }
    protected IPTVKey(){}

}
