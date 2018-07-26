package com.amt.dialog.concrete;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.amt.utils.ALOG;
import com.android.smart.terminal.iptvNew.R;

import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
/**
 * Dialog 基类
 * @author djf
 *
 */
public class MyDialog extends Dialog {


    private static Context mContext;
    private TextView titleText;//标头
    private TextView contentText;//内容
    private Button PositiveButton;//确定键
    private Button cancelButton;//取消键
    private Button NeutralButton;//第三个按键
    public static  int nScreenWidth;//屏幕宽度
    public static int  nScreenHeight;//屏幕高度
    public static String TAG="MyDialog";
    /**
     * 创建Dialog的管理栈，LIFO数据结构来进行管理
     */
    private static Stack<MyDialog> myDialogStack = new Stack<MyDialog>();

    public MyDialog(Context context) {
        super(context,R.style.MyDialog);
        this.mContext=context;
        //remove 允许返回键关闭dialog
        //设置不能通过其他方式关闭
     //   this.setCancelable(false);
        //创建是我们自己手动去拿一次系统分辨率
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        this.nScreenWidth =dm.widthPixels;
        this.nScreenHeight = dm.heightPixels;
        ALOG.debug(TAG, "MyDialog single Width&&Height-->"+ nScreenWidth+"   "+nScreenHeight+"   &&stack-->"+myDialogStack.isEmpty());

    }

    public MyDialog(Context context, int theme) {
        super(context, theme);
        this.mContext=context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub
        super.show();
        ALOG.debug(TAG,"MyDialog showDialog");
        //检查当前是否已经已经有Dialog在前段了
        clearExistedDialog();
        pushSatck(this);
    }


    @Override
    public void dismiss() {
        // TODO Auto-generated method stub
        super.dismiss();
        ALOG.debug(TAG,"MyDialog disDialog");
        popSatck();
    }


    /**
     * 设置布局ID
     * @param LayoutID
     */
    protected void setView(int LayoutID){
        ALOG.debug("setView  -->"+LayoutID);
        this.setContentView(LayoutID);
    }

    /**
     * 设置自定义布局的View
     * @param view
     */
    protected void setView(View view){
        this.setContentView(view);
    }
    /**
     *设置自定义布局的View
     * @param view The desired content to display.
     * @param params Layout parameters for the view.
     */
    protected void setView(View view, ViewGroup.LayoutParams params){
        this.setContentView(view,params);
    }
    /**
     * 需要传入title的内容,和对应的控件的ID
     * @param title
     * @param TitleTextID
     */
    protected void setTitleText(String title, int TitleTextID){
        setTitleText(title, TitleTextID, 0);
    }

    /**
     * 头部内容，控件ID，颜色， 如果没有传入ID则不显示Title
     * @param title
     * @param TitleTextID
     * @param color
     */
    protected void setTitleText(String title, int TitleTextID, int color){

        titleText =(TextView) findViewById(TitleTextID);
        titleText.setTag("title");

        if (titleText != null) {
            if (!TextUtils.isEmpty(title)) {
                titleText.setText(Html.fromHtml(title));
                titleText.getPaint().setFakeBoldText(true);
            }
        }
        if (color != 0){
            titleText.setTextColor(color);
        }
    }

    /**
     * 需要传入subtitle的内容,和对应的控件的ID
     * @param title
     * @param TitleTextID
     */
    protected void setSubTitleText(String title, int TitleTextID){
        setSubTitleText(title, TitleTextID, 0);
    }

    /**
     * 副标题内容，控件ID，颜色， 如果没有传入ID则不显示Title
     * @param title
     * @param TitleTextID
     * @param color
     */
    protected void setSubTitleText(String title, int TitleTextID, int color){

        titleText =(TextView) findViewById(TitleTextID);
        titleText.setTag("subtitle");

        if (titleText != null) {
            if (!TextUtils.isEmpty(title)) {
                titleText.setText(Html.fromHtml(title));
                titleText.getPaint().setFakeBoldText(true);
            }
        }
        if (color != 0){
            titleText.setTextColor(color);
        }
    }
    /**
     * 提示内容，控件ID
     * @param context
     * @param ContextID
     */
    protected void setContextText(String context, int ContextID){
        setContextText(context, ContextID, 0);
    }

