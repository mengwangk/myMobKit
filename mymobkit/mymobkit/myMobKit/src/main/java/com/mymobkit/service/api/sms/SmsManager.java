package com.mymobkit.service.api.sms;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.Telephony;

import com.mymobkit.R;
import com.mymobkit.common.ValidationUtils;
import com.mymobkit.data.contact.ContactsManager;
import com.mymobkit.data.phone.Phone;
import com.mymobkit.enums.MessageType;

import java.util.ArrayList;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.LOGW;
import static com.mymobkit.common.LogUtils.makeLogTag;

public final class SmsManager {

	private static final String TAG = makeLogTag(SmsManager.class);

	private final Context context;

	private static final Uri THREADS_CONTENT_URI = Uri.parse("content://mms-sms/threadID");
	private static final Uri SMS_CONTENT_URI = Uri.parse("content://sms");
	private static final Uri SMS_INBOX_CONTENT_URI = Uri.withAppendedPath(SMS_CONTENT_URI, "inbox");
	//private static final Uri SMS_SENTBOX_CONTENT_URI = Uri.withAppendedPath(SMS_CONTENT_URI, "sent");

	private static final String COLUMNS[] = new String[] { "_id", "thread_id", "service_center", "person", "address", "body", "date", "type", "protocol", "status", "subject", "read" };
	private static final String SORT_ORDER = "date DESC";
	//private static final String SORT_ORDER_LIMIT = "date DESC ";

	//private static final int SMS_DRAFT = 3;
	//public static final int SMS_SENT = Calls.OUTGOING_TYPE;
	//public static final int SMS_INBOX = Calls.INCOMING_TYPE;
	private static final String ADDRESS = "address";
	private static final String TYPE = "type";
	private static final String BODY = "body";
	private static final String DATE = "date";
	private static final String READ = "read";

	private static final Uri URI_SENT = Uri.parse("content://sms/sent");
	private static final String[] PROJECTION_ID = new String[] { BaseColumns._ID };

	public SmsManager(Context baseContext) {
		context = baseContext;
	}

    /*
	public ArrayList<Sms> getSms(ArrayList<Phone> phones) {
		return getSms(phones, null);
	}
	*/

	public ArrayList<Sms> getSms(ArrayList<Phone> phones, String search) {
		ArrayList<Sms> res = new ArrayList<Sms>();

		for (Phone phone : phones) {
			Cursor c = context.getContentResolver().query(THREADS_CONTENT_URI.buildUpon().appendQueryParameter("recipient", phone.getCleanNumber()).build(), null, null, null, null);
			if (c != null) {
				for (boolean hasData = c.moveToFirst(); hasData; hasData = c.moveToNext()) {
					res.addAll(getSmsByThreadId(ValidationUtils.getInt(c, "_id"), search));
				}
				c.close();
			}
		}

		return res;
	}

	public ArrayList<Sms> getSmsById(long id) {
		String where = "_id = " + id;
		return getAllSms(where);
	}

	private ArrayList<Sms> getSmsByThreadId(int threadId, String search) {
		String where = "thread_id = " + threadId;
		if (search != null) {
			where += " and body LIKE '%" + ValidationUtils.encodeSql(search) + "%'";
		}
		return getAllSms(where);
	}

	//public ArrayList<Sms> getLastUnreadSms() {
	//	return getAllSms("read = 0");
	//}

	//public ArrayList<Sms> getAllSms() {
	//	return getAllSms(null);
	//}

	//public ArrayList<Sms> getSmsByTye(List<Integer> types) {
	//	String where = "type in (" + TextUtils.join(", ", types) + ")";
	//	return getAllSms(where);
	//}

    /*
	public ArrayList<Sms> getLastSms(String search) {
		return getAllSms("body LIKE '%" + ValidationUtils.encodeSql(search) + "%'");
	}
	*/

