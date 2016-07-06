package com.vpr.vprlock.adapter;

import java.util.List;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpr.vprlock.R;
import com.vpr.vprlock.bean.AppInfo;

public class LockAppListAdapter extends CommonAdapter<AppInfo> {

	public LockAppListAdapter(Context context, List<AppInfo> mDatas,
			int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
	}

	@Override
	public void convert(ViewHolder holder, AppInfo item, int position) {
		ImageView imageView = holder.getView(R.id.lock_app_list_item_img);
		TextView appNameTv = holder.getView(R.id.lock_app_list_item_name_tv);
		imageView.setImageDrawable(item.getAppIcon());
		appNameTv.setText(item.getAppName());
	}

}
