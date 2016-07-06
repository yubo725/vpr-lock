package com.vpr.vprlock.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpr.vprlock.R;

/**
 * 显示“加载中”的对话框
 * @author yubo
 *
 */
public class LoadingDialog extends Dialog {
	private AnimationDrawable animDrawable;
	private ImageView imageView;
	private TextView loadingTv;

	public LoadingDialog(Context context, boolean cancelable,
						 OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init(context);
	}

	public LoadingDialog(Context context, int theme) {
		super(context, theme);
		init(context);
	}

	public LoadingDialog(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context){
		View view = LayoutInflater.from(context).inflate(R.layout.loading_dialog_layout, null);
		setContentView(view);
		//设置在窗口外触摸不可将窗口关闭
		setCanceledOnTouchOutside(false);
		initView();
	}

	/**设置提示的文字*/
	public void setLoadingMsg(String msg){
		loadingTv.setText(msg);
	}

	private void initView(){
		imageView = (ImageView) findViewById(R.id.loadingimageview);
		loadingTv = (TextView) findViewById(R.id.loading_msg);
		animDrawable = (AnimationDrawable) imageView.getDrawable();
		animDrawable.start();
	}

}
