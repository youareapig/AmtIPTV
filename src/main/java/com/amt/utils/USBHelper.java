package com.amt.utils;

import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import java.io.File;
import java.io.InputStream;

/**
 * Created by DJF on 2017/6/7.
 */
public class USBHelper {

    private static String TAG="USBHelper";
    /**
     * usb 路径
     *当usb路径为空的的时候 内置sdcard
     * 如：
     * usbPath-->/storage/emulated/0
     * 注：路径后面没有/
     */

    public static String usbPath;

    /**
     * 扫描U盘路径
     * @return   String UsbPath
     */
    public static void updateUsbPath() {
        String strMountInfo = "";

        // 1.首先获得系统已加载的文件系统信息
        try
        {
            // 创建系统进程生成器对象
            ProcessBuilder objProcessBuilder = new ProcessBuilder();
            // 执行 mount -h 可以看到 mount : list mounted filesystems
            // 这条命令可以列出已加载的文件系统
            objProcessBuilder.command( "mount" ); // 新的操作系统程序和它的参数
            // 设置错误输出都将与标准输出合并
            objProcessBuilder.redirectErrorStream( true );
            // 基于当前系统进程生成器的状态开始一个新进程，并返回进程实例
            Process objProcess = objProcessBuilder.start();
            // 阻塞线程至到本地操作系统程序执行结束，返回本地操作系统程序的返回值
            objProcess.waitFor();

            // 得到进程对象的输入流，它对于进程对象来说是已与本地操作系统程序的标准输出流(stdout)相连接的
            InputStream objInputStream = objProcess.getInputStream();

            byte[] buffer = new byte[1024];

            // 读取 mount 命令程序返回的信息文本
            while ( -1 != objInputStream.read( buffer ) )
            {
                strMountInfo = strMountInfo + new String( buffer );
            }
            // 关闭进程对象的输入流
            objInputStream.close();

            // 终止进程并释放与其相关的任何流
            objProcess.destroy();

            // 2.然后再在系统已加载的文件系统信息里查找 SD 卡路径
            // mount 返回的已加载的文件系统信息是以一行一个信息的形式体现的，
            // 所以先用换行符拆分字符串
            String[] lines = strMountInfo.split( "\n" );
            // 清空该字符串对象，下面将用它来装载真正有用的 SD 卡路径列表
            strMountInfo = "";

            outer:for ( int i = 0; i < lines.length; i++ )
            {
                ALOG.debug(TAG,"mount info-->"+lines[i]);
                // 如果该行内有 /ntfs/和 vfat字符串,或者包含/dev/block/vold/字符串，说明可能是内/外置 SD 卡的挂载路径
                if (lines[i].contains("/dev/block/vold/8:1")/* || (( -1 != lines[i].indexOf( " /mnt/" ) || -1 != lines[i].indexOf( " /storage/" ))&& // 前面要有空格，以防断章取义
                        (-1 != lines[i].indexOf( " vfat " ) || lines[i].contains("ntfs")))*/)
                {
                    // 再以空格分隔符拆分字符串
                    String[] blocks = lines[i].split( "\\s" ); // \\s 为空格字符
                    for (int j = 0; j < blocks.length;j++)
                    {
                        // 如果字符串中含有/mnt/或/storage/字符串，说明可能是我们要找的 SD 卡挂载路径
                        if ( -1 != blocks[j].indexOf("/mnt/") || -1 != blocks[j].indexOf("/storage/") )
                        {
                            // 排除重复的路径
                            if ( -1 == strMountInfo.indexOf( blocks[j] ) )
                            {
                                // 用分号符(;)分隔 SD 卡路径列表，
                                strMountInfo += blocks[j] + ";";
                                break outer;
                            }
                        }
                    }
                }
            }
        }
        catch ( Exception e )
        {
            ALOG.debug(TAG,"getUsbPath Error-->"+e.toString());
        }

        if (TextUtils.isEmpty(strMountInfo)){
            //为空获取内置SD卡路径
            usbPath=getUsbDirectory(); //Environment.getExternalStorageDirectory().getPath();
        }else {
            usbPath = strMountInfo;
        }
        //如果获取的路径含有;我们将把他除去
        if (usbPath.contains(";")) {
            usbPath=usbPath.substring(0,usbPath.lastIndexOf(";"));
        }
        ALOG.debug(TAG,"usbPath-->"+usbPath);
    }

    private static  String[] usbDefaultPath={"/mnt/sdcard", "/sdcard"};
    public static String getUsbDirectory() {
        String usbPath="";
        // 处理保存LOG路径
        {
            // if (com.SyMedia.Config.Config.LOGTOFILE
            // && com.SyMedia.Config.Config.USBDIRECTORYS != null)
            for (int i = 0; i < usbDefaultPath.length; i++) {
                File file = new File(usbDefaultPath[i]);
                if (file.exists()) {
                    if (file.canRead() && file.canWrite()) {

                        long s = getAvailaleSize(file.getPath(), Unit.B);
                        if (s > 0) {
                            usbPath = usbDefaultPath[i];
                            break;
                        } else {
                            ALOG.debug(TAG,file.getPath() + " size:" + s);
                        }
                    } else {
                        ALOG.debug(TAG, file.getPath() + " not read or not write.");
                    }
                } else {
                    ALOG.debug(TAG, file.getPath() + " not exist.");
                }
            }
            ALOG.debug(TAG," usbPath-->" + usbPath);
            if (TextUtils.isEmpty(usbPath)){
                usbPath=Environment.getExternalStorageDirectory().getPath();
            }
            ALOG.debug(TAG,"fianll usbPath-->" + usbPath);
            return usbPath;
        }
    }

    private static enum Unit {
        G, M, KB, B
    }
    /**
     * 取得指定目录空闲大小
     *
     * @return
     */
    public static long getAvailaleSize(String path, Unit unit) {

        long size = 0;
        try {

            try {
                File file = new File(path);
                if (!file.exists())
                    file.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
            }

            StatFs stat = new StatFs(path);
			/* 获取block的SIZE */
            long blockSize = stat.getBlockSize();
			/* 空闲的Block的数量 */
            long availableBlocks = stat.getAvailableBlocks();
			/* 返回bit大小值 */
            switch (unit) {
                case G:
                    size = (availableBlocks * blockSize) / 1024 / 1024 / 1024;
                    break;
                case M:
                    size = (availableBlocks * blockSize) / 1024 / 1024;
                    break;
                case KB:
                    size = (availableBlocks * blockSize) / 1024;
                    break;
                case B:
                    size = (availableBlocks * blockSize);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }


}
