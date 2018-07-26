package com.amt.amtdata.dataI;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.amt.amtdata.AmtDataManager;
import com.amt.amtdata.AmtPermission;
import com.amt.amtdata.IAmtDataAidl;
import com.amt.amtdata.IDataCallBack;
import com.amt.amtdata.IPTVData;
import com.amt.amtdata.ProcessHelper;
import com.amt.app.IptvApp;
import com.amt.config.Config;
import com.amt.utils.ALOG;
import com.amt.utils.CUBootLogHelper;
import com.amt.utils.USBHelper;


public class AmtDataAIdlService extends Service{

	public static final String TAG = "AmtDataAIdlService";

	@Override
	public void onCreate() {
		super.onCreate();
		ALOG.info(TAG,"AmtDataAIdlService > onCreate");
	}

	@Override
	public IBinder onBind(Intent intent) {
		ALOG.info(TAG,"AmtDataAIdlService > onBind");
		return new IAmtDataSerive();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		ALOG.info(TAG,"AmtDataAIdlService > onUnbind");
		return super.onUnbind(intent);
	}
	@Override
	public void onDestroy() {
		ALOG.info(TAG,"AmtDataAIdlService > onDestroy");
		super.onDestroy();
	}

	class IAmtDataSerive extends IAmtDataAidl.Stub {

		public AmtDataAIdlService getBinder(){
			return AmtDataAIdlService.this;
		}
		@Override
		public int putString(String key, String value) throws RemoteException {
			int result =0;
			AmtPermission permisson = checkWritePermisson();
			ALOG.info(TAG,"putString > key : " + key + ", value : " + value + ", isWritePermisson : " + permisson.isWritePermissonGranted);
			if(Config.PACKAGENAME_TR069.equals(permisson.callingPackageName)) {
				IptvApp.mTR069.handleTr069PutData(key, value);
			} else {
				if(permisson.isWritePermissonGranted){
					AmtDataManager.putString(key,value,permisson.callingPackageName);
					result = 1;
				}else{
					ALOG.error(TAG, "write data failed! Permisson Denied!");
				}
			}
			return result;
		}
		@Override
		public String getString(String key, String defValue) throws RemoteException {
			String value = "";
			AmtPermission permisson = checkWritePermisson();
			if("com.androidmov.tr069".equals(permisson.callingPackageName)) {
				value = IptvApp.mTR069.handleTr069GetData(key);
			}else if("VALIDTIME".equalsIgnoreCase(key)||"VAILDTIME".equalsIgnoreCase(key)){
				value =  Config.timeBox;
				return value;
			}
			else {
				value = AmtDataManager.getString(key,defValue);
				if(TextUtils.isEmpty(value)){
					value = defValue;
				}
			}
			ALOG.info(TAG,"getString > key : "+key+", value : "+value+", calling pid:"+permisson.callingPackageName);
			return value;
		}
		@Override
		public int putBoolean(String key,boolean value){
			int result =0;
			AmtPermission permisson = checkWritePermisson();
			ALOG.info(TAG,"putBoolean > key : " + key + ", value : " + value + ", isWritePermisson : " + permisson.isWritePermissonGranted);
			if(permisson.isWritePermissonGranted){
				if(IPTVData.Config_Device_Log_Enable.equals(key)){
					CUBootLogHelper.notifyDeviceLog(value, USBHelper.usbPath+CUBootLogHelper.PATH_DEVICEINFO);
				}
				AmtDataManager.putBoolean(key,value,permisson.callingPackageName);
				result = 1;
			}else{
				ALOG.error(TAG, "write data failed! Permisson Denied!");
			}
			return result;
		}
		@Override
		public boolean getBoolean(String key,boolean defValue){
			boolean value = AmtDataManager.getBoolean(key,defValue);
			ALOG.info(TAG,"getBoolean > key : "+key+", value : "+value);
			return value;
		}

		@Override
		public void registDataCallBack(IDataCallBack callback) throws RemoteException {
			AmtPermission permisson = checkWritePermisson();
			AmtDataManager.setDataCallback(permisson.callingPackageName,callback);
		}

		@Override
		public void unRegistDataCallBack() throws RemoteException {
			AmtPermission permisson = checkWritePermisson();
			AmtDataManager.removeDataCallBack(permisson.callingPackageName);
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
				int permissonCheck = checkCallingPermission("com.amt.iptvdata.write.permisson");
				permission.isWritePermissonGranted = permissonCheck == PackageManager.PERMISSION_GRANTED;
			}
			permission.callingPackageName = ProcessHelper.getProcessName(AmtDataAIdlService.this, permission);
			return permission;
		}
	}

}
