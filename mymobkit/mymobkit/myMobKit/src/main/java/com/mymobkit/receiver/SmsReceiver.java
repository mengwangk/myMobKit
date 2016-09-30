package com.mymobkit.receiver;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import com.mymobkit.R;
import com.mymobkit.common.ValidationUtils;
import com.mymobkit.service.RemoteStartupService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Listen for new SMS.
 */
public class SmsReceiver extends BroadcastReceiver {

    /**
     * Tag for logging.
     */
    private static final String TAG = makeLogTag(SmsReceiver.class);

    /**
     * Intent.action for receiving SMS.
     */
    private static final String ACTION_SMS_OLD = "android.provider.Telephony.SMS_RECEIVED";

    private static final String ACTION_SMS_NEW = "android.provider.Telephony.SMS_DELIVER";

    /**
     * Intent.action for receiving MMS.
     */
    //private static final String ACTION_MMS_OLD = "android.provider.Telephony.WAP_PUSH_RECEIVED";

    //private static final String ACTION_MMS_MEW = "android.provider.Telephony.WAP_PUSH_DELIVER";

    /**
     * An unreadable MMS body.
     */
    //private static final String MMS_BODY = "<MMS>";

    /**
     * Delay for spinlock, waiting for new messages.
     */
    //private static final long SLEEP = 500;

    private static int resultCode = 0;

    @Override
    public final void onReceive(final Context context, final Intent intent) {
        resultCode = getResultCode();
        handleOnReceive(context, intent);
    }

    private void handleOnReceive(final Context context, final Intent intent) {
        final String action = intent.getAction();

        //LOGD(TAG, "onReceive(context, " + action + ")");

        //final PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //final PowerManager.WakeLock wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        //wakelock.acquire();

        //LOGI(TAG, "got wakelock");
        //LOGD(TAG, "got intent: " + action);
        //try {
        //    LOGD(TAG, "sleep(" + SLEEP + ")");
        //    Thread.sleep(SLEEP);
        //} catch (InterruptedException e) {
        //    LOGE(TAG, "interrupted in spinlock", e);
        // }

        //String msgBody = null;

        boolean remoteStartup = false;
        String remoteStartupKeyword = context.getString(R.string.remote_startup_keyword);

        try {
            if (ACTION_SMS_OLD.equals(action) || ACTION_SMS_NEW.equals(action)) {

                final Map<String, SmsMessage> messages = new ConcurrentHashMap<String, SmsMessage>(1);
                final Map<String, String> msgContents = new ConcurrentHashMap<String, String>(1);

                if (retrieveMessages(intent, messages, msgContents)) {

                    // check if related to remote start up
                    for (String msgBody : msgContents.values()) {
                        if (msgBody != null && msgBody.toLowerCase().startsWith(remoteStartupKeyword.toLowerCase())) {
                            remoteStartup = true;
                        }
                    }

                    if (action.equals(ACTION_SMS_NEW)) {
                        // API19+: save message to the database

                        for (String from: messages.keySet()) {
                            final SmsMessage smsMessage = messages.get(from);
                            final String msgBody = msgContents.get(from);
                            final ContentValues values = new ContentValues();
                            values.put("address", from);
                            values.put("body", msgBody);
                            values.put("service_center", ValidationUtils.getString(smsMessage.getServiceCenterAddress()));
                            values.put("date", smsMessage.getTimestampMillis());
                            values.put("protocol", smsMessage.getProtocolIdentifier());
                            values.put("reply_path_present", smsMessage.isReplyPathPresent());
                            values.put("type", Telephony.TextBasedSmsColumns.MESSAGE_TYPE_INBOX);
                            context.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);

                            //LOGD(TAG, "Insert SMS into database: " + s + ", " + msgBody);
                        }
                    }
                }
                /*
                Bundle b = intent.getExtras();
                // assert b != null;
                if (b == null) return;

                Object obj = b.get("pdus");
                if (obj == null) return;

                Object[] pdus = (Object[]) b.get("pdus");
                SmsMessage[] smsMessage = new SmsMessage[pdus.length];
                int l = pdus.length;
                for (int i = 0; i < l; i++) {
                    smsMessage[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                msgBody = null;
                if (l > 0) {
                    for (SmsMessage message : smsMessage) {
                        msgBody = message.getDisplayMessageBody();
                        // ! Check in blacklist db - filter spam
                        final String s = message.getDisplayOriginatingAddress();
                        final String serviceCenter = message.getServiceCenterAddress();
                        final long serviceCenterTimestamp = message.getTimestampMillis();

                        // check if related to remote start up
                        if (msgBody != null && msgBody.toLowerCase().startsWith(remoteStartupKeyword.toLowerCase())) {
                            remoteStartup = true;
                        }

                        if (action.equals(ACTION_SMS_NEW)) {
                            // API19+: save message to the database
                            ContentValues values = new ContentValues();
                            values.put("address", s);
                            values.put("body", msgBody);
                            values.put("service_center", ValidationUtils.getString(serviceCenter));
                            values.put("date", serviceCenterTimestamp);
                            context.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);

                            //LOGD(TAG, "Insert SMS into database: " + s + ", " + msgBody);
                        }
                    }
                }
                */
            }
            /*
            else if (ACTION_MMS_OLD.equals(action) || ACTION_MMS_MEW.equals(action)) {
                msgBody = MMS_BODY;
                // TODO API19+ MMS code
            }
            */

            if (remoteStartup) {
                intent.setClass(context, RemoteStartupService.class);
                intent.putExtra("result", resultCode);

                RemoteStartupService.beginStartingService(context, intent);
            }
        } catch (Exception ex) {
            LOGE(TAG, "[onReceive] Unable to handle received SMS", ex);
        }


