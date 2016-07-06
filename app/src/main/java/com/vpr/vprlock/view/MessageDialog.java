package com.vpr.vprlock.view;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpr.vprlock.R;

/**
 * 消息对话框
 * @author yubo
 *
 */
public class MessageDialog extends Dialog {
	@ViewInject(id= R.id.message_dialog_title) TextView titleTv;
	@ViewInject(id=R.id.message_dialog_msg) TextView centerMsgTv;
	@ViewInject(id=R.id.message_dialog_linear) LinearLayout centerLinear;
	@ViewInject(id=R.id.message_dialog_ok_btn) Button okBtn;
	@ViewInject(id=R.id.message_dialog_cancel_btn, click="cancelBtnClick") Button cancelBtn;
	private View view;

	public MessageDialog(Context context, boolean cancelable,
						 OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init(context);
	}

	public MessageDialog(Context context, int theme) {
		super(context, theme);
		init(context);
	}

	public MessageDialog(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context){
		view = LayoutInflater.from(context).inflate(R.layout.message_dialog_layout, null);
		FinalActivity.initInjectedView(this, view);
	}

	/**在消息对话框中央添加自定义的布局*/
	public void addCenterView(View view){
		if(view != null){
			centerMsgTv.setVisibility(View.GONE);
			centerLinear.setVisibility(View.VISIBLE);
			centerLinear.addView(view);
		}
	}

	public void removeAllCenterViews(){
		centerLinear.removeAllViews();
	}

	public void setBtnsVisible(boolean b){
		if(!b){
			okBtn.setVisibility(View.GONE);
			cancelBtn.setVisibility(View.GONE);
		}else{
			okBtn.setVisibility(View.VISIBLE);
			cancelBtn.setVisibility(View.VISIBLE);
		}
	}

	/**设置对话框的标题*/
	public void setTitle(String title){
		titleTv.setText(title);
	}

	public void setNeedCancelBtn(boolean need){
		if(need){
			cancelBtn.setVisibility(View.VISIBLE);
		}else{
			cancelBtn.setVisibility(View.GONE);
		}
	}

	/**设置消息对话框中央显示的信息*/
	public void setCenterMsg(String msg){
		centerMsgTv.setText(msg);
	}

	public void setOnOkBtnClickListener(View.OnClickListener l){
		okBtn.setOnClickListener(l);
	}

	/**显示对话框*/
	public void showDialog(){
		setContentView(view);
		show();
	}

	//取消
	public void cancelBtnClick(View view){
		dismiss();
	}

}
