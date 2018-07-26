package com.amt.utils.adv;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.text.TextUtils;
import com.amt.amtdata.AmtDataManager;
import com.amt.amtdata.IPTVData;
import com.amt.utils.ALOG;
import com.amt.utils.NetUtils.HttpUtils;
import com.amt.utils.NetUtils.NetCallback;
import com.amt.utils.Security;

/**
 * lyn 下载广告，上报功能(移动规范)
 * @author Administrator
 *
 */
public class ADLogoManageCMCC {

	ScheduledExecutorService newScheduledThreadPool = Executors.newScheduledThreadPool(3);
	String url = AmtDataManager.getString("ADPlatformUrl", "");// 请求广告url
	String StartPIC2 = "StartPIC2";// 开机LOGO
	String AppLaunchPIC2 = "AppLaunchPIC2";// 开机图片-视频
	String AuthenPIC2 = "AuthenPIC2";// 认证图片
	String userid = AmtDataManager.getString(IPTVData.IPTV_Account,"");// iptv账号
	String terminaltype = android.os.Build.MODEL;// 标识机顶盒的类型"B860AV2.1";
	String terminalversion ="";//SystemProperties.get("persist.sys.versioninfo");//"V81011351.0055";
	long time = 5000;
	final static String SavePath = "/data/local";//下载广告图片等保存目录
	Map<String, String> mapRequest=null;
	String showsuccessurl="/mad_interface/rest/startuptv/showsuccess/submit";//开机广告成功展示通知接口
	String downloadsuccessUrl="/mad_interface/rest/startuptv/downloadsuccess/submit";//开机广告成功下载通知接口
	String submiturl=HttpUtils.getIP(url);
	public ADLogoManageCMCC() {
		ALOG.debug("ADLogoManageCMCC");
		if(submiturl.indexOf("http://")>-1){
			showsuccessurl=submiturl+showsuccessurl;
			downloadsuccessUrl=submiturl+downloadsuccessUrl;
		}
		newScheduledThreadPool.schedule(StartPIC2Task, time,TimeUnit.MILLISECONDS);
		newScheduledThreadPool.schedule(AppLaunchPIC2Task, time+1000,TimeUnit.MILLISECONDS);
		newScheduledThreadPool.schedule(AuthenPIC2Task, time+1500,TimeUnit.MILLISECONDS);
	}

