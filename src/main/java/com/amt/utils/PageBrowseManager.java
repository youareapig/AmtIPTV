package com.amt.utils;

import com.amt.player.AmtMediaPlayer;
import com.amt.player.PlayerMediator;
import com.amt.webview.SyMediaCustom;

/**
 * 记录页面浏览事件管理器
 * Created by zw on 2017/6/9.
 */

public class PageBrowseManager {
    private static PageBrowseManager instance = null;
    private String TAG = "PageBrowseManager";

    private PageBrowseManager() {}

    public static PageBrowseManager getManager() {
        if (instance == null) {
            instance = new PageBrowseManager();
        }
        return instance;
    }

    /**
     * 浏览器回调接口
     * {@link SyMediaCustom#onBroswerMessage(int, String, String, String)}
     * message：loadevent_onunload
     * if (arg1 == 0)
     * 即页面未监听onunload事件，如果页面有小窗口视频或者音乐播放，会造成小视频一直在后台进行播放，不能关闭。
     */
    public void doStopVideo() {
        ALOG.info(TAG, "doStopVideo--->PlayerMediator.mainPlayer: " + PlayerMediator.mainPlayer + ", PlayerMediator.pipPlayer: " + PlayerMediator.pipPlayer);
        if (PlayerMediator.pipPlayer != null) {
            if ((PlayerMediator.pipPlayer.getVideoDisplayMode() != AmtMediaPlayer.FULL_SCREEN || PlayerMediator.pipPlayer.getStrMediaPlayUrl().endsWith(".mp3"))
                    && PlayerMediator.pipPlayer.isStartPlay()) {
                PlayerMediator.pipPlayer.stop();
               ALOG.info(TAG, "page onunload pipPlayer doStopVideo...");
            }
        }

        if (PlayerMediator.mainPlayer != null) {
            if ((PlayerMediator.mainPlayer.getVideoDisplayMode() != AmtMediaPlayer.FULL_SCREEN || PlayerMediator.mainPlayer.getStrMediaPlayUrl().endsWith(".mp3"))
                    && PlayerMediator.mainPlayer.isStartPlay()) {
                PlayerMediator.mainPlayer.stop();
                ALOG.info(TAG, "page onunload mainPlayer doStopVideo...");
            }
        }

    }

}
