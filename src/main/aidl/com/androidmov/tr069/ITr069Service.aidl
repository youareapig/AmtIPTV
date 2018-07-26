package com.androidmov.tr069;
interface ITr069Service {
	 boolean setValueToTr069(String key,String value);
	 String getValueFromTr069(String key);
	 void notifyMessageTotr069(String msg, String arg0, String arg1, String arg2);
}
