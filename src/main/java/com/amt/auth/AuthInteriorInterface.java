package com.amt.auth;


/**
 * Created by liaoyn on 2017/3/6.
 * 认证回调接口。主要用于更新进度条UI，以及弹出相应的事件对话框
 */

public interface AuthInteriorInterface {



    /**
     * 网络变化事件回调
     * @param netEventType 网络事件类型。 1： 网络变化  2：网线/wifi 端口变化
     * @param isUp <li>当netEventType 为1时，true代表网络连接成功，false代表网络断开连接
     * 			        <li>当netEventType 为2时，true代表网线插上/wifi可用 ，false代表网线拔掉/wifi被禁用
     */
    void onNetChanged(int netEventType, boolean isUp);

    /**
     *
     * @param tr069EventType 1:为判断iptv存储的零配置标示，2：为网管通知
     * @param isConnected:tr069EventType 为2时 true：网管终端连接网管服务器成功    false：网管终端连接网管服务器失败/超时
     *                   tr069EventType 为1时 true:已经零配置，false：未零配置
     */
  //  void onAuthEvent(int tr069EventType, boolean isConnected);

    /**
     * 网管消息通知事件(主要是弹框相关的事件)
     * @param key 事件类型
     * @param value 相关值
     */
    //void onTr069MsgNotify(String key, String value);

    /**
     *
     *认证成功
     */
    void onAuthSuceed();

    /**
     * 开机时没有数据情况下进行处理
     * @param notdataType 通知类型：1：拉起设置， 2：弹出提示框
     */
    void onNoDataMsg(int notdataType);






}
