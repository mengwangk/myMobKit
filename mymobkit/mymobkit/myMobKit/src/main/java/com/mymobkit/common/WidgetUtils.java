package com.mymobkit.common;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.mymobkit.ui.fragment.ServiceSettingsFragment;
import com.mymobkit.ui.widget.ViewerWidget;

/**
 * Widget helper class.
 */
public final class WidgetUtils {

    private static final String DEFAULT_INTERVAL_MINUTES = "5";

    public static void scheduleUpdate(Context context) {
        String interval = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_WIDGET_UPDATE_INTERVAL, DEFAULT_INTERVAL_MINUTES);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long intervalMillis = Integer.parseInt(interval) * 60 * 1000;

        PendingIntent pi = getAlarmIntent(context);
        am.cancel(pi);
        am.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), intervalMillis, pi);
    }

    private static PendingIntent getAlarmIntent(Context context) {
        Intent intent = new Intent(context, ViewerWidget.class);
        intent.setAction(ViewerWidget.ACTION_UPDATE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        return pi;
    }

    public static void clearUpdate(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(getAlarmIntent(context));
    }

    public static boolean hasInstances(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, ViewerWidget.class));
        return (appWidgetIds.length > 0);
    }
}
