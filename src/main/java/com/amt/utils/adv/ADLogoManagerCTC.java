package com.amt.utils.adv;

import android.content.Intent;
import android.text.TextUtils;

import com.amt.amtdata.AmtDataManager;
import com.amt.amtdata.IPTVData;
import com.amt.app.IptvApp;
import com.amt.utils.ALOG;
import com.amt.utils.NetUtils.HttpUtils;
import com.amt.utils.NetUtils.download.DownloadResponseCallback;
import com.hisilicon.android.hisysmanager.HiSysManager;

import java.io.File;


/**
 * 负责开机广告图片更新、下载等操作（电信/联通规范）
 * Created by DonWZ on 2017/7/24.
 */

public class ADLogoManagerCTC {
    public static final String IPTV_ADLOGO_BootPic = "BootPicURL";
    /**开机广告图片文件名，启动图片*/
    public static final String IPTV_ADLOGO_StartPic = "StartPicURL";
    /**开机广告图片文件名，认证图片*/
    public static final String IPTV_ADLOGO_AuthenticatePic = "AuthenticatePicURL"; // AuthenticatePicURL
    /**广告图片/视频的保存路径。要保证系统也能获取到*/
    public static String ADLOGO_PATH = "/data/local/";

    private static ADLogoManagerCTC instance = new ADLogoManagerCTC();
    private static final String TAG="ADLogoManagerCTC";
    //2018.02.27 add by xw 网管下发图片显示时长
    private String bootPic_Time;
    private String startPicURL_Time;
    private ADLogoManagerCTC(){}

    public static ADLogoManagerCTC init(){
        return instance;
    }
    //2018.02.27 add by xw 网管下发第一张图片显示时长
    public void setBootPic_Time(String bootPic_Time) {
        this.bootPic_Time = bootPic_Time;
    }
    //2018.02.27 add by xw 网管下发第二张图片显示时长
    public void setStartPicURL_Time(String startPicURL_Time) {
        this.startPicURL_Time = startPicURL_Time;
    }
    /**
     * 通知更新开机广告图片
     * @param cmd
     * @param value
     * @param from 修改广告图片的进程包名
     */
    public void notifyUpdateADLogo(String cmd,String value,String from){
        if(IPTVData.Config_BootPicURL.equals(cmd)
                ||IPTVData.Config_StartPicURL.equals(cmd)
                ||IPTVData.Config_AuthenticatePicURL.equals(cmd)){
            String fileNamePath = cmd.substring(cmd.lastIndexOf("/")+1)+"_"+ System.currentTimeMillis();
            fileNamePath = ADLOGO_PATH+fileNamePath;
            ALOG.info(TAG,"Dowload LOGO Pic > PicURL : "+value+", filename : "+fileNamePath);
            HttpUtils.download(value,fileNamePath,new DownloadResponseCallback(){
                @Override
                public void onFinish(File file) {
                    super.onFinish(file);
                    if(file != null){
                        String fileName = file.getName();
                        ALOG.debug(TAG,"广告图片下载完成. file path : "+file.getAbsolutePath());
                        //如果老图片存在，删除，然后将新文件改名
                        String oldFileName = "";
                        if(fileName.startsWith(IPTV_ADLOGO_BootPic)){
                            oldFileName = ADLOGO_PATH+IPTV_ADLOGO_BootPic;
                            //上报结果
                            IptvApp.mTR069.setDataToTr069(IPTVData.Config_BootPicURL_Result,"1");
                        }else if(fileName.startsWith(IPTV_ADLOGO_StartPic)){
                            oldFileName = ADLOGO_PATH+IPTV_ADLOGO_StartPic;
                            //上报结果
                            IptvApp.mTR069.setDataToTr069(IPTVData.Config_StartPicURL_Result,"1");
                        }else if(fileName.startsWith(IPTV_ADLOGO_AuthenticatePic)) {
                            oldFileName = ADLOGO_PATH + IPTV_ADLOGO_AuthenticatePic;
                            //上报结果
                            IptvApp.mTR069.setDataToTr069(IPTVData.Config_AuthenticatePicURL_Result,"1");
                        }
                        if(!TextUtils.isEmpty(oldFileName)){
                            File oldPic = new File(oldFileName);
                            if(oldPic.exists()){
                                ALOG.debug(TAG,"old file exists>delete!");
                                oldPic.delete();
                            }
                            ALOG.debug(TAG,"rename ! ");
                            file.renameTo(oldPic);
//                            if(oldPic.getName().contains(IPTV_ADLOGO_BootPic)){
//                                //将第一张广告图片更新到系统里去
//                                ALOG.info(TAG,"update Bootpic! ");
//                                HiSysManager sysmanager = new HiSysManager();
//                                sysmanager.updateLogo(oldFileName);
//                            }
                            //2018.02.27 add by xw 图片下载成功后赋予权限并发送广播给系统
                            com.amt.utils.Utils.execRootCmd("chmod 777 " + oldFileName);
                            Intent intent=new Intent();
                            if(oldPic.getName().contains(IPTV_ADLOGO_AuthenticatePic)){
                                ALOG.debug(TAG,"--------->IPTV_ADLOGO_AuthenticatePic ! ");
                                return;
                            }else if(oldPic.getName().contains(IPTV_ADLOGO_BootPic)){
                                ALOG.debug(TAG,"----action----->android.intent.action.BOOT_PIC_UPDATE");
                                intent.setAction("android.intent.action.BOOT_PIC_UPDATE");
                                intent.putExtra("showtime",bootPic_Time);
                            }else if(oldPic.getName().contains(IPTV_ADLOGO_StartPic)){
                                ALOG.debug(TAG,"----action----->android.intent.action.START_PIC_UPDATE");
                                intent.setAction("android.intent.action.START_PIC_UPDATE");
                                intent.putExtra("showtime",startPicURL_Time);
                            }
                            intent.putExtra("path",oldFileName);
                            ALOG.debug(TAG,"----path----->"+oldFileName);
                            IptvApp.app.sendBroadcast(intent);
                        }
                    }
                }
            });
        }else{
            AmtDataManager.putString(cmd,value,from);
        }
    }
    /**
     * 认证阶段（第三阶段）广告图片是否显示。默认为显示
     * @return
     */
    public boolean authenticatePicEnable(){
        return AmtDataManager.getString(IPTVData.Config_AuthenticatePicURL_Enable,"1").equals("1");
    }
}
