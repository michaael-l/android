package pl.chiveit.android.securenotes.fragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import pl.chiveit.android.securenotes.R;
import pl.chiveit.android.securenotes.service.DBUtils;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class ImpExpFragment extends Fragment {

	public static final String FILENAME = "notes.exp";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.imp_exp, container, false);
	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		((Button) getActivity().findViewById(R.id.impButton))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						importEntries();
					}
				});

		((Button) getActivity().findViewById(R.id.expButton))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						try {
							exportEntries();
						} catch (IOException e) {
							Toast.makeText(getActivity(),
									"unable to create file: " + e.getMessage(),
									Toast.LENGTH_SHORT).show();
							;
						}
					}
				});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {

			BufferedReader br = null;
			try {
				br = new BufferedReader(new InputStreamReader(getActivity()
						.getContentResolver().openInputStream(data.getData())));
				DBUtils db = DBUtils.getInstance(getActivity());
				String line = null;
				while ((line = br.readLine()) != null) {
					String[] cols = line.split(",");
					db.createPasswdEntry(cols[0], cols[1], cols[2]);
				}
			} catch (FileNotFoundException e) {
				Toast.makeText(getActivity(),
						"unable to open import file: " + e.getMessage(),
						Toast.LENGTH_LONG).show();
				Log.e("", "unable to open import file: " + e.getMessage());
			} catch (IOException e) {
				Toast.makeText(getActivity(),
						"unable to open import file: " + e.getMessage(),
						Toast.LENGTH_LONG).show();
				Log.e("", "unable to open import file: " + e.getMessage());
			} finally {
				try {
					if (br != null) {
						br.close();
					}
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	private void importEntries() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("*/*");
		Intent i = Intent.createChooser(intent, "File");
		startActivityForResult(i, 0);
	}

	private void exportEntries() throws IOException {
		saveEntriesForExport();
		Intent intent = new Intent(android.content.Intent.ACTION_SEND);
		intent.setType("text/plain");
		Uri uri = Uri.fromFile(getActivity().getFileStreamPath(FILENAME));
		intent.putExtra(android.content.Intent.EXTRA_STREAM, uri);
		intent.putExtra(Intent.EXTRA_SUBJECT, "secure notes export");
		startActivity(Intent.createChooser(intent, "Send eMail.."));
	}

	@SuppressWarnings("deprecation")
	private void saveEntriesForExport() throws IOException {
		Cursor cur = DBUtils.getInstance(this.getActivity()).fetchAllEntries();
		BufferedWriter bw = null;

		try {
			bw = new BufferedWriter(new FileWriter(getActivity()
					.openFileOutput(FILENAME, Context.MODE_WORLD_READABLE)
					.getFD()));

			while (cur.moveToNext()) {
				bw.write(cur.getString(0).toString().trim() + ","
						+ cur.getString(1).toString().trim() + ","
						+ cur.getString(2).toString().trim());
				bw.newLine();
			}

		} finally {
			bw.close();
		}

	}
}
