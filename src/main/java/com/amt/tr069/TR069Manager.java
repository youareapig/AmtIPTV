package com.amt.tr069;

import android.content.Context;
import android.content.Intent;

import com.amt.amtdata.AmtDataManager;
import com.amt.amtdata.IPTVData;
import com.amt.app.IPTVActivity;
import com.amt.config.Config;
import com.amt.player.iptvplayer.IPTVPlayer;
import com.amt.utils.ALOG;
import com.amt.utils.NetConnnectEditor;
import com.amt.utils.adv.ADLogoManagerCTC;
import com.amt.utils.powermanager.AmtPowerManager;

/**
 * 处理网管业务，包含零配置过程处理、网管通知事件、QOS数据上报等
 * Created by DonWZ on 2017/4/21.
 */

public class TR069Manager {

    /**网管功能性数据*/
    public static final String TR069_MESSAGE = "Service/Message";
    /**网管下发重启命令*/
    public static final String TR069_VALUE_REBOOT = "Reboot";
    /**网管下发恢复出厂*/
    public static final String TR069_VALUE_FACRESET = "FactoryReset";

    private static final String TAG ="TR069Manager";
    private static TR069Manager instance;
    private Context mContext;
    private ITr069Service mTR069Service;

    private TR069Manager(Context context){
        this.mContext = context;
        mTR069Service = new ITr069Service(context);
    }

    /**
     * 初始化网管业务对象。
     * @param context 不能为空！
     * @return
     */
    public static synchronized TR069Manager init(Context context){
        if(context!=null){
            if(instance == null){
                instance = new TR069Manager(context);
            }
            return instance;
        }else{
            return null;
        }
    }

    public String handleTr069GetData(String key){
        String value = "";
        if(isMediaControlData(key)){
            value = IPTVPlayer.getDataFromMediaControl(key);
        }else if(IPTVData.Config_IptvConfigPath.equals(key)){
            value = AmtDataManager.getConfigFilePath();
        }else{
            value = AmtDataManager.getString(key,"");
        }
        ALOG.debug(TAG,"handleTr069GetData > "+key+" : "+value);
        return value;
    }

