package com.amt.player;

public interface MediaEventInfoListener {
	
	public static final int TYPE_EVENT_GO_CHANNEL = 1;// 当按下CH+,CH-、数字键、页面链接等，终端将打开下一个频道时触发
	public static final int TYPE_EVENT_MEDIA_END = 2;// 当媒体播放器中的媒体播放到末端时触发
	public static final int TYPE_EVENT_MEDIA_BEGINING = 3;// 当媒体播放器中的媒体播放到起始端时触发
	public static final int TYPE_EVENT_MEDIA_ERROR = 4;// 当媒体播放器，发生异常时触发
	public static final int TYPE_EVENT_PLAYMODE_CHANGE = 5;// 当媒体播放器的playbackmode发生改变的时候触发
	public static final int TYPE_EVENT_REMINDER = 6;// 当终端定时提醒时触发
	public static final int TYPE_EVENT_JVM_CLIENT = 7;// 当增值业务客户端产生下载、启动、退出、出错等状态发生时触发
	public static final int TYPE_EVENT_PLTVMODE_CHANGE = 9;//直播、时移切换状态
	public static final int TYPE_EVENT_PLAYER = 21;// 四川用的机顶盒异常处理
	public static final int TYPE_EVENT_REPLAY = 31;// 断网后接收不到数据要求重新播放
	public static final int TYPE_EVENT_ETHDOWN = 32;// 断网通知,终端管理检测的太慢了，设置Ｂ平面网络已断开
	public static final int TYPE_EVENT_ETHUP = 33;// 播放过程发现断开网络后会每隔1秒去检测是否连上，当连上后会有通知，如果断开的时候没有收到B平面断开的通知，会一直无法操作，这时要把B平面的标志设回去
	/**
	 * 
	 * @return
	 */
	String getEvent();
	/**
	 * 
	 * @param event
	 * @param strExtMsg
	 */
	void sendEvent(int event, String strExtMsg);
	/**
	 * 
	 */
	void clearEvent();
}
