package com.mymobkit.common;

import android.content.Context;
import android.widget.Toast;

/**
 * <p>
 * Class for show or not Toast notifications
 * </p>
 * 
 * <p>
 * Two levels: - Debug mode (for develop process) - Release mode (for release application)
 * </p>
 * 
 */
public final class ToastUtils {

	public static void toastLong(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	public static void toastLong(Context context, int message) {
		Toast.makeText(context, context.getText(message), Toast.LENGTH_LONG).show();
	}

	public static void toastShort(Context context, int message) {
		Toast.makeText(context, context.getText(message), Toast.LENGTH_SHORT).show();
	}

	public static void toastShort(Context context, CharSequence message) {
		Toast.makeText(context, message.toString(), Toast.LENGTH_SHORT).show();
	}
}
