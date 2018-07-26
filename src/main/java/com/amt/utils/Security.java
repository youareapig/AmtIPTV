package com.amt.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 安全类，用于加密、解密等操作
 * Created by DonWZ on 2016-9-18
 */
public class Security {
	
	private static final String PASSWORD_CRYPT_KEY = Utils.GetValue("SECRETKEY");
	private final static String DES = "DES";

	
	/**
	 * IPTV平台认证时3DES加密数据(CTCGetAuthInfo方法加密过程)
	 * 
	 * @param sData
	 *            Random+“$”+EncryToken+”$”+UserID+”$”+STBID+”$”+IP+”$”+MAC+”$”+
	 *            Reserved+”$”+“CTC”
	 * @param sKey
	 *            <ol>
	 *            <li>
	 *            在不采用机卡分离方式的情况下，使用用户的密码（由运营商统一分配，用户可通过终端操作界面进行配置）作为3DES密钥进行加密；</li>
	 *            <li>在采用机卡分离方式的情况下，采用为终端SIM卡内保存的密钥ICKey作为3DES 密钥；</li>
	 *            <li>密钥长度不足24字节时，右补ASCII字符“0”；</li>
	 *            <li>内容采用PKCS5Padding方式填充，即长度以8字节切分，不能被8整除的末尾
	 *            部分，根据长度不足8字节的部分，填充“0x01”—“0x08”，如不足1字节，则填充
	 *            1个“0x01”，如不足2字节，则填充2个“0x02”，以此类推，如整除，则填充8个 “0x08”；</li>
	 *            <li>加密算法采用的鉴权算法应采用3DES（168位，ECB方式）。</li>
	 *            <li>Random为十进制表示的随机数字，范围为0~99999999</li>
	 *            <li>【注】返回结果使用ASCII形式的十六进制编码表示，采用大写“ABCDEF”，如加密结果为
	 *            8字节二进制字符串：0x0123456789ABCDEF，则Authenticator为16字节ASCII字符
	 *            串：”0123456789ABCDEF”。</li>
	 *            </ol>
	 * @return
	 */
	public static String Encrypt3DES(String sData, String sKey) {
		int n = sKey.length();
		int i = 0;
		int nDataLen = sData.length();
		String sEnData = "";
		int nBQ = 8 - (nDataLen % 8);
		byte btData[] = new byte[nDataLen + nBQ];
		byte src[] = sData.getBytes();
		for (i = 0; i < nDataLen; i++) {
			btData[i] = src[i];
		}
		for (i = 0; i < nBQ; i++) {
			btData[nDataLen + i] = (byte) nBQ;
		}
		for (i = n; i < 24; i++)
			sKey += '0';
		try {
			SecretKey dstKey = new SecretKeySpec(sKey.getBytes(), "DESede");
			Cipher cr = Cipher.getInstance("DESede");
			cr.init(Cipher.ENCRYPT_MODE, dstKey);
			byte dstData[] = cr.doFinal(btData);
			for (int j = 0; j < btData.length; j++) {
				sEnData += String.format("%02X", dstData[j] & 0xFF);
			}
			ALOG.debug("KEY", sKey);
			return sEnData;
		} catch (java.security.NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (javax.crypto.NoSuchPaddingException e2) {
			e2.printStackTrace();
		} catch (java.lang.Exception e3) {
			e3.printStackTrace();
		}
		return "";
	}
	
	/**
	 * MD5加密算法
	 * 
	 * @param originalPassword
	 *            明文密码
	 * @param cutLength
	 *            需要截取密文的长度（为0时不截取）
	 * @return
	 */
	public static String MD5Encrypt(String originalPassword, int cutLength) {
		String afterEncryptPassword = null;

		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(originalPassword.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		byte[] byteArray = messageDigest.digest();

		StringBuffer md5StrBuff = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i])
						.toUpperCase());
		}
		afterEncryptPassword = md5StrBuff.toString();
		if (cutLength > 0 && cutLength < afterEncryptPassword.length()) {
			afterEncryptPassword = afterEncryptPassword.substring(0, cutLength);
		}

		return afterEncryptPassword.toUpperCase();//加密后转换为字母转换为大写
	}

	/**
	 * DES加密
	 * 
	 * @param str
	 * @return
	 */
	public static String EncryptionDES(String str) {
		if (TextUtils.isEmpty(str)) {
			return str;
		}
		try {
			return StringTools.byte2hex(encrypt(str.getBytes(),
					PASSWORD_CRYPT_KEY.getBytes())).toUpperCase();
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * DES解密
	 * 
	 * @param str
	 * @return
	 */
	public static String DecryptionDES(String str) {
		if (TextUtils.isEmpty(str)) {
			return str;
		}
		try {
			return new String(decrypt(StringTools.hex2byte(str.getBytes()),
					PASSWORD_CRYPT_KEY.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/** */
	/**
	 * 加密
	 * 
	 * @param src
	 *            数据源
	 * @param key
	 *            密钥，长度必须是8的倍数
	 * @return 返回加密后的数据
	 * @throws Exception
	 */
	private static byte[] encrypt(byte[] src, byte[] key) throws Exception {
		// DES算法要求有一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		// 从原始密匙数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);
		// 创建一个密匙工厂，然后用它把DESKeySpec转换成
		// 一个SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance(DES);
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
		// 现在，获取数据并加密
		// 正式执行加密操作
		return cipher.doFinal(src);
	}

	/** */
	/**
	 * 解密
	 * 
	 * @param src
	 *            数据源
	 * @param key
	 *            密钥，长度必须是8的倍数
	 * @return 返回解密后的原始数据
	 * @throws Exception
	 */
	private static byte[] decrypt(byte[] src, byte[] key) throws Exception {
		// DES算法要求有一个可信任的随机数源
		SecureRandom sr = new SecureRandom();
		// 从原始密匙数据创建一个DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);
		// 创建一个密匙工厂，然后用它把DESKeySpec对象转换成
		// 一个SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance(DES);
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
		// 现在，获取数据并解密
		// 正式执行解密操作
		return cipher.doFinal(src);
	}

	/**
	 * so加密数据
	 * @param data
	 * @return
     */
	public static String EncryptSo(String data){
		return Utils.EncyptData(data);
	}

	/**
	 * so解密数据
	 * @param data
	 * @return
     */
	public static String DecryptSo(String data){
		return Utils.DecyptData(data);
	}
	/**
	 * 获取单个文件的MD5值！
	 *
	 * @param file
	 * @return
	 */

	public static String getFileMD5(File file) {

		if(file==null || !file.exists()){
			return "";
		}
		MessageDigest messageDigest;
		RandomAccessFile randomAccessFile = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			randomAccessFile=new RandomAccessFile(file,"r");
			byte[] bytes=new byte[1024*1024*10];
			int len=0;
			while ((len=randomAccessFile.read(bytes))!=-1){
				messageDigest.update(bytes,0, len);
			}
			BigInteger bigInt = new BigInteger(1, messageDigest.digest());
			String md5 = bigInt.toString(16);
			while (md5.length() < 32) {
				md5 = "0" + md5;
			}
			return md5;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (randomAccessFile != null) {
					randomAccessFile.close();
					randomAccessFile = null;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "";


	}
}
