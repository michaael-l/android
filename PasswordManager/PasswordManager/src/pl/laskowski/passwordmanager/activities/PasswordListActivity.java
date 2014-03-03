package pl.laskowski.passwordmanager.activities;

import java.util.ArrayList;
import java.util.List;

import pl.laskowski.passwordmanager.R;
import pl.laskowski.passwordmanager.service.DBUtils;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PasswordListActivity extends ListActivity {

	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;

	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 2;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		fillData();

		registerForContextMenu(getListView());

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.menu_insert);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:
			createPasswdEntry();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		fillData();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, PasswordEntryActivity.class);
		i.putExtra(DBUtils.KEY_ENTRY_ID,
				(String) getListAdapter().getItem(position));
		startActivityForResult(i, ACTIVITY_EDIT);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case DELETE_ID:
			AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
					.getMenuInfo();
			DBUtils.getInstance(this).deleteEntry(
					(String) getListAdapter().getItem(info.position));
			DBUtils.getInstance(this).close();
			fillData();
			return true;
		}
		return super.onContextItemSelected(item);
	}

	private void createPasswdEntry() {
		Intent i = new Intent(this, PasswordEntryActivity.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	private void fillData() {

		Cursor cur = DBUtils.getInstance(this).fetchAllEntries();

		List<String> list = new ArrayList<String>(cur.getCount());

		while (cur.moveToNext()) {
			list.add(cur.getString(0).toString());
		}

		ArrayAdapter<String> entries = new ArrayAdapter<String>(this,
				R.layout.passwd_item, list);
		setListAdapter(entries);

		DBUtils.getInstance(this).close();

	}
}
