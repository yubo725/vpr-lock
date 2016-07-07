package com.vpr.vprlock.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.vpr.vprlock.R;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

/**
 * Created by yubo on 2015/7/23.
 * 加载中提示框
 */
public class LoadingDialog extends Dialog {

    @ViewInject(id = R.id.loading_dialog_img)
    ImageView loadingImg;

    @ViewInject(id = R.id.loading_dialog_msg)
    TextView loadingMsg;

    private View rootView;
    private Context context;
    private Animation animation;

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    private void init(Context context){
        this.context = context;
        rootView = LayoutInflater.from(context).inflate(R.layout.loading_dialog, null);
        setContentView(rootView);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        FinalActivity.initInjectedView(this, rootView);
        initView();
    }

    private void initView(){
        AccelerateDecelerateInterpolator lin = new AccelerateDecelerateInterpolator();
        animation = AnimationUtils.loadAnimation(context, R.anim.loading_img_rotate_anim);
        animation.setInterpolator(lin);//设置动画匀速
    }

    /**
     * 设置提示框显示的文本
     * @param msg
     */
    public void setLoadingMsg(String msg){
        loadingMsg.setText(msg);
    }

    public void showLoadingDialog(){
        if(!isShowing()) {
            try {
                loadingImg.startAnimation(animation);
                show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
