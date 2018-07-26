package com.amt.amtdata.dao;

import android.content.ContentValues;

/**
 * 数据读写接口。
 * Created by DonWZ on 2016/12/21.
 */

public interface IDataInterface {


    String getString(String key, String defValue);

    /**
     * 写入数据
     * @param key
     * @param value
     * @return
     */
    int putString(String key, String value);

    /**
     * 尽量不使用int类型。因为在实际使用过程中，会发现很多数据不知该用String还是int，造成选择困难。
     * 且在多进程共享数据时，容易造成耦合度高的问题，比如设置和IPTV公用一个数据，约定使用String或者int类型，就是耦合度高的表现。
     * 加上此接口，仅仅是为了兼容老版本的配置文件。
     * @param key
     * @param devValue
     * @return
     */
    int getInt(String key, int devValue);

    /**
     * 尽量不使用int类型。因为在实际使用过程中，会发现很多数据不知该用String还是int，造成选择困难。
     * 且在多进程共享数据时，容易造成耦合度高的问题，比如设置和IPTV公用一个数据，约定使用String或者int类型，就是耦合度高的表现。
     * 加上此接口，仅仅是为了兼容老版本的配置文件。
     * @param key
     * @param value
     * @return
     */
    int putInt(String key, int value);

    boolean getBoolean(String key, boolean defaultValue);

    int putBoolean(String key, boolean value);

    /**
     * 批量写入String类型数据。
     * @return
     */
    int putStringBatch(ContentValues values);

    /**
     * 获取配置文件/数据库的绝对存储路径
     * @return
     */
    String getFilePath();

}
