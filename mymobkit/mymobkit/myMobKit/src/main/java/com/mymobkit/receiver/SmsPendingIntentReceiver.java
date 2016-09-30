package com.mymobkit.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mymobkit.data.SmsHelper;
import com.mymobkit.service.HttpdService;
import com.mymobkit.service.api.MessagingApiHandler;
import com.mymobkit.service.api.sms.Sms;

public abstract class SmsPendingIntentReceiver extends BroadcastReceiver {
    
	public static final int SUCCESS_STATUS = 0;
	public static final int IN_PROGRESS = -1;
	
    protected final SmsHelper smsHelper;
    protected final HttpdService service;
    protected String answerTo;
    
    SmsPendingIntentReceiver(final HttpdService service, final SmsHelper smsHelper) {
        this.service = service;
        this.smsHelper = smsHelper;
    }
    
    public void onReceive(Context context, Intent intent) {
        String smsID = intent.getStringExtra(MessagingApiHandler.PARAM_SMS_ID);
        int partNum = intent.getIntExtra("partNum", -1);
        int res = getResultCode();
        // check if we found the sms in our database
        Sms s = getSms(smsID);
        if (s != null) {
            // we have found a sms in our database
            onReceiveWithSms(context, s, partNum, res, smsID);
        } else {
            // the sms is missing in our database
            onReceiveWithoutSms(context, partNum, res);
        }
    }
    
    protected abstract void onReceiveWithSms(Context context, Sms s, int partNum, int res, String smsId);
    
    protected abstract void onReceiveWithoutSms(Context context, int partNum, int res);
    
    protected Sms getSms(String smsId) {
        return smsHelper.getSms(smsId);
    }
    
    protected void removeSms(String smsId) {
        smsHelper.deleteSms(smsId);
    }    
}

