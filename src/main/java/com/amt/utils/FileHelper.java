package com.amt.utils;

import android.text.TextUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 对文件的操作工具类
 * Created by DonWZ on 2017/5/27.
 */

public class FileHelper {

    private static String TAG="FileHelper";
    // 用于读取文件MD5值
    private static MessageDigest messagedigest = null;


    /**
     * 拷贝文件  默认覆盖文件
     * add djf
     * @param srcFileName   源文件
     * @param destFileName  目的文件
     * @return
     * @throws IOException
     */
    public static boolean copyFile(String srcFileName, String destFileName){
        boolean isSuccess=false;
        try {
            isSuccess=copyFile(srcFileName,destFileName,true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  isSuccess;
    }

    /**
     * 拷贝文件
     * add djf
     * @param srcFileName   源文件
     * @param destFileName  目的文件
     * @param reWrite       若以存在是否需要覆盖
     * @return
     * @throws IOException
     */
    public static boolean copyFile(String srcFileName, String destFileName, boolean reWrite)
            throws IOException {
        ALOG.debug(TAG, "copyFile, begin");
        File srcFile = new File(srcFileName);
        File destFile = new File(destFileName);
        if(!srcFile.exists()) {
            ALOG.debug(TAG, "copyFile, source file not exist.");
            return false;
        }
        if(!srcFile.isFile()) {
            ALOG.debug(TAG, "copyFile, source file not a file.");
            return false;
        }
        if(!srcFile.canRead()) {
            ALOG.debug(TAG, "copyFile, source file can't read.");
            return false;
        }
        if(!destFile.getParentFile().exists()){
            destFile.getParentFile().mkdirs();
        }
        if(destFile.exists() && reWrite){
            ALOG.debug(TAG, "copyFile, before copy File, delete first.");
            destFile.delete();
        }

        try {
            InputStream inStream = new FileInputStream(srcFile);
            FileOutputStream outStream = new FileOutputStream(destFile);
            byte[] buf = new byte[1024];
            int byteRead = 0;
            while ((byteRead = inStream.read(buf)) != -1) {
                outStream.write(buf, 0, byteRead);
            }
            outStream.flush();
            outStream.close();
            inStream.close();
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //校验两个文件的md5值
        boolean isSuccess=checkFileMD5Same(srcFile,destFile);
        if (isSuccess){
            ALOG.debug(TAG, "copyFile, success");
            return true;
        }else{
            ALOG.debug(TAG, "copyFile, fail");
            return false;
        }


    }

    /**
     * 删除目录（包括子目录和文件）
     * @param dir
     * @throws Exception
     */
    public static void deleteDir(File dir) throws Exception{
        if (dir != null && dir.exists()) {
            String[] children = dir.list();
            if (children != null) {
                if (children.length > 0) {
                    // 递归删除目录中的子目录下
                    for (int i = 0; i < children.length; i++) {
                        File cache = new File(dir, children[i]);
                        if (cache.exists()) {
                            if (cache.isDirectory()) {
                                deleteDir(cache);
                            } else {
                                cache.delete();
                            }
                        }
                    }
                }
            }
        }
    }



    /**
     * 获取文件的MD5值
     *
     * @param file
     * add djf
     * @return
     * @throws IOException
     */
    public static String getFileMD5String(File file) throws IOException {
        if (!file.exists()) {
            return "";
        }
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        FileInputStream in = new FileInputStream(file);
        FileChannel ch = in.getChannel();
        MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0,
                file.length());
        messagedigest.update(byteBuffer);
        return StringTools.byte2hex(messagedigest.digest());
    }

    /**
     * 获取字节数组的MD5值
     *
     * @param bytes
     * add djf
     * @return
     */
    public static String getBytesMD5String(byte[] bytes) throws IOException {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        messagedigest.update(bytes);
        return StringTools.byte2hex(messagedigest.digest());
    }
    /**
     * 校验两个文件的MD5值
     *
     * @param a
     * @param b
     * add djf
     * @return
     */
    public static boolean checkFileMD5Same(File a, File b) {
        String md5a = "";
        try {
            md5a = getFileMD5String(a);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String md5b = "";
        try {
            md5b = getFileMD5String(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (md5a.length() <= 0 || md5b.length() <= 0) {
            return false;
        }
        if (!md5a.equals(md5b)) {
            ALOG.debug(TAG, "MD5 vaule imparity!!!!!");
        } else {
            ALOG.debug(TAG, "MD5 value same!!");
        }
        return md5a.equals(md5b);
    }

    /**
     * 将字符串内容写入文件
     * @param content
     * @return
     */
    public static boolean writeFile(String content,String path){
        boolean isSuccessed = false;
        try{
            if(TextUtils.isEmpty(content) || TextUtils.isEmpty(path)){
                return isSuccessed;
            }
            File file = new File(path);
            if(file.exists()){
                file.delete();
            }
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
            BufferedWriter buffer = new BufferedWriter(new FileWriter(file, true));
            buffer.write(content);
            buffer.flush();
            buffer.close();
            ALOG.info(TAG,"writeFile successed! file path : "+path);
            isSuccessed = true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return isSuccessed;
    }

    /**
     * 追加写入文件
     * @param content
     * @param path
     * @return
     */
    public static boolean appendFile(String content,String path){
        boolean isSuccessed = false;
        try{
            if(TextUtils.isEmpty(content) || TextUtils.isEmpty(path)){
                return isSuccessed;
            }
            File file = new File(path);
            if(!file.exists()){
                if(!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
            }
            BufferedWriter buffer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true)));
            buffer.write(content+"\n");
            buffer.flush();
            buffer.close();
        //    ALOG.info(TAG,"appendFile successed! file path : "+path);
            isSuccessed = true;
        }catch(Exception e){
            e.printStackTrace();
        }
        return isSuccessed;
    }

}
