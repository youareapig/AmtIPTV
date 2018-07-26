package com.amt.player.entity;

public class LiveChannel {

	private String Channel; //平台下发的原始JSON字符串

	private String ChannelID; //频道唯一编号 默认频道的ChannelID信 息(如果服务器端没有频 道则返回字符串 “NULL”)

	private String ChannelName; //频道名称 频道名称(如果服务器端 没有频道则返回字符串 “NULL”)

	private int UserChannelID; //用户频道编号 用户频道编号(如果服务 器端没有频道则返回- 1)，一般为频道号

	private String ChannelURL; //频道URL 1.组播直播URL(igmp:// 组播地址:端口| rtsp://单播 地址:端口/路径) 2.单播直播URL(rtsp://单播地址:端口/路径) 3.webchannel(http://服务 地址:端口/路径) 如果服务器端没有频道则 返回字符串“NULL”

	private int TimeShift; //频道是否支持时移 1：支持 0：不支持

	private int TimeShiftLength; //时移时长，单位为秒

	private String ChannelSDP; //频道的SDP信息

	private String TimeShiftURL; //频道的时移地址 当该节目不支持时移，该 值无意义；当该节目为单 播直播，该值无意义。

	private String ChannelLogURL; //台标图片的URL

	private String PositionX; //以屏幕和图标左上角为准 的台标显示横坐标位置

	private String PositionY; //以屏幕和图标左上角为准 的台标显示纵坐标位置

	private String BeginTime; //台标显示开始时间，与频 道开始播放开始时间相对 的时间(以秒为单位)

	private String Interval; //台标两次显示之间的间隔 时间(单位为秒，-1为台 标一直显示，此时忽略 lasting；0代表显示一次)

	private String Lasting; //每次出现台标后的显示时 间，Lasting的值一定要 小于Interval，单位为秒

	private int ChannelType; //频道类型 1：视频； 2：音频； 3：页面频道

	private String ChannelPurchased = "1"; //用户授权标识 1：已授权； 0：未授权

	private int IsHDChannel; //1 ：高清频道； 若为其他值，或没有该字 段，则为标清频道

	private int PreviewEnable = 1; //是否支持预览，0：不支 持；1：支持。 只在未授权的情况下使 用。页面频道都是1。

	private int ChannelLocked = 0; //用户加锁标识 0：未锁定； 1：已锁定 锁定意思为频道上有锁或 用户的父母控制字级别小 于频道的父母控制字级 别，需要输入密码才能观 看。

	private int ActionType; //1：增加 0：删除 如果不存在表示增加，如 果发现已存在，则进行覆 盖。

	private int ChannelFECPort; //FEC的端口号 ,上海电信规范中的定义： 频道支持FEC的端口 号，若该频道支持FEC 则填写端口号，否则为空

	private int FCEnable = 0; //支持FEC标志 ,华为规范中的定义：频道 是否支持FEC (0：不支 持；1：支持），默认为 0。

	private int FCCEnable = 0; //支持FCC标志, 华为规范中的定义：频道 是否支持FCC和 RET(0：不支持FCC和 RET；1：支持FCC和 RET；2：仅支持FCC；3：仅支持RET)，默认为 0。

	private String ChannelFCCIP; //频道支持FCC的ip 地址，若该频道支持FCC则填写，否则为空

	private String ChannelFCCPort = "0"; //频道支持FCC的端口号，若该频道支持FCC则填写端口号，否则为空

	private int UserTeamChannelID = 0;

	private String ChannelFCCServerAddr = "";

	private int LocalTimeShift = 0;

	public String getChannel() {
		return Channel;
	}

	public void setChannel(String channel) {
		Channel = channel;
	}

	public String getChannelID() {
		return ChannelID;
	}

	public void setChannelID(String channelID) {
		ChannelID = channelID;
	}

	public String getChannelName() {
		return ChannelName;
	}

	public void setChannelName(String channelName) {
		ChannelName = channelName;
	}

	public int getUserChannelID() {
		return UserChannelID;
	}

	public void setUserChannelID(int userChannelID) {
		UserChannelID = userChannelID;
	}

	public String getChannelURL() {
		return ChannelURL;
	}

