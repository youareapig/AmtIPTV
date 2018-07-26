package com.amt.amtdata;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import com.amt.utils.ALOG;
import com.amt.utils.FileHelper;

/**
 * 用于通过进程ID（PID）获取进程名相关功能
 * Created by DonWZ on 2017/02/28.
 */
public class ProcessHelper {

	 private static final String TMP_PROC_PATH = "procCache";

	public static String getProcessName(Context context,AmtPermission permission){
		String processName = "getCallingPackageFailed";
		if(permission == null){
			return processName;
		}
		long startTime = System.currentTimeMillis();
		ALOG.info("getProcessName > UID : "+permission.callingUid+", PID : "+permission.callingPid);
		//如果调用者的uid大于1000(非系统权限)，则可以直接通过uid拿包名，如果是1000的，则需要通过PID拿包名。因为1000的UID进程非常多，很多系统级APP都是1000
		if(permission.callingUid > 1000){
			String[] packages = context.getPackageManager().getPackagesForUid(permission.callingUid);
			if(packages!=null && packages.length>0){
				processName = packages[0];
			}
		}else{
			processName = getProcessName(context, permission.callingPid);
		}
		permission.callingPackageName = processName;
		ALOG.info("wz === get proc name : "+processName+", spent "+(System.currentTimeMillis() - startTime)+"ms");
		return processName;
	}

	private static String getProcessName(Context context,int callingPid){
		String processName = "getCallingPackageFailed";
		if(callingPid < 0){
			return processName;
		}
		if(Build.VERSION.SDK_INT < 21){
			try {
				ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
				List<ActivityManager.RunningAppProcessInfo> l = am.getRunningAppProcesses();
				Iterator i = l.iterator();
				while (i.hasNext()) {
					ActivityManager.RunningAppProcessInfo appProcess = (ActivityManager.RunningAppProcessInfo) (i.next());
					try {
						if (appProcess.pid ==callingPid) {
							processName = appProcess.processName;
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			//由于Android 7.0权限控制太严格，系统权限也不能获取进程信息(cat proc/pid/cmdline)，所以通过执行root命令，
			//将cat proc/pid/cmdline 结果重定向到IPTV安装包名/cache目录内，以进程id为文件名，并执行chmod 777 权限命令，然后读取这个文件，在IPTV启动时再删除这个文件。
			//第一次查询某个进程时大概花费50ms，在这次运行期间内再次查询此进程只需1-5ms左右。
			String procDirPath = "/data/data/"+context.getPackageName()+"/"+TMP_PROC_PATH;
			String procFilePath = procDirPath+"/"+callingPid;
			File procDir = new File(procDirPath);
			if(!procDir.exists()){
				procDir.mkdir();
			}
			File procFile = new File(procFilePath);
			if(procFile.exists()){
				processName = readProcFile(procFile);
			}else{
				String cmd = "cat proc/"+callingPid+"/cmdline > "+procFilePath;
				com.amt.utils.Utils.execRootCmd(cmd);
				com.amt.utils.Utils.execRootCmd("chmod 777 "+procFilePath);
				if(procFile.exists()){
					processName = readProcFile(procFile);
				}
			}
		}
		return processName;
	}
	/**
	 * 清除缓存的进程信息文件。
	 * @param context
	 */
	public static void clearProcFiles(Context context){
		File file = new File("/data/data/"+context.getPackageName()+"/"+TMP_PROC_PATH);
		try {
			FileHelper.deleteDir(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 /**
     * 读取进程信息文件
     * @param procFile
     * @return
     */
    private static String readProcFile(File procFile){
    	String processName = "";
    	Reader reader = null;
		StringBuilder sb = new StringBuilder();
		try{
			reader = new InputStreamReader(new FileInputStream(procFile));
			int tmpChar; ;
			while ((tmpChar = reader.read()) > 0){//把0也过滤掉。
				sb.append((char)tmpChar);
			}
			processName = sb.toString();
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return processName;
    }
}
