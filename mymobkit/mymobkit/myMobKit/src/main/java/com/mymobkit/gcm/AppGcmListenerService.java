package com.mymobkit.gcm;

import android.os.Bundle;
import android.text.TextUtils;

import com.google.android.gms.gcm.GcmListenerService;
import com.mymobkit.R;
import com.mymobkit.common.AppPreference;
import com.mymobkit.common.EntityUtils;
import com.mymobkit.common.GcmUtils;
import com.mymobkit.common.Notifier;
import com.mymobkit.data.NotificationMsgHelper;
import com.mymobkit.gcm.command.MotionDetectionCommand;
import com.mymobkit.gcm.command.ServiceCommand;
import com.mymobkit.gcm.command.SurveillanceCommand;
import com.mymobkit.gcm.command.SwitchCameraCommand;
import com.mymobkit.gcm.command.WakeUpCommand;
import com.mymobkit.model.NotificationMsg;
import com.mymobkit.ui.fragment.DetectionSettingsFragment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.LOGI;
import static com.mymobkit.common.LogUtils.makeLogTag;

public class AppGcmListenerService extends GcmListenerService {

    private static final String TAG = makeLogTag(AppGcmListenerService.class);

    private static final Map<String, GcmCommand> MESSAGE_RECEIVERS;

    static {
        // Known messages and their GCM message receivers
        Map<String, GcmCommand> receivers = new HashMap<String, GcmCommand>();
        receivers.put(GcmMessage.ActionType.WAKE_UP.getType(), new WakeUpCommand());
        receivers.put(GcmMessage.ActionType.MOTION_DETECTION.getType(), new MotionDetectionCommand());
        receivers.put(GcmMessage.ActionType.SERVICE.getType(), new ServiceCommand());
        receivers.put(GcmMessage.ActionType.SURVEILLANCE.getType(), new SurveillanceCommand());
        receivers.put(GcmMessage.ActionType.SWITCH_CAMERA.getType(), new SwitchCameraCommand());
        MESSAGE_RECEIVERS = Collections.unmodifiableMap(receivers);
    }


    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs. For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        final String action = data.getString(GcmUtils.PARAMETER_ACTION);
        final String extraData = data.getString(GcmUtils.PARAMETER_EXTRA_DATA);

        LOGI(TAG, "From: " + from);
        LOGI(TAG, "Action: " + action);
        LOGI(TAG, "Extra data: " + extraData);

        if (TextUtils.isEmpty(action)) {
            LOGE(TAG, "[onMessageReceived] Message received without command action");
            return;
        }

        // Production applications would usually process the message here. Eg: - Syncing with server. - Store message in local database. - Update UI.
        final NotificationMsgHelper messageHelper = NotificationMsgHelper.getNotificationMsgHelper(this);
        final NotificationMsg msg = new NotificationMsg(EntityUtils.generateUniqueId(), action, extraData, System.currentTimeMillis());
        messageHelper.addMsg(msg);

        final GcmCommand command = MESSAGE_RECEIVERS.get(action);
        GcmMessage message = null;
        if (command == null) {
            LOGE(TAG, "[onMessageReceived] Unknown command received: " + action);
        } else {
            message = command.execute(this, action, extraData);
        }

        // In some cases it may be useful to show a notification indicating to the user that a message was received.
        final boolean isShowNotification = AppPreference.getInstance().getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_SHOW_GCM_NOTIFICATION, Boolean.valueOf(this.getString(R.string.default_show_gcm_notification)));
        if (isShowNotification && message != null) {
            Notifier.showGcmNotification(this, message.getDescription());
        }
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
     /* private void sendNotification(String message) {
       final CharSequence title = AppController.appName();
        final Intent intent = new Intent(this, ControlPanelActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher).setContentTitle(title).setContentText(message)
                .setAutoCancel(true).setSound(defaultSoundUri).setContentIntent(pendingIntent);
        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }*/
}
