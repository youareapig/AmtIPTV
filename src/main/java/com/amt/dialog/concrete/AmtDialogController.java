package com.amt.dialog.concrete;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.amt.utils.ALOG;
import com.android.smart.terminal.iptvNew.R;

public class AmtDialogController {

    private String TAG="AmtDialogController";
    private AmtDialogParams amtDialogInfo;
    private AmtDialog amtDialog;
    private Context amtContext;
    private MyDialogListener amtlistener;//dialog按键监听
    /******************************************装饰变量********************/
    private View mView;
    private ViewGroup.LayoutParams mlayoutparams;
    private int mlayoutResID;
    private String title;   //标题
    private String subtitle;
    private int titleColor; //标题颜色
    private int subtitleColor;//副标题颜色
    private String errorcontent; //内容
    private int contextColor; //内容颜色
    private String positiveButtonText; //第一键
    private String negativeButtonText;//第二键
    private String neutralButtonText;//第三键
    private boolean positiveButtonVisible=false;
    private boolean negativeButtonVisible=false;
    private boolean neutralButtonVisible=false;
    //是否能按键关闭dialog
    private boolean positiveButtonDefaultColse =false;
    private boolean negativeButtonDefaultColse = false;
    private boolean neutralButtonDefaultColse=false;
    private boolean systemDialog =false;
    private int autoColseMs;//自动关闭的时间
    private double width;//自定义dialog的缩放尺寸   宽度
    private double height;//自定义dialog的缩放尺寸   长度
    /****************************************控制变量***********************/
    private int amtDialog_id_layout=-1;
    private int amtDialog_id_title=-1;
    private int amtDialog_id_subtitle=-1;
    private int amtDialog_id_context=-1;
    private int amtDialog_id_positiveButton=-1;
    private int amtDialog_id_negativeButton=-1;
    private int amtDialog_id_neutalButton=-1;

    public AmtDialogController(Context mContext, AmtDialog myDialog , MyDialogListener mylistener, int mydialogTpye) {
        // TODO Auto-generated constructor stub
        if (mContext != null) {
            amtContext = mContext;
        }
        if (mylistener != null) {
            amtlistener = mylistener;
        }
        amtDialog = myDialog;
        //初始化布局
        initView(mydialogTpye);

    }

    /**
     * 初始化布局
     * @param mydialogTpye
     */
    private void initView(int mydialogTpye) {
        // TODO Auto-generated method stub
        switch (mydialogTpye) {
            case AmtDialog.DIALOG_TYPE_AMTNATURALSTYLE:
                //新版IPTVDialog风格
                amtNaturalDialog();
                break;
            case AmtDialog.DIALOG_TYPE_DEFAULT:
                //联通集采规范默认布局
                amtDefaultDialog();
                break;
            default:
                break;
        }
    }


    /**
     * 设置自定义View
     * @param layoutID
     */
    public void setView(int layoutID){

        amtDialog.setView(layoutID);
    }

    /**
     * 设置自定义View
     * @param layoutView
     */
    public void setView(View layoutView){

        amtDialog.setView(layoutView);
    }

    /**
     * 设置自定义VIew
     * @param layoutView
     * @param layoutParams
     */
    public void setView(View layoutView, ViewGroup.LayoutParams layoutParams){
        amtDialog.setView(layoutView,layoutParams);
    }
    /**
     * 设置Title内容
     * @param title
     */
    public void setTitle(String title){
        ALOG.debug(TAG,"title-->"+ title);
        if (amtDialog_id_title > 0) {
            amtDialog.setTitleText(title, amtDialog_id_title);
        }
    }
    /**
     * 设置 Title内容和颜色
     * @param title
     * @param colorID
     */
    public void setTitleColor(String title, int colorID){
        ALOG.debug(TAG,"setTitleColor-->"+colorID);
        //设置颜色
        if (amtDialog_id_title >0) {
            amtDialog.setTitleText(title, amtDialog_id_title, colorID);
        }

    }
    /**
     * 设置subTitle内容
     * @param title
     */
    public void setSubTitle(String title){
        ALOG.debug(TAG,"title-->"+ title);
        if (amtDialog_id_title > 0) {
            amtDialog.setSubTitleText(title, amtDialog_id_subtitle);
        }
    }
    /**
     * 设置 subTitle内容和颜色
     * @param title
     * @param colorID
     */
    public void setSubTitleColor(String title, int colorID){
        ALOG.debug(TAG,"setTitleColor-->"+colorID);
        //设置颜色
        if (amtDialog_id_title >0) {
            amtDialog.setSubTitleText(title, amtDialog_id_subtitle, colorID);
        }

    }
    /**
     * 设置内容
     * @param content
     */
    public void setContent(String content){
        ALOG.debug(TAG,"setContent-->"+ content);
        if (amtDialog_id_context >0) {
            amtDialog.setContextText(content, amtDialog_id_context);

        }
    }

