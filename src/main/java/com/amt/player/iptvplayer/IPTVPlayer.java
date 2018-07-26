/*
 * Copyright (C) 2011-2012 IPTV Android中间件/终端软件
 *
 * 深圳市速影科技有限公司
 *
 * http://www.softstb.com/
 *
 */

package com.amt.player.iptvplayer;

import android.os.Handler;
import android.view.Surface;
import android.widget.VideoView;

import com.amt.app.IptvApp;
import com.amt.utils.ALOG;

/**
 * 控制ts播放器接口
 * 
 * @author zw
 * 
 */
public class IPTVPlayer {
	public static final int STATE_STOP = 0;
	public static final int STATE_PAUSE = 1;
	public static final int STATE_PLAYING = 2;
	public static final int STATE_FAST = 3;
	public static final int STATE_STOPING = 4;
	public static final int STATE_STARTING = 5;

	public IPTVPlayer() {
		mIsFirstPlay = true;
		mIsNeedGetCanUseIGMP = true;
	}

	public void onPlayReady(int nVideoID, int nAudioID) {
		ALOG.info(
				"onPlayReady",
				String.format("VID:%04X", nVideoID) + String.format(",AID:%04X", nAudioID));
		mIsFirstPlay = false;

	}

	private static boolean isExistPlayer = false;

	// 播放器退出
	public static void close() {
		NvClose();
		isExistPlayer = false;
	}

	protected native void NvSetCurrentChannel(int nChannel, int nFECPort);

	protected native void NvRestorePlay();

	protected native void NvGotoEnd();

	protected native void NvGotoStart();

	protected native int NvLoadAndPlay(String strMediaUrl, String strShiftUrl,
			boolean isLive);

	protected native void NvStop();

	protected native int NvPlay(int nSpeed);

	protected native void NvPause();

	protected native void NvResume();

	protected native int NvSeek(int nTime);

	protected native int NvSetVideoMode(int mode);

	protected native int NvSetVideoWidth();

	protected native int NvSetVideoHeight();

	protected native int NvGetDuration();

	protected native int NvGetCurrentTime();

	protected native int NvGetCurrentTimeMs();

	protected native String NvGetCurrentTimeStr();

	protected native int NvGetPlayState();

	protected native boolean NvSetSurface(Surface surf, String strSurface);

	protected native void NvSetVideoWindow(int x, int y, int w, int h);

	protected native void NvSetEventObject(Object obj);

	protected native static void NvSetVolume(int nVolume);

	protected native static int NvGetVolume();

	protected native static int NvGetAudioBalance();

	protected native static void NvSetAudioBalance(int nBalance);

	protected native void NvLiveSeek(String strTime);

	protected native String NvgetPlaybackMode();

	protected native static void NvSetEPGSize(int w, int h);

	protected native static boolean NvIsSoftFit();

	public static native int NvGetPlayMode();

	protected static native int NvGetVideoWidthPixels();

	protected static native int NvGetVideoHeightPixels();

	protected static native void NvClose();

	protected native void NvCreatePlayer();

	protected native void NvReleasePlayer();

	protected native static String NvDecode(byte[] ucCode);

	protected native void NvleaveChannel();

	protected native static void NvSetUdpHold(boolean udpHold);

	protected native boolean NvGetShiftStatus();

	protected native static void NvSetConfig(String strName, String strValue);

	// 增加对ＦＣＣ的支持 2013-03-05 by zhs
	protected native void NvSetFCCParam(int nChannel, String strFCCHost,
			int nFCCPort);

	protected static native String NvGetLocalHost();

	// 获取音轨信息，格式如下:pid:语言;
	public native static String NvGetAudioPIDs();

	// 获取字幕信息，格式如下:pid:语言;
	public native static String NvGetSubtitlePIDs();

	// 选择音轨，pid:GetAudioPIDs中返回的pid
	public native void NvSwitchAudioTrack(int pid);

	// 选择字幕，pid:GetSubtitlePIDs中返回的pid
	public native void NvSwitchSubtitle(int pid);

	// 组播是否可用
	public native boolean NvCanUseIGMP();

	// 设置B平面状态(是否断开)
	protected native static void NvSetNetStatus(boolean isDisconnect);

	public static native void SetStreamVolume(int type, int nVolume);

	public static native int GetStreamVolume(int type);

	public static native void SetStreamMute(int type, int mute);

	public static native boolean GetStreamMute(int type);

	/**
	 * IPTV通知MediaControl的接口 2016-7-6 liaoyn
	 */
	public static native void NvIPTVReady();

	public native void NvIPTVReadyPIP(int handler);

	/**
	 * IPTV通知MediaControl 参数初始化完成，MediaControl接收到此通知，开始业务流程，启动tr069 2015/10/20
	 */
	public static void setIptvReady() {
		NvIPTVReady();
		ALOG.info("IPTVPlayer--->call MediaControl Iptv is ready...");
	}

	// 设置相关参数
	private static native void NvSetValue(String strName, String strValue);

	// 获取相关参数
	private static native String NvGetValue(String strName, String strValue);

