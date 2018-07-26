package com.amt.player;

import com.SyMedia.webkit.SyJavascriptInterface;
import com.amt.utils.ALOG;

public class PlayerJSInterface {

	public static final String TAG = "MediaPlayerManager";
	private static final PlayerJSInterface instance = new PlayerJSInterface();
	private PlayerJSInterface() {
		
	}
	public static PlayerJSInterface getInstance() {
		return instance;
	}

	@SyJavascriptInterface
	public PlayerMediator createPlayer() {
		ALOG.info(TAG, "createJsPlayer!!!");
		return new PlayerMediator();
	}
}
