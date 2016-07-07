package com.vpr.vprlock.activity;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.vpr.vprlock.R;
import com.vpr.vprlock.adapter.ViewPagerAdapter;
import com.vpr.vprlock.service.LockService;
import com.vpr.vprlock.utils.CommonUtils;
import com.vpr.vprlock.utils.SPUtils;
import com.vpr.vprlock.view.DotView;
import com.vpr.vprlock.view.MessageDialog;

public class MainActivity extends FragmentActivity {

	public static final String AUTH_ID = CommonUtils.createAuthId();

	@ViewInject(id = R.id.title) TextView titleTv;
	@ViewInject(id = R.id.rightbtn) Button rightBtn;
	@ViewInject(id = R.id.viewpager) ViewPager viewPager;
	@ViewInject(id = R.id.dotview) DotView dotView;
	@ViewInject(id = R.id.modelHintTv) TextView modelHintTv;
	@ViewInject(id = R.id.btn_train, click = "btnClick") Button trainBtn;
	@ViewInject(id = R.id.btn_model, click = "btnClick") Button modelBtn;
	@ViewInject(id = R.id.btn_verify, click = "btnClick") Button verifyBtn;
	@ViewInject(id = R.id.btn_lock, click = "btnClick") Button lockBtn;

	private ViewPagerAdapter viewPagerAdapter;
	private MessageDialog msgDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		startService(new Intent(this, LockService.class));

		//判断当前SDK版本号，如果是4.4以上，就是支持沉浸式状态栏的
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}

		FinalActivity.initInjectedView(this);
		EventBus.getDefault().register(this);

		initView();
	}

	private void initView(){
		titleTv.setText("声纹识别应用锁");
		rightBtn.setText("关于");
		rightBtn.setVisibility(View.VISIBLE);
		rightBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(msgDialog == null){
					msgDialog = new MessageDialog(MainActivity.this, R.style.loading_dialog);
					msgDialog.setTitle("软件信息");
					msgDialog.setCenterMsg("本软件的声纹识别技术使用了科大讯飞的声纹识别引擎，采用8位数字做声纹识别密码，声纹识别的错误接受率和错误拒绝率未具体验证，问题反馈邮箱：yubo@listome.com");
					msgDialog.setBtnsVisible(false);
				}
				msgDialog.showDialog();
			}
		});
		viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(viewPagerAdapter);
		viewPager.setCurrentItem(Integer.MAX_VALUE / 6 * 3);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				dotView.setCurrentPos(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		if(isModelExist()){
			modelHintTv.setVisibility(View.VISIBLE);
		}else{
			modelHintTv.setVisibility(View.GONE);
		}
	}

	public void btnClick(View view){
		switch(view.getId()){
			case R.id.btn_train://声纹训练
				if(isModelExist()){
					Toast.makeText(this, "声纹模型已建立，请删除后再训练", Toast.LENGTH_SHORT).show();
				}else{
					startActivity(new Intent(this, TrainActivity.class));
				}
				break;
			case R.id.btn_model://声纹模型
				if(isModelExist()){
					startActivity(new Intent(this, ModelActivity.class));
				}else{
					Toast.makeText(this, "声纹模型不存在，请先做声纹训练", Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.btn_verify://声纹验证
				if(!isModelExist()){
					Toast.makeText(this, "声纹模型不存在，请先做声纹训练", Toast.LENGTH_SHORT).show();
				}else{
					startActivity(new Intent(this, VerifyActivity.class));
				}
				break;
			case R.id.btn_lock://加密应用
				startActivity(new Intent(this, LockAppActivity.class));
				break;
		}
	}

	@Subscriber(mode = ThreadMode.MAIN, tag = "model_build")
	public void modelBuild(String voiceId){
		String id = SPUtils.getInstance().getString(SPUtils.VOICE_ID, null);
		if(TextUtils.isEmpty(id)){
			modelHintTv.setVisibility(View.GONE);
		}else{
			modelHintTv.setVisibility(View.VISIBLE);
		}
	}

	private boolean isModelExist(){
		String voiceId = SPUtils.getInstance().getString(SPUtils.VOICE_ID, null);
		if(TextUtils.isEmpty(voiceId)){
			return false;
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

}