    /**
     * 提示内容，控件ID,颜色
     * @param context
     * @param ContextID
     * @param color
     */
    protected void setContextText(String context, int ContextID, int color){

        contentText = (TextView) this.findViewById(ContextID);
        contentText.setTag("content");

        if (contentText != null && !TextUtils.isEmpty(context)) {
            contentText.setText(Html.fromHtml(context));
        }

        if (color != 0){
            contentText.setTextColor(color);
        }
    }

    /**
     *  设置 确定键
     * @param BtnID   传入Button的ID
     * @param BtnText  Button的值
     * @param isVisable	 * 设置是否可见
     */
    protected void setPositiveButton(String BtnText, int BtnID, Boolean isVisable){

        PositiveButton =(Button) findViewById(BtnID);
        PositiveButton.setTag("confrim");

        if (BtnText != null) {
            PositiveButton.setText(BtnText);
        }

        if (PositiveButton != null ) {
            if (isVisable) {
                PositiveButton.setVisibility(View.VISIBLE);
            }else {
                PositiveButton.setVisibility(View.GONE);
            }
        }

    }

    /**
     *     设置确定键监听
     * @param myDialogListener  按键监听的Listenner
     * @param isAutoColse  点击后是否关闭对话框
     */

    protected void setPositiveButtonListener(final MyDialogListener myDialogListener, final Boolean isAutoColse){

        if (PositiveButton  ==  null) {
            Log.i("Deng", "PositiveButton is null");
            return;
        }
        PositiveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (myDialogListener != null) {
                    myDialogListener.PositiveButton();
                }

                if (isAutoColse) {
                    dismiss();
                }

            }
        });
        //设置默认焦点
        try {
            PositiveButton.setFocusable(true);
            PositiveButton.setFocusableInTouchMode(true);//设置焦点联系方式
            PositiveButton.requestFocus();
            PositiveButton.requestFocusFromTouch();
        } catch (Exception e) {
            // TODO: handle exception
            PositiveButton.requestFocus();
        }

    }

    /**
     *  设置 取消键
     * @param BtnID   传入的id
     * @param BtnText   按钮的显示键
     * @param isVisable  是否可以见
     */
    protected void setNegativeButton(String BtnText, int BtnID, Boolean isVisable){

        cancelButton =(Button) findViewById(BtnID);
        cancelButton.setTag("confrim");

        if (BtnText != null) {
            cancelButton.setText(BtnText);
        }

        if (cancelButton != null ) {
            if (isVisable) {
                cancelButton.setVisibility(View.VISIBLE);
            }else {
                cancelButton.setVisibility(View.GONE);
            }
        }

    }

    /**
     *
     *    设置取消键监听
     * @param myDialogListener  按键监听的Listenner
     * @param isColse  点击后是否关闭对话框
     */
    protected void setNegativeButtonListener(final MyDialogListener myDialogListener,final Boolean isColse){

        if (cancelButton  ==  null) {
            Log.i("Deng", "NegativeButton is null");
            return;
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("Deng", "NegativeButton is not null");
                if (myDialogListener != null) {
                    myDialogListener.NegativeButton();
                }

                if (isColse) {
                    dismiss();
                }
            }
        });

    }

    /**
     *  设置 功能键
     * @param BtnID   传入的id
     * @param BtnText   按钮的显示键
     * @param isVisable  是否可以见
     */
    protected void setNeutralButton(String BtnText, int BtnID, Boolean isVisable){

        //设置tag,避免重复的寻找id
        NeutralButton =(Button) findViewById(BtnID);
        NeutralButton.setTag("confrim");

        if (BtnText != null) {
            NeutralButton.setText(BtnText);
        }

        if (NeutralButton != null ) {
            if (isVisable) {
                NeutralButton.setVisibility(View.VISIBLE);
            }else {
                NeutralButton.setVisibility(View.GONE);
            }
        }

    }

    /**
     *
     *    设置功能键监听
     * @param myDialogListener  按键监听的Listenner
     * @param isAutoColse  点击后是否关闭对话框
     */
    protected void setNeutralButtonListener(final MyDialogListener myDialogListener,final Boolean isAutoColse){

        if (NeutralButton  ==  null) {
            Log.i("Deng", "NegativeButton is null");
            return;
        }

        NeutralButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("Deng", "setNeutralButton is not null");
                if (myDialogListener != null) {
                    myDialogListener.NeutalButton();
                }

                if (isAutoColse) {
                    dismiss();
                }
            }
        });

    }

    /**
     * 设置是否自动关闭Dialog
     * @param ms  单位 : 秒
     */
    protected void setAutoColseDialog(int ms){
        Timer timer = new Timer();
        int delayTime = ms * 1000 ;

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    ALOG.debug("AutoColseDialog");
                    dismiss();
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }, delayTime);
    }

