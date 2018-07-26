package com.amt.app.view;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.amt.utils.ALOG;
import com.android.smart.terminal.iptvNew.R;

/**
 * 频道号本地UI
 * Created by Kelvin young on 2017/6/1.
 */

public class ChannelNoView {
    private View channelnoview = null;
    private TextView channelNo = null;
    private int dismisstime = 3000;
    private static final int MSG_CHANNEL_SHOW = 1;
    private static final int MSG_CHANNEL_HIDE = 2;

    private Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case MSG_CHANNEL_SHOW:
                    int flag = msg.arg1;
                    ALOG.debug("ChannelNoView>flag : " + flag + " , status : " + msg.arg2);
                    if(flag == 0){
                        mhandler.sendEmptyMessage(MSG_CHANNEL_HIDE);
                    }else{
                        channelNo.setText(msg.arg2+"");
                        channelNo.setTextSize(40);
                        channelNo.setTextColor(Color.GREEN);
                        channelnoview.setVisibility(View.VISIBLE);
                        mhandler.sendEmptyMessageDelayed(MSG_CHANNEL_HIDE,dismisstime);
                    }
                    break;
                case MSG_CHANNEL_HIDE:
                    channelnoview.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    };

    public ChannelNoView(View layout){
        channelnoview = layout.findViewById(R.id.include_channelnoview);
        channelNo = (TextView) channelnoview.findViewById(R.id.channelnumber);
    }

    /**
     * 初始化频道号视图
     * abandon
     */
//    private void initChannelNoView(){
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(560,350);
//        channelnoview.setX(ResolutionHelper.epgWidth-60);
//        channelnoview.setY(50);
//        channelnoview.setLayoutParams(params);
//        channelnoview.requestLayout();
//        channelnoview.invalidate();
//    }

    /**
     * 显示频道号
     * @param channelFlag 0：不使用频道号的本地UI 1：使用频道号的本地UI：保留此属性，
     * 播放器是否显示缺省的本地频道号UI。该属性与nativeUIFlag属性是逻辑与的关系。设置后立即生效。
     * @param channelNum 显示的频道号
     */
    public void setChannelNoFlag(int channelFlag, int channelNum){
        mhandler.removeMessages(MSG_CHANNEL_HIDE);
        Message msg = new Message();
        msg.what = MSG_CHANNEL_SHOW;
        msg.arg1 = channelFlag;
        msg.arg2 = channelNum;
        mhandler.sendMessage(msg);
    }
}
