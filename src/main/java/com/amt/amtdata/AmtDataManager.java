package com.amt.amtdata;

import android.content.ContentValues;
import android.content.Context;
import android.os.Build;
import android.os.RemoteException;
import android.text.TextUtils;

import com.amt.amtdata.backupdb.BackupDBManager;
import com.amt.amtdata.backupdb.BackupData;
import com.amt.amtdata.dao.DataFactory;
import com.amt.amtdata.dao.IDataInterface;
import com.amt.utils.ALOG;
import com.amt.utils.Security;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;


/**
 * IPTV业务数据管理服务。提供数据读写接口，管理数据去向、来源,自动备份数据等。
 * <br><i>此接口仅提供业务数据的读写接口，若要获取网络信息，如当前IP、MAC、网络方式等，请使用{@link com.amt.net.NetConnectManager},
 * <br><i>若要获取机顶盒硬件信息,如stbid，model，OUI，型号等，请使用{@link com.amt.utils.DeviceInfo}
 * <br>Created by DonWZ on 2016-12-16
 */
public class AmtDataManager {

	/**存放数据监听对象*/
	private static HashMap<String,IDataCallBack> dataCallBackList;
	private static IDataInterface iData;
	private static Context mContext;
	/**数据备份管理器*/
	private static BackupDBManager mBackupManager;
	public static final String TAG = "AmtDataManager";
	/**配置文件/数据库的版本号*/
	private static final String CONFIG_VERSION = "Config_Version";

	/**
	 * 数据模块初始化，需要在程序入口处调用一次。
	 * @param context
     */
	public synchronized static void init(Context context){
		//有关单例模式的线程安全及性能问题，可以参考资料：http://blog.csdn.net/cselmu9/article/details/51366946
		mContext = context;
		if(iData == null){
			iData = DataFactory.creat(context);
		}
		if(Build.VERSION.SDK_INT >= 21){
			//清除IPTV安装包目录下procache目录的进程信息文件
			new Thread(){
				@Override
				public void run() {
					ProcessHelper.clearProcFiles(mContext);
				}
			}.start();
		}
	}

	private AmtDataManager(){
	}

	public static synchronized String getString(String key, String defValue){
		//优先从配置文件中读取
		String value = iData.getString(key,defValue);
		//若配置文件中读取为空，从预置数据里读取一遍。
		if(TextUtils.isEmpty(value) && PresetData.isPreset(key)){
			value = PresetData.getPresetData(key);
		}
		//若为加密项，解密数据
		if(checkNeedEncryptOrDecrypt(key) && !TextUtils.isEmpty(value) && !value.equals(defValue)){
			if(!ALOG.SECRET_DEBUG){
				ALOG.debug(TAG,"getString>"+key+":"+value);
			}
			value = Security.DecryptionDES(value);
			ALOG.secretLog(TAG,"getString>"+key+":"+value);
		}else{
			ALOG.debug(TAG,"getString>"+key+":"+value);
		}
		return value;
	}

	/**
	 * 保存/更新 数据
	 * @param key
	 * @param value
	 * @param from 数据来源。一般传递调用者包名。某些情况下可能需要收集数据更新的来源
     * @return
     */
	public static synchronized int putString(String key, String value, String from){
		if(TextUtils.isEmpty(key)){
			return -1;
		}
		if(mBackupManager == null){
			mBackupManager = BackupDBManager.getBackManager(mContext);
		}
		String oldValue = getString(key, "");
		String encryptionValue = value;
		//如果是加密项，加密后存储
		if(checkNeedEncryptOrDecrypt(key)){
			encryptionValue = Security.EncryptionDES(value);
			if(ALOG.SECRET_DEBUG){
				ALOG.secretLog(TAG,"putString > "+key+" : "+value+" (old : "+oldValue+"), from PID : "+from);
			}else{
				ALOG.info(TAG,"putString > "+key+" : ? , from PID : "+from);
			}
		}else{
			ALOG.info(TAG,"putString > "+key+" : "+value+" (old : "+oldValue+"), from PID : "+from);
		}
		int result = iData.putString(key,encryptionValue);
		if(result == 1){
			if(TextUtils.isEmpty(from)){
				from = mContext.getPackageName();
			}
			mBackupManager.backupData(key, value, from);
		}
		notifyDataChanged(from,key,oldValue, value);
		return result;
	}


