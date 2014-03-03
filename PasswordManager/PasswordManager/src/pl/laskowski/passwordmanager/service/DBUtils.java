package pl.laskowski.passwordmanager.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBUtils {

	public static final String KEY_USER = "user";
	public static final String KEY_PASSWD = "passwd";
	public static final String KEY_ENTRY_ID = "_id";

	private static final String TAG = "DBUtils";
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	/**
	 * Database creation sql statement
	 */
	private static final String DATABASE_CREATE = "create table passwords (_id text primary key  not null, user text not null, passwd text not null);";

	private static final String DATABASE_NAME = "passwd.db";
	private static final String DATABASE_TABLE = "passwords";
	private static final int DATABASE_VERSION = 2;

	private final Context mCtx;

	private static DBUtils instance;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}
	}

	public static DBUtils getInstance(Context ctx) {
		if (instance == null) {
			instance = new DBUtils(ctx);
			instance.open();
		}

		if (instance.mDb != null && !instance.mDb.isOpen()) {
			instance.open();
		}
		return instance;
	}

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created, private for singleton pattern usage
	 * 
	 * @param ctx
	 *            the Context within which to work
	 */
	private DBUtils(Context ctx) {
		this.mCtx = ctx;
	}

	/**
	 * Open the database. If it cannot be opened, try to create a new instance
	 * of the database. If it cannot be created, throw an exception to signal
	 * the failure
	 * 
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public DBUtils open() throws SQLException {
		mDbHelper = new DatabaseHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	/**
	 * Create a new entry using the data provided. If the entry is successfully
	 * created return the new rowId for that note, otherwise return a -1 to
	 * indicate failure.
	 * 
	 * @param entryId
	 *            id
	 * @param user
	 *            the title of the note
	 * @param passwd
	 *            the body of the note
	 * @return rowId or -1 if failed
	 */
	public long createPasswdEntry(String entryId, String user, String passwd) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_ENTRY_ID, entryId);
		initialValues.put(KEY_USER, user);
		initialValues.put(KEY_PASSWD, passwd);

		return mDb.insert(DATABASE_TABLE, null, initialValues);
	}

	/**
	 * Delete the entry with the given rowId
	 * 
	 * @param entryName
	 *            entryName of note to delete
	 * @return true if deleted, false otherwise
	 */
	public boolean deleteEntry(String entryName) {

		return mDb.delete(DATABASE_TABLE, KEY_ENTRY_ID + "= '" + entryName
				+ "'", null) > 0;
	}

	/**
	 * Return a Cursor over the list of all entries in the database
	 * 
	 * @return Cursor over all notes
	 */
	public Cursor fetchAllEntries() {

		return mDb.query(DATABASE_TABLE, new String[] { KEY_ENTRY_ID, KEY_USER,
				KEY_PASSWD }, null, null, null, null, null);
	}

	/**
	 * Return a Cursor positioned at the entry that matches the given rowId
	 * 
	 * @param entryName
	 *            name of entry to retrieve
	 * @return Cursor positioned to matching entry, if found
	 * @throws SQLException
	 *             if entry could not be found/retrieved
	 */
	public Cursor fetchEntry(String entryName) throws SQLException {

		Cursor mCursor =

		mDb.query(true, DATABASE_TABLE, new String[] { KEY_ENTRY_ID, KEY_USER,
				KEY_PASSWD }, KEY_ENTRY_ID + "= '" + entryName + "'", null,
				null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/**
	 * Update the entry using the details provided. The entry to be updated is
	 * specified using the rowId, and it is altered to use the values passed in
	 * 
	 * @param entryName
	 *            id of entry to update
	 * @param user
	 *            value to set entry user to
	 * @param passwd
	 *            value to set entry password to
	 * @return true if the entry was successfully updated, false otherwise
	 */
	public boolean updateEntry(String entryName, String user, String passwd) {
		ContentValues args = new ContentValues();
		args.put(KEY_USER, user);
		args.put(KEY_PASSWD, passwd);

		return mDb.update(DATABASE_TABLE, args, KEY_ENTRY_ID + "= '"
				+ entryName + "'", null) > 0;
	}
}
