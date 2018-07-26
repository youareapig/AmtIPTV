package com.amt.jsinterface;

import com.SyMedia.webkit.SyJavascriptInterface;
import com.amt.player.MediaEventInfo;
import com.amt.utils.ALOG;
import com.amt.utils.APKHelper;
import com.amt.app.IPTVActivity;


/**
 * EPG js扩展对象，详情请参考中国电信集团IPTV3.0规范《扩展JavaScript对象.pdf》附录
 * @author zw
 */
public class Utility {

	private static final String TAG = "Utility";
	
	/**
	 * 底层libCTC_MediaControl.so通知IPTV播放器事件
	 * @param event
	 * @param strMessage
	 */
	public void onEvent(int event, String strMessage) {
		ALOG.info(TAG,"onEvent >> event : "+event+", message :"+strMessage);
		MediaEventInfo.getInstance().sendEvent(event, strMessage);
	}

	@SyJavascriptInterface
	public String getEvent() {
		String eventStr = MediaEventInfo.getInstance().getEvent();
		ALOG.info(TAG,"getEvent >> "+eventStr);
		return eventStr;
	}

	@SyJavascriptInterface
	public void setBrowserWindowAlpha(byte alpha) {
		ALOG.info(TAG,"setBrowserWindowAlpha > "+alpha);
		//WebViewManager.getManager().getCurrentWebview().getCustom().setWebsetAlpha(alpha);
	}

	/**
	 * 启动本地设置APK
	 */
	@SyJavascriptInterface
	public void startLocalCfg() {
		ALOG.info(TAG,"startLocalCfg!");
		APKHelper.goSettings(IPTVActivity.context);
	}

	/**
	 * 收发件箱的控制
	 */
	@SyJavascriptInterface
	public void MsgBoxCommand(String pcURI, String pcCmd, String pcCmdInfo, String pcOutBuf, int iOutLen) {
		// TODO: 2017/6/21
	}

	/**
	 * 页面调用接口获取数据
	 * @param keyName
	 * @return
     */
	@SyJavascriptInterface
	public String getValueByName(String keyName) {
		String result = Iptv2EPG.getIptv2EPG().authInfo.CTCGetConfig(keyName);
		ALOG.info(TAG,"getValueByName > "+keyName+" : "+result);
		return result;
	}

	/**
	 * 页面调用接口设置数据（功能调用）
	 * @param name
	 * @return
     */
	@SyJavascriptInterface
	public int setValueByName(String name) {
		ALOG.info(TAG,"setValueByName -->"+name);
		if (name.contains("exitIptvApp")) {
			//TODO 跳转Launcher
			APKHelper.goLauncher(IPTVActivity.context);
		}
		return 1;
	}

	/**
	 * 页面调用接口设置数据
	 * @param name
	 * @param value
     * @return
     */
	@SyJavascriptInterface
	public int setValueByName(String name, String value) {
		ALOG.info(TAG,"setValueByName > "+name+" : "+value);
		Iptv2EPG.getIptv2EPG().authInfo.CTCSetConfig(name,value);
		return 1;
	}

}
