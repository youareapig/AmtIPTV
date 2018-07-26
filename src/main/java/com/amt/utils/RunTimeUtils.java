package com.amt.utils;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
/**
 * RunTime工具类，用于执行linux命令
 * Created by DonWZ on 2016-9-23
 */
public class RunTimeUtils {

	private static Process currentRuningProcess = null;

	/**
	 * 通过RunTime执行linux命令，获取执行结果.(阻塞执行，耗时操作！！！！)
	 * @param cmdStr cmd命令
	 * @return 执行结果
	 */
	public static String execCmd(String cmdStr){
		String value = "";
		StringBuffer valueString = new StringBuffer();
		InputStream in = null;
		BufferedReader reader = null;
		try {
			Process process = Runtime.getRuntime().exec(cmdStr);
			in = process.getInputStream();
			if(in!=null){
				reader = new BufferedReader(new InputStreamReader(in));
				String subStr = "";
				while ((subStr = reader.readLine()) != null) {
					valueString.append(subStr+"\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception ex){
			ex.printStackTrace();
		} finally{
			try {
				if(reader!=null){
					reader.close();
				}
				if(in!=null){
					in.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		value = valueString.toString();
		if(value.endsWith("\n")){
			value = value.substring(0, valueString.lastIndexOf("\n"));
		}
		ALOG.debug("execCmd > cmd : "+cmdStr+", value : "+value);
		return value;
	}

	/**
	 * 执行cmd命令，并将结果写入文件。
	 * @param cmdStr
	 * @param file
     */
	public static void execCmdAndWriteFile(String cmdStr,String file){
		if(TextUtils.isEmpty(cmdStr) || TextUtils.isEmpty(file)){
			return;
		}
		StringBuffer valueString = new StringBuffer();
		InputStream in = null;
		BufferedReader reader = null;
		try {
			currentRuningProcess = Runtime.getRuntime().exec(cmdStr);
			in = currentRuningProcess.getInputStream();
			if(in!=null){
				reader = new BufferedReader(new InputStreamReader(in));
				String subStr = "";
				while ((subStr = reader.readLine()) != null) {
					valueString.append(subStr+"\n");
					if(valueString.length() > 1024){
						FileHelper.appendFile(valueString.toString(),file);
						valueString.delete(0,valueString.length());
					}
				}
				if(valueString.length()>0){
					FileHelper.appendFile(valueString.toString(),file);
				}
				currentRuningProcess = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception ex){
			ex.printStackTrace();
		} finally{
			try {
				if(reader!=null){
					reader.close();
				}
				if(in!=null){
					in.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 中断当前正在执行的CMD命令
	 */
	public static void exitCurrentCmd(){
		if(currentRuningProcess!=null){
			try{
				currentRuningProcess.destroy();
				currentRuningProcess = null;
			}catch (Exception e){
				e.printStackTrace();
			}
		}
	}
}
