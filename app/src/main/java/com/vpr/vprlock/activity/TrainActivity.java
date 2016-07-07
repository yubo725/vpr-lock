package com.vpr.vprlock.activity;

import java.util.ArrayList;
import java.util.List;

import org.simple.eventbus.EventBus;

import net.tsz.afinal.annotation.view.ViewInject;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeakerVerifier;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechListener;
import com.iflytek.cloud.VerifierListener;
import com.iflytek.cloud.VerifierResult;
import com.vpr.vprlock.R;
import com.vpr.vprlock.VPRApplication;
import com.vpr.vprlock.utils.SPUtils;
import com.vpr.vprlock.view.VolumeView;

/**
 * 声纹训练
 * @author yubo
 *
 */
public class TrainActivity extends BaseActivity {

	private SpeakerVerifier mVerifier = VPRApplication.getSpeakerVerifier();
	private List<String> pwdList = new ArrayList<String>();
	private String numPwd = "";

	@ViewInject(id = R.id.title) TextView titleTv;
	@ViewInject(id = R.id.centerTv) TextView centerTv;
	@ViewInject(id = R.id.leftbtn, click = "back") Button backBtn;
	@ViewInject(id = R.id.recorderBtn, click = "record") Button recordBtn;
	@ViewInject(id = R.id.leftVolumeView)
	VolumeView leftVolumeView;
	@ViewInject(id = R.id.rightVolumeView) VolumeView rightVolumeView;
	@ViewInject(id = R.id.timesTv) TextView timesTv;
	@ViewInject(id = R.id.pwdNowTv) TextView pwdNowTv;
	@ViewInject(id = R.id.msgTv) TextView msgTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_train);
		EventBus.getDefault().register(this);
		initView();
		getPassword();
	}

	private void initView(){
		titleTv.setText("声纹训练");
		backBtn.setVisibility(View.VISIBLE);
	}

	/**
	 * 获取训练用的密码串，需要读出这些密码串用于建立模型
	 */
	private void getPassword(){
		mVerifier.setParameter(SpeechConstant.PARAMS, null);
		mVerifier.setParameter(SpeechConstant.ISV_PWDT, "3");
		mVerifier.getPasswordList(new SpeechListener() {

			@Override
			public void onEvent(int arg0, Bundle arg1) {
			}

			@Override
			public void onCompleted(SpeechError error) {
				if (null != error && ErrorCode.SUCCESS != error.getErrorCode()) {
					showTip("获取失败：" + error.getErrorCode());
				}
			}

			@Override
			public void onBufferReceived(byte[] arg0) {
				String result = new String(arg0);
				if(!TextUtils.isEmpty(result)){
					JSONObject obj = JSON.parseObject(result);
					if(obj.containsKey("num_pwd")){
						JSONArray arr = obj.getJSONArray("num_pwd");
						if(arr != null && arr.size() > 0){
							String s = null;
							for(int i = 0; i < arr.size(); i++){
								s = arr.get(i).toString();
								pwdList.add(s);
							}
							showPwd();
						}else{
							showTip("没有获取到密码，无法继续声纹训练");
						}
					}else{
						showTip("没有获取到密码，无法继续声纹训练");
					}
				}else{
					showTip("没有获取到密码，无法继续声纹训练");
				}
			}
		});
	}

	/**
	 * 显示获取到的密码，以“-”连接5条密码串
	 */
	private void showPwd(){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < pwdList.size(); i++){
			String s = pwdList.get(i);
			sb.append(s);
			if(i != pwdList.size() - 1){
				sb.append("\n");
			}
		}
		Log.e("yubo", "train password: \n" + sb.toString());
		String pwd = sb.toString();
		centerTv.setText(pwd);
	}

	//开始录音按钮
	public void record(View view){
		mVerifier.setParameter(SpeechConstant.PARAMS, null);
		mVerifier.setParameter(SpeechConstant.ISV_AUDIO_PATH,
				Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/test.pcm");
		// 对于某些麦克风非常灵敏的机器，如nexus、samsung i9300等，建议加上以下设置对录音进行消噪处理
		mVerifier.setParameter(SpeechConstant.AUDIO_SOURCE, "" + MediaRecorder.AudioSource.VOICE_RECOGNITION);

		mVerifier.setParameter(SpeechConstant.ISV_PWD, getNumPwd());
		timesTv.setText("正在录音，第1遍");
		timesTv.setVisibility(View.VISIBLE);
		pwdNowTv.setText("请匀速读出：" + pwdList.get(0));
		pwdNowTv.setVisibility(View.VISIBLE);

		// 设置auth_id，不能设置为空
		mVerifier.setParameter(SpeechConstant.AUTH_ID, VPRApplication.getAuthId());
		// 设置业务类型为注册
		mVerifier.setParameter(SpeechConstant.ISV_SST, "train");
		// 设置声纹密码类型
		mVerifier.setParameter(SpeechConstant.ISV_PWDT, VPRApplication.getPwdType());
		// 开始注册
		mVerifier.startListening(mRegisterListener);

		recordBtn.setEnabled(false);
	}

	private VerifierListener mRegisterListener = new VerifierListener() {

		@Override
		public void onBeginOfSpeech() {
		}

		@Override
		public void onEndOfSpeech() {
		}

		@Override
		public void onError(SpeechError arg0) {
			Toast.makeText(TrainActivity.this, arg0.getErrorDescription() + arg0.getErrorCode(), Toast.LENGTH_LONG).show();
		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
		}

		@Override
		public void onResult(VerifierResult result) {
			if (result.ret == ErrorCode.SUCCESS) {
				switch (result.err) {
					case VerifierResult.MSS_ERROR_IVP_GENERAL:
						msgTv.setText("内核异常");
						break;
					case VerifierResult.MSS_ERROR_IVP_EXTRA_RGN_SOPPORT:
						msgTv.setText("训练达到最大次数");
						break;
					case VerifierResult.MSS_ERROR_IVP_TRUNCATED:
						msgTv.setText("出现截幅");
						break;
					case VerifierResult.MSS_ERROR_IVP_MUCH_NOISE:
						msgTv.setText("太多噪音");
						break;
					case VerifierResult.MSS_ERROR_IVP_UTTER_TOO_SHORT:
						msgTv.setText("录音太短");
						break;
					case VerifierResult.MSS_ERROR_IVP_TEXT_NOT_MATCH:
						msgTv.setText("训练失败，您所读的文本不一致");
						break;
					case VerifierResult.MSS_ERROR_IVP_TOO_LOW:
						msgTv.setText("音量太低");
						break;
					case VerifierResult.MSS_ERROR_IVP_NO_ENOUGH_AUDIO:
						msgTv.setText("音频长达不到自由说的要求");
					default:
						msgTv.setText("");
						break;
				}

				if (result.suc == result.rgn) {
					msgTv.setText("注册成功");
					//注册成功，得到声纹id
					String voiceId = result.vid;
					Toast.makeText(TrainActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
					EventBus.getDefault().post(voiceId, "model_build");
					SPUtils.getInstance().setString(SPUtils.VOICE_ID, voiceId);
					finish();
				} else {
					int nowTimes = result.suc + 1;
//					int leftTimes = result.rgn - nowTimes;

					timesTv.setText("正在录音，第" + nowTimes + "遍");
					pwdNowTv.setText("请匀速读出：" + pwdList.get(nowTimes - 1));
				}

			}else {
				msgTv.setText("注册失败，请重新开始");
				recordBtn.setEnabled(true);
			}
		}

		@Override
		public void onVolumeChanged(int arg0, byte[] data) {
			leftVolumeView.setVolume(arg0);
			rightVolumeView.setVolume(arg0);
		}

	};

	/**
	 * 获取用"-"连接起来的密码
	 * @return
	 */
	private String getNumPwd(){
		if(!TextUtils.isEmpty(numPwd)){
			return numPwd;
		}
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < pwdList.size(); i++){
			sb.append(pwdList.get(i));
			if(i != pwdList.size() - 1){
				sb.append("-");
			}
		}
		numPwd = sb.toString();
		return numPwd;
	}

	public void back(View view){
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

}
