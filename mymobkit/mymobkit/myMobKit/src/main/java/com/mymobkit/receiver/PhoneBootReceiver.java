package com.mymobkit.receiver;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.mymobkit.R;
import com.mymobkit.common.AppPreference;
import com.mymobkit.common.ServiceUtils;
import com.mymobkit.common.WidgetUtils;
import com.mymobkit.ui.fragment.ServiceSettingsFragment;
import com.mymobkit.ui.widget.ViewerWidget;

import static com.mymobkit.common.LogUtils.LOGD;
import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.LOGI;
import static com.mymobkit.common.LogUtils.makeLogTag;

public final class PhoneBootReceiver extends BroadcastReceiver {

    private static final String TAG = makeLogTag(PhoneBootReceiver.class);

    private final String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

    //private GcmHeartBeatReceiver gcmHeartBeatReceiver = new GcmHeartBeatReceiver();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(BOOT_COMPLETED)) {
            listenBootCompleted(context);
            scheduleWidget(context);
        }
    }

    public void scheduleWidget(Context context){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(context, ViewerWidget.class));
        if (ids.length > 0) {
            WidgetUtils.scheduleUpdate(context);
        }
    }

    /**
     * Listen for BOOT_COMPLETED event and start myMobKit control panel service
     * if it is configured to start upon reboot
     *
     * @param context
     */
    public void listenBootCompleted(final Context context) {
        try {
            LOGD(TAG, "[listenBootCompleted] Boot completed!!");

            AppPreference prefs = AppPreference.getInstance();
            boolean isServiceStarted = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_CONTROL_PANEL_STATUS, Boolean.parseBoolean(context.getString(R.string.default_start_stop_service)));
            boolean isAutoStart = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_CONTROL_PANEL_AUTO_START, Boolean.parseBoolean(context.getString(R.string.default_auto_start_service)));
            if (isServiceStarted && isAutoStart) {

                // Create the service intent
               /* int port = Integer.valueOf(prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_CONTROL_PANEL_PORT, context.getString(R.string.default_control_panel_http_port)));
                boolean loginRequired = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_CONTROL_PANEL_LOGIN_REQUIRED, Boolean.valueOf(context.getString(R.string.default_login_required)));
                String userName = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_CONTROL_PANEL_USER_NAME, context.getString(R.string.default_http_user_name));
                String userPassword = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_CONTROL_PANEL_USER_PASSWORD, context.getString(R.string.default_http_user_password));
                boolean disableNotification = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_CONTROL_PANEL_DISABLE_NOTIFICATION, Boolean.valueOf(context.getString(R.string.default_disable_notification)));
                String primaryAddressFamily = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_PRIMARY_ADDRESS_FAMILY, context.getString(R.string.default_primary_address_family));

                ServiceUtils.startHttpdService(context, port, loginRequired, userName, userPassword, disableNotification, primaryAddressFamily);*/
                ServiceUtils.startHttpdService(context);
                LOGI(TAG, "[listenBootCompleted] myMobKit HTTPD service is started!!");
            }
        } catch (Exception e) {
            LOGE(TAG, "[listenBootCompleted] Error starting HTTPD service", e);
        } finally {
            // Start up the GCM heart beat
            //gcmHeartBeatReceiver.setAlarm(context);
        }
    }
}
