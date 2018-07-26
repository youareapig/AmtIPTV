package com.amt.app.view;


import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.amt.app.IPTVActivity;
import com.amt.utils.ALOG;
import com.android.smart.terminal.iptvNew.R;


/**
 * 声道本地UI：左声道、右声道、立体声
 * Created by Kelvin young on 2017/6/1.
 */

public class AudioTrackView {
    private View audiotrackview = null;
    private TextView audiotrack = null;
    private static final int MSG_SHOW = 1;
    private static final int MSG_HIDE = 2;
    private int dismisstime = 3000;
    private Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_HIDE:
                    audiotrackview.setVisibility(View.INVISIBLE);
                    break;
                case MSG_SHOW:
                    String audiovalue = msg.obj + "";
                    if(audiovalue.equalsIgnoreCase("Left")){
                        audiovalue = IPTVActivity.context.getString(R.string.iptv_ui_audiotrack_left);
                    }else if(audiovalue.equalsIgnoreCase("Right")){
                        audiovalue = IPTVActivity.context.getString(R.string.iptv_ui_audiotrack_right);
                    }else if(audiovalue.equalsIgnoreCase("Stereo")){
                        audiovalue = IPTVActivity.context.getString(R.string.iptv_ui_audiotrack_stereo);
                    }else if(audiovalue.equalsIgnoreCase("JointStereo")){
                        audiovalue = IPTVActivity.context.getString(R.string.iptv_ui_audiotrack_jointstereo);
                    }
                    if(!TextUtils.isEmpty(audiovalue)){
                        audiotrack.setText(audiovalue);
                        audiotrack.setTextSize(25);
                        audiotrack.setTextColor(Color.GREEN);
                        audiotrackview.setVisibility(View.VISIBLE);
                        mhandler.sendEmptyMessageDelayed(MSG_HIDE,dismisstime);
                    }
                    break;
            }
        }
    };

    public AudioTrackView(View layout){
        audiotrackview = layout.findViewById(R.id.include_audiotrackview);
        audiotrack = (TextView) audiotrackview.findViewById(R.id.audiotrack);
    }

    /**
     * 初始化声道UI
     * abandon
     */
//    private void initAudioTrackView(){
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        audiotrackview.setX(ResolutionHelper.epgWidth-180);
//        audiotrackview.setY(50);
//        audiotrackview.setLayoutParams(params);
//        audiotrackview.requestLayout();
//        audiotrackview.invalidate();
//    }

    /**
     * 设置声道
     * @param straudiotrack
     */
    public void setAudiotrack(String straudiotrack){
        ALOG.debug("AudioTrackView>AudioTrack : " + straudiotrack);
        mhandler.removeMessages(MSG_HIDE);
        Message msg = new Message();
        msg.what = MSG_SHOW;
        msg.obj = straudiotrack;
        mhandler.sendMessage(msg);
    }
}
