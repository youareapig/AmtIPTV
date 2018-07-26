package com.amt.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.amt.app.IptvApp;
import com.amt.dialog.AmtDialogManager;
import com.amt.utils.NetUtils.HttpUtils;
import com.amt.utils.NetUtils.download.DownloadResponseCallback;
import com.amt.utils.mainthread.MainThreadSwitcher;
import com.amt.app.IPTVActivity;

import java.io.File;
import java.util.List;

/**
 * APK操作工具类
 * Created by DonWZ on 2017/6/9.
 */

public class APKHelper {

    /**
     * 根据包名判断APK是否已安装
     * @param context
     * @param packageName
     * @return
     */
    private static final String TAG="APKHelper";
    public static boolean isAppInstalled(Context context, String packageName){
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            if(packageInfo!= null){
                installed = true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ALOG.info(TAG,"isAppInstalled > "+packageName+" : "+installed);
        return installed;
    }

    /**
     * 根据包名获取APK的版本号名称（VersionName）
     * @param context
     * @param packageName
     * @return
     */
    public static String getAppVersionName(Context context, String packageName){
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 据包名获取APK的版本号（VersionCode）
     * @param context
     * @param packageName
     * @return
     */
    public static int getAppVersionCode(Context context, String packageName){
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 判断IPTV是否在前台运行
     * @param context
     * @return
     */
    public static boolean isIptvTop(Context context){
        return context == null ? false : isTop(context,context.getPackageName());
    }

    /**
     * 判断指定包名应用是否处于前台运行
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isTop(Context context,String packageName){
        if(context == null || TextUtils.isEmpty(packageName)){
            return false;
        }
        return packageName.equals(getTopPackageName(context));
    }

    /**
     * 得到当前置顶程序的包名
     * @param context
     * @return
     */
    public static String getTopPackageName(Context context) {
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> tasksInfo = am.getRunningTasks(1);
            if (tasksInfo != null && tasksInfo.size() > 0) {
                return tasksInfo.get(0).topActivity.getPackageName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 跳转设置APK
     * @param context
     */
    public static void goSettings(Context context){
        Intent settingIntent = new Intent("com.android.smart.terminal.settings");
    //settingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try{
            context.startActivity(settingIntent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 跳转:LauncherAPK
     * @param context
     */
    private static String launcher_package="com.huawei.tj.launcher";
    private static String launcher_mainName="com.huawei.iptv.launcher.LauncherMain";
    public static void goLauncher(Context context){
        ALOG.info(TAG,"goLauncher");
        if(isAppInstalled(context,launcher_package)){
            Intent goLauncher = new Intent(Intent.ACTION_MAIN);
            //  goLauncher.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName cn = new ComponentName(launcher_package,launcher_mainName);
            goLauncher.setComponent(cn);
            context.startActivity(goLauncher);
        }
    }


    /**
     * 运行JVM 游戏APK
     * @param context
     * @param width
     * @param height
     * @param strJadUrl
     * @param strJarUrl
     * @param strParam
     */
    public static void goJVMGameApk(Context context,int width, int height, String strJadUrl, String strJarUrl, String strParam){
        String jvmPackageName = "com.android.smart.terminal.itvgame";
        String jvmClassName = "com.android.smart.terminal.itvgame.ItvGameActivity";
        if(isAppInstalled(context,jvmPackageName)){
            if(!isTop(context,jvmPackageName)){
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                ComponentName componentName = new ComponentName(jvmPackageName, jvmClassName);
                intent.setComponent(componentName);
                intent.putExtra("width", width);
                intent.putExtra("height", height);
                intent.putExtra("JadUrl", Security.EncryptSo(strJadUrl));
                intent.putExtra("JarUrl", Security.EncryptSo(strJarUrl));
                intent.putExtra("param", Security.EncryptSo(strParam));
                context.startActivity(intent);
            }else{
                ALOG.error("goJVMGameApk failed!!! apk running in top!");
            }
        }else{
            ALOG.error("goJVMGameApk failed!!! apk not installed!");
        }
    }

    /**
     * 下载APK并且自动安装
     * @param url APK的下载链接
     * @param pathName APK本地存放路径（包含文件名）
     * @param isViewProgress 是否显示下载过程
     */
    public static void dowloadApkAndInstall(String url, String pathName, final boolean isViewProgress){
        HttpUtils.download(url, pathName, new DownloadResponseCallback() {
            AmtDialogManager.ProgressNotifier progressNotifier;
            int lastProgress = 0;
            @Override
            public void onStart(long totalBytes) {
                super.onStart(totalBytes);
                ALOG.info(TAG,"download APK > onStart");
                if(isViewProgress){
                    progressNotifier = IPTVActivity.mDialogManager.showProgressDialog("正在下载，请稍后...");
                }
            }

            @Override
            public void onFinish(final File file) {
                super.onFinish(file);
                ALOG.info(TAG,"download APK > onFinish");
                if(isViewProgress){
                    progressNotifier = null;
                    IPTVActivity.mDialogManager.showIPTVErrorDialog("下载完成！正在安装...",-1,-1);
                }
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        final boolean installed= installApk(file);
                        if(isViewProgress) {
                            MainThreadSwitcher.runOnMainThreadAsync(new Runnable() {
                                @Override
                                public void run() {
                                    String title = installed ? "安装成功！" : "安装失败！";
                                    IPTVActivity.mDialogManager.showAutoDisDialog(title, -1, -1);
                                }
                            });
                        }
                    }
                }.start();
            }

            @Override
            public void onFail(String error) {
                super.onFail(error);
                ALOG.info(TAG,"download APK > onFail > "+error);
                if(isViewProgress && progressNotifier != null){
                    progressNotifier = null;
                    IPTVActivity.mDialogManager.showIPTVErrorDialog("下载失败！",-1,-1);
                }
            }

            @Override
            public void onProgress(long currentBytes, long totalBytes) {
                super.onProgress(currentBytes, totalBytes);
//                ALOG.info("onProgress >> currentBytes :"+currentBytes+", totalBytes : "+totalBytes);
                int progress = (int)(((float)currentBytes / (float)totalBytes )* 100 );
                if(progress > lastProgress){
                    lastProgress = progress;
                    ALOG.info("download APK > onProgress : "+progress);
                    if(isViewProgress && progressNotifier != null){
                        progressNotifier.onProgressChange(progress);
                    }
                }
            }
            @Override
            public void onCancel() {
                super.onCancel();
                ALOG.info(TAG,"download APK > onCancel ");
                if(isViewProgress && progressNotifier != null){
                    progressNotifier = null;
                    IPTVActivity.mDialogManager.showIPTVErrorDialog("下载被取消！",-1,-1);
                }
            }
        });
    }

    /**
     * 安装APK。耗时操作。需在子线程调用
     * @param apkFile
     * @return
     */
    public static boolean installApk(File apkFile){
        boolean isSuccessed = false;
        if(apkFile == null ) return isSuccessed;
        if(!apkFile.exists()) return isSuccessed;
        try {
            //校验APK完整性
            PackageManager pm = IptvApp.app.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(apkFile.getPath(), PackageManager.GET_ACTIVITIES);
            if (info != null) {
                Utils.execRootCmd("chmod 777 "+apkFile.getAbsolutePath());
                Utils.execRootCmd("pm install -r "+apkFile.getAbsolutePath());
                isSuccessed = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccessed;
    }

}
