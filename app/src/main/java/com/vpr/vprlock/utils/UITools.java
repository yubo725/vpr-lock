package com.vpr.vprlock.utils;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.Toast;

import com.vpr.vprlock.R;
import com.vpr.vprlock.view.LoadingDialog;

public class UITools {
	private static LoadingDialog loadingDialog;
	
	public static void showLoadingDialog(Context context, String msg){
		try {
			loadingDialog = new LoadingDialog(context, R.style.loading_dialog);
			if(!TextUtils.isEmpty(msg)){
				loadingDialog.setLoadingMsg(msg);
			}
			loadingDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void dismissLoadingDialog(){
		if(loadingDialog != null && loadingDialog.isShowing()){
			loadingDialog.dismiss();
		}
	}
	
	public static void showToast(Context context, String msg){
		Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

}
