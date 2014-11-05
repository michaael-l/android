package pl.laskowski.android.smsforwarder;

import pl.laskowski.android.smsforwarder.fragments.PreferencesFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {

	public static final String FORWARD_TEXT = "Forwarded message from : ";

	SmsManager manager = SmsManager.getDefault();

	@Override
	public void onReceive(Context context, Intent intent) {

		if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
				PreferencesFragment.ENABLE_FORWARD_PREF_KEY, false)) {
			Bundle extras = intent.getExtras();

			Object[] pdus = (Object[]) extras.get("pdus");
			for (Object pdu : pdus) {
				SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdu);

				String origin = msg.getOriginatingAddress();
				String body = msg.getMessageBody();

				manager.sendTextMessage(
						PreferenceManager.getDefaultSharedPreferences(context).getString(
								PreferencesFragment.FORWARD_NUMBER_PREF_KEY, ""), null, FORWARD_TEXT + origin + ":"
								+ body, null, null);

			}
		}
	}
}