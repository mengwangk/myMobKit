package com.mymobkit.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.mymobkit.R;
import com.mymobkit.common.MimeType;
import com.mymobkit.common.StorageUtils;
import com.mymobkit.common.WidgetUtils;
import com.mymobkit.service.api.drive.DriveFileInfo;
import com.mymobkit.service.api.drive.ListFileSyncTask;
import com.mymobkit.ui.activity.ViewerActivity;
import com.mymobkit.ui.activity.ViewerWidgetConfigureActivity;
import com.mymobkit.ui.fragment.PhotoGalleryFragment;
import com.mymobkit.ui.handler.DriveRequestHandler;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link ViewerWidgetConfigureActivity ViewerWidgetConfigureActivity}
 */
public class ViewerWidget extends AppWidgetProvider {

    public static final String ACTION_UPDATE = "com.mymobkit.widget.action.UPDATE";

    boolean isViewerUpdateInProgress = false;
    Picasso picasso;
    Handler mainThreadHandler;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            ViewerWidgetConfigureActivity.deletePref(context, appWidgetIds[i], ViewerWidgetConfigureActivity.PREF_KEY_DEVICE_ID);
            ViewerWidgetConfigureActivity.deletePref(context, appWidgetIds[i], ViewerWidgetConfigureActivity.PREF_KEY_DEVICE_NAME);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        WidgetUtils.scheduleUpdate(context);

        picasso = new Picasso.Builder(context)
                .addRequestHandler(new DriveRequestHandler(context))
                .build();

        mainThreadHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        WidgetUtils.clearUpdate(context);
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        CharSequence deviceName = ViewerWidgetConfigureActivity.loadPref(context, appWidgetId, ViewerWidgetConfigureActivity.PREF_KEY_DEVICE_NAME, "");
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.viewer_widget);
        views.setTextViewText(R.id.name, deviceName);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_UPDATE.equals(intent.getAction())) {

            if (picasso == null) {
                picasso = new Picasso.Builder(context)
                        .addRequestHandler(new DriveRequestHandler(context))
                        .build();
            }

            if (mainThreadHandler == null) {
                mainThreadHandler = new Handler(Looper.getMainLooper());
            }

            int extraWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
            final Bundle bundle = intent.getExtras();
            if (bundle != null) {
                extraWidgetId = bundle.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            }
            updateViewer(context, extraWidgetId);
        } else {
            super.onReceive(context, intent);
        }
    }

    private void updateViewer(Context context, int extraWidgetId) {
        if (isViewerUpdateInProgress) return;

        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final ComponentName componentName = new ComponentName(context.getPackageName(), getClass().getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
        if (appWidgetIds.length > 0) {
            ViewerUpdateTask viewerUpdateTask = new ViewerUpdateTask(context, extraWidgetId);
            viewerUpdateTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, appWidgetIds);
        }
    }

    private boolean hasInstances(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, this.getClass()));
        return (appWidgetIds.length > 0);
    }


    /**
     * Housekeeping task.
     */
    class ViewerUpdateTask extends AsyncTask<int[], Void, Void> {

        private final Context context;
        private final int extraWidgetId;

        public ViewerUpdateTask(final Context context, final int extraWidgetId) {
            this.context = context;
            this.extraWidgetId = extraWidgetId;
        }

        @Override
        protected Void doInBackground(int[]... appWidgetIdLists) {
            final int[] appWidgetIds = appWidgetIdLists[0];
            final int N = appWidgetIds.length;
            for (int i = 0; i < N; i++) {
                final int appWidgetId = appWidgetIds[i];
                if (extraWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID && appWidgetId != extraWidgetId)
                    continue;

                final String deviceName = ViewerWidgetConfigureActivity.loadPref(context, appWidgetId, ViewerWidgetConfigureActivity.PREF_KEY_DEVICE_NAME, "");
                final String deviceId = ViewerWidgetConfigureActivity.loadPref(context, appWidgetId, ViewerWidgetConfigureActivity.PREF_KEY_DEVICE_ID, "");

                if (TextUtils.isEmpty(deviceName) || TextUtils.isEmpty(deviceId)) continue;

                final ListFileSyncTask task = new ListFileSyncTask(context);
                final List<DriveFileInfo> fileInfos = task.execute(deviceId, MimeType.IMAGE_JPEG, "1");
                if (fileInfos != null && fileInfos.size() > 0) {
                    final DriveFileInfo fileInfo = fileInfos.get(0);
                    if (!TextUtils.isEmpty(fileInfo.getDriveId())) {
                        runOnMainThread(new Runnable() {
                            @Override
                            public void run() {

                                // Construct the RemoteViews object
                                RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.viewer_widget);
                                views.setTextViewText(R.id.name, deviceName);
                                views.setTextViewText(R.id.timestamp, StorageUtils.tidyImageName(fileInfo.getTitle()));

                                Intent intent = new Intent(context, ViewerActivity.class);
                                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                                views.setOnClickPendingIntent(R.id.image, pendingIntent);

                                // Get the device latest image
                                picasso.load(DriveRequestHandler.SCHEMA_DRIVE + ":" + fileInfo.getDriveId())
                                        .tag(context)
                                        .into(views, R.id.image, new int[]{appWidgetId});

                                // Instruct the widget manager to update the widget
                                final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                                appWidgetManager.updateAppWidget(appWidgetId, views);
                            }
                        });
                    }
                }

            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            isViewerUpdateInProgress = true;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            isViewerUpdateInProgress = false;
        }
    }


    protected void runOnMainThread(Runnable runnable) {
        mainThreadHandler.post(runnable);
    }

}

