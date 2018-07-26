package com.amt.player.systemplayer;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.amt.player.AmtMediaPlayer;
import com.amt.player.IPlayerInterface;
import com.amt.player.MediaEventInfoListener;
import com.amt.player.iptvplayer.IPTVPlayer;
import com.amt.utils.ALOG;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class SystemMediaPlayer implements IPlayerInterface {
	
	private static final String TAG = "SystemPlayer";
	private MediaPlayer mp;
	private int cycleFlag; //是否循环播放1：不循环  其他：循环
	private String mediaUrl; //播放地址
	private boolean isSurfaceCreated; //surface是否创建成功
	private boolean isNeedWaitSurfaceCreate; //是否需要等待创建surface
	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private int currentPlayTime; //记录当前播放时间
	private String stopPlayTime; //记录停止时当前播放时间
	private boolean isOnPrepared; //mp是否准备完毕
	private boolean isStartPlay; //mp是否正在播放
	private boolean isPaused; // mp是否暂停
	private boolean isSeekComplete; //mp是否完成seek
	private boolean isFastForward; //mp是否正在快进
	private boolean isFastRewind; //mp是否正在快退
	private int MP_PLAYMODE; //记录当前HttpMediaPlayer的播放状态,1:Pause,2:Play,3:Trick
	private int MP_PLAY_RATE; //记录当前HttpMediaPlayer的播放速率,1:正常播放,0:暂停,其他：当前快进或者快退的速率
	private Timer timer;
	private MediaEventInfoListener mEvent = null;
	public SurfaceViewHolder mSurfaceHolder = new SurfaceViewHolder();
	private int videoDisplayMode;

	public SystemMediaPlayer() {
		initProperties();
		initPlayer();
	}
	
	/**
	 * 初始化播放器属性
	 */
	public void initProperties() {
		this.cycleFlag = 1;
		this.currentPlayTime = 0;
		isSurfaceCreated = false;
		isNeedWaitSurfaceCreate = false;
		isOnPrepared = false;
		isStartPlay = false;
		isPaused = false;
		isFastForward = false;
		isFastRewind = false;
		isSeekComplete = true;
		MP_PLAYMODE = 2;
		MP_PLAY_RATE = 1;
		surfaceView = null;
		surfaceHolder = null;
		this.mediaUrl = null;
	}
	/**
	 * 初始化播放器， 设置监听事件等
	 * <ul>
	 * 	<li>{@link ErrorListener#onError(MediaPlayer, int, int)} 错误监听</li>
	 * 	<li>{@link PreparedListener#onPrepared(MediaPlayer)} 准备完成</li>
	 * 	<li>{@link CompletionListener#onCompletion(MediaPlayer)} 播放完成</li>
	 * 	<li>{@link InfoListener#onInfo(MediaPlayer, int, int)} 播放状态及一些info通知</li>
	 * 	<li>{@link SeekCompleteListener#onSeekComplete(MediaPlayer)} seek完成</li>
	 * 	<li>{@link BufferingUpdateListener#onBufferingUpdate(MediaPlayer, int)} BufferingUpdate</li>
	 * </ul>
	 */
	public void initPlayer() {
		ALOG.info(TAG, "initialize systemMediaPlayer.");
		if (mp == null) {
			mp = new MediaPlayer();
			mp.setOnErrorListener(new ErrorListener());
			mp.setOnPreparedListener(new PreparedListener());
			mp.setOnCompletionListener(new CompletionListener());
			mp.setOnInfoListener(new InfoListener());
			mp.setOnSeekCompleteListener(new SeekCompleteListener());
			mp.setOnBufferingUpdateListener(new BufferingUpdateListener());
			if (cycleFlag == 1) {
				mp.setLooping(false);
			} else {
				mp.setLooping(true);
			}
		} else {
			mp.reset();
			mp.setDisplay(null);
			if (cycleFlag == 1) {
				mp.setLooping(false);
			} else {
				mp.setLooping(true);
			}
		}
	}

	@Override
	public void setSurfaceView(SurfaceView surfaceView) {
		this.surfaceView = surfaceView;
	}
	
	@Override
	public int joinChannel(int userChannelID) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean leaveChannel() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 开始播放，调用此接口开始播放视频。
	 */
	@Override
	public void play() {
		ALOG.info(TAG, "start play mediaStr: " + mediaUrl);
		if (TextUtils.isEmpty(mediaUrl)) {
			ALOG.info(TAG, "mediaStr is empty, stop play! ");
			return;
		}
		if (mEvent != null) {
			mEvent.clearEvent();
		}
		//如果surfaceView未创建成功，则不能进行播放，否则会黑屏
		//20180130 modify by wenzong 增加显示模式判断，255为不显示视频画面
		if (!isSurfaceCreated && videoDisplayMode != AmtMediaPlayer.HIDDEN_DISPLAY) {
			ALOG.info(TAG, "Surface did not be created, please wait! ");
			isNeedWaitSurfaceCreate = true;
			return;
		}
		//再次检测mp是否创建成功，如果没有，则再次创建
		if (mp == null) {
			initPlayer();
		}
		try {
			mp.setDataSource(mediaUrl);
			mp.prepareAsync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 暂停暂停。
	 */
	@Override
	public void pause() {
		ALOG.info(TAG, "MediaPlayer is pause.");
		if (isPause()) {
			ALOG.info(TAG, "MediaPlayer have been paused, return!");
			return;
		}
		if (!isOnPrepared && isStartPlay()) {
			ALOG.info("TAG", "the player has not been initialized, return.");
			return;
		}
		currentPlayTime = Integer.parseInt(getCurrentPlayTime());
		mp.pause();
		isPaused = true;
		String strExtMsg = "\"new_play_mode\":1,\"new_play_rate\":" + 0 
				+ ",\"old_play_mode\":" + MP_PLAYMODE
				+ ",\"old_play_rate\":" + MP_PLAY_RATE;
		MP_PLAYMODE = 1;
		MP_PLAY_RATE = 0;
		if (mEvent != null) {
			mEvent.sendEvent(MediaEventInfoListener.TYPE_EVENT_PLAYMODE_CHANGE, strExtMsg);
		}
	}

	/**
	 * 停止播放。
	 */
	@Override
	public void stop() {
		ALOG.info(TAG, "MediaPlayer is stop.");
		if (!isOnPrepared && isStartPlay()) {
			ALOG.info(TAG, "the player has not been initialized, return.");
			return;
		}
		stopPlayTime = getCurrentPlayTime();
		if (mEvent != null) {
			mEvent.clearEvent();
		}
		try {
			mp.stop();
			mp.setDisplay(null);
			//mEvent.sendEvent(MediaEventInfoListener.TYPE_EVENT_MEDIA_END, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		//初始化播放器属性
		initProperties();
	}

	/**
	 * 当前播放器是否暂停
	 */
	@Override
	public boolean isPause() {
		ALOG.info(TAG, "MediaPlayer is isOnPause: " + isPaused);
		return isPaused;
	}

	//AML芯片接口快进参数
	private final int KEY_PARAMETER_AML_PLAYER_TRICKPLAY_FORWARD = 2005;
	//AML芯片接口快退参数
	private final int KEY_PARAMETER_AML_PLAYER_TRICKPLAY_BACKWARD =2006;
	//其他芯片接口快进、快进参数
	private final int KEY_PARAMETER_PLAYER_TRICKPLAY = 1300;

	/**
	 * 快进，此接口为模拟快进，后续对接芯片快进接口。
	 * @param speed 速度，单位秒
	 */
	@Override
	public void fastForward(final int speed) {
		ALOG.info(TAG, "MediaPlayer fastForward, speed: " + speed);
		isFastForward = true;
		if (timer != null) {
			timer.cancel();
		}
		if (!isOnPrepared) {
			ALOG.info(TAG, "MediaPlayer is not prepared, return.");
			return;
		}

		//add by zw 20170628 误删，此代码为mp隐藏接口需系统提供jar包
//		if ("amlogic".equalsIgnoreCase(android.os.Build.HARDWARE)) {
//			mp.setParameter(KEY_PARAMETER_AML_PLAYER_TRICKPLAY_FORWARD, "forward:" + Math.abs(speed));
//			ALOG.info(TAG, "setParameter fastforward for AML!");
//		} else {
//			mp.setParameter(KEY_PARAMETER_PLAYER_TRICKPLAY, 1000 * speed);
//			ALOG.info(TAG, "setParameter fastforward for else!");
//		}
//
//		ALOG.info(TAG, "MediaPlayer fastForward, seekTime..." + 1000 * speed);
//		String strExtMsg = "\"new_play_mode\":3,\"new_play_rate\":" + speed
//				+ ",\"old_play_mode\":" + MP_PLAYMODE
//				+ ",\"old_play_rate\":" + MP_PLAY_RATE;
//		MP_PLAYMODE = 3;
//		MP_PLAY_RATE = speed;
//		if (mEvent != null) {
//			mEvent.sendEvent(MediaEventInfoListener.TYPE_EVENT_PLAYMODE_CHANGE, strExtMsg);
//		}


		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (isSeekComplete) {
					if (Integer.parseInt(getCurrentPlayTime()) >= (getMediaDuration() - 5)) {
						ALOG.info(TAG, "getCurrentPlayTime is above getMediaDuration.");
						timer.cancel();
						mp.start();
						String strExtMsg = "\"new_play_mode\":2,\"new_play_rate\":" + 1
								+ ",\"old_play_mode\":" + MP_PLAYMODE
								+ ",\"old_play_rate\":" + MP_PLAY_RATE;
						MP_PLAYMODE = 2;
						MP_PLAY_RATE = 1;
						if (mEvent != null) {
							mEvent.sendEvent(MediaEventInfoListener.TYPE_EVENT_PLAYMODE_CHANGE, strExtMsg);
						}
					} else {
						isSeekComplete = false;
						int seekTime = (Integer.parseInt(getCurrentPlayTime()) + Math.abs(speed)/2) * 1000;
						ALOG.info(TAG, "MediaPlayer fastForward, seekTime..." + seekTime);
						String strExtMsg = "\"new_play_mode\":3,\"new_play_rate\":" + speed
								+ ",\"old_play_mode\":" + MP_PLAYMODE
								+ ",\"old_play_rate\":" + MP_PLAY_RATE;
						MP_PLAYMODE = 3;
						MP_PLAY_RATE = speed;
						if (mEvent != null) {
							mEvent.sendEvent(MediaEventInfoListener.TYPE_EVENT_PLAYMODE_CHANGE, strExtMsg);
						}
						ALOG.info(TAG,"FF -->"+seekTime);
						mp.seekTo(seekTime);
					}
				}
			}
		}, 0, 500);
		
	}
	
	/**
	 * 快退，此接口为模拟快进，后续对接芯片快退接口。
	 * @param speed 速度，单位秒
	 */
	@Override
	public void fastRewind(final int speed) {
		ALOG.info(TAG, "MediaPlayer fastRewind, speed: " + speed);
		isFastRewind = true;
		if (timer != null) {
			timer.cancel();
		}
		if (!isOnPrepared) {
			ALOG.info(TAG, "MediaPlayer is not prepared, return.");
			return;
		}

		//add by zw 20170628 误删，此代码为mp隐藏接口需系统提供jar包
//		if ("amlogic".equalsIgnoreCase(android.os.Build.HARDWARE)) {
//			mp.setParameter(KEY_PARAMETER_AML_PLAYER_TRICKPLAY_BACKWARD, "backward:" + Math.abs(speed));
//			ALOG.info(TAG, "setParameter fastRewind for AML!");
//		} else {
//			mp.setParameter(KEY_PARAMETER_PLAYER_TRICKPLAY, 1000 * speed);
//			ALOG.info(TAG, "setParameter fastRewind for else!");
//		}
//
//		ALOG.info(TAG, "MediaPlayer fastRewind, seekTime..." + 1000 * speed);
//		String strExtMsg = "\"new_play_mode\":3,\"new_play_rate\":" + speed
//				+ ",\"old_play_mode\":" + MP_PLAYMODE
//				+ ",\"old_play_rate\":" + MP_PLAY_RATE;
//		MP_PLAYMODE = 3;
//		MP_PLAY_RATE = speed;
//		if (mEvent != null) {
//			mEvent.sendEvent(MediaEventInfoListener.TYPE_EVENT_PLAYMODE_CHANGE, strExtMsg);
//		}

		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (isSeekComplete) {
					if (Integer.parseInt(getCurrentPlayTime()) <= 5) {
						timer.cancel();
						mp.start();
						String strExtMsg = "\"new_play_mode\":2,\"new_play_rate\":" + 1
								+ ",\"old_play_mode\":" + MP_PLAYMODE
								+ ",\"old_play_rate\":" + MP_PLAY_RATE;
						MP_PLAYMODE = 2;
						MP_PLAY_RATE = 1;
						if (mEvent != null) {
							mEvent.sendEvent(MediaEventInfoListener.TYPE_EVENT_PLAYMODE_CHANGE, strExtMsg);
						}
					} else {
						isSeekComplete = false;
						int seekTime = (Integer.parseInt(getCurrentPlayTime()) - Math.abs(speed)/2) * 1000 ;
						ALOG.info(TAG, "MediaPlayer fastRewind, seekTime..." + seekTime);
						String strExtMsg = "\"new_play_mode\":3,\"new_play_rate\":" + speed
								+ ",\"old_play_mode\":" + MP_PLAYMODE
								+ ",\"old_play_rate\":" + MP_PLAY_RATE;
						MP_PLAYMODE = 3;
						MP_PLAY_RATE = speed;
						if (mEvent != null) {
							mEvent.sendEvent(MediaEventInfoListener.TYPE_EVENT_PLAYMODE_CHANGE, strExtMsg);
						}
						ALOG.info(TAG,"FF -->"+seekTime);

						mp.seekTo(seekTime);
					}
				}
			}
		}, 0, 500);
		
	}

	/**
	 * 根据时间长度，seek播放。
	 * @param type 1:点播seek 其他：由iptv底层播放器seek{@link com.amt.player.iptvplayer.IPTVPlayerPIP#playByTime(int, String)}
	 * @param timestamp seek时间长度，单位秒
	 */
	@Override
	public void playByTime(int type, String timestamp) {
		ALOG.info(TAG, "MediaPlayer playByTime, type: " + type + ", timestamp: " + timestamp);
		if (TextUtils.isEmpty(timestamp)) {
			ALOG.info(TAG, "playByTime, timestamp is Empty, return.");
			return;
		}
		if (!isOnPrepared) {
			currentPlayTime = Integer.parseInt(timestamp);
			play();
			return;
		}
		if (type == 1) {
			ALOG.info(TAG, "MediaPlayer is seeking, seekTime: " + timestamp);
			if (isStartPlay() || mp != null) {
				if (Integer.parseInt(timestamp) < getMediaDuration()) {
					mp.seekTo(Integer.parseInt(timestamp) * 1000);
				} else if (Integer.parseInt(timestamp) >= getMediaDuration()) {
					mp.seekTo(Integer.parseInt(timestamp) * 1000 - 5000);
				}
			}
		}
		
	}

	@Override
	public void rePlay() {
		ALOG.info(TAG, "MediaPlayer is rePlay.");
		if (!TextUtils.isEmpty(stopPlayTime)) {
			playByTime(1, stopPlayTime);
		} else {
			play();
		}
	}

	@Override
	public void resume() {
		ALOG.info(TAG, "MediaPlayer is resume.");
		if (timer != null) {
			timer.cancel();
		}
		if (!isOnPrepared && !isStartPlay) {
			ALOG.info(TAG, "MediaPlayer is not prepared, return.");
			return;
		}
		mp.start();
		isStartPlay = true;
		isPaused = false;
		String strExtMsg = "\"new_play_mode\":2,\"new_play_rate\":" + 1 
				+ ",\"old_play_mode\":" + MP_PLAYMODE
				+ ",\"old_play_rate\":" + MP_PLAY_RATE;
		MP_PLAYMODE = 2;
		MP_PLAY_RATE = 1;
		if (mEvent != null) {
			mEvent.sendEvent(MediaEventInfoListener.TYPE_EVENT_PLAYMODE_CHANGE, strExtMsg);
		}
	}

	@Override
	public String getCurrentPlayTime() {
		String currentTime = "0";
		if (!isStartPlay()) {
			ALOG.info(TAG, "MediaPlayer did not start play, getCurrentPlayTime is failed!");
			return "0";
		}
		try {
			if (mp != null) {
				if (isOnPrepared) {
					currentTime = String.valueOf(mp.getCurrentPosition() / 1000);
				}
			}
		} catch (Exception e) {
			ALOG.error(TAG, "MediaPlayer getCurrentPosition is on error.");
			e.printStackTrace();
		}
		ALOG.info(TAG, "getCurrentPlayTime: " + currentTime);
		return currentTime;
	}

	@Override
	public int getMediaDuration() {
		int durationTime = 0;
		if (!isStartPlay()) {
			ALOG.info(TAG, "MediaPlayer did not start play, getMediaDuration is failed!");
			return 0;
		}
		try {
			if (mp != null) {
				if (isOnPrepared) {
					durationTime = mp.getDuration() / 1000;
				}
			}
		} catch (Exception e) {
			ALOG.error(TAG, "MediaPlayer getMediaDuration is on error!");
			e.printStackTrace();
		}
		ALOG.info(TAG, "getMediaDuration: " + durationTime);
		return durationTime;
	}

	private final int MAX_PLAY_RATE = 64; //最大倍速
	@Override
	public String getPlaybackMode() {
		String playMode = "";
		String speed = (MP_PLAY_RATE >= MAX_PLAY_RATE ? MAX_PLAY_RATE : MP_PLAY_RATE) + "x";
		JSONObject playbackMode = new JSONObject();
		String strMode = "";
		try {
			switch (MP_PLAYMODE) {
				case 1 :
					playMode = "Pause";
					break;
				case 2 :
					playMode = "Normal Play";
					break;
				case 3 :
					playMode = "Trickmode";
					break;
			}
			//add by zw 20170628 条件判断错误
//			if (mp.isPlaying()) {
//				playMode = "Normal Play";
//				speed = "1x";
//			}
			playbackMode.put("PlayMode", playMode);
			playbackMode.put("Speed", speed);
			strMode = "{PlayMode:\"" + playbackMode.getString("PlayMode")
					+ "\",Speed:\"" + playbackMode.getString("Speed") + "\"}";
		} catch (Exception e) {
			ALOG.error(TAG, "MediaPlayer getPlaybackMode is on error!");
			e.printStackTrace();
		}
		ALOG.info(TAG, "getPlaybackMode: " + playbackMode.toString());
		return strMode;
	}
	
	@Override
	public void gotoStart() {
		ALOG.info(TAG, "MediaPlayer gotoStart.");
		if (isOnPrepared) {
			mp.seekTo(0);
			if (mEvent != null) {
				mEvent.sendEvent(MediaEventInfoListener.TYPE_EVENT_MEDIA_BEGINING, "");
			}
		} else {
			ALOG.info(TAG, "gotoStart(), MediaPlayer is not prepared!");
		}
	}

	@Override
	public void gotoEnd() {
		ALOG.info(TAG, "MediaPlayer gotoEnd.");
		if (isOnPrepared) {
			if((getMediaDuration() * 1000)>5000) {
				mp.seekTo(getMediaDuration() * 1000 - 5000);
			}else{
				mp.seekTo(getMediaDuration() * 1000);
				if (mEvent != null) {
					mEvent.sendEvent(MediaEventInfoListener.TYPE_EVENT_MEDIA_END, "");
				}
			}
		} else {
			ALOG.info(TAG, "gotoEnd(), MediaPlayer is not prepared!");
		}
		
	}

	@Override
	public boolean isStartPlay() {
		ALOG.info(TAG, "MediaPlayer is isOnStartPlay: " + isStartPlay);
		return isStartPlay;
	}

	@Override
	public boolean isLivePlay() {
		return false;
	}

	@Override
	public void switchAudioChannel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void switchAudioTrack(int id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void switchSubtitle() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendVendorSpecificCommand(String sXmlCmd) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int releaseMediaPlayer(int nativePlayerInstanceID) {
		ALOG.info(TAG, "releaseMediaPlayer!!!");
		if (mp != null) {
			mp.release();
			mp = null;
		}
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

	private class ErrorListener implements OnErrorListener {

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			if (mEvent != null) {
				mEvent.sendEvent(MediaEventInfoListener.TYPE_EVENT_MEDIA_ERROR, String.valueOf(what));
			}
			mp.reset();
			initProperties();
			return false;
		}
	}
	
	private class PreparedListener implements OnPreparedListener {

		@Override
		public void onPrepared(final MediaPlayer mp) {
			ALOG.info(TAG, "MediaPlayer is onPrepared and start play.");
			if (currentPlayTime != 0) {
				ALOG.info(TAG, "currentPlayTime is not empty, now seek to it.");
				mp.seekTo(currentPlayTime * 1000);
				currentPlayTime = 0;
			} else {
				mp.start();
			}
			if (mEvent != null) {
				mEvent.sendEvent(MediaEventInfoListener.TYPE_EVENT_MEDIA_BEGINING, "");
			}
			isOnPrepared = true;
			isStartPlay = true;
			isPaused = false;
		}
	}
	
	private class CompletionListener implements OnCompletionListener {

		@Override
		public void onCompletion(MediaPlayer arg0) {
			ALOG.info(TAG, "onCompletion!!!");
			if (mEvent != null) {
				mEvent.sendEvent(MediaEventInfoListener.TYPE_EVENT_MEDIA_END, "");
			}
		}
	}
	
	private class InfoListener implements OnInfoListener {

		@Override
		public boolean onInfo(MediaPlayer arg0, int what, int extra) {
			ALOG.info("4KExtra", "onInfoListener! what:" + what + ", extra:" + extra);
			switch (what) {
			case MediaPlayer.MEDIA_INFO_BUFFERING_START:// 统计缓存下溢事件的次数
				IPTVPlayer.setValue("BUFFERING_START", extra + "");// MediaControl统计次数。
				break;
			case 1008:// MediaPlayer.MEDIA_INFO_BUFFER_FULL://监控周期内缓存上溢出次数
				IPTVPlayer.setValue("BUFFER_FULL", extra + "");// MediaControl统计次数。
				break;
			}
			return false;
		}
	}
	
	private class SeekCompleteListener implements OnSeekCompleteListener {

		@Override
		public void onSeekComplete(MediaPlayer mp) {
			ALOG.info(TAG, "Is SeekCompleted let play!");
			isSeekComplete = true;
			mp.start();
			isPaused = false;
			isStartPlay = true;
//			if (mEvent != null) {
//				mEvent.sendEvent(MediaEventInfoListener.TYPE_EVENT_MEDIA_BEGINING, "");
//			}
		}
	}
	
	
	private class BufferingUpdateListener implements OnBufferingUpdateListener {

		@Override
		public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
			// TODO Auto-generated method stub
		}
	}
	
	private class SurfaceViewHolder implements SurfaceHolder.Callback {

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			ALOG.info(TAG, "surfaceChanged.");
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			ALOG.info(TAG, "surfaceCreated: " + surfaceView);
			isSurfaceCreated = true;
			if (surfaceView == null) {
				return;
			}
			if (mp != null) {
				mp.setDisplay(holder);
			}
			if (isNeedWaitSurfaceCreate) {
				play();
				isNeedWaitSurfaceCreate = false;
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			ALOG.info(TAG, "surfaceDestroyed.");
			if (mp != null) {
				mp.setDisplay(null);
			}
		}
	}

	@Override
	public void setMediaEventInfoListener(MediaEventInfoListener mediaEventInfo) {
		mEvent = mediaEventInfo;
	}
	
	@Override
	public void setSingleMedia(String mediaUrl) {
		ALOG.info(TAG, "setSingleMedia: " + mediaUrl);
		this.mediaUrl = mediaUrl;
		if (mp != null) {
			mp.reset();
			mp.setDisplay(null);
			if (surfaceView != null) {
				surfaceHolder = surfaceView.getHolder();
				surfaceHolder.addCallback(mSurfaceHolder);
			}
		}
	}
	
	@Override
	public void setCycleFlag(int cycleFlag) {
		if (mp == null) {
			return;
		}
		if (cycleFlag == 1) {
			mp.setLooping(false);
		} else {
			mp.setLooping(true);
		}
	}

	/**
	 * 设置视频窗口模式
	 * @param mode
	 */
	@Override
	public void setVideoMode(int mode) {
		videoDisplayMode = mode;
	}

}
