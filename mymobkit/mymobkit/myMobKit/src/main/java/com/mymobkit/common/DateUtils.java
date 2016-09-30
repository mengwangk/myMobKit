package com.mymobkit.common;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.text.format.DateFormat;

public final class DateUtils {

	private static final String TAG = makeLogTag(DateUtils.class);

	private static final String TIME_FORMAT_12_HOUR = "h:mm a";

	private static final String TIME_FORMAT_24_HOUR = "H:mm";

	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss Z";

	/**
	 * Format date into more human readable format.
	 * 
	 * @param date - The date to be formatted.
	 * @return String
	 */
	public static String formatDate(String dateFormat, String date, String toFormat) {

		String formatted = "";

		java.text.DateFormat formatter = new SimpleDateFormat(dateFormat);
		try {
			Date dateStr = formatter.parse(date);
			formatted = formatter.format(dateStr);
			Date formatDate = formatter.parse(formatted);
			formatter = new SimpleDateFormat(toFormat);
			formatted = formatter.format(formatDate);

		} catch (ParseException e) {
			LOGE(TAG, "ParseException", e);
		}
		return formatted;
	}

	/**
	 * Format an Unix timestamp to a string suitable for display to the user
	 * according to their system settings (12 or 24 hour time).
	 * 
	 * @param context - The context of the calling activity.
	 * @param timestamp - The human unfriendly timestamp.
	 * @return String
	 */
	public static String formatTimestamp(Context context, long timestamp) {
		final boolean is24Hr = DateFormat.is24HourFormat(context);

		SimpleDateFormat mSDF = new SimpleDateFormat();
		if (is24Hr) {
			mSDF.applyLocalizedPattern(TIME_FORMAT_24_HOUR);
		} else {
			mSDF.applyLocalizedPattern(TIME_FORMAT_12_HOUR);
		}
		return mSDF.format(new Date(timestamp));
	}

	public static String formatDateTime(long milliseconds, String dateTimeFormat) {
		final Date date = new Date(milliseconds);
		try {
			if (date != null) {
				SimpleDateFormat submitFormat = new SimpleDateFormat(dateTimeFormat);
				return submitFormat.format(date);
			}
		} catch (IllegalArgumentException e) {
			LOGE(TAG, "IllegalArgumentException", e);
		}
		return null;
	}
	
	public static String getCurrentDateString(){
		return formatDateTime(System.currentTimeMillis(), DEFAULT_DATE_FORMAT);
	}

}
