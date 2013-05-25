package com.laskowski.simplegpstracker.service;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.laskowski.simplegpstracker.MainActivity;
import com.laskowski.simplegpstracker.R;
import com.laskowski.simplegpstracker.util.SpeedDistanceTuple;

/**
 * class that performs GPS calculation in background
 * 
 * @author laskowsm
 * 
 */
public class GpsTrackService extends Service {

	private NotificationManager mNM;

	// holds all locations updates
	private List<Location> mLocations;
	private LocationManager mLocationManager;
	private Integer mTotalDistanceInMeters = 0;

	// Unique Identification Number for the Notification.
	// We use it on Notification start, and to cancel it.
	private int NOTIFICATION = 1;
	private static final long GPS_UPDATE_INTERVAL = 1000;

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		public GpsTrackService getService() {
			return GpsTrackService.this;
		}
	}

	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// Get a reference to the LocationManager object.
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		mLocations = new ArrayList<Location>();

		requestUpdatesFromProvider(LocationManager.GPS_PROVIDER,
				R.string.not_support_gps);

		// Display a notification about us starting. We put an icon in the
		// status bar.
		showNotification();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Cancel the persistent notification.
		mNM.cancel(NOTIFICATION);
		mLocationManager.removeUpdates(listener);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	// This is the object that receives interactions from clients. See
	// RemoteService for a more complete example.
	private final IBinder mBinder = new LocalBinder();

	/**
	 * Show a notification while this service is running.
	 */
	@SuppressWarnings("deprecation")
	private void showNotification() {

		// Set the icon, scrolling text and timestamp
		Notification.Builder builder = new Notification.Builder(this)
				.setContentTitle(
						getResources().getString(R.string.notification_title))
				.setContentText(
						getResources().getString(R.string.notification_text))
				.setSmallIcon(
						com.laskowski.simplegpstracker.R.drawable.running_icon);

		// The PendingIntent to launch our activity if the user selects this
		// notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, MainActivity.class), 0);
		builder.setContentIntent(contentIntent);

		// Set the info for the views that show in the notification panel.

		// Send the notification.
		mNM.notify(NOTIFICATION, builder.getNotification());
	}

	/**
	 * Method to register location updates with a desired location provider. If
	 * the requested provider is not available on the device, the app displays a
	 * Toast with a message referenced by a resource id.
	 * 
	 * @param provider
	 *            Name of the requested provider.
	 * @param errorResId
	 *            Resource id for the string message to be displayed if the
	 *            provider does not exist on the device.
	 * @return A previously returned {@link android.location.Location} from the
	 *         requested provider, if exists.
	 */
	private Location requestUpdatesFromProvider(final String provider,
			final int errorResId) {
		Location location = null;
		if (mLocationManager.isProviderEnabled(provider)) {
			mLocationManager.requestLocationUpdates(provider,
					GPS_UPDATE_INTERVAL, 5, listener);
			location = mLocationManager.getLastKnownLocation(provider);
		} else {
			Toast.makeText(this, errorResId, Toast.LENGTH_LONG).show();
		}
		return location;
	}

	private final LocationListener listener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			(new CalculateDistanceTask())
					.execute(new Location[] {
							location,
							mLocations.size() != 0 ? mLocations.get(mLocations
									.size() - 1) : location });
			mLocations.add(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	private class CalculateDistanceTask extends AsyncTask<Location, Void, Void> {

		public CalculateDistanceTask() {
			super();
		}

		@Override
		protected Void doInBackground(Location... params) {

			Location curLoc = params[0];
			Location prevLoc = params[1];

			float[] results = new float[3];

			Location.distanceBetween(prevLoc.getLatitude(),
					prevLoc.getLongitude(), curLoc.getLatitude(),
					curLoc.getLongitude(), results);

			if (results[0] > 0) {
				mTotalDistanceInMeters += (int) results[0];
			}

			return null;
		}
	}

	// interface methods

	/**
	 * 
	 * @return total distance travelled so far, in kilometers
	 */
	public SpeedDistanceTuple getUIData() {

		if (mLocations.size() != 0) {
			SpeedDistanceTuple sdt = new SpeedDistanceTuple();

			Location l = mLocations.get(mLocations.size() - 1);
			sdt.setSpeed(l.hasSpeed() ? Float.valueOf(l.getSpeed()).toString()
					: "");
			sdt.setDistance(String.format("%.3g%n",
					mTotalDistanceInMeters * 0.001));
			return sdt;
		}
		return null;
	}

	/**
	 * 
	 * @return average speed for this trip
	 */
	@SuppressLint("DefaultLocale")
	public String getAverageSpeed(int totalTime) {

		return String.format("%.3g%n", ((float) mTotalDistanceInMeters / 1000)
				/ (Float.valueOf(totalTime) / (60 * 60)));
	}

	public List<Location> getLocations() {
		return mLocations;
	}

}
