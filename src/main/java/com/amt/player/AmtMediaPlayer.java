package com.amt.player;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.amt.amtdata.AmtDataManager;
import com.amt.amtdata.IPTVData;
import com.amt.app.IPTVAvtivityView;
import com.amt.config.Config;
import com.amt.player.entity.LiveChannelHelper;
import com.amt.player.iptvplayer.IPTVPlayer;
import com.amt.player.iptvplayer.IPTVPlayerBase;
import com.amt.player.iptvplayer.IPTVPlayerPIP;
import com.amt.player.systemplayer.SystemMediaPlayer;
import com.amt.utils.ALOG;
import com.amt.utils.NetUtils.HttpUtils;
import com.amt.utils.NetUtils.NetCallback;
import com.amt.utils.ResolutionHelper;
import com.amt.utils.keymap.KeyHelper;
import com.amt.webview.WebViewManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;

/**
 * 播放器
 * @author zw 2017/05/20
 *
 */
public class AmtMediaPlayer {
	/**按 setVideoDisplayArea()中设定的Height,Width,Left,Top 属性所指定的位置和大小来显示视频*/
	public static final int SMALL_PLAY = 0;
	/**全屏显示，按全屏高度和宽度显示(默认值)*/
	public static final int FULL_SCREEN = 1;
	/**按宽度显示，指在不改变原有图像纵横比的情况下按全屏宽度显示*/
	public static final int FIX_WIDTH = 2;
	/**按高度显示，指在不改变原有图像纵横比的情况下按全屏高度显示*/
	public static final int FIX_HEIGHT = 3;
	/**视频显示窗口将被关闭。它将在保持媒体流连接的前提下，隐藏视频窗口。如果流媒体播放没有被暂停，将继续播放音频。*/
	public static final int HIDDEN_DISPLAY = 255;

	private static final String TAG = "AmtMediaPlayer";
	private int nativePlayerInstanceID;
	private int singleOrPlaylistMode;
	private int videoDisplayMode;
	private int videoDisplayLeft;
	private int videoDisplayTop;
	private int videoDisplayWidth;
	private int videoDisplayHeight;
	private int muteFlag;
	private int nativeUIFlag;
	private int muteUIFlag;
	private String mSubTitleColor = "#FFFFFF";
	private int mSubTitleSize = 6;
	private float mAdjustSubTitleDisplayTime;// 外挂字幕偏移量
	private byte mSubTitleDisplay = 1;// 默认显示字幕
	private String mSubTitleUrl;
	boolean isSetSubTitlePostion = false;
	private int audioVolumeUIFlag;
	private int audioTrackUIFlag;
	private int progressBarUIFlag;
	private int channelNoUIFlag;
	private int subtitileFlag;
	private int cycleFlag = 1;
	private String mediaCode;
	private String entryId;
	private int allowTrickmodeFlag;
	private int randomFlag;
	
	public String getStrMediaPlayUrl() {
		ALOG.info(TAG, "getStrMediaPlayUrl--->" + strMediaPlayUrl);
		return strMediaPlayUrl;
	}

	public void setStrMediaPlayUrl(String strMediaPlayUrl) {
		ALOG.info(TAG, "setStrMediaPlayUrl--->" + strMediaPlayUrl);
		this.strMediaPlayUrl = strMediaPlayUrl;
	}
	public int getNativePlayerInstanceID() {
		ALOG.info(TAG, "getNativePlayerInstanceID--->" + nativePlayerInstanceID);
		return nativePlayerInstanceID;
	}
	
	public void setNativePlayerinstanceID(int nativePlayerInstanceID) {
		ALOG.info(TAG, "setNativePlayerinstanceID--->" + nativePlayerInstanceID);
		this.nativePlayerInstanceID = nativePlayerInstanceID;
	}
	
	public int getSingleOrPlaylistMode() {
		ALOG.info(TAG, "getSingleOrPlaylistMode--->" + singleOrPlaylistMode);
		return singleOrPlaylistMode;
	}
	
	public void setSingleOrPlaylistMode(int singleOrPlaylistMode) {
		ALOG.info(TAG, "setSingleOrPlaylistMode--->" + singleOrPlaylistMode);
		this.singleOrPlaylistMode = singleOrPlaylistMode;
	}
	/**
	 * MediaPlayer 对象对应的视频窗口的显示模式。
	 * @returns videoDisplayMode
	 * <li>0：按 setVideoDisplayArea()中设定的Height,Width,Left,Top 属性所指定的位置和大小来显示视频</li>
	 * <li>1：全屏显示，按全屏高度和宽度显示(默认值)</li>
	 * <li>2：按宽度显示，指在不改变原有图像纵横比的情况下按全屏宽度显示</li>
	 * <li>3：按高度显示，指在不改变原有图像纵横比的情况下按全屏高度显示</li>
	 * <li>255：视频显示窗口将被关闭。它将在保持媒体流连接的前提下，隐藏视频窗口。如果流媒体播放没有被暂停，将继续播放音频。</li>
	 */
	public int getVideoDisplayMode() {
		ALOG.info(TAG, "getVideoDisplayMode--->" + videoDisplayMode);
		return videoDisplayMode;
	}

	/**
	 * MediaPlayer 对象对应的视频窗口的显示模式。每次调用该函数后，
	 * 视频显示窗口并不会被立即重新刷新以反映更改后的显示效果只有等到显式调用 refreshVideoDisplay()后才会刷新
	 * @param videoDisplayMode
	 * <li>0：按 setVideoDisplayArea()中设定的Height,Width,Left,Top 属性所指定的位置和大小来显示视频</li>
	 * <li>1：全屏显示，按全屏高度和宽度显示(默认值)</li>
	 * <li>2：按宽度显示，指在不改变原有图像纵横比的情况下按全屏宽度显示</li>
	 * <li>3：按高度显示，指在不改变原有图像纵横比的情况下按全屏高度显示</li>
	 * <li>255：视频显示窗口将被关闭。它将在保持媒体流连接的前提下，隐藏视频窗口。如果流媒体播放没有被暂停，将继续播放音频。</li>
	 */
	public void setVideoDisplayMode(int videoDisplayMode) {
		ALOG.info(TAG, "setVideoDisplayMode--->" + videoDisplayMode);
		this.videoDisplayMode = videoDisplayMode;
	}
	
	public int getVideoDisplayLeft() {
		ALOG.info(TAG, "getVideoDisplayLeft--->" + videoDisplayLeft);
		return videoDisplayLeft;
	}
	
	public void setVideoDisplayLeft(int videoDisplayLeft) {
		ALOG.info(TAG, "setVideoDisplayLeft--->" + videoDisplayLeft);
		this.videoDisplayLeft = videoDisplayLeft;
	}
	
	public int getVideoDisplayTop() {
		ALOG.info(TAG, "getVideoDisplayTop--->" + videoDisplayTop);
		return videoDisplayTop;
	}
	
	public void setVideoDisplayTop(int videoDisplayTop) {
		ALOG.info(TAG, "setVideoDisplayTop--->" + videoDisplayTop);
		this.videoDisplayTop = videoDisplayTop;
	}
	
	public int getVideoDisplayWidth() {
		ALOG.info(TAG, "getVideoDisplayWidth--->" + videoDisplayWidth);
		return videoDisplayWidth;
	}
	
	public void setVideoDisplayWidth(int videoDisplayWidth) {
		ALOG.info(TAG, "setVideoDisplayWidth--->" + videoDisplayWidth);
		this.videoDisplayWidth = videoDisplayWidth;
	}
	
