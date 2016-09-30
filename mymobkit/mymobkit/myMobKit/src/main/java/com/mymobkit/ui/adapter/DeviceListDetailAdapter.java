package com.mymobkit.ui.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mymobkit.R;
import com.mymobkit.app.AppConfig;
import com.mymobkit.app.AppController;
import com.mymobkit.common.AppPreference;
import com.mymobkit.common.MimeType;
import com.mymobkit.common.ToastUtils;
import com.mymobkit.model.Device;
import com.mymobkit.model.DeviceInfo;
import com.mymobkit.model.ResponseCode;
import com.mymobkit.service.api.drive.DriveFileInfo;
import com.mymobkit.service.api.drive.HousekeepFileAsyncTask;
import com.mymobkit.service.api.drive.ListFileSyncTask;
import com.mymobkit.ui.fragment.PhotoListViewFragment;
import com.mymobkit.ui.fragment.ServiceSettingsFragment;
import com.mymobkit.ui.handler.DriveRequestHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.SocketTimeoutException;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.mymobkit.common.LogUtils.LOGD;
import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.LOGI;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Adapter to display connected devices.
 */
public class DeviceListDetailAdapter extends BaseAdapter {

    private static final String TAG = makeLogTag(DeviceListDetailAdapter.class);

    private final Context context;
    private DeviceInfo deviceInfo = new DeviceInfo();
    private static AsyncHttpClient httpClient = new AsyncHttpClient();
    private final boolean isDeviceTrackingEnabled;
    protected Handler mainThreadHandler;
    private ListView listView;
    private Activity activity;
    private final Picasso picasso;

    public DeviceListDetailAdapter(final Activity activity, final Context context, final ListView listView) {

        // Added Oct 8th
        AsyncHttpClient.allowRetryExceptionClass(IOException.class);
        AsyncHttpClient.allowRetryExceptionClass(SocketTimeoutException.class);
        AsyncHttpClient.allowRetryExceptionClass(ConnectTimeoutException.class);

        this.context = context;
        this.listView = listView;
        this.activity = activity;
        this.picasso = new Picasso.Builder(context)
                .addRequestHandler(new DriveRequestHandler(context))
                .build();

        // Check if we need to connect to Google Drive
        isDeviceTrackingEnabled = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_DEVICE_TRACKING, Boolean.valueOf(context.getString(R.string.default_device_tracking)));
        mainThreadHandler = new Handler(Looper.getMainLooper());