	/***
	 * 开机LOGO下載
	 */
	TimerTask StartPIC2Task = new TimerTask() {

		@Override
		public void run() {
			ALOG.debug("StartPIC2Task===URL:" + url);
			if (!TextUtils.isEmpty(url)) {

				final String param = "slotid=" + StartPIC2 + "&userid=" + userid
						+ "&terminaltype=" + terminaltype + "&terminalversion="
						+ terminalversion;
				//get请求广告地址，获取返回的下载广告地址
				HttpUtils.sendGet(url,param,new NetCallback() {


					@Override
					public void onSuccess(String result) {
						// TODO Auto-generated method stub
						ALOG.debug("====lyn=StartPIC2Task=statusCode:===result:" + result);
						if(result.indexOf("http://")>-1&&result.indexOf("file=")>-1){
							mapRequest = HttpUtils.URLRequest(result);//获取返回广告地址里的参数
							String md5=mapRequest.get("MD5CheckSum");//获取平台下发的md5
							ALOG.debug("----md5:"+md5);
							String filename="logo.jpg";//保存图片名字
							String dir=makeDir(SavePath);
							ALOG.debug("dir:"+dir);
							String resultfile=mapRequest.get("file");//平台下发的文件名字
							final String pathFile = dir+"/"+filename;//保存的文件名字
							final String resultPathFile=dir+"/"+resultfile;//先保存平台下发的文件名字
							final File oldFile = new File(pathFile);
							ALOG.debug("md5pathFile:"+pathFile);
							String fmd5= Security.getFileMD5(oldFile);//获取盒子里文件md5
							ALOG.debug("----fmd5:"+fmd5);
							final String downloadurl=result;//上报的下载地址url

							if(md5.equalsIgnoreCase(fmd5)&&oldFile.exists()){//md5一样就不下载

									showsuccessurl=showsuccessurl+param;
									ALOG.debug("showsuccessurl:"+showsuccessurl);
									if(showsuccessurl.indexOf("http://")>-1){
										HttpUtils.post(showsuccessurl, "downloadurl",downloadurl);//上报广告展示成功
									}

								return;
							}
							ALOG.debug("isFile:"+oldFile.isFile());
							delFile(resultPathFile);
							//请求广告下载地址，获取302跳转的图片下载地址
							HttpUtils.get302Location(result,new NetCallback() {

								@Override
								public void onSuccess(final String result) {
									// TODO Auto-generated method stub
									ALOG.debug("====302url:"+result+"==statusCode:");
									new Thread(new Runnable() {
										@Override
										public void run() {
											URL url;
											try {
												url = new URL(result);
												FileOutputStream writer=null;
												try {
													HttpURLConnection conn = (HttpURLConnection) url.openConnection();
													conn.setConnectTimeout(5000);
													int responseCode = conn.getResponseCode();
													ALOG.debug("responseCode:"+responseCode);
													if(responseCode == 200){
														InputStream input = conn.getInputStream();
														byte[] bytes = new byte[1024];
														int result = input.read(bytes);
														writer = new FileOutputStream(resultPathFile);
														ALOG.debug("result:"+result);
														while(result != -1){
															writer.write(bytes,0,result);
															result = input.read(bytes);
														}
														writer.close();

														try {

															ALOG.debug("set chmod 777 before  filename:" + resultPathFile);
															Process p = Runtime.getRuntime().exec("chmod 777 " + resultPathFile);//给文件权限777
													        int status = p.waitFor();
															ALOG.debug("set chmod 777 after, status :" + status);
													        p.destroy();
													        if(exists(resultPathFile)){	//下载完成后重命名
													        	delFile(pathFile);
													        	File renamefile=new File(resultPathFile);
													        	renamefile.renameTo(new File(pathFile));
													        	Process pro = Runtime.getRuntime().exec("chmod 777 " + pathFile);//给文件权限777
														        int rtstatus = p.waitFor();
																ALOG.debug("set chmod 777 after, rtstatus :" + rtstatus);
																pro.destroy();
													        }
													        if(exists(pathFile)){//下载完成后上报
																downloadsuccessUrl=downloadsuccessUrl+param;
																ALOG.debug("downloadsuccessUrl:"+downloadsuccessUrl);
																if(downloadsuccessUrl.indexOf("http://")>-1){

																	HttpUtils.post(downloadsuccessUrl, "downloadurl",downloadurl);//上报下载成功
																}
															}

														} catch (Exception e) {
															e.printStackTrace();
														}

													}
												} catch (IOException e) {
													e.printStackTrace();
												}finally{

													if(writer != null){
														try {
															writer.close();
														} catch (IOException e) {
															e.printStackTrace();
														}
													}

												}
											} catch (MalformedURLException e) {
												e.printStackTrace();
											}

										}
									}).start();

								}

								@Override
								public void onFail(String errorMsg) {
									// TODO Auto-generated method stub
									ALOG.debug("StartPIC2Task:onError:statusCode>>>errorMsg:"+errorMsg);
								}
							});

						}
					}

					@Override
					public void onFail(String errorMsg) {
						// TODO Auto-generated method stub
						ALOG.debug("====lyn=StartPIC2Task=statusCode:===errorMsg:" + errorMsg);
					}
				});
			}
		}
	};
	/**
	 *  开机图片下載(視頻)
	 */
	TimerTask AppLaunchPIC2Task = new TimerTask() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (!TextUtils.isEmpty(url)) {

				final String param ="slotid=" + AppLaunchPIC2 + "&userid=" + userid
						+ "&terminaltype=" + terminaltype + "&terminalversion="
						+ terminalversion;
				HttpUtils.sendGet(url,param,new NetCallback() {

					@Override
					public void onSuccess(String result) {
						// TODO Auto-generated method stub
						ALOG.debug("====lyn=AppLaunchPIC2Task=statusCode:===result:" + result);
						if(result.indexOf("http://")>-1&&result.indexOf("file=")>-1){
							mapRequest = HttpUtils.URLRequest(result);//获取返回广告地址里的参数
							String md5=mapRequest.get("MD5CheckSum");//获取平台下发的md5
							ALOG.debug("----md5:"+md5);
							final String downloadurl=result;//上报的下载地址url
							String filename="";
							String resultfile=mapRequest.get("file");//平台下发的文件名字
							if(resultfile.indexOf("ts")>-1||resultfile.indexOf("mp4")>-1){//如果是视频格式文件，统一保存为ts格式
								filename="bootvideo.ts";//視頻名字
							}else if(resultfile.indexOf("zip")>-1){
								filename="bootanimation.zip";//壓縮包名字
							}
							int ShowTime=0;
							try{
								ShowTime=Integer.parseInt(mapRequest.get("ShowTime"));
							}catch(Exception e){
								ShowTime=0;
							}
							if(ShowTime>0){
								//SystemProperties.set("persist.sys.bootanim.showtime", ShowTime+"");//设置视频时间给系统

							}
							String dir=makeDir(SavePath);
							ALOG.debug("dir:"+dir);
							final String pathFile = dir+"/"+filename;//最终保存的文件名字
							final String resultPathFile=dir+"/"+resultfile;//先保存平台下发的文件名字
							final File oldFile = new File(pathFile);
							String fmd5=Security.getFileMD5(oldFile);//获取盒子里文件md5
							ALOG.debug("----fmd5:"+fmd5);
							if(md5.equalsIgnoreCase(fmd5)&&oldFile.exists()){//md5一样就不下载

								showsuccessurl=showsuccessurl+param;
								ALOG.debug("showsuccessurl:"+showsuccessurl);
								if(showsuccessurl.indexOf("http://")>-1){
									HttpUtils.post(showsuccessurl, "downloadurl",downloadurl);//上报广告展示成功
								}
								return;
							}
							ALOG.debug("isFile:"+oldFile.isFile());
							delFile(resultPathFile);
							//请求广告下载地址，获取302跳转的图片下载地址
							HttpUtils.get302Location(result,new NetCallback() {

								@Override
								public void onSuccess(final String result) {
									// TODO Auto-generated method stub
									ALOG.debug("====302url:"+result);

									//download(result,pathFile);
									new Thread(new Runnable() {

										@Override
										public void run() {
											URL url;
											try {
												url = new URL(result);
												FileOutputStream writer=null;
												try {
													HttpURLConnection conn = (HttpURLConnection) url.openConnection();
													conn.setConnectTimeout(5000);
													int responseCode = conn.getResponseCode();
													ALOG.debug("responseCode:"+responseCode);
													if(responseCode == 200){
														InputStream input = conn.getInputStream();
														byte[] bytes = new byte[1024];
														int result = input.read(bytes);
														writer = new FileOutputStream(resultPathFile);
														ALOG.debug("result:"+result);
														while(result != -1){
															writer.write(bytes,0,result);
															result = input.read(bytes);
														}
														writer.close();

														try {

															ALOG.debug("set chmod 777 before  filename:" + resultPathFile);
															Process p = Runtime.getRuntime().exec("chmod 777 " + resultPathFile);//给文件权限777
													        int status = p.waitFor();
															ALOG.debug("set chmod 777 after, status :" + status);
													        p.destroy();
													        if(exists(resultPathFile)){	//下载完成后重命名
													        	delFile(pathFile);
													        	File renamefile=new File(resultPathFile);
													        	renamefile.renameTo(new File(pathFile));
													        	Process pro = Runtime.getRuntime().exec("chmod 777 " + pathFile);//给文件权限777
														        int rtstatus = p.waitFor();
																ALOG.debug("set chmod 777 after, rtstatus :" + rtstatus);
																pro.destroy();
													        }
													        if(exists(pathFile)){//下载完成后上报
																downloadsuccessUrl=downloadsuccessUrl+param;
																ALOG.debug("downloadsuccessUrl:"+downloadsuccessUrl);
																if(downloadsuccessUrl.indexOf("http://")>-1){

																	HttpUtils.post(downloadsuccessUrl, "downloadurl",downloadurl);//上报下载成功
																}
															}

														} catch (Exception e) {
															e.printStackTrace();
														}
													}
												} catch (IOException e) {
													e.printStackTrace();
												}finally{
													if(writer != null){
														try {
															writer.close();
														} catch (IOException e) {
															e.printStackTrace();
														}
													}
												}
											} catch (MalformedURLException e) {
												e.printStackTrace();
											}

										}
									}).start();

								}

								@Override
								public void onFail( String errorMsg) {
									// TODO Auto-generated method stub
									ALOG.debug("StartPIC2Task:onError:statusCode:>>>errorMsg:"+errorMsg);
								}
							});

						}

					}

					@Override
					public void onFail(String errorMsg) {
						// TODO Auto-generated method stub
						ALOG.debug("====lyn=AppLaunchPIC2Task=statusCode:===errorMsg:" + errorMsg);
					}
				});
			}
		}
	};
	/**
	 * 認證圖片下載
	 */
	TimerTask AuthenPIC2Task = new TimerTask() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (!TextUtils.isEmpty(url)) {

				final String param = "slotid=" + AuthenPIC2 + "&userid=" + userid
						+ "&terminaltype=" + terminaltype + "&terminalversion="
						+ terminalversion;
				HttpUtils.sendGet(url,param,new NetCallback() {

					@Override
					public void onSuccess(String result) {
						// TODO Auto-generated method stub
						ALOG.debug("====lyn=AuthenPIC2Task=statusCode:===result:" + result);
						if(result.indexOf("http://")>-1&&result.indexOf("file=")>-1){
							mapRequest = HttpUtils.URLRequest(result);//获取返回广告地址里的参数
							String md5=mapRequest.get("MD5CheckSum");//获取平台下发的md5
							ALOG.debug("----md5:"+md5);
							final String downloadurl=result;//上报的下载地址url
							String filename="launcher.jpg";//保存的认证图片名字
							final String dir=makeDir(SavePath);
							ALOG.debug("dir:"+dir);
							final String resultfile=mapRequest.get("file");//平台下发的文件名字
							final String pathFile = dir+"/"+filename;//广告图片的地址名字
							final String resultPathFile=dir+"/"+resultfile;//先保存平台下发的文件名字
							final File oldFile = new File(pathFile);
							String fmd5=Security.getFileMD5(oldFile);//获取盒子里文件md5
							ALOG.debug("----fmd5:"+fmd5);
							if(exists(pathFile)&&md5.equalsIgnoreCase(fmd5)){//md5一样就不下载

								showsuccessurl=showsuccessurl+param;
								ALOG.debug("showsuccessurl:"+showsuccessurl);
								if(showsuccessurl.indexOf("http://")>-1){
									HttpUtils.post(showsuccessurl, "downloadurl",downloadurl);//上报广告展示成功
								}
								return;
							}
							ALOG.debug("isFile:"+oldFile.isFile());
							delFile(resultPathFile);
							//请求广告下载地址，获取302跳转的图片下载地址
							HttpUtils.get302Location(result,new NetCallback() {

								@Override
								public void onSuccess(final String result) {
									// TODO Auto-generated method stub
									ALOG.debug("====302url:"+result);
									new Thread(new Runnable() {
										@Override
										public void run() {
											URL url;
											try {
												url = new URL(result);//传入下载的地址
												FileOutputStream writer=null;
												try {
													HttpURLConnection conn = (HttpURLConnection) url.openConnection();
													conn.setConnectTimeout(5000);
													int responseCode = conn.getResponseCode();
													ALOG.debug("responseCode:"+responseCode);
													if(responseCode == 200){
														InputStream input = conn.getInputStream();
														byte[] bytes = new byte[1024];
														int result = input.read(bytes);
														writer = new FileOutputStream(resultPathFile);//先保存平台下发的图片名字
														ALOG.debug("result:"+result);
														while(result != -1){
															writer.write(bytes,0,result);
															result = input.read(bytes);
														}
														writer.close();
														try {

															ALOG.debug("set chmod 777 before  filename:" + resultPathFile);
															Process p = Runtime.getRuntime().exec("chmod 777 " + resultPathFile);//给文件权限777
													        int status = p.waitFor();
															ALOG.debug("set chmod 777 after, status :" + status);
													        p.destroy();
													        if(exists(resultPathFile)){	//下载完成后重命名
													        	delFile(pathFile);
													        	File renamefile=new File(resultPathFile);
													        	renamefile.renameTo(new File(pathFile));
													        	Process pro = Runtime.getRuntime().exec("chmod 777 " + pathFile);//给文件权限777
														        int rtstatus = p.waitFor();
																ALOG.debug("set chmod 777 after, rtstatus :" + rtstatus);
																pro.destroy();
													        }
													        if(exists(pathFile)){//下载完成后上报
																downloadsuccessUrl=downloadsuccessUrl+param;
																ALOG.debug("downloadsuccessUrl:"+downloadsuccessUrl);
																if(downloadsuccessUrl.indexOf("http://")>-1){

																	HttpUtils.post(downloadsuccessUrl, "downloadurl",downloadurl);//上报下载成功
																}
															}

														} catch (Exception e) {
															e.printStackTrace();
														}

													}
												} catch (IOException e) {
													e.printStackTrace();
												}finally{
													if(writer != null){
														try {
															writer.close();
														} catch (IOException e) {
															e.printStackTrace();
														}
													}
												}
											} catch (MalformedURLException e) {
												e.printStackTrace();
											}

										}
									}).start();

								}

								@Override
								public void onFail(String errorMsg) {
									// TODO Auto-generated method stub
									ALOG.debug("StartPIC2Task:onError:statusCode:>>>errorMsg:"+errorMsg);
								}
							});

						}
					}

					@Override
					public void onFail(String errorMsg) {
						// TODO Auto-generated method stub
						ALOG.debug("====lyn=AuthenPIC2Task=statusCode:===errorMsg:" + errorMsg);
					}
				});
			}
		}
	};



	/**
	 * 下载完成后通知给系统
	 * @param path
	 * @return
	 */
	public int IptvUpdateLogo(String path) {
		ALOG.debug("IptvUpdateLogo >> path:" + path);
		//HiSysManager hisys = new HiSysManager();
		//int update_ret = hisys.updateLogo(path);
		//ALOG.debug("IptvUpdateLogo >> update_ret:" + update_ret);
		return 0;//update_ret;
	}
	/**
	 * 给权限并返回目录
	 * @param dir
	 * @return
	 */
	private static String makeDir(String dir) {
		File savaPath = new File(dir);
		if(!savaPath.exists()){
			savaPath.mkdirs();
		}

		try {
			Process p = Runtime.getRuntime().exec("chmod 777 " + savaPath.getAbsolutePath());
			int status = p.waitFor();
			ALOG.debug(" set chmod 777 : "+savaPath.getAbsolutePath()+", status :"+status);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return savaPath.getAbsolutePath();
	}
	/***
	 * 判断文件是否存在
	 * @param pathFile
	 * @return
	 */
	private static boolean exists(String pathFile){
		File file = new File(pathFile);
		if(file.exists()){

			return true;
		}
		return false;

	}
	/***
	 * 判断文件是否存在
	 * @param pathFile
	 * @return
	 */
	private static void delFile(String pathFile){
		File file = new File(pathFile);
		if(file.exists()){
			file.delete();
		}
	}

	//复制文件
    public static void copyFile(File sourceFile,File targetFile) throws IOException{
        // 新建文件输入流并对它进行缓冲
        FileInputStream input = new FileInputStream(sourceFile);
        BufferedInputStream inBuff = new BufferedInputStream(input);

        // 新建文件输出流并对它进行缓冲
        FileOutputStream output = new FileOutputStream(targetFile);
        BufferedOutputStream outBuff = new BufferedOutputStream(output);

        // 缓冲数组
        byte[] b = new byte[1024 * 5];
        int len;
        while ((len =inBuff.read(b)) != -1) {
            outBuff.write(b, 0, len);
        }
        // 刷新此缓冲的输出流
        outBuff.flush();
        //关闭流
        inBuff.close();
        outBuff.close();
        output.close();
        input.close();
    }
}