	public ArrayList<Sms> getAllSms(String where) {
		ArrayList<Sms> res = new ArrayList<Sms>();

		Cursor c = context.getContentResolver().query(SMS_CONTENT_URI, COLUMNS, where, null, SORT_ORDER);
		if (c != null) {
			for (boolean hasData = c.moveToFirst(); hasData; hasData = c.moveToNext()) {
				MessageType msgType = MessageType.get(ValidationUtils.getInt(c, "type"));
				boolean isSent = (msgType == MessageType.MESSAGE_TYPE_SENT);
				String address = ValidationUtils.getString(c, "address");
				String sender = ContactsManager.getContactName(context, address);
				String receiver = context.getString(R.string.chat_me);
				// String dateSent = Utils.getString(c, "date_sent");
				// if (!TextUtils.isEmpty(dateSent)) {

				// }
				Sms sms = new Sms(address, ValidationUtils.getString(c, "body"), ValidationUtils.getDateMilliSeconds(c, "date"), isSent ? sender : receiver);
				sms.setSender(isSent ? receiver : sender);
				sms.setServiceCenter(ValidationUtils.getString(c, "service_center"));
				sms.setThreadID(ValidationUtils.getInt(c, "thread_id"));
				sms.setID(String.valueOf(ValidationUtils.getInt(c, "_id")));
				sms.setMessageType(msgType);

				int read = ValidationUtils.getInt(c, "read");
				if (read == 0) {
					sms.setRead(false);
				} else {
					sms.setRead(true);
				}
				res.add(sms);
			}
			c.close();
		}
		return res;
	}

	/**
	 * Marks all SMS from a given phone number as read
	 * 
	 * @param id
	 *            The phone number
	 * @return true if successfully, otherwise false
	 */
	public boolean markAsRead(String id) {
		try {
			ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put("read", "1");
			int updated = cr.update(SMS_INBOX_CONTENT_URI, values, "_id=" + id, null);
			if (updated > 0)
				return true;
		} catch (Exception e) {
			LOGW(TAG, "[markAsRead] Error in mark as read", e);
			
		}
		return false;
	}

	/**
	 * Mark all unread SMS as read.
	 * 
	 * @return
	 */
	public int markAllAsRead() {
		ArrayList<Sms> allUnreadSms = getAllSms("read=0");
		int count = 0;
		for (Sms sms : allUnreadSms) {
			if (markAsRead(sms.getId())) {
				count++;
			}
		}
		return count;
	}

	/*
	public void addSmsToSentBox(String message, String phoneNumber) {
		ContentValues values = new ContentValues();
		values.put("address", phoneNumber);
		values.put("date", System.currentTimeMillis());
		values.put("body", message);
		context.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
	}

	public void addSmsToFailedBox(String message, String phoneNumber) {
		ContentValues values = new ContentValues();
		values.put("address", phoneNumber);
		values.put("date", System.currentTimeMillis());
		values.put("body", message);
		context.getContentResolver().insert(Uri.parse("content://sms/failed"), values);
	}
	*/

	public Uri addSmsSent(final String message, final String recipient) {
		Uri uri = Uri.EMPTY;
		try {
			final ContentResolver cr = context.getContentResolver();
			ContentValues values = new ContentValues();
			values.put(TYPE, Telephony.TextBasedSmsColumns.MESSAGE_TYPE_SENT);
			values.put(BODY, message);
			values.put(READ, 1);
			values.put(ADDRESS, recipient);
			values.put(DATE, System.currentTimeMillis());

			// save sms to content://sms/sent
			Cursor cursor = cr.query(SMS_CONTENT_URI, PROJECTION_ID, TYPE + " = " + Telephony.TextBasedSmsColumns.MESSAGE_TYPE_DRAFT + " AND " + ADDRESS + " = '" + recipient + "' AND " + BODY + " like '" + message.replace("'", "_") + "'", null, DATE + " DESC");
			if (cursor != null && cursor.moveToFirst()) {
				uri = URI_SENT.buildUpon().appendPath(cursor.getString(0)).build();
			} else {
				try {
					uri = cr.insert(URI_SENT, values);
				} catch (SQLiteException e) {
					LOGE(TAG, "[addSmsSent] Unable to save SMS", e);
				}
			}
			values = null;
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
			cursor = null;
		} catch (Exception e) {
			LOGW(TAG, "[addSmsSent] Error adding SMS to sent folder.", e);
		}
		return uri;
	}