	public int getVideoDisplayHeight() {
		ALOG.info(TAG, "getVideoDisplayHeight--->" + videoDisplayHeight);
		return videoDisplayHeight;
	}
	
	public void setVideoDisplayHeight(int videoDisplayHeight) {
		ALOG.info(TAG, "setVideoDisplayHeight--->" + videoDisplayHeight);
		this.videoDisplayHeight = videoDisplayHeight;
	}

	/**
	 * MediaPlayer 对应的本地播放器实例是否静音
	 * @return muteFlag
	 * <li>0：有声(默认值)</li>
	 * <li>1：静音</li>
	 */
	public int getMuteFlag() {
		ALOG.info(TAG, "getMuteFlag--->" + muteFlag);
		return muteFlag;
	}

	/**
	 * MediaPlayer 对应的本地播放器实例是否静音（ sessionscope， Mute键所触发的MUTE状态为全局MUTE状态，不影响该值），
	 * 该值并不影响 STB 本地其它音频有关应用的 Mute 状态。设置后立即生效。
	 * @param muteFlag
	 * <li>0：设置为有声(默认值)</li>
	 * <li>1：设置为静音</li>
	 */
	public void setMuteFlag(int muteFlag) {
		if (muteFlag != 0 && muteFlag != 1) {
			muteFlag = (byte) (this.muteFlag == 1 ? 0 : 1);
			ALOG.info(TAG, "setMuteFlag--->nVolume:" + getVolume() + "  muteFlag:" + muteFlag);
		}
		amr.setStreamMute(AudioManager.STREAM_MUSIC, muteFlag == 1 ? true : false);
		ALOG.info(TAG, "setMuteFlag--->setStreamMute: " + (muteFlag == 1 ? true : false));
		this.muteFlag = muteFlag;
	}

	/**
	 * 可选的属性：保留此属性，但可以不实现功能播放器是否显示缺省的 NativeUI，如进度条/音量提示/静音提示/频道号 / 等 。
	 * 详细描述参考章节“MediaPlayer 的行为”。设置后立即生效。
	 * @return nativeUIFlag
	 * <li>0：不使用 Player 的本地 UI 显示功能</li>
	 * <li>1：使用 Player 的本地 UI 显示功能(默认值)</li>
	 */
	public int getNativeUIFlag() {
		ALOG.info(TAG, "getNativeUIFlag--->" + nativeUIFlag);
		return nativeUIFlag;
	}

	/**
	 * 可选的属性：保留此属性，但可以不实现功能播放器是否显示缺省的 NativeUI，如进度条/音量提示/静音提示/频道号 / 等 。
	 * 详细描述参考章节“MediaPlayer 的行为”。设置后立即生效。
	 * @param nativeUIFlag
	 * <li>0：不使用 Player 的本地 UI 显示功能</li>
	 * <li>1：使用 Player 的本地 UI 显示功能(默认值)</li>
	 */
	public void setNativeUIFlag(int nativeUIFlag) {
		ALOG.info(TAG, "setNativeUIFlag--->" + nativeUIFlag);
		this.nativeUIFlag = nativeUIFlag;
	}

	/**
	 * （可选）：保留此属性，但可以不实现功能播放器是否显示缺省的本地静音提示 UI。
	 * 该属性与 nativeUIFlag 属性是逻辑与的关系。设置后立即生效。
	 * @return muteUIFlag
	 * <li>0：不使用静音提示的本地 UI 显示功能</li>
	 * <li>1：使用静音提示的本地 UI 显示功能（默认值）</li>
	 */
	public int getMuteUIFlag() {
		ALOG.info(TAG, "getMuteUIFlag--->" + muteUIFlag);
		return muteUIFlag;
	}

	/**
	 * （可选）：保留此属性，但可以不实现功能播放器是否显示缺省的本地静音提示 UI。
	 * 该属性与 nativeUIFlag 属性是逻辑与的关系。设置后立即生效。
	 * @param muteUIFlag
	 * <li>0：不使用静音提示的本地 UI 显示功能</li>
	 * <li>1：使用静音提示的本地 UI 显示功能（默认值）</li>
	 */
	public void setMuteUIFlag(int muteUIFlag) {
		ALOG.info(TAG, "setMuteUIFlag--->" + muteUIFlag);
		this.muteUIFlag = muteUIFlag;
	}

	/**
	 * （可选）：保留此属性，但可以不实现功能播放器是否显示缺省的本地音量调节 UI。
	 * 该属性与 nativeUIFlag 属性是逻辑与的关系。设置后立即生效。
	 * @return audioVolumeUIFlag
	 *<li>0：不使用音量调节的本地 UI 显示功能</li>
	 *<li>1：使用音量调节的本地 UI 显示功能（默认值）</li>
	 */
	public int getAudioVolumeUIFlag() {
		ALOG.info(TAG, "getAudioVolumeUIFlag--->" + audioVolumeUIFlag);
		return audioVolumeUIFlag;
	}

	/**
	 * （可选）：保留此属性，但可以不实现功能播放器是否显示缺省的本地音量调节 UI。
	 * 该属性与 nativeUIFlag 属性是逻辑与的关系。设置后立即生效。
	 * @param audioVolumeUIFlag
	 * <li>0：不使用音量调节的本地 UI 显示功能</li>
	 * <li>1：使用音量调节的本地 UI 显示功能（默认值）</li>
	 */
	public void setAudioVolumeUIFlag(int audioVolumeUIFlag) {
		ALOG.info(TAG, "setAudioVolumeUIFlag--->" + audioVolumeUIFlag);
		this.audioVolumeUIFlag = audioVolumeUIFlag;
	}
	/**
	 * （可选）：保留此属性，但可以不实现功能播放器是否显示缺省的本地音轨选择 UI。
	 * 该属性与 nativeUIFlag 属性是逻辑与的关系。设置后立即生效。
	 * @returns audioTrackUIFlag
	 * <li>0：不使用音轨选择的本地 UI 显示功能</li>
	 * <li>1：使用音轨选择的本地 UI 显示功能（默认值）</li>
	 */
	public int getAudioTrackUIFlag() {
		ALOG.info(TAG, "getAudioTrackUIFlag--->" + audioTrackUIFlag);
		return audioTrackUIFlag;
	}

	/**
	 * （可选）：保留此属性，但可以不实现功能播放器是否显示缺省的本地音轨选择 UI。
	 * 该属性与 nativeUIFlag 属性是逻辑与的关系。设置后立即生效。
	 * @param audioTrackUIFlag
	 * <li>0：不使用音轨选择的本地 UI 显示功能</li>
	 * <li>1：使用音轨选择的本地 UI 显示功能（默认值）</li>
	 */
	public void setAudioTrackUIFlag(int audioTrackUIFlag) {
		ALOG.info(TAG, "setAudioTrackUIFlag--->" + audioTrackUIFlag);
		this.audioTrackUIFlag = audioTrackUIFlag;
	}
	/**
	 * （可选）：保留此属性，但可以不实现功能播放器是否显示缺省的本地进度条UI。
	 * 该属性与 nativeUIFlag 属性是逻辑与的关系。设置后立即生效。
	 * @returns progressBarUIFlag
	 * <li>0：不使用进度条的本地 UI 显示功能</li>
	 * <li>1：使用进度条的本地 UI 显示功能（默认值）</li>
	 */
	public int getProgressBarUIFlag() {
		ALOG.info(TAG, "getProgressBarUIFlag--->" + progressBarUIFlag);
		return progressBarUIFlag;
	}

