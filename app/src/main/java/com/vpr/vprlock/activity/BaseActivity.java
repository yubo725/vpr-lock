package com.vpr.vprlock.activity;

import net.tsz.afinal.FinalActivity;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.vpr.vprlock.R;
import com.vpr.vprlock.view.LoadingDialog;

public class BaseActivity extends FinalActivity {

	protected LoadingDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initProgressDialog();
		//判断当前SDK版本号，如果是4.4以上，就是支持沉浸式状态栏的
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
	}

	private void initProgressDialog(){
		loadingDialog = new LoadingDialog(this, R.style.loading_dialog);
	}

	protected void showTip(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

}