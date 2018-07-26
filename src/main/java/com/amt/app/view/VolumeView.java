package com.amt.app.view;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amt.player.PlayerMediator;
import com.amt.utils.ALOG;
import com.android.smart.terminal.iptvNew.R;

/**
 * 音量条
 * Created by Kelvin young on 2017/5/27.
 */

public class VolumeView {
    private Context context = null;
    private View base = null;
    private FrameLayout volumebaseview = null;
    private ImageView muteview = null;
    private AudioManager am = null;
    private int Maxvolume = 0;
    private int lasttimevolume = 0;//保存的上一次音量值，用于静音恢复
    private int step = 1; //步长
    private static int dismisstime = 5000;//UI消失时间
    private static final int MSG_MUTE = 1;
    private static final int MSG_VOLUM = 2;
    private static final int MSG_HIDE = 3;

    public VolumeView(Context context, View layout){
        base = layout.findViewById(R.id.include_volumeview);
        volumebaseview = (FrameLayout) base.findViewById(R.id.volumebase);
        muteview = (ImageView) base.findViewById(R.id.mute);
        this.context = context;
        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        Maxvolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 通过handler控制音量显示、消失及静音
     */
    private Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mhandler.removeMessages(MSG_HIDE);
            mhandler.sendEmptyMessageDelayed(MSG_HIDE, dismisstime);
            switch (msg.what){
                case MSG_HIDE:
                    mhandler.removeMessages(MSG_HIDE);
                    dismissVolumeUI();
                    if(muteview != null){
                        muteview.setVisibility(View.INVISIBLE);
                    }
                    base.setVisibility(View.INVISIBLE);
                    break;
                case MSG_MUTE:
                    int flag = msg.arg1;
                    ALOG.debug("VolumeView>MuteFlag : " + flag);
                    dismissVolumeUI();
                    if(flag == 0){
                        muteview.setImageResource(R.drawable.muteoff);
                    }else{
                        muteview.setImageResource(R.drawable.muteon);
                    }
                    base.setVisibility(View.VISIBLE);
                    muteview.setVisibility(View.VISIBLE);
                    ALOG.debug("VolumeView>MuteView show");
                    break;
                case MSG_VOLUM:
                    int volume = msg.arg1;
                    ALOG.debug("VolumeView>volume : " + volume);
                    if(volume == 0){
                        setMuteAction(1);
                    }else{
                        showVolumeUI(context,volume);
                    }
                    break;
            }
        }
    };

    /**
     * 初始化volumeview和muteview
     * abandon
     */
//    private void initView(){
//        base.setX(50);
//        base.setY(ResolutionHelper.epgHeight - 50 - 29);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        base.setLayoutParams(params);
//        base.requestLayout();
//        base.invalidate();
//    }

    /**
     * 音量控制
     * @param action  1：声音加 -1：声音减
     */
    public void volumeControl(int action){
        int currentvolume = 0;
        if(PlayerMediator.mainPlayer.getMuteFlag() == 1){
            currentvolume = lasttimevolume;
            am.setStreamMute(AudioManager.STREAM_MUSIC,false);
        }else{
            currentvolume = getCurrentSystemVolume();
        }
        switch (action){
            case 1:
                currentvolume += step;
                break;
            case -1:
                currentvolume -= step;
                break;
        }
        if (currentvolume > (Maxvolume * step)) {
            currentvolume = (Maxvolume * step);
        } else if (currentvolume < 0) {
            currentvolume = 0;
        }
        ALOG.debug("VolumeView>setStreamVolume-->" + currentvolume);
        am.setStreamVolume(AudioManager.STREAM_MUSIC,currentvolume,0);
        lasttimevolume = currentvolume;
        PlayerMediator.mainPlayer.setMuteFlag(lasttimevolume <= 0 ? 1:0);
        Message msg = new Message();
        msg.what = MSG_VOLUM;
        msg.arg1 = lasttimevolume / step;
        mhandler.sendMessage(msg);
    }

    /**
     * 获取当前系统音量
     * @return
     */
    private int getCurrentSystemVolume(){
        int current = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        return current;
    }

    /**
     * 隐藏音量UI
     */
    private void dismissVolumeUI(){
        if(volumebaseview != null){
            volumebaseview.removeAllViews();
            volumebaseview.setVisibility(View.INVISIBLE);
            ALOG.debug("VolumeView>dismissVolumeUI");
        }
    }

    /**
     * 通过muteflag设置静音状态
     * @param muteflag muteflag为0时（默认值）恢复声音，muteflag为1时设置为静音
     */
    public void setMuteAction(int muteflag){
        PlayerMediator.mainPlayer.setMuteFlag(muteflag);
        if(muteflag == 1){
            am.setStreamMute(AudioManager.STREAM_MUSIC, true);
        }else{
            am.setStreamMute(AudioManager.STREAM_MUSIC, false);
        }
        mhandler.removeMessages(MSG_HIDE);
        Message msg = new Message();
        msg.what = MSG_MUTE;
        msg.arg1 = muteflag;
        mhandler.sendMessage(msg);

    }

    /**
     * 静音设置
     */
    public void setMuteAction(){
        setMuteAction(PlayerMediator.mainPlayer.getMuteFlag() == 1 ? 0:1);
    }

    /**
     * 判断是否为静音状态
     * @return
     */
    private boolean isMute(){
        boolean ismute = false;
        if(getCurrentSystemVolume() <= 0){
            ismute = true;
        }
        return ismute;
    }

    /**
     * 显示音量UI
     */
    private void showVolumeUI(Context context, int volume){
        int width = 30;
        int height = 60;
        int leftMargin = 20;
        int txtSize = 30;
        if(muteview != null){
            muteview.setVisibility(View.INVISIBLE);
        }
        /*先清空下之前的视图*/
        dismissVolumeUI();
        TextView text = new TextView(context);
        text.setText(volume + "");
        /*渲染底层空心音量条*/
        LinearLayout layout_background = new LinearLayout(context);
        layout_background.setGravity(Gravity.CENTER | Gravity.LEFT);
        layout_background.setOrientation(LinearLayout.HORIZONTAL);
        for(int i = 0; i < Maxvolume; i++){
            ImageView imagebottom = new ImageView(context);
            imagebottom.setBackgroundColor(Color.parseColor("#cccccc"));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
            params.leftMargin = (i == 0 ? 0 : leftMargin);
            imagebottom.setLayoutParams(params);
            layout_background.addView(imagebottom);
        }
        /*渲染上层实心音量条*/
        LinearLayout layout_upper = new LinearLayout(context);
        layout_upper.setGravity(Gravity.CENTER | Gravity.LEFT);
        layout_upper.setOrientation(LinearLayout.HORIZONTAL);
        for(int j = 0; j < volume; j++){
            ImageView imageupper = new ImageView(context);
            imageupper.setBackgroundColor(Color.parseColor("#00ff00"));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
            params.leftMargin = (j == 0 ? 0 : leftMargin);
            imageupper.setLayoutParams(params);
            layout_upper.addView(imageupper);
        }
        /*渲染音量值*/
        text.setTextColor(Color.GREEN);
        text.setTextSize(txtSize);
        TextPaint tp = text.getPaint();
        tp.setFakeBoldText(true);
        text.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 15;
        layout_background.addView(text,params);

        volumebaseview.addView(layout_background);
        volumebaseview.addView(layout_upper);
        base.setVisibility(View.VISIBLE);
        volumebaseview.setVisibility(View.VISIBLE);
    }
}