	/**
	 * （可选）：保留此属性，但可以不实现功能播放器是否显示缺省的本地进度条UI。
	 * 该属性与 nativeUIFlag 属性是逻辑与的关系。设置后立即生效。
	 * @param progressBarUIFlag
	 * <li>0：不使用进度条的本地 UI 显示功能</li>
	 * <li>1：使用进度条的本地 UI 显示功能（默认值）</li>
	 */
	public void setProgressBarUIFlag(int progressBarUIFlag) {
		ALOG.info(TAG, "setProgressBarUIFlag--->" + progressBarUIFlag);
		this.progressBarUIFlag = progressBarUIFlag;
	}
	/**
	 * 可选的属性：保留此属性，但可以不实现功能播放器是否需要显示字幕。设置后立即生效。
	 * @returns subtitileFlag
	 * <li>0：不显示字幕(默认值)</li>
	 * <li>1：显示字幕</li>
	 */
	public int getSubtitileFlag() {
		ALOG.info(TAG, "getSubtitileFlag--->" + subtitileFlag);
		return subtitileFlag;
	}

	/**
	 * 可选的属性：保留此属性，但可以不实现功能播放器是否需要显示字幕。设置后立即生效。
	 * @param subtitileFlag
	 * <li>0：不显示字幕(默认值)</li>
	 * <li>1：显示字幕</li>
	 */
	public void setSubtitileFlag(int subtitileFlag) {
		ALOG.info(TAG, "setSubtitileFlag--->" + subtitileFlag);
		this.subtitileFlag = subtitileFlag;
	}
	/**
	 * 可选属性：设置是否循环播放节目
	 * @returns cycleFlag
	 * <li>0：设置为循环播放</li>
	 * <li>1：设置为单次播放（默认值）</li>
	 */
	public int getCycleFlag() {
		ALOG.info(TAG, "getCycleFlag--->" + cycleFlag);
		return cycleFlag;
	}

	/**
	 * 可选属性：设置是否循环播放节目
	 * @param cycleFlag
	 * <li>0：设置为循环播放</li>
	 * <li>1：设置为单次播放（默认值）</li>
	 */
	public void setCycleFlag(int cycleFlag) {
		ALOG.info(TAG, "setCycleFlag--->" + cycleFlag);
		this.cycleFlag = cycleFlag;
	}

	/**
	 * 获取当前系统音量
	 * @return 返回值：当前系统音量，0-100
	 */
	public int getVolume() {
		int temp = 0;
		int nVol = 0;
		nVol = amr.getStreamVolume(AudioManager.STREAM_MUSIC);
		temp = calcVolume(nVol);
		ALOG.info(TAG, "getVolume--->AudioManager.STREAM_MUSIC: " 
				+ Integer.toString(nVol) + ", MaxVolume: " + nMaxVolume 
				+ ", temp: " + temp);
		return temp;
	}
	
	/**
	 * 系统音量与EPG实际音量转换 
	 * 
	 * @param nVol 系统音量值，一般是0-15或0-25或0-100
	 * @return EPG显示值，如：0-20，0-100
	 */
	private int calcVolume(int nVol) {
		int temp = (int) ((nVol * 100 / nMaxVolume) + 4) / 5 * 5;
		temp = nEpgVolume == 0 ? temp : nEpgVolume;
		ALOG.info(TAG, "calcVolume--->nVol:" + nVol + ",temp: " + temp);
		return temp;
	}

	/**
	 * 设置系统音量
	 * @param volume
	 */
	public void setVolume(int volume) {
		setVolume(volume, true);
	}
	
	private int nMaxVolume;
	private int nEpgVolume;
	public static AudioManager amr;
	
	public void setVolume(int newVal, boolean isShowUI) {
		if (newVal > 100)
			newVal = 100;
		if (newVal < 0)
			newVal = 0;
		nEpgVolume = newVal;
		int nVolume = (int) ((newVal * nMaxVolume) / 100.0);
		if (nMaxVolume == 15) {
			if (newVal <= 0) {
				nVolume = 0;
			} else if (newVal > 0 && newVal <= 5) {
				nVolume = 1;
			} else if (newVal > 5 && newVal <= 10) {
				nVolume = 2;
			} else if (newVal > 10 && newVal <= 15) {
				nVolume = 3;
			} else if (newVal > 15 && newVal <= 20) {
				nVolume = 4;
			} else if (newVal > 20 && newVal <= 30) {
				nVolume = 5;
			} else if (newVal > 30 && newVal <= 35) {
				nVolume = 6;
			} else if (newVal > 35 && newVal <= 40) {
				nVolume = 7;
			} else if (newVal > 40 && newVal <= 50) {
				nVolume = 8;
			} else if (newVal > 50 && newVal <= 55) {
				nVolume = 9;
			} else if (newVal > 55 && newVal <= 60) {
				nVolume = 10;
			} else if (newVal > 60 && newVal <= 70) {
				nVolume = 11;
			} else if (newVal > 70 && newVal <= 75) {
				nVolume = 12;
			} else if (newVal > 75 && newVal <= 85) {
				nVolume = 13;
			} else if (newVal > 85 && newVal <= 90) {
				nVolume = 14;
			} else if (newVal > 90) {
				nVolume = 15;
			}
		}
		// TODO: 2018/03/01 静音状态下修改音量，系统不会重置静音状态，需要我们重置下。目前只考虑播放状态下通过EPG接口设置音量的场景
		if (getMuteFlag() == 1 && nVolume > 0) {
			setMuteFlag(0);
		}
		amr.setStreamVolume(AudioManager.STREAM_MUSIC, nVolume, 0);
		AmtDataManager.putInt(IPTVData.IPTV_Exit_VOLUME, newVal, null);
		ALOG.info(TAG, "setVolume--->STREAM_MUSIC:" + Integer.toString(nVolume) + "  newVal:" + newVal);
	}
	
	public String getMediaCode() {
		ALOG.info(TAG, "getMediaCode--->" + mediaCode);
		return mediaCode;
	}

	public void setMediaCode(String mediaCode) {
		ALOG.info(TAG, "setMediaCode--->" + mediaCode);
		this.mediaCode = mediaCode;
	}

	public String getEntryId() {
		ALOG.info(TAG, "getEntryId--->" + entryId);
		return entryId;
	}

	
	public void setEntryId(String entryId) {
		ALOG.info(TAG, "setEntryId--->" + entryId);
		this.entryId = entryId;
	}
	
	
	private static MediaEventInfo eventInfo;
	private IPTVPlayerBase iptvPlayer;
	private SystemMediaPlayer systemPlayer;
	private Context context;
	private SurfaceView surfaceView;
	private String mediaUrl;
	private String strMediaPlayUrl;
	private boolean useSystemMediaPlayer = false;
	private int currentChannelID = -1;
	private int addProgramId;
	private boolean refreshDisplay = false;
	private IPTVAvtivityView iptvView;
	private Handler mHandler = new Handler();

	public int getAddProgramId() {
		return addProgramId;
	}

	public AmtMediaPlayer(Context context, int addProgramId) {
		this.context = context;
		this.addProgramId = addProgramId;
		init();
	}

	public void setIPTVAvtivityView(IPTVAvtivityView iptvView) {
		this.iptvView = iptvView;
		eventInfo.setIPTVAvtivityView(iptvView);
	}

	/**
	 * 初始化播放器属性参数
	 */
	private void init() {
		ALOG.info(TAG, "init....");
		surfaceView = new SurfaceView(context);
		surfaceView.getHolder().setFormat(android.graphics.PixelFormat.TRANSPARENT);
		surfaceView.setVisibility(View.INVISIBLE);
		eventInfo = MediaEventInfo.getInstance();
		if (Config.IS_PIPPLAYER) {
			iptvPlayer = new IPTVPlayerPIP(addProgramId);
		} else {
			iptvPlayer = new IPTVPlayerBase();
		}
		systemPlayer = new SystemMediaPlayer();
		if (addProgramId == 0) {
			iptvPlayer.setMediaEventInfoListener(eventInfo);
			systemPlayer.setMediaEventInfoListener(eventInfo);
		}
		amr = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		nMaxVolume = amr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}
	
