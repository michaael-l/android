package com.laskowski.simplegpstracker;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

public abstract class BaseActivity extends FragmentActivity {

	protected TextView mTotalDistance;
	protected TextView mAvgSpeed;
	protected TextView mTimeElapsed;

	protected LinearLayout mFragmentLayout;
	protected SupportMapFragment mMapFragment;

	protected boolean mIsBound = false;

	protected GoogleMap mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	public void showHideMap(boolean visible) {

		if (visible) {
			mFragmentLayout.setVisibility(View.VISIBLE);
		} else {
			mFragmentLayout.setVisibility(View.INVISIBLE);
		}

	}

	public GoogleMap getmMap() {
		return mMap;
	}

	public void setmMap(GoogleMap mMap) {
		this.mMap = mMap;
	}

	public LinearLayout getmFragmentLayout() {
		return mFragmentLayout;
	}

	public void setmFragmentLayout(LinearLayout mFragmentLayout) {
		this.mFragmentLayout = mFragmentLayout;
	}

	public boolean ismIsBound() {
		return mIsBound;
	}

	public void setmIsBound(boolean mIsBound) {
		this.mIsBound = mIsBound;
	}

}
