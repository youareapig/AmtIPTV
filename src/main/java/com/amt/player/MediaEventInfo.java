package com.amt.player;

import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.amt.app.IPTVAvtivityView;
import com.amt.player.entity.LiveChannelHelper;
import com.amt.utils.ALOG;

/**
 * 处理Event事件相关操作类
 * @author zw
 *
 */
public class MediaEventInfo implements MediaEventInfoListener {

	private final String TAG = "EventInfo";
	private MyEventInfoQueue eventQueue = new MyEventInfoQueue(5);
	private MyEventInfoQueue eventTime = new MyEventInfoQueue(5);
	private static MediaEventInfo eventInfo;
	private IPTVAvtivityView iptvView;

	private MediaEventInfo() {

	}

	/**
	 * 设计成单例，所以的event通知都通过一个实例进行操作，保证event事件读写的统一。
	 * @return
	 */
	public static MediaEventInfo getInstance() {
		if (eventInfo == null) {
			eventInfo = new MediaEventInfo();
		}
		return eventInfo;
	}

	public void setIPTVAvtivityView(IPTVAvtivityView iptvView) {
		this.iptvView = iptvView;
	}

	@JavascriptInterface
	public String getEvent() {
		String event = "";
		long time = 0;
		boolean isValid = false;
		while (!isValid) {
			event = (String) eventQueue.get();
			time = (Long) eventTime.get();
			if (System.currentTimeMillis() - time < 2000 || TextUtils.isEmpty(event)) {
				isValid = true;
			}
		}
		if (!TextUtils.isEmpty(event)) {
			return event;
		} else {
			return "";
		}

	}

	@Override
	public void sendEvent(int event, String strMessage) {
		String eventInfo = "";

		switch (event) {
			case MediaEventInfoListener.TYPE_EVENT_MEDIA_BEGINING:
				eventInfo = "{\"type\":\"EVENT_MEDIA_BEGINING\",\"instance_id\":1,\"media_code\":\""
						+ PlayerMediator.mainPlayer.getMediaCode()
						+ "\",\"entry_id\":\""
						+ PlayerMediator.mainPlayer.getEntryId() + "\"}";
				break;
			case MediaEventInfoListener.TYPE_EVENT_GO_CHANNEL:
				eventInfo = "{\"type\":\"EVENT_GO_CHANNEL\",\"instance_id\":1,"
						+ "\"channel_code\":\"" + LiveChannelHelper.getInstance().getCurrentChannel().getChannelID()
						+ "\",\"channel_num\":" + LiveChannelHelper.getInstance().getCurrentChannel().getUserChannelID() + "}";
				break;
			case MediaEventInfoListener.TYPE_EVENT_PLAYMODE_CHANGE:
				eventInfo = "{\"type\":\"EVENT_PLAYMODE_CHANGE\",\"instance_id\":1," + strMessage + "}";
				break;
			case MediaEventInfoListener.TYPE_EVENT_PLTVMODE_CHANGE:
				eventInfo = "{\"type\":\"EVENT_PLTVMODE_CHANGE\",\"service_type\":" + strMessage + "}";
				break;
			case MediaEventInfoListener.TYPE_EVENT_MEDIA_END:
				eventInfo = "{\"type\":\"EVENT_MEDIA_END\",\"instance_id\":1,\"media_code\":\""
						+ PlayerMediator.mainPlayer.getMediaCode()
						+ "\",\"entry_id\":\"" + PlayerMediator.mainPlayer.getEntryId() + "\"}";
				break;
			case MediaEventInfoListener.TYPE_EVENT_MEDIA_ERROR:
				eventInfo = "{\"type\":\"EVENT_MEDIA_ERROR\",\"instance_id\":1,\"error_code\":"
						+ strMessage + ",\"error_message:\":\"\",\"media_code\":\"" + PlayerMediator.mainPlayer.getMediaCode() + "\"}";
				break;
			case MediaEventInfoListener.TYPE_EVENT_REPLAY:
				//TODO
				break;
			case MediaEventInfoListener.TYPE_EVENT_JVM_CLIENT:
				eventInfo = "{\"type\":\"EVENT_JVM_CLIENT\",\"event_code\":3,\"event_result\":0}";
				break;
			default:
				break;
		}
		if (!TextUtils.isEmpty(eventInfo)) {
			eventTime.put(System.currentTimeMillis());
			eventQueue.put(eventInfo);
			if (iptvView != null) {
				iptvView.sendVirtualEvent();
			}
			ALOG.info(TAG, "eventInfo: " + eventInfo);
		}
	}

	@Override
	public void clearEvent() {
		ALOG.info(TAG, "clearEvent.");
		eventQueue.removeAll();
		eventTime.removeAll();
	}

}
