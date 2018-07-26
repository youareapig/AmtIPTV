package com.amt.app;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amt.app.view.InputEditText;
import com.amt.utils.ALOG;
import com.amt.webview.WebViewManager;

/**
 * 输入法操作类，用于页面上有输入框时显示/隐藏输入法。
 * 输入法由浏览器webcore监听焦点是否在input标签上并且点击了确定键来触发的，默认是不弹输入法，完全屏蔽的。
 * 如果需要弹出输入发，在fix里添加代码：AddFixItem(url,"ShowInputMethod"); 其中url是要弹输入法的页面地址。
 * 如果配置AddFixItem("/","ShowInputMethod"); 则代表所有页面都可以弹输入法。
 * Created by DonWZ on 2017/6/5.
 */
public class SoftInptHelper {

    private Context mContext;
    private InputMethodManager inputMethodManager;
    private RelativeLayout mRootLayout;
    private InputEditText mEditText;
    private MyTextChangeListener textChangeListener;

    public SoftInptHelper(Context context,RelativeLayout rootLayout){
        mContext = context;
        mRootLayout = rootLayout;
    }

    public void showInput(String message,int index,int top){
        if(mEditText!=null){
            clearEditText();
        }
        mEditText = new InputEditText(mContext);
        mEditText.setAlpha(0.0f);
//        mEditText.setBackgroundColor(Color.RED);
        RelativeLayout.LayoutParams edtParam = new RelativeLayout.LayoutParams(100,ViewGroup.LayoutParams.WRAP_CONTENT);
        edtParam.topMargin = top;
        mEditText.setLayoutParams(edtParam);
        if(!TextUtils.isEmpty(message)){
            mEditText.setText(message);
            index = index < 0 ? 0 : (index > message.length() ? message.length() : index);
            ALOG.info("showInput > setSelection : "+index);
            mEditText.setSelection(index);
        }
        mRootLayout.addView(mEditText,edtParam);
        textChangeListener = new MyTextChangeListener();
        mEditText.addTextChangedListener(textChangeListener);
        mEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                ALOG.info("InputMethod Text > onEditorAction > actionId:"+actionId);
                if (actionId == 0 || actionId == 6) {//回车
                    showInput(false,mEditText);
                    clearEditText();
                    return true;
                }
                return false;
            }
        });
        mEditText.setOnKeyBackListener(new InputEditText.onKeyBackListener() {
            @Override
            public boolean onBack(EditText editText) {
                showInput(false,mEditText);
                clearEditText();
                return true;//如果这里返回false，有可能导致edittext的最后一个字被删除。要注意。
            }
        });
        mEditText.requestFocus();
        showInput(true,mEditText);
    }

    private void clearEditText(){
        ALOG.info("SoftInptHelper > clearEditText");
        if(mEditText !=null ){
            mEditText.clearFocus();
            mEditText.removeTextChangedListener(textChangeListener);
            textChangeListener = null;
            try{
                mRootLayout.removeView(mEditText);
            }catch(Exception e){
                e.printStackTrace();
            }
            mEditText = null;
        }
    }

    private void showInput(boolean isShow,View mEditText){
        if(isShow ){
            if(inputMethodManager == null){
                inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            }
            inputMethodManager.showSoftInput(mEditText,0);
        }else{
            if(inputMethodManager != null){
                inputMethodManager.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
                inputMethodManager = null;
            }
        }
    }

    class MyTextChangeListener implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            ALOG.info("InputMethod Text > onTextChanged > "+s.toString());
            if(s != null){
                int index = mEditText == null ? 0 : mEditText.getSelectionStart();
                WebViewManager.getManager().updateIputText(s.toString(),index);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
