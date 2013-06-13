package com.laskowski.simplegpstracker;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.laskowski.simplegpstracker.db.DBUtils;

public class TripDetailsActivity extends FragmentActivity {

	private TextView mTimeElapsed;
	private TextView mTotalDistance;
	private TextView mAvgSpeed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.trip_item_details);

		mTimeElapsed = (TextView) findViewById(R.id.time);
		mTotalDistance = (TextView) findViewById(R.id.totaldist);
		mAvgSpeed = (TextView) findViewById(R.id.avgspeed);

		fetchDetails(savedInstanceState);

	}

	private void fetchDetails(Bundle savedInstanceState) {
		Long id = (savedInstanceState == null) ? null
				: (Long) savedInstanceState
						.getSerializable(DBUtils.KEY_TRIP_ID);
		if (id == null) {
			Bundle extras = getIntent().getExtras();
			id = extras != null ? extras.getLong(DBUtils.KEY_TRIP_ID) : null;

			Cursor cur = DBUtils.getInstance(this).fetchTripEntry(id);

			if (cur.getCount() != 0) {
				//TODO fetch coordinates and fill in the view
			}

		}
	}
}
