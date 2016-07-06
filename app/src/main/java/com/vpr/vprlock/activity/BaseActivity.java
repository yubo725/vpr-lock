package com.vpr.vprlock.activity;

import net.tsz.afinal.FinalActivity;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

public class BaseActivity extends FinalActivity {

	protected ProgressDialog pd;

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
		pd = new ProgressDialog(this);
		pd.setCancelable(false);
		pd.setTitle("提示");
		pd.setMessage("请稍等...");
	}

	protected void showTip(String msg){
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

}