    /**
     * 处理网管下发数据。包含零配置数据、恢复出厂等操作性质数据等
     * @param key
     * @param value
     * @return
     */
    public void handleTr069PutData(String key,String value){
        ALOG.debug(TAG,"handleTr069PutData > "+key+" : "+value);
        if(isFunctionData(key)){
            handleFunctinData(key, value);
        }else {
            AmtDataManager.putString(key,value, Config.PACKAGENAME_TR069);
            //2018.02.27 add by xw 网管下发图片显示时长
            if (IPTVData.Config_BootPicURL_Time.equals(key)){
                ADLogoManagerCTC.init().setBootPic_Time(value);
            }else if (IPTVData.Config_StartPicURL_Time.equals(key)){
                ADLogoManagerCTC.init().setStartPicURL_Time(value);
            }else if(isMediaControlData(key)){
                IPTVPlayer.setDataToMediacontrol(key, value);
            }//网管下发网络账号，网络连接方式等操作
            else if (IPTVData.Config_DHCPUserName.equals(key)) {
                NetConnnectEditor.init(mContext).setDHCPUserName(value);
            } else if (IPTVData.Config_DHCPPassword.equals(key)) {
                NetConnnectEditor.init(mContext).setDHCPPassword(value);
            } else if (IPTVData.Config_PPPOEUserName.equals(key)) {
                NetConnnectEditor.init(mContext).setPPPOEUserName(value);
            } else if (IPTVData.Config_PPPOEPassword.equals(key)) {
                NetConnnectEditor.init(mContext).setPPPOEPassword(value);
            } else if(IPTVData.Config_Static_IpAddress.equalsIgnoreCase(key)){
                NetConnnectEditor.init(mContext).setmIpAddress(value);
            } else if(IPTVData.Config_Static_Gateway.equalsIgnoreCase(key)){
                NetConnnectEditor.init(mContext).setmGateWay(value);
            } else if(IPTVData.Config_Static_Mask.equalsIgnoreCase(key)){
                NetConnnectEditor.init(mContext).setmMask(value);
            } else if(IPTVData.Config_Static_DNS.equalsIgnoreCase(key)){
                NetConnnectEditor.init(mContext).setmDNS(value);
            } else if (IPTVData.Config_DHCPEnable.equals(key)) {
                NetConnnectEditor.init(mContext).setDHCPEnable(value);
            } else if(IPTVData.Config_LANDevice_Enable.equals(key)){
                Intent intent = new Intent();
                intent.setAction("com.android.smart.terminal.iptv.IPTV_TO_SETTING_WIFI_AP");
                intent.putExtra("ENABLE", value);
                mContext.sendBroadcast(intent);
            }else if(IPTVData.Config_LANDevice_SSID.equals(key)){
                Intent intent = new Intent();
                intent.setAction("com.android.smart.terminal.iptv.IPTV_TO_SETTING_WIFI_AP");
                intent.putExtra("SSID", value);
                mContext.sendBroadcast(intent);
            }else if(IPTVData.Config_LANDevice_Keypass.equals(key)){
                Intent intent = new Intent();
                intent.setAction("com.android.smart.terminal.iptv.IPTV_TO_SETTING_WIFI_AP");
                intent.putExtra("KEYPASS", value);
                mContext.sendBroadcast(intent);
                if(value.length()<8){
                    ALOG.info("keypass less than 8 digital No,Do not set!");
                }
            }else if(IPTVData.Config_LANDevice_AuthMode.equals(key)
                    ||IPTVData.Config_LANDevice_BeaconType.equals(key)){
                Intent intent = new Intent();
                intent.setAction("com.android.smart.terminal.iptv.IPTV_TO_SETTING_WIFI_AP");
                intent.putExtra("AUTHMODE", value);
                mContext.sendBroadcast(intent);
                if(value.equalsIgnoreCase("0")){
                    ALOG.info("clear password when mode is open");
                    AmtDataManager.putString(IPTVData.Config_LANDevice_Keypass, "",Config.PACKAGENAME_TR069);
                }
            }
        }
    }

    private void handleFunctinData(String key, String value){
        if(TR069_MESSAGE.equals(key)){
            if(TR069_VALUE_REBOOT.equals(value)){
                AmtPowerManager.reboot();
            }else if(TR069_VALUE_FACRESET.equals(value)){
                AmtPowerManager.restoreFatory();
            }
        }else if(key.startsWith("Service/ITMS_LOGO")){
            ADLogoManagerCTC.init().notifyUpdateADLogo(key,value,Config.PACKAGENAME_TR069);
        }
        else if("Service/ServiceInfo/ConnectModeString".equals(key)){
            //TODO 网管下发修改网络连接方式。
            NetConnnectEditor.init(IPTVActivity.context).setConnectType(value);
        }
    }

    /**
     * 上报视频性能告警等信息给网管。传递"EventCode","0 BOOTSTRAP"，可以让网管再发起一次0 Boot
     * @param key
     * @param value
     */
    public void setDataToTr069(String key,String value){
        ALOG.info(TAG, "setDataToTr069 > ["+key+":"+value+"]");
        //调用网管的aidl接口。
        mTR069Service.setValueToTr069(key, value);
    }

    /**
     * 是否是MediaControl数据，比如QOS数据，视频性能数据等
     * @param key
     * @return
     */
    private boolean isMediaControlData(String key){
        return key!=null && key.startsWith("Device.");
    }

    /**
     * 判断网管下发的数据是否是功能性数据，比如重启、恢复出厂等
     * @param key
     * @return
     */
    private boolean isFunctionData(String key){
        return TR069_MESSAGE.equals(key)
                || "Service/ServiceInfo/ConnectModeString".equals(key)
                || key.startsWith("Service/ITMS_LOGO");
    }

}
