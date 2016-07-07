package com.vpr.vprlock.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.annotation.view.ViewInject;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vpr.vprlock.R;
import com.vpr.vprlock.adapter.LockAppListAdapter;
import com.vpr.vprlock.bean.AppInfo;
import com.vpr.vprlock.utils.DimenUtils;

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
	@ViewInject(id = R.id.count_textview) TextView selectedCountTv;

	private LockAppListAdapter listAdapter;
	private MyHandler handler;

	private FinalDb finalDb;
	private Map<String, String> selectedAppMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_app);
		finalDb = FinalDb.create(this);
		selectedAppMap = new HashMap<>();
		init();
		loadSelectedAppsFromDB();
	}

	private void init() {
		handler = new MyHandler(this);
		titleTv.setText("加密应用");
		backBtn.setVisibility(View.VISIBLE);
		loadingDialog.setLoadingMsg("正在加载应用数据");
	}

	//从数据库加载已被选择加密的app
	private void loadSelectedAppsFromDB() {
		List<AppInfo> selectedList = finalDb.findAllByWhere(AppInfo.class, "selected=1");
		if(selectedList != null && selectedList.size() > 0) {
			for(AppInfo item : selectedList) {
				selectedAppMap.put(item.getPackageName(), item.getAppName());
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		loadingDialog.showLoadingDialog();
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
			loadingDialog.dismiss();
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

		List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);

		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			AppInfo tmpInfo = new AppInfo();
			tmpInfo.setAppName(packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
			tmpInfo.setAppIcon(packageInfo.applicationInfo.loadIcon(getPackageManager()));
			tmpInfo.setVersionCode(packageInfo.versionCode);
			tmpInfo.setVersionName(packageInfo.versionName);
			tmpInfo.setPackageName(packageInfo.packageName);
			// 如果非系统应用，则添加至appList
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
					&& !"com.example.vprdemo".equals(packageInfo.packageName)) {
				appList.add(tmpInfo);
			}
		}
		//跟数据库中的数据做对比，标记哪些应用被选择
		if(appList.size() > 0) {
			for(AppInfo item : appList) {
				if(selectedAppMap.containsKey(item.getPackageName())) {
					item.setSelected(true);
				}
			}
			Collections.sort(appList, LockAppListAdapter.comparator);
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

		TextView headerView = new TextView(this);
		headerView.setBackgroundColor(Color.TRANSPARENT);
		headerView.setHeight(DimenUtils.dip2px(this, 35));
		listView.addHeaderView(headerView);

		listAdapter = new LockAppListAdapter(this, list, R.layout.lock_app_list_item);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(onItemClickListener);

		refreshCountTextView();
	}

	//刷新显示选择个数的TextView
	private void refreshCountTextView() {
		List<AppInfo> list = listAdapter.getData();
		int count = 0;
		if(list != null && list.size() > 0) {
			for(AppInfo item : list) {
				if(item.isSelected()) {
					count++;
				}
			}
		}
		selectedCountTv.setText(String.format("已选择加密%d款应用", count));
	}

	private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			AppInfo appInfo = (AppInfo) parent.getAdapter().getItem(position);
			Log.e("yubo", "selected app name: " + appInfo.getAppName());
			boolean selected = appInfo.isSelected();
			appInfo.setSelected(!selected);
			listAdapter.refresh();
			refreshCountTextView();
		}
	};

	//将选择的应用存进数据库
	private void saveSelectedAppsToDB() {
		List<AppInfo> list = listAdapter.getData();
		if(list != null && list.size() > 0) {
			for(AppInfo item : list) {
				if(exsitInDB(item.getPackageName())) {
					finalDb.update(item);
				}else {
					finalDb.save(item);
				}
			}
		}
	}

	private boolean exsitInDB(String packageName) {
		List<AppInfo> list = finalDb.findAllByWhere(AppInfo.class, "packageName=\"" + packageName + "\"");
		if(list != null && list.size() > 0) {
			return true;
		}
		return false;
	}

	public void back(View view){
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//退出当前界面时，保存数据到数据库
		saveSelectedAppsToDB();
	}
}
