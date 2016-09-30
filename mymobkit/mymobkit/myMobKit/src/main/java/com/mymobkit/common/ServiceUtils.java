package com.mymobkit.common;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.mymobkit.R;
import com.mymobkit.app.AppConfig;
import com.mymobkit.gcm.message.DeviceInfoMessage;
import com.mymobkit.google.TrackDeviceAsyncTask;
import com.mymobkit.service.HttpdService;
import com.mymobkit.service.webcam.WebcamService;
import com.mymobkit.ui.activity.WebcamActivity;
import com.mymobkit.ui.fragment.DetectionSettingsFragment;
import com.mymobkit.ui.fragment.ServiceSettingsFragment;

public final class ServiceUtils {

    public static boolean isServiceRunning(final Context context, final Class clazz) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (clazz.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static ComponentName startHttpdService(final Context context, final int controlPanelPort, final boolean loginRequired, final String userName, final String userPassword, final boolean disableNotification, final String primaryAddressFamily) {
        Intent intent = new Intent(context, HttpdService.class);
        intent.putExtra(AppConfig.HTTPD_SERVICE_ACTION_PARAM, AppConfig.INTENT_START_HTTPD_ACTION);
        intent.putExtra(AppConfig.CONTROL_PANEL_LISTENING_PORT_PARAM, controlPanelPort);
        intent.putExtra(AppConfig.LOGIN_REQUIRED_PARAM, loginRequired);
        intent.putExtra(AppConfig.LOGIN_USER_NAME_PARAM, userName);
        intent.putExtra(AppConfig.LOGIN_USER_PASSWORD_PARAM, userPassword);
        intent.putExtra(AppConfig.DISABLE_NOTIFICATION, disableNotification);
        intent.putExtra(AppConfig.PRIMARY_ADDRESS_FAMILY_PARAM, primaryAddressFamily);
        return context.startService(intent);
    }

    public static Intent newHttpdServiceIntent(final Context context){
        final AppPreference prefs = AppPreference.getInstance();
        final int controlPanelPort = Integer.parseInt(prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_CONTROL_PANEL_PORT, context.getString(R.string.default_control_panel_http_port)));
        final boolean loginRequired = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_CONTROL_PANEL_LOGIN_REQUIRED, Boolean.valueOf(context.getString(R.string.default_login_required)));
        final String userName = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_CONTROL_PANEL_USER_NAME, context.getString(R.string.default_http_user_name));
        final String userPassword = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_CONTROL_PANEL_USER_PASSWORD, context.getString(R.string.default_http_user_password));
        final boolean disableNotification = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_CONTROL_PANEL_DISABLE_NOTIFICATION, Boolean.valueOf(context.getString(R.string.default_disable_notification)));
        final String primaryAddressFamily = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_PRIMARY_ADDRESS_FAMILY, context.getString(R.string.default_primary_address_family));

        Intent intent = new Intent(context, HttpdService.class);
        intent.putExtra(AppConfig.CONTROL_PANEL_LISTENING_PORT_PARAM, controlPanelPort);
        intent.putExtra(AppConfig.LOGIN_REQUIRED_PARAM, loginRequired);
        intent.putExtra(AppConfig.LOGIN_USER_NAME_PARAM, userName);
        intent.putExtra(AppConfig.LOGIN_USER_PASSWORD_PARAM, userPassword);
        intent.putExtra(AppConfig.DISABLE_NOTIFICATION, disableNotification);
        intent.putExtra(AppConfig.PRIMARY_ADDRESS_FAMILY_PARAM, primaryAddressFamily);

        return intent;
    }

    public static ComponentName startHttpdService(final Context context) {
        final Intent intent = newHttpdServiceIntent(context);
        intent.putExtra(AppConfig.HTTPD_SERVICE_ACTION_PARAM, AppConfig.INTENT_START_HTTPD_ACTION);
        return context.startService(intent);
    }

    public static boolean stopHttpdService(final Context context) {
        return context.stopService(new Intent(context, HttpdService.class));
    }

    public static ComponentName startCameraService(final Context context) {
        Intent intent = new Intent(context, WebcamService.class);
        return context.startService(intent);
    }

    public static boolean stopCameraService(final Context context) {
        return context.stopService(new Intent(context, WebcamService.class));
    }

  /*  public static ComponentName startViewerService(final Context context) {
        Intent intent = new Intent(context, ViewerService.class);
        return context.startService(intent);
    }

    public static boolean stopViewerService(final Context context) {
        return context.stopService(new Intent(context, ViewerService.class));
    }*/

    public static boolean trackDevice(final Context context, final boolean isStarted, final String uri) {
        // Check if we need to connect to Google Drive
        final boolean isDeviceTrackingEnabled = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_DEVICE_TRACKING, Boolean.valueOf(context.getString(R.string.default_device_tracking)));

        if (!isDeviceTrackingEnabled) return false;

        final DeviceInfoMessage message = new DeviceInfoMessage(context);
        if (isStarted) {
            message.setIsServiceStarted(true);
            message.setUri(uri);
        } else {
            message.setIsServiceStarted(false);
        }
        new TrackDeviceAsyncTask(context, message).execute();
        return true;
    }


    public static void startWebcam(final Context context) {
        // Check the preference first
        final boolean isBackgroundMode = AppPreference.getInstance().getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_BACKGROUND_CAMERA, Boolean.valueOf(context.getString(R.string.default_background_camera)));
        final boolean isServiceRunning = ServiceUtils.isServiceRunning(context, WebcamService.class);
        if (isBackgroundMode && !isServiceRunning) {
            ComponentName cn = ServiceUtils.startCameraService(context);
        } else if (!isBackgroundMode && isServiceRunning) {
            ServiceUtils.stopCameraService(context);
            Intent intent = new Intent(context, WebcamActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
        } else if (isBackgroundMode && isServiceRunning) {
            // Do nothing
        } else {
            Intent intent = new Intent(context, WebcamActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(intent);
        }
    }

    public static TelephonyManager getTelephonyManager(Context context) {
        return (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
    }
}
