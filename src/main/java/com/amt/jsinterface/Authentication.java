package com.amt.jsinterface;

import android.content.Intent;
import android.text.TextUtils;

import com.SyMedia.webkit.SyJavascriptInterface;
import com.amt.amtdata.AmtDataManager;
import com.amt.amtdata.IPTVData;
import com.amt.app.IptvApp;
import com.amt.auth.AuthData;
import com.amt.player.entity.LiveChannel;
import com.amt.player.entity.LiveChannelHelper;
import com.amt.player.iptvplayer.IPTVPlayer;
import com.amt.utils.ALOG;
import com.amt.utils.DeviceInfo;
import com.amt.utils.Security;
import com.amt.utils.Utils;
import com.amt.utils.keymap.GlobalKeyHelper;
import com.amt.webview.WebViewManager;
import com.android.org.sychromium.content.browser.JavascriptInterface;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 *  EPG js扩展对象，详情请参考中国电信集团IPTV3.0规范《扩展JavaScript对象.pdf》附录
 * Created by zw on 2017/5/19.
 */

public class Authentication {
    public static final String TAG = "Authentication";
    private Map<String, String> configMap = new HashMap<String, String>();
    /**记录EPG是否完整地下频道数据，作为认证是否成功的标识之一*/
    private boolean isChannelDataCompleted = false;
    /**记录EPG下发的频道列表总数*/
    private int mChannelCount = 0;
    /**记录已接收的频道列表数量*/
    private int mLocalChannel = 0;

    public Authentication() {
        //初始化一些页面上将要获取的数据
        configMap.put("SupportHD", "1");
        configMap.put("isSmartHomeSTB", "1");
        configMap.put("TVDesktopID", "2");
        configMap.put("Lang", "1");
        configMap.put("FCCSupport", "1");
        configMap.put("AccessMethod", "lan");
        configMap.put("STBType", DeviceInfo.MODEL);
        configMap.put("STBVersion", DeviceInfo.SoftwareVersion);
        configMap.put("SoftwareVersion", DeviceInfo.SoftwareVersion);
        configMap.put("STBID", DeviceInfo.STBID);
        configMap.put("mac",DeviceInfo.MAC);
    }

    /**
     * 联通规范接口
     * @param strToken
     * @return
     */
    @SyJavascriptInterface
    public String CUGetAuthInfo(String strToken){
        return CTCGetAuthInfo(strToken);
    }

    /**
     * 电信规范接口
     * @param strToken
     * @return
     */
    @SyJavascriptInterface
    public String CTCGetAuthInfo(String strToken) {
        String userID = AuthData.userID;
        String password = AuthData.password;
        String stbID = DeviceInfo.STBID;
        String mac = DeviceInfo.MAC;//认证的MAC需要有冒号
        return CTCGetAuthInfo(strToken, userID, password, stbID, mac, "CTC");
    }

    /**
     * 联通规范接口
     * @param strToken
     * @return
     */
    @SyJavascriptInterface
    public String CUGetAuthInfo(String strToken, String userID,
                                 String password, String stbid, String mac, String strLast) {
        return CTCGetAuthInfo(strToken,userID,password,stbid,mac,strLast);
    }

    /**
     * 电信规范接口
     * @param strToken
     * @return
     */
    @SyJavascriptInterface
    public String CTCGetAuthInfo(String strToken, String userID,
                                 String password, String stbid, String mac, String strLast) {
        ALOG.secretLog("CTCGetAuthInfo --> strToken :" + strToken
                + ", UsreID : " + userID + ", Password : " + password
                + ", StbID:" + stbid + ",Mac: " + mac + ",strLast:"
                + strLast);
        String strAuth;
        Random r = new Random();
        strAuth = String.format(Locale.getDefault(), "%08d", (Math.abs(r.nextLong()) % 10000000));
        strAuth += "$";
        strAuth += strToken;
        strAuth += "$";
        strAuth += userID;
        strAuth += "$";
        strAuth += stbid;
        strAuth += "$";
        strAuth += IptvApp.mNetManager.getIp();
        strAuth += "$";
        strAuth += mac;
        strAuth += "$$" + strLast;
        String strPwd = IPTVPlayer.getValue("mac2", "mac|"
                        + configMap.get("EncryptionType") + "|"
                        + configMap.get("UserToken") + "|"
                        + password + "|"
                        + stbid+ "|"
                        + mac);
        String str = Security.Encrypt3DES(strAuth, strPwd);
        ALOG.secretLog(TAG, "CTCGetAuthInfo--->" + str + ", strToken:" + strToken);
        return str;
    }

