package com.amt.amtdata.dataI;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import com.amt.amtdata.AmtDataManager;
import com.amt.amtdata.IPTVData;
import com.amt.jsinterface.Iptv2EPG;
import com.amt.utils.DeviceInfo;

public class STBContentProvider extends ContentProvider{

	public static final String TAG = "STBContentProvider";

	public static final String AUTHORITY = "stbconfig";

	/**获取认证数据。包含业务账号、usertoken、和EPGDomain*/
	public static final int ITEM_AUTH_CONFIG = 2;
	/**获取业务账号*/
	public static final int ITEM_AUTH_USERNAME = 4;
	/**获取usertoken*/
	public static final int ITEM_AUTH_USERTOKEN = 5;
	/**获取EPGDomain*/
	public static final int ITEM_AUTH_EPGSERVER = 6;
	public static final int ITEM_STBID = 9;

	private static UriMatcher uriMatcher;
	static{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY,"authentication", ITEM_AUTH_CONFIG);
		uriMatcher.addURI(AUTHORITY,"authentication/username", ITEM_AUTH_USERNAME);
		uriMatcher.addURI(AUTHORITY,"authentication/epg_server", ITEM_AUTH_EPGSERVER);
		uriMatcher.addURI(AUTHORITY,"authentication/serverURL", ITEM_AUTH_EPGSERVER);
		uriMatcher.addURI(AUTHORITY,"authentication/user_token", ITEM_AUTH_USERTOKEN);
		uriMatcher.addURI(AUTHORITY,"authentication/usertoken", ITEM_AUTH_USERTOKEN);
		uriMatcher.addURI(AUTHORITY,"authentication/userSTBID", ITEM_STBID);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public boolean onCreate() {
		Log.i(TAG,"onCreate");
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Log.i(TAG,"query > uri : "+uri.toString());
		int queryCode = uriMatcher.match(uri);
		MatrixCursor c = new MatrixCursor(new String[]{"name","value"});
		c.moveToFirst();
		switch (queryCode) {
			case ITEM_AUTH_CONFIG://获取认证数据。包含业务账号、usertoken、和EPGDomain
				c.addRow(new String[] { "username", getusername() });
				c.addRow(new String[] { "user_token", getuser_token() });
				c.addRow(new String[] { "epg_server", get_epg_server() });
				break;
			case ITEM_AUTH_USERNAME://获取业务账号
				c.addRow(new String[] { "username", getusername()});
				break;
			case ITEM_AUTH_USERTOKEN://获取usertoken
				c.addRow(new String[] { "user_token", getuser_token()});
				break;
			case ITEM_AUTH_EPGSERVER://获取EPGDomain
				c.addRow(new String[] { "epg_server", get_epg_server()});
				break;
			case ITEM_STBID:
				c.addRow(new String[]{"userSTBID", DeviceInfo.STBID});
				break;
		}
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		return 0;
	}

	private String getuser_token() {
		String value = Iptv2EPG.getIptv2EPG().authInfo.CTCGetConfig("UserToken");
		return value;
	}

	private String getusername() {
		String value = AmtDataManager.getString(IPTVData.IPTV_Account,"");
		return value;
	}

	private String get_epg_server() {
		String value =AmtDataManager.getString(IPTVData.IPTV_EPGDomain,"");
		return value;
	}

}
