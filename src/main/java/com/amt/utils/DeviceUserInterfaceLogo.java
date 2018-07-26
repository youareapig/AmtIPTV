package com.amt.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import com.amt.amtdata.AmtDataManager;

import android.content.Context;

/**
 * 电信 开机图片、程序载入图片、认证图片下载和操作接口
 * 
 * @author xp wz lyn 同步修改优化
 *
 */
public class DeviceUserInterfaceLogo {
	private static final String TAG = "DeviceUserInterfaceLogo";
	
	private static final String LOGO = "Device.UserInterface.Logo.";
	
	private static final String KEY_BootPicURL = LOGO +"X_CT-COM_BootPicURL";
	private static final String KEY_BootPic_Enable = LOGO +"X_CT-COM_BootPic_Enable";
	private static final String KEY_BootPic_Result = LOGO+"X_CT-COM_BootPic_Result";
	private static final String KEY_BootPic_Time = LOGO+"X_CT-COM_BootPic_Time";
	
	private static final String KEY_StartPicURL = LOGO+"X_CT-COM_StartPicURL";
	private static final String KEY_StartPic_Enable = LOGO +"X_CT-COM_StartPic_Enable";
	private static final String KEY_StartPic_Result = LOGO+"X_CT-COM_StartPic_Result";
	private static final String KEY_StartPic_Time = LOGO+"X_CT-COM_StartPic_Time";
	
	private static final String KEY_AuthenticatePicURL = LOGO+"X_CT-COM_AuthenticatePicURL";
	private static final String KEY_AuthenticatePic_Enable = LOGO +"X_CT-COM_AuthenticatePic_Enable";
	private static final String KEY_AuthenticatePic_Result = LOGO+"X_CT-COM_AuthenticatePic_Result";
	private static final String KEY_AuthenticatePic_Time = LOGO+"X_CT-COM_AuthenticatePic_Time";
	
	private static final int DEFAULT_TIME = 5;
	
	/**
	 * 判断参数是否未网管可以处理的参数
	 * @param param
	 * @return
	 */
	public static boolean isDeviceUserInterfaceLogoParam(String param){
		if(param == null || param == ""){
			return false;
		}
		return param.startsWith(LOGO);
	}

	public static void doAction(String param,String value,Context context) {
		if(param == null || param == ""||value==null||value==""){
			return;
		}

			String key = param;
			String val = value;
			AmtDataManager.putString(key, val,"IPTV");
			if(key.equals(KEY_StartPicURL)){
				doStartDownStartPicURL(context,key,val);
			}else if(key.equals(KEY_AuthenticatePicURL)){
				doStartDownAuthenticatePicURL(context,key,val);
			}else if(key.equals(KEY_BootPicURL)){//(暂无使用，系统自实现)
				//doStartDownBootPicURL(context,key,val);
			}
		//}
	}
	
	static Object authPicLocked = new Object();
	static Object startPicLocked = new Object();

