package com.amt.dialog;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amt.dialog.concrete.AmtDialog;
import com.amt.dialog.concrete.AmtPopupWindow;
import com.amt.dialog.concrete.MyDialogListener;
import com.amt.utils.ALOG;
import com.android.smart.terminal.iptvNew.R;

public class AmtDialogManager {

    private static String tag="AmtDialogManager";
    private static AmtDialogManager amtDialogManager = new AmtDialogManager();
    private static AmtDialog amtDialog;
    private static Context amtContext;
    //特殊局点使用样例
    public static LTJC ltjc;

    private AmtDialogManager() {
        // TODO Auto-generated constructor stub
    }
    public static AmtDialogManager getManager(Context mcontext){

        amtContext =mcontext;
        ALOG.debug(tag,"new scyd");
        ltjc = new LTJC(amtContext);
        return amtDialogManager;
    }


    /**
     * IPTV默认基础布局
     * 提示框
     * @param title     一般放在类中
     * @param subtitle  一般存放在在String.xml中
     * @param Content   一般存放在在String.xml中
     */
    public void showIPTVErrorDialog(String title,int subtitle,int Content){

        AmtDialog.Builder builder=new AmtDialog.Builder(amtContext);
        builder.setTitle(title)
                .setSubTitle(subtitle)
                .setContext(Content)
                .setDialogStyle(AmtDialog.DIALOG_TYPE_DEFAULT);
        amtDialog =builder.create_();
        showAMTDialog();
    }

    /**
     * IPTV新版本基础布局
     * @param errorcode
     * @param errorContent
     */
    public void showIPTVNewErrorDialog(int errorcode,int errorContent){
        AmtDialog.Builder builder=new AmtDialog.Builder(amtContext);
        builder.setTitle(errorcode)
                .setContext(errorContent)
                .setPositiveButtonText("确定",false)
                .setNegativeButtonText("取消",true)
                .setMyDialogListener(new MyDialogListener() {


                    @Override
                    public void PositiveButton() {
                        // TODO Auto-generated method stub
                        Log.i(tag, "this is PositiveButton");
                        if (amtDialog != null) {
                            //	amtDialogBuilder2();
                            dismissAMTDialog();
                        }
                    }

                    @Override
                    public void NegativeButton() {
                        // TODO Auto-generated method stub
                        Log.i(tag, "this is Reboot");
                    }

                })
                .setDialogStyle(AmtDialog.DIALOG_TYPE_AMTNATURALSTYLE);
        amtDialog =builder.create_();
        showAMTDialog();

    }

    /**
     * 显示自动消失的提示dialog。默认3秒后自动消失
     * @param title
     * @param subtitle
     * @param Content
     */
    public void showAutoDisDialog(String title,int subtitle,int Content){
        AmtDialog.Builder builder=new AmtDialog.Builder(amtContext);
        builder.setTitle(title)
                .setSubTitle(subtitle)
                .setContext(Content)
                .setAutoColse(3)
                .setDialogStyle(AmtDialog.DIALOG_TYPE_DEFAULT);
        amtDialog =builder.create_();
        showAMTDialog();
    }

    public ProgressNotifier showProgressDialog(int subtitle){
        return showProgressDialog(amtContext.getString(subtitle));
    }
    public ProgressNotifier showProgressDialog(String subtitle){
        ALOG.info("showProgressDialog");
        AmtDialog.Builder builder = new AmtDialog.Builder(amtContext);
        builder.setDialogSize(2,2.3);
        View progressLayout = LayoutInflater.from(amtContext).inflate(R.layout.layout_dialog_progress,null);
        builder.setContentView(progressLayout);
        final ProgressBar progressBar = (ProgressBar) progressLayout.findViewById(R.id.progress_horizontal);
        progressBar.setProgress(0);
        final TextView tvTitle = (TextView)progressLayout.findViewById(R.id.dialogtitle);
        tvTitle.setText(subtitle);
        amtDialog = builder.create_();
        amtDialog.setCancelable(false);
        showAMTDialog();
        ProgressNotifier progressNotifier = new ProgressNotifier() {
            @Override
            public void onProgressChange(int progress) {
                if(progressBar != null){
                    progressBar.setProgress(progress);
                }
            }
        };
        return progressNotifier;
    }

    public interface ProgressNotifier{
        void onProgressChange(int progress);
    }

    /**
     * 厂家型号不对
     * 进行弹窗限制
     * 提示框
     * @param title     一般放在类中
     * @param subtitle  一般存放在在String.xml中
     * @param Content   一般存放在在String.xml中
     */
    private boolean isSTBErrorShow=false;
    public void showCheckSTBError(String title,int subtitle,int Content){
        AmtDialog.Builder builder=new AmtDialog.Builder(amtContext);
        builder.setTitle(title)
                .setSubTitle(subtitle)
                .setContext(Content)
                .setDialogStyle(AmtDialog.DIALOG_TYPE_DEFAULT);
        amtDialog =builder.create_();
        //设置不能返回键取消
        amtDialog.setCancelable(false);
        showAMTDialog();
        isSTBErrorShow=true;
    }
    /**
     * 显示PopupWindow窗口
     */
    public void showAmtPopupWindow(){
        AmtPopupWindow.create(amtContext);
    }

    /**
     * 关闭PopupWindow窗口
     */
    public void disAmtPopupWindow(){
        AmtPopupWindow.disPopWindos();
    }



    public void dismissAMTDialog(){
        ALOG.debug("dismissAMTDialog");
        if (amtDialog != null){
            amtDialog.dismiss();
        }
    }

    /**
     * 显示与关闭统一管理
     */
    private void showAMTDialog(){
        ALOG.debug("dismissAMTDialog");
 /*       //STBError弹出，关闭其他Dialog
        if (isSTBErrorShow){
            ALOG.debug("isSTBErrorShow-->"+isSTBErrorShow);
            return;
        }*/
        if (amtDialog != null){
            amtDialog.show();
        }

    }

    public boolean currentDialogIsShow(){
        ALOG.debug("getCurrentDialog");
        if (amtDialog != null){
            amtDialog.getMyDialog();
            return amtDialog.isShowing();
        }
        return false;
    }

/**==================基础ErrorCode**/
    /**
     * 网络未连接(1、 网线没插上； 2、 其它网络连接问题。 网络未连接，请检查后重试， 若问题仍然存在，请联系客服 人员)
     */
    public static final String ERROR_CODE_0010 = "0010";
    /**
     * DHCP服务器没有响应(获取地址失败，请重新尝试， 若问题仍然存在，请联系客服 人员)
     */
    public static final String ERROR_CODE_0013 = "0013";
    /**
     * 节目无法播放！(流媒体服务关闭了RTSP 连接， MediaPlayer 对象抛出 “RTSP_CONNECT_STOPPED”事件。媒
     * 体流中断超过10秒钟，节目无法继续播 放，由EPG显示)[暂未处理，由ＥＰＧ显示]
     */
    public static final String ERROR_CODE_0021 = "0021";

    /**
     * 流媒体连接被服务器关闭 (流媒体服务关闭了RTSP 连接， MediaPlayer 对象抛出 “RTSP_CONNECT_STOPPED”事件，但
     * 媒体流未中断，节目继续播放，由EPG显 示 )
     */
    public static final String ERROR_CODE_0022 = "0022";

    /**
     * 认证服务器失败
     */
    public static final String ERROR_CODE_0025 = "0025";


}
