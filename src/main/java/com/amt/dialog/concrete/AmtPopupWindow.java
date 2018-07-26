package com.amt.dialog.concrete;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import com.amt.utils.ALOG;
import com.android.smart.terminal.iptvNew.R;

import java.util.Stack;

/**
 * Created by DJF on 2017/6/13.
 */
public class AmtPopupWindow {

    /**
     * 创建Dialog的管理栈，LIFO数据结构来进行管理
     */
    private static Stack<PopupWindow> myDialogStack = new Stack<PopupWindow>();
    private static PopupWindow amtpopupWindow;
    private static String TAG="AmtPopupWindow";
    //设置默认布局
    private static View mContentView=null;
    public static void create(Context mContext){
        ALOG.debug("popUpWindow oncreate");
        LayoutInflater layoutInflater =
                (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContentView = layoutInflater.inflate(R.layout.layout_popupwindow_default, null);
        amtpopupWindow =new PopupWindow(mContentView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        amtpopupWindow.setFocusable(false);
        //多点触控
        amtpopupWindow.setSplitTouchEnabled(false);
        //忽略CheekPress事件
        amtpopupWindow.setIgnoreCheekPress();
        //弹窗外部被点击
        amtpopupWindow.setOutsideTouchable(true);
        amtpopupWindow.setAnimationStyle(R.style.popupAnimation);
        amtpopupWindow.showAtLocation(mContentView, Gravity.BOTTOM| Gravity.RIGHT,0,0);
        //加入堆栈进行管理
        pushSatck(amtpopupWindow);

    }

    /**
     * 关闭AmtPopWindow
     */
    public static void disPopWindos(){
        ALOG.debug("disPopWindos");
        while (!myDialogStack.empty()){
            PopupWindow loaclPopupWindow = getAmtPopupWindow();
            if (loaclPopupWindow != null && loaclPopupWindow.isShowing()){
                loaclPopupWindow.dismiss();
                popSatck();
            }
        }
    }


    /**
     * 压栈
     * @param popupWindow
     */
    private static void pushSatck(PopupWindow popupWindow){
        if (popupWindow != null ) {
            myDialogStack.push(popupWindow);
            ALOG.debug(TAG,"pushSatck-->"+myDialogStack.size()+"  &&    "  +myDialogStack.empty()+"   &&  "+popupWindow);
        }
    }

    /**
     * 获取栈顶的Dialog
     * @return
     */
    public static PopupWindow getAmtPopupWindow(){
        if (!myDialogStack.empty()) {
            PopupWindow popupWindow = myDialogStack.peek();
            ALOG.debug(TAG,"getMyDialog-->"+popupWindow+"  && size-->"+myDialogStack.size());
            return popupWindow;
        }
        return null;
    }
    /**
     * 出栈
     */
    private static void popSatck(){
        if (!myDialogStack.empty()) {
            myDialogStack.pop();
            ALOG.debug(TAG,"popSatck-->"+myDialogStack.size());
        }
    }
}
