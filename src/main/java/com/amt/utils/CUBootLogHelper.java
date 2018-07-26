package com.amt.utils;

import android.text.TextUtils;
import android.util.Log;

import com.SyMedia.SyDebug.RemoteDebug;
import com.amt.amtdata.AmtDataManager;
import com.amt.amtdata.IPTVData;
import com.amt.app.IptvApp;
import com.amt.dialog.AmtDialogManager;
import com.amt.player.iptvplayer.IPTVPlayer;
import com.amt.utils.keymap.EPGKey;
import com.amt.utils.keymap.KeyHelper;
import com.amt.utils.mainthread.MainThreadSwitcher;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 联通集团 开机信息收集，一键信息收集，可视化定位操作类。
 * 2017-3-10
 */
public class CUBootLogHelper {

    /**
     * 记录上一次按下#键的时间。
     */
    private static long lastPressTime = 0;
    private static String str = "";
    private static final String TAG = "CUBootLogHelper";
    /**开机信息保存路径*/
    public static String PATH_BOOTINFO = "/begininfo/";
    /**一键信息收集保存路径*/
    public static String PATH_DEVICEINFO = "/deviceInfo/";
    /**每次按键的间隔限制*/
    private static final long INTERVAL = 1000;
    /** 抓包文件最大大小（单位字节） */
    private static final int MAX_CAPINFO_SIZE = 100 * 1024 * 1024;//100MB
    /** 抓包最大时间（单位秒） */
    private static final int MAX_CAPINFO_TIME = 2 * 60;//2分钟
    /**抓LOG 最大时间（单位毫秒）*/
    private static final long MAX_LOGINFO_TIME = 3 * 60 * 1000;
    private static boolean logLock = AmtDataManager.getBoolean(IPTVData.Config_Device_Log_Enable,false);
    //一段时间后，停止抓LOG操作。
    private static ScheduledExecutorService itScheduledExecutorService;
    private static String currentSavePath = "";
    /**
     * # 号键的处理
     */
    public static void checkKeydownPound(int keyCode) {
        String keyName = KeyHelper.getKeyName(keyCode);
        long interval = System.currentTimeMillis() - lastPressTime;
        ALOG.debug("wz === CUBootLogHelper > press key  '" + keyName + "' ,time interval : " + interval);
        lastPressTime = System.currentTimeMillis();
        if (interval < INTERVAL) {
            str += keyName;
            ALOG.debug("wz === ", "record key name : " + str);
            if ("10010".equals(str)) {
                // TODO 一键信息收集
                notifyDeviceLog(!logLock,USBHelper.usbPath+PATH_DEVICEINFO);
                str = "";
            } else if ("**".equals(str)) {
                // TODO 可视化定位
                viewInfo();
                str = "";
            }
        } else {
            str = keyName;
        }
    }

