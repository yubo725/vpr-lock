package com.vpr.vprlock.view;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vpr.vprlock.R;

/**
 * 首页显示的三个小圆点
 * @author yubo
 *
 */
public class DotView extends LinearLayout {

	private View rootView;

	@ViewInject(id = R.id.dot1) ImageView dot1;
	@ViewInject(id = R.id.dot2) ImageView dot2;
	@ViewInject(id = R.id.dot3) ImageView dot3;

	public DotView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public DotView(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context){
		rootView = inflate(context, R.layout.dot_view, this);
		FinalActivity.initInjectedView(this, rootView);

		dot1.setBackgroundResource(R.drawable.ic_dot_white);
		dot2.setBackgroundResource(R.drawable.ic_dot_gray);
		dot3.setBackgroundResource(R.drawable.ic_dot_gray);
	}

	/**
	 * 设置当前显示哪一个dot
	 * @param pos
	 */
	public void setCurrentPos(int pos){
		switch(pos % 3){
			case 0:
				dot1.setBackgroundResource(R.drawable.ic_dot_white);
				dot2.setBackgroundResource(R.drawable.ic_dot_gray);
				dot3.setBackgroundResource(R.drawable.ic_dot_gray);
				break;
			case 1:
				dot1.setBackgroundResource(R.drawable.ic_dot_gray);
				dot2.setBackgroundResource(R.drawable.ic_dot_white);
				dot3.setBackgroundResource(R.drawable.ic_dot_gray);
				break;
			case 2:
				dot1.setBackgroundResource(R.drawable.ic_dot_gray);
				dot2.setBackgroundResource(R.drawable.ic_dot_gray);
				dot3.setBackgroundResource(R.drawable.ic_dot_white);
				break;
		}
	}

}
