package com.amt.dialog;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.amt.dialog.concrete.AmtDialog;
import com.amt.dialog.concrete.MyDialogListener;
import com.amt.utils.ALOG;
import com.android.smart.terminal.iptvNew.R;

/**
 * Created by DJF on 2017/6/6.
 */
public class SCYD {


    /**
     * 特殊局点错误放在特殊的Dialog 创建类里面
     */

     // 业务用户类型不正确

    public static String HFDX_ERROR_CODE_0147 = "0147";


     // 连接平台失败

    public static String SCYD_ERROR_CODE_0141 = "0141";
    /**
     * SCYD需要使用AmtDialogManager里面的amtDialog
     * 保持对象一致
     * @param context
     * @param dialog
     */
    public SCYD(Context context, AmtDialog dialog) {
        amtContext =context;
        amtDialog = dialog;
    }

    private String tag="AmtDialogManager";
    private Context amtContext;
    private static AmtDialog amtDialog;
    public void showZeroSetting(int title,int subtitle,int Content){

        AmtDialog.Builder builder =new AmtDialog.Builder(amtContext);

        builder.setContentView(R.layout.demo_dialogview_hf_zero)//设置自定义布局
                .setTitle(R.string.hfdx_error_title_10147,R.id.scyd_zero_title)//标题
                .setSubTitle(R.string.hfdx_zero_contentmsg_06,R.id.scyd_zero_content)//副标题
                .setPositiveButton("手动设置",false,R.id.scyd_zero_btn)//设置Btn
                .setMyDialogListener(new MyDialogListener(){//设置监听
                    @Override
                    public void PositiveButton() {
                        // TODO Auto-generated method stub
                        Log.i(tag, "this is PositiveButton");
                        Toast.makeText(amtContext,"Go Setting", Toast.LENGTH_SHORT).show();
                        dismissAMTDialog();
                    }
                });
        amtDialog =builder.create_();//创建Dialog
        amtDialog.show();//显示
    }



    public void dismissAMTDialog(){
        ALOG.debug("dismissAMTDialog");
        if (amtDialog != null){
            amtDialog.dismiss();
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
}
