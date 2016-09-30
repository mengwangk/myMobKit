package com.mymobkit.gsm;


import android.annotation.TargetApi;
import android.content.Context;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

import java.util.List;

import static com.mymobkit.common.LogUtils.makeLogTag;

@TargetApi(22)
public class DualSimManagerLollipop {


    private static final String TAG = makeLogTag(DualSimManagerLollipop.class);
    public static SubscriptionManager subscriptionManager = null;
    private Context context;

    public DualSimManagerLollipop(Context context) {
        this.context = context;
        if (subscriptionManager == null) {
            subscriptionManager = SubscriptionManager.from(this.context);
        }
    }

    public int getSimSupportedCount() {
        return subscriptionManager.getActiveSubscriptionInfoCountMax();
    }

    public List<SubscriptionInfo> getActiveSubscriptionInfo() {
        return subscriptionManager.getActiveSubscriptionInfoList();
    }
}
