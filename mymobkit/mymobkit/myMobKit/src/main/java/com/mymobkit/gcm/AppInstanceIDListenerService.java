package com.mymobkit.gcm;

import static com.mymobkit.common.LogUtils.makeLogTag;
import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class AppInstanceIDListenerService extends InstanceIDListenerService {

    private static final String TAG = makeLogTag(AppInstanceIDListenerService.class);

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        final Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }
}
