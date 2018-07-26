package com.amt.amtdata;

/**
 * 用于数据模块权限验证，包名、进程ID、用户ID信息存放
 */
public class AmtPermission {
	
	public boolean isWritePermissonGranted = false;
	public String callingPackageName="";
	public int callingUid;
	public int callingPid;

}
