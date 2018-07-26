package com.amt.net;

import android.content.Context;

import com.amt.app.IptvApp;
import com.amt.auth.AuthManager;
import com.amt.player.iptvplayer.IPTVPlayer;
import com.amt.utils.ALOG;
import com.amt.utils.APKHelper;
import com.amt.app.IPTVActivity;


/**
 * Created by lyn on 2017/4/11.
 */



public class NetWorkProxy implements NetWorkListener {
    private Context mcontext;

    public NetWorkProxy(Context context){

        if(context!=null){
            mcontext=context;
        }

    }

    /***
     *
     * @param netType 网络连接类型。
     * NetConnectManager#NETTYPE_LINK: 有线网络
     *  NetConnectManager#NETTYPE_WIFI: 无线网络
     * @param networkExtra 网络信息。如pppoe拨号状态等。扩展用
     */
    @Override
    public void onNetConnected(int netType, NetWorkExtra networkExtra) {
        ALOG.debug("NetWorkProxy > onNetConnected > netType:"+netType);
        if(!IptvApp.authManager.isAuth&& APKHelper.isIptvTop(IptvApp.app)) {
            //更新进度条，50%
            IptvApp.authManager.onNetChanged(AuthManager.NETEVENT_TYPE_CONNECTIVITY, true);
        }
        IPTVPlayer.setValue("setEthUpDown","1");
        //插上网线取消Dialog
       /* if (IPTVActivity.mDialogManager.currentDialogIsShow()){
            IPTVActivity.mDialogManager.dismissAMTDialog();
        }*/
        IPTVActivity.mDialogManager.disAmtPopupWindow();

    }

    /***
     *
     * @param netType 网络连接类型。
     *  NetConnectManager#NETTYPE_LINK 有线网络
     * NetConnectManager#NETTYPE_WIFI 无线网络
     * @param networkExtra 网络信息。如pppoe拨号状态等。扩展用
     */
    @Override
    public void onNetDisConnect(int netType, NetWorkExtra networkExtra) {
        ALOG.debug("NetWorkProxy > onNetDisConnect > netType:"+netType);
        if(!IptvApp.authManager.isAuth&& APKHelper.isIptvTop(IptvApp.app)) {
            //进度条50%错误
            IptvApp.authManager.onNetChanged(AuthManager.NETEVENT_TYPE_CONNECTIVITY, false);
        }else if(IptvApp.authManager.isAuth){
            /*IPTVActivity.mDialogManager.showIPTVErrorDialog(AmtDialogManager.ERROR_CODE_0013,
                    R.string.error_title_10013,R.string.error_suggest_10013);*/
            //弹在右下角的小弹窗 djf
            IPTVActivity.mDialogManager.showAmtPopupWindow();
        }
        IPTVPlayer.setValue("setEthUpDown","0");
    }

    @Override
    public void onPhyLinkUp() {
        ALOG.debug("NetWorkProxy > onPhyLinkUp ");
        if(!IptvApp.authManager.isAuth&& APKHelper.isIptvTop(IptvApp.app)) {
            //更新进度条10%
            IptvApp.authManager.onNetChanged(AuthManager.NETEVENT_TYPE_TRAFFIC, true);
        }
        //插上网线取消Dialog
       /* if (IPTVActivity.mDialogManager.currentDialogIsShow()){
            IPTVActivity.mDialogManager.dismissAMTDialog();
        }*/
        IPTVActivity.mDialogManager.disAmtPopupWindow();


    }

    @Override
    public void onPhyLinkDown() {
        ALOG.debug("NetWorkProxy > onPhyLinkDown ");
        if(!IptvApp.authManager.isAuth&& APKHelper.isIptvTop(IptvApp.app)) {
            ////更新进度条10%错误
            IptvApp.authManager.onNetChanged(AuthManager.NETEVENT_TYPE_TRAFFIC, false);
        }else if(IptvApp.authManager.isAuth) {
            /*IPTVActivity.mDialogManager.showIPTVErrorDialog(AmtDialogManager.ERROR_CODE_0010,
                    R.string.error_title_10010, R.string.error_suggest_10010);*/
            //弹在右下角的小弹窗 djf
            IPTVActivity.mDialogManager.showAmtPopupWindow();
        }

    }

    @Override
    public void onNetInfoExtra(NetWorkExtra networkExtra) {
        ALOG.debug("NetWorkProxy > onNetInfoExtra ");
    }
}
