package com.vpr.vprlock.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences的封装
 * @author yubo
 *
 */
public class SPUtils {
	private static final String SP_NAME = "Config";
	private static SPUtils spUtils;
	private static SharedPreferences sp;

	public static final String AUTH_ID = "auth_id";
	public static final String VOICE_ID = "voice_id";

	private SPUtils(Context context){
		if(sp == null){
			sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		}
	}

	public static void init(Context context){
		if(spUtils == null){
			spUtils = new SPUtils(context);
		}
	}

	public static SPUtils getInstance(){
		if(spUtils == null){
			throw new IllegalStateException("please call init before use SPUtils.");
		}
		return spUtils;
	}

	public void setBoolean(String key, boolean value){
		sp.edit().putBoolean(key, value).commit();
	}

	public boolean getBoolean(String key, boolean defValue){
		return sp.getBoolean(key, defValue);
	}

	public void setString(String key, String value){
		sp.edit().putString(key, value).commit();
	}

	public String getString(String key, String defValue){
		return sp.getString(key, defValue);
	}

}
