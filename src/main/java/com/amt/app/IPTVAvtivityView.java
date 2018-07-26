package com.amt.app;

import android.graphics.Rect;
import android.widget.RelativeLayout;

/**
 * Created by DonWZ on 2017/5/12.
 */

public interface IPTVAvtivityView {
    public static final int LYOUT_TOP_UI = 1;
    public static final int LYOUT_WEBVIEW_UI = 2;
    public static final int LYOUT_VIDEO_UI = 3;

    /**
     * 显示/隐藏加载圈
     * @param isShow
     */
    void showLoading(boolean isShow);

    /**
     * 页面尺寸发生变化。需要重设webview设置
     * @param w
     * @param h
     */
    void onPageViewSizeChanged(int w,int h);

    /**
     * 运行JVM游戏APK
     * @param w
     * @param h
     * @param strJad
     * @param strJar
     * @param strParam
     */
    void runGame(int w, int h, String strJad, String strJar, String strParam);

    /**
     * 弹出软键盘
     * @param message 输入框原文本信息
     * @param selectionStart 光标下标起始位置
     * @param selectionEnd 光标下标结束位置
     * @param top 输入框top位置
     */
    void showInputMethod(String message,int selectionStart,int selectionEnd,int top);

    void onPushData(int flag, String arg, String arg2);


    void showShiftImage(boolean isShow);

    void showShift2liveImage(boolean show);

    RelativeLayout getLayout(int id);

    void sendVirtualEvent();

}
