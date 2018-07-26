package com.amt.utils;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.amt.amtdata.AmtDataManager;
import com.amt.amtdata.IPTVData;
import com.amt.config.Config;
import com.amt.player.iptvplayer.IPTVPlayer;
import com.amt.webview.WebViewManager;
import com.android.smart.terminal.iptvNew.R;


/**
 * Degbug开关页面。
 * 1、IPTV LOG(永久生效)
 * 2、IPTV LOG(重启失效)
 * 3、保存EPG
 * 4、Chromium LOG
 * 5、V8 LOG
 * 6、跳过零配置(重启生效)
 * @author zw 20170306
 *
 */
public class DebugActivity extends Activity {
	
	private ToggleButton  btnIPTVlog;
	private ToggleButton  btnIPTVlogPer;
	private ToggleButton  btnSaveEPG;
	private Spinner webCoreSpinner;
	private Spinner v8Spinner;
	private ToggleButton btnZeroSet;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_debug);
		btnIPTVlog = (ToggleButton) findViewById(R.id.IPTV_toggleButton);
		btnIPTVlogPer = (ToggleButton) findViewById(R.id.IPTV_Per_toggleButton);
		btnSaveEPG = (ToggleButton) findViewById(R.id.IPTV_SaveEPG_toggleButton);
		webCoreSpinner = (Spinner) findViewById(R.id.WebCoreSpinner);
		v8Spinner = (Spinner) findViewById(R.id.V8Spinner);
		btnZeroSet = (ToggleButton) findViewById(R.id.zeroSet_toggleButton);
		
		btnIPTVlogPer.setChecked(AmtDataManager.getBoolean(IPTVData.IPTV_LOG_ENABLE, false));
		btnIPTVlog.setChecked(ALOG.SECRET_DEBUG);
		btnSaveEPG.setChecked(com.amt.config.Config.isAutoSaveWebPage);
		webCoreSpinner.setSelection(com.amt.config.Config.webCoreLevel);
		v8Spinner.setSelection(com.amt.config.Config.v8SpinnerLevel);
		int zeroStatus = Integer.valueOf(AmtDataManager.getString(IPTVData.IPTV_ZEROSETTING_STATUS,"0"));
		btnZeroSet.setChecked(zeroStatus == 1 ? true : false);
		btnIPTVlog.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				ALOG.DEBUG = ALOG.SECRET_DEBUG = isChecked;
				IPTVPlayer.setValue("setLogEnable",ALOG.DEBUG ? "1" : "0");
				if(WebViewManager.getManager().getCurrentWebview()!=null){
					WebViewManager.getManager().getCurrentWebview().getCustom().setIPTVDebug(isChecked);
				}
			}
		});
		
		btnIPTVlogPer.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				AmtDataManager.putBoolean(IPTVData.IPTV_LOG_ENABLE,isChecked,"");
				Log.i(ALOG.TAG,"IPTV Log > "+isChecked+"  >> Perpetual");
				ALOG.DEBUG = ALOG.SECRET_DEBUG = isChecked;
				IPTVPlayer.setValue("setLogEnable",ALOG.DEBUG ? "1" : "0");
				if(WebViewManager.getManager().getCurrentWebview()!=null){
					WebViewManager.getManager().getCurrentWebview().getCustom().setIPTVDebug(isChecked);
				}
			}
		});
		
		btnSaveEPG.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				Log.i(ALOG.TAG,"Auto save EPG  > "+isChecked);
				AmtDataManager.putBoolean(IPTVData.IPTV_SAVEEPG_ENABLE,isChecked,"");
			}
		});
		
		webCoreSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					String webcoreLevel = (String) webCoreSpinner.getSelectedItem();
					Log.i(ALOG.TAG,"Webcore Log level > "+webcoreLevel);
					if(WebViewManager.getManager().getCurrentWebview()!=null){
						Config.webCoreLevel = position;
						WebViewManager.getManager().getCurrentWebview().getCustom().setWebcoreDebug(Integer.valueOf(webcoreLevel));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});
		
		v8Spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					String v8Level = (String) v8Spinner.getSelectedItem();
					Log.i(ALOG.TAG,"V8 Log level > "+v8Level);
					if(WebViewManager.getManager().getCurrentWebview()!=null){
						Config.v8SpinnerLevel = position;
						WebViewManager.getManager().getCurrentWebview().getCustom().setV8Debug(Integer.valueOf(v8Level));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});
		
		btnZeroSet.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				AmtDataManager.putString(IPTVData.IPTV_ZEROSETTING_STATUS,isChecked ? "1" : "0","");
			}
		});
	}
}
