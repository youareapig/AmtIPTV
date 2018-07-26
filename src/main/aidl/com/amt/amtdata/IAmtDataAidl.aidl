package com.amt.amtdata;

import com.amt.amtdata.IDataCallBack;
interface IAmtDataAidl {
   int putString(String key,String value);
   String getString(String key,String defValue);
   int putBoolean(String key,boolean value);
   boolean getBoolean(String key,boolean defValue);
   void registDataCallBack(IDataCallBack callback);
   void unRegistDataCallBack();
}
