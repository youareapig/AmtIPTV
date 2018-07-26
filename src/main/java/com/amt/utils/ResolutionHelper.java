package com.amt.utils;

import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.amt.app.IPTVActivity;
import com.amt.player.iptvplayer.IPTVPlayer;
import com.amt.player.iptvplayer.IPTVPlayerBase;

/**
 * 此类用于计算分辨率及页面、视频缩放比例。
 * IPTV机顶盒坐标系分为三个体系，分别为：
 *<li>1. EPG坐标系：页面分辨率，一般不同页面会有变化，比如标清 650 x 530 ,高清 1280 x 720 等</li>
 *<li>2. OSD坐标系：即屏幕分辨率。通过WindowManager获得的分辨率。</li>
 *<li>3. 视频层坐标系：即视频画面分辨率。系统将视频层单独区别于OSD层，通过在设置APK里设置视频制式分辨率，可调整视频层分辨率</li>
 * 通常RK、HISI芯片，OSD和视频层分辨率都保持一致。而Amlogic芯片的OSD是写到uboot里的， 不可调，视频层是可调的。
 * 所以OSD分辨率和视频层分辨率可能不一样。针对页面拉伸、视频拉伸需要单独处理。
 * <br/>浏览器需要将EPG页面拉伸到适合屏幕显示的分辨率，以EPG和OSD分辨率为基准拉伸。
 * 而通过CTC播放的视频需要以EPG和视频层分辨率为基准拉伸，通过MediaPlayer播放的视频则以EPG和屏幕分辨率为基准拉伸。
 *
 * Created by zw on 2017/6/6.
 */

public class ResolutionHelper {
    public static final String TAG = "ResolutionHelper";
    public static int screenWidth;
    public static int screenHeight;
    public static int epgWidth = 1280;
    public static int epgHeight = 720;
    /**EPG-OSD横轴拉伸比*/
    public static float zoomX;
    /**EPG-OSD纵轴拉伸比*/
    public static float zoomY;
    /**EPG-视频层横轴拉伸比*/
    public static float videoZoomX;
    /**EPG-视频层纵轴拉伸比*/
    public static float videoZoomY;

    /**
     * 计算页面分辨率缩放比例
     */
    public static void calcWebResolution() {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager)IPTVActivity.context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        ALOG.info(TAG, "calcWebResolution---> DisplayMetrics: " + dm.toString()
                + ",  ScreenWidth:" + screenWidth
                + ",  ScreenHeight:" + screenHeight
                + ",  isSoftFit:" + (IPTVPlayer.isSoftFit()));

        zoomX = (float) screenWidth / (float) epgWidth + 0.005f;
        zoomY = (float) screenHeight / (float) epgHeight + 0.005f;
        if (IPTVPlayer.isSoftFit()) {
            videoZoomX = (float) IPTVPlayerBase.getVideoWidthPixels() / epgWidth;
            videoZoomY = (float) IPTVPlayerBase.getVideoWidthPixels() / epgHeight;
        } else {
            videoZoomX = 1.0f;
            videoZoomY = 1.0f;
        }
        IPTVPlayer.setEPGSize(epgWidth, epgHeight);
    }

    /**
     *  计算视频位置(surface控件的拉伸，使用系统分辨率计算拉伸比例)
     * @param rect
     * @return
     */
    public static Rect calcVideoResolutionSurace(Rect rect) {
        int w = rect.width();
        int h = rect.height();
        Rect r = rect;
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager)IPTVActivity.context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        ALOG.info(TAG,"screenWidth : "+screenWidth+", screenHeight : "+screenHeight);
        float zoomSurfaceX = (float) screenWidth / (float) epgWidth ;
        float zoomSurfaceY = (float) screenHeight / (float) epgHeight;
        ALOG.info(TAG, "calcVideoResolutionSurace before--->Rect x:" + r.left + ", y:" + r.top + ", w:" + w + ", h:" + h);
        r.left *= zoomSurfaceX;
        r.top *= zoomSurfaceY;
        w *= zoomSurfaceX;
        h *= zoomSurfaceY;
        r.right = r.left + w;
        r.bottom = r.top + h;
        ALOG.info(TAG, "calcVideoResolutionSurace after--->Rect x:" + r.left + ", y:" + r.top + ", w:" + r.width() + ", h:" + r.height());
        return r;
    }

    /**
     * 计算视频位置(系统视频层分辨率，使用系统芯片提供的视频分辨率计算拉伸比例)
     * @param rect
     * @return
     */
    public static Rect calcVideoResolutionPixles(Rect rect){
        int w = rect.width();
        int h = rect.height();
        Rect r = rect;
        if(IPTVPlayer.isSoftFit()){
            videoZoomX = (float) IPTVPlayerBase.getVideoWidthPixels() / epgWidth;
            videoZoomY = (float) IPTVPlayerBase.getVideoHeightPixels() / epgHeight;
        }else{
            videoZoomX = 1;
            videoZoomY = 1;
        }
        ALOG.info(TAG, "calcVideoResolutionPixles before--->Rect x:" + r.left + ", y:" + r.top + ", w:" + w + ", h:" + h);
        r.left *= videoZoomX;
        r.top *= videoZoomY;
        w *= videoZoomX;
        h *= videoZoomY;
        r.right = r.left + w;
        r.bottom = r.top + h;
        ALOG.info(TAG, "calcVideoResolutionPixles after--->Rect x:" + r.left + ", y:" + r.top + ", w:" + r.width() + ", h:" + r.height());
        return r;
    }


}