	/**
	 * js扩展接口
	 * @param nativePlayerinstanceID 播放器实例ID，多播放器情况下使用
	 * @param playlistFlag 播放列表
	 * @param videoDisplayMode 视频显示模式：1 ：全屏， 0：小窗口
	 * @param height 小视频窗口高度
	 * @param width 小视频窗口宽度
	 * @param left 小视频窗口位置：左
	 * @param top 小视频窗口宽位置：上
	 * @param muteFlag 是否静音标识： 1：静音， 2：不静音
	 * @param useNativeUIFlag 是否使用播放器本地UI标识：1：使用， 0：不使用
	 * @param subtitleFlag 是否显示字幕标识：1：显示， 2：不显示
	 * @param videoAlpha 视频窗口透明度
	 * @param cycleFlag 视频是否循环播放：1 ：不循环， 其他：循环
	 * @param randomFlag 
	 * @param autoDelFlag
	 */
	
	public void initMediaPlayer(byte nativePlayerinstanceID, byte playlistFlag,
			byte videoDisplayMode, int height, int width, int left, int top,
			byte muteFlag, byte useNativeUIFlag, byte subtitleFlag,
			byte videoAlpha, byte cycleFlag, byte randomFlag, byte autoDelFlag) {
		ALOG.info(TAG, "AmtMediaPlayer: " 
				+ nativePlayerinstanceID + ", "
				+ playlistFlag + ", "
				+ videoDisplayMode + ", "
				+ height + ", "
				+ width + ", "
				+ left + ", "
				+ top + ", "
				+ muteFlag + ", "
				+ useNativeUIFlag + ", "
				+ videoAlpha + ", "
				+ cycleFlag + ", "
				+ randomFlag + ", "
				+ autoDelFlag + ", ");
		setNativePlayerinstanceID(nativePlayerinstanceID);
		setVideoDisplayMode(videoDisplayMode);
		setVideoDisplayHeight(height);
		setVideoDisplayWidth(width);
		setVideoDisplayLeft(left);
		setVideoDisplayTop(top);
		setMuteUIFlag(muteFlag);
		setNativeUIFlag(useNativeUIFlag);
		setSubtitileFlag(subtitleFlag);
		setCycleFlag(cycleFlag);
	}

	/**
	 * 每次调用该函数后，视频显示窗口并不会被立即重新刷新以反映更改后的显示效果只有等到显式调用refreshVideoDisplay()后才会刷新
	 * @param left x
	 * @param top  y
	 * @param width w
	 * @param height h
	 */
	public void setVideoDisplayArea(int left, int top, int width, int height) {
		ALOG.info(TAG, "setVideoDisplayArea--->left: " + left + ", top: " + top + ", w:" + width + ", h:" + height);
		
		videoDisplayLeft = left;
		videoDisplayTop = top;
		videoDisplayWidth = width;
		videoDisplayHeight = height;
		if ((left == 0 && top == 0 && width == 0 && height == 0) 
				|| left > 0
				|| top > 0)
			videoDisplayMode = SMALL_PLAY;
	}
	
	/**
	 * 获取播放器实例,仅供内部使用:
	 * 当useSystemMediaPlayer值为true时，即表示使用系统mediaPlayer播放器
	 * {@link AmtMediaPlayer#useSystemMediaPlayer}
	 * {@link AmtMediaPlayer#checkUrl()}
	 * @return
	 */
	private IPlayerInterface getMediaPlayerInstance() {
		if (systemPlayer == null || iptvPlayer == null) {
			init();
		}
		if (useSystemMediaPlayer) {
			return systemPlayer;
		} else {
			return iptvPlayer;
		}
	}

