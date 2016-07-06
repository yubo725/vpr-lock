package com.vpr.vprlock.activity;

import org.json.JSONException;
import org.json.JSONObject;
import org.simple.eventbus.EventBus;

import net.tsz.afinal.annotation.view.ViewInject;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeakerVerifier;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechListener;
import com.vpr.vprlock.R;
import com.vpr.vprlock.VPRApplication;
import com.vpr.vprlock.utils.SPUtils;
import com.vpr.vprlock.view.MessageDialog;

/**
 * 声纹模型
 * @author yubo
 *
 */
public class ModelActivity extends BaseActivity {

	@ViewInject(id = R.id.title) TextView titleTv;
	@ViewInject(id = R.id.voiceIdTv) TextView voiceIdTv;
	@ViewInject(id = R.id.deleteBtn, click = "delete") Button deleteBtn;
	@ViewInject(id = R.id.leftbtn, click = "back") Button backBtn;

	private MessageDialog msgDialog;
	private SpeakerVerifier mVerifier = VPRApplication.getSpeakerVerifier();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_model);
		EventBus.getDefault().register(this);
		initView();
	}

	private void initView(){
		titleTv.setText("声纹模型");
		backBtn.setVisibility(View.VISIBLE);
		String voiceId = SPUtils.getInstance().getString(SPUtils.VOICE_ID, "null");
		voiceIdTv.setText(voiceId);
	}

	public void delete(View view){
		if(msgDialog == null){
			msgDialog = new MessageDialog(this, R.style.loading_dialog);
			msgDialog.setTitle("提示");
			msgDialog.setCenterMsg("确定要删除已建立的声纹模型吗？");
			msgDialog.setCancelable(false);
			msgDialog.setOnOkBtnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					//确定删除声纹模型
					msgDialog.cancel();
					// 清空参数
					mVerifier.setParameter(SpeechConstant.PARAMS, null);
					mVerifier.setParameter(SpeechConstant.ISV_PWDT, VPRApplication.getPwdType());
					mVerifier.sendRequest("del", VPRApplication.getAuthId(), listener);
				}
			});
		}
		msgDialog.showDialog();
	}

	private SpeechListener listener = new SpeechListener(){

		@Override
		public void onBufferReceived(byte[] buffer) {
			String result = new String(buffer);
			try {
				JSONObject object = new JSONObject(result);
				String cmd = object.getString("cmd");
				int ret = object.getInt("ret");

				if ("del".equals(cmd)) {
					if (ret == ErrorCode.SUCCESS) {
						SPUtils.getInstance().setString(SPUtils.VOICE_ID, "");
						EventBus.getDefault().post("", "model_build");
						finish();
						showTip("删除成功");
					} else if (ret == ErrorCode.MSP_ERROR_FAIL) {
						showTip("删除失败，模型不存在");
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onCompleted(SpeechError error) {
			if (null != error && ErrorCode.SUCCESS != error.getErrorCode()) {
				showTip("操作失败：" + error.getErrorCode());
			}
		}

		@Override
		public void onEvent(int arg0, Bundle arg1) {
		}

	};

	public void back(View view){
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
}
