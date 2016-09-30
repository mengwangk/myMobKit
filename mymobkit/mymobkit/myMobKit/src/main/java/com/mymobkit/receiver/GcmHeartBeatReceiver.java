package com.mymobkit.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.mymobkit.common.GcmUtils;

import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Created by MEKOH on 1/1/2016.
 */
public final class GcmHeartBeatReceiver extends BroadcastReceiver {

    private static final String TAG = makeLogTag(GcmHeartBeatReceiver.class);

    private static final long HEARTBEAT_INTERVAL = 1000 * 60 * 3;  // Every 3 minutes

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        wl.acquire();

        GcmUtils.broadcastHeartBeat(context);

        wl.release();
    }

    public void setAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, GcmHeartBeatReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), HEARTBEAT_INTERVAL, pi);
    }

    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, GcmHeartBeatReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}