    /**
     * 设置内容和颜色
     * @param content
     */
    public void setContent(String content, int colorID){
        ALOG.debug(TAG,"setContentColor-->"+ colorID);
        if (amtDialog_id_context >0) {
            amtDialog.setContextText(content, amtDialog_id_context,colorID);
        }
    }

    /**
     * 设置内容的颜色
     * @param colorID
     */
    public void setContentColor(int colorID){
        ALOG.debug(TAG,"setContentColor-->"+ colorID);
        if (amtDialog_id_context >0) {
            amtDialog.setContextText(null, amtDialog_id_context,colorID);
        }
    }


    /**
     * 设置Positive按钮文本
     * @param content     按钮内容
     * @param isVisible   按钮是否可见
     */
    public void setPositiveButtonText(String content, boolean isVisible){
        ALOG.debug(TAG,"setConfrimButtonText-->"+ content);
        if (amtDialog_id_positiveButton > 0) {
            amtDialog.setPositiveButton(content, amtDialog_id_positiveButton, isVisible);
        }

    }

    /**
     * 设置Negative按钮文本
     * @param content     按钮内容
     * @param isVisible   按钮是否可见
     */
    public void setNegativeButtonText(String content, boolean isVisible){
        ALOG.debug(TAG,"setConfrimButtonText-->"+ content);
        if (amtDialog_id_negativeButton >0) {
            amtDialog.setNegativeButton(content, amtDialog_id_negativeButton, isVisible);
        }

    }

    /**
     * 设置确定Neutal钮文本
     * @param content     按钮内容
     * @param isVisible   按钮是否可见
     */
    public void setNeutalButtonText(String content, boolean isVisible){
        ALOG.debug(TAG,"setmultiConfigButtonText-->"+ content);
        if (content != null) {
            amtDialog.setNeutralButton(content, amtDialog_id_neutalButton, isVisible);
        }
    }


    /**
     * 自定义Dialog尺寸
     * 实际效果：
     * realsize= screenheight/ height;
     * realsize= screenwidth/ width
     * @param width    宽度的比例
     * @param height    高度的比例
     */
    public void setDialogSize(double width,double height){
        ALOG.debug(TAG,"setDialogSize-->"+ width +" && height-->"+height);
        amtDialog.setDialogSize(amtDialog.getWindow(), amtContext, width, height);
    }
    /**
     * 设置自动关闭的时间
     * @param ms  单位: 秒
     */
    public void setAutoColse(int ms){
        ALOG.debug(TAG,"setAutoColseDialog-->"+ ms);
        if (ms > 0) {
            amtDialog.setAutoColseDialog(ms);
        }
    }

    /**
     * 设置Dialog是否是系统级的
     * 需要改权限<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
     * @param systemDialog
     */
    public void setSystemDialog(boolean systemDialog){
        if (systemDialog) {
            //添加系统级弹窗
            amtDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }

    }
    /**
     * 设置PositiveButton 监听事件
     * @param myDialogListener
     * @param isCanClose  是否可以点击就关闭
     */
    public void setPositiveButtonListener(MyDialogListener myDialogListener,boolean isCanClose){
        if (amtDialog != null) {
            amtDialog.setPositiveButtonListener(myDialogListener,isCanClose);
        }
    }