/*	*//**
     * 获取错误码对应的title
     * @return
     *//*
	protected String getTitleErrorCode(String errorCode){
		if (errorCode != null) {
			String fristCode = getFristErrorCode();
			String title=getString(R.string.inquiry_title)+fristCode+errorCode;
			return title;
		}
		return null;
	}*/





    /**
     * 	 *对不同系统的密度不一样，让界面自动放大缩小
     * @param window
     */
    protected void setLayoutHelper(Window window){
        //	LayoutHelper lHelper= new LayoutHelper(window, context, DialogType, DialogLayoutID);
//		lHelper.init();
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.width = (int) (nScreenWidth / (1.6f));
        wl.height = (int) (nScreenHeight / (1.45f));
        ALOG.debug("IPTV","w-->"+wl.x +"  && y -->"+wl.y);
        window.setAttributes(wl);
    }

    /**
     * 自定义Dialog尺寸
     * 实际效果：
     * realsize= screenheight/ height;
     * realsize= screenwidth/ width
     * @param window
     * @param context
     * @param widthzoom    宽度的比例
     * @param heighzoom    高度的比例
     */
    protected void setDialogSize(Window window, Context context, double widthzoom, double heighzoom){
        WindowManager.LayoutParams wl = window.getAttributes();
        if (widthzoom != 0 && widthzoom >0) {
            wl.width = (int) (nScreenWidth / widthzoom);
        }
        if (heighzoom != 0 && heighzoom > 0) {
            wl.height = (int) (nScreenHeight /heighzoom);
        }
        ALOG.debug("IPTV","w-->"+wl.x +"  && y -->"+wl.y);
        window.setAttributes(wl);
    }


    private void clearExistedDialog() {
        // TODO Auto-generated method stub
        MyDialog localDilaog =getMyDialog();
        if (localDilaog != null) {
            localDilaog.dismiss();
        }
    }

    /**
     * 压栈
     * @param myDialog
     */
    private void pushSatck(MyDialog myDialog){
        if (myDialog != null ) {
            myDialogStack.push(myDialog);
            ALOG.debug(TAG,"pushSatck-->"+myDialogStack.size()+"  &&    "  +myDialogStack.empty()+"   &&  "+myDialog);
        }
    }
    /**
     * 出栈
     */
    private void popSatck(){
        if (!myDialogStack.empty()) {
            myDialogStack.pop();
            ALOG.debug(TAG,"popSatck-->"+myDialogStack.size());
        }
    }

    /**
     * 获取栈顶的Dialog
     * @return
     */
    public static MyDialog getMyDialog(){
        if (!myDialogStack.empty()) {
            MyDialog myDialog = myDialogStack.peek();
            ALOG.debug(TAG,"getMyDialog-->"+myDialog);
            return myDialog;
        }
        return null;
    }


/*    *//**
     * 弹出dialog时候，背景不变色
     *//*
    protected void setScreenBgLight() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 1.0f;
        lp.dimAmount = 0.0f;
        getWindow().setAttributes(lp);
    }*/

    /**
     * 获取字符串的资源ID
     * @param id
     * @return
     */
    protected static String getString(int id) {
        return mContext.getString(id);
    }
    /**
     * 获取颜色的资源ID
     * @param id
     * @return
     */
    private int getColor(int id) {
        return mContext.getResources().getColor(id);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }



}