        //final ProgressDialog progressDialog = ProgressDialog.show(context, context.getString(R.string.msg_title_wait), context.getString(R.string.msg_retrieving_devices), true);
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(context.getString(R.string.msg_title_wait));
        progressDialog.setMessage(context.getString(R.string.msg_retrieving_devices));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.label_dialog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    dialog.dismiss();
                } catch (Exception ex) {
                }
            }
        });
        progressDialog.show();

        final String url = AppController.getConnectedDevicesUrl(context);
        if (!TextUtils.isEmpty(url)) {

            // Retrieve the devices from our server
            httpClient.get(url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    // called when response HTTP status is "200 OK"
                    try {
                        final String response = responseBody == null ? null : new String(responseBody, this.getCharset());
                        LOGI(TAG, "Post status " + response);
                        if (!TextUtils.isEmpty(response)) {
                            try {
                                deviceInfo = new Gson().fromJson(response, DeviceInfo.class);
                                if (deviceInfo != null && deviceInfo.getResponseCode() == ResponseCode.SUCCESS.getCode()) {

                                    // Manage to retrieve devices
                                    DeviceListDetailAdapter.this.notifyDataSetChanged();


                                    // Check if there is a need to trigger housekeeping task
                                    if ((System.currentTimeMillis() - AppController.lastDriveHousekeepTime) > AppConfig.HOUSEKEEP_INTERVAL) {

                                        // Keep last housekeep time
                                        AppController.lastDriveHousekeepTime = System.currentTimeMillis();

                                        // Trigger housekeeping task
                                        HousekeepingTask housekeepingTask = new HousekeepingTask();
                                        housekeepingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, deviceInfo);
                                    }

                                } else {
                                    ToastUtils.toastLong(context, R.string.msg_error_retrieving_devices);
                                }
                            } catch (Exception ex) {
                                LOGE(TAG, "[onSuccess] Unable to check response", ex);
                            }
                        }
                    } catch (UnsupportedEncodingException e1) {
                        LOGE(TAG, "[onSuccess] Unsupported charset", e1);
                    } finally {
                        try {
                            progressDialog.dismiss();
                        } catch (Exception ex) {
                        }
                    }

                    if (!isDeviceTrackingEnabled) {
                        ToastUtils.toastLong(context, R.string.msg_device_tracking_not_enabled);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    try {
                        progressDialog.dismiss();
                    } catch (Exception ex) {
                    }
                    ToastUtils.toastLong(context, R.string.msg_error_retrieving_devices);
                }

            });
        } else {
            try {
                progressDialog.dismiss();
            } catch (Exception ex) {
            }
            ToastUtils.toastLong(context, R.string.msg_error_retrieving_devices);
        }
    }

    protected void runOnMainThread(Runnable runnable) {
        mainThreadHandler.post(runnable);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.device_list_details_item, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) view.findViewById(R.id.image);
            holder.name = (TextView) view.findViewById(R.id.name);
            //holder.id = (TextView) view.findViewById(R.id.id);
            holder.version = (TextView) view.findViewById(R.id.version);
            holder.overFlowMenu = (ImageView) view.findViewById(R.id.overflow_menu);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // Get the device info for the current position.
        final Device device = DeviceListDetailAdapter.this.getItem(position);
        holder.name.setText(device.getDeviceName());
        //holder.id.setText(device.getDeviceId());
        holder.version.setText(String.format(context.getString(R.string.title_version), device.getRegVersion()));
        holder.overFlowMenu.setOnClickListener(new OnOverflowSelectedListener(context, device, this));

        if (isDeviceTrackingEnabled) {
            new AsyncTask<ViewHolder, Void, Boolean>() {
                @Override
                public Boolean doInBackground(ViewHolder... args) {
                    final ViewHolder holder = args[0];
                    // Show the latest image
                    final ListFileSyncTask task = new ListFileSyncTask(context);
                    final List<DriveFileInfo> fileInfos = task.execute(device.getDeviceId(), MimeType.IMAGE_JPEG, "1");
                    if (fileInfos != null && fileInfos.size() > 0) {
                        final DriveFileInfo fileInfo = fileInfos.get(0);
                        if (!TextUtils.isEmpty(fileInfo.getDriveId())) {
                            runOnMainThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Get the device latest image
                                    picasso.load(DriveRequestHandler.SCHEMA_DRIVE + ":" + fileInfo.getDriveId())
                                            .placeholder(R.drawable.placeholder)
                                            .error(R.drawable.placeholder)
                                                    //.resizeDimen(R.dimen.list_detail_image_size_width, R.dimen.list_detail_image_size_height)
                                            .centerInside()
                                            .tag(context)
                                            .fit()
                                            .into(holder.image, new com.squareup.picasso.Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    LOGD(TAG, "success");
                                                }

                                                @Override
                                                public void onError() {
                                                    LOGD(TAG, "error");
                                                }


                                            });
                                }
                            });
                        }
                    }
                    return true;
                }

                @Override
                protected void onPostExecute(Boolean result) {

                }
            }.execute(holder);
        }

        return view;
    }

    @Override
    public int getCount() {
        return deviceInfo.getDevices().size();
    }

    @Override
    public Device getItem(int position) {
        return deviceInfo.getDevices().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getPosition(final Device currentDevice) {
        for (int i = 0; i < deviceInfo.getDevices().size(); i++) {
            final Device device = deviceInfo.getDevices().get(i);
            if (device.getDeviceId().equalsIgnoreCase(currentDevice.getDeviceId())) {
                return i;
            }
        }
        return 0;
    }

    static class ViewHolder {
        ImageView image;
        TextView name;
        TextView id;
        TextView version;
        ImageView overFlowMenu;
    }

    public class OnOverflowSelectedListener implements View.OnClickListener {
        private Device device;
        private Context context;
        private DeviceListDetailAdapter listAdapter;

        public OnOverflowSelectedListener(final Context context, final Device device, final DeviceListDetailAdapter listAdapter) {
            this.context = context;
            this.device = device;
            this.listAdapter = listAdapter;
        }

        @Override
        public void onClick(View v) {

            if (!isDeviceTrackingEnabled) {
                ToastUtils.toastLong(context, R.string.msg_device_tracking_not_enabled);
                return;
            }

            // Dim out all the other list items if they exist
            int firstPos = listView.getFirstVisiblePosition() - listView.getHeaderViewsCount();
            final int ourPos = listAdapter.getPosition(device) - firstPos;
            int count = listView.getChildCount();
            for (int i = 0; i <= count; i++) {
                if (i == ourPos) {
                    final View child = listView.getChildAt(i);
                    if (child != null) {
                        // http://stackoverflow.com/questions/31590714/getcolorint-id-deprecated-on-android-6-0-marshmallow-api-23
                        child.setBackgroundColor(ContextCompat.getColor(context, R.color.light_gray));
                    }
                    break;
                }
            }

            final PopupMenu popupMenu = new PopupMenu(context, v) {
                @Override
                public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.photo_browser:
                            activity.getFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.fragment_content, PhotoListViewFragment.newInstance(context, device.getDeviceId()))
                                    .addToBackStack(null)
                                    .commit();
                            return true;
                       /* case R.id.live_view:
                            // Check if GTalk is active
                            if (HttpdService.isGTalkActive()) {
                                activity.getFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.fragment_content, LiveViewFragment.newInstance(context, device.getDeviceId()))
                                        .addToBackStack(null)
                                        .commit();
                            } else {
                                // Show the message on the display
                                ToastUtils.toastLong(context, R.string.msg_live_view_not_available );
                            }
                            return true;*/
                        default:
                            return super.onMenuItemSelected(menu, item);
                    }
                }
            };
            popupMenu.inflate(R.menu.menu_device);

            // Make sure to bring them back to normal after the menu is gone
            popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
                @Override
                public void onDismiss(PopupMenu popupMenu) {
                    int count = listView.getChildCount();
                    for (int i = 0; i <= count; i++) {
                        if (i == ourPos) {
                            final View v = listView.getChildAt(i);
                            if (v != null) {
                                v.setBackgroundColor(Color.WHITE);
                            }
                        }
                    }
                }
            });

            // Force icons to show
            Object menuHelper;
            Class[] argTypes;
            try {
                Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
                fMenuHelper.setAccessible(true);
                menuHelper = fMenuHelper.get(popupMenu);
                argTypes = new Class[]{boolean.class};
                menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
            } catch (Exception e) {
                // Possible exceptions are NoSuchMethodError and NoSuchFieldError
                //
                // In either case, an exception indicates something is wrong with the reflection code, or the
                // structure of the PopupMenu class or its dependencies has changed.
                //
                // These exceptions should never happen since we're shipping the AppCompat library in our own apk,
                // but in the case that they do, we simply can't force icons to display, so log the error and
                // show the menu normally.
                popupMenu.show();
                return;
            }

            // Try to force some horizontal offset
            /*try {
                Field fListPopup = menuHelper.getClass().getDeclaredField("mPopup");
                fListPopup.setAccessible(true);
                Object listPopup = fListPopup.get(menuHelper);
                argTypes = new Class[]{int.class};
                Class listPopupClass = listPopup.getClass();

                // Get the width of the popup window
                int width = (Integer) listPopupClass.getDeclaredMethod("getWidth").invoke(listPopup);

                // Invoke setHorizontalOffset() with the negative width to move left by that distance
                listPopupClass.getDeclaredMethod("setHorizontalOffset", argTypes).invoke(listPopup, -width);

                // Invoke show() to update the window's position
                listPopupClass.getDeclaredMethod("show").invoke(listPopup);
            } catch (Exception e) {
                // Again, an exception here indicates a programming error rather than an exceptional condition
                // at runtime
                LOGW(TAG, "Unable to force offset", e);
            }*/

            popupMenu.show();
        }
    }

    /**
     * Housekeeping task.
     */
    class HousekeepingTask extends AsyncTask<DeviceInfo, Void, Boolean> {

        @Override
        protected Boolean doInBackground(DeviceInfo... deviceInfos) {
            final DeviceInfo deviceInfo = deviceInfos[0];
            if (deviceInfo.getDevices() == null || deviceInfo.getDevices().size() == 0) {
                return false;
            }
            for (final Device device : deviceInfo.getDevices()) {
                runOnMainThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                new HousekeepFileAsyncTask(context).execute(device.getDeviceId(), MimeType.IMAGE_JPEG);
                            }
                        });

            }
            return true;
        }
    }
}
