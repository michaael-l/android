package pl.laskowski.android.smsforwarder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class SMSForwarderService extends Service {

	// Unique Identification Number for the Notification.
	// We use it on Notification start, and to cancel it.
	private int NOTIFICATION = 1;

	private NotificationManager mNM;

	/**
	 * Class for clients to access. Because we know this service always runs in the same process as its clients, we
	 * don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		public SMSForwarderService getService() {
			return SMSForwarderService.this;
		}
	}

	@Override
	public void onCreate() {

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
				.setContentTitle(getResources().getString(R.string.notification_title))
				.setContentText(getResources().getString(R.string.notification_text))
				.setSmallIcon(pl.laskowski.android.smsforwarder.R.drawable.ic_launcher);

		// The PendingIntent to launch our activity if the user selects this
		// notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
		builder.setContentIntent(contentIntent);

		// Set the info for the views that show in the notification panel.

		// Send the notification.
		mNM.notify(NOTIFICATION, builder.getNotification());
	}

	// interface methods

}
