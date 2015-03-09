package pl.chiveit.android.securenotes.fragment;

import pl.chiveit.android.securenotes.R;
import pl.chiveit.android.securenotes.service.CryptoService;
import pl.chiveit.android.securenotes.service.DBUtils;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class NoteEditFragment extends Fragment {
	private TextView mEntryNameText;

	private TextView mUser;

	private TextView mPasswd;

	private String mEntryName;

	private CryptoService mCrypto;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.passwd_edit, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mCrypto = CryptoService.getInstance(this.getActivity()
				.getApplicationContext());

		mEntryNameText = (TextView) getActivity().findViewById(R.id.entryName);
		mUser = (TextView) getActivity().findViewById(R.id.user);
		mPasswd = (TextView) getActivity().findViewById(R.id.passwd);

		Button confirmButton = (Button) getActivity()
				.findViewById(R.id.confirm);

		mEntryName = (savedInstanceState == null) ? null
				: (String) savedInstanceState
						.getSerializable(DBUtils.KEY_ENTRY_ID);
		if (mEntryName == null && getActivity().getIntent() != null) {
			Bundle extras = getActivity().getIntent().getExtras();
			mEntryName = extras != null ? extras
					.getString(DBUtils.KEY_ENTRY_ID) : null;
		}

		populateFields();

		confirmButton.setOnClickListener(new OnClickListener() {

			public void onClick(View view) {
				saveState();
				getFragmentManager().popBackStack();
				getActivity().setIntent(null);
				return;
			}

		});
	};

	private void populateFields() {
		if (mEntryName != null) {
			Cursor entries = DBUtils.getInstance(
					this.getActivity().getApplicationContext()).fetchEntry(
					mEntryName);

			mEntryNameText.setText(entries.getString(entries
					.getColumnIndexOrThrow(DBUtils.KEY_ENTRY_ID)));
			mEntryNameText.setEnabled(false);
			mUser.setText(mCrypto.decodeAES(entries.getString(entries
					.getColumnIndexOrThrow(DBUtils.KEY_USER))));
			mPasswd.setText(mCrypto.decodeAES(entries.getString(entries
					.getColumnIndexOrThrow(DBUtils.KEY_PASSWD))));
			DBUtils.getInstance(this.getActivity().getApplicationContext())
					.close();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// saveState();
		outState.putSerializable(DBUtils.KEY_ENTRY_ID, mEntryName);
	}

	@Override
	public void onPause() {
		super.onPause();
		// saveState();
	}

	@Override
	public void onResume() {
		super.onResume();
		populateFields();
	}

	private void saveState() {
		String entryName = mEntryNameText.getText().toString();
		String user = mCrypto.encodeAES(mUser.getText().toString());
		String passwd = mCrypto.encodeAES(mPasswd.getText().toString());

		if (mEntryName == null) {
			long id = DBUtils.getInstance(
					this.getActivity().getApplicationContext())
					.createPasswdEntry(entryName, user, passwd);
			if (id > 0) {
				// mEntryName = id;
			}
		} else {
			DBUtils.getInstance(this.getActivity().getApplicationContext())
					.updateEntry(mEntryName, user, passwd);
		}

		DBUtils.getInstance(this.getActivity().getApplicationContext()).close();
	}
}
