package com.mymobkit.data;

import com.mymobkit.service.api.sms.Sms;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * Backend Class for the SMS database allows manipulation of the database.
 */
public class SmsTable extends DatabaseTable {

    public SmsTable(Context context) {
        super(context);
    }

    public static boolean addSms(ContentValues values) {
        long ret = dbInstance.insert(DatabaseUtils.SMS_TABLE_NAME, null, values);
        return ret != -1;
    }

    public static boolean updateSms(ContentValues values, String id) {
        int ret = dbInstance.update(DatabaseUtils.SMS_TABLE_NAME, values, "smsID=" + id + "", null);
        return ret == 1;
    }

    public static boolean deleteSms(String id) {
        int ret = dbInstance.delete(DatabaseUtils.SMS_TABLE_NAME, "smsID=" + id + "", null);
        return ret == 1;
    }

    public static boolean containsSms(String id) {
        Cursor c = dbInstanceReadOnly.query(DatabaseUtils.SMS_TABLE_NAME, new String[]{"smsID"}, "smsID=" + id + "", null, null, null, null);
        try {
            boolean ret = c.getCount() == 1;
            return ret;
        } finally {
            c.close();
        }
    }

    public static Sms getSms(String id) {
        Sms s = null;
        Cursor c = dbInstanceReadOnly.query(DatabaseUtils.SMS_TABLE_NAME, new String[]{"smsID", "phoneNumber", "name", "shortenedMessage", "answerTo", "dIntents", "sIntents", "resSIntent", "resDIntent", "date"}, "smsID=" + id + "", null, null, null,
                null);
        try {
            int rowCount = c.getCount();
            c.moveToFirst();
            if (rowCount == 1) {
                s = new Sms(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getInt(7), c.getInt(8), c.getLong(9));
            }
        } finally {
            c.close();
        }
        return s;
    }

	/*
    public static Sms[] getFullDatabase() {
		Cursor c = dbInstanceReadOnly.query(DatabaseUtils.SMS_TABLE_NAME, new String[] { "smsID", "phoneNumber", "name", "shortenedMessage", "answerTo", "dIntents", "sIntents", "resSIntent", "resDIntent", "date" }, null, null, null, null, null);
		int rowCount = c.getCount();
		c.moveToFirst();
		Sms[] res = new Sms[rowCount];
		for (int i = 0; i < rowCount; i++) {
			res[i] = new Sms(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getInt(7), c.getInt(8), c.getLong(9));
			c.moveToNext();
		}
		c.close();
		return res;
	}
	*/

    public static int getRecordCount() {
        Cursor c = dbInstanceReadOnly.query(DatabaseUtils.SMS_TABLE_NAME, new String[]{"smsID"}, null, null, null, null, null);
        try {
            c.moveToFirst();
            return c.getCount();
        } finally {
            c.close();
        }
    }

    /**
     * Deletes SMS from the Database that are older then 5 days
     */
    public static int deleteOldSms(final int days) {
        final long threshold = 1000 * 60 * 60 * 24 * days;
        long olderThan = System.currentTimeMillis() - threshold;
        return dbInstance.delete(DatabaseUtils.SMS_TABLE_NAME, "date < " + olderThan, null);
    }

    public static void deleteOldSmsByNumber(final int totalRecords) {
        int limit = getRecordCount() - totalRecords;
        if (limit <= 0)
            return;
        String sql = "DELETE FROM " + DatabaseUtils.SMS_TABLE_NAME + "  WHERE smsID IN (SELECT smsID FROM " + DatabaseUtils.SMS_TABLE_NAME + "  ORDER BY date ASC LIMIT " + limit + ")";
        dbInstance.execSQL(sql);
    }

    /**
     * @param smsID
     * @return the result if there was one, otherwise null
     */
    public static String getSentIntent(String smsID) {
        String res = null;
        Cursor c = dbInstanceReadOnly.query(DatabaseUtils.SMS_TABLE_NAME, new String[]{"sIntents"}, "smsID=" + smsID, null, null, null, null);
        try {
            if (c.moveToFirst()) {
                res = c.getString(0);
            }
        } finally {
            c.close();
        }
        return res;
    }

    public static boolean putSentIntent(String smsID, String string) {
        boolean res = false;
        if (containsSms(smsID)) {
            ContentValues value = new ContentValues();
            value.put("sIntents", string);
            int ret = (dbInstance.update(DatabaseUtils.SMS_TABLE_NAME, value, "smsID=" + smsID, null));
            if (ret == 1) {
                res = true;
            }
        }
        return res;
    }

    /**
     * @param smsId
     * @return the result if there was one, otherwise null
     */
    public static String getDelIntent(String smsId) {
        String res = null;
        Cursor c = dbInstanceReadOnly.query(DatabaseUtils.SMS_TABLE_NAME, new String[]{"dIntents"}, "smsID=" + smsId, null, null, null, null);
        try {
            if (c.moveToFirst()) {
                res = c.getString(0);
            }
        } finally {
            c.close();
        }
        return res;
    }

    public static boolean putDelIntent(String smsID, String string) {
        boolean res = false;
        if (containsSms(smsID)) {
            ContentValues value = new ContentValues();
            value.put("dIntents", string);
            int ret = (dbInstance.update(DatabaseUtils.SMS_TABLE_NAME, value, "smsID=" + smsID, null));
            if (ret == 1) {
                res = true;
            }
        }
        return res;
    }
}
