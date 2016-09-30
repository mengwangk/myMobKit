package com.mymobkit.common;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;

import com.mymobkit.app.AppConfig;
import com.mymobkit.model.SimInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.mymobkit.common.LogUtils.LOGD;
import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Dual SIM SMS sending.
 * <p/>
 * Adapted from http://stackoverflow.com/questions/27351936/how-to-send-a-sms-using-smsmanager-in-dual-sim-mobile/30677542#30677542
 */
public final class SmsUtils {

	private static final String TAG = makeLogTag(SmsUtils.class);

	public static final String SIM_SLOT_0 = "0";
	public static final String SIM_SLOT_1 = "1";
	public static final String SIM_SLOT_2 = "2";


	/**
	 * Sends SMS to a number.
	 *
     * @param context Context
	 * @param sendTo Number to send SMS to.
	 * @param msg The message to be sent.
	 */
	public static void sendSms(Context context, String sendTo, String msg) {

		ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
		ArrayList<PendingIntent> deliveryIntents = new ArrayList<PendingIntent>();
		LOGD(TAG, "sendSms(): Sends SMS to a number: sendTo: " + sendTo + " message: " + msg);
		SmsManager sms = SmsManager.getDefault();
		ArrayList<String> parts = sms.divideMessage(msg);

		for (int i = 0; i < parts.size(); i++) {
			PendingIntent sentIntent = PendingIntent.getBroadcast(context, 0, new Intent(AppConfig.MESSAGE_SENT_ACTION), PendingIntent.FLAG_UPDATE_CURRENT);
			PendingIntent deliveryIntent = PendingIntent.getBroadcast(context, 0, new Intent(AppConfig.MESSAGE_DELIVERED_ACTION), PendingIntent.FLAG_UPDATE_CURRENT);
			sentIntents.add(sentIntent);
			deliveryIntents.add(deliveryIntent);
		}
		if (PhoneNumberUtils.isGlobalPhoneNumber(sendTo)) {
			sms.sendMultipartTextMessage(sendTo, null, parts, sentIntents, deliveryIntents);
		}
	}

    public static boolean sendSms(Context context, int simId, String toNumber, String sca, String smsText, PendingIntent sentIntent, PendingIntent deliveryIntent) {
        String name;
        try {
            if (simId == 0) {
                name = "isms0";
            } else if (simId == 1) {
                name = "isms";
                // for model : "Philips T939" name = "isms0"
            } else if (simId == 2) {
                name = "isms2";
            } else {
                throw new Exception("can not get service which for sim '" + simId + "', only 0,1 accepted as values");
            }
            Method method = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", String.class);
            method.setAccessible(true);
            Object param = method.invoke(null, name);

            method = Class.forName("com.android.internal.telephony.ISms$Stub").getDeclaredMethod("asInterface", IBinder.class);
            method.setAccessible(true);
            Object stubObj = method.invoke(null, param);
            if (Build.VERSION.SDK_INT < 18) {
                method = stubObj.getClass().getMethod("sendText", String.class, String.class, String.class, PendingIntent.class, PendingIntent.class);
                method.invoke(stubObj, toNumber, sca, smsText, sentIntent, deliveryIntent);
            } else {
                method = stubObj.getClass().getMethod("sendText", String.class, String.class, String.class, String.class, PendingIntent.class, PendingIntent.class);
                method.invoke(stubObj, context.getPackageName(), toNumber, sca, smsText, sentIntent, deliveryIntent);
            }

            return true;
        } catch (ClassNotFoundException e) {
            LOGE(TAG, "ClassNotFoundException", e);
        } catch (NoSuchMethodException e) {
            LOGE(TAG, "NoSuchMethodException", e);
        } catch (InvocationTargetException e) {
            LOGE(TAG, "InvocationTargetException", e);
        } catch (IllegalAccessException e) {
            LOGE(TAG, "IllegalAccessException", e);
        } catch (Exception e) {
            LOGE(TAG, "Exception", e);
        }
        return false;
    }


    public static boolean sendMultipartTextSms(Context context, int simId, String toNumber, String sca, ArrayList<String> smsTextlist, ArrayList<PendingIntent> sentIntentList, ArrayList<PendingIntent> deliveryIntentList) {
        String name;
        try {
            if (simId == 0) {
                name = "isms0";
            } else if (simId == 1) {
                name = "isms";
                // for model : "Philips T939" name = "isms0"
            } else if (simId == 2) {
                name = "isms2";
            } else {
                throw new Exception("can not get service which for sim '" + simId + "', only 0,1 accepted as values");
            }
            Method method = Class.forName("android.os.ServiceManager").getDeclaredMethod("getService", String.class);
            method.setAccessible(true);
            Object param = method.invoke(null, name);

            method = Class.forName("com.android.internal.telephony.ISms$Stub").getDeclaredMethod("asInterface", IBinder.class);
            method.setAccessible(true);
            Object stubObj = method.invoke(null, param);
            if (Build.VERSION.SDK_INT < 18) {
                method = stubObj.getClass().getMethod("sendMultipartText", String.class, String.class, List.class, List.class, List.class);
                method.invoke(stubObj, toNumber, sca, smsTextlist, sentIntentList, deliveryIntentList);
            } else {
                method = stubObj.getClass().getMethod("sendMultipartText", String.class, String.class, String.class, List.class, List.class, List.class);
                method.invoke(stubObj, context.getPackageName(), toNumber, sca, smsTextlist, sentIntentList, deliveryIntentList);
            }
            return true;
        } catch (ClassNotFoundException e) {
            LOGE(TAG, "ClassNotFoundException", e);
        } catch (NoSuchMethodException e) {
            LOGE(TAG, "NoSuchMethodException", e);
        } catch (InvocationTargetException e) {
            LOGE(TAG, "InvocationTargetException", e);
        } catch (IllegalAccessException e) {
            LOGE(TAG, "IllegalAccessException", e);
        } catch (Exception e) {
            LOGE(TAG, "Exception", e);
        }
        return false;
    }

    public static List<SimInfo> getSiminfo(Context context) {
        List<SimInfo> simInfoList = new ArrayList<SimInfo>();
        Uri URI_TELEPHONY = Uri.parse("content://telephony/siminfo/");
        Cursor c = context.getContentResolver().query(URI_TELEPHONY, null, null, null, null);
        if (c.moveToFirst()) {
            do {
                int id = c.getInt(c.getColumnIndex("_id"));
                int slot = c.getInt(c.getColumnIndex("slot"));
                String displayName = c.getString(c.getColumnIndex("display_name"));
                String iccId = c.getString(c.getColumnIndex("icc_id"));
                SimInfo simInfo = new SimInfo(id, displayName, iccId, slot);
                simInfoList.add(simInfo);
            } while (c.moveToNext());
        }
        c.close();
        return simInfoList;
    }
}
