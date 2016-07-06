package com.vpr.vprlock.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.annotation.view.ViewInject;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vpr.vprlock.R;
import com.vpr.vprlock.adapter.LockAppListAdapter;
import com.vpr.vprlock.bean.AppInfo;

/**
 * 加密app
 *
 * @author yubo
 *
 */
public class LockAppActivity extends BaseActivity {

	@ViewInject(id = R.id.title) TextView titleTv;
	@ViewInject(id = R.id.leftbtn, click = "back") Button backBtn;
	@ViewInject(id = R.id.list_view) ListView listView;

	private LockAppListAdapter listAdapter;
	private MyHandler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_app);

		handler = new MyHandler(this);
		titleTv.setText("加密应用");
		backBtn.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		pd.show();
		new Thread(new Runnable() {

			@Override
			public void run() {
				getUserApps();
			}
		}).start();
	}

	private class MyHandler extends Handler {

		private WeakReference<Activity> actRef;

		public MyHandler(Activity act) {
			actRef = new WeakReference<Activity>(act);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			pd.dismiss();
			if(actRef.get() != null) {
				List<AppInfo> appList = (List<AppInfo>) msg.obj;
				showUserAppsList(appList);
			}
		}

	}

	/**
	 * 获取用户安装的app信息
	 */
	private void getUserApps() {
		List<AppInfo> appList = new ArrayList<AppInfo>(); // 用来存储获取的应用信息数据

		List<PackageInfo> packages = getPackageManager()
				.getInstalledPackages(0);

		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			AppInfo tmpInfo = new AppInfo();
			tmpInfo.setAppName(packageInfo.applicationInfo.loadLabel(
					getPackageManager()).toString());
			tmpInfo.setAppIcon(packageInfo.applicationInfo
					.loadIcon(getPackageManager()));
			tmpInfo.setVersionCode(packageInfo.versionCode);
			tmpInfo.setVersionName(packageInfo.versionName);
			tmpInfo.setPackageName(packageInfo.packageName);
			// 如果非系统应用，则添加至appList
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
					&& !"com.example.vprdemo".equals(packageInfo.packageName)) {
				appList.add(tmpInfo);
			}
		}
		Message msg = handler.obtainMessage();
		msg.obj = appList;
		handler.sendMessage(msg);
	}

	private void showUserAppsList(List<AppInfo> list) {
		if(list.size() == 0) {
			Toast.makeText(this, "用户没有安装APP", Toast.LENGTH_SHORT).show();
			return ;
		}
		listAdapter = new LockAppListAdapter(this, list, R.layout.lock_app_list_item);
		listView.setAdapter(listAdapter);
	}

	public void back(View view){
		finish();
	}

}
