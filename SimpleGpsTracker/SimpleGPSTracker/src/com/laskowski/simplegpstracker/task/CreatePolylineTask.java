package com.laskowski.simplegpstracker.task;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.view.Display;
import android.view.WindowManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.laskowski.simplegpstracker.BaseActivity;
import com.laskowski.simplegpstracker.util.PolyLineLatLngBoundsTuple;

public class CreatePolylineTask extends
		AsyncTask<LatLng, Void, PolyLineLatLngBoundsTuple> {

	private final WeakReference<BaseActivity> activity;
	private final int screenHeight;
	private final int screenWidth;

	public CreatePolylineTask(BaseActivity activity) {
		super();
		this.activity = new WeakReference<BaseActivity>(activity);
		WindowManager wm = (WindowManager) activity
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		this.screenHeight = size.x;
		this.screenWidth = size.y;
	}

	@Override
	protected PolyLineLatLngBoundsTuple doInBackground(LatLng... params) {

		PolyLineLatLngBoundsTuple result = new PolyLineLatLngBoundsTuple();
		PolylineOptions polyline = new PolylineOptions();
		LatLngBounds.Builder builder = new LatLngBounds.Builder();

		for (LatLng param : params) {
			builder = builder.include(param);
			polyline.add(param);
		}

		result.setBounds(builder.build());
		result.setOptions(polyline);
		return result;
	}

	@Override
	protected void onPostExecute(PolyLineLatLngBoundsTuple result) {
		if (activity != null) {
			if (result != null && result.getOptions().getPoints().size() != 0) {
				activity.get().showHideMap(true);
				activity.get().getmMap().addPolyline(result.getOptions());
				activity.get()
						.getmMap()
						.moveCamera(
								CameraUpdateFactory.newLatLngBounds(
										result.getBounds(), screenWidth,
										(int) (screenHeight * 0.4), 20));
			}
		}
	}

}
