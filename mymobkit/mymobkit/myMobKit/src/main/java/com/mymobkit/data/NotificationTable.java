package com.mymobkit.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.mymobkit.model.NotificationMsg;

import java.util.ArrayList;
import java.util.List;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

public class NotificationTable extends DatabaseTable {

    private static final String TAG = makeLogTag(NotificationTable.class);

    public NotificationTable(Context context) {
        super(context);
    }

    public static List<NotificationMsg> getAll() {
        Cursor c = null;
        try {
            c = dbInstanceReadOnly.query(DatabaseUtils.GCM_MESSAGE_TABLE_NAME,
                    DatabaseUtils.GCM_MESSAGE_TABLE_COLUMNS,
                    null, null, null, null,
                    IDbContentProvider.Field.TIMESTAMP.getId() + " DESC");
            int rowCount = c.getCount();
            c.moveToFirst();
            List<NotificationMsg> result = new ArrayList<NotificationMsg>(rowCount);
            for (int i = 0; i < rowCount; i++) {
                result.add(new NotificationMsg(c.getString(0), c.getString(1), c.getString(2), c.getLong(3)));
                c.moveToNext();
            }
            c.close();
            return result;
        } catch (Exception ex) {
            LOGE(TAG, "[getAll] Error retrieving messages", ex);
        } finally {
            if (c != null && !c.isClosed()) {
                c.close();
                c = null;
            }
        }
        return new ArrayList<NotificationMsg>();
    }

    public static boolean insert(final ContentValues values) {
        try {
            long ret = dbInstance.insert(DatabaseUtils.GCM_MESSAGE_TABLE_NAME, null, values);
            return ret != -1;
        } catch (Exception ex) {
            LOGE(TAG, "[insert] Unable to insert message", ex);
        }
        return false;
    }

    public static int purge() {
        try {
            // http://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html#delete(java.lang.String, java.lang.String, java.lang.String[])
            // To remove all rows and get a count pass "1" as the whereClause.
            return dbInstance.delete(DatabaseUtils.GCM_MESSAGE_TABLE_NAME, "1", null);
        } catch (Exception ex) {
            LOGE(TAG, "[purge] Unable to purge table", ex);
        }
        return -1;
    }
}
