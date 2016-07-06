package com.vpr.vprlock.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 所有ListView通用的Adapter类
 * @author yubo
 *
 * @param <T> ListView中展示的数据对应的JavaBean
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
	protected LayoutInflater mInflater;
	protected Context mContext;
	protected List<T> mDatas;
	protected final int mItemLayoutId;

	public CommonAdapter(Context context, List<T> mDatas, int itemLayoutId) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
		this.mDatas = mDatas;
		this.mItemLayoutId = itemLayoutId;
	}

	public void setData(List<T> data){
		this.mDatas = data;
	}

	public List<T> getData(){
		return mDatas;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public T getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder = getViewHolder(position, convertView,
				parent);
		convert(viewHolder, getItem(position), position);
		return viewHolder.getConvertView();

	}

	/**
	 * 由子类去复写的转换方法，主要用于获取Item布局中的控件，以及为控件赋值等操作
	 * @param holder ViewHolder工具类
	 * @param item ListView每个Item中的JavaBean
	 */
	public abstract void convert(ViewHolder holder, T item, int position);

	private ViewHolder getViewHolder(int position, View convertView,
			ViewGroup parent) {
		return ViewHolder.get(mContext, convertView, parent, mItemLayoutId,
				position);
	}

}
