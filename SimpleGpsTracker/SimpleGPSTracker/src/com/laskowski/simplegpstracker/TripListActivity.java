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
import com.laskowski.simplegpstracker.util.TripDetails;

public class TripListActivity extends ListActivity {

	public static final int TRIP_DETAILS = 0;

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

		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, TripDetailsActivity.class);
		i.putExtra(DBUtils.KEY_TRIP_ID,
				((TripDetails) getListAdapter().getItem(position)).getId());
		startActivityForResult(i, TRIP_DETAILS);

	}

	private void fillData() {

		Cursor cur = DBUtils.getInstance(this).fetchAllTrips();

		List<TripDetails> list = new ArrayList<TripDetails>(cur.getCount());

		while (cur.moveToNext()) {
			list.add(new TripDetails(cur.getString(1).toString(), cur
					.getLong(0)));
		}

		ArrayAdapter<TripDetails> entries = new ArrayAdapter<TripDetails>(this,
				R.layout.trip_item, list);
		setListAdapter(entries);

		DBUtils.getInstance(this).close();

	}
}
