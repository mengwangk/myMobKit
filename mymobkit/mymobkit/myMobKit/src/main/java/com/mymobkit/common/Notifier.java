package com.mymobkit.common;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.mymobkit.R;
import com.mymobkit.app.AppController;
import com.mymobkit.ui.activity.ControlPanelActivity;

import static com.mymobkit.common.LogUtils.makeLogTag;

public final class Notifier {

    private static final String TAG = makeLogTag(Notifier.class);

    // Notification
    //private static NotificationManager notificationManager = null;

    //private final static int NOTIFICATION_ID = R.layout.notification;

    //private static final int NOTIFY_RUNNING = 100;

    private final static int FAILURE_NOTIFICATION_ID = 42;

    private static final int GCM_NOTIFICATION_ID = 1;

    private final static int NOTIFICATION_RINGING = 188;

    /**
     * Create the intent that is used when the user touch the QuiteSleep notification, and then go to the Main class application.
     *
     * @return the PendingIntent
     * @see PendingIntent
     */
    private static PendingIntent notificationIntent(final Context context) {
        // The PendingIntent to launch our activity if the user selects this
        // notification. Note the use of FLAG_UPDATE_CURRENT so that if there
        // is already an active matching pending intent, we will update its
        // extras to be the ones passed in here.
        final Intent intent = new Intent(context, ControlPanelActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        final PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return contentIntent;
    }

    /**
     * Show myMobKit service running notification. If is the stop action, cancel the notification and hide.
     */
   /* public static void showNotification(final Context context, final boolean showNotif, final int iconId, final int msgId) {

        try {
            if (showNotif) {

                // Get the notification manager service
                if (notificationManager == null)
                    notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                CharSequence title = context.getText(R.string.app_name);
                CharSequence message;
                message = AppController.getContext().getText(msgId);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                Notification notification = builder.setContentIntent(notificationIntent(context)).setSmallIcon(iconId).setTicker(message).setWhen(System.currentTimeMillis()).setOngoing(true).setContentTitle(title).setContentText(message).build();
                notificationManager.notify(NOTIFICATION_ID, notification);
            } else {
                if (notificationManager == null) {
                    notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                }
                LOGD(TAG, "[showNotification] Notification is canceled");
                notificationManager.cancel(NOTIFICATION_ID);
            }

        } catch (Exception e) {
            LOGE(TAG, "[showNotification] Error showing notification", e);
        }
    }*/

    /**
     * Build an ongoing notification.
     *
     * @param context
     * @param msg
     * @return
     */
    public static Notification buildNotification(final Context context, final String msg) {
        CharSequence title = AppController.appName();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.setContentIntent(notificationIntent(context)).setSmallIcon(R.drawable.ic_launcher).setTicker(msg).setWhen(System.currentTimeMillis()).setOngoing(true).setContentTitle(title).setContentText(msg).build();
        return notification;
    }

    /**
     * Clear the standard notification alert.
     *
     * @param context - The context of the calling activity.
     * @return void
     */
   /* public static void clear(Context context) {
        clearAll(context);
    }*/

    /**
     * Clear all notifications shown to the user.
     *
     * @param context - The context of the calling activity.
     * @return void.
     */
   /* public static void clearAll(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }*/

    /**
     * Clear a running notification.
     *
     * @param context - The context of the calling activity.
     * @return void
     */
   /* public static void clearNotify(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFY_RUNNING);
    }

    public static void notificationSound() {
        final Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final Ringtone r = RingtoneManager.getRingtone(AppController.getContext().getApplicationContext(), notification);
        r.play();
    }*/

   /* public static void showFailureNotification(final Context context,
                                               final int iconId,
                                               final CharSequence title,
                                               final CharSequence msg) {
        final Intent intent = new Intent(context, ControlPanelActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(AppConfig.GTALK_EXTRA_KEY_REAUTHORIZE, true);

        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(iconId).setContentTitle(title).setContentText(msg)
                .setAutoCancel(true).setSound(defaultSoundUri).setContentIntent(pendingIntent);
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(FAILURE_NOTIFICATION_ID, notificationBuilder.build());

        *//*
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(iconId).setContentTitle(title).setContentText(msg);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(ControlPanelActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(FAILURE_NOTIFICATION_ID, builder.build());
        *//*
    }*/

    public static void showGcmNotification(final Context context, final String message) {
        final CharSequence title = context.getString(R.string.msg_notification_gcm);
        final Intent intent = new Intent(context, ControlPanelActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher).setContentTitle(title).setContentText(message)
                .setAutoCancel(true).setSound(defaultSoundUri).setContentIntent(pendingIntent);
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(GCM_NOTIFICATION_ID, notificationBuilder.build());
    }

   /* *//**
     * Displays an notification in the status bar to send the command ring:stop
     *//*
    public static void displayRingingNotification(final Context context) {
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        final Intent intent = ServiceUtils.newHttpdServiceIntent(context);
        intent.setAction(AppConfig.INTENT_GTALK_ACTION_COMMAND);
        intent.putExtra(GTalkUtils.PARAM_COMMAND, RingCommand.ACTION_RING);
        intent.putExtra(GTalkUtils.PARAM_COMMAND_ARGS, GTalkUtils.COMMAND_STOP);

        final PendingIntent pendingIntentStopRinging = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle(context.getString(R.string.msg_notification_alert));
        builder.setContentText(context.getString(R.string.msg_notification_stop_ringing));
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentIntent(pendingIntentStopRinging);
        builder.setOngoing(true);
        notificationManager.notify(NOTIFICATION_RINGING, builder.build());
    }*/

   /* *//**
     * Hides the stop ringing notification
     *//*
    public static void hideRingingNotification(final Context context) {
        final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_RINGING);
    }*/

}