    /**
     * 设置NegativeButton 监听事件
     * @param myDialogListener
     * @param isCanClose  是否可以点击就关闭
     */
    public void setNegativeButtonListener(MyDialogListener myDialogListener,boolean isCanClose){
        if (amtDialog != null) {
            amtDialog.setNegativeButtonListener(myDialogListener,isCanClose);
        }
    }

    /**
     * 设置NeutralButton 监听事件
     * @param myDialogListener
     * @param isCanClose  是否可以点击就关闭
     */
    public void setNeutralButtonListener(MyDialogListener myDialogListener,boolean isCanClose){
        if (amtDialog != null) {
            amtDialog.setNeutralButtonListener(myDialogListener,isCanClose);
        }
    }

    /**
     * 初始化规范Dialog框布局
     */
    private void amtDefaultDialog() {
        amtDialog.setView(R.layout.layout_dialog_default);
        amtDialog.setLayoutHelper(amtDialog.getWindow());
        amtDialog.setTitleText("提示",R.id.dialog_default_title);
        amtDialog.setSubTitleText("副标题框",R.id.dialog_default_subtitle);
        amtDialog.setContextText("内容",R.id.dialog_default_content);
        //默认设置3个按钮都不显示
        amtDialog.setPositiveButton("确定",R.id.dialog_default_positive,false);
        amtDialog.setNegativeButton("取消",R.id.dialog_default_negative,false);
        amtDialog.setNeutralButton("设置",R.id.dialog_default_neutral,false);
        setPositiveButtonListener(amtlistener, false);
        setNegativeButtonListener(amtlistener, false);
        setNeutralButtonListener(amtlistener, false);
        amtDialog_id_layout = R.layout.layout_dialog_default;
        amtDialog_id_title = R.id.dialog_default_title;
        amtDialog_id_subtitle = R.id.dialog_default_subtitle;
        amtDialog_id_context = R.id.dialog_default_content;
        amtDialog_id_positiveButton = R.id.dialog_default_positive;
        amtDialog_id_negativeButton = R.id.dialog_default_negative;
        amtDialog_id_neutalButton =R.id.dialog_default_neutral;
    }

    /**
     * 初始化新版IPTV框布局
     */
    private void amtNaturalDialog() {
        // TODO Auto-generated method stub
        amtDialog.setView(R.layout.layout_dialog_newdefault);
        amtDialog.setLayoutHelper(amtDialog.getWindow());
        amtDialog.setTitleText("提示", R.id.dialog_natural_title);
        amtDialog.setContextText("显示的内容", R.id.dialog_natural_context);
        amtDialog.setPositiveButton("确定", R.id.dialog_natural_positive, false);
        amtDialog.setNegativeButton("取消", R.id.dialog_natural_negative, false);
        amtDialog.setNeutralButton("帮助", R.id.dialog_natural_neutral, false);
        setPositiveButtonListener(amtlistener, false);
        setNegativeButtonListener(amtlistener, false);
        setNeutralButtonListener(amtlistener, false);
        amtDialog_id_layout = R.layout.layout_dialog_newdefault;
        amtDialog_id_title = R.id.dialog_natural_title;
        amtDialog_id_context = R.id.dialog_natural_context;
        amtDialog_id_positiveButton = R.id.dialog_natural_positive;
        amtDialog_id_negativeButton = R.id.dialog_natural_negative;
        amtDialog_id_neutalButton =R.id.dialog_natural_neutral;
    }



