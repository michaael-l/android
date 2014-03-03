package pl.laskowski.passwordmanager.activities;

import pl.laskowski.passwordmanager.R;
import pl.laskowski.passwordmanager.service.CryptoService;
import pl.laskowski.passwordmanager.service.DBUtils;
import android.accounts.AccountAuthenticatorActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AccountAuthenticatorActivity {

	private TextView mMessage;

	private String mPassword;

	private String mRetypedPassword;

	private EditText mEditPassword;

	private EditText mEditRetypePassword;

	private TextView mRetypePasswordLabel;

	private boolean mNewAccount = false;

	public static final String TAG = "LoginActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.login_activity);

		mMessage = (TextView) findViewById(R.id.message);

		mEditPassword = (EditText) findViewById(R.id.password_edit);

		// if there are no entries in the DB than we need to set up new account
		mNewAccount = DBUtils.getInstance(this).fetchAllEntries().getCount() == 0 ? true
				: false;

		DBUtils.getInstance(this).close();

		if (mNewAccount) {

			mRetypePasswordLabel = (TextView) findViewById(R.id.retype_password_label);
			mEditRetypePassword = (EditText) findViewById(R.id.retype_password_edit);

			mRetypePasswordLabel.setVisibility(View.VISIBLE);
			mEditRetypePassword.setVisibility(View.VISIBLE);

		}

	}

	public void handleLogin(View view) {

		mPassword = mEditPassword.getText().toString();
		if (mNewAccount) {
			mRetypedPassword = mEditRetypePassword.getText().toString();
		}

		if (TextUtils.isEmpty(mPassword) || mNewAccount ? TextUtils
				.isEmpty(mRetypedPassword)
				|| (!mPassword.equals(mRetypedPassword)) : false) {
			Log.i(TAG, "either login or password was empty");
			mMessage.setText(getMessage());
		} else {

			if (!CryptoService.getInstance(this).authenticate(mPassword)) {

				mMessage.setText(getText(R.string.wrong_user_or_passwd));
				return;
			}

			Intent intent = new Intent(this, PasswordListActivity.class);
			startActivity(intent);
		}

	}

	private CharSequence getMessage() {
		getString(R.string.label);

		if (TextUtils.isEmpty(mPassword)) {
			// We have an account but no password
			return getText(R.string.empty_pwd);
		}
		if (TextUtils.isEmpty(mRetypedPassword)) {
			// password retype is empty
			return getText(R.string.empty_retype_pwd);
		} else if (!mPassword.equals(mRetypedPassword)) {
			return getText(R.string.pwd_not_same);
		}

		return null;
	}

}