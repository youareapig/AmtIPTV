package com.amt.utils.keymap;

import android.content.Context;
import android.text.TextUtils;
import android.util.Xml;

import com.amt.utils.ALOG;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 读取IPTV键值转换表以及提供键值转换的工具类
 * Created by DonWZ on 2017/5/2.
 */

public class KeyHelper {
    /**EPG键值映射表，以Android键值为key*/
    private static HashMap<Integer,IPTVKey> iptvKeyMaps = new HashMap<Integer, IPTVKey>();
    /**EPG键值映射表，以kname节点为key。（仅限keymap包内使用）*/
    private static HashMap<String,IPTVKey> keyNameMaps = new HashMap<String, IPTVKey>();

    private static boolean isKeyDown = false;
    //按下的键值
    private static int downKeyCode = 0;
    private static HashMap<String,Runnable> runnableTask;
    private static ArrayList<String> runnableList;
    private static Object keyLock = new Object();
    private static Object runnableLock = new Object();
    private static ExecutorService keyupExecutorService = Executors.newSingleThreadExecutor();
    /**
     * 根据Android键值获取EPG键值
     * @param androidCode
     * @return
     */
    public static int getEPGKeyCode(int androidCode){
        if(!iptvKeyMaps.isEmpty() && iptvKeyMaps.containsKey(androidCode)){
            if(iptvKeyMaps.get(androidCode)!=null){
                return iptvKeyMaps.get(androidCode).iptvCode;
            }
        }
        return androidCode;
    }

    /**
     * 获取Android键值对应的键值名称
     * @param androidCode
     * @return
     */
    public static String getKeyName(int androidCode){
        String keyName = "";
        if(iptvKeyMaps.keySet().contains(androidCode)){
            keyName = iptvKeyMaps.get(androidCode).keyName;
        }
        return keyName;
    }
    /**
     * 根据按键名称获取EPG键值。（仅限keymap包内使用）
     * @param keyName
     * @return
     */
    protected static int getEPGKeyCode(String keyName){
        if(!keyNameMaps.isEmpty() && keyNameMaps.containsKey(keyName)){
            if(keyNameMaps.get(keyName) != null){
                return keyNameMaps.get(keyName).iptvCode;
            }
        }
        return 0;
    }

    public static int getAndroidKeyCode(String keyName){
        if(!keyNameMaps.isEmpty() && keyNameMaps.containsKey(keyName)){
            if(keyNameMaps.get(keyName) != null){
                return keyNameMaps.get(keyName).androidCode;
            }
        }
        return 0;
    }

    /**
     * 读取键值映射表
     * @param context
     */
    public static void initKeyXml(Context context){
        if(context == null){
            return;
        }
        InputStream is = null;
        try{
            is = context.getResources().getAssets().open("ITVKey/iptv.xml");
        }catch(IOException e){
            e.printStackTrace();
        }
        try{
            if(is != null ){
                XmlPullParser xml = Xml.newPullParser();
                xml.setInput(is, "UTF-8");
                IPTVKey key = null ;
                int domType = xml.getEventType();
                while(domType != XmlPullParser.END_DOCUMENT){
                    switch (domType){
                        case XmlPullParser.START_TAG:
                            if (xml.getName().equals("keymap")) {
                                key = new IPTVKey();
                            } else if (xml.getName().equals("keycode")) {
                                try {
                                    if (key != null)
                                        key.androidCode = Integer.valueOf(xml.nextText().trim());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (xml.getName().equals("iptvkey")) {
                                try {
                                    if (key != null)
                                        key.iptvCode =  Integer.valueOf(xml.nextText().trim());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (xml.getName().equals("kname")) {
                                try {
                                    if (key != null)
                                        key.keyName = xml.nextText().trim();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if (xml.getName().equals("keymap") && key != null) {
                                iptvKeyMaps.put(key.androidCode,key);
                                keyNameMaps.put(key.keyName,key);
                                key = null;
                            }
                            break;
                    }
                    domType = xml.next();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        EPGKey.init();
    }

    /**
     * 设置按键状态
     * @param keyDown
     * @param keyCode
     */
    public static void keydown(final boolean keyDown,final int keyCode){
        synchronized (keyLock){
            isKeyDown = keyDown;
            if(!isKeyDown){
                downKeyCode = 0;
                synchronized (runnableLock) {
                    long startTime = System.currentTimeMillis();
                    if (runnableList != null && !runnableList.isEmpty()) {
                        try{
                            if(keyupExecutorService!=null){
                                keyupExecutorService.shutdownNow();
                                keyupExecutorService = null;
                            }
                            keyupExecutorService =  Executors.newSingleThreadExecutor();
                        }catch(Exception e){e.printStackTrace();}
                        ALOG.info("KeyHelper>onkeyup!runnable size :" + runnableList.size());
                        Iterator it = runnableList.iterator();//取list里的任务标签，按添加的顺序来执行任务。先进先执行
                        while (it.hasNext()) {
                            Runnable runnable = runnableTask.get(it.next());
                            keyupExecutorService.execute(runnable);
                        }
                        runnableTask.clear();
                        runnableTask = null;
                        runnableList.clear();
                        runnableList = null;
                    }
                    ALOG.info("KeyHelper>run keyup runnable,spent "+(System.currentTimeMillis() - startTime)+"ms...");
                }
            }else{
                downKeyCode = keyCode;
            }
        }
    }

    /**
     * 判断当前按键是否按下，记录长按场景
     * @return
     */
    public static int[] isKeyDown(){
        synchronized (keyLock){
            ALOG.info("KeyHelper>isKeyDown:"+isKeyDown+", keyCode:"+ downKeyCode);
            return new int[]{isKeyDown?1:0,downKeyCode};
        }
    }

    /**
     * 添加一个任务，当按键抬起时执行
     * @param runnableTag 任务标签，添加任务时覆盖之前相同标签的任务。否则累加任务
     * @param runnableOnKeyUp 当按键抬起时需要执行的任务
     */
    public static void addRunnableWhenKeyUp(String runnableTag,Runnable runnableOnKeyUp) {
        if (runnableOnKeyUp == null) {
            return;
        }
        synchronized (runnableLock){
            if(runnableTask == null){
                runnableTask = new HashMap<String, Runnable>();
            }
            if(runnableList == null){
                runnableList = new ArrayList<String>();
            }
            if (TextUtils.isEmpty(runnableTag)) {
                runnableTag = runnableTask.size() + 1 + "";
            }
            runnableTask.put(runnableTag, runnableOnKeyUp);
            if (runnableList.contains(runnableTag)) {
                runnableList.remove(runnableTag);
            }
            runnableList.add(runnableTag);
        }
    }
}