	/**
	 * 下载52%显示的图片(开始显示图片)
	 * @param context
	 * @param key
	 * @param val
	 */
	private static void doStartDownStartPicURL(final Context context, final String key,final String val) {
		ALOG.debug(TAG,"doStartDownStartPicURL key:"+key+",val:"+val );
		final String fileName = "DownStartPicURL_"+System.currentTimeMillis();
		final String dir = makeDir(getDir());
		final String pathFile = dir+"/"+fileName;
		ALOG.debug(TAG,"fileName : " +fileName);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				URL url;
				try {
					url = new URL(val);
					FileOutputStream writer=null;
					try {
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						conn.setConnectTimeout(5000);
						int responseCode = conn.getResponseCode();
						//System.out.println("responseCode:"+responseCode);
						if(responseCode == 200){
							InputStream input = conn.getInputStream();							
							byte[] bytes = new byte[1024];
							int result = input.read(bytes);
							writer = new FileOutputStream(pathFile);
							while(result != -1){
								writer.write(bytes,0,result);
								result = input.read(bytes);
							}
							writer.close();
							//2016/01/03 add by wz 下载完成，给个权限。S905不给权限会读取失败
							try {
								String pic = getDir() + "/" + result;
								ALOG.debug("set chmod 777 before  filename:" + pic);
								Process p = Runtime.getRuntime().exec("chmod 777 " + pic);
								int status = p.waitFor();
								ALOG.debug(" === set chmod 777 after, status :" + status);
							} catch (Exception e) {
								e.printStackTrace();
							}
							//如果认证图片现在完成，就替换之前的图片
							//replaceOldAuthenticatePicURLFile(pathFile);
							synchronized (startPicLocked) {
								String old_fileName = AmtDataManager.getString("Final_DownLoad_StartPicURL_FileName", "");
								AmtDataManager.putString("Final_DownLoad_StartPicURL_FileName", fileName,"IPTV");
								ALOG.debug(TAG," old_fileName"+(getDir()+ "/"+ old_fileName));
								if(old_fileName != null && old_fileName != ""){
									File oldFile = new File(getDir()+ "/"+ old_fileName);
									if(oldFile.exists()){
										ALOG.debug(TAG," oldFile.delete done");
										oldFile.delete();
									}
								}
							}
							ALOG.debug(TAG," replaceOldStartPicURL done");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}finally{
						if(writer != null){
							try {
								writer.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 下载开机图片
	 * @param context
	 * @param key
	 * @param val
	 */
	private static void doStartDownBootPicURL(final Context context,final String key, final String val) {
		ALOG.debug(TAG," doStartDownBootPicURL key:"+key+",val:"+val );
		final String fileName = "BootPicURLPicURL_"+System.currentTimeMillis();
		final String dir = makeDir(getDir());
		final String pathFile = dir+"/"+fileName;
		ALOG.debug(TAG," fileName : " +fileName);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				URL url;
				try {
					url = new URL(val);
					FileOutputStream writer=null;
					try {
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						conn.setConnectTimeout(5000);
						int responseCode = conn.getResponseCode();
						//System.out.println("responseCode:"+responseCode);
						if(responseCode == 200){
							InputStream input = conn.getInputStream();							
							byte[] bytes = new byte[1024];
							int result = input.read(bytes);
							writer = new FileOutputStream(pathFile);
							while(result != -1){
								writer.write(bytes,0,result);
								result = input.read(bytes);
							}
							writer.close();
							//如果认证图片现在完成，就替换之前的图片
							//replaceOldAuthenticatePicURLFile(pathFile);
							synchronized (authPicLocked) {
								String old_fileName = AmtDataManager.getString("Final_DownLoad_BootPicURL_FileName", "");
								AmtDataManager.putString("Final_DownLoad_BootPicURL_FileName", fileName,"IPTV");
								ALOG.debug(TAG," old_fileName"+(getDir()+ "/"+ old_fileName));
								if(old_fileName != null && old_fileName != ""){
									File oldFile = new File(getDir()+ "/"+ old_fileName);
									if(oldFile.exists()){
										ALOG.debug(TAG,"xp oldFile.delete done");
										oldFile.delete();
									}
								}
							}
							ALOG.debug(TAG," replaceOldBootPicURLPicURL done");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}finally{
						if(writer != null){
							try {
								writer.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 开始下载认证图片(83%)
	 * @param key
	 * @param val
	 */
	private static void doStartDownAuthenticatePicURL(final Context context,final String key, final String val) {
		ALOG.debug(TAG,"xp doStartDownAuthenticatePicURL key:"+key+",val:"+val );
		final String fileName = "AuthenticatePicURL_"+System.currentTimeMillis();
		final String dir = makeDir(getDir());
		final String pathFile = dir+"/"+fileName;
		ALOG.debug(TAG," fileName : " +fileName);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				URL url;
				try {
					url = new URL(val);
					FileOutputStream writer=null;
					try {
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						conn.setConnectTimeout(5000);
						int responseCode = conn.getResponseCode();
						//System.out.println("responseCode:"+responseCode);
						if(responseCode == 200){
							InputStream input = conn.getInputStream();							
							byte[] bytes = new byte[1024];
							int result = input.read(bytes);
							writer = new FileOutputStream(pathFile);
							while(result != -1){
								writer.write(bytes,0,result);
								result = input.read(bytes);
							}
							writer.close();
							//如果认证图片现在完成，就替换之前的图片
							//replaceOldAuthenticatePicURLFile(pathFile);
							//2016/01/03 add by wz 下载完成，给个权限。S905不给权限会读取失败
							try {
								String pic = getDir() + "/" + result;
								ALOG.debug("set chmod 777 before  filename:" + pic);
								Process p = Runtime.getRuntime().exec("chmod 777 " + pic);
								int status = p.waitFor();
								ALOG.debug(" === set chmod 777 after, status :" + status);
							} catch (Exception e) {
								e.printStackTrace();
							}
							synchronized (authPicLocked) {
								String old_fileName = AmtDataManager.getString("Final_DownLoad_AuthenticatePicURL_FileName", "");
								AmtDataManager.putString("Final_DownLoad_AuthenticatePicURL_FileName", fileName,"IPTV");
								ALOG.debug(TAG," old_fileName"+(getDir()+ "/"+ old_fileName));
								if(old_fileName != null && old_fileName != ""){
									File oldFile = new File(getDir()+ "/"+ old_fileName);
									if(oldFile.exists()){
										ALOG.debug(TAG," oldFile.delete done");
										oldFile.delete();
									}
								}
							}
							ALOG.debug(TAG," replaceOldAuthenticatePicURLFile done");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}finally{
						if(writer != null){
							try {
								writer.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
	}

	private static String makeDir(String dir) {
		File savaPath = new File(dir);
		if(!savaPath.exists()){
			savaPath.mkdirs();
		}
		//2016/01/03 add by wz 加上权限
		try {
			Process p = Runtime.getRuntime().exec("chmod 777 " + savaPath.getAbsolutePath());
			int status = p.waitFor();
			ALOG.debug(" === set chmod 777 : "+savaPath.getAbsolutePath()+", status :"+status);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return savaPath.getAbsolutePath();
	}

	
	/**
	 * 替换认证图片时确保图片的完整性，只允许一个线程进行替换。
	 * @param newPath
	 */
	private static void replaceOldAuthenticatePicURLFile(String newPath) {
		synchronized (authPicLocked) {
			File newFile = new File(newPath);
			File oldFile = new File(getDir()+"/AuthenticatePicURL");
			File destFile = new File(getDir()+"/AuthenticatePicURL.bak");
			if(oldFile.exists()){//1，备份
				oldFile.renameTo(destFile);
			}
			if(newFile.exists()){//2，替换
				newFile.renameTo(oldFile);
			}
			if(destFile.exists()){//3，删除或恢复
				if(oldFile.exists()){//如果替换成功，则删除备份的
					destFile.deleteOnExit();
				}else{//如果替换未成功，则恢复之前备份的数据
					destFile.renameTo(oldFile);
				}
			}
		}
	}
	
	/**
	 * 获取网管下载图片的备份目录
	 * @return
	 */
	private static String getDir() {
		return "/data/data/com.android.smart.terminal.iptv/bak";
	}

	/**
	 * 获取认证图片的路径(83%)
	 * @return
	 */
	public static String getAuthenticatePicURL(Context context) {
		String result = AmtDataManager.getString("Final_DownLoad_AuthenticatePicURL_FileName", "");
		ALOG.debug(TAG," result = " + result);
		if(result != null && result != ""){
			return getDir() + "/" + result;
		}
		return getDir() + "/AuthenticatePicURL";
	}

	/**
	 * 判断认证图片是否启用83%)
	 * @param context
	 * @return
	 */
	public static boolean isEnableAuthenticate(Context context) {
		File file = new File(getAuthenticatePicURL(context));
		if(!file.exists()){
			return false;
		}
		String result = AmtDataManager.getString( KEY_AuthenticatePic_Enable, "true");
		ALOG.debug(TAG," KEY_AuthenticatePic_Enable " +result);
		if("1".equals(result))
			return true;
		else if("0".equals(result))
			return false;
		return Boolean.parseBoolean(result);
	}
	
	/**
	 * 获取(83%)的显示时间
	 * @param context
	 * @return
	 */
	public static long getAuthenticatePicTime(Context context){
		String result = AmtDataManager.getString(KEY_AuthenticatePic_Time, String.valueOf(DEFAULT_TIME));
		ALOG.debug(TAG," KEY_AuthenticatePic_Time " +result);
		try{
			return Math.abs(Long.parseLong(result))*1000;
		}catch(NumberFormatException e){
			ALOG.debug(TAG,"getAuthenticatePicTime error " +e.getMessage());
			return DEFAULT_TIME*1000;
		}
	}
	
	/**
	 * 获取程序开始载入图片的路径(52%)
	 * @return
	 */
	public static String getStartPicURL(Context context) {
		String result = AmtDataManager.getString( "Final_DownLoad_StartPicURL_FileName", "");
		ALOG.debug(TAG," result = " + result);
		//2016/01/03 add by wz 加上权限
		try {
			String pic = getDir() + "/" + result;
			ALOG.debug("set chmod 777 before  filename:" + pic);
			Process p = Runtime.getRuntime().exec("chmod 777 " + pic);
			int status = p.waitFor();
			ALOG.debug("wz === set chmod 777 after, status :" + status);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(result != null && result != ""){
			return getDir() + "/" + result;
		}
		return getDir() + "/StartPicURL";
	}

	/**
	 * 判断开始程序开始载入是否启用
	 * @param context
	 * @return
	 */
	public static boolean isEnableStartPic(Context context) {
		File file = new File(getStartPicURL(context));
		if(!file.exists()){
			return false;
		}
		String result = AmtDataManager.getString(KEY_StartPic_Enable, "true");
		ALOG.debug(TAG," KEY_StartPic_Enable " +result);
		if("1".equals(result))
			return true;
		else if("0".equals(result))
			return false;
		return Boolean.parseBoolean(result);
	}
	
	/**
	 * 获取程序开始载入图片的显示时间
	 * @param context
	 * @return
	 */
	public static long getStartPicTime(Context context){
		String result = AmtDataManager.getString( KEY_StartPic_Time, String.valueOf(DEFAULT_TIME));
		ALOG.debug(TAG," KEY_StartPic_Time " +result);
		try{
			return Math.abs(Long.parseLong(result))*1000;
		}catch(NumberFormatException e){
			ALOG.debug(TAG,"getStartPicTime error " +e.getMessage());
			return DEFAULT_TIME*1000;
		}
	}
	
	
	/**
	 * 获取开机图片的路径(暂无使用，系统自实现)
	 * @return
	 */
	public static String getBootPicURL(Context context) {
		String result = AmtDataManager.getString("Final_DownLoad_BootPicURL_FileName", "");
		ALOG.debug(TAG," result = " + result);
		if(result != null && result != ""){
			return getDir() + "/" + result;
		}
		return getDir() + "/BootPicURL";
	}

	/**
	 * 判断开机图片是否启用(暂无使用，系统自实现)
	 * @param context
	 * @return
	 */
	public static boolean isEnableBootPic(Context context) {
		String result = AmtDataManager.getString(KEY_BootPic_Enable, "false");
		ALOG.debug(TAG," KEY_BootPic_Enable " +result);
		if("1".equals(result))
			return true;
		else if("0".equals(result))
			return false;
		return Boolean.parseBoolean(result);
	}

	private static long startPicTime = 0L;
	/**
	 * 标记StartPic开始显示时间 ，该方法配合 getDelayedShowStartPicTime使用
	 */
	private static AtomicInteger itStarticTime = new AtomicInteger(DEFAULT_TIME);
	private static ScheduledExecutorService itScheduledExecutorService;
	
	/**
	 * 标记StartPic开始显示时间 ，该方法配合 getDelayedShowStartPicTime使用
	 */
	public static void signStartStartPicShowTime() {
		//startPicTime = System.currentTimeMillis();
		ALOG.debug(TAG,"signStartStartPicShowTime");
		if(itScheduledExecutorService != null){
			itScheduledExecutorService.shutdownNow();
		}
		itStarticTime.set(DEFAULT_TIME);
		itScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		itScheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
			
			@Override
			public void run() {
				ALOG.debug(TAG,"time : " + itStarticTime.get());
				if(itStarticTime.decrementAndGet()<=0){
					itScheduledExecutorService.shutdownNow();
				}
			}
		}, 0, 1000L, TimeUnit.MILLISECONDS);
	}
	
	/**
	 *获取StartPic延迟显示时间 ，该方法配合signStartStartPicShowTime使用
	 * @param context
	 * @return
	 */
	public static long getDelayedShowStartPicTime(Context context){
		long disTime = itStarticTime.get();//System.currentTimeMillis()-startPicTime;
		ALOG.debug(TAG,"disTime : " + disTime);
		if(itScheduledExecutorService != null){
			itScheduledExecutorService.shutdownNow();
		}
		if(disTime >=0){
			return disTime*1000;
		}
		return -1;
	}
}
