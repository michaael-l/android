package pl.chiveit.android.securenotes.activities;

import pl.chiveit.android.securenotes.R;
import pl.chiveit.android.securenotes.service.CryptoService;
import pl.chiveit.android.securenotes.service.DBUtils;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity implements LoaderCallbacks<Cursor> {

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// UI references.
	private AutoCompleteTextView mPasswordView;
	private EditText mPasswordRetypeView;
	private View mProgressView;
	private View mLoginFormView;

	private boolean mNewAccnt;

	public static final String TAG = "LoginActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// if there are no entries in the DB than we need to set up new account
		mNewAccnt = DBUtils.getInstance(this).fetchAllEntries().getCount() == 0 ? true
				: false;

		// Set up the login form.
		mPasswordView = (AutoCompleteTextView) findViewById(R.id.password);
		populateAutoComplete();

		if (mNewAccnt) {
			mPasswordRetypeView = (EditText) findViewById(R.id.password_retype);
			mPasswordRetypeView
					.setOnEditorActionListener(new TextView.OnEditorActionListener() {
						@Override
						public boolean onEditorAction(TextView textView,
								int id, KeyEvent keyEvent) {
							if (id == R.id.login || id == EditorInfo.IME_NULL) {
								attemptLogin();
								return true;
							}
							return false;
						}
					});
			mPasswordRetypeView.setVisibility(View.VISIBLE);
		}

		Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
		mEmailSignInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});

		mLoginFormView = findViewById(R.id.login_form);
		mProgressView = findViewById(R.id.login_progress);
	}

	private void populateAutoComplete() {
		getLoaderManager().initLoader(0, null, this);
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		String password = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid email address.
		if (TextUtils.isEmpty(password)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}

		if (mNewAccnt) {
			String passwordRetype = mPasswordRetypeView.getText().toString();
			mPasswordRetypeView.setError(null);
			if (!(TextUtils.isEmpty(passwordRetype) || passwordRetype
					.equals(password))) {
				mPasswordRetypeView
						.setError(getString(R.string.error_pwd_not_same));
				focusView = mPasswordRetypeView;
				cancel = true;
			}
			// Check for a valid password, if the user entered one.
			if (!TextUtils.isEmpty(passwordRetype)
					&& !isPasswordValid(passwordRetype)) {
				mPasswordRetypeView
						.setError(getString(R.string.error_invalid_password));
				focusView = mPasswordRetypeView;
				cancel = true;
			}
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			showProgress(true);
			mAuthTask = new UserLoginTask(password);
			mAuthTask.execute((Void) null);
		}
	}

	private boolean isPasswordValid(String password) {
		return password.length() >= 4;
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});

			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mProgressView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mProgressView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		return new CursorLoader(this,
				// Retrieve data rows for the device user's 'profile' contact.
				Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
						ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
				ProfileQuery.PROJECTION,

				// Select only email addresses.
				ContactsContract.Contacts.Data.MIMETYPE + " = ?",
				new String[] { ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE },

				// Show primary email addresses first. Note that there won't be
				// a primary email address if the user hasn't specified one.
				ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
	}

	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

	}

	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {

	}

	private interface ProfileQuery {
		String[] PROJECTION = { ContactsContract.CommonDataKinds.Email.ADDRESS,
				ContactsContract.CommonDataKinds.Email.IS_PRIMARY, };

	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

		private final String mPassword;

		UserLoginTask(String password) {
			mPassword = password;
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			if (!CryptoService.getInstance(
					LoginActivity.this.getApplicationContext()).authenticate(
					mPassword)) {

				mPasswordView
						.setError(getText(R.string.error_wrong_user_or_passwd));
				return false;
			}

			Intent intent = new Intent(
					LoginActivity.this.getApplicationContext(),
					MainActivity.class);
			startActivity(intent);

			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				finish();
			} else {
				mPasswordRetypeView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordRetypeView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
