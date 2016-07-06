package com.vpr.vprlock.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class VolumeView extends View {
	
	private int width;
	private int height;
	private int maxVolume = 25;
	private int currentVolume = 0;
	private Paint paint;

	public VolumeView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public VolumeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public VolumeView(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		paint = new Paint();
		paint.setColor(Color.parseColor("#17B4EB"));
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width = getMeasuredWidth();
		height = getMeasuredHeight();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawRect(0, ((maxVolume - currentVolume) * 1.0f / maxVolume) * height, width, height, paint);
	}
	
	public void setVolume(int volume){
		currentVolume = volume > maxVolume ? maxVolume : volume;
		invalidate();
	}
	
	public void setMaxVolume(int maxVolume){
		this.maxVolume = maxVolume;
	}

}
