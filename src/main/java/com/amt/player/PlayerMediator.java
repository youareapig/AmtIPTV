package com.amt.player;


import com.SyMedia.webkit.SyJavascriptInterface;
import com.amt.config.Config;
import com.amt.utils.ALOG;
import com.amt.app.IPTVActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 播放器中介者
 * @author zw 20170414
 *
 */
public class PlayerMediator {
	public static AmtMediaPlayer mainPlayer;
	public static AmtMediaPlayer pipPlayer;
	static {
		mainPlayer = new AmtMediaPlayer(IPTVActivity.context, 0);
		if (Config.IS_PIPPLAYER) {
			pipPlayer = new AmtMediaPlayer(IPTVActivity.context, 1);
		} else {
			pipPlayer = mainPlayer;
		}
	}


	private int haveorNoVoice = 0;

	@SyJavascriptInterface
	public void bindNativePlayerInstance(int nativePlayerInstanceID) {
		// TODO: 2017/6/21
	}
	
	@SyJavascriptInterface
	public int getNativePlayerInstanceID() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getNativePlayerInstanceID();
		} else {
			return pipPlayer.getNativePlayerInstanceID();
		}
	}
	
	@SyJavascriptInterface
	public void setNativePlayerinstanceID(int nativePlayerinstanceID) {
		if (haveorNoVoice == 0) {
			mainPlayer.setNativePlayerinstanceID(nativePlayerinstanceID);
		} else {
			pipPlayer.setNativePlayerinstanceID(nativePlayerinstanceID);
		}
	}
	
	@SyJavascriptInterface
	public int getSingleOrPlaylistMode() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getSingleOrPlaylistMode();
		} else {
			return pipPlayer.getSingleOrPlaylistMode();
		}
	}
	
	@SyJavascriptInterface
	public void setSingleOrPlaylistMode(int singleOrPlaylistMode) {
		if (haveorNoVoice == 0) {
			mainPlayer.setSingleOrPlaylistMode(singleOrPlaylistMode);
		} else {
			pipPlayer.setSingleOrPlaylistMode(singleOrPlaylistMode);
		}
	}
	
	@SyJavascriptInterface
	public int getVideoDisplayMode() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getVideoDisplayMode();
		} else {
			return pipPlayer.getVideoDisplayMode();
		}
	}
	
	@SyJavascriptInterface
	public void setVideoDisplayMode(int videoDisplayMode) {
		if (haveorNoVoice == 0) {
			mainPlayer.setVideoDisplayMode(videoDisplayMode);
		} else {
			pipPlayer.setVideoDisplayMode(videoDisplayMode);
		}
	}
	
	@SyJavascriptInterface
	public int getVideoDisplayLeft() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getVideoDisplayLeft();
		} else {
			return pipPlayer.getVideoDisplayLeft();
		}
	}
	
	@SyJavascriptInterface
	public void setVideoDisplayLeft(int videoDisplayLeft) {
		if (haveorNoVoice == 0) {
			mainPlayer.setVideoDisplayLeft(videoDisplayLeft);
		} else {
			pipPlayer.setVideoDisplayLeft(videoDisplayLeft);
		}
	}
	
	@SyJavascriptInterface
	public int getVideoDisplayTop() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getVideoDisplayTop();
		} else {
			return pipPlayer.getVideoDisplayTop();
		}
	}
	
	@SyJavascriptInterface
	public void setVideoDisplayTop(int videoDisplayTop) {
		if (haveorNoVoice == 0) {
			mainPlayer.setVideoDisplayTop(videoDisplayTop);
		} else {
			pipPlayer.setVideoDisplayTop(videoDisplayTop);
		}
	}
	
	@SyJavascriptInterface
	public int getVideoDisplayWidth() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getVideoDisplayWidth();
		} else {
			return pipPlayer.getVideoDisplayWidth();
		}
	}
	
	@SyJavascriptInterface
	public void setVideoDisplayWidth(int videoDisplayWidth) {
		if (haveorNoVoice == 0) {
			mainPlayer.setVideoDisplayWidth(videoDisplayWidth);
		} else {
			pipPlayer.setVideoDisplayWidth(videoDisplayWidth);
		}
	}
	
	@SyJavascriptInterface
	public int getVideoDisplayHeight() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getVideoDisplayHeight();
		} else {
			return pipPlayer.getVideoDisplayHeight();
		}
	}
	
	@SyJavascriptInterface
	public void setVideoDisplayHeight(int videoDisplayHeight) {
		if (haveorNoVoice == 0) {
			mainPlayer.setVideoDisplayHeight(videoDisplayHeight);
		} else {
			pipPlayer.setVideoDisplayHeight(videoDisplayHeight);
		}
	}
	
	@SyJavascriptInterface
	public int getMuteFlag() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getMuteFlag();
		} else {
			return pipPlayer.getMuteFlag();
		}
	}
	
	@SyJavascriptInterface
	public void setMuteFlag(int muteFlag) {
		if (haveorNoVoice == 0) {
			mainPlayer.setMuteFlag(muteFlag);
		} else {
			pipPlayer.setMuteFlag(muteFlag);
		}
	}
	
	@SyJavascriptInterface
	public int getNativeUIFlag() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getNativeUIFlag();
		} else {
			return pipPlayer.getNativeUIFlag();
		}
	}
	
	@SyJavascriptInterface
	public void setNativeUIFlag(int nativeUIFlag) {
		if (haveorNoVoice == 0) {
			mainPlayer.setNativeUIFlag(nativeUIFlag);
		} else {
			pipPlayer.setNativeUIFlag(nativeUIFlag);
		}
	}
	
	@SyJavascriptInterface
	public int getMuteUIFlag() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getMuteUIFlag();
		} else {
			return pipPlayer.getMuteUIFlag();
		}
	}
	
	@SyJavascriptInterface
	public void setMuteUIFlag(int muteUIFlag) {
		if (haveorNoVoice == 0) {
			mainPlayer.setMuteUIFlag(muteUIFlag);
		} else {
			pipPlayer.setMuteUIFlag(muteUIFlag);
		}
	}
	
	@SyJavascriptInterface
	public int getAudioVolumeUIFlag() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getAudioVolumeUIFlag();
		} else {
			return pipPlayer.getAudioVolumeUIFlag();
		}
	}
	
	@SyJavascriptInterface
	public void setAudioVolumeUIFlag(int audioVolumeUIFlag) {
		if (haveorNoVoice == 0) {
			mainPlayer.setAudioVolumeUIFlag(audioVolumeUIFlag);
		} else {
			pipPlayer.setAudioVolumeUIFlag(audioVolumeUIFlag);
		}
	}
	
	@SyJavascriptInterface
	public int getAudioTrackUIFlag() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getAudioTrackUIFlag();
		} else {
			return pipPlayer.getAudioTrackUIFlag();
		}
	}
	
	@SyJavascriptInterface
	public void setAudioTrackUIFlag(int audioTrackUIFlag) {
		if (haveorNoVoice == 0) {
			mainPlayer.setAudioTrackUIFlag(audioTrackUIFlag);
		} else {
			pipPlayer.setAudioTrackUIFlag(audioTrackUIFlag);
		}
	}
	
	@SyJavascriptInterface
	public int getProgressBarUIFlag() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getProgressBarUIFlag();
		} else {
			return pipPlayer.getProgressBarUIFlag();
		}
	}
	
	@SyJavascriptInterface
	public void setProgressBarUIFlag(int progressBarUIFlag) {
		if (haveorNoVoice == 0) {
			mainPlayer.setProgressBarUIFlag(progressBarUIFlag);
		} else {
			pipPlayer.setProgressBarUIFlag(progressBarUIFlag);
		}
	}
	
	@SyJavascriptInterface
	public int getChannelNoUIFlag() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getChannelNoUIFlag();
		} else {
			return pipPlayer.getChannelNoUIFlag();
		}
	}
	
	@SyJavascriptInterface
	public void setChannelNoUIFlag(int channelNoUIFlag) {
		if (haveorNoVoice == 0) {
			mainPlayer.setChannelNoUIFlag(channelNoUIFlag);
		} else {
			pipPlayer.setChannelNoUIFlag(channelNoUIFlag);
		}
	}
	
	@SyJavascriptInterface
	public int getSubtitileFlag() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getSubtitileFlag();
		} else {
			return pipPlayer.getSubtitileFlag();
		}
	}
	
	@SyJavascriptInterface
	public void setAllowTrickmodeFlag(int allowTrickmodeFlag) {
		if (haveorNoVoice == 0) {
			mainPlayer.setAllowTrickmodeFlag(allowTrickmodeFlag);
		} else {
			pipPlayer.setAllowTrickmodeFlag(allowTrickmodeFlag);
		}
	}
	
	@SyJavascriptInterface
	public int getAllowTrickmodeFlag() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getAllowTrickmodeFlag();
		} else {
			return pipPlayer.getAllowTrickmodeFlag();
		}
	}
	
	@SyJavascriptInterface
	public int getRandomFlag() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getRandomFlag();
		} else {
			return pipPlayer.getRandomFlag();
		}
	}

	@SyJavascriptInterface
	public void setRandomFlag(int randomFlag) {
		if (haveorNoVoice == 0) {
			mainPlayer.setRandomFlag(randomFlag);
		} else {
			pipPlayer.setRandomFlag(randomFlag);
		}
	}
	
	@SyJavascriptInterface
	public void setSubtitileFlag(int subtitileFlag) {
		if (haveorNoVoice == 0) {
			mainPlayer.setSubtitileFlag(subtitileFlag);
		} else {
			pipPlayer.setSubtitileFlag(subtitileFlag);
		}
	}
	
	@SyJavascriptInterface
	public int getCycleFlag() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getCycleFlag();
		} else {
			return pipPlayer.getCycleFlag();
		}
	}
	
	@SyJavascriptInterface
	public void setCycleFlag(int cycleFlag) {
		if (haveorNoVoice == 0) {
			mainPlayer.setCycleFlag(cycleFlag);
		} else {
			pipPlayer.setCycleFlag(cycleFlag);
		}
	}
	
	@SyJavascriptInterface
	public int getVolume() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getVolume();
		} else {
			return pipPlayer.getVolume();
		}
	}
	
	@SyJavascriptInterface
	public void setVolume(int volume) {
		if (haveorNoVoice == 0) {
			mainPlayer.setVolume(volume);
		} else {
			pipPlayer.setVolume(volume);
		}
	}
	
	@SyJavascriptInterface
	public String getMediaCode() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getMediaCode();
		} else {
			return pipPlayer.getMediaCode();
		}
	}

	@SyJavascriptInterface
	public void setMediaCode(String mediaCode) {
		if (haveorNoVoice == 0) {
			mainPlayer.setMediaCode(mediaCode);
		} else {
			pipPlayer.setMediaCode(mediaCode);
		}
	}

	@SyJavascriptInterface
	public String getEntryId() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getEntryId();
		} else {
			return pipPlayer.getEntryId();
		}
	}

	@SyJavascriptInterface
	public void setEntryId(String entryId) {
		if (haveorNoVoice == 0) {
			mainPlayer.setEntryId(entryId);
		} else {
			pipPlayer.setEntryId(entryId);
		}
	}
	
	@SyJavascriptInterface
	public void initMediaPlayer(byte nativePlayerinstanceID, byte playlistFlag,
			byte videoDisplayMode, int height, int width, int left, int top,
			byte muteFlag, byte useNativeUIFlag, byte subtitleFlag,
			byte videoAlpha, byte cycleFlag, byte randomFlag, byte autoDelFlag) {
		if (haveorNoVoice == 0) {
			mainPlayer.initMediaPlayer(nativePlayerinstanceID, playlistFlag,
					videoDisplayMode, height, width, left, top,
					muteFlag, useNativeUIFlag, subtitleFlag,
					videoAlpha, cycleFlag, randomFlag, autoDelFlag);
		} else {
			pipPlayer.initMediaPlayer(nativePlayerinstanceID, playlistFlag,
					videoDisplayMode, height, width, left, top,
					muteFlag, useNativeUIFlag, subtitleFlag,
					videoAlpha, cycleFlag, randomFlag, autoDelFlag);
		}
	}
	
	@SyJavascriptInterface
	public void setVideoDisplayArea(int left, int top, int width, int height) {
		if (haveorNoVoice == 0) {
			mainPlayer.setVideoDisplayArea(left, top, width, height);
		} else {
			pipPlayer.setVideoDisplayArea(left, top, width, height);
		}
	}

	@SyJavascriptInterface
	public void setSingleMedia(String mediaUrl) { 
		if (haveorNoVoice == 0) {
			mainPlayer.setSingleMedia(mediaUrl);
		} else {
			pipPlayer.setSingleMedia(mediaUrl);
		}
	}

	@SyJavascriptInterface
	public void addSingleMedia(int index, String mediaStr) {
		// TODO: 2017/6/21
	}

	public void addBatchMedia(String batchMediaStr) {
		// TODO: 2017/6/21
	}


	@SyJavascriptInterface
	public void refreshVideoDisplay() {
		if (haveorNoVoice == 0) {
			mainPlayer.refreshVideoDisplay();
		} else {
			pipPlayer.refreshVideoDisplay();
		}
	}

	@SyJavascriptInterface
	public int joinChannel(String userChannelID) {
		ALOG.debug("joinChannel--string:"+userChannelID);
		return joinChannel(Integer.parseInt(userChannelID));
	}
	
	//@SyJavascriptInterface  20171019 mmodify by wenzong 欧成国说如果joinchannel(int)也加了SyJavascriptInterface注解，
	// 会导致浏览器区分不了joinchannel(String)接口，导致页面传入String类型参数时，也调用到int接口导致参数丢失，直播黑屏。
	public int joinChannel(int userChannelID) {
		ALOG.debug("joinChannel--int:"+userChannelID);
		if (haveorNoVoice == 0) {
			return mainPlayer.joinChannel(userChannelID);
		} else {
			return pipPlayer.joinChannel(userChannelID);
		}
	}

	@SyJavascriptInterface
	public boolean leaveChannel() {
		if (haveorNoVoice == 0) {
			return mainPlayer.leaveChannel();
		} else {
			return pipPlayer.leaveChannel();
		}
	}
	
	@SyJavascriptInterface
	public void play() {
		if (haveorNoVoice == 0) {
			mainPlayer.play();
		} else {
			pipPlayer.play();
		}
		
	}
	
	@SyJavascriptInterface
	public void playFromStart() {
		if (haveorNoVoice == 0) {
			mainPlayer.playFromStart();
		} else {
			pipPlayer.playFromStart();
		}
	}

	@SyJavascriptInterface
	public void pause() {
		if (haveorNoVoice == 0) {
			mainPlayer.pause();
		} else {
			pipPlayer.pause();
		}
	}

	@SyJavascriptInterface
	public void stop() {
		if (haveorNoVoice == 0) {
			mainPlayer.stop();
		} else {
			pipPlayer.stop();
		}
	}

	public boolean isPause() {
		if (haveorNoVoice == 0) {
			return mainPlayer.isPause();
		} else {
			return pipPlayer.isPause();
		}
	}

	@SyJavascriptInterface
	public void fastForward(int speed) {
		if (haveorNoVoice == 0) {
			mainPlayer.fastForward(speed);
		} else {
			pipPlayer.fastForward(speed);
		}
		
	}

	@SyJavascriptInterface
	public void fastRewind(int speed) {
		if (haveorNoVoice == 0) {
			mainPlayer.fastRewind(speed);
		} else {
			pipPlayer.fastRewind(speed);
		}
		
	}

	@SyJavascriptInterface
	public void playByTime(int type, String timestamp) {
		playByTime(type, timestamp, 1);
	}
	
	@SyJavascriptInterface
	public void playByTime(int type, String timestamp, int speed) {
		if (haveorNoVoice == 0) {
			mainPlayer.playByTime(type, timestamp, speed);
		} else {
			pipPlayer.playByTime(type, timestamp, speed);
		}
	}

	@SyJavascriptInterface
	public void rePlay() {
		if (haveorNoVoice == 0) {
			mainPlayer.rePlay();
		} else {
			pipPlayer.rePlay();
		}
	}

	@SyJavascriptInterface
	public void resume() {
		if (haveorNoVoice == 0) {
			mainPlayer.resume();
		} else {
			pipPlayer.resume();
		}
	}

	@SyJavascriptInterface
	public String getCurrentPlayTime() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getCurrentPlayTime();
		} else {
			return pipPlayer.getCurrentPlayTime();
		}
	}

	@SyJavascriptInterface
	public int getMediaDuration() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getMediaDuration();
		} else {
			return pipPlayer.getMediaDuration();
		}
	}

	@SyJavascriptInterface
	public String getPlaybackMode() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getPlaybackMode();
		} else {
			return pipPlayer.getPlaybackMode();
		}
	}

	@SyJavascriptInterface
	public void gotoStart() {
		if (haveorNoVoice == 0) {
			mainPlayer.gotoStart();
		} else {
			pipPlayer.gotoStart();
		}
	}

	@SyJavascriptInterface
	public void gotoEnd() {
		if (haveorNoVoice == 0) {
			mainPlayer.gotoEnd();
		} else {
			pipPlayer.gotoEnd();
		}
	}

	public boolean isStartPlay() {
		if (haveorNoVoice == 0) {
			return mainPlayer.isStartPlay();
		} else {
			return pipPlayer.isStartPlay();
		}
	}

	@SyJavascriptInterface
	public boolean isLivePlay() {
		if (haveorNoVoice == 0) {
			return mainPlayer.isLivePlay();
		} else {
			return pipPlayer.isLivePlay();
		}
	}

	@SyJavascriptInterface
	public void switchAudioChannel() {
		if (haveorNoVoice == 0) {
			mainPlayer.switchAudioChannel();
		} else {
			pipPlayer.switchAudioChannel();
		}
	}

	@SyJavascriptInterface
	public void selectAudio(int id) {
		if (haveorNoVoice == 0) {
			mainPlayer.switchAudioTrack(id);
		} else {
			pipPlayer.switchAudioTrack(id);
		}
	}
	
	@SyJavascriptInterface
	public int getAudioPID() {
		// TODO: 2017/6/21
		return 0;
	}

	@SyJavascriptInterface
	public Object getAudioPIDs() {
		// TODO: 2017/6/21
		return null;
	}

	@SyJavascriptInterface
	public void setAudioPID() {
		// TODO: 2017/6/21
	}

	@SyJavascriptInterface
	public String getCurrentAudioTrackInfo() {
		//TODO
		JSONObject jObject = new JSONObject();
		try {
			jObject.put("PID", -1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jObject.toString();
	}
	
	@SyJavascriptInterface
	public String getAllAudioTrackInfo() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getAllAudioTrackInfo();
		} else {
			return pipPlayer.getAllAudioTrackInfo();
		}
	}

	@SyJavascriptInterface
	public String getAudioTrack() {
		// TODO: 2017/6/21
		return "";
	}
	
	@SyJavascriptInterface
	public void switchAudioTrack() {
		// TODO: 2017/6/21
	}

	@SyJavascriptInterface
	public String getSubtitle() {
		// TODO: 2017/6/21
		return "";
	}

	@SyJavascriptInterface
	public int getSubtitlePID() {
		// TODO: 2017/6/21
		return 0;
	}

	@SyJavascriptInterface
	public Object getSubtitlePIDs() {
		// TODO: 2017/6/21
		return null;
	}

	@SyJavascriptInterface
	public void setSubtitlePID() {
		// TODO: 2017/6/21
	}


	@SyJavascriptInterface
	public void switchSubtitle() {
		if (haveorNoVoice == 0) {
			mainPlayer.switchSubtitle();
		} else {
			pipPlayer.switchSubtitle();
		}
		
	}
	
	@SyJavascriptInterface
	public int getChannelNum() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getChannelNum();
		} else {
			return pipPlayer.getChannelNum();
		}
	}
	
	@SyJavascriptInterface
	public String getCurrentAudioChannel() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getCurrentAudioChannel();
		} else {
			return pipPlayer.getCurrentAudioChannel();
		}
	}
	
	@SyJavascriptInterface
	public int releaseMediaPlayer(int nativePlayerinstanceID) {
		if (haveorNoVoice == 0) {
			return mainPlayer.releaseMediaPlayer(1);
		} else {
			return pipPlayer.releaseMediaPlayer(1);
		}
	}
	
	
	
	@SyJavascriptInterface
	public void set(String ioStr, int info) {
		if ("HaveorNoVoice".equals(ioStr)) {
			if (info == 1) {
				this.haveorNoVoice = 1;
			} 
		}
		if (haveorNoVoice == 0) {
			mainPlayer.set(ioStr, info);
		} else {
			pipPlayer.set(ioStr, info);
		}
	}

	@SyJavascriptInterface
	public Object get(String ioStr) {
		if (haveorNoVoice == 0) {
			return mainPlayer.get(ioStr);
		} else {
			return pipPlayer.get(ioStr);
		}
	}
	
	@SyJavascriptInterface
	public void setVideoAlpha(int newVal) {
		//TODO
	}
	@SyJavascriptInterface
	public int getVideoAlpha() {
		return 0;
	}

	@SyJavascriptInterface
	public void setVendorSpecificAttr(String vendorSpecificAttr) {
		// TODO: 2017/6/21
	}

	@SyJavascriptInterface
	public String getVendorSpecificAttr() {
		// TODO: 2017/6/21  
		return null;
	}
	
	@SyJavascriptInterface
	public void sendVendorSpecificCommand(String xml) {
		// TODO: 2017/6/21  
	}

	@SyJavascriptInterface
	public int GetLastError() {
		// TODO: 2017/6/21
		return 0;
	}

	@SyJavascriptInterface
	public int getMediaCount() {
		// TODO: 2017/6/21
		return 0;
	}

	@SyJavascriptInterface
	public int getCurrentIndex() {
		// TODO: 2017/6/21
		return 0;
	}

	@SyJavascriptInterface
	public String getEntryID() {
		if (haveorNoVoice == 0) {
			return mainPlayer.getEntryId();
		} else {
			return pipPlayer.getEntryId();
		}
	}

	@SyJavascriptInterface
	public void removeMediaByEntryID(String entryID) {
		// TODO: 2017/6/21
	}

	@SyJavascriptInterface
	public void removeMediaByIndex(int index) {
		// TODO: 2017/6/21
	}

	@SyJavascriptInterface
	public void moveMediaByIndex(String entryID, int toIndex) {
		// TODO: 2017/6/21
	}

	@SyJavascriptInterface
	public void moveMediaByIndex1(int index, int toIndex) {
		// TODO: 2017/6/21
	}

	@SyJavascriptInterface
	public void moveMediaByOffset(String entryID, int offset) {
		// TODO: 2017/6/21
	}

	@SyJavascriptInterface
	public void moveMediaByOffset1(int index, int offset) {
		// TODO: 2017/6/21
	}

	@SyJavascriptInterface
	public void moveMediaToNext(String entryID) {
		// TODO: 2017/6/21
	}

	@SyJavascriptInterface
	public void moveMediaToNext1(String entryID) {
		// TODO: 2017/6/21
	}

	@SyJavascriptInterface
	public void moveMediaToPrevious(String entryID) {
		// TODO: 2017/6/21
	}

	@SyJavascriptInterface
	public void moveMediaToPrevious1(String entryID) {
		// TODO: 2017/6/21
	}

	@SyJavascriptInterface
	public void moveMediaToFirst(String entryID) {
		// TODO: 2017/6/21
	}

	@SyJavascriptInterface
	public void moveMediaToFirst1(String entryID) {
		// TODO: 2017/6/21
	}

	@SyJavascriptInterface
	public void moveMediaToLast1(String entryID) {
		// TODO: 2017/6/21
	}

	@SyJavascriptInterface
	public void selectMediaByIndex(int index) {
		// TODO: 2017/6/21
	}

	@SyJavascriptInterface
	public void SelectMediaByEntryID(int entryID) {
		// TODO: 2017/6/21
	}

	@SyJavascriptInterface
	public void selectMediaByOffset(int offset) {
		// TODO: 2017/6/21
	}

	@SyJavascriptInterface
	public void selectNext() {
		// TODO: 2017/6/21
	}

	@SyJavascriptInterface
	public void selectPrevious() {
		// TODO: 2017/6/21
	}

	@SyJavascriptInterface
	public void selectFirst() {
		// TODO: 2017/6/21
	}

	@SyJavascriptInterface
	public void selectLast() {
		// TODO: 2017/6/21
	}

	@SyJavascriptInterface
	public void SelectMediaByEntryID(String entryID) {
		// TODO: 2017/6/21
	}


	@SyJavascriptInterface
	public void clearAllMedia() {
		// TODO: 2017/6/21
	}
	
	@SyJavascriptInterface
	public String getPlaylist() {
		// TODO: 2017/6/21
		return "";
	}

	
}