	public void setChannelURL(String channelURL) {
		ChannelURL = channelURL;
	}

	public int getTimeShift() {
		return TimeShift;
	}

	public void setTimeShift(int timeShift) {
		TimeShift = timeShift;
	}

	public int getTimeShiftLength() {
		return TimeShiftLength;
	}

	public void setTimeShiftLength(int timeShiftLength) {
		TimeShiftLength = timeShiftLength;
	}

	public String getChannelSDP() {
		return ChannelSDP;
	}

	public void setChannelSDP(String channelSDP) {
		ChannelSDP = channelSDP;
	}

	public String getTimeShiftURL() {
		return TimeShiftURL;
	}

	public void setTimeShiftURL(String timeShiftURL) {
		TimeShiftURL = timeShiftURL;
	}

	public String getChannelLogURL() {
		return ChannelLogURL;
	}

	public void setChannelLogURL(String channelLogURL) {
		ChannelLogURL = channelLogURL;
	}

	public String getPositionX() {
		return PositionX;
	}

	public void setPositionX(String positionX) {
		PositionX = positionX;
	}

	public String getPositionY() {
		return PositionY;
	}

	public void setPositionY(String positionY) {
		PositionY = positionY;
	}

	public String getBeginTime() {
		return BeginTime;
	}

	public void setBeginTime(String beginTime) {
		BeginTime = beginTime;
	}

	public String getInterval() {
		return Interval;
	}

	public void setInterval(String interval) {
		Interval = interval;
	}

	public String getLasting() {
		return Lasting;
	}

	public void setLasting(String lasting) {
		Lasting = lasting;
	}

	public int getChannelType() {
		return ChannelType;
	}

	public void setChannelType(int channelType) {
		ChannelType = channelType;
	}

	public String getChannelPurchased() {
		return ChannelPurchased;
	}

	public void setChannelPurchased(String channelPurchased) {
		ChannelPurchased = channelPurchased;
	}

	public int getIsHDChannel() {
		return IsHDChannel;
	}

	public void setIsHDChannel(int isHDChannel) {
		IsHDChannel = isHDChannel;
	}

	public int getPreviewEnable() {
		return PreviewEnable;
	}

	public void setPreviewEnable(int previewEnable) {
		PreviewEnable = previewEnable;
	}

	public int getChannelLocked() {
		return ChannelLocked;
	}

	public void setChannelLocked(int channelLocked) {
		ChannelLocked = channelLocked;
	}

	public int getActionType() {
		return ActionType;
	}

	public void setActionType(int actionType) {
		ActionType = actionType;
	}

	public int getChannelFECPort() {
		return ChannelFECPort;
	}

	public void setChannelFECPort(int channelFECPort) {
		ChannelFECPort = channelFECPort;
	}

	public int getFCEnable() {
		return FCEnable;
	}

	public void setFCEnable(int FCEnable) {
		this.FCEnable = FCEnable;
	}

	public int getFCCEnable() {
		return FCCEnable;
	}

	public void setFCCEnable(int FCCEnable) {
		this.FCCEnable = FCCEnable;
	}

	public String getChannelFCCIP() {
		return ChannelFCCIP;
	}

	public void setChannelFCCIP(String channelFCCIP) {
		ChannelFCCIP = channelFCCIP;
	}

	public String getChannelFCCPort() {
		return ChannelFCCPort;
	}

	public void setChannelFCCPort(String channelFCCPort) {
		ChannelFCCPort = channelFCCPort;
	}

	public int getUserTeamChannelID() {
		return UserTeamChannelID;
	}

	public void setUserTeamChannelID(int userTeamChannelID) {
		UserTeamChannelID = userTeamChannelID;
	}

	public String getChannelFCCServerAddr() {
		return ChannelFCCServerAddr;
	}

	public void setChannelFCCServerAddr(String channelFCCServerAddr) {
		ChannelFCCServerAddr = channelFCCServerAddr;
	}

	public int getLocalTimeShift() {
		return LocalTimeShift;
	}

	public void setLocalTimeShift(int localTimeShift) {
		LocalTimeShift = localTimeShift;
	}
}
