package pl.laskowski.android.smsforwarder;

import pl.laskowski.android.smsforwarder.fragments.PreferencesFragment;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static final String PHONE_NUMBER_REGEXP = "(\\d{1,4})?\\d{9}";
	public static final String BAD_NUMBER_TEXT = "Please provide a valid phone number";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().replace(R.id.container, new PreferencesFragment()).commit();
			
		}
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the menu; this adds items to the action bar
	 * if it is present. getMenuInflater().inflate(R.menu.main, menu); return true; }
	 * 
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { // Handle action bar item clicks here. The action
	 * bar will // automatically handle clicks on the Home/Up button, so long // as you specify a parent activity in
	 * AndroidManifest.xml. int id = item.getItemId(); if (id == R.id.action_settings) {
	 * getFragmentManager().beginTransaction().replace(R.id.container, new PreferencesFragment()).commit();
	 * 
	 * return true; } return super.onOptionsItemSelected(item); }
	 */

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);
			return rootView;
		}
	}

	public void updatePreferenceVaue(Preference pref) {

		if (pref instanceof EditTextPreference) {
			EditTextPreference editPref = (EditTextPreference) pref;

			if (editPref.getTitle().equals(getResources().getString(R.string.preferences_forward_number_tile))) {

				if (editPref.getText().matches(PHONE_NUMBER_REGEXP)) {
					pref.setSummary(editPref.getText());
				}

				else {
					Toast.makeText(this, BAD_NUMBER_TEXT, Toast.LENGTH_LONG).show();
				}
			}

		}
	}
}