    /**
     * 联通规范接口
     * @param key
     * @param value
     * @return
     */
    @SyJavascriptInterface
    public int CUSetConfig(String key, String value) {
        return CTCSetConfig(key,value);
    }

    /**
     * 电信规范接口
     * @param key
     * @param value
     * @return
     */
    @SyJavascriptInterface
    public int CTCSetConfig(String key, String value) {
        ALOG.secretLog(TAG, "CTCSetConfig---> key: " + key + ", value: " + value);
        //页面退出IPTV会用到这个方法
        if ((TextUtils.isEmpty(value) || "null".equals(value)) && !key.contains("exitIptvApp")) {
            ALOG.debug("vaule is null");
            return 0;
        }

        if (!key.equalsIgnoreCase("Channel") && !key.equalsIgnoreCase("ServiceEntry")) {
            configMap.put(key, value);
        }
        if ("Channel".equalsIgnoreCase(key)) {
            mLocalChannel++;
            //有些局点中兴平台会下发两次频道列表，第二次下发的频道列表才是正常能用的数据，
            // 目前代码没有考虑这种情况，需要在遇到时添加业务逻辑过滤。
            if(mLocalChannel == mChannelCount){
                isChannelDataCompleted = true;
                notifyAuthCompleted();
            }
            DealChannelInfo(value);
        } else if ("ServiceEntry".equalsIgnoreCase(key)) {
            try {
                JSONObject json = new JSONObject("{" + value + "}");
                String strURL = json.getString("URL");
                String strHotKey = json.getString("HotKey");
                configMap.put(strHotKey, strURL);
                AmtDataManager.putString(IPTVData.IPTV_BASE_URL + strHotKey, strURL, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("EPGGroupNMB".equalsIgnoreCase(key)) {
            AmtDataManager.putString(IPTVData.IPTV_EPG_GROUP_NMB, value, null);
        } else if ("UserGroupNMB".equalsIgnoreCase(key)) {
            AmtDataManager.putString(IPTVData.IPTV_USER_GROUP_NMB, value, null);
        } else if ("ChannelCount".equalsIgnoreCase(key)) {
            try{
                mChannelCount = Integer.valueOf(value);
            }catch(Exception e){
                e.printStackTrace();
            }
        } else if ("EPGDomain".equalsIgnoreCase(key)) {
            AmtDataManager.putString(IPTVData.IPTV_EPGDomain, value, null);
            IptvApp.authManager.updatePercent100();
            notifyAuthCompleted();
        } else if ("UserToken".equalsIgnoreCase(key)) {
            notifyAuthCompleted();
        } else if (("GlobalKeyTable").equalsIgnoreCase(key)) {// 平台下发的键值表
            GlobalKeyHelper.init(value);
        } else if("ShowPic".equals(key)){
            if("2".equals(value)){
               // IptvApp.authManager.hiddenAuthUI();
            }
        } else if("NTPDomain".equals(key)){
            Utils.startNtpUpdate(value);
        }
        //SessionTimeOut了，页面会下发resignon这个key，用于重新认证
        else if("resignon".equals(key)){
            IptvApp.authManager.startAuthService();//启动认证
        }
        //页面调用退出IPTV
        if ("exitIptvApp".equals(key)){
            Iptv2EPG.getIptv2EPG().utility.setValueByName("exitIptvApp");
        }
        else if("SetEpgMode".equals(key)){
            int w ,h = 0;
            if("PAL".equals(value)){
                w = 640;
                h = 530;
                WebViewManager.getManager().getCurrentWebview().getCustom().SetEPGSize(w,h);
            }else if("720P".equals(value)){
                w = 1280;
                h = 720;
                WebViewManager.getManager().getCurrentWebview().getCustom().SetEPGSize(w,h);
            }
        }
        return 0;
    }

    /**
     * 校验认证下发的数据是否完整，一般校验 EPGDomain、UserToken、频道列表。校验成功，则回调认证成功事件。
     * （有特殊情况下，isChannelDataCompleted并不准确，因为EPG下发的ChannelCount可能不准确）
     */
    private void notifyAuthCompleted(){
        if(!TextUtils.isEmpty(CTCGetConfig("EPGDomain"))
                &&!TextUtils.isEmpty(CTCGetConfig("UserToken"))
                &&isChannelDataCompleted){
            IptvApp.authManager.onAuthSuceed();
        }
    }

    private void DealChannelInfo(String channelInfo) {
        String tempInfo  = "";
        Gson gson = new Gson();
        LiveChannel channel = null;
        if (!TextUtils.isEmpty(channelInfo)) {
            tempInfo = channelInfo.trim();
            if (!tempInfo.startsWith("{")) {
                tempInfo = "{" + tempInfo;
            }
            if (!tempInfo.endsWith("}")) {
                tempInfo += "}";
            }
            channel = gson.fromJson(tempInfo, LiveChannel.class);
            LiveChannelHelper.getInstance().setChannelInfo(channel.getUserChannelID(), channel);
        }
    }

    /**
     * 联通规范接口
     * @param key
     * @return
     */
    @SyJavascriptInterface
    public String CUGetConfig(String key) {
        return CTCGetConfig(key);
    }

    /**
     * 电信规范接口
     * @param key
     * @return
     */
    @SyJavascriptInterface
    public String CTCGetConfig(String key) {
        if (TextUtils.isEmpty(key) || "null".equals(key)) {
            return "";
        }
        String value = "";
        if (configMap != null && configMap.containsKey(key)) {
            value = configMap.get(key);
        }
        if ("identityEncode".equalsIgnoreCase(key)) {
            value = IPTVPlayer.getValue("mac", "mac|"
                            + configMap.get("SessionID") + "|"
                            + configMap.get("UserToken") + "|"
                            + configMap.get("shareKey") + "|"
                            + configMap.get("STBID") + "|"
                            + configMap.get("mac"));
        } else if ("iptvmd5Extented".equalsIgnoreCase(key)) {
            value = Security.MD5Encrypt( AuthData.password, 8);
        }
        /**
         * 华为平台获取DirectPlay字段，用于控制进入EPG的方式。
         * 如果是1  就代表直接进入频道播放
         * 如果是0 就代表进入EPG首页
         */
        if ("DirectPlay".equals(key)){
            value = "1" ;
        }

        ALOG.secretLog(TAG, "CTCGetConfig---> key: " + key + ", value: " + value);
        return value;
    }

    @JavascriptInterface
    @SyJavascriptInterface
    public void CUStartUpdate(){
        CTCStartUpdate();
    }

    /**
     * 后台启动升级功能。 终端启动升级流程，并立即返回
     */
    @JavascriptInterface
    @SyJavascriptInterface
    public void CTCStartUpdate(){
        String upgradeDomain = configMap.get("UpgradeDomain");
        String upgradeDomainBackup = configMap.get("UpgradeDomainBackup");
        Intent intent = new Intent();
        intent.setAction("ACTION_IPTV_TO_UPGRADE");
        intent.putExtra("UpgradeDomain", upgradeDomain);// 主升级服务器地址
        intent.putExtra("UpgradeDomainBackup", upgradeDomainBackup);// 备用地址
        intent.putExtra("UserToken", configMap.get("UserToken"));
        IptvApp.app.sendBroadcast(intent);
        ALOG.debug("sendUpgradeBroadcast----" + intent.getExtras());
    }

    @JavascriptInterface
    @SyJavascriptInterface
    public void CULogin(){
        CTCLogin();
    }

    @JavascriptInterface
    @SyJavascriptInterface
    public void CTCLogin(){

    }
}
