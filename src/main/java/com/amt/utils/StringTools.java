package com.amt.utils;
/**
 * 字符串工具类
 * Created by DonWZ on 2016-9-23
 */
public class StringTools {

	/**
	 * byte数组转换成16进制字符串
	 * @param b
	 * @return
	 */
	public static String byte2hex(byte[] b) {
		StringBuffer sb = new StringBuffer(b.length);
		String stmp = "";
		int len = b.length;
		for (int n = 0; n < len; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			if (stmp.length() == 1)
				sb = sb.append("0").append(stmp);
			else {
				sb = sb.append(stmp);
			}
		}
		return String.valueOf(sb);
	}
	/**
	 * 16进制字符串转byte数组
	 * @param b
	 * @return
	 */
	public static byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0)
			throw new IllegalArgumentException("长度不是偶数");
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return b2;
	}
}
