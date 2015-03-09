package pl.chiveit.android.securenotes.fragment;

import java.util.ArrayList;
import java.util.List;

import pl.chiveit.android.securenotes.R;
import pl.chiveit.android.securenotes.activities.MainActivity;
import pl.chiveit.android.securenotes.service.DBUtils;
import android.app.ListFragment;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NoteListFragment extends ListFragment {

	private static final int DELETE_ID = Menu.FIRST;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState == null) {
			registerForContextMenu(getListView());
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		fillData();
		return inflater.inflate(R.layout.fragment_main, container, false);
	};

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.add(0, DELETE_ID, 0, R.string.menu_delete)
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						switch (item.getItemId()) {
						case DELETE_ID:
							AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
									.getMenuInfo();
							DBUtils.getInstance(
									NoteListFragment.this.getActivity())
									.deleteEntry(
											(String) getListAdapter().getItem(
													info.position));
							DBUtils.getInstance(
									NoteListFragment.this.getActivity())
									.close();
							fillData();
							return true;
						}
						return false;
					}
				});
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Intent i = new Intent(this.getActivity(), MainActivity.class);
		i.putExtra(DBUtils.KEY_ENTRY_ID,
				(String) getListAdapter().getItem(position));

		getActivity().setIntent(i);

		getFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.animator.card_flip_right_in,
						R.animator.card_flip_right_out,
						R.animator.card_flip_left_in,
						R.animator.card_flip_left_out)
				.replace(R.id.container, new NoteEditFragment())
				.addToBackStack(null).commit();
	}

	private void fillData() {

		Cursor cur = DBUtils.getInstance(this.getActivity()).fetchAllEntries();

		List<String> list = new ArrayList<String>(cur.getCount());

		while (cur.moveToNext()) {
			list.add(cur.getString(0).toString());
		}

		ArrayAdapter<String> entries = new ArrayAdapter<String>(
				this.getActivity(), R.layout.passwd_item, list);
		setListAdapter(entries);

		DBUtils.getInstance(this.getActivity()).close();

	}
}
