package com.mymobkit.data.phone;

import java.util.ArrayList;

import com.mymobkit.common.ValidationUtils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

public class PhoneManager {

	private final Context context;

	public PhoneManager(Context baseContext) {
		context = baseContext;
	}

	/** Dial a phone number */
	public Boolean Dial(String number, boolean makeTheCall) {
		try {
			Intent intent = new Intent(makeTheCall ? Intent.ACTION_CALL : Intent.ACTION_VIEW, Uri.parse("tel:" + number));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public ArrayList<Call> getPhoneLogs() {
		ArrayList<Call> res = new ArrayList<Call>();

		ContentResolver resolver = context.getContentResolver();

		String[] projection = new String[] { CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DURATION, CallLog.Calls.DATE };
		String sortOrder = CallLog.Calls.DATE + " ASC";

		Cursor c = resolver.query(CallLog.Calls.CONTENT_URI, projection, null, null, sortOrder);

		if (c != null) {
			for (boolean hasData = c.moveToFirst(); hasData; hasData = c.moveToNext()) {
				Call call = new Call();
				call.phoneNumber = ValidationUtils.getString(c, CallLog.Calls.NUMBER);
				if (call.phoneNumber.equals("-1") || call.phoneNumber.equals("-2")) {
					call.phoneNumber = null;
				}
				call.duration = ValidationUtils.getLong(c, CallLog.Calls.DURATION);
				call.date = ValidationUtils.getDateMilliSeconds(c, CallLog.Calls.DATE);
				call.type = ValidationUtils.getInt(c, CallLog.Calls.TYPE);

				res.add(call);
			}
			c.close();
		}
		return res;
	}
}
