package com.mymobkit.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.mymobkit.common.ValidationUtils;
import com.mymobkit.model.Mms;

/**
 * MMS database table
 */
public class MmsTable extends DatabaseTable {

    public MmsTable(Context context) {
        super(context);
    }

    public static boolean addMms(ContentValues values) {
        long ret = dbInstance.insert(DatabaseUtils.MMS_TABLE_NAME, null, values);
        return ret != -1;
    }

    public static boolean updateMms(ContentValues values, String id) {
        int ret = dbInstance.update(DatabaseUtils.MMS_TABLE_NAME, values, "mmsID=" + id + "", null);
        return ret == 1;
    }

    public static boolean deleteMms(String id) {
        int ret = dbInstance.delete(DatabaseUtils.MMS_TABLE_NAME, "mmsID=" + id + "", null);
        return ret == 1;
    }

    public static boolean containsMms(String id) {
        Cursor c = dbInstanceReadOnly.query(DatabaseUtils.MMS_TABLE_NAME, new String[]{"mmsID"}, "mmsID=" + id + "", null, null, null, null);
        boolean ret = c.getCount() == 1;
        c.close();
        return ret;
    }

    public static Mms getMms(String id) {
        Cursor c = dbInstanceReadOnly.query(DatabaseUtils.MMS_TABLE_NAME, new String[]{
                        "mmsID", "_to", "cc", "bcc",
                        "subject", "body", "delivery", "read", "delivered", "isRead",
                        "date"}, "mmsID=" + id + "", null, null, null,
                null);
        int rowCount = c.getCount();
        c.moveToFirst();
        Mms mms = null;
        if (rowCount == 1) {
            mms = new Mms(c.getString(0), c.getString(1), c.getString(4));
            mms.setCc(ValidationUtils.getString(c.getString(2)));
            mms.setBcc(ValidationUtils.getString(c.getString(3)));
            mms.setBody(ValidationUtils.getString(c.getString(5)));
            mms.setDeliveryReport(ValidationUtils.getInt(c, "delivery") == 1 ? true : false);
            mms.setReadReport(ValidationUtils.getInt(c, "read") == 1 ? true : false);
            mms.setIsDelivered(ValidationUtils.getInt(c, "delivered") == 1 ? true : false);
            mms.setIsRead(ValidationUtils.getInt(c, "isRead") == 1 ? true : false);
            mms.setDate(ValidationUtils.getDateMilliSeconds(c, "date"));
        }
        c.close();
        return mms;
    }

    /**
     * Deletes MMS from the Database that are older then 5 days
     *
     */
    public static int deleteOldMms(final int days) {
        final long threshold = 1000 * 60 * 60 * 24 * days;
        long olderThan = System.currentTimeMillis() - threshold;
        return dbInstance.delete(DatabaseUtils.MMS_TABLE_NAME, "date < " + olderThan, null);
    }

    public static void deleteOldMmsByNumber(final int totalRecords) {
        int limit = getRecordCount() - totalRecords;
        if (limit <= 0)
            return;
        String sql = "DELETE FROM " + DatabaseUtils.MMS_TABLE_NAME + "  WHERE mmsID IN (SELECT mmsID FROM " + DatabaseUtils.MMS_TABLE_NAME + "  ORDER BY date ASC LIMIT " + limit + ")";
        dbInstance.execSQL(sql);
    }

    public static int getRecordCount() {
        Cursor c = dbInstanceReadOnly.query(DatabaseUtils.MMS_TABLE_NAME, new String[]{"mmsID"}, null, null, null, null, null);
        c.moveToFirst();
        int rowCount = c.getCount();
        c.close();
        return rowCount;
    }
}
