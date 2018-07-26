package com.amt.utils;

import android.os.SystemProperties;
import android.text.TextUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import java.io.BufferedInputStream;
import java.io.File;

/**
 * 系统属性读取类，直接读取build.prop文件
 * Created by DonWZ on 2016-9-9
 */
public class SystemPropHelper {
	
	/**系统属性文件目录*/
	private static final String BUILDPROP = "/system/build.prop";
	/**STBID 保存32位完整字符串*/
	public static final String SERIAL = "ro.serialno";//"ro.serialno";
	/**机顶盒名称（非型号）*/
	public static final String MODEL_NAME = "ro.product.name";
	/**盒端设备软件版本信息*/
	public static final String SOFTWARE_VERSION = "ro.build.version.incremental";
	/**盒端设备硬件版本信息*/
	public static final String HARDWARE_VERSION = "ro.build.hardware.id";
	/**OUI*/
	public static final String OUI = "ro.product.manufactureroui";
	/**model*/
	public static final String model = "ro.product.model";
	/**设备厂商*/
	public static final String manufacturer = "ro.product.manufacturer";
	/**
	 * add by zw 20160920 获取build.prop中的属性值
	 * @param key
	 * @param def
	 * @return
	 */
	public static String getProp(String key, String def) {
		//如果从SystemProperties.get里读不到，就直接从build.prop文件读一遍。如果还读不到，就返回def默认值
		String propValue = SystemProperties.get(key);
		if(TextUtils.isEmpty(propValue)){
			try {
				propValue = def;
				Properties p = new Properties();
				try {
					InputStream in = new BufferedInputStream(new FileInputStream(new File(BUILDPROP)));
					if(in!=null){
						p.load(in);
						try {
							in.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
						propValue = p.getProperty(key);
						propValue = TextUtils.isEmpty(propValue) ? def : propValue;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ALOG.debug("get system prop > key : "+key +", value : "+propValue);
		return propValue;
	}
}
