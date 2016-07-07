package com.vpr.vprlock.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpr.vprlock.R;
import com.vpr.vprlock.bean.AppInfo;

public class LockAppListAdapter extends CommonAdapter<AppInfo> {

	private List<AppInfo> dataList;

	public LockAppListAdapter(Context context, List<AppInfo> mDatas,
			int itemLayoutId) {
		super(context, mDatas, itemLayoutId);
		dataList = mDatas;
	}

	@Override
	public void convert(ViewHolder holder, AppInfo item, int position) {
		ImageView imageView = holder.getView(R.id.lock_app_list_item_img);
		TextView appNameTv = holder.getView(R.id.lock_app_list_item_name_tv);
		ImageView checkboxImage = holder.getView(R.id.lock_app_list_item_checkbox);
		imageView.setImageDrawable(item.getAppIcon());
		appNameTv.setText(item.getAppName());
		if(item.isSelected()) {
			checkboxImage.setImageResource(R.drawable.ic_checkbox_checked);
		}else {
			checkboxImage.setImageResource(R.drawable.ic_checkbox_normal);
		}
	}

	public void refresh() {
		if(dataList != null && dataList.size() > 0) {
			Collections.sort(dataList, comparator);
			setData(dataList);
			notifyDataSetChanged();
		}
	}

	public static Comparator<AppInfo> comparator = new Comparator<AppInfo>() {
		@Override
		public int compare(AppInfo lhs, AppInfo rhs) {
			boolean lSelected = lhs.isSelected();
			boolean rSelected = rhs.isSelected();
			if(lSelected && !rSelected) {
				return -1;
			}else if(!lSelected && rSelected) {
				return 1;
			}
			return 0;
		}
	};

}
