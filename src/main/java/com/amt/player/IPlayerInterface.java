package com.amt.player;

import android.view.SurfaceView;


public interface IPlayerInterface {
	public final static int MSG_PLAYER_JOINCHANNEL = 1;
	public final static int MSG_PLAYER_SETMEDIA = 2;
	public final static int MSG_PLAYER_DISPLAY = 3;
	public final static int MSG_PLAYER_STARTPLAY = 4;
	public final static int MSG_PLAYER_PAUSE = 5;
	public final static int MSG_PLAYER_PLAY = 6;
	public final static int MSG_PLAYER_STOP = 7;
	public final static int MSG_PLAYER_FASTFORWARD = 8;
	public final static int MSG_PLAYER_FASTREWIND = 9;
	public final static int MSG_PLAYER_SETALPHA = 10;
	public final static int MSG_PLAYER_EVNET = 11;
	public final static int MSG_PLAYER_LEAVECHANNEL = 12;
	public final static int MSG_PLAYER_SEEK = 13;
	public final static int MSG_PLAYER_GOTOEND = 14;
	public final static int MSG_PLAYER_GOTOSTART = 15;
	
	int joinChannel(int userChannelID);
	
	boolean leaveChannel();
	
	void play();
	
	void pause();
	
	void stop();
	
	boolean isPause();
	
	void fastForward(final int speed);
	
	void fastRewind(final int speed);
	
	void playByTime(int type, String timestamp);
	
	void rePlay();
	
	void resume();
	
	String getCurrentPlayTime(); 
	
	int getMediaDuration();
	
	String getPlaybackMode();
	
	void gotoStart();
	
	void gotoEnd();
	
	boolean isStartPlay();
	
	boolean isLivePlay();
	
	void switchAudioChannel();
	
	void switchAudioTrack(int id);
	
	void switchSubtitle();
	
	void sendVendorSpecificCommand(String sXmlCmd);
	
	int releaseMediaPlayer(int nativePlayerInstanceID);
	
	void addSingleMedia(int nIndex, String mediaStr);
	
	void addBatchMedia(String batchMediaStr);
	
	void setMediaEventInfoListener(MediaEventInfoListener mediaEventInfo);	
	
	void setSingleMedia(String mediaUrl);
	
	void setCycleFlag(int cycleFlag);
	
	void setSurfaceView(SurfaceView surfaceView);

	void setVideoMode(int mode);
}
