package com.vpr.vprlock.fragment;

import net.tsz.afinal.FinalActivity;

import com.vpr.vprlock.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 声纹训练Fragment
 * @author yubo
 *
 */
public class TrainFragment extends Fragment {

	private View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_train, null);
		FinalActivity.initInjectedView(this, rootView);
		return rootView;
	}

}
