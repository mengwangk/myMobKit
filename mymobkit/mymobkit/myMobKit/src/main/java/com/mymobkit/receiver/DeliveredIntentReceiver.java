package com.mymobkit.receiver;

import android.app.Activity;
import android.content.Context;

import com.mymobkit.data.SmsHelper;
import com.mymobkit.service.HttpdService;
import com.mymobkit.service.api.sms.Sms;

public class DeliveredIntentReceiver extends SmsPendingIntentReceiver {

    public DeliveredIntentReceiver(final HttpdService service, final SmsHelper smsHelper) {
        super(service, smsHelper);
    }

    @Override
    public void onReceiveWithSms(Context context, Sms s, int partNum, int res, String smsId) {
        this.answerTo = s.getAnswerTo();
        s.setDelIntentTrue(partNum);
        smsHelper.setDelIntentTrue(smsId, partNum);
        boolean delIntComplete = s.delIntentsComplete();
        String smsSendTo;
        if (s.getTo() != null) { // prefer a name over a number in the to field
            smsSendTo = s.getTo();
        } else {
            smsSendTo = s.getNumber();
        }
        if (res == Activity.RESULT_OK && delIntComplete) {
            s.setDelIntentResult(SUCCESS_STATUS);
            smsHelper.addSms(s);
        //} else if (s.getSentIntentResult() == SmsPendingIntentReceiver.IN_PROGRESS) { //
        // Modified  Feb 11 2016
        } else if (s.getDelIntentResult() == SmsPendingIntentReceiver.IN_PROGRESS) {
            if (res != Activity.RESULT_OK) {
                s.setDelIntentResult(res);
                smsHelper.addSms(s);
            }
        }
    }

    @Override
    public void onReceiveWithoutSms(Context context, int partNum, int res) {
        answerTo = null;
        switch (res) {
            case Activity.RESULT_OK:
                break;
            case Activity.RESULT_CANCELED:
                break;
        }
    }
}