    /**
     *设置我们自定义的Dialog样式
     * @param dialogInfo
     */
    public void apply(AmtDialogParams dialogInfo){
        ALOG.debug(TAG,"apply DialogInfo-->"+dialogInfo.toString());
        if (dialogInfo != null) {
            amtDialogInfo  =dialogInfo;
            mView =dialogInfo.mview;
            mlayoutparams=dialogInfo.mlayoutparams;
            mlayoutResID = dialogInfo.mlayoutResID;
            title = dialogInfo.title;
            subtitle =dialogInfo.subtitle;
            errorcontent = dialogInfo.content;
            titleColor = dialogInfo.titleColor;
            subtitleColor=dialogInfo.subTitleColor;
            contextColor = dialogInfo.contentColor;
            positiveButtonText = dialogInfo.PositiveButtonText;
            negativeButtonText = dialogInfo.NegativeButtonText;
            neutralButtonText = dialogInfo.NeutralButtonText;
            positiveButtonVisible =dialogInfo.PositiveVisible;
            negativeButtonVisible = dialogInfo.NegativeVisible;
            neutralButtonVisible = dialogInfo.NeutralVisible;
            positiveButtonDefaultColse =dialogInfo.PositiveDefaultColse;
            negativeButtonDefaultColse= dialogInfo.NegativeDefaultColse;
            neutralButtonDefaultColse = dialogInfo.NeutralDefaultColse;
            autoColseMs = dialogInfo.AutoCloseMs;
            systemDialog = dialogInfo.systemType;
            width = dialogInfo.width;
            height = dialogInfo.height;
        }else {
            throw new IllegalArgumentException("DialogInfo does not exist");
        }
        //设置Title控件ID
        if (dialogInfo.title_id > 0){
            amtDialog_id_title = dialogInfo.title_id;
        }
        //设置subTitle控件ID
        if (dialogInfo.subtitle_id > 0){
            amtDialog_id_subtitle = dialogInfo.subtitle_id;
        }
        //设置Content 控件ID
        if (dialogInfo.content_id > 0){
            amtDialog_id_context = dialogInfo.content_id;
        }
        //设置PositiveBtn 控件ID
        if (dialogInfo.PositiveButton_id >0){
            amtDialog_id_positiveButton =dialogInfo.PositiveButton_id;
        }
        //设置NegativeBtn 控件ID
        if (dialogInfo.NegativeButton_id >0){
            amtDialog_id_negativeButton =dialogInfo.NegativeButton_id;
        }
        //设置NeutalBtn 控件ID
        if (dialogInfo.NeutalButton_id >0){
            amtDialog_id_neutalButton =dialogInfo.NeutalButton_id;
        }
        //设置布局
        if (mlayoutResID > 0){
            setView(mlayoutResID);
        }

        if (mView != null){
            if (mlayoutparams == null){
                setView(mView);
            }else{
                setView(mView,mlayoutparams);
            }
        }

        //设置标题
        if (title != null ) {
            setTitle(title);
        }
        //设置副标题
        if (subtitle != null){
            setSubTitle(subtitle);
        }
        //设置标题颜色
        if (titleColor >0 && title != null) {
            setTitleColor(title,titleColor);
        }
        //设置副标题颜色
        if ( subtitleColor >0 && subtitle != null) {
            setTitleColor(title,titleColor);
        }
        //设置内容
        if (errorcontent != null) {
            setContent(errorcontent);
        }
        //设置内容的颜色
        if (contextColor != 0) {
            setContentColor(contextColor);
        }
        //设置PositiveButton 按钮文本
        if (positiveButtonText != null) {
            setPositiveButtonText(positiveButtonText,positiveButtonVisible);
        }
        //设置 NegativeButton 按钮文本
        if (negativeButtonText != null) {
            setNegativeButtonText(negativeButtonText,negativeButtonVisible);
        }
        //设置NeutralButton 按钮文本
        if (neutralButtonText != null) {
            setNeutalButtonText(neutralButtonText,neutralButtonVisible);
        }
        //设置是否需要自动关闭
        if (autoColseMs >0) {
            setAutoColse(autoColseMs);
        }
        //设置缩放比例
        if (width >0 || height >0) {
            setDialogSize(width, height);
        }
        //设置是否是系统级Dialog
        if (systemDialog) {
            setSystemDialog(true);
        }
        //默认关闭  无需多余动作
        setPositiveButtonListener(amtlistener, positiveButtonDefaultColse);

        setNegativeButtonListener(amtlistener, negativeButtonDefaultColse);

        setNeutralButtonListener(amtlistener, neutralButtonDefaultColse);


    }


}
