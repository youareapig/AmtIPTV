package com.amt.amtdata;

import java.util.HashMap;

/**
 * 预置数据方案。
 * <br />预置方案：预置的数据在代码里写死，不直接存入配置文件里。读取数据时遵循读取优先级：1:读取配置文件 2. 读取预置数据
 * <br />为什么不能写入配置文件？电信或联通局方经常要求IPTV预置诸如日志文件上传地址、ftp地址等数据，但后期会要求更新预置地址，但是要求不能影响网管修改过的值，
 * 只能对网管未修改的，使用预置地址的盒子有效。针对这种需求，此方案下我们只需要修改代码里的预置数据就行了，不需要操作配置文件。
 * <br> <br>根据实际对接项目的经验，需要以此方式预置的参数，一般为QOS和网管部分的数据，如
 *  <ol>
 *      <li>{@link IPTVData#Config_Performance_LogServerUrl}:视频性能日志上传地址</li>
 *      <li>{@link IPTVData#Config_Performance_LogServerUrl_Backup}:备用视频性能日志上传地址</li>
 *      <li>{@link IPTVData#Config_CPE_USERID}:CPE用户名</li>
 *      <li>{@link IPTVData#Config_CPE_PASSWD}:CPE密码</li>
 *      <li>{@link IPTVData#Config_MANAGESERVER_USER}:注册网管用户名</li>
 *      <li>{@link IPTVData#Config_MANAGESERVER_PASSWD}:注册网管密码</li>
 *      <li>{@link IPTVData#Config_ITMS_ServiceUrl}:网管地址</li>
 *      <li>{@link IPTVData#Config_LogServer}:日志服务器</li>
 *  </ol>
 * Created by DonWZ on 2017/4/1.
 */

public class PresetData {

    private static final HashMap<String,String> presetDataList = new HashMap<String, String>();

    /**
     * 初始化预置数据。
     */
    static{
        //TODO 将预置参数存放在map表里。如：
//        presetDataList.put(IPTVData.Config_Performance_LogServerUrl,"tftp://182.145.129.102");
    }

    /**
     * 获取预置数据。若没有预置数据，则返回空字符
     * @param key
     * @return
     */
    public static String getPresetData(String key){
        if(!presetDataList.isEmpty() && presetDataList.containsKey(key)){
            return presetDataList.get(key);
        }
        return "";
    }

    /**
     * 是否为预置项
     * @param key
     * @return
     */
    public static boolean isPreset(String key){
        return presetDataList.containsKey(key);
    }
}
