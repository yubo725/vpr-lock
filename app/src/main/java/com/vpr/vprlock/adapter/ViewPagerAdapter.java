package com.vpr.vprlock.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vpr.vprlock.fragment.ModelFragment;
import com.vpr.vprlock.fragment.TrainFragment;
import com.vpr.vprlock.fragment.VerifyFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
	
	private Fragment trainFragment;
	private Fragment modelFragment;
	private Fragment verifyFragment;

	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
		trainFragment = new TrainFragment();
		modelFragment = new ModelFragment();
		verifyFragment = new VerifyFragment();
	}

	@Override
	public Fragment getItem(int arg0) {
		Fragment f = null;
		switch(arg0 % 3){
		case 0:
//			f = trainFragment;
			f = new TrainFragment();
			break;
		case 1:
//			f = modelFragment;
			f = new ModelFragment();
			break;
		case 2:
//			f = verifyFragment; 
			f = new VerifyFragment();
			break;
		}
		return f;
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

}