    /*
	public int deleteAllSms() {
		return deleteSms(SMS_CONTENT_URI, null);
	}

	public int deleteSentSms() {
		return deleteSms(SMS_SENTBOX_CONTENT_URI, null);
	}
	*/

	public int deleteSmsById(long id) {
		return deleteSms(SMS_CONTENT_URI, "_id = " + id);
	}

	public int deleteSmsByThreadId(long threadId) {
		return deleteSms(SMS_CONTENT_URI, "thread_id = " + threadId);
	}

	/*
	public int deleteSmsByContact(ArrayList<Long> rawIds) {
		int result = -1;
		if (rawIds.size() > 0) {
			return deleteThreads(SMS_INBOX_CONTENT_URI, "person IN (" + TextUtils.join(", ", rawIds) + ")");
		}
		return result;
	}
	*/

    /*
	public int deleteSmsByNumber(String smsNumber) {
		return deleteThreads(SMS_INBOX_CONTENT_URI, "address = '" + smsNumber + "'");
	}
	*/

    /*
	private int deleteThreads(Uri deleteUri, String where) {
		int result = 0;

		ContentResolver cr = context.getContentResolver();
		Cursor c = cr.query(deleteUri, new String[] { "thread_id" }, where, null, null);
		try {
			if (c != null) {
				Set<String> threads = new HashSet<String>();

				while (c.moveToNext()) {
					threads.add(c.getString(0));
				}

				for (String thread : threads) {
					// Delete the SMS
					String uri = "content://sms/conversations/" + thread;
					result += cr.delete(Uri.parse(uri), null, null);
				}
			}
		} catch (Exception e) {
			LOGE(TAG, "[deleteThreads] Error in deleting SMS", e);
			if (result == 0) {
				result = -1;
			}
		}
		if (c != null && !c.isClosed()) {
			c.close();
		}
		return result;
	}
	*/

	private int deleteSms(Uri deleteUri, String where) {
		int result = 0;

		ContentResolver cr = context.getContentResolver();
		Cursor c = cr.query(deleteUri, new String[] { "_id" }, where, null, null);
		try {
			if (c != null) {
				while (c.moveToNext()) {
					// Delete the SMS
					String uri = "content://sms/" + c.getString(0);
					result += cr.delete(Uri.parse(uri), null, null);
				}
			}
		} catch (Exception e) {
			LOGE(TAG, "[deleteSms] Exception in deleting SMS", e);
			if (result == 0) {
				result = -1;
			}
		}
		if (c != null && !c.isClosed()) {
			c.close();
		}
		return result;
	}

    /*
	public int deleteLastSms(int number) {
		return deleteLastSms(SMS_CONTENT_URI, number);
	}

	public int deleteLastInSms(int number) {
		return deleteLastSms(SMS_INBOX_CONTENT_URI, number);
	}

	public int deleteLastOutSms(int number) {
		return deleteLastSms(SMS_SENTBOX_CONTENT_URI, number);
	}
	*/

    /*
	int deleteLastSms(Uri deleteUri, int number) {
		int result = 0;

		ContentResolver cr = context.getContentResolver();
		Cursor c = cr.query(deleteUri, new String[] { "_id" }, null, null, SORT_ORDER);
		try {
			if (c != null) {
				for (int i = 0; i < number && c.moveToNext(); ++i) {
					// Delete the SMS
					String uri = "content://sms/" + c.getString(0);
					result += cr.delete(Uri.parse(uri), null, null);
				}
			}
		} catch (Exception e) {
			LOGE(TAG, "[deleteLastSms] Exception in deleting SMS", e);
			if (result == 0) {
				result = -1;
			}
		}

		if (c != null && !c.isClosed()) {
			c.close();
		}
		return result;
	}
	*/
}