	/**
	 * 获取备份数据
	 * @param key
	 * @return
     */
	public static synchronized BackupData getBackupData(String key){
		return mBackupManager.getBackupData(key);
	}

	/**
	 * 在全新项目中不使用此接口，在现网老版本IPTV升级为新IPTV时可以使用。在实际使用过程中，会发现很多数据不知该用String还是int，造成选择困难。
	 * 且在多进程共享数据时，容易造成耦合度高的问题，比如设置和IPTV公用一个数据，约定使用String或者int类型，就是耦合度高的表现。
	 * 加上此接口，仅仅是为了兼容老版本的配置文件。
	 * @param key
	 * @param defValue
     * @return
     */
	@Deprecated
	public static synchronized int getInt(String key,int defValue){
		int value = iData.getInt(key, defValue);
		ALOG.debug(TAG,"getInt > "+key+" : "+value);
		return value;
	}

	/**
	 * 在全新项目中不使用此接口，在现网老版本IPTV升级为新IPTV时可以使用。在实际使用过程中，会发现很多数据不知该用String还是int，造成选择困难。
	 * 且在多进程共享数据时，容易造成耦合度高的问题，比如设置和IPTV公用一个数据，约定使用String或者int类型，就是耦合度高的表现。
	 * 加上此接口，仅仅是为了兼容老版本的配置文件。
	 * @param key
	 * @param value
	 * @param from
     * @return
     */
	@Deprecated
	public static synchronized int putInt(String key ,int value,String from){
		if(TextUtils.isEmpty(key)){
			return -1;
		}
		if(mBackupManager == null){
			mBackupManager = BackupDBManager.getBackManager(mContext);
		}
		int result = iData.putInt(key, value);
		int oldValue = getInt(key, -1);
		ALOG.debug(TAG,"putInt > "+key+" : "+value+"(old : "+oldValue+"), from PID : "+from+", result : "+result);
		if(result == 1){
			if(TextUtils.isEmpty(from)){
				from = mContext.getPackageName();
			}
			mBackupManager.backupData(key, value+"", from);
		}
		notifyDataChanged(from,key,oldValue+"", value+"");
		return result;
	}

	public static synchronized boolean getBoolean(String key,boolean defValue){
		boolean value = iData.getBoolean(key, defValue);
		ALOG.debug(TAG,"getBoolean > "+key+" : "+value);
		return value;
	}

	public static synchronized int putBoolean(String key,boolean value,String from){
		if(TextUtils.isEmpty(key)){
			return -1;
		}
		if(mBackupManager == null){
			mBackupManager = BackupDBManager.getBackManager(mContext);
		}
		int result = iData.putBoolean(key, value);
		ALOG.debug(TAG,"putBoolean > "+key+" : "+value+", from "+from+", result : "+result);
		if(result == 1){
			if(TextUtils.isEmpty(from)){
				from = mContext.getPackageName();
			}
			mBackupManager.backupData(key, value+"", from);
		}
		notifyDataChanged(from, key, !value+"", value+"");
		return result;
	}

	/**
	 * 批量保存数据。一般用于IPTV本身初始化业务数据时使用。不对外提供此接口
	 * @param values ContentValues 对象。批量存放数据
	 * @return
     */
	public static synchronized int putStringBatch(ContentValues values){
		if(values == null || values.size() <= 0){
			return -1;
		}
		if(mBackupManager == null){
			mBackupManager = BackupDBManager.getBackManager(mContext);
		}
		int result = iData.putStringBatch(values);
		if(result == 1){
			mBackupManager.backupData(values);
		}
		return result;
	}

	/**
	 * 获取配置文件的版本号
	 * @return
     */
	public static synchronized int getConfigFileVersion(){
		return Integer.valueOf(iData.getString(CONFIG_VERSION,"0"));
	}

	/**
	 * 更新配置文件的版本号
	 * @param newVersion
	 * @return
     */
	public static synchronized boolean updateConfigFileVersion(int newVersion){
		return iData.putString(CONFIG_VERSION,String.valueOf(newVersion)) == 1;
	}

	/**
	 * 获取配置文件的绝对路径
	 * @return
     */
	public static String getConfigFilePath(){
		return iData.getFilePath();
	}