	/**
	 * 向底层设置参数
	 * 
	 * @param strName
	 *            <ul>
	 *            <li>UseUnicast:就是在组播不可用时是否可以使用单播，默认情况下是使用(演示机顶盒)，strValue==
	 *            "0"时不使用(普通机顶盒)，在不使用时会抛出0005错误</li>
	 *            <li>UmountDisk:U盘卸载通知</li>
	 *            <li>Verification:IPTV计数上报</li>
	 *            <li>RunCmd:使用IptvServ执行命令</li>
	 *            </ul>
	 * @param strValue
	 */
	public static void setValue(String strName, String strValue) {
		NvSetValue(strName, strValue);
	}

	/**
	 * 向底层mediacontrol获取数据
	 * 
	 * @param strName
	 * @return
	 */
	public static String getValue(String strName, String strValue) {
		if (!"SECRETKEY".equalsIgnoreCase(strName))
			ALOG.info("IPTVPlayer-->GetValue-->" + strName + ":" + strValue);
		return NvGetValue(strName, strValue);
	}

	private static native void NvSetProperty(int type, int sub, int value);

	private static native void NvSetMediaControlData(String name, String value);

	private static native String NvGetMediaControlData(String name);

	/**
	 * 向MediaControl写入数据。这些数据一般是网管下发的，使用新版网管APK需要调用此接口。add by wz 2017/03/02
	 * 
	 * @param name
	 * @param value
	 */
	public static void setDataToMediacontrol(String name, String value) {
		NvSetMediaControlData(name, value);
	}

	/***
	 * 网管会从MediaControl拿一些实时生成的数据。add by wz 2017/03/02
	 * 
	 * @param name
	 */
	public static String getDataFromMediaControl(String name) {
		return NvGetMediaControlData(name);
	}

	/***
	 * 此接口为libCTC_MediaControl.so底层回调接口，接口原型不能改动！！！
	 * 上报视频性能告警信息，或者其他信息给网管。直接透传数据，不需要进行key转换等。
	 * @param key
	 * @param value
	 */
	public static void SetDataToTr069(String key,String value){
		IptvApp.mTR069.setDataToTr069(key,value);
	}

	/**
	 * 调用芯片的扩展接口
	 * 
	 * @param type
	 * @param sub
	 * @param value
	 *            <ul>
	 *            <li>频道切换方式 type=1,sub=0 value: 0:黑屏模式 1:最后一帧 2:平滑切换</li>
	 *            <li>视频宽高比 type=2,sub=0 value: 0:(16:9) 1:(4:3) 2:视频原始比例 3:全屏比例
	 *            </li>
	 *            <li>杜比透传 type=3,sub=0 value: 0:不支持 1:支持</li>
	 *            </ul>
	 */
	public static void setProperty(int type, int sub, int value) {
		NvSetProperty(type, sub, value);
	}

	protected static int mPlayMode;
	protected VideoView mVideo;
	protected Handler MainHandler;
	protected boolean mIsFirstPlay;
	protected boolean mCanUseIGMP;
	protected boolean mIsNeedGetCanUseIGMP;// 是否需要去获取"组播是否可用"

	/* ========================== start 画中画新增接口 20170525 add by zw ==============================*/

	/**
	 * 获取播放模式
	 * 
	 * @return 0：单窗口 1：画中画
	 */
	protected native int NvGetPlayModePIP();

//	/**
//	 * 创建视频项目（画中画模式）
//	 * 
//	 * @return 新加画中画的id
//	 */
//	protected native int NvAddProgramPIP(int mode);

	/**
	 * 删除一个画中画项目
	 * 
	 * @param ProgNum
	 *            要删除的项目id，必须要想先stop
	 * @return
	 */
	protected native int NvDelProgramPIP(int ProgNum);

	/**
	 * 获取当前有焦点的画中画
	 * 
	 * @return
	 */
	protected native int NvGetCurFocusProgramPIP();

	/**
	 * 使当前画中画获得焦点
	 * 
	 * @param ProgNum
	 * @return
	 */
	protected native int NvSetCurFocusProgramPIP(int ProgNum);

	/**
	 * 创建播放器对象
	 * 
	 * @param volume
	 *            当前音量值
	 * @param isFisrtCreate
	 *            是否是第一次创建
	 * @return handler：int类型，播放器句柄。负数代表创建失败。
	 */
	protected native int NvCreatePlayerPIP(int volume, boolean isFisrtCreate);

	/**
	 * 创建播放器。专门用于IPTV恢复播放时调用
	 * 
	 * @param handler
	 * @return
	 */
	protected native int NvCreatePlayer2PIP(int handler);

	/**
	 * 开始播放
	 * 
	 * @param handler
	 *            播放器对象句柄
	 * @param strMediaUrl
	 *            播放地址
	 * @param strShiftUrl
	 *            时移地址
	 * @param isLive
	 *            是否是直播
	 * @return
	 */
	protected native int NvLoadAndPlayPIP(int handler, String strMediaUrl,
			String strShiftUrl, boolean isLive);

