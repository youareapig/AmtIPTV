package com.amt.tr069;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.amt.utils.ALOG;

/**
 * 处理网管业务AIDL接口
 * Created by zw on 2017/4/21.
 */
public class ITr069Service {
	
	private static final String TR069_SERVICE = "com.androidmov.tr069.TR069Service";
	
	private Context mContext;
	private com.androidmov.tr069.ITr069Service aidl;
	
	public ITr069Service(Context context){
		mContext = context;
		if(mContext!=null && aidl == null){
			Intent intent = new Intent(TR069_SERVICE);
			mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
		}
	}
	
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			ALOG.debug("ITr069Service > onServiceDisconnected");
			aidl = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			ALOG.debug("ITr069Service > onServiceConnected");
			aidl = com.androidmov.tr069.ITr069Service.Stub.asInterface(service);
		}
	};

	public boolean setValueToTr069(String key,String value){
		if(aidl != null) {
			try {
				return aidl.setValueToTr069(key, value);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public String getValueFromTr069(String key){
		if(aidl != null){
			try {
				return aidl.getValueFromTr069(key);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public void notifyMessageTotr069(String msg, String arg0, String arg1, String arg2){
		if(aidl != null){
			try {
				aidl.notifyMessageTotr069(msg, arg0, arg1, arg2);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

		}
	}
	
	public void unBindService(){
		if(mContext!=null){
			try {
				mContext.unbindService(mServiceConnection);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
