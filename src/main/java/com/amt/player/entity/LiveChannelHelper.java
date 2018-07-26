package com.amt.player.entity;

import android.util.SparseArray;

import com.amt.amtdata.AmtDataManager;
import com.amt.amtdata.IPTVData;
import com.amt.utils.ALOG;

/**
 * Created by zw on 2017/5/24.
 */

public class LiveChannelHelper {
    public static final String TAG = "LiveChannelHelper";
    private static LiveChannelHelper helper = null;
    private static SparseArray<LiveChannel> channelMap = new SparseArray<LiveChannel>();
    private LiveChannel currentChannel;
    private LiveChannelHelper() {}

    public static LiveChannelHelper getInstance() {
        if (helper == null) {
            helper = new LiveChannelHelper();
        }
        return helper;
    }

    public void setChannelInfo(int userChannelID, LiveChannel liveChannel) {
        ALOG.info(TAG, "setChannelInfo---> userChannelID: " + userChannelID);
        if (userChannelID > 0 && liveChannel!= null) {
            channelMap.put(userChannelID, liveChannel);
        }
    }

    public SparseArray<LiveChannel> getChannelInfo() {
        ALOG.info(TAG, "getChannelInfo---> channelMap: " + channelMap);
        if (channelMap.size() > 0) {
            return  channelMap;
        } else {
            return  null;
        }
    }

    /**
     * 记录最后一次播放频道号
     * @param userChannelID
     */
    public void setLastChannelID(int userChannelID) {
        ALOG.info(TAG, "setLastChannelID---> userChannelID: " + userChannelID);
        AmtDataManager.putInt(IPTVData.IPTV_LastchannelID, userChannelID, null);
    }


    public LiveChannel getCurrentChannel() {
        if (currentChannel != null) {
            return currentChannel;
        } else {
            return null;
        }
    }

    public void setCurrentChannel(LiveChannel currentChannel) {
        this.currentChannel = currentChannel;
    }

    public LiveChannel getChannelByUserChannelID(int userChannelID) {
        ALOG.info(TAG, "getChannelByUserChannelID--->userChannelID: " + userChannelID);
        if (userChannelID > 0) {
            return channelMap.get(userChannelID);
        } else {
            return null;
        }
    }

}
