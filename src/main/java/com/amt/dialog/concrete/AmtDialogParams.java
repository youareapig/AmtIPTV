package com.amt.dialog.concrete;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 *  dialog 属性类
 *
 * @author djf
 *
 */
public class AmtDialogParams {

    /**
     * 自定义布局
     */

    public View mview;

    /**
     * 自定义布局ID
     */
    public int mlayoutResID;

    /**
     * 自定义布局属性
     */

    public ViewGroup.LayoutParams mlayoutparams;
    /**
     * {@link Context}
     *
     */
    public Context mContext;
    /**
     * 标题
     */
    public String title;

    /**
     * 副标题
     */
    public String subtitle;

    /**
     * 标题颜色
     */
    public int titleColor;

    /**
     * 标题颜色
     */
    public int subTitleColor;

    /**
     * 内容
     */
    public String content;


    /**
     * 内容颜色
     */
    public int contentColor;


    /**
     *  第一个按钮的文字
     */
    public String PositiveButtonText;

    /**
     * PositiveButton是否可见
     */
    public boolean PositiveVisible;

    /**
     * Positive 是否可点击关闭弹窗
     */
    public boolean PositiveDefaultColse;

    /**
     * 第二个按钮的文字
     */
    public String NegativeButtonText;

    /**
     * NegativeButton是否可见
     */
    public boolean NegativeVisible;

    /**
     * NegativeButton 是否可点击关闭弹窗
     */
    public boolean NegativeDefaultColse;
    /**
     * 第三个按钮的文字
     */
    public String NeutralButtonText;

    /**
     * NeutralButton是否可见
     */
    public boolean NeutralVisible;

    /**
     * NeutralButton 是否可点击关闭弹窗
     */
    public boolean NeutralDefaultColse;
    /**
     *   设置AmtDialog 特殊监听者
     */
    public MyDialogListener myDialogListener = null;

    /**
     * 定时关闭的时间单
     * 位：秒
     *
     */
    public int AutoCloseMs;

    /**
     *
     * 是否能按返回键取消
     */
    public boolean isCanBack;

    /**
     * 设置dialog系统级权限
     */
    public boolean  systemType;

    /**
     * 设置Dialog样式
     * @return
     */
    public int dialogStyle=-1;

    /**
     * Dialog需要缩放尺寸，表示 宽度比例  例：  realsize= screenwidth/ width
     */
    public double width;
    /**
     * Dialog 需要缩放的尺寸，表示 高度比例    realsize= screenheight/ height
     */
    public double height;

/***************自定义按键ID**************/
    /**
     * 自定义布局的titleid
     */
    public int title_id;
    /**
     * 自定义布局的subtitleid
     */
    public int subtitle_id;
    /**
     * 自定义布局的content_id
     */
    public int content_id;

    /**
     * 自定义布局 positive id
     */
    public int PositiveButton_id;

    /**
     * 自定义布局 negativeButton id
     */
    public int NegativeButton_id;

    /**
     * 自定义布局 neutralButton id
     */
    public int NeutalButton_id;
}
