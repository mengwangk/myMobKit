package com.mymobkit.google;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.mymobkit.R;
import com.mymobkit.common.AppPreference;
import com.mymobkit.ui.fragment.ServiceSettingsFragment;

import java.util.concurrent.CountDownLatch;

import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Google API client sync task.
 */
public abstract class ApiClientSyncTask<Params, Progress, Result> {

    private static final String TAG = makeLogTag(ApiClientSyncTask.class);

    private GoogleApiClient googleApiClient = null;

    private Context context;

    public ApiClientSyncTask(Context context) {
        this.context = context;
        final String gmail = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_DEVICE_EMAIL_ADDRESS, context.getString(R.string.default_device_email_address));
        if (!TextUtils.isEmpty(gmail)) {
            GoogleApiClient.Builder builder = new GoogleApiClient.Builder(context)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .setAccountName(gmail);
            googleApiClient = builder.build();
        }
    }

    public final Result execute(Params... params) {

        if (googleApiClient == null) return null;

        final CountDownLatch latch = new CountDownLatch(1);

        googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnectionSuspended(int cause) {
            }

            @Override
            public void onConnected(Bundle bundle) {
                latch.countDown();
            }
        });

        googleApiClient.registerConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult result) {
                latch.countDown();
            }
        });

        googleApiClient.connect();

        try {
            latch.await();
        } catch (InterruptedException e) {
            return null;
        }

        if (!googleApiClient.isConnected()) {
            return null;
        }

        try {
            return executeConnected(params);
        } finally {
            googleApiClient.disconnect();
        }
    }

    /**
     * Override this method to perform a computation on a background thread, while the client is
     * connected.
     */
    protected abstract Result executeConnected(Params... params);

    /**
     * Gets the GoogleApiClient owned by this async task.
     */
    protected GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }
}