	/**
	 * 设置单个播放的媒体。传 入 字 符 串 mediaStr 中 的 媒 体 对 象 的mediaURL。
	 * <li>rtsp： //的单播地址，则要求连接单播地址进行播放；</li>
	 * <li>igmp： //的组播地址，要求连接组播地址进行播放；</li>
	 * <li>http： //的地址用于播放 mp3、 WAV 等音频，如 http： //xxxxx/test.mp3、 test.wav</li>
	 * {@link AmtMediaPlayer#mediaUrl}
	 * {@link SystemMediaPlayer#setSingleMedia(String)}
	 * {@link IPTVPlayerPIP#setSingleMedia(String)}
	 * @param mediaUrl 播放地址
	 */
	public void setSingleMedia(String mediaUrl) {
		ALOG.info(TAG, "setSingleMedia : " + mediaUrl);
		this.mediaUrl = mediaUrl;
		try {
			String strJSON = this.mediaUrl;
			if (TextUtils.isEmpty(strJSON) || strJSON.equalsIgnoreCase("undefined")) {
				strMediaPlayUrl = ""; 
				ALOG.info(TAG, "setSingleMedia--->mediaStr is empty or undefined!" );
				return;
			}
			String json = strJSON = strJSON.trim();
			if ((strJSON.startsWith("[") && strJSON.endsWith("]"))) {
				ALOG.info(TAG,"djf  transfer");
				if (strJSON.startsWith("[") && strJSON.endsWith("]")){
					json = strJSON.substring(1, strJSON.length() - 1);
				}
				try {
					JSONObject jsonObj = new JSONObject(json);
					try {
						strMediaPlayUrl = jsonObj.getString("mediaUrl");
						if (TextUtils.isEmpty(strMediaPlayUrl)) {
							strMediaPlayUrl = jsonObj.getString("mediaURL");
						}
					} catch (Exception e) {
					}
					setMediaCode(jsonObj.getString("mediaCode"));
					setEntryId(jsonObj.getString("entryID"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				strMediaPlayUrl = mediaUrl;
			}
			checkUrl();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	boolean returnStatus;
	/**
	 * 检测视频播放地址，HTTP协议使用系统MediaPlayer播放器进行播放，
	 * 其他协议使用MediaControl进行播放，同时将真实播放地址传递给播放器。
	 * {@link AmtMediaPlayer#useSystemMediaPlayer}
	 */
	private void checkUrl() {
		ALOG.info(TAG, "is checkUrl strMediaPlayUrl: " + strMediaPlayUrl);
		if (!TextUtils.isEmpty(strMediaPlayUrl)) {
			useSystemMediaPlayer = false;
			returnStatus = true;
			try {
				URL url = new URL(strMediaPlayUrl);
				if (url.getProtocol().toLowerCase().startsWith("http") && strMediaPlayUrl.indexOf(".m3u8") < 0) {
					if (strMediaPlayUrl.indexOf("RedirectPlay.jsp") > 0) {
						int returnCount = 0;
						do {
							HttpUtils.get302Location(strMediaPlayUrl, new NetCallback() {
								
								@Override
								public void onSuccess(String result) {
									if (result.contains(".mp4") || result.contains(".ts")) {
										returnStatus = false;
										strMediaPlayUrl = result;
									} 
								}
								
								@Override
								public void onFail(String error) {
									returnStatus = false;
									ALOG.info(TAG,"get 302 Location failed：" + error);
								}

								@Override
								public void on302Moved(String location) {
									if (location.contains(".mp4") || location.contains(".ts")) {
										returnStatus = false;
										strMediaPlayUrl = location;
									}
								}
							});
							returnCount ++;
						} while(returnStatus && returnCount <= 5);
					}
					ALOG.info(TAG, "playUrl is " + strMediaPlayUrl + " Now, let's play.");
					useSystemMediaPlayer = true;
					refreshVideoDisplay();
				}
				
			} catch (Exception e) {
				ALOG.info(TAG, e.toString());
			}
			ALOG.info(TAG, "useSystemMediaPlayer: " + useSystemMediaPlayer);
			getMediaPlayerInstance().setSingleMedia(strMediaPlayUrl);
		}
	}
	
	
	/**
	 * 根据videoDisplayMode,vedioDisplayArea 属性, 调整视频的显示。所以设定Area和Mode参
	 * 数后并不是立即生效，而是要在显式调用该函数 后才会生效.
	 */
	public void refreshVideoDisplay() {
		ALOG.info(TAG, "refreshVideoDisplay"
				+ ", displayMode:" + videoDisplayMode
				+ ", x:" + videoDisplayLeft
				+ ", y:" + videoDisplayTop
				+ ", w:" + videoDisplayWidth
				+ ", h:" + videoDisplayHeight);
		final Rect r = new Rect();
		if (videoDisplayMode == SMALL_PLAY) {// 0:窗口模式时
			r.left = (int) videoDisplayLeft;
			r.top = (int) videoDisplayTop;
			r.right = r.left + (int) videoDisplayWidth;
			r.bottom = r.top + (int) videoDisplayHeight;
			if (videoDisplayWidth == 640 && videoDisplayHeight == 530) videoDisplayMode = FULL_SCREEN;
			else if (videoDisplayWidth >= 1200 && videoDisplayHeight >= 700) videoDisplayMode = FULL_SCREEN;
			else if (videoDisplayWidth == 640 && videoDisplayHeight == 510) videoDisplayMode = FULL_SCREEN;
			else if (videoDisplayWidth == 639 && videoDisplayHeight == 529) videoDisplayMode = FULL_SCREEN;
		}
		//传递窗口位置给底层
		WebViewManager.getManager().getCurrentWebview().getCustom().setVideoPosition(videoDisplayLeft, videoDisplayTop, videoDisplayWidth, videoDisplayHeight);
		getMediaPlayerInstance().setSurfaceView(surfaceView);
		calcVideoWindow(r);
		refreshDisplay = true;
	}

	/**
	 * 计算视频窗口缩放大小
	 * @param r
	 */
	private void calcVideoWindow(Rect r) {
		if (r.width() == 0 && r.height() == 0) {
			setVideoWindow(0, 0, 0, 0);
		}
		Rect defaultr=new Rect();
		defaultr.bottom=r.bottom;
		defaultr.right=r.right;
		defaultr.top=r.top;
		defaultr.left=r.left;
		if (videoDisplayMode == FULL_SCREEN) {
			setVideoWindow(0, 0, ResolutionHelper.screenWidth, ResolutionHelper.screenHeight);
			if (!useSystemMediaPlayer) {
				if(IPTVPlayer.isSoftFit()){
					setIptvPlayerWindow(0, 0, getVideoWidthPixels(), getVideoHeightPixels());
				}else{
					setIptvPlayerWindow(0, 0, ResolutionHelper.screenWidth, ResolutionHelper.screenHeight);
				}
			}
		} else if (videoDisplayMode == SMALL_PLAY) {
			Rect rect = ResolutionHelper.calcVideoResolutionSurace(r);
			setVideoWindow(rect.left, rect.top, rect.width(), rect.height());
			if (!useSystemMediaPlayer) {
				Rect videoRect = ResolutionHelper.calcVideoResolutionPixles(defaultr);
				setIptvPlayerWindow(videoRect.left, videoRect.top, videoRect.width(), videoRect.height());
			}
		} else if (videoDisplayMode == HIDDEN_DISPLAY) {
			setVideoWindow(0, 0, 0, 0);
		}
		iptvPlayer.setVideoMode(videoDisplayMode);
	// TODO: 2017/5/25  
//		if (!TextUtils.isEmpty(mSubTitleUrl) && mSubTitleDisplay == 1) {
//			SyIptv.srtSubtitle.Start();
//		}
	}
	
	/**
	 * 要求终端访问指定的频道，并立即返回。对由本地设置为跳过的频道，也返回-1。
	 * 频道地址为通过 CTCSetConfig 设置的频道列表中的地址：
	 * <li>如频道地址为 igmp： //的组播地址，则加入组播频道，播放器开始播放组播频道，并处理相应的时移等功能；</li>
	 * <li>如频道地址为 rtsp： //的单播地址，则连接单播频道，播放器开始播放；</li>
	 * <li>如频道地址为 http： //的地址，则浏览器直接发起请求，访问该页面。</li>
	 * 【注】：在加入一个频道之前已经加入另外一个频道，需先调用 leaveChannel 方法离开前一个频道
	 * {@link IPTVPlayerPIP#joinChannel(int)}}
	 * @param userChannelID
	 * @return
	 * <li>0，表示成功；</li>
	 * <li>-1：表示频道号无效。</li>
	 */
	public int joinChannel(final int userChannelID) {
		//20180301 add by wenzong 如果切台时一切台键一直按下的，则等待按键抬起后再真正切台
		int[] iskeydown = KeyHelper.isKeyDown();
		if(iskeydown[0]==1 &&(iskeydown[1] == KeyEvent.KEYCODE_DPAD_UP
				||iskeydown[1] == KeyEvent.KEYCODE_DPAD_DOWN
				||iskeydown[1] == KeyHelper.getAndroidKeyCode("CHANNEL_ADD")
				||iskeydown[1] == KeyHelper.getAndroidKeyCode("CHANNEL_MIN"))
				&& Config.CHANNEL_MODE != Config.CHANNEL_MODE_SEASONABLE){
			ALOG.info(TAG,"joinChannel >> "+userChannelID+". The channel key is down..Waiting for keyup...");
			KeyHelper.addRunnableWhenKeyUp("joinchannel",new Runnable() {
				@Override
				public void run() {
					ALOG.info(TAG,"OnkeyUp!!! call handleJoinChannel!!! >>"+userChannelID);
					handleJoinChannel(userChannelID);
				}
			});
			return -1;
		}
		return handleJoinChannel(userChannelID);
	}

	private int handleJoinChannel(int userChannelID){
		ALOG.info(TAG, "handleJoinChannel userChannelID: " + userChannelID);
		if (!refreshDisplay) {
			refreshVideoDisplay();
		}
		currentChannelID = userChannelID;
		useSystemMediaPlayer = false;
		getMediaPlayerInstance().joinChannel(userChannelID);
		LiveChannelHelper.getInstance().setLastChannelID(userChannelID);
		return 0;
	}

	/**
	 * 要求终端离开指定的频道，并立即返回。
	 * <li>如原频道地址为 igmp： //的组播地址，则立即离开组播频道，播放器停止播放组播频道，并断开相应的时移连接；</li>
	 * <li>如频道地址为 rtsp： //的单播地址，则断开单播频道，播放器停止播放；</li>
	 * <li>如频道地址为 http： //的地址，则浏览器不作操作。</li>
	 * 【注】：本方法只用于离开通过 joinChannel 方法加入的频道。
	 * {@link IPTVPlayerPIP#leaveChannel()}}
	 * @return
	 * <li>0，表示成功；</li>
	 * <li>-1：表示频道号无效。</li>
	 */
	public boolean leaveChannel() {
		//20180301 add by wenzong 如果切台时一切台键一直按下的，则等待按键抬起后再真正切台
		int[] iskeydown = KeyHelper.isKeyDown();
		if(iskeydown[0]==1 &&(iskeydown[1] == KeyEvent.KEYCODE_DPAD_UP
				||iskeydown[1] == KeyEvent.KEYCODE_DPAD_DOWN
				||iskeydown[1] == KeyHelper.getAndroidKeyCode("CHANNEL_ADD")
				||iskeydown[1] == KeyHelper.getAndroidKeyCode("CHANNEL_MIN"))
				&& Config.CHANNEL_MODE == Config.CHANNEL_MODE_NORMOL){
			ALOG.info(TAG,"leaveChannel >> The channel key is down..Waiting for keyup...");
			KeyHelper.addRunnableWhenKeyUp("leavechannel",new Runnable() {
				@Override
				public void run() {
					ALOG.info(TAG,"OnkeyUp!!! call handleLeaveChannel!!!");
					handleLeaveChannel();
				}
			});
			return false;
		}
		return handleLeaveChannel();
	}

	private boolean handleLeaveChannel(){
		ALOG.info(TAG, "handleLeaveChannel: " + currentChannelID);
		if (useSystemMediaPlayer) {
			stop();
			return true;
		}
		//传递窗口位置给底层
		WebViewManager.getManager().getCurrentWebview().getCustom().setVideoPosition(0, 0, 0, 0);
		getMediaPlayerInstance().leaveChannel();
		refreshDisplay = false;
		return true;
	}

	/**
	 * 播放
	 */
	public void play() {
		playFromStart();
		
	}
	
	/**
	 * 点播播放接口
	 * {@link IPTVPlayerPIP#play() SystemMediaPlayer#play()}}
	 */
	public void playFromStart() {
		ALOG.info(TAG, "playFromStart, play url: " + strMediaPlayUrl);
		// TODO: 2017/5/25  
//		if (useSystemMediaPlayer) {
//			//四川电信强插广告需求相关代码
//			if (strMediaPlayUrl.indexOf("ADDone=1") < 0 && videoDisplayMode == 1) {
//				int hasVod = Advertisement.getResult();
//				if (hasVod == 1) {
//					String playUrl = Advertisement.getADPPlayUrl();
//					ALOG.info(TAG, "play AD url--->" + playUrl);
//					if (!TextUtils.isEmpty(playUrl)) {
//						Message urlMsg = new Message();
//						urlMsg.what = SyIptv.MSG_LOAD_URL;
//						urlMsg.obj = playUrl;
//						SyIptv.mHandler.sendMessage(urlMsg);
//						return;
//					}
//				}
//			}
//		}
		if(TextUtils.isEmpty(strMediaPlayUrl)){
			ALOG.info(TAG,"play url is null .do nothing...");
			return ;
		}
		getMediaPlayerInstance().play();
	}

	/**
	 * 暂停播放
	 */
	public void pause() {
		ALOG.info(TAG, "pause.");
		getMediaPlayerInstance().pause();
		
	}

	/**
	 * 停止播放
	 */
	public void stop() {
		//20180301 add by wenzong 如果切台时一切台键一直按下的，则等待按键抬起后再真正切台
		int[] iskeydown = KeyHelper.isKeyDown();
		if(iskeydown[0]==1 &&(iskeydown[1] == KeyEvent.KEYCODE_DPAD_UP
				||iskeydown[1] == KeyEvent.KEYCODE_DPAD_DOWN
				||iskeydown[1] == KeyHelper.getAndroidKeyCode("CHANNEL_ADD")
				||iskeydown[1] == KeyHelper.getAndroidKeyCode("CHANNEL_MIN"))
				&& Config.CHANNEL_MODE != Config.CHANNEL_MODE_SEASONABLE){
			ALOG.info(TAG,"stop >>The channel key is down..Waiting for keyup...");
			KeyHelper.addRunnableWhenKeyUp("stop",new Runnable() {
				@Override
				public void run() {
					ALOG.info(TAG,"OnkeyUp!!! call handleStop()!!!");
					handleStop();
				}
			});
			return ;
		}
		handleStop();
	}

	private void handleStop(){
		ALOG.info(TAG, "handleStop.");
		getMediaPlayerInstance().stop();
		//20171218 modify by wenzong 增加getVideoDisplayMode判断，非全屏下，调用stop接口，都隐藏surface
		ALOG.info(TAG,"getVideoDisplayMode : "+getVideoDisplayMode());
		if (useSystemMediaPlayer || getVideoDisplayMode() != 1) {
			surfaceManager(false);
		}
		if(iptvView!=null){
			iptvView.showShiftImage(false);
		}
		useSystemMediaPlayer = false;
		refreshDisplay = false;
		strMediaPlayUrl = null;
		cycleFlag = 1;
		//传递窗口位置给底层
		WebViewManager.getManager().getCurrentWebview().getCustom().setVideoPosition(0, 0, 0, 0);
	}

	/**
	 * 当前视频是否暂停
	 * @return
	 */
	public boolean isPause() {
		boolean isPause = getMediaPlayerInstance().isPause();
		ALOG.info(TAG, "isPause: " + isPause);
		return isPause;
	}

	/**
	 * 快进接口
	 * @param speed
	 */
	public void fastForward(int speed) {
		ALOG.info(TAG, "fastForward.");
		getMediaPlayerInstance().fastForward(speed);
		
	}

	/**
	 * 快退接口
	 * @param speed
	 */
	public void fastRewind(int speed) {
		ALOG.info(TAG, "fastRewind.");
		getMediaPlayerInstance().fastRewind(speed);
		
	}

	/**
	 * seek接口
	 * @param type
	 * @param timestamp seek时间段
	 * @param speed 播放速度
	 */
	public void playByTime(int type, String timestamp, int speed) {
		ALOG.info(TAG, "playByTime, type: " + type + ", timestamp: " + timestamp + ", speed: " + speed);
		getMediaPlayerInstance().playByTime(type, timestamp);
		
	}

	/**
	 * 重新播放
	 */
	public void rePlay() {
		ALOG.info(TAG, "rePlay.");
		getMediaPlayerInstance().rePlay();
	}

	/**
	 * 恢复播放
	 */
	public void resume() {
		ALOG.info(TAG, "resume.");
		getMediaPlayerInstance().resume();
	}

	/**
	 * 获取视频当前播放时间
	 */
	public String getCurrentPlayTime() {
		String currentPlayTime  = getMediaPlayerInstance().getCurrentPlayTime();
		ALOG.info(TAG, "getCurrentPlayTime: " + currentPlayTime);
		return currentPlayTime;
	}

	/**
	 * 获取视频总时间
	 * @return
	 */
	public int getMediaDuration() {
		int mediaDurationTime  = getMediaPlayerInstance().getMediaDuration();
		ALOG.info(TAG, "getMediaDuration: " + mediaDurationTime);
		return mediaDurationTime;
	}

	/**
	 * 当 STB 播放器的 playbackmode 发生改变的时候以虚拟键及事件的方式通知 EPG
	 * @return
	 * 播放器的当前播放模式。返回值为JSON 字符串，其中至少包括“播放模式”和“模式相关参数”两类信息，
	 * 播放模式 分 ： NormalPlay ， Pause ，Trickmode；当模式为 Trickmode 时
	 * 必须带2x/-2x,4x/-4x,8x/-8x,16x/-16x,32x/-32x 参数来表示快进/快退的速度参数，
	 * 如：{PlayMode： “NormalPlay”,Speed： “1x”}
	 */
	public String getPlaybackMode() {
		String playbackMode  = getMediaPlayerInstance().getPlaybackMode();
		ALOG.info(TAG, "getPlaybackMode: " + playbackMode);
		return playbackMode;
	}

	/**
	 * 跳到媒体起始点播放
	 */
	public void gotoStart() {
		ALOG.info(TAG, "gotoStart.");
		getMediaPlayerInstance().gotoStart();
	}

	/**
	 * 跳到媒体末端播放
	 */
	public void gotoEnd() {
		ALOG.info(TAG, "gotoEnd.");
		getMediaPlayerInstance().gotoEnd();
		
	}
	
	public boolean isStartPlay() {
		boolean isStartPlay = getMediaPlayerInstance().isStartPlay();
		ALOG.info(TAG, "isStartPlay: " + isStartPlay);
		return isStartPlay;
	}

	/**
	 * 判断当前是否是直播
	 * @return
	 */
	public boolean isLivePlay() {
		//useSystemMediaPlayer = false;
		boolean isLivePlay = getMediaPlayerInstance().isLivePlay();
		ALOG.info(TAG, "isLivePlay: " + isLivePlay);
		return isLivePlay;
	}
	
	public void switchAudioChannel() {
		ALOG.info(TAG, "switchAudioChannel.");
		//useSystemMediaPlayer = false;
		getMediaPlayerInstance().switchAudioChannel();
	}
	
	public void switchAudioTrack(int id) {
		ALOG.info(TAG, "switchAudioTrack: " + id);
		//useSystemMediaPlayer = false;
		getMediaPlayerInstance().switchAudioTrack(id);
	}

	public String getAllAudioTrackInfo() {
		String audioInfo = iptvPlayer.getAllAudioTrackInfo();
		ALOG.info(TAG, "getAllAudioTrackInfo: " + audioInfo);
		return audioInfo;
	}
	
	public void switchSubtitle() {
		ALOG.info(TAG, "switchSubtitle.");
		//useSystemMediaPlayer = false;
		getMediaPlayerInstance().switchSubtitle();
		
	}
	
	
	public int getChannelNum() {
		ALOG.info(TAG, "getChannelNum---->" + currentChannelID);
		return currentChannelID;
	}

	/**
	 * 释放播放器
	 * @param nativePlayerInstanceID
	 * @return
	 */
	public int releaseMediaPlayer(int nativePlayerInstanceID) {
		ALOG.info(TAG, "releaseMediaPlayer.");
		getMediaPlayerInstance().stop();
		if (nativePlayerInstanceID == 0) {
			iptvPlayer.releaseMediaPlayer(nativePlayerInstanceID);
			systemPlayer.releaseMediaPlayer(nativePlayerInstanceID);
		}
		useSystemMediaPlayer = false;
		refreshDisplay = false;
		strMediaPlayUrl = null;
		cycleFlag = 1;
		surfaceManager(false);
		return 0;
	}
	
	private HashMap<String, Integer> hm = new HashMap<String, Integer>();
	
	/**
	 * ioStr: 要设置的参数 的名称。（具体意义由机 顶盒定义） wrStr：要设置的参 数的值。 可选的属性：保留此属性，但 可以不实现功能
	 * 未来作为扩展使用。
	 * 
	 * @param ioStr
	 * @param wrStr
	 */
	
	
	public void set(String ioStr, int wrStr) {
		ALOG.info(TAG, "ioStr: " + ioStr + ", wrStr: " + wrStr);
		if ("HaveorNoVoice".equals(ioStr)) {
			if (wrStr == 1) {
				iptvPlayer.setProgramAudioMute(true);
			} 
		}
		hm.put(ioStr, wrStr);
	}

	/**
	 * ioStr: 要读取的参数 的名称。（具体意义由机 顶盒定义） 可选的属性：保留此属性，但 可以不实现功能 未来作为扩展使用。
	 * 
	 * @param ioStr
	 * @return
	 */
	
	public Object get(String ioStr) {
		if (ioStr != null) {
			ALOG.info(TAG, "get--->" + hm.get(ioStr));
			return hm.get(ioStr);
		}
		return "";
	}
	
	public String getCurrentAudioChannel() {
		return iptvPlayer.getCurrentAudioChannel();
	}

	public void setIptvPlayerWindow(int x, int y, int w, int h) {
		if(iptvPlayer != null){
			if (x < 0 || y < 0 || w < 0 || h < 0) {
				ALOG.info(TAG, "the window is inivalied!");
				return;
			}
			iptvPlayer.setVideoWindow( x, y, w, h);
		}
	}
	
	public int getPlayState() {
		if(iptvPlayer != null){
			return iptvPlayer.getPlayState();
		}else{
			return -1;
		}
	}
	
	public void setVideoMode(int mode) {
		if(iptvPlayer != null){
			iptvPlayer.setVideoMode(mode);
		}
	}
	
	public boolean canUseIGMP() {
		if(iptvPlayer != null){
			return iptvPlayer.canUseIGMP();
		}else{
			return false;
		}
	}
	
	public boolean getShiftStatus() {
		if(iptvPlayer != null){
			return iptvPlayer.getShiftStatus();
		}else{
			return false;
		}
	}
	
	/***
	 * 功能说明：设置外挂字幕路径并下载字幕文件，放在内存中。 url：http://格式的外挂字幕文件路径
	 * 
	 * @param subTitleUrl
	 */
	public void setSubtitleURL(String subTitleUrl) {
		ALOG.info("setSubTitleURL:" + subTitleUrl);
		mSubTitleUrl = subTitleUrl;
		if (!TextUtils.isEmpty(mSubTitleUrl) && mSubTitleDisplay == 1) {
			// TODO: 2017/5/25
//			SrtView.isRun = false;
//			if (SyIptv.srtSubtitle != null)
//				SyIptv.srtSubtitle.Start();
		}
	}

	public String getSubTitleURL() {
		// Utils.info("getSubTitleURL:" + mSubTitleUrl);
		return mSubTitleUrl;
	}
	/***
	 * 外挂字幕: 1为显示，0为隐藏。默认显示
	 * 
	 * @param subTitleDisplay
	 */
	public void setSubtitleDisplay(byte subTitleDisplay) {
		mSubTitleDisplay = subTitleDisplay;

		ALOG.info("setSubTitleDisplay:" + subTitleDisplay);
		// TODO: 2017/5/25
//		if (SyIptv.srtSubtitle != null)
//			if (mSubTitleDisplay == 1) {
//				SyIptv.srtSubtitle.Start();
//			} else
//				SyIptv.srtSubtitle.Stop();
	}

	/***
	 * 外挂字幕: 1为显示，0为隐藏。默认显示
	 * 
	 */
	public byte getSubTitleDisplay() {

		return mSubTitleDisplay;
	}
	/***
	 * 设置外挂字幕颜色,取值：#000000--#FFFFFF
	 * 
	 * @param subTitleColor
	 */
	public void setSubtitleFontColor(String subTitleColor) {
		ALOG.info(TAG, "setSubTitleFontColor:" + subTitleColor);
		mSubTitleColor = subTitleColor;
		// TODO: 2017/5/25
//		if (SyIptv.srtSubtitle != null) {
//			SyIptv.srtSubtitle.setFontColor();
//		}
	}

	/***
	 * 获取外挂字幕颜色，默认为FFFFFF
	 */
	public int getSubTitleFontColor() {
		ALOG.info(TAG, "getSubTitleFontColor:" + mSubTitleColor);
		return Color.parseColor(mSubTitleColor);
	}
	
	/***
	 * 设置外挂字幕大小 默认值6 范围4~8，超过范围容许不予响应。
	 * 
	 * @param subTitleSize
	 */
	public void setSubtitleFontSize(int subTitleSize) {
		ALOG.info("setSubTitleFontSize:" + subTitleSize);
		if (subTitleSize < 4 || subTitleSize > 8)
			return;
		mSubTitleSize = subTitleSize;
		// TODO: 2017/5/25
//		if (SyIptv.srtSubtitle != null) {
//			SyIptv.srtSubtitle.setFontSize();
//		}
	}

	/***
	 * 获取外挂字幕大小 默认值6
	 */
	public int getSubTitleFontSize() {
		int size = 0;
		if (!isSetSubTitlePostion) {
			// 获取视频标准字体， 定位视频高度的1/15
			if (videoDisplayMode == SMALL_PLAY) {
				size = videoDisplayHeight / 15;
			}

		} else {
			// TODO: 2017/5/25
			//size = SyIptv.nEpgHeight / 15;
		}
		int Zoom = mSubTitleSize - 6;// 缩放比例

		if (Zoom > 0) {
			for (int i = 0; i < Zoom; i++) {
				size *= 1.2;
			}
		} else if (Zoom < 0) {
			for (int i = 0; i < Math.abs(Zoom); i++) {
				size /= 1.2;
			}
		}
		ALOG.info("getSubTitleFontSize:" + size + "px");
		return size;
	}
	
	/***
	 * 功能说明：设置外挂字幕位置。 参数x ，y：位置坐标 取值范围：0<x<screenWidth/2，0<y<screenHeight
	 * 这里的xy坐标应该是字幕显示区域的左上角坐标，字幕居中显示 如果字幕过长，支持自动换行
	 * 
	 * @param x
	 * @param y
	 */
	public void setSubtitlePostion(int x, int y) {
		ALOG.info("setSubTitlePostion X:" + x + ",Y:" + y);
		// TODO: 2017/5/25
//		if (x > 0 && x < SyIptv.nEpgWidth / 2 && y > 0 && y < SyIptv.nEpgHeight)
			isSetSubTitlePostion = true;
	}
	
	/***
	 * 是否由EPG设置字幕位置 默认为false
	 * 
	 * @return
	 */
	public boolean IsSetSubTitlePostion() {
		return isSetSubTitlePostion;
	}
	
	/***
	 * 功能说明: 调整外挂字幕字幕显示时间 参数time：以秒为单位，float型，正数表示字幕延迟显示， 负数表示字幕提前显示 (eg:
	 * +0.5延迟显示0.5s，-0.5提前显示0.5s)
	 * 
	 * @param adjustSubTitleDisplayTime
	 */
	public void adjustSubtitleDisplayTime(float adjustSubTitleDisplayTime) {
		ALOG.info("adjustSubTitleDisplayTime" + adjustSubTitleDisplayTime);
		mAdjustSubTitleDisplayTime = adjustSubTitleDisplayTime;
	}

	/***
	 * 获取调整外挂字幕字幕显示时间
	 */
	public float getadjustSubTitleDisplayTime() {
		ALOG.info("getadjustSubTitleDisplayTime" + mAdjustSubTitleDisplayTime);
		return mAdjustSubTitleDisplayTime;
	}
	
	/**
	 * 设置视频播放窗口大小
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	private void setVideoWindow(final int x, final int y, final int w, final int h) {
		ALOG.info(TAG, "setVideoWindow:" + x + " " + y + " " + w + "  " + h + ", nChannel:" + currentChannelID + ", videoDisplayMode:" + videoDisplayMode);
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				if (w == 0 && h == 0) {
					return;
				} else {
					if (surfaceView != null) {
						RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
						if (videoDisplayMode != FULL_SCREEN) {
							lp.setMargins(x, y, 0, 0);
							lp.width = w;
							lp.height = h;
						}
						surfaceView.setLayoutParams(lp);
					}
					//20171020 add by xiewei 确保触发SurfaceHolder回调
					if(useSystemMediaPlayer){
						ALOG.info(TAG, "add mSurfaceHolder");
						surfaceView.getHolder().addCallback(((SystemMediaPlayer)getMediaPlayerInstance()).mSurfaceHolder);
					}else {
						ALOG.info(TAG, "not add mSurfaceHolder");
					}
					surfaceManager(true);
				}
			}
		});
	}
	
	/**
	 * surfaceView 管理
	 * @param flag
	 */
	private void surfaceManager(final boolean flag) {
		if ( surfaceView == null || iptvView == null) {
			ALOG.info(TAG, "surfaceView or iptvView is null");
			return;
		}
		ALOG.info(TAG, "surfaceManager---> flag: " + flag+"  &&surfaceView-->"+surfaceView+" &&visiable-->"+surfaceView.getVisibility()+",useSystemMediaPlayer : "+useSystemMediaPlayer);
		//20171023 modify by wenzong 增加!useSystemMediaPlayer判断。针对系统播放器，在RK芯片上，
		// 有可能surface实际回调比我们添加callback早，所以需要走到下面的对surface控件的操作流程上，保证我们过一定能接收到surface回调
		if (flag && surfaceView.getVisibility()==View.VISIBLE && !useSystemMediaPlayer){
			ALOG.info(TAG, "surfaceView is show");
			return;
		}
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				iptvView.getLayout(IPTVAvtivityView.LYOUT_VIDEO_UI).removeView(surfaceView);
				if (flag) {
					surfaceView.setVisibility(View.VISIBLE);
					iptvView.getLayout(IPTVAvtivityView.LYOUT_VIDEO_UI).addView(surfaceView);
				} else {
					surfaceView.setVisibility(View.INVISIBLE);
					iptvView.getLayout(IPTVAvtivityView.LYOUT_VIDEO_UI).removeView(surfaceView);
				}
			}
		});
	}

	public int getChannelNoUIFlag() {
		ALOG.info(TAG, "getChannelNoUIFlag---> " + channelNoUIFlag);
		return channelNoUIFlag;
	}

	public void setChannelNoUIFlag(int channelNoUIFlag) {
		ALOG.info(TAG, "setChannelNoUIFlag---> " + channelNoUIFlag);
		this.channelNoUIFlag = channelNoUIFlag;
	}

	public int getAllowTrickmodeFlag() {
		ALOG.info(TAG, "getAllowTrickmodeFlag---> " + allowTrickmodeFlag);
		return allowTrickmodeFlag;
	}

	public void setAllowTrickmodeFlag(int allowTrickmodeFlag) {
		ALOG.info(TAG, "setAllowTrickmodeFlag---> " + allowTrickmodeFlag);
		this.allowTrickmodeFlag = allowTrickmodeFlag;
	}

	public int getRandomFlag() {
		ALOG.info(TAG, "getRandomFlag---> " + randomFlag);
		return randomFlag;
	}

	public void setRandomFlag(int randomFlag) {
		ALOG.info(TAG, "setRandomFlag---> " + randomFlag);
		this.randomFlag = randomFlag;
	}
	
	/**
	 * 只需要设置一次即可。
	 * @param obj IPTVPlayer event事件，设置回调类。
	 * {@link MediaEventInfo#sendEvent(int, String)}
	 */
	public void setEventObject(Object obj) {
		iptvPlayer.setEventObject(obj);
	}
	
	/**
	 * 创建播放器，仅用于离开IPTV以后因释放播放器而需再次创建。
	 */
	public void createPlayer() {
		iptvPlayer.createPlayer();
		systemPlayer.initPlayer();
	}

	public int getVideoWidthPixels() {
		if (iptvPlayer != null) {
			return iptvPlayer.getVideoWidthPixels();
 		}
 		return 1280;
	}

	public int getVideoHeightPixels() {
		if (iptvPlayer != null) {
			return iptvPlayer.getVideoHeightPixels();
		}
		return 720;
	}
}