	/**
	 * 跳转至开始播放
	 * 
	 * @param handler
	 *            播放器对象句柄
	 */
	protected native void NvGotoStartPIP(int handler);

	
	protected native void NvGotoEndPIP(int handler);

	/**
	 * 恢复播放器
	 * 
	 * @param handler
	 *            播放器对象句柄
	 */
	protected native void NvRestorePlayPIP(int handler);

	/**
	 * 设置当前频道
	 * 
	 * @param handler
	 *            播放器对象句柄
	 */
	protected native void NvSetCurrentChannelPIP(int handler);

	/**
	 * 停止播放
	 * 
	 * @param handler
	 *            播放器对象句柄
	 */
	protected native void NvStopPIP(int handler);

	/**
	 * 播放
	 * 
	 * @param handler
	 * @param nSpeed
	 * @return
	 */
	protected native int NvPlayPIP(int handler, int nSpeed);

	/**
	 * 暂停播放
	 * 
	 * @param handler
	 *            播放器对象句柄
	 */
	protected native void NvPausePIP(int handler);

	/**
	 * 恢复播放
	 * 
	 * @param handler
	 *            播放器对象句柄
	 */
	protected native void NvResumePIP(int handler);

	/**
	 * 跳转指定时间播放（保留）
	 * 
	 * @param handler
	 *            播放器对象句柄
	 * @param nTime
	 *            跳转的时间
	 * @return
	 */
	protected native int NvSeekPIP(int handler, int nTime);

	/**
	 * 设置视频窗口模式（保留）
	 * 
	 * @param handler
	 * @param mode
	 * @return
	 */
	protected native int NvSetVideoModePIP(int handler, int mode);

	/**
	 * 设置视频窗口宽度（保留）
	 * 
	 * @param handler
	 * @return
	 */
	protected native int NvSetVideoWidthPIP(int handler);

	/**
	 * 设置视频窗口高度（保留）
	 * 
	 * @param handler
	 * @return
	 */
	protected native int NvSetVideoHeightPIP(int handler);

	/**
	 * 获取视频总时间
	 * 
	 * @param handler
	 * @return
	 */
	protected native int NvGetDurationPIP(int handler);

	/**
	 * 获取当前播放时间（秒）
	 * 
	 * @param handler
	 * @return
	 */
	protected native int NvGetCurrentTimePIP(int handler);

	/**
	 * 获取当前播放时间（毫秒）
	 * 
	 * @param handler
	 * @return
	 */
	protected native int NvGetCurrentTimeMsPIP(int handler);

	/**
	 * 获取当前播放时间
	 * 
	 * @param handler
	 * @return
	 */
	protected native String NvGetCurrentTimeStrPIP(int handler);

	/**
	 * 获取当前播放状态
	 * 
	 * @param handler
	 * @return
	 */
	protected native int NvGetPlayStatePIP(int handler);

	/**
	 * 设置视频控件
	 * 
	 * @param handler
	 * @return
	 */
	protected native boolean NvSetSurfacePIP(int handler, Surface surf,
			String strSurface);

	/**
	 * 设置视频窗口
	 * 
	 * @param handler
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	protected native void NvSetVideoWindowPIP(int handler, int x, int y, int w,
			int h);

	/**
	 * 获取播放状态json字符串
	 * 
	 * @param handler
	 */
	protected native String NvGetPlaybackModePIP(int handler);

	/**
	 * 释放播放器
	 * 
	 * @param handler
	 */
	protected native void NvReleasePlayerPIP(int handler);

	/**
	 * 离开频道
	 * 
	 * @param handler
	 */
	protected native void NvLeaveChannelPIP(int handler);

	/**
	 * 是否是时移状态
	 * 
	 * @param handler
	 * @return
	 */
	protected native boolean NvGetShiftStatusPIP(int handler);

	/**
	 * 设置programId
	 * 
	 * @param handler
	 *            programId add by djf
	 */

	protected native void NvSetProgramIDPIP(int handler, int programId);

	protected native void NvLiveSeekPIP(int handler, String strTime);

	/**
	 * 设置FCC数据
	 * 
	 * @param handler
	 * @param nChannel
	 * @param strFCCHost
	 * @param nFCCPort
	 * @return
	 */
	protected native void NvSetFCCParamPIP(int handler, int nChannel,
			String strFCCHost, int nFCCPort);

	/**
	 * 组播是否可用
	 * 
	 * @param handler
	 * @return
	 */

	protected native boolean NvCanUseIGMPPIP(int handler);

	/**
	 * 设置音频动态切换功能 modle中 false为不静音 true 为静音
	 * 
	 * @param progNum
	 * @param modle
	 */
	// add by djf 20160928
	protected native void NvSetProgramAudioMutePIP(int progNum, boolean modle);
	
	/**
	 * 设置音量
	 * @param handler
	 * @param nVolume
	 * @return
	 */
	protected native void NvSetVolumePIP(int handler, int nVolume);
	
	/**
	 * 获取音量
	 * @param handler
	 * @return
	 */
	protected native int NvGetVolumePIP(int handler);
	
	/**
	 * 选择音轨
	 * @param handler
	 * @param pid
	 */
	protected native void NvSwitchAudioTrackPIP(int handler, int pid);

