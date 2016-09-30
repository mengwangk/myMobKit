package com.mymobkit.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.mymobkit.app.AppConfig;
import com.mymobkit.common.GcmUtils;

import java.io.IOException;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.LOGI;
import static com.mymobkit.common.LogUtils.makeLogTag;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = makeLogTag(RegistrationIntentService.class);

    //private static final String[] TOPICS = {"global"};

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            // In the (unlikely) event that multiple refresh operations occur simultaneously,
            // ensure that they are processed sequentially.
            synchronized (TAG) {

                // Not registered to server yet
                GcmUtils.saveRegistrationStatus(this, GcmUtils.RegistrationStatus.UNREGISTERED);

                // Initially this call goes out to the network to retrieve the token, subsequent calls are local.
                final InstanceID instanceID = InstanceID.getInstance(this);
                final String gcmRegId = instanceID.getToken(GcmUtils.getGcmSenderId(), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                LOGI(TAG, "GCM Registration Token: " + gcmRegId);

                // Persist the regID - no need to register again.
                if (GcmUtils.saveRegistrationId(this, gcmRegId)) {

                    // You should send the registration ID to your server over
                    // HTTP, so it can use GCM/HTTP or CCS to send messages to your app.
                    GcmUtils.syncRegisterToGcmServer(this);
                }

                //sendRegistrationToServer(token);

                // Subscribe to topic channels
                // subscribeTopics(token);

                // You should store a boolean that indicates whether the generated token has been
                // sent to your server. If the boolean is false, send the token to your server,
                // otherwise your server should have already received the token.
                //sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
            }
        } catch (Exception e) {

            LOGE(TAG, "[onHandleIntent] Unable to register device in GCM", e);

            // Log.d(TAG, "Failed to complete token refresh", e);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
           //  sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(AppConfig.INTENT_GCM_REGISTRATION_COMPLETE_ACTION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }


    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    //private void subscribeTopics(String token) throws IOException {
     //   for (String topic : TOPICS) {
     //       GcmPubSub pubSub = GcmPubSub.getInstance(this);
     //       pubSub.subscribe(token, "/topics/" + topic, null);
    //    }
    //}

}