	/**
	 * 注册数据变化监听
	 * @param callingPck 注册者包名
	 * @param callback
     */
	public static synchronized void setDataCallback(String callingPck,IDataCallBack callback){
		if(dataCallBackList == null){
			dataCallBackList = new HashMap<String,IDataCallBack>();
		}
		dataCallBackList.put(callingPck,callback);
		ALOG.info(TAG,"setDataCallback end!! size : "+dataCallBackList.size()+", callingPck : "+callingPck);
	}

	public static synchronized void removeDataCallBack(String callingPck){
		ALOG.info(TAG,"unRegistDataCallBack start !! size : "+(dataCallBackList == null?"null" : dataCallBackList.size())+", callingPck : "+callingPck);
		if(dataCallBackList!=null && dataCallBackList.containsKey(callingPck)){
			dataCallBackList.remove(callingPck);
		}
		ALOG.info(TAG,"unRegistDataCallBack end !! size : "+(dataCallBackList == null?"null" : dataCallBackList.size()));
		if(dataCallBackList!=null && dataCallBackList.isEmpty()){
			dataCallBackList = null;
		}
	}
	/**
	 * 通知数据变化。
	 * @param callingPackage 修改者包名。根据此参数，过滤掉对修改者本身的通知。
	 * @param key
	 * @param oldValue
	 * @param newValue
	 */
	private static void notifyDataChanged(String callingPackage,String key,String oldValue,String newValue){
		if(dataCallBackList!=null && !dataCallBackList.isEmpty() && isNeedNotifyDataChanged(key) && !newValue.equals(oldValue)){
			Iterator<String> it = dataCallBackList.keySet().iterator();
			while (it.hasNext()) {
				String packageName = it.next();
				if(!packageName.equals(callingPackage)){
					try {
						dataCallBackList.get(packageName).dataChanged(key, oldValue, newValue);
					} catch (RemoteException e) {
						e.printStackTrace();
					} catch (Exception e){
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 是否需要加密。
	 * @param key
	 * @return
	 */
	private static boolean checkNeedEncryptOrDecrypt(String key) {
		boolean flag = false;
		if (key.equalsIgnoreCase(IPTVData.IPTV_Account)) {
			flag = true;
		} else if (key.equalsIgnoreCase(IPTVData.IPTV_Password)) {
			flag = true;
		} else if (key.equalsIgnoreCase(IPTVData.Config_DHCPPassword)) {
			flag = true;
		} else if (key.equalsIgnoreCase(IPTVData.Config_DHCPUserName)) {
			flag = true;
		} else if (key.equalsIgnoreCase(IPTVData.Config_PPPOEUserName)) {
			flag = true;
		} else if (key.equalsIgnoreCase(IPTVData.Config_PPPOEPassword)) {
			flag = true;
		} else if (key.equalsIgnoreCase(IPTVData.Config_MANAGESERVER_USER)) {
			flag = true;
		} else if (key.equalsIgnoreCase(IPTVData.Config_MANAGESERVER_PASSWD)) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 存放需要通知数据变化的节点。
	 */
	private static String[] notifyData = new String[]{
			IPTVData.IPTV_Account,
			IPTVData.IPTV_Password,
			IPTVData.IPTV_AuthURL,
			IPTVData.IPTV_AuthURLBackup,
			IPTVData.Config_DHCPUserName,
			IPTVData.Config_DHCPPassword,
			IPTVData.Config_PPPOEUserName,
			IPTVData.Config_PPPOEPassword,
			IPTVData.Config_Static_IpAddress,
			IPTVData.Config_Static_Gateway,
			IPTVData.Config_Static_Mask,
			IPTVData.Config_Static_DNS,
			IPTVData.Config_Static_DNSBackup,
			IPTVData.Config_ITMS_Enable,
			IPTVData.Config_ITMS_HeartBeat_Enable,
			IPTVData.Config_ITMS_HeartBeat_Interval,
			IPTVData.Config_ITMS_ServiceUrl,
			IPTVData.Config_MANAGESERVER_USER,
			IPTVData.Config_MANAGESERVER_PASSWD,
			IPTVData.Config_CPE_USERID,
			IPTVData.Config_CPE_PASSWD,
			IPTVData.IPTV_ZEROSETTING_STATUS,
	};

	/**
	 * 判断是否需要通知数据变化。
	 * @param key
	 * @return
	 */
	private static boolean isNeedNotifyDataChanged(String key){
		if(TextUtils.isEmpty(key)){
			return false;
		}
		return Arrays.asList(notifyData).contains(key);
	}

}
