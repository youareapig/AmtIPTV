package com.amt.amtdata.dao;

import android.content.Context;

/**
 * Created by DonWZ on 2016/12/21.
 */

public class DataFactory {

    private static IDataInterface iData;

    public static IDataInterface creat(Context context){
        if(iData == null){
            iData = new ImplSharedXml(context);
//            iData = new ImplDataDB(context);
        }
        return iData;
    }

}
