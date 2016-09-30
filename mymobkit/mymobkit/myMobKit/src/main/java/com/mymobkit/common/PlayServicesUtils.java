package com.mymobkit.common;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import static com.mymobkit.common.LogUtils.makeLogTag;
import static com.mymobkit.common.LogUtils.LOGI;

/**
 * Helper for Google Play services-related operations.
 */
public class PlayServicesUtils {

    private static final String TAG = makeLogTag(PlayServicesUtils.class);
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static boolean checkGooglePlaySevices(final Activity activity) {
        final int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, activity, PLAY_SERVICES_RESOLUTION_REQUEST);
                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        activity.finish();
                    }
                });
                dialog.show();
            } else {
                LOGI(TAG, "[checkGooglePlaySevices] This device is not supported.");
                activity.finish();
            }
            return false;
        }
        return true;
    }
}
