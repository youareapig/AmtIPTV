package com.amt.player.iptvplayer;

import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.amt.config.Config;
import com.amt.player.IPlayerInterface;
import com.amt.player.MediaEventInfoListener;
import com.amt.player.entity.LiveChannel;
import com.amt.player.entity.LiveChannelHelper;
import com.amt.utils.ALOG;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class IPTVPlayerPIP extends IPTVPlayerBase{
	
	private static final String TAG = "IPTVPlayerPIP";
	private MediaEventInfoListener mEvent = null;
	private String mediaUrl; //播放地址
	private int MP_PLAYMODE; //记录当前HttpMediaPlayer的播放状态,1:Pause,2:Play,3:Trick
	private int MP_PLAY_RATE; //记录当前HttpMediaPlayer的播放速率,1:正常播放,0:暂停,其他：当前快进或者快退的速率
	private SurfaceView surfaceView;
	private static Handler mHandler;
	private int playerHandler;//播放器实例句柄
	private int programId;
	
	/**
	 * 创建播放器
	 * @param addProgramId 设置播放器programId，主播放器为0，其他播放器为1~n
	 */
	public IPTVPlayerPIP(int addProgramId) {
		initProperties();
//		if (addProgramId == 0) {
//			//设置播放器模式 1：多例， 0：单例
//			//setPlayMode(0);
//		}
		createPlayerPIP(addProgramId);
	}
		
	/**
	 * 创建MediaControl播放器
	 * @param addProgramId 设置播放器programId，主播放器为0，其他播放器为1~n
	 */
	private void createPlayerPIP(int addProgramId) {
		ALOG.info(TAG, "createPlayerPIP--->addProgramId: " + addProgramId);
		programId = addProgramId;
		//获取播放器实例句柄
		playerHandler = createPlayerWithHandler(-1, true);
		//将获取到的programId设置下去
		setProgramIDPIP(playerHandler, addProgramId);
		iptvReadyPIP(playerHandler);
	}
	/**
	 * 专门用于回到IPTV恢复播放时调用此接口创建释放之前的播放器
	 * @return
	 */
	public int createPlayer() {
		ALOG.info(TAG, "createPlayerPIP2--->playerHandler: " + playerHandler);
		return createPlayerWithHandler2(playerHandler);
	}
	
	private void initSurface() {
		ALOG.info(TAG, "initSurface.");
		if (surfaceView != null) {
			surfaceView.getHolder().addCallback(new SurfaceViewHolder());
		}
	}
	
	public void initProperties() {
		ALOG.info(TAG, "initProperties.");
		MP_PLAYMODE = 2;
		MP_PLAY_RATE = 1;
		surfaceView = null;
		mediaUrl = "";
	} 
	
	public void setSurfaceView(SurfaceView surfaceView) {
		this.surfaceView = surfaceView;
		initSurface();
	}
	
	@Override
	public int joinChannel(int userChannelID) {
		ALOG.info(TAG, "joinChannel, userChannelID: " + userChannelID);
		if (mEvent != null) {
			mEvent.clearEvent();
		}
		LiveChannel mCurrentLive = LiveChannelHelper.getInstance().getChannelByUserChannelID(userChannelID);
		if (mCurrentLive != null) {
			LiveChannelHelper.getInstance().setCurrentChannel(mCurrentLive);
			if ((!TextUtils.isEmpty(mCurrentLive.getChannelFCCIP()) && !"0".equalsIgnoreCase(mCurrentLive.getChannelFCCPort()))) {
				ALOG.info(TAG, "start play FCC channel: " + userChannelID);
				setFCCParam(playerHandler, mCurrentLive.getUserChannelID(), mCurrentLive.getChannelFCCIP(), Integer.parseInt(mCurrentLive.getChannelFCCPort()));
			} 
			loadAndPlay(playerHandler, mCurrentLive.getChannelURL(), mCurrentLive.getTimeShiftURL(), true);
			if (mCurrentLive.getChannelFECPort() != 0 && DeviceInfo.MODEL.equalsIgnoreCase(Config.FEC_MODEL)) {
				NvSetCurrentChannel(mCurrentLive.getUserChannelID(), mCurrentLive.getChannelFECPort());
			}
		} else {
			ALOG.info(TAG, "joinChannel failed, LiveChannel is null! ");
			return -1;
		}
		return 0;
	}

	@Override
	public boolean leaveChannel() {
		ALOG.info(TAG, "leaveChannel.");
		if (isStartPlay()) {
			leaveChannel(playerHandler);
			initProperties();
			return true;
		}
		return false;
	}

	@Override
	public void play() {
		ALOG.info(TAG, "start play mediaStr: " + mediaUrl);
		if (TextUtils.isEmpty(mediaUrl)) {
			ALOG.info(TAG, "the mediaUrl is empty, return!!!");
			return;
		}
		if (mEvent != null) {
			mEvent.clearEvent();
		}
		loadAndPlay(playerHandler, mediaUrl, "", false);
//		if (mEvent != null) {
//			mEvent.sendEvent(MediaEventInfoListener.TYPE_EVENT_MEDIA_BEGINING, "");
//		}
	}

	@Override
	public void pause() {
		ALOG.info(TAG, "IptvPlayer is pause.");
		if (isStartPlay()) {
			pause(playerHandler);
			String strExtMsg = "\"new_play_mode\":1,\"new_play_rate\":" + 0 
					+ ",\"old_play_mode\":" + MP_PLAYMODE
					+ ",\"old_play_rate\":" + MP_PLAY_RATE;
			MP_PLAYMODE = 1;
			MP_PLAY_RATE = 0;
		}
	}

	@Override
	public void stop() {
		ALOG.info(TAG, "IptvPlayer is stop.");
		if (isStartPlay()) {
			if (mEvent != null) {
				mEvent.clearEvent();
			}
			stop(playerHandler);
			initProperties();
		}
	}

	@Override
	public boolean isPause() {
		boolean state = true;
		state = (getPlayState(playerHandler) == STATE_PAUSE);
		ALOG.info(TAG, "IptvPlayer is isOnPause : " + state);
		return state;
	}

	@Override
	public void fastForward(int speed) {
		ALOG.info(TAG, "IptvPlayer is fastForward, speed: " + speed);
		String strExtMsg = "\"new_play_mode\":3,\"new_play_rate\":" + speed 
				+ ",\"old_play_mode\":" + MP_PLAYMODE
				+ ",\"old_play_rate\":" + MP_PLAY_RATE;
		MP_PLAYMODE = 3;
		MP_PLAY_RATE = speed;
//		if (mEvent != null) {
//			mEvent.sendEvent(MediaEventInfoListener.TYPE_EVENT_PLAYMODE_CHANGE, strExtMsg);
//		}
		play(playerHandler, speed);
	}

	@Override
	public void fastRewind(int speed) {
		ALOG.info(TAG, "IptvPlayer is fastRewind, speed: " + speed);
		String strExtMsg = "\"new_play_mode\":3,\"new_play_rate\":" + speed 
				+ ",\"old_play_mode\":" + MP_PLAYMODE
				+ ",\"old_play_rate\":" + MP_PLAY_RATE;
		MP_PLAYMODE = 3;
		MP_PLAY_RATE = speed;
//		if (mEvent != null) {
//			mEvent.sendEvent(MediaEventInfoListener.TYPE_EVENT_PLAYMODE_CHANGE, strExtMsg);
//		}
		play(playerHandler, speed);
		
	}

	@Override
	public void playByTime(int type, String timestamp) {
		ALOG.info(TAG, "IptvPlayer playByTime, type: " + type + ", timestamp: " + timestamp);
		if (TextUtils.isEmpty(timestamp)) {
			ALOG.info(TAG, "playByTime, timestamp is Empty, return.");
			return;
		}
		if (!isStartPlay()) {
			play();
		} 
		if (type == 1) {
			seek(playerHandler, Integer.parseInt(timestamp));
		} else {
			liveSeek(playerHandler, timestamp);
		}
		String strExtMsg = "\"new_play_mode\":2,\"new_play_rate\":1"
				+ ",\"old_play_mode\":" + MP_PLAYMODE
				+ ",\"old_play_rate\":" + MP_PLAY_RATE;
		MP_PLAYMODE = 2;
		MP_PLAY_RATE = 1;
//			if (mEvent != null) {
//				mEvent.sendEvent(MediaEventInfoListener.TYPE_EVENT_PLAYMODE_CHANGE, strExtMsg);
//			}
	}

	@Override
	public void rePlay() {
		ALOG.info(TAG, "IptvPlayer is rePlay.");
		restorePlay(playerHandler);
		
	}

	@Override
	public void resume() {
		ALOG.info(TAG, "IptvPlayer is resume.");
		if (isStartPlay()) {
			String strExtMsg = "\"new_play_mode\":2,\"new_play_rate\":" + 1 
					+ ",\"old_play_mode\":" + MP_PLAYMODE
					+ ",\"old_play_rate\":" + MP_PLAY_RATE;
			MP_PLAYMODE = 2;
			MP_PLAY_RATE = 1;
//			if (mEvent != null) {
//				mEvent.sendEvent(MediaEventInfoListener.TYPE_EVENT_PLAYMODE_CHANGE, strExtMsg);
//			}
			resume(playerHandler);
		} else {
			ALOG.info(TAG, "IptvPlayer is playing, return!!!");
		}
	}

	@Override
	public String getCurrentPlayTime() {
		String currentTime = "0";
		if (!isStartPlay()) {
			ALOG.info(TAG, "IptvPlayer did not start play, getCurrentPlayTime is failed!");
			currentTime = "0";
		} else {
			currentTime = getCurrentTimeStr(playerHandler);
		}
		ALOG.info(TAG, "getCurrentPlayTime: " + currentTime);
		return currentTime;
	}

	@Override
	public int getMediaDuration() {
		int durationTime = 0;
		if (!isStartPlay()) {
			ALOG.info(TAG, "IptvPlayer did not start play, getMediaDuration is failed!");
		} else {
			durationTime = getDuration(playerHandler);
			if (durationTime == 0) {
				SystemClock.sleep(50);
				durationTime = getDuration(playerHandler);
			}
		}
		ALOG.info(TAG, "getMediaDuration: " + durationTime);
		return durationTime;
	}

	@Override
	public String getPlaybackMode() {
		String backMode = "";
		backMode = getPlaybackMode(playerHandler);
		ALOG.info(TAG, "getPlaybackMode: " + backMode);
		return backMode;
	}
	
	@Override
	public void gotoStart() {
		ALOG.info(TAG, "IptvPlayer gotoStart.");
		gotoStart(playerHandler);
//			if (mEvent != null) {
//				mEvent.clearEvent();
//				mEvent.sendEvent(MediaEventInfoListener.TYPE_EVENT_MEDIA_BEGINING, "");
//			}
	}

	@Override
	public void gotoEnd() {
		ALOG.info(TAG, "IptvPlayer gotoEnd.");
		gotoEnd(playerHandler);	
//			if (mEvent != null) {
//				mEvent.clearEvent();
//				mEvent.sendEvent(MediaEventInfoListener.TYPE_EVENT_MEDIA_END, "");
//			}
	}

	@Override
	public boolean isStartPlay() {
		boolean state = false;
		int playstat = 0;
		playstat = getPlayState(playerHandler);
		state = (playstat != STATE_STOP) && (playstat != STATE_STOPING);
		ALOG.info(TAG, "IptvPlayer is isOnStartPlay : " + state);
		return state;
	}

	@Override
	public boolean isLivePlay() {
		ALOG.info(TAG, "IptvPlayer is isLivePlay : " + true);
		return true;
	}

	private int audioChannel = 1;
	@Override
	public void switchAudioChannel() {
		audioChannel++;
		// 目前考虑最多4种或5种的情况
		if (audioChannel == 4 || audioChannel == 5) audioChannel = 1;
		setAudioBalancePIP(playerHandler, audioChannel);
		ALOG.info(TAG, "switchAudioChannel: " + Integer.toString(audioChannel));
	}

	private int curAudioPID = 1;
	private int curAudioIndex = 0;
	
	@Override
	public void switchAudioTrack(int id) {
		if (id >= 0) {
			ALOG.info(TAG, "switchAudioTrack: " + id);
			switchAudioTrack(playerHandler, id);
			return;
		}
		
		String audioPIDs = "";
		audioPIDs = getAudioPIDs(playerHandler);
		int pid = 0;
		if (!TextUtils.isEmpty(audioPIDs)) {
			// 默认第一个
			curAudioPID = Integer.parseInt(audioPIDs.split(";")[0].split(":")[0]);
			try {
				// 得到当前音轨在音轨列表中的索引
				String[] items = audioPIDs.split(";");
				for (int i = 0; i < items.length; i++) {
					int audioPID = Integer.parseInt(items[i].split(":")[0]);
					if (audioPID == curAudioPID) {
						curAudioIndex = i;
						break;
					}
				}
				curAudioIndex++;
				if (curAudioIndex >= items.length) {
					curAudioIndex = 0;
				}
				pid = Integer.parseInt(items[curAudioIndex].split(":")[0]);
				switchAudioTrack(playerHandler, pid);
			} catch (Exception e) {
			}
		}
		ALOG.info(TAG, "switchAudioTrack: " + pid);
	}
	
	/**
	 * 获取音轨信息
	 * 如果有音轨信息，返回：
	 * {“audio_track_list_count”:2,”audio_track_list“:
	 * [{“PID”:33, “language_code”:”deu”,”language_eng_name”:”Germany”, “audio_type”:0, “codec”:”AC3”},
	 * {“PID”:34,“language_code”:”deu”，”language_eng_name”:”Germany”, “audio_type”:3, “codec”:”MP2”}]}
	 * 如果没有音轨信息，返回：
	 * {”audio_track_list_count”:”0”}
	 * @return
	 */
	public String getAllAudioTrackInfo() {
		String audioInfo = "";
		JSONArray jsonArray = new JSONArray();
		JSONObject temp = null;
		JSONObject jsonInfo = new JSONObject();
		String audioPIDs = getAudioPIDs(playerHandler).trim();
		ALOG.info(TAG, "getAllAudioTrackInfo, audioPIDs--->" + audioPIDs);
		if (!TextUtils.isEmpty(audioPIDs) && audioPIDs != null) {
			String[] trackList = audioPIDs.split(";");
			for (String info : trackList) {
				try {
					String[] audioList = info.split(":");
					temp = new JSONObject();
					if (audioList.length > 0 ) {
						temp.put("PID", Integer.parseInt(audioList[0]));
						temp.put("language_code", "deu");
						if (audioList.length == 2) {
							temp.put("language_eng_name", audioList[1]);
						} else {
							temp.put("language_eng_name", "English");
						}
						temp.put("audio_type", 0);
						temp.put("codec", "AC3");
						jsonArray.put(temp);
						temp = null;
					}
				} catch (JSONException e) {
					ALOG.info(e.toString());
				}
			}
			try {
				jsonInfo.put("audio_track_list_count", trackList.length);
				jsonInfo.put("audio_track_list", jsonArray);
				audioInfo = jsonInfo.toString();
			} catch (JSONException e) {
				ALOG.info(e.toString());
			}
		} else {
			try {
				jsonInfo.put("audio_track_list_count", "0");
				audioInfo = jsonInfo.toString();
			} catch (JSONException e) {
				ALOG.info(e.toString());
			}
		}
		ALOG.info(TAG, "getAllAudioTrackInfo: " + audioInfo);
		return audioInfo;
	}
	
	
	/**
	 * 字符串：Left 、Right、Stereo 、 JointStereo 获取当前的声道类型
	 * 
	 * @return
	 */
	public String getCurrentAudioChannel() {
		String result = "";
		int audioBalance = getAudioBalancePIP(playerHandler);
		if (audioBalance == 1)
			result = "Left";// 左声道
		else if (audioBalance == 2)
			result = "Right";// 右声道
		else if (audioBalance == 3) {
			result = "Stereo";// 立体声
		} else {
			result = "JointStereo";// 混合立体声
		}
		ALOG.info("IPTVPlayer>getCurrentAudioChannel", result + ", nChannel:" + audioBalance);
		return result;
	}
	
	private int curSubtitlePID = 1;
	private int curSubtitleIndex = 0;
	@Override
	public void switchSubtitle() {
		String subtitlePIDs = "";
		subtitlePIDs = getSubtitlePIDs();
		int subtitle = 0;
		if (!TextUtils.isEmpty(subtitlePIDs)) {
			// 默认第一个
			curSubtitlePID = Integer.parseInt(subtitlePIDs.split(";")[0].split(":")[0]);
			try {
				// 得到当前音轨在音轨列表中的索引
				String[] items = subtitlePIDs.split(";");
				for (int i = 0; i < items.length; i++) {
					int id = Integer.parseInt(items[i].split(":")[0]);
					if (id == curSubtitlePID) {
						curSubtitleIndex = i;
						break;
					}
				}
				curSubtitleIndex++;
				if (curSubtitleIndex >= items.length) {
					curSubtitleIndex = 0;
				}
				subtitle = Integer.parseInt(items[curSubtitleIndex].split(":")[0]);
				switchSubtitle(subtitle);
			} catch (Exception e) {
			}
		}
		ALOG.info(TAG, "switchSubtitle: " + subtitle);
		
	}

	@Override
	public void sendVendorSpecificCommand(String sXmlCmd) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int releaseMediaPlayer(int nativePlayerInstanceID) {
		releasePlayer(playerHandler);
		return 0;
	}

	@Override
	public void addSingleMedia(int nIndex, String mediaStr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addBatchMedia(String batchMediaStr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMediaEventInfoListener(MediaEventInfoListener mediaEventInfo) {
		mEvent = mediaEventInfo;
		
	}
	
	@Override
	public void setSingleMedia(String mediaUrl) {
		this.mediaUrl = mediaUrl;
		
	}
	
	@Override
	public void setCycleFlag(int cycleFlag) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 设置播放器是否静音
	 * @param modle true: 静音， false： 不静音
	 * @return
	 */
	public boolean setProgramAudioMute(boolean modle) {
		return setProgramAudioMute(programId, modle);
	}
	
	/**
	 * 设置播放器音量
	 */
	public void setVolume(int nVolume) {
		setVolumePIP(playerHandler, nVolume);
	}
	
	/**
	 * 获取播放器音量
	 */
	public int getVolume() {
		return getVolumePIP(playerHandler);
	}
	
	/**
	 * 是否是时移状态
	 * @return
	 */
	public boolean getShiftStatus() {
		return getShiftStatus(playerHandler);
	}	
	
	public String getCurrentTimeStr() {
		return getCurrentTimeStr(playerHandler);
		
	}
	
	public int getCurrentTime() {
		return getCurrentTime(playerHandler);
	}
	
	public int getCurrentTimeMs() {
		return getCurrentTimeMs(playerHandler);
	}
	
	/**
	 * 设置视频窗口位置大小
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 */
	public void setVideoWindow(int x, int y, int w, int h) {
		setVideoWindow(playerHandler, x, y, w, h);
	}

	/**
	 * 设置surface
	 * @param surface
	 * @return
	 */
	private boolean setSurface(Surface surface) {
		return setSurface(playerHandler, surface);
	}
	
	/**
	 * 获取当前播放状态
	 * @return
	 */
	public int getPlayState() {
		return getPlayState(playerHandler);
	}
	
	/**
	 * 设置视频窗口模式
	 * @param mode
	 */
	public void setVideoMode(int mode) {
		setVideoMode(playerHandler, mode);
	}
	
	/**
	 * 组播是否可用  
	 * @return
	 */
	public boolean canUseIGMP() {
		return canUseIGMP(playerHandler);
	}
	
	private class SurfaceViewHolder implements SurfaceHolder.Callback {

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			//ALOG.info(TAG, "surfaceChanged.");
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			//ALOG.info(TAG, "surfaceCreated.");
			setSurface(holder.getSurface());
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			//ALOG.info(TAG, "surfaceDestroyed.");
		}
	}
	
}
