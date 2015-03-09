package pl.chiveit.android.securenotes.activities;

import pl.chiveit.android.securenotes.R;
import pl.chiveit.android.securenotes.fragment.ImpExpFragment;
import pl.chiveit.android.securenotes.fragment.NoteEditFragment;
import pl.chiveit.android.securenotes.fragment.NoteListFragment;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	private static final int INSERT_ID = Menu.FIRST;
	private static final int EXPORT_ID = Menu.FIRST + 1;

	public boolean mEditView = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new NoteListFragment()).commit();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, INSERT_ID, 0, R.string.menu_insert);
		menu.add(0, EXPORT_ID, 0, R.string.menu_export_import);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case INSERT_ID:
			getFragmentManager()
					.beginTransaction()
					.setCustomAnimations(R.animator.card_flip_right_in,
							R.animator.card_flip_left_out,
							R.animator.card_flip_left_in,
							R.animator.card_flip_right_out)
					.replace(R.id.container, new NoteEditFragment())
					.addToBackStack(null).commit();
			return true;
		case EXPORT_ID:
			getFragmentManager()
					.beginTransaction()
					.setCustomAnimations(R.animator.card_flip_right_in,
							R.animator.card_flip_left_out,
							R.animator.card_flip_left_in,
							R.animator.card_flip_right_out)
					.replace(R.id.container, new ImpExpFragment())
					.addToBackStack(null).commit();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

}
