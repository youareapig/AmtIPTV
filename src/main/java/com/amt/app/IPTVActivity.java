package com.amt.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.SyMedia.SyDebug.RemoteBroadCastReceiver;
import com.amt.app.view.AudioTrackView;
import com.amt.app.view.ChannelNoView;
import com.amt.app.view.LoadingView;
import com.amt.app.view.ShiftView;
import com.amt.app.view.VolumeView;
import com.amt.config.Config;
import com.amt.dialog.AmtDialogManager;
import com.amt.jsinterface.Iptv2EPG;
import com.amt.player.AmtMediaPlayer;
import com.amt.player.MediaEventInfo;
import com.amt.player.MediaEventInfoListener;
import com.amt.player.PlayerMediator;
import com.amt.utils.ALOG;
import com.amt.utils.APKHelper;
import com.amt.utils.ResolutionHelper;
import com.amt.utils.Utils;
import com.amt.utils.keymap.EPGKey;
import com.amt.utils.keymap.GlobalKeyHelper;
import com.amt.utils.keymap.KeyHelper;
import com.amt.utils.powermanager.StandbyBroadcastReceived;
import com.amt.webview.IPTVWebChromeClient;
import com.amt.webview.IPTVWebCustomClient;
import com.amt.webview.IPTVWebView;
import com.amt.webview.IPTVWebViewClient;
import com.amt.webview.IPTVWebviewListener;
import com.amt.webview.WebViewManager;
import com.android.smart.terminal.iptvNew.R;

import java.util.Arrays;

public class IPTVActivity extends Activity implements IPTVAvtivityView {

