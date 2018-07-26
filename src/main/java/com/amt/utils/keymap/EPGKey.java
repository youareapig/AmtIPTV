package com.amt.utils.keymap;

/**
 * 提供EPG键值
 * Created by DonWZ on 2017/6/8.
 */
public class EPGKey {
    /** 首页键 */
    public static final int HOME = KeyHelper.getEPGKeyCode("HOME");
    /** 返回键 */
    public static final int BACK = KeyHelper.getEPGKeyCode("BACK");
    /** 确定键 */
    public static final int ENTER = KeyHelper.getEPGKeyCode("ENTER");
    /** 左 */
    public static final int LEFT = KeyHelper.getEPGKeyCode("LEFT");
    /** 上 */
    public static final int UP = KeyHelper.getEPGKeyCode("UP");
    /** 右 */
    public static final int RIGHT = KeyHelper.getEPGKeyCode("RIGHT");
    /** 下 */
    public static final int DOWN = KeyHelper.getEPGKeyCode("DOWN");
    /** 上页 */
    public static final int PAGE_UP = KeyHelper.getEPGKeyCode("PAGE_UP");
    /** 下页 */
    public static final int PAGE_DOWN = KeyHelper.getEPGKeyCode("PAGE_DOWN");
    /** 频道加 channel + */
    public static final int CHANNEL_ADD = KeyHelper.getEPGKeyCode("CHANNEL_ADD");
    /** 频道减 channel - */
    public static final int CHANNEL_MIN = KeyHelper.getEPGKeyCode("CHANNEL_MIN");
    /** 音量加 */
    public static final int VOLUME_ADD = KeyHelper.getEPGKeyCode("VOLUME_ADD");
    /** 音量减 */
    public static final int VOLUME_MIN = KeyHelper.getEPGKeyCode("VOLUME_MIN");
    /** 静音键 */
    public static final int MUTE = KeyHelper.getEPGKeyCode("MUTE");
    /** 快进 */
    public static final int FF = KeyHelper.getEPGKeyCode("FF");
    /** 快退 */
    public static final int FR = KeyHelper.getEPGKeyCode("FR");
    /** 定位 */
    public static final int SEEK = KeyHelper.getEPGKeyCode("SEEK");
    /** 停止 */
    public static final int STOPPLAY = KeyHelper.getEPGKeyCode("STOPPLAY");
    /** 播放/暂停 */
    public static final int PLAYORPAUSE = KeyHelper.getEPGKeyCode("PLAYORPAUSE");
    /** 直播 快捷键 红色 */
    public static final int LIVE = KeyHelper.getEPGKeyCode("LIVE");
    /** 回看 快捷键 绿色 */
    public static final int TVOD = KeyHelper.getEPGKeyCode("TVOD");
    /** 点播 快捷键 黄色 */
    public static final int VOD = KeyHelper.getEPGKeyCode("VOD");
    /** 信息 快捷键 蓝色 */
    public static final int INFO = KeyHelper.getEPGKeyCode("INFO");
    /** 频道快速回切 */
    public static final int CHANNEL_BACK = KeyHelper.getEPGKeyCode("CHANNEL_BACK");
    /** 声道 */
    public static final int AUDIO_TRACK = KeyHelper.getEPGKeyCode("AUDIO_TRACK");
    /** 互动**/
    public static final int ANTERACTION = KeyHelper.getEPGKeyCode("互动");
    /** # 键*/
    public static final int POUND  = KeyHelper.getEPGKeyCode("#");
    /** *键 */
    public static final int ASTERISK = KeyHelper.getEPGKeyCode("*");
    /**  播放器事件虚拟键值 */
    public static final int VIRTUAL_KEY = 768;

    public static final int NUMBER_0 = KeyHelper.getEPGKeyCode("0");
    public static final int NUMBER_1 = KeyHelper.getEPGKeyCode("1");
    public static final int NUMBER_2 = KeyHelper.getEPGKeyCode("2");
    public static final int NUMBER_3 = KeyHelper.getEPGKeyCode("3");
    public static final int NUMBER_4 = KeyHelper.getEPGKeyCode("4");
    public static final int NUMBER_5 = KeyHelper.getEPGKeyCode("5");
    public static final int NUMBER_6 = KeyHelper.getEPGKeyCode("6");
    public static final int NUMBER_7 = KeyHelper.getEPGKeyCode("7");
    public static final int NUMBER_8 = KeyHelper.getEPGKeyCode("8");
    public static final int NUMBER_9 = KeyHelper.getEPGKeyCode("9");

    /** 手动保存EPG键值 */
    public static final int SAVE_EPG = 1000;

    /**
     * 四色键键值：红色直播键 275
     * 四色键键值：绿色回看键 276
     * 四色键键值：黄色点播键 277
     * 四色键键值：蓝色信息键 278
     *联通集采纯色键红色 1108
     *联通集采纯色键绿色 1109
     *联通集采纯色键黄色 1180
     *联通集采纯色键蓝色 268
     * */
    public static final int[] FOUR_COLOR_KEY = new int[] {LIVE, TVOD, VOD, INFO};

    /** 联通集采纯色键 红色 */
    public static final int Red = KeyHelper.getEPGKeyCode("Red");
    /** 联通集采纯色键 绿色 */
    public static final int Green = KeyHelper.getEPGKeyCode("Green");
    /** 联通集采纯色键 黄色 */
    public static final int Yellow = KeyHelper.getEPGKeyCode("Yellow");
    /** 联通集采纯色键 蓝色 */
    public static final int Bule = KeyHelper.getEPGKeyCode("Bule");
    /** 联通集采纯色键 黄色 */
    public static final int location = KeyHelper.getEPGKeyCode("location");
    /** 联通集采纯色键 蓝色 */
    public static final int application = KeyHelper.getEPGKeyCode("application");
    public static void init(){
        //TODO 空方法，触发上面常量初始化的。
    };
}
