package com.vpr.vprlock.utils;

import java.util.Random;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CommonUtils {

	private static Random random;

	static {
		random = new Random();
	}

	/**
	 * 生成一个随机的authId用于声纹识别，由字母和数字组成
	 * @return
	 */
	public static String createAuthId(){
		StringBuffer sb = new StringBuffer();
		int i;
		for(i = 0; i < 5; i++){
			char c = getRandomChar();
			sb.append(c);
		}
		for(i = 0; i < 3; i++){
			sb.append(random.nextInt(10));
		}
		return sb.toString();
	}

	/**
	 * 获取一个随机小写字母
	 * @return
	 */
	private static char getRandomChar(){
		int n = random.nextInt(26) + 97;
		return (char) n;
	}

	/**
	 * 判断是否有网络连接
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context){
		ConnectivityManager cm=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo=cm.getActiveNetworkInfo();
		if(netInfo != null && netInfo.isAvailable()){
			return true;
		}
		return false;
	}

}
