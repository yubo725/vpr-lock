package com.vpr.vprlock.activity;

import net.tsz.afinal.annotation.view.ViewInject;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeakerVerifier;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.VerifierListener;
import com.iflytek.cloud.VerifierResult;
import com.vpr.vprlock.R;
import com.vpr.vprlock.VPRApplication;
import com.vpr.vprlock.view.VolumeView;

/**
 * 声纹验证
 * @author yubo
 *
 */
public class VerifyActivity extends BaseActivity {

	@ViewInject(id = R.id.title) TextView titleTv;
	@ViewInject(id = R.id.hintTv) TextView hintTv;
	@ViewInject(id = R.id.pwdTv) TextView pwdTv;
	@ViewInject(id = R.id.centerTv) TextView centerTv;
	@ViewInject(id = R.id.leftVolumeView)
	VolumeView leftVolumeView;
	@ViewInject(id = R.id.rightVolumeView) VolumeView rightVolumeView;
	@ViewInject(id = R.id.result_icon) ImageView resultImage;
	@ViewInject(id = R.id.leftbtn, click = "back") Button backBtn;
	@ViewInject(id = R.id.recorderBtn, click = "record") Button recordBtn;

	private SpeakerVerifier mVerifier = VPRApplication.getSpeakerVerifier();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verify);
		initView();
	}

	private void initView(){
		titleTv.setText("声纹验证");
		backBtn.setVisibility(View.VISIBLE);
	}

	public void record(View view){
		mVerifier.setParameter(SpeechConstant.PARAMS, null);
		mVerifier.setParameter(SpeechConstant.ISV_AUDIO_PATH,
				Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/verify.pcm");
		mVerifier = SpeakerVerifier.getVerifier();
		// 设置业务类型为验证
		mVerifier.setParameter(SpeechConstant.ISV_SST, "verify");
		// 对于某些麦克风非常灵敏的机器，如nexus、samsung i9300等，建议加上以下设置对录音进行消噪处理
		mVerifier.setParameter(SpeechConstant.AUDIO_SOURCE, "" + MediaRecorder.AudioSource.VOICE_RECOGNITION);
		String pwd = mVerifier.generatePassword(8);
		mVerifier.setParameter(SpeechConstant.ISV_PWD, pwd);
		// 设置auth_id，不能设置为空
		mVerifier.setParameter(SpeechConstant.AUTH_ID, VPRApplication.getAuthId());
		mVerifier.setParameter(SpeechConstant.ISV_PWDT, VPRApplication.getPwdType());
		// 开始验证
		mVerifier.startListening(listener);
		recordBtn.setEnabled(false);

		hintTv.setVisibility(View.VISIBLE);
		hintTv.setText("正在录音");
		pwdTv.setVisibility(View.VISIBLE);
		pwdTv.setText("请匀速读出：" + pwd);

		resultImage.setVisibility(View.GONE);
		centerTv.setText(Html.fromHtml("<h1><font color='white'>声纹识别中</font></h1>"));
	}

	private VerifierListener listener = new VerifierListener() {

		@Override
		public void onVolumeChanged(int volume, byte[] data) {
			leftVolumeView.setVolume(volume);
			rightVolumeView.setVolume(volume);
		}

		@Override
		public void onResult(VerifierResult result) {

			centerTv.setText(result.source);
			recordBtn.setEnabled(true);
			hintTv.setVisibility(View.GONE);
			pwdTv.setVisibility(View.GONE);

			if (result.ret == 0) {
				// 验证通过
				resultImage.setVisibility(View.VISIBLE);
				resultImage.setImageResource(R.drawable.ic_right);
				centerTv.setText(Html.fromHtml("<h1><font color='green'>验证通过</font></h1>"));
			}
			else{
				// 验证不通过
				resultImage.setVisibility(View.VISIBLE);
				resultImage.setImageResource(R.drawable.ic_error);
				switch (result.err) {
					case VerifierResult.MSS_ERROR_IVP_GENERAL:
						centerTv.setText("内核异常");
						break;
					case VerifierResult.MSS_ERROR_IVP_TRUNCATED:
						centerTv.setText("出现截幅");
						break;
					case VerifierResult.MSS_ERROR_IVP_MUCH_NOISE:
						centerTv.setText("太多噪音");
						break;
					case VerifierResult.MSS_ERROR_IVP_UTTER_TOO_SHORT:
						centerTv.setText("录音太短");
						break;
					case VerifierResult.MSS_ERROR_IVP_TEXT_NOT_MATCH:
						centerTv.setText("验证不通过，您所读的文本有误");
						break;
					case VerifierResult.MSS_ERROR_IVP_TOO_LOW:
						centerTv.setText("音量太低");
						break;
					case VerifierResult.MSS_ERROR_IVP_NO_ENOUGH_AUDIO:
						centerTv.setText("音频长达不到自由说的要求");
						break;
					default:
						centerTv.setText(Html.fromHtml("<h1><font color='red'>验证不通过</font></h1>"));
						break;
				}
			}
		}
		// 保留方法，暂不用
		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle arg3) {

		}

		@Override
		public void onError(SpeechError error) {
			switch (error.getErrorCode()) {
				case ErrorCode.MSP_ERROR_NOT_FOUND:
					centerTv.setText("模型不存在，请先注册");
					break;

				default:
					showTip("onError Code："	+ error.getErrorCode());
					break;
			}
		}

		@Override
		public void onEndOfSpeech() {
		}

		@Override
		public void onBeginOfSpeech() {
		}
	};

	public void back(View view){
		finish();
	}

}
