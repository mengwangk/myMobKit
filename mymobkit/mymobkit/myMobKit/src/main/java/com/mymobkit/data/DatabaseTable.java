package com.mymobkit.data;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

abstract class DatabaseTable {

	private static final String TAG = makeLogTag(DatabaseTable.class);

	protected static final String SEMAPHORE = "SEMAPHORE_MYMOBKIT";

	static SQLiteDatabase dbInstance;
	static SQLiteDatabase dbInstanceReadOnly;

	static DatabaseUtils helper;

	DatabaseTable(Context context) {
		if (dbInstance == null) {
			helper = new DatabaseUtils(context);
			dbInstance = helper.getWritableDatabase();
			dbInstanceReadOnly = helper.getReadableDatabase();
		}
	}

	public void cleanUp() {
		synchronized (SEMAPHORE) {
			try {
				if (dbInstance != null) {
					dbInstance.close();
					dbInstance = null;
				}
				
				if (dbInstanceReadOnly != null) {
					dbInstanceReadOnly.close();
					dbInstanceReadOnly = null;
				}
				
				if (helper != null) {
					helper.close();
					helper = null;
				}
				
			} catch (Exception ex) {
				LOGE(TAG, "[cleanUp] Error cleaning up database", ex);
			}
		}
	}

}
