package com.mymobkit.gcm.command;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.mymobkit.app.AppConfig;
import com.mymobkit.common.ServiceUtils;
import com.mymobkit.gcm.GcmCommand;
import com.mymobkit.gcm.GcmMessage;
import com.mymobkit.gcm.message.ServiceMessage;
import com.mymobkit.service.HttpdService;
import com.mymobkit.service.webcam.WebcamService;

import static com.mymobkit.common.LogUtils.LOGD;
import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Command to start or stop the HTTPD service.
 */
public class ServiceCommand extends GcmCommand {

    private static final String TAG = makeLogTag(ServiceCommand.class);

    @Override
    public GcmMessage execute(Context context, String type, String extraData) {
        LOGD(TAG, "Received GCM message: type=" + type + ", extraData=" + extraData);
        ServiceMessage message = null;
        try {
            message = new Gson().fromJson(extraData, ServiceMessage.class);
            if (message != null) {
                final GcmMessage.ActionCommand actionCommand = GcmMessage.ActionCommand.get(message.getActionCommand());
                final boolean isServiceRunning = ServiceUtils.isServiceRunning(context, HttpdService.class);

                if (actionCommand == GcmMessage.ActionCommand.START) {
                    if (!isServiceRunning) {
                       /* AppPreference prefs = AppPreference.getInstance();
                        int controlPanelPort = Integer.parseInt(prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_CONTROL_PANEL_PORT, context.getString(R.string.default_control_panel_http_port)));
                        boolean loginRequired = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_CONTROL_PANEL_LOGIN_REQUIRED, Boolean.valueOf(context.getString(R.string.default_login_required)));
                        String userName = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_CONTROL_PANEL_USER_NAME, context.getString(R.string.default_http_user_name));
                        String userPassword = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_CONTROL_PANEL_USER_PASSWORD, context.getString(R.string.default_http_user_password));
                        boolean disableNotification = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_CONTROL_PANEL_DISABLE_NOTIFICATION, Boolean.valueOf(context.getString(R.string.default_disable_notification)));
                        String primaryAddressFamily = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_PRIMARY_ADDRESS_FAMILY, context.getString(R.string.default_primary_address_family));

                        // Start the service
                        ComponentName cn = ServiceUtils.startHttpdService(context, controlPanelPort, loginRequired, userName, userPassword, disableNotification, primaryAddressFamily);*/
                        ComponentName cn = ServiceUtils.startHttpdService(context);

                    }
                } else if (actionCommand == GcmMessage.ActionCommand.STOP) {
                    if (isServiceRunning) {
                        Intent intent = new Intent(AppConfig.INTENT_SHUTDOWN_SERVICE_ACTION);
                        context.sendBroadcast(intent);
                        ServiceUtils.stopHttpdService(context);
                    }

                    // Check if camera service is running
                    boolean isRunning = ServiceUtils.isServiceRunning(context, WebcamService.class);
                    if (isRunning) {
                        ServiceUtils.stopCameraService(context);
                    }
                } else {
                    LOGE(TAG, "[execute] Unknown action command");
                }
            }
        } catch (Exception ex) {
            LOGE(TAG, "[execute] Unable to get the correct GCM message", ex);
        }
        return message;
    }
}