	public static Context context;
	public static final String TAG = "IPTVActivity";
	private Handler mHandler = null;
	private LoadingView mLoadingView = null;
	private ShiftView mShiftView = null;
	private AudioTrackView mAudioTrackView = null;
	private ChannelNoView mChannelNoView = null;
	private VolumeView mVolumeView = null;
	/**video视图容器，用于存放surfaceView的容器*/
	private RelativeLayout layoutVideo ;
	/**Webview容器，用于存放Webview的容器。*/
	private RelativeLayout layoutWebview;
	/**永远在最顶层的layout容器，用于存放本地音量条、时移图标、加载圈、本地播控UI、本地频道图标等*/
	private RelativeLayout layoutTopUI=null;
	private  IPTVWebView iptvWebView=null;
	public static final String WEBTAG_IPTV = "web_iptv";
	private View rootView;
	private WebViewManager webManager;
	private String backStorePlayUrl;
	private boolean needRestorePlay = false;
	private boolean isLivePlay = false;
	private SoftInptHelper mInputHelper;
	/**Dialog模块管理器对象  所有的Dialog创建都将通过这个来进行**/
	public static AmtDialogManager mDialogManager;
	/**标识是否是首次启动Activity,用于在onResume生命周期内判断是否需要创建播放器*/
	private boolean isFirstEnter = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		this.setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
		super.onCreate(savedInstanceState);
		getWindow().setFlags(0x1000000, 0x1000000);
		getWindow().setFlags(0x1000000, 0x1000000);
		context = this;
		ALOG.debug(TAG,"onCreate >>>");
		rootView = LayoutInflater.from(this).inflate(R.layout.activity_iptv_main, null);
		setContentView(rootView);
		mDialogManager= AmtDialogManager.getManager(this);
		webManager = WebViewManager.getManager();
		//检查机顶盒是否合法
		Config.checkSTB();
		initView();
		initHandler();
		mInputHelper = new SoftInptHelper(context,layoutTopUI);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				//20171102 add by wenzong
				if(Config.showTimeBoxInfo){
					ALOG.info("show valid time infomation by Toast");
					Toast.makeText(context,"当前版本带有时间限制："+Config.timeBox,Toast.LENGTH_LONG).show();
				}
			}
		},5 * 1000);

	}



	private void initHandler() {
		mHandler = new Handler();
	}


	/**
	 * 初始化布局控件
	 */
	private void initView() {
		layoutVideo = (RelativeLayout)findViewById(R.id.layout_video);
		layoutWebview = (RelativeLayout)findViewById(R.id.layout_webview);
		layoutTopUI = (RelativeLayout)findViewById(R.id.layout_top);
		//创建webview实例并初始化
		webManager.setWebViewContainer(layoutWebview);
		webManager.setIptvView(this);
		iptvWebView = webManager.createWebviewAutoAdd(context, WEBTAG_IPTV);
		iptvWebView.setWebCustomClient(new IPTVWebCustomClient(this));
		iptvWebView.setWebChromeClient(new IPTVWebChromeClient(context, this));
		iptvWebView.setWebViewClient(new IPTVWebViewClient(this));
		iptvWebView.setOnError(new IPTVWebviewListener());

		//初始化本地UI控件
		mLoadingView = new LoadingView(layoutTopUI);
		mShiftView = new ShiftView(layoutTopUI);
		mAudioTrackView = new AudioTrackView(layoutTopUI);
		mChannelNoView = new ChannelNoView(layoutTopUI);
		mVolumeView = new VolumeView(context,layoutTopUI);


		webManager.showWebView();
		IptvApp.authManager.setAuthView(context,rootView);
		Iptv2EPG.getIptv2EPG().addJsObject();
		Iptv2EPG.getIptv2EPG().setIptvView(this);
		PlayerMediator.mainPlayer.setIPTVAvtivityView(this);
		ResolutionHelper.calcWebResolution();
	}

	@Override
	protected void onPause() {
		super.onPause();
		ALOG.debug(TAG, "onPause >>> ");
		//iptv进入其他引用时做认证判断
		IptvApp.authManager.checkAuth("onPause");
		if(IptvApp.authManager.isAuth) {
			iptvWebView.onPause();
			iptvWebView.pauseTimers();
		}else{
			webManager.stopLoading();
		}

		if (PlayerMediator.mainPlayer != null) {
			backStorePlayUrl = PlayerMediator.mainPlayer.getStrMediaPlayUrl();
			isLivePlay = PlayerMediator.mainPlayer.isLivePlay();
			if (PlayerMediator.mainPlayer.isStartPlay()) {
				needRestorePlay = true;
			}
			PlayerMediator.mainPlayer.releaseMediaPlayer(0);
			PlayerMediator.pipPlayer.releaseMediaPlayer(0);
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		ALOG.debug(TAG, "onRestart >>> ");
	}

	@Override
	protected void onResume() {
		super.onResume();
		ALOG.debug(TAG, "onResume >>> needRestorePlay:"+needRestorePlay+", isLivePlay:"+isLivePlay+", isFirstEnter : "+isFirstEnter+", isAuth:"+IptvApp.authManager.isAuth);

		if (StandbyBroadcastReceived.isNeedKill){
		//	待机后不能自杀，在非该系统应用进程杀IPTV， AMS机制启动的IPTV就会报错，我们唤醒后在自杀。
			StandbyBroadcastReceived.isNeedKill =false;
			ALOG.info("kill myself  -->"+StandbyBroadcastReceived.isNeedKill);
			android.os.Process.killProcess(android.os.Process.myPid());
		}

		if (isRunGame) {
			MediaEventInfo.getInstance().sendEvent(MediaEventInfoListener.TYPE_EVENT_JVM_CLIENT, "");
			isRunGame = false;
		}

		//这段代码一定要加在IptvApp.authManager.checkAuth("onResume");之前，因为checkAuth内部可能会修改 isAuth状态。
		if(IptvApp.authManager.isAuth){
			iptvWebView.onResume();
			iptvWebView.resumeTimers();
		}
		//iptv进入其他apk返回时，判断认证
		IptvApp.authManager.checkAuth("onResume");
		if (PlayerMediator.mainPlayer != null && !isFirstEnter) {
			PlayerMediator.mainPlayer.createPlayer();
			PlayerMediator.pipPlayer.createPlayer();
		}
		if (needRestorePlay) {
			if (isLivePlay) {
				PlayerMediator.mainPlayer.refreshVideoDisplay();
				PlayerMediator.mainPlayer.rePlay();
			} else {
				if (TextUtils.isEmpty(backStorePlayUrl)) {
					ALOG.info(TAG, "backStorePlayUrl is empty, do not replay!!!");
				} else {
					PlayerMediator.mainPlayer.setSingleMedia(backStorePlayUrl);
					PlayerMediator.mainPlayer.refreshVideoDisplay();
					PlayerMediator.mainPlayer.rePlay();
				}
			}
			needRestorePlay = false;
		}
		isFirstEnter = false;
	}

	@Override
	protected void onStop() {
		super.onStop();
		ALOG.debug(TAG, "onStop >>> "+Utils.getTopPackageName());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ALOG.debug(TAG, "onDestroy >>> ");
		IptvApp.mNetManager.unRegistReceiver();
		RemoteBroadCastReceiver.releaseRemoteReceiver(IptvApp.app);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		ALOG.info(TAG,"onNewIntent >>> extra : "+Utils.viewBundle(intent.getExtras()));
		String intentMsg = intent.getStringExtra("intentMsg");
		ALOG.debug(TAG,"onNewIntent >>> intentMsg : "+intentMsg);
		if(!TextUtils.isEmpty(intentMsg)){
			backStorePlayUrl = "";
			isLivePlay = false;
			if("LIVE_CHANNEL_LIST".equalsIgnoreCase(intentMsg)){//直播
				dealFourColorKey(EPGKey.LIVE,true);
			}else if("VOD_CATEGORY_PAGE".equalsIgnoreCase(intentMsg)){//点播
				dealFourColorKey(EPGKey.VOD,true);
			}else if("TVOD_CHANNEL_LIST".equalsIgnoreCase(intentMsg)){//回看
				dealFourColorKey(EPGKey.TVOD,true);
			}else if("Infomation".equalsIgnoreCase(intentMsg)){//信息
				dealFourColorKey(EPGKey.INFO,true);
			}else if("EPGDomain".equalsIgnoreCase(intentMsg)){//首页
				dealHomeKey(EPGKey.HOME,true);
			}else if(URLUtil.isNetworkUrl(intentMsg)){
				webManager.stopAndLoadNewUrl(intentMsg);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		ALOG.debug(TAG, "keycode:"+keyCode);
		//转换键值为 EPG键值
		int epgKeycode = KeyHelper.getEPGKeyCode(keyCode);
		// 保存EPG
		if(epgKeycode == EPGKey.SAVE_EPG && Config.isAutoSaveWebPage){
			webManager.saveEPGPage();
			return true;
		}
		if (dealFourColorKey(epgKeycode,false)) {
			return true;
		} else if (dealHomeKey(epgKeycode,false)) {
			return true;
		} else if (epgKeycode == EPGKey.VOLUME_ADD) { //音量加
			if (PlayerMediator.mainPlayer.getAudioVolumeUIFlag() == 1) {
				mVolumeView.volumeControl(1);
				return true;
			}
		} else if (epgKeycode == EPGKey.VOLUME_MIN) { //音量减
			if (PlayerMediator.mainPlayer.getAudioVolumeUIFlag() == 1) {
				mVolumeView.volumeControl(-1);
				return true;
			}
		} else if(epgKeycode == EPGKey.MUTE){ //静音键
			if(PlayerMediator.mainPlayer.getMuteUIFlag() == 1){
				mVolumeView.setMuteAction();
				return true;
			}
		} else if(epgKeycode == EPGKey.AUDIO_TRACK){ //声道切换键
			if(PlayerMediator.mainPlayer.getAudioTrackUIFlag() == 1){
				PlayerMediator.mainPlayer.switchAudioChannel();
				mAudioTrackView.setAudiotrack(PlayerMediator.mainPlayer.getCurrentAudioChannel());
				return true;
			}
		}
		KeyEvent epgKeyEvent = new KeyEvent(KeyEvent.ACTION_DOWN,epgKeycode);
		webManager.sendKeyToWeb(epgKeyEvent);
		if(keyCode != EPGKey.VIRTUAL_KEY){
			KeyHelper.keydown(true,keyCode);
		}
		return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		ALOG.info(TAG,"onKeyUp > keycode:"+keyCode);
		KeyHelper.keydown(false,keyCode);
		return super.onKeyUp(keyCode, event);
	}


	@Override
	public void showLoading(final boolean isShow) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(isShow){
					mLoadingView.show();
				}else{
					mLoadingView.hide();
				}
			}
		});
	}

	@Override
	public void onPageViewSizeChanged(int w, int h) {
		ResolutionHelper.epgWidth = w;
		ResolutionHelper.epgHeight = h;
		ResolutionHelper.calcWebResolution();
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				webManager.setInitialScale((int) (ResolutionHelper.zoomX * 100), (int) (ResolutionHelper.zoomY * 100));
			}
		});
	}
	private boolean isRunGame = false;
	@Override
	public void runGame(int w, int h, String strJad, String strJar, String strParam) {
		ALOG.info(TAG,"runGame >> width : "+w+", height : "+h+", strJad : "+strJad+", strJar : "+strJar+", strParam : "+strParam);
		//运行JVM游戏APK。
		APKHelper.goJVMGameApk(this,w,h,strJad,strJar,strParam);
		isRunGame = true;
	}

	@Override
	public void showInputMethod(final String message,final int selectionStart,final int selectionEnd,final int top) {
//		ALOG.info(TAG,"showInputMethod > message : "+message+", selection : "+selection+", top : "+top);
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				int cursorStart = selectionStart < 0 ? 0 : selectionStart;
				int cursorEnd = selectionEnd < 0 ? 0 : selectionEnd;
				String value = message;
				//考虑一下字符串被选中的区域，吧被选中的区域截掉，留下非选中字符串。以cursorStart为光标位置
				if(cursorStart < cursorEnd && !TextUtils.isEmpty(message) && message.length() >= cursorEnd){
					String selectedValue = message.substring(cursorStart, cursorEnd);
					value = message.replace(selectedValue, "");
				}
				mInputHelper.showInput(value,cursorStart,(int) ((float) (top) * ResolutionHelper.zoomY));
			}
		});
	}

	private boolean isHaveShiftTime = false;
	@Override
	public void sendVirtualEvent() {
		if (PlayerMediator.mainPlayer.getVideoDisplayMode() == AmtMediaPlayer.FULL_SCREEN) {
			ALOG.info("sendVirtualEvent---> getShiftStatus: " + PlayerMediator.mainPlayer.getShiftStatus());
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mShiftView.showShiftImage(PlayerMediator.mainPlayer.getShiftStatus());
				}
			});
		}
		if (isHaveShiftTime && !PlayerMediator.mainPlayer.getShiftStatus()) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mShiftView.showShift2liveImage();
				}
			});

		}
		isHaveShiftTime = PlayerMediator.mainPlayer.getShiftStatus();

		this.onKeyDown(EPGKey.VIRTUAL_KEY, new KeyEvent(KeyEvent.ACTION_DOWN, EPGKey.VIRTUAL_KEY));
	}

	@Override
	public void onPushData(int flag, String arg, String arg2) {

	}

	@Override
	public void showShiftImage(final boolean isShow) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mShiftView.showShiftImage(isShow);
			}
		});
	}

    @Override
    public void showShift2liveImage(boolean show) {

    }

    @Override
	public RelativeLayout getLayout(int id) {
		RelativeLayout layout = null;
		switch (id) {
			case LYOUT_TOP_UI:
				layout = layoutTopUI;
				break;
			case LYOUT_WEBVIEW_UI:
				layout = layoutWebview;
				break;
			case LYOUT_VIDEO_UI:
				layout = layoutVideo;
				break;
			default:
				break;
		}

		return layout;
	}

	/**
	 * 处理四色键
	 * @param keycode EPG键值
	 * @param clearWebview 是否清除浏览器页面画面。
	 * @return
	 */
	private boolean dealFourColorKey(int keycode,boolean clearWebview) {
		if (keycode >= 0) {
			int index = Arrays.binarySearch(EPGKey.FOUR_COLOR_KEY, keycode);
			ALOG.info(TAG, "dealFourColorKey---> keycode: " + keycode + ", index: " + index);
			if (index >= 0) {
				String url = Iptv2EPG.getIptv2EPG().authInfo.CTCGetConfig(keycode + "");
				if (TextUtils.isEmpty(url)) {
					url = GlobalKeyHelper.getQuickEnterUrl(keycode);
				}
				if (!TextUtils.isEmpty(url)) {
					if (PlayerMediator.mainPlayer.isStartPlay()) {
						PlayerMediator.mainPlayer.stop();
					}
					if (PlayerMediator.pipPlayer.isStartPlay()) {
						PlayerMediator.pipPlayer.stop();
					}
					//20171221 modify by wenzong stopAndLoadNewUrl接口内部会clearWebview清屏。要根据实际需求来确定是否清屏
					if (clearWebview) {
						webManager.stopAndLoadNewUrl(url);
					} else {
						webManager.getCurrentWebview().stopLoading();
						webManager.getCurrentWebview().loadUrl(url);
					}
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 处理首页键
	 * @param keycode 首页键EPG键值
	 * @param clearWebview 是否清除浏览器页面画面。
	 * @return
	 */
	private boolean dealHomeKey(int keycode,boolean clearWebview) {
		if (keycode == EPGKey.HOME) {
			String url = Iptv2EPG.getIptv2EPG().authInfo.CTCGetConfig("EPGDomain");
			if (!TextUtils.isEmpty(url)) {
				if (PlayerMediator.mainPlayer.isStartPlay()) {
					PlayerMediator.mainPlayer.stop();
				}
				if (PlayerMediator.pipPlayer.isStartPlay()) {
					PlayerMediator.pipPlayer.stop();
				}
				//20171221 modify by wenzong stopAndLoadNewUrl接口内部会clearWebview清屏。要根据实际需求来确定是否清屏
				if (clearWebview) {
					webManager.stopAndLoadNewUrl(url);
				} else {
					webManager.getCurrentWebview().stopLoading();
					webManager.getCurrentWebview().loadUrl(url);
				}
				return true;
			}
		}
		return false;
	}
}
