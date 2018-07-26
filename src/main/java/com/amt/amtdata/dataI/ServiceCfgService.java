package com.amt.amtdata.dataI;


import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.amt.amtdata.AmtDataManager;
import com.amt.amtdata.AmtPermission;
import com.amt.amtdata.IPTVData;
import com.amt.amtdata.ProcessHelper;
import com.amt.app.IptvApp;
import com.amt.config.Config;
import com.amt.utils.ALOG;
import com.amt.utils.DeviceInfo;
import com.amt.utils.CUBootLogHelper;
import com.amt.utils.USBHelper;

public class ServiceCfgService extends Service {
	public static final String TAG = "ServiceCfgService";

	private ServiceCfgServiceBinder binder = new ServiceCfgServiceBinder();

	public IBinder onBind(Intent intent) {
		ALOG.info(TAG,"ServiceCfgService > onBind");
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		ALOG.info(TAG,"ServiceCfgService > onCreate");
	}

	private final class ServiceCfgServiceBinder extends
			com.android.smart.terminal.iptv.aidl.IServiceCfg.Stub {

		@Override
		public boolean putString(String key, final String value)
				throws RemoteException {
			ALOG.debug(TAG,"ServiceCfg.aidl putString > "+key+":"+value);
			AmtPermission permission = checkWritePermisson();
			if(Config.PACKAGENAME_TR069.equals(permission.callingPackageName)) {
				IptvApp.mTR069.handleTr069PutData(key, value);
				return true;
			} else {
				return AmtDataManager.putString(key, value, permission.callingPackageName) ==1;
			}
		}

		@Override
		public boolean putInt(String key, int value) throws RemoteException {
			AmtPermission permission = checkWritePermisson();
			boolean str = AmtDataManager.putInt(key, value, permission.callingPackageName) == 1;
			return str;
		}

		@Override
		public boolean putBoolean(String key, boolean value)
				throws RemoteException {
			AmtPermission permission = checkWritePermisson();
			if(IPTVData.Config_Device_Log_Enable.equals(key)){
				CUBootLogHelper.notifyDeviceLog(value, USBHelper.usbPath+CUBootLogHelper.PATH_DEVICEINFO);
			}
			return AmtDataManager.putBoolean(key, value, permission.callingPackageName) == 1;
		}

		@Override
		public String getString(String key, String defValue)
				throws RemoteException {
			AmtPermission permission = checkWritePermisson();
			String value = "";
			if(Config.PACKAGENAME_TR069.equals(permission.callingPackageName)) {
				value =  IptvApp.mTR069.handleTr069GetData(key);
			}else if ("Service/ServiceInfo/STBID".equalsIgnoreCase(key)) {
				value =  DeviceInfo.STBID;
			}else if ("Service/ServiceInfo/MAC".equalsIgnoreCase(key)) {
				value =  DeviceInfo.MAC;
			}else if("VALIDTIME".equalsIgnoreCase(key)||"VAILDTIME".equalsIgnoreCase(key)){
				value =  Config.timeBox;
			}else{
				value = AmtDataManager.getString(key, defValue);
				if(TextUtils.isEmpty(value)){
					value = defValue;
				}
			}
			ALOG.info(TAG,"getString > key:"+key+", value : "+value+", calling pid:"+permission.callingPackageName);
			return value;
		}

		@Override
		public int getInt(String key, int defValue) throws RemoteException {
			if(IPTVData.IPTV_ZEROSETTING_STATUS.equals(key)){
				return Integer.valueOf(AmtDataManager.getString(key,"0"));
			}
			return AmtDataManager.getInt(key, defValue);
		}

		@Override
		public boolean getBoolean(String key, boolean defValue)
				throws RemoteException {
			return AmtDataManager.getBoolean(key, defValue);
		}

		@Override
		public String getChannelUrl() throws RemoteException {
			return "";
		}

		/**
		 * 根据用户频道号，获取相关信息xb20140220(还未用，原型)
		 * 
		 * @param UserChannelID
		 *            用户频道号
		 * @return
		 * @throws RemoteException
		 */
		public String getChannel(int UserChannelID) throws RemoteException {
			return null;
		}

		/**
		 * 检查是否有写权限
		 * @return
		 */
		private AmtPermission checkWritePermisson(){
			AmtPermission permission = new AmtPermission();
			//在这里进行远程APK的验证。
			//权限验证
			permission.callingUid = getCallingUid();
			permission.callingPid = getCallingPid();
			if(permission.callingUid == 0){//root权限
				permission.isWritePermissonGranted = true;
			}else{
				int permissonCheck = checkCallingPermission("com.android.smart.terminal.iptv.aidl.SERVICES");
				permission.isWritePermissonGranted = permissonCheck == PackageManager.PERMISSION_GRANTED;
			}
			permission.callingPackageName = ProcessHelper.getProcessName(ServiceCfgService.this, permission);
			return permission;
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ALOG.debug(TAG, "ServiceCfgService onDestory");
	}

}
