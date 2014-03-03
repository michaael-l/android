package pl.laskowski.passwordmanager.activities;

import pl.laskowski.passwordmanager.R;
import pl.laskowski.passwordmanager.service.CryptoService;
import pl.laskowski.passwordmanager.service.DBUtils;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PasswordEntryActivity extends Activity {

	private EditText mEntryNameText;

	private EditText mUser;

	private EditText mPasswd;

	private String mEntryName;

	private CryptoService mCrypto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		mCrypto = CryptoService.getInstance(this);

		setContentView(R.layout.passwd_edit);
		setTitle(R.string.edit_passwd);

		mEntryNameText = (EditText) findViewById(R.id.entryName);
		mUser = (EditText) findViewById(R.id.user);
		mPasswd = (EditText) findViewById(R.id.passwd);

		Button confirmButton = (Button) findViewById(R.id.confirm);

		mEntryName = (savedInstanceState == null) ? null
				: (String) savedInstanceState
						.getSerializable(DBUtils.KEY_ENTRY_ID);
		if (mEntryName == null) {
			Bundle extras = getIntent().getExtras();
			mEntryName = extras != null ? extras
					.getString(DBUtils.KEY_ENTRY_ID) : null;
		}

		populateFields();

		confirmButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				saveState();
				setResult(RESULT_OK);
				finish();
			}

		});
	}

	private void populateFields() {
		if (mEntryName != null) {
			Cursor entries = DBUtils.getInstance(this).fetchEntry(mEntryName);

			mEntryNameText.setText(entries.getString(entries
					.getColumnIndexOrThrow(DBUtils.KEY_ENTRY_ID)));
			mEntryNameText.setEnabled(false);
			mUser.setText(mCrypto.decodeAES(entries.getString(entries
					.getColumnIndexOrThrow(DBUtils.KEY_USER))));
			mPasswd.setText(mCrypto.decodeAES(entries.getString(entries
					.getColumnIndexOrThrow(DBUtils.KEY_PASSWD))));
			DBUtils.getInstance(this).close();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// saveState();
		outState.putSerializable(DBUtils.KEY_ENTRY_ID, mEntryName);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}

	private void saveState() {
		String entryName = mEntryNameText.getText().toString();
		String user = mCrypto.encodeAES(mUser.getText().toString());
		String passwd = mCrypto.encodeAES(mPasswd.getText().toString());

		if (mEntryName == null) {
			long id = DBUtils.getInstance(this).createPasswdEntry(entryName,
					user, passwd);
			if (id > 0) {
				// mEntryName = id;
			}
		} else {
			DBUtils.getInstance(this).updateEntry(mEntryName, user, passwd);
		}

		DBUtils.getInstance(this).close();
	}
}
