package com.amt.utils;

import android.text.TextUtils;

import com.amt.app.IptvApp;
import com.amt.config.Config;

/**
 * 设备硬件数据读取接口，提供STBID、型号、硬件版本号、软件版本、芯片等各种硬件信息。
 *
 * Created by DonWZ on 2017/1/25.
 */

public class DeviceInfo {
    /**eth0网卡的MAC地址(带冒号)*/
    public static final String MAC = getMac();

    public static final String STBID = getSTBID();

    public static final String OUI = getOUI();

    /**盒子硬件版本*/
    public static final String HardwareVersion = getHardwareVersion();
    /**盒子软件版本*/
    public static final String SoftwareVersion = getSoftwareVersion();
    /**盒子名称*/
    public static final String ModelName = getModelName();
    /**盒子型号*/
    public static final String MODEL =getModel(); //android.os.Build.MODEL;
    /**是否融合终端。以当期盒子型号匹配DroidSans.ttf配置的融合终端型号为准*/
    public static final boolean isFusionTerminal = isFusionTerminal();
    /**芯片型号。*/
    public static final ChipType chipType = getChipType();
    /**终端制造商（意义可读的字符串）,厂商*/
    public static final String MANUFACTURER = android.os.Build.MANUFACTURER;
    /**Android版本。如4.4.2*/
    public static final String OsVersion = android.os.Build.VERSION.RELEASE;
    /**设备厂商**/
    public static final String manufacturer = getManufacturer();

    /**
     * 获取设备厂商
     * @return
     */
    private static String getManufacturer() {
        return SystemPropHelper.getProp(SystemPropHelper.manufacturer, "");
    }

    /**
     * 获取STBID
     * @return
     */
    private static String getSTBID() {
        String mStbID = "";
        String buildID = android.os.Build.ID;
        String serial = SystemPropHelper.getProp(SystemPropHelper.SERIAL, "");
        //有些盒子直接拿ro.serialno拿不到。
        if(TextUtils.isEmpty(serial)){
            serial = android.os.Build.SERIAL;
        }
        if (!TextUtils.isEmpty(serial) && serial.length() == 32) {
            mStbID = serial;
        } else if (!TextUtils.isEmpty(buildID) && buildID.length() == 20) {
            mStbID = buildID + IptvApp.mNetManager.getMacFormat();
        }

        mStbID.toUpperCase();
        ALOG.debug("STBID:"+mStbID);
        return mStbID;
    }
    /**
     * 获取设备OUI
     * @return
     */
    private static String getOUI(){
        // TODO 两种获取方案，1：从系统属性拿   2：从STBID里截取
//        String oui = SystemPropHelper.getProp(SystemPropHelper.OUI, "");
        String oui = STBID.length() == 32 ? STBID.substring(6, 12) : "";
        return oui;
    }

    /**
     * 获取盒子硬件版本
     * @return
     */
    private static String getHardwareVersion(){
//        return SystemPropHelper.getProp(SystemPropHelper.HARDWARE_VERSION, "");
        return android.os.Build.DISPLAY;
    }

    /**
     * 获取系统软件版本
     * @return
     */
    private static String getSoftwareVersion(){
//        return SystemPropHelper.getProp(SystemPropHelper.SOFTWARE_VERSION, "");
        return android.os.Build.VERSION.INCREMENTAL;
    }
    /**
     * 获取机顶盒型号
     * @return
     */
    private static String getModel(){
        return SystemPropHelper.getProp(SystemPropHelper.model,"");
    }
    /**
     * 获取机顶盒名称
     * @return
     */
    private static String getModelName(){
        return SystemPropHelper.getProp(SystemPropHelper.MODEL_NAME,"");
    }

    private static String getMac(){
        if(IptvApp.mNetManager!=null){
            return IptvApp.mNetManager.getMac();
        }
        return "";
    }

    /**
     * 是否融合终端。以当期盒子型号匹配DroidSans.ttf配置的融合终端型号为准
     * @return
     */
    private static boolean isFusionTerminal(){
        boolean isFusion = false;
        if(!TextUtils.isEmpty(Config.FUSIONMODEL)){
            String[] fusionModels = Config.FUSIONMODEL.split(";");
            for(int i=0;i<fusionModels.length;i++){
                if(!TextUtils.isEmpty(fusionModels[i]) && MODEL.equals(fusionModels[i])) {
                    isFusion = true;
                    break;
                }
            }
        }
        return isFusion;
    }

    private static ChipType getChipType() {
        ChipType chipType = ChipType.UNKNOWN;
        try{
            if (android.os.Build.DEVICE.startsWith("Hi3798")) {
                chipType = ChipType.HISI_3798;
            } else if (SystemPropHelper.getProp("ro.board.platform", "").startsWith("rk3228")) {
                chipType = ChipType.RK_3228;
            } else if(SystemPropHelper.getProp("ro.product.description","").startsWith("Amlogic S905")){
                chipType = ChipType.AML_S905;
            } else if(SystemPropHelper.getProp("ro.hardware","").equals("clippers")){
                chipType = ChipType.Mstar_9280;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return chipType;
    }
}
