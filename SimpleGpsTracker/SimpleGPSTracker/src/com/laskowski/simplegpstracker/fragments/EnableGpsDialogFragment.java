package com.laskowski.simplegpstracker.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;

import com.laskowski.simplegpstracker.R;

/**
 * Dialog to prompt users to enable GPS on the device.
 */
public class EnableGpsDialogFragment extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.enable_gps)
				.setMessage(R.string.enable_gps_dialog)
				.setPositiveButton(R.string.enable_gps,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent settingsIntent = new Intent(
										Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(settingsIntent);
							}
						}).create();
	}
}