	/**
	 * 获取音轨信息
	 * @param handler
	 */
	protected native String NvGetAudioPIDsPIP(int handler);
	
	/**
	 * 设置声道
	 * @param handler
	 * @param nBalance
	 */
	protected native void NvSetAudioBalancePIP(int handler, int nBalance);

	/**
	 * 获取当前声道信息
	 * @param handler
	 */
	protected native int NvGetAudioBalancePIP(int handler);

	protected native int NvGetVideoWidthPixelsPIP(int handler);

	protected native int NvGetVideoHeightPixelsPIP(int handler);

	/**
	 * 隐藏窗口 add djf 20161025
	 */
	protected native void NvVideoHidePIP();

	/**
	 * 显示视屏 add djf 20161020
	 */
	protected native void NvVideoShowPIP();

	/**
	 * 获取芯片版本 大于2 为华为
	 */
	protected native int NvGetPlayerVer();
	
	/**
	 * 用于存放播放器句柄和对应的programId的集合。key 为播放器句柄handler,value为programId
	 */
	// public static HashMap<Integer,Integer> playerHandlerMap = new
	// HashMap<Integer, Integer>();

	/**
	 * 创建播放器实例
	 * 
	 * @return handler 播放器实例句柄，int类型。当值不为输负数时，代表创建成功（包括0），否则创建失败
	 * @param volume
	 *           当前音量值
	 * @param isFirstCreate
	 * @return
	 */
	public int createPlayerWithHandler(int volume, boolean isFirstCreate) {
		int handler = -1;
		if (true) {
			ALOG.info("IPTVPlayer--Start CreatePlayerWithHandle(), volume > " + volume);
			handler = NvCreatePlayerPIP(volume, isFirstCreate);
			isExistPlayer = true;
		}
		return handler;
	}

	/**
	 * 恢复播放时调用
	 * 
	 * @param handler
	 * @return
	 */
	public int createPlayerWithHandler2(int handler) {
		// 判断是否要加载libCTC_MediaProcessor.so,默认是加载的,只有江苏联通OTT认证版本才不会加载 xb 20131104
		if (true) {
			ALOG.info("IPTVPlayer--CreatePlayerWithHandler2(), handler > " + handler);
			handler = NvCreatePlayer2PIP(handler);
		}
		//
		return handler;
	}

	/**
	 * 隐藏 窗口 add djf 20161025
	 */
	public void setVideoHidePIP() {
		for (int i = 0; i < 3; i++) {
			setCurFocusProgramPIP(i);
			NvVideoHidePIP();
		}

	}

	public void setProgramIDPIP(int handler, int programId) {
		ALOG.info("IPTVPlayer > SetProgramIDPIP : " + handler
				+ "   programId=" + programId);
		NvSetProgramIDPIP(handler, programId);
	}

	//add by djf 20160928 设置静音
	public void setProgramAudioMutePIP() {
		ALOG.info("IPTVPlayer > SetProgramAudioMutePIP -->" + progNum);
		NvSetProgramAudioMutePIP(progNum, false);

		ALOG.info("IPTVPlayer > Don't  need SetProgramAudioMutePIP");
	}

	public void videoShowPIP() {
		ALOG.info("IPTVPlayer > VideoShowPIP");
		NvVideoShowPIP();
	}

	/**
	 * 获取播放模式
	 * 
	 * @return int类型 0：单例模式 1： 画中画模式（多例）
	 */
	public int getPlayMode() {
		int playMode = 1;
		playMode = NvGetPlayModePIP();
		return playMode;
	}

	/**
	 * 获取芯片版本号
	 */
	public int getPlayerVer() {
		int version = NvGetPlayerVer();
		ALOG.info("IPTVPlayer >  GetPlayerVer-->" + version);
		return version;
	};

	/**
	 * 判断播放器是否存在
	 * 
	 * @param handler
	 *            int类型，播放器句柄。若是多例模式下，根据播放句柄，判断对应的播放器是否存在
	 * @return
	 */
	public boolean isExistPlayer(int handler) {
		ALOG.info("isExitPlayer > handler : " + handler + ", isExit : "
				+ isExistPlayer);
		return isExistPlayer;
	}

	private int progNum = -999;

//	/**
//	 * 创建视频项目（画中画模式）
//	 * 
//	 * @param count
//	 *            创建次数。0：主播放器，有音频。1：副播放器。
//	 * @return
//	 */
//	public int addProgram(int count) {
//		ALOG.info("SetaddProgram count : " + count);
//		// 大窗口设置addProgram为0，小窗口为都为1
//		progNum = NvAddProgramPIP(count);
//		ALOG.info("addProgram id : " + progNum);
//		return progNum;
//	}

	/**
	 * 删除一个画中画项目，必须要先stop。参考《IPTV画中画方案驱动设计说明书.doc》（目前是MediaControl自己在调用，
	 * java层暂未使用）
	 * 
	 * @return
	 */
	public int deleteProgram(int program) {
		ALOG.info("deleteProgram > program :" + program);
		int result = NvDelProgramPIP(program);
		return result;
	}

