package com.laskowski.simplegpstracker;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.laskowski.simplegpstracker.db.DBUtils;

public class TripListActivity extends ListActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.trip_list_activity);

		fillData();

		registerForContextMenu(getListView());

	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
/*		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, PasswordEntryActivity.class);
		i.putExtra(DBUtils.KEY_ENTRY_ID,
				(String) getListAdapter().getItem(position));
		startActivityForResult(i, ACTIVITY_EDIT);*/
	}

	private void fillData() {

		Cursor cur = DBUtils.getInstance(this).fetchAllTrips();

		List<String> list = new ArrayList<String>(cur.getCount());

		while (cur.moveToNext()) {
			list.add(cur.getString(1).toString());
		}

		ArrayAdapter<String> entries = new ArrayAdapter<String>(this,
				R.layout.trip_item, list);
		setListAdapter(entries);

		DBUtils.getInstance(this).close();

	}
}