        /*
        finally {
            if (wakelock != null && wakelock.isHeld()) {
                try {
                    wakelock.release();
                    LOGI(TAG, "wakelock released");
                } catch (Throwable th) {
                    // ignoring this exception, probably wakeLock was already released
                }
            }
        }
        */
    }


    private static boolean retrieveMessages(final Intent intent, final Map<String, SmsMessage> messages, Map<String, String> msgContents) {
        //final List<SmsMessage> messages = new ArrayList<SmsMessage>(1);
        //Map<String, String> msg = null;
        final SmsMessage[] smsMessages;
        final Bundle bundle = intent.getExtras();

        if (bundle == null || !bundle.containsKey("pdus")) {
            return false;
        }
        final Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus != null) {
            int numberOfPdus = pdus.length;
            //msg = new HashMap<String, String>(numberOfPdus);
            smsMessages = new SmsMessage[numberOfPdus];

            // There can be multiple SMS from multiple senders, there can be a maximum of nbrOfpdus different senders
            // However, send long SMS of same sender in one message
            for (int i = 0; i < numberOfPdus; i++) {
                smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                String originatingAddress = smsMessages[i].getDisplayOriginatingAddress();

                // Check if index with number exists
                if (!messages.containsKey(originatingAddress)) {
                    // Index with number doesn't exist
                    // Save string into associative array with sender number as index
                    messages.put(smsMessages[i].getDisplayOriginatingAddress(), smsMessages[i]);
                }

                // Check if index with number exists
                if (!msgContents.containsKey(originatingAddress)) {
                    // Index with number doesn't exist
                    // Save string into associative array with sender number as index
                    msgContents.put(smsMessages[i].getDisplayOriginatingAddress(), ValidationUtils.getString(smsMessages[i].getDisplayMessageBody()));
                } else {
                    // Number has been there, add content but consider that
                    // msg.get(originatinAddress) already contains sms:sndrNbr:previousparts of SMS,
                    // so just add the part of the current PDU
                    String previousParts = msgContents.get(originatingAddress);
                    String msgString = previousParts + ValidationUtils.getString(smsMessages[i].getDisplayMessageBody());
                    msgContents.put(originatingAddress, msgString);
                }
            }
        } else {
            return false;
        }
        return true;
    }
}
