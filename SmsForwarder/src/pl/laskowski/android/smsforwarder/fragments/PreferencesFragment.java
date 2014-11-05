package pl.laskowski.android.smsforwarder.fragments;

import pl.laskowski.android.smsforwarder.MainActivity;
import pl.laskowski.android.smsforwarder.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class PreferencesFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	public static final String FORWARD_NUMBER_PREF_KEY = "forward_number_key";
	public static final String ENABLE_FORWARD_PREF_KEY = "enable_forward_key";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		// register a change listener
		getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

		// update to actual values if set
		EditTextPreference editTextPref = (EditTextPreference) findPreference(FORWARD_NUMBER_PREF_KEY);
		editTextPref.setSummary(getPreferenceManager().getSharedPreferences().getString(FORWARD_NUMBER_PREF_KEY, ""));
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		Preference pref = findPreference(key);
		((MainActivity) getActivity()).updatePreferenceVaue(pref);

	}

}
