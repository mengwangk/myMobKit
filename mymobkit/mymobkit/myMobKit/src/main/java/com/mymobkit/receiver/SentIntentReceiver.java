package com.mymobkit.receiver;

import static com.mymobkit.common.LogUtils.makeLogTag;
import android.app.Activity;
import android.content.Context;

import com.mymobkit.data.SmsHelper;
import com.mymobkit.service.HttpdService;
import com.mymobkit.service.api.sms.Sms;
import com.mymobkit.service.api.sms.SmsManager;

public class SentIntentReceiver extends SmsPendingIntentReceiver {

	private boolean saveSentMessage;

	private static final String TAG = makeLogTag(SentIntentReceiver.class);
	
	public SentIntentReceiver(final HttpdService service, final SmsHelper smsHelper, final boolean saveSentMessage) {
		super(service, smsHelper);
		this.saveSentMessage = saveSentMessage;
	}

	@Override
	public void onReceiveWithSms(Context context, Sms s, int partNum, int res, String smsId) {
		answerTo = s.getAnswerTo();
		s.setSentIntentTrue(partNum);
		smsHelper.setSentIntentTrue(smsId, partNum);
		boolean sentIntComplete = s.sentIntentsComplete();
		String smsSendTo;
		if (s.getTo() != null) { // prefer a name over a number in the to field
			smsSendTo = s.getTo();
		} else {
			smsSendTo = s.getNumber();
		}

		if (res == Activity.RESULT_OK && sentIntComplete) {
			s.setSentIntentResult(SUCCESS_STATUS);
			smsHelper.addSms(s);
			if (saveSentMessage) {
				SmsManager smsManager = new SmsManager(context);
				smsManager.addSmsSent(s.getMessage(), s.getNumber());
			}
		} else if (s.getSentIntentResult() == SmsPendingIntentReceiver.IN_PROGRESS) {
			if (res != Activity.RESULT_OK) {
				s.setSentIntentResult(res);
				smsHelper.addSms(s);
				if (saveSentMessage) {
				}
			} 
		}

		// if (sentIntComplete) {
		// removeSms(smsID);
		// }
	}

	@Override
	public void onReceiveWithoutSms(Context context, int partNum, int res) {
		answerTo = null;
	}

}
