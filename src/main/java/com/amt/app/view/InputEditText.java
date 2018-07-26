package com.amt.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

import com.amt.utils.ALOG;

/**
 * 输入法专用输入框控件，用于在页面上的input标签需要弹出输入法时模拟的本地控件，让输入法依附。
 * Created by DonWZ on 2017/6/5.
 */

public class InputEditText  extends EditText {
    private onKeyBackListener mKeyBackListener;

    public InputEditText(Context context) {
        super(context);
    }

    public InputEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InputEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnKeyBackListener(onKeyBackListener keyBackListener){
        mKeyBackListener = keyBackListener;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            ALOG.info("InputEditText","onKeyPreIme > keyCode:"+keyCode+", hide input method!");
            if(mKeyBackListener!=null){
                return mKeyBackListener.onBack(this);
            }
        }
        return super.onKeyPreIme(keyCode, event);
    }

    /**
     * 监听EditText 输入法的返回键按键事件。
     */
    public interface onKeyBackListener{
        boolean onBack(EditText editText);
    }
}
