package com.vpr.vprlock;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeakerVerifier;
import com.iflytek.cloud.SpeechUtility;
import com.vpr.vprlock.utils.CommonUtils;
import com.vpr.vprlock.utils.CrashHandler;
import com.vpr.vprlock.utils.SPUtils;

import android.app.Application;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class VPRApplication extends Application {

	private static SpeakerVerifier mVerifier;
	private static String pwdType = "3";//代表数字密码类型

	@Override
	public void onCreate() {
		super.onCreate();
		//初始化声纹识别引擎
		SpeechUtility.createUtility(this, "appid=577cb19e");

		//初始化SharedPreferences
		SPUtils.init(this);

		//初始化程序崩溃处理器
		CrashHandler.getInstance().init(this);

		Log.e("yubo", "准备初始化声纹引擎...");
		mVerifier = SpeakerVerifier.createVerifier(this, new InitListener() {

			@Override
			public void onInit(int errorCode) {
				if (ErrorCode.SUCCESS == errorCode) {
					Log.i("yubo", "引擎初始化成功");
					showTip("引擎初始化成功");
				} else {
					Log.i("yubo", "引擎初始化失败，code = " + errorCode);
					showTip("引擎初始化失败，错误码：" + errorCode);
				}
			}
		});


	}

	public static SpeakerVerifier getSpeakerVerifier(){
		return mVerifier;
	}

	public static String getAuthId(){
		String authId = SPUtils.getInstance().getString(SPUtils.AUTH_ID, null);
		if(TextUtils.isEmpty(authId)){
			authId = CommonUtils.createAuthId();
			SPUtils.getInstance().setString(SPUtils.AUTH_ID, authId);
		}
		return authId;
	}

	public static String getPwdType(){
		return pwdType;
	}

	private void showTip(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

}
