
package com.mymobkit.common.apis;

import java.util.ArrayList;

import android.app.PendingIntent;
import android.telephony.gsm.SmsManager;
import android.telephony.gsm.SmsMessage;

/**
 * Wrap around Telephony API.
 * 
 * @version 3
 */
@SuppressWarnings("deprecation")
public final class TelephonyWrapper3 extends TelephonyWrapper {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int[] calculateLength(final String messageBody, final boolean use7bitOnly) {
		return SmsMessage.calculateLength(messageBody, use7bitOnly);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<String> divideMessage(final String text) {
		return SmsManager.getDefault().divideMessage(text);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendMultipartTextMessage(final String destinationAddress, final String scAddress,
			final ArrayList<String> parts, final ArrayList<PendingIntent> sentIntents,
			final ArrayList<PendingIntent> deliveryIntents) {
		SmsManager.getDefault().sendMultipartTextMessage(destinationAddress, scAddress, parts,
				sentIntents, deliveryIntents);
	}
}
