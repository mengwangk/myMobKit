package com.mymobkit.data;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;
import android.content.ContentValues;
import android.content.Context;

import com.mymobkit.service.api.sms.Sms;


/**
 * Middle-end helper. Adds and restores SMS to the database backend.
 * 
 */
public final class SmsHelper {

	private static final String TAG = makeLogTag(SmsHelper.class);
	
	private static SmsHelper smsHelper = null;

	public static final char TRUE_VALUE = 'T';

	public static final char FALSE_VALUE = 'F';

	/**
	 * This constructor ensures that the database is setup correctly
	 * 
	 * @param context
	 */
	private SmsHelper(Context context) {
		new SmsTable(context);
	}

	public static SmsHelper getSmsHelper(Context ctx) {
		if (smsHelper == null) {
			smsHelper = new SmsHelper(ctx);
		}
		return smsHelper;
	}

	public boolean addSms(Sms sms) {
		String smsID = sms.getId();
		ContentValues values = new ContentValues();
		values.put("smsID", smsID);
		values.put("phoneNumber", sms.getNumber());
		values.put("name", sms.getTo().replace('\'', '\"'));
		values.put("shortenedMessage", sms.getMessage().replace('\'', '\"'));
		values.put("answerTo", sms.getAnswerTo() == null ? "unknown" : sms.getAnswerTo());
		values.put("dIntents", sms.getDelIntents());
		values.put("sIntents", sms.getSentIntents());
		values.put("numParts", sms.getNumParts());
		values.put("resSIntent", sms.getSentIntentResult());
		values.put("resDIntent", sms.getDelIntentResult());
		values.put("date", sms.getCreatedDate().getTime());
		return addOrUpdate(values, smsID);
	}

	public boolean deleteSms(String id) {
		if (SmsTable.containsSms(id)) {
			return SmsTable.deleteSms(id);
		} else {
			return false;
		}
	}

	//public boolean containsSms(String id) {
	//	return SmsTable.containsSms(id);
	//}

	public Sms getSms(String id) {
		return SmsTable.getSms(id);
	}

	public void deleteOldSms(final int days) {
		SmsTable.deleteOldSms(days);
	}
	
	public void deleteOldSmsByNumber(final int toKeep) {
		SmsTable.deleteOldSmsByNumber(toKeep);
	}

	//public Sms[] getFullDatabase() {
	//	return SmsTable.getFullDatabase();
	//}

	private boolean addOrUpdate(ContentValues values, String smsID) {
		if (SmsTable.containsSms(smsID)) {
			return SmsTable.updateSms(values, smsID);
		} else {
			return SmsTable.addSms(values);
		}
	}

	public void setSentIntentTrue(final String smsID, final int partNum) {
		setSentIntentValue(smsID, partNum, TRUE_VALUE);

	}

	public void setSentIntentValue(final String smsID, final int partNum, final char value) {
		String sentIntentStr = SmsTable.getSentIntent(smsID);
		if (sentIntentStr != null) {
			char[] sentIntent = sentIntentStr.toCharArray();
			// OoB check, see issue 187
			if (partNum < sentIntent.length) {
				sentIntent[partNum] = value;
				SmsTable.putSentIntent(smsID, new String(sentIntent));
			} else {
				LOGE(TAG, "SmsHelper.setSentIntent() OutOfBounds: " + "partNum=" + partNum + " length=" + sentIntent.length + " sentIntentSTr= " + sentIntentStr);
			}
		} 
	}

	public void setDelIntentTrue(final String smsId, final int partNum) {
		setDelIntentValue(smsId, partNum, TRUE_VALUE);
	}

	public void setDelIntentValue(final String smsId, final int partNum, final char value) {
		String delIntentStr = SmsTable.getDelIntent(smsId);
		if (delIntentStr != null) {
			char[] delIntent = delIntentStr.toCharArray();
			// OoB check, see issue 208
			if (partNum < delIntent.length) {
				delIntent[partNum] = value;
				SmsTable.putDelIntent(smsId, new String(delIntent));
			} else {
				LOGE(TAG, "SmsHelper.setSentIntent() OutOfBounds: " + "partNum=" + partNum + " length=" + delIntent.length + " sentIntentSTr= " + delIntentStr);
			}
		} 
	}
}