	public int loadAndPlay(int handler, String strMediaUrl, String strShiftUrl,
			boolean isLive) {
		ALOG.info("LoadAndPlay:" + strMediaUrl + "; " + strShiftUrl);
		//setCurFocusProgramPIP(progNum);
		return NvLoadAndPlayPIP(handler, strMediaUrl, strShiftUrl, isLive);
	}

	public void leaveChannel(int handler) {
		ALOG.info("LeaveChannel : " + handler);
		//setCurFocusProgramPIP(progNum);
		NvLeaveChannelPIP(handler);
	}

	public void stop(int handler) {
		//setCurFocusProgramPIP(progNum);
		NvStopPIP(handler);
	}

	public boolean isStartPlay(int handler) {
		int nState = NvGetPlayStatePIP(handler);
		ALOG.info("GetPlayState-->" + nState);
		return (nState != STATE_STOP && nState != STATE_STOPING);
	}

	public boolean isPause(int handler) {
		return (NvGetPlayStatePIP(handler) == STATE_PAUSE);
	}

	public void pause(int handler) {
		//setCurFocusProgramPIP(progNum);
		NvPausePIP(handler);
	}

	public void resume(int handler) {
		//setCurFocusProgramPIP(progNum);
		NvResumePIP(handler);
	}

	public boolean setSurface(int handler, Surface surf) {
		if (android.os.Build.VERSION.SDK_INT > 18) {
			return NvSetSurfacePIP(handler, surf, "mNativeObject");
		} else if (android.os.Build.VERSION.SDK_INT >= 13)
			return NvSetSurfacePIP(handler, surf, "mNativeSurface");
		else
			return NvSetSurfacePIP(handler, surf, "mSurface");
	}

	public void restorePlay(int handler) {
		//setCurFocusProgramPIP(progNum);
		NvRestorePlayPIP(handler);
	}

	public void releasePlayer(int handler) {
		ALOG.info("IPTVPlayer----releasePlayer, handler: " + handler);
		NvReleasePlayerPIP(handler);
		isExistPlayer = false;
	}

	public int play(int handler, int nSpeed) {
		//setCurFocusProgramPIP(progNum);
		return NvPlayPIP(handler, nSpeed);
	}

	public int getPlayState(int handler) {
		return NvGetPlayStatePIP(handler);
	}

	public int getDuration(int handler) {
		return NvGetDurationPIP(handler);
	}

	public boolean getShiftStatus(int handler) {
		return NvGetShiftStatusPIP(handler);
	}

	public String getCurrentTimeStr(int handler) {
		return NvGetCurrentTimeStrPIP(handler);
	}

	public int getCurrentTime(int handler) {
		return NvGetCurrentTimePIP(handler);
	}

	public int getCurrentTimeMs(int handler) {
		return NvGetCurrentTimeMsPIP(handler);
	}

	public void iptvReadyPIP(int handler) {
		NvIPTVReadyPIP(handler);
	}

	public void setVideoWindow(int handler, int x, int y, int w, int h) {
		ALOG.info("IPTVPlayer", "SetVideoWindow-->handler :" + handler
					+ "; " + x + "," + y + "," + w + "," + h);
		setCurFocusProgramPIP(progNum);
		NvSetVideoWindowPIP(handler, x, y, w, h);
	}

	public int seek(int handler, int nTime) {
		//setCurFocusProgramPIP(progNum);
		return NvSeekPIP(handler, nTime);
	}

	public String getPlaybackMode(int handler) {
		return NvGetPlaybackModePIP(handler);
	}

	public void gotoStart(int handler) {
		//setCurFocusProgramPIP(progNum);
		NvGotoStartPIP(handler);
	}

	public void gotoEnd(int handler) {
		//setCurFocusProgramPIP(progNum);
		NvGotoEndPIP(handler);
	}

	public void setVideoMode(int handler, int mode) {
		//setCurFocusProgramPIP(progNum);
		NvSetVideoModePIP(handler, mode);
	}

	public boolean canUseIGMP(int handler) {
		//setCurFocusProgramPIP(progNum);
		return NvCanUseIGMPPIP(handler);
	}

	public void setFCCParam(int handler, int nChannel, String strFCCHost,
			int nFCCPort) {
		//setCurFocusProgramPIP(progNum);
		NvSetFCCParamPIP(handler, nChannel, strFCCHost, nFCCPort);
	}

	public void setCurrentChannel(int handler, int nChannel, int nFECPort) {
		//setCurFocusProgramPIP(progNum);
		NvSetCurrentChannelPIP(handler);
	}

	public void liveSeek(int handler, String strTime) {
		//setCurFocusProgramPIP(progNum);
		NvLiveSeekPIP(handler, strTime);
	}
	
	/**
	 * 获取音轨信息
	 * @param handler
	 */
	public String getAudioPIDs(int handler) {
		return NvGetAudioPIDsPIP(handler);
	}
	
	/**
	 * 设置音轨信息
	 * @param handler
	 * @param audioPID
	 */
	public void switchAudioTrack(int handler, int audioPID) {
		NvSwitchAudioTrackPIP(handler, audioPID);
	}

	public void switchSubtitle(int subtitlePID) {
		NvSwitchSubtitle(subtitlePID);
	}
	
	/**
	 * 设置焦点节目programX
	 * 
	 * @param programId
	 */
	private int setCurFocusProgramPIP(int programId) {
		ALOG.info("setCurFocusProgramPIP > programId : " + programId);
		try {
			NvSetCurFocusProgramPIP(programId);
			return programId;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * 设置播放器是否静音
	 * @param programId
	 * @param modle true: 静音， false： 不静音
	 * @return
	 */
	public boolean setProgramAudioMute(int programId, boolean modle) {
		ALOG.info("setProgramAudioMute > programId : " + programId + ", modle: " + modle);
		try {
			NvSetProgramAudioMutePIP(programId, modle);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 设置当前音量
	 * @param handler
	 * @param nVolume
	 */
	public void setVolumePIP(int handler, int nVolume) {
		NvSetVolumePIP(handler, nVolume);
	}
	
	/**
	 * 获取当前音量
	 * @param handler
	 */
	public int getVolumePIP(int handler) {
		return NvGetVolumePIP(handler);
	}
	
	/**
	 * 设置声道
	 * @param handler
	 * @param nBalance
	 */
	public void setAudioBalancePIP(int handler, int nBalance) {
		NvSetAudioBalancePIP(handler, nBalance);
	}
	
	/**
	 * 获取当前声道信息
	 * @param handler
	 */
	public int getAudioBalancePIP(int handler) {
		return NvGetAudioBalancePIP(handler);
	}
	
	/** ========================== end 画中画新增接口============================== */
	
	public static void setUdpHold(boolean udpHold){
		NvSetUdpHold(udpHold);
	}
	
	public static boolean isSoftFit(){
		return NvIsSoftFit();
	}
	
	public static void setEPGSize(int w, int h) {
		ALOG.info("SetEPGSize:" + w + ",h:" + h);
		NvSetEPGSize(w, h);
	}
	
	public void setEventObject(Object obj) {
		ALOG.info("setEventObject---->" + obj);
		NvSetEventObject(obj);
	}
	
	public static void setConfig(String strName, String strValue) {
		NvSetConfig(strName, strValue);
	}
	
	public static void setNetStatus(boolean isDisconnect) {
		NvSetNetStatus(isDisconnect);
	}
	
	public static int getDisplayMode() {
		return mPlayMode;
	}
	
//	public static void setVolume(int nVolume) {
//		NvSetVolume(nVolume);
//	}

	public int getVolume() {
		return NvGetVolume();
	}
	
//	public static int getAudioBalance() {
//		return NvGetAudioBalance();
//	}

	public static void setAudioBalance(int nBalance) {
		NvSetAudioBalance(nBalance);
	}

	public static String getAudioPIDs() {
		return NvGetAudioPIDs();
	}
	
	public static String getSubtitlePIDs() {
		return NvGetSubtitlePIDs();
	}
	
	public static String decode(byte[] ucCode) {
		return NvDecode(ucCode);
	}
	
	public static String getLocalHost() {
		return NvGetLocalHost();
	}
	
	protected int getVideoWidthPixelsPIP(int handler) {
		return NvGetVideoWidthPixelsPIP(handler);
	}

	protected int getVideoHeightPixelsPIP(int handler) {
		return NvGetVideoHeightPixelsPIP(handler);
	}



	//=================================================可视化信息获取接口========================================//
	//获取到的展示信息的东西，下面有几个方法是写死的拿到后需要改正
	//	1)  机顶盒的CPU占有率  //卓影提供    CPURate
	//	2)  机顶盒内存占有率 //卓影提供        MEMRate
	//	3)  视频分辨率  //芯片提供，使用查询接口    VideoResolution
	//	4)  视频宽高比  //芯片提供，使用查询接口    VideoRatio
	//	5)  视频编码方式  //卓影提供                VideoEncode
	//	6)  帧场模式   //芯片提供，使用查询接口     SampleType
	//	7)  第x个音轨的音频编码方式  //卓影提供    AudioEncode
	//	8)  第x个音轨的音频码率  //卓影提供        AudioBandwidth
	//	9)  第x个音轨的声道个数  //卓影提供        AudioChannels
	//	10)  第x个音轨的音频采样率  //卓影提供     AudioSampleRate
	//	11)  第x个音轨的字幕名称  //卓影提供       AudioSubtitle
	//	12)  上一个统计周期内的总丢包数（SQA纠错前）  //卓影提供       LostFrameCnt
	//	13)  网络包乱序统计  //卓影提供             OutOfOrderCnt
	//	14)  网络时延  //卓影提供                   NetworkDelay
	//	15)  传输协议  //卓影提供                   Protocol
	//	16)  上一个统计周期内的ts的连续计数错误  //卓影提供   TSCountError
	//	17)  上一个统计周期内的同步头丢失数量  //卓影提供      TSHeadLost
	//	18)  ECM错误次数  //卓影提供，待确认具体含义          ECMError
	//	19)  音视频播放diff   //芯片提供，使用查询接口         VidAudDiff
	//	20)  视频缓冲区大小  //芯片提供，使用查询接口         VideoBufSize
	//	21)  视频缓冲区使用大小  //芯片提供，使用查询接口      VideoBufUsedSize
	//	22)  音频缓冲区大小  //芯片提供，使用查询接口          AudioBufSize
	//	23)  音频缓冲区已使用大小  //芯片提供，使用查询接口    AudioBufUsedSize
	//	24)  视频解码错误统计 //芯片提供，使用事件上报方式    VideoDecodeErrorStat
	//	25)  视频解码丢包统计  //芯片提供，使用事件上报方式   VideoDecodeLostStat
	//	26)  视频解码下溢统计 //芯片提供，使用事件上报方式    VideoDecodeUnderflowStat
	//	27)  视频解码Pts错误统计 //芯片提供，使用事件上报方式  VideoDecodePTSErrorStat
	//	28)  音频解码错误统计 //芯片提供，使用事件上报方式    AudioDecodeErrorStat
	//	29)  音频解码丢弃统计 //芯片提供，使用事件上报方式，AudioDecodeLostStat
	//	30)  音频解码下溢次数 //芯片提供，使用事件上报方式  AudioDecodeUnderflowStat
	//	31) 音频Pts错误次数 //芯片提供，使用事件上报方式   AudioDecodePTSErrorStat


	public static String getCPURate(){
		String cpurate = String.valueOf(NvGetValue("CPURate",null));
		ALOG.info("IPTV viewinfo-->getCPURate :" + cpurate);
		if(cpurate.length()>2){
			cpurate = cpurate.substring(0, 2) + "." + cpurate.substring(2);
		}
		String temp = "CPU 使用率为 ：" + cpurate + "%";
		return temp;
	}

	public static String getMemRate(){
		String memoryrate = String.valueOf(NvGetValue("MEMRate",null));
		if(memoryrate.length()>2){
			memoryrate = memoryrate.substring(0, 2) + "." + memoryrate.substring(2);
		}
		ALOG.info("IPTV viewinfo-->getMemRate :" +memoryrate);
		String temp = "内存占有率 ：" + memoryrate + "%";
		return temp;
	}
	public static String getVideoWidthPixelsInfo(){
		String  value = NvGetValue("VideoResolution",null);
		ALOG.info("IPTV viewinfo-->GetVideoWidthPixels :"+value);
		String temp = "" ;
		if (value==null){
			temp = "视屏分辨率：空";
		}else  if(value.equals("0")){
			temp = "视屏分辨率：640*480";
		}else if(value.equals("1")){
			temp = "视屏分辨率：720*576";
		}else if(value.equals("2")){
			temp = "视屏分辨率：1280*720";
		}else if(value.equals("3")){
			temp = "视屏分辨率：1920*1080";
		}else if(value.equals("4")){
			temp = "视屏分辨率：3840*2160";
		}else if(value.equals("5")){
			temp = "视屏分辨率：others";
		}
		return temp;

	}
	public static String getVideoAspectRatio(){
		String value = NvGetValue("VideoRatio",null);
		String temp= "";
		ALOG.info("IPTV viewinfo-->GetVideoAspectRatio :"+value );
		if (value==null){
			temp = "视频宽高比：空";
		}else if(value.equals("0")){
			temp = "视频宽高比 ：4：3";
		}else if (value.equals("1")){
			temp = "视频宽高比 ：16：9";
		}else {
			temp = "视频宽高比 ：未知";
		}
		return temp;

	}
	public static String getVideoEncodingMode(){
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :"+NvGetValue("VideoEncode",null) );
		String value = NvGetValue("VideoEncode",null);
		String temp = "";
		if(value.equals("2")){
			temp = "视频编码方式 ：H.264";
		}else if(value.equals("8")) {
			temp = "视频编码方式 ：H.265";
		}else {
			temp = "视频编码方式 ：MPEG4";
		}

		return temp;
	}

	public static String getFrameFieldMode(){
		String value = NvGetValue("SampleType",null);
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :" +value);
		String temp = "";
		if (value==null){
			temp = "帧场模式 ：空";
		}else if(value.equals("1")){
			temp = "帧场模式 ：逐行源 ";
		}else if(value.equals("0")){
			temp = "帧场模式 ：隔行源 ";
		}
		return temp;
	}
	public static String getAudioEncodingMode(){
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :"+NvGetValue("AudioEncode",null) );
		String temp  = "";
		if(NvGetValue("AudioEncode",null)!=null&&NvGetValue("AudioEncode",null).equals("2")){
			temp = "第1个音轨的音频编码方式 ：AAC ";
		}else {
			temp = "第1个音轨的音频编码方式 ：MPEG ";
		}
		return temp;
	}

	public static String getAudioRate(){
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :"+"8000" );
		String temp = "第1个音轨的音频码率 :8000 ";
		return temp;
	}

	public static String getAudioCount(){
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :" +NvGetValue("AudioChannels",null));
		String temp = "第1个音轨的声道个数 : "+NvGetValue("AudioChannels",null);
		return temp;
	}

	public static String getAudioSamplingRate(){
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :" +NvGetValue("AudioSampleRate",null));
		String temp = "第1个音轨的音频采样率 : "+NvGetValue("AudioSampleRate",null);
		return temp;
	}

	public static String getAudioSubtitleName(){
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :" );
		String temp = "第1个音轨的字幕名称 : 无";
		return temp;
	}
	public static String getLastPeriodPackageLoss(){
		String temp = "上一个统计周期内的总丢包数 : " + NvGetValue("LostFrameCnt",null);
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :"+temp );
		return temp;
	}

	public static String getPackageOutOfOrderStatistics(){
		String temp = "网络包乱序统计 : "+NvGetValue("OutOfOrderCnt",null);
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :"+temp );
		return temp;
	}

	public static String getNetDelay(){
		int random=(int)(Math.random()*20)+10;
		String temp = "网络时延 : " + random+"ms";
		return temp;
	}

	public static String getProtocol(){
		String temp ="";
		String value = NvGetValue("Protocol",null);
		if(value!=null&&value.equals("0")){
			temp = "传输协议 : UDP";
		}else if(value!=null&&value.equals("1")){
			temp = "传输协议 : TCP";
		}else{
			temp = "传输协议 : 无";
		}
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :"+temp );
		return temp;
	}

	public static String getLastPeriodTsCountError(){
		String temp = "上一个统计周期内的ts的连续计数错误 : " + NvGetValue("TSCountError",null);
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :"+temp );
		return temp;
	}

	public static String getLastPeriodSyncheadlossNum(){
		String temp = "上一个统计周期内的同步头丢失数量 : " +NvGetValue("TSHeadLost",null);
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :"+temp );
		return temp;
	}

	public static String getECMErrorNum(){
		String temp = "ECM错误次数 : " +NvGetValue("ECMError",null);
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :"+temp );
		return temp;
	}

	public static String getAudioPlayDiff(){
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :" );
		String temp = "音视频播放diff : "+NvGetValue("VidAudDiff",null);
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :"+temp );
		return temp;
	}

	public static String getVideoBuffer(){
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :"+NvGetValue("VideoBufSize",null) );
		String temp = "视频缓冲区大小 : "+NvGetValue("VideoBufSize",null)+"B";
		return temp;
	}

	public static String getVideoBufferUsedSize(){
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :"+NvGetValue("VideoBufUsedSize",null) );
		String temp = "视频缓冲区使用大小 : " +NvGetValue("VideoBufUsedSize",null)+"B";
		return temp;
	}

	public static String getAudioBuffer(){
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :"+NvGetValue("AudioBufSize",null+"B") );
		String temp = "音频缓冲区大小 :"+NvGetValue("AudioBufSize",null)+"B";
		return temp;
	}
	public static String getAudioBufferUsedSize(){
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :"+ NvGetValue("AudioBufUsedSize",null) );
		String temp = "音频缓冲区已使用大小 : " + NvGetValue("AudioBufUsedSize",null) + "B";
		return temp;
	}

	public static String getVideoDecodeError(){
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :" + NvGetValue("VideoDecodeErrorStat",null));
		String temp = "视频解码错误统计 : " + NvGetValue("VideoDecodeErrorStat",null);
		return temp;
	}

	public static String getVideoDecodePackageLoss(){
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :"+ NvGetValue("VideoDecodeLostStat",null) );
		String temp = "视频解码丢包统计 : " + NvGetValue("VideoDecodeLostStat",null);
		return temp;
	}

	public static String getVideoDecodeUnderflow(){
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :" +NvGetValue("VideoDecodeUnderflowStat",null));
		String temp = "视频解码下溢统计 : " +NvGetValue("VideoDecodeUnderflowStat",null);
		return temp;
	}

	public static String getVideoDecodePTSError(){
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :"+NvGetValue("VideoDecodePTSErrorStat",null) );
		String temp = "视频解码Pts错误统计 : " +NvGetValue("VideoDecodePTSErrorStat",null);
		return temp;
	}

	public static String getAudioDecodeError(){
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :" +NvGetValue("AudioDecodeErrorStat",null));
		String temp = "音频解码错误统计 : " +NvGetValue("AudioDecodeErrorStat",null);
		return temp;
	}

	public static String getAudioDecodeThrowError(){
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :"+NvGetValue("AudioDecodeLostStat",null) );
		String temp = "音频解码丢弃统计 : " +NvGetValue("AudioDecodeLostStat",null);
		return temp;
	}

	public static String getAudioDecodeUnderflow(){
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :"  + NvGetValue("AudioDecodeUnderflowStat",null));
		String temp = "音频解码下溢次数 : " + NvGetValue("AudioDecodeUnderflowStat",null);
		return temp;
	}

	public static String getAudioPTSError(){
		ALOG.info("IPTV viewinfo-->GetVideoEncodingMode :"+ NvGetValue("AudioDecodePTSErrorStat",null) );
		String temp = "音频Pts错误次数 : " + NvGetValue("AudioDecodePTSErrorStat",null);
		return temp;
	}
}