    /**
     * 一键信息收集开关改变通知
     *
     * @param logEnable
     * @param path 保存的路径。可选值：{@link CUBootLogHelper#PATH_BOOTINFO}、{@link CUBootLogHelper#PATH_DEVICEINFO}
     */
    public static void notifyDeviceLog(boolean logEnable,String path) {
        Log.i(ALOG.TAG, TAG + ">" + "notifyDeviceLog >> " + logEnable+", save dir : "+path);
        if(logLock && logEnable){
            stopAndSaveInfo(currentSavePath);
        }
        logLock = logEnable;
        if (logEnable) {
            startSaveLogInfo(path);
            //一段时间后自动停止
            if (itScheduledExecutorService != null) {
                try {
                    itScheduledExecutorService.shutdownNow();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            itScheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            itScheduledExecutorService.schedule(new Runnable() {
                @Override
                public void run() {
                    ALOG.info(TAG, "stop logcat !!!");
                    notifyDeviceLog(false,"");
                }
            }, MAX_LOGINFO_TIME, TimeUnit.MILLISECONDS);
        } else {
            stopAndSaveInfo(currentSavePath);
            currentSavePath = "";
        }
    }

    /**
     * 可视化定位
     */
    private static void viewInfo() {
        //这里是点击了10010组合按键后获取的信息
        try {
            getListData(new GetListCallBack<String>() {
                @Override
                public void onGetData(final List<String> data) {
                    MainThreadSwitcher.runOnMainThreadAsync(new Runnable() {
                        @Override
                        public void run() {
                            AmtDialogManager.ltjc.setList(data);
                            AmtDialogManager.ltjc.showViewInfoDialog();
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止抓包/log,并且保存信息
     */
    public static void stopAndSaveInfo(final String path) {
        if (itScheduledExecutorService != null) {
            try {
                itScheduledExecutorService.shutdownNow();
            } catch (Exception e) {
                e.printStackTrace();
            }
            itScheduledExecutorService = null;
        }
        new Thread() {
            @Override
            public void run() {
                super.run();
                Log.i(ALOG.TAG, TAG + " > stopAndSaveInfo!!!!");
                Utils.stopTcpdump();
                //停止logcat
                RunTimeUtils.exitCurrentCmd();
                String stbInfo = RemoteDebug.getValue("getSTBInfo", "");
                String savePath = path;
                if(TextUtils.isEmpty(path)){
                    savePath = currentSavePath;
                }
                if(TextUtils.isEmpty(savePath)){
                    savePath = USBHelper.usbPath+PATH_DEVICEINFO;
                }
                //copy数据xml到U盘
                FileHelper.writeFile(stbInfo, savePath+"iptvInfo.txt");
                FileHelper.copyFile(AmtDataManager.getConfigFilePath(), savePath+"iptv_prefs.xml");
                //以下是获取一些设备当前运行情况，并保存到U盘中
                RunTimeUtils.execCmdAndWriteFile("busybox top -n 1",savePath+"topInfo.txt");
                RunTimeUtils.execCmdAndWriteFile("busybox ifconfig",savePath+"configInfo.txt");
                RunTimeUtils.execCmdAndWriteFile("busybox arp",savePath+"arpInfo.txt");
                RunTimeUtils.execCmdAndWriteFile("busybox route",savePath+"routeInfo.txt");
                RunTimeUtils.execCmdAndWriteFile("ps",savePath+"psInfo.txt");
                AmtDataManager.putBoolean(IPTVData.Config_Device_Log_Enable, false, "");
            }
        }.start();
    }

    /**
     * 开始抓网络包，系统LOG
     */
    private static void startSaveLogInfo(final String path) {
        final String savePath = TextUtils.isEmpty(path) ? USBHelper.usbPath+PATH_DEVICEINFO : path;
        currentSavePath = savePath;
        new Thread() {
            @Override
            public void run() {
                super.run();
                Log.i(ALOG.TAG, TAG + " > startSaveLogInfo!!!!");
                //开始抓包
                String capinfoSavePath = savePath+"capInfo.pcap";
                Utils.startTcpdump(capinfoSavePath, "", "", MAX_CAPINFO_SIZE, MAX_CAPINFO_TIME);
                //抓LOG
                String logFile = savePath+"logcatInfo.txt";
                File log = new File(logFile);
                if (log.exists()) {
                    log.delete();
                }
                RunTimeUtils.execCmdAndWriteFile("logcat -v time", logFile);
            }
        }.start();
    }

    public interface GetListCallBack<T>{
        void onGetData(List<T> data);
    }
    private static ExecutorService executor = Executors.newCachedThreadPool();

    //add  by  cz 获取可视化定位的信息数据
    public static void getListData(final GetListCallBack<String> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<String> list = new ArrayList<String>();
                list.clear();
                try{
                    String CPURate = IPTVPlayer.getCPURate();
                    String MemRate = IPTVPlayer.getMemRate();
                    String BufferSize = IPTVPlayer.getVideoWidthPixelsInfo();
                    String videoWidthPixels = IPTVPlayer.getVideoAspectRatio();
                    String videoHeightPixels = IPTVPlayer.getVideoEncodingMode();
                    String videoAspectRatio = IPTVPlayer.getFrameFieldMode();
                    String videoEncodingMode = IPTVPlayer.getAudioEncodingMode();
                    String frameFieldMode = IPTVPlayer.getAudioRate();
                    String audioEncodingMode = IPTVPlayer.getAudioCount();
                    String audioRate = IPTVPlayer.getAudioSamplingRate();
                    String audioCount = IPTVPlayer.getAudioSubtitleName();
                    String audioSamplingRate = IPTVPlayer.getLastPeriodPackageLoss();
                    String audioSubtitleName = IPTVPlayer.getPackageOutOfOrderStatistics();
                    String lastPeriodPackageLoss = IPTVPlayer.getNetDelay();
                    String packageOutOfOrderStatistics = IPTVPlayer.getProtocol();
                    String netDelay = IPTVPlayer.getLastPeriodTsCountError();
                    String protocol = IPTVPlayer.getLastPeriodSyncheadlossNum();
                    String lastPeriodTsCountError = IPTVPlayer.getECMErrorNum();
                    String lastPeriodSyncheadlossNum = IPTVPlayer.getAudioPlayDiff();
                    String ECMErrorNum = IPTVPlayer.getVideoBuffer();
                    String audioPlayDiff = IPTVPlayer.getVideoBufferUsedSize();
                    String videoBuffer = IPTVPlayer.getAudioBuffer();
                    String videoBufferUsedSize = IPTVPlayer.getAudioBufferUsedSize();
                    String audioBuffer = IPTVPlayer.getVideoDecodeError();
                    String audioBufferUsedSize = IPTVPlayer.getVideoDecodePackageLoss();
                    String videoDecodeError = IPTVPlayer.getVideoDecodeUnderflow();
                    String videoDecodePackageLoss = IPTVPlayer.getVideoDecodePTSError();
                    String videoDecodeUnderflow = IPTVPlayer.getAudioDecodeError();
                    String videoDecodePTSError = IPTVPlayer.getAudioDecodeThrowError();
                    String audioDecodeError = IPTVPlayer.getAudioDecodeUnderflow();
                    String audioDecodeThrowError = IPTVPlayer.getAudioPTSError();
                    ALOG.info(TAG, "getListData!!!");
                    list.add(CPURate);
                    list.add(MemRate);
                    list.add(BufferSize);
                    list.add(videoWidthPixels);
                    list.add(videoHeightPixels);
                    list.add(videoAspectRatio);
                    list.add(videoEncodingMode);
                    list.add(frameFieldMode);
                    list.add(audioEncodingMode);
                    list.add(audioRate);
                    list.add(audioCount);
                    list.add(audioSamplingRate);
                    list.add(audioSubtitleName);
                    list.add(lastPeriodPackageLoss);
                    list.add(packageOutOfOrderStatistics);
                    list.add(netDelay);
                    list.add(protocol);
                    list.add(lastPeriodTsCountError);
                    list.add(lastPeriodSyncheadlossNum);
                    list.add(ECMErrorNum);
                    list.add(audioPlayDiff);
                    list.add(videoBuffer);
                    list.add(videoBufferUsedSize);
                    list.add(audioBuffer);
                    list.add(audioBufferUsedSize);
                    list.add(videoDecodeError);
                    list.add(videoDecodePackageLoss);
                    list.add(videoDecodeUnderflow);
                    list.add(videoDecodePTSError);
                    list.add(audioDecodeError);
                    list.add(audioDecodeThrowError);
                }catch(Exception e){e.printStackTrace();}
                if(callback!= null){
                    callback.onGetData(list);
                }
            }
        });
    }
}
