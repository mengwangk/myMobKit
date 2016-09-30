package com.mymobkit.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;

import com.mymobkit.mms.utils.Utils;

import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Listen for new MMS.
 */
public class MmsReceiver extends BroadcastReceiver {

    private static final String TAG = makeLogTag(MmsReceiver.class);

    private boolean isRelevant(Context context, Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                Telephony.Sms.Intents.WAP_PUSH_RECEIVED_ACTION.equals(intent.getAction()) &&
                Utils.isDefaultSmsProvider(context)) {
            return false;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void onReceive(final Context context, final Intent intent) {
        // SmsReceiver.handleOnReceive(this, context, intent);

        final String action = intent.getAction();
        if ((Telephony.Sms.Intents.WAP_PUSH_DELIVER_ACTION.equals(action) &&
                Utils.isDefaultSmsProvider(context)) ||
                (Telephony.Sms.Intents.WAP_PUSH_RECEIVED_ACTION.equals(action) &&
                        isRelevant(context, intent))) {
          /*  ApplicationContext.getInstance(context)
                    .getJobManager()
                    .add(new MmsReceiveJob(context, intent.getByteArrayExtra("data")));

            abortBroadcast();*/
        }
    }
}
