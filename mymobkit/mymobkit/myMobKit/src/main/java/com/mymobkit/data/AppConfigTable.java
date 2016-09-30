package com.mymobkit.data;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;

import com.mymobkit.data.IDbContentProvider.Field;
import com.mymobkit.model.ConfigParam;

class AppConfigTable extends DatabaseTable {

	private static final String TAG = makeLogTag(AppConfigTable.class);

	public AppConfigTable(Context context) {
		super(context);
	}

	public static ConfigParam getAppConfig(final String name, final String module) {
		ConfigParam config = new ConfigParam();
		synchronized (SEMAPHORE) {
			Cursor c = null;
			try {
				c = dbInstanceReadOnly.query(DatabaseUtils.APPCONFIG_TABLE_NAME, DatabaseUtils.APPCONFIG_TABLE_COLUMNS, Field.NAME.getId() + "='" + name + "' and " + Field.MODULE.getId() + "='" + module + "'", null, null, null, null);
				int numRows = c.getCount();
				c.moveToFirst();
				if (numRows > 0) {
					config.setId(c.getString(0));
					config.setName(c.getString(1));
					config.setValue(c.getString(2));
					config.setModule(c.getString(3));
					config.setDescription(c.getString(4));
					config.setConfigurable(c.getString(5));
					config.setDateCreated(c.getString(6));
					config.setDateModified(c.getString(7));
				}
				c.close();
				c = null;
			} catch (SQLException e) {
				LOGE(TAG, e.getMessage(), e);
			} finally {
				if (c != null && !c.isClosed()) {
					c.close();
					c = null;
				}
			}
			return config;
		}
	}

	public static boolean updateConfigValue(final String name, final String module, final String value) {
		ContentValues values = new ContentValues();
		values.put(Field.VALUE.getId(), value);
		int row = dbInstance.update(DatabaseUtils.APPCONFIG_TABLE_NAME, values, Field.NAME.getId() + "='" + name + "' and " + Field.MODULE.getId() + "='" + module + "'", null);
		return (row > 0 ? true : false);

	}
}
