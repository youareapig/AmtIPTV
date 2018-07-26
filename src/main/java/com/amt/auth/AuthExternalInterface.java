package com.amt.auth;

/**
 * Created by lyn 认证对外接口 on 2017/6/2.
 */

public interface AuthExternalInterface {
    /**
     * 认证成功接口，用于对外认证成功后处理
     * @param args
     */
    public void onSuccess(Object... args);
    public void onFail(String errorCode, String strMsg, Object... args);
}
