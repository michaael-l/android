package com.laskowski.simplegpstracker.task;

import java.lang.ref.WeakReference;
import java.util.List;

import android.location.Location;
import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.laskowski.simplegpstracker.MainActivity;
import com.laskowski.simplegpstracker.util.PolyLineLatLngBoundsTuple;

public class CreatePolylineTask extends
		AsyncTask<Void, Void, PolyLineLatLngBoundsTuple> {

	private final WeakReference<MainActivity> activity;

	public CreatePolylineTask(MainActivity activity) {
		super();
		this.activity = new WeakReference<MainActivity>(activity);
	}

	@Override
	protected PolyLineLatLngBoundsTuple doInBackground(Void... params) {
		if (activity != null) {
			if (activity.get().ismIsBound()) {

				PolyLineLatLngBoundsTuple result = new PolyLineLatLngBoundsTuple();
				PolylineOptions polyline = new PolylineOptions();
				LatLngBounds.Builder builder = new LatLngBounds.Builder();
				LatLng ll = null;
				List<Location> locations = activity.get().getmBoundService()
						.getLocations();
				for (Location loc : locations) {
					ll = new LatLng(loc.getLatitude(), loc.getLongitude());
					polyline.add(ll);
					builder = builder.include(ll);

				}
				result.setBounds(builder.build());
				result.setOptions(polyline);
				return result;
			}
		}
		return null;
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
										result.getBounds(), activity.get()
												.getmFragmentLayout()
												.getWidth(), activity.get()
												.getmFragmentLayout()
												.getHeight(), 20));
			}
		}
	}

}
