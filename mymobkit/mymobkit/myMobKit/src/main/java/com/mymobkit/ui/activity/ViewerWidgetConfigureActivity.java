package com.mymobkit.ui.activity;

import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mymobkit.R;
import com.mymobkit.app.AppController;
import com.mymobkit.common.AppPreference;
import com.mymobkit.common.ToastUtils;
import com.mymobkit.model.Device;
import com.mymobkit.model.DeviceInfo;
import com.mymobkit.model.ResponseCode;
import com.mymobkit.ui.widget.ViewerWidget;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.LOGI;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * The configuration screen for the {@link ViewerWidget ViewerWidget} AppWidget.
 */
public class ViewerWidgetConfigureActivity extends AppCompatActivity {

    private static final String TAG = makeLogTag(ViewerWidgetConfigureActivity.class);

    // Primary toolbar and drawer toggle
    private Toolbar actionBarToolbar;

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Intent resultValue;

    private Spinner appWidgetDeviceName;

    public static final String PREFS_NAME = "com.mymobkit.ui.widget.ViewerWidget";
    public static final String PREF_PREFIX_KEY = "appwidget_";

    public static final String PREF_KEY_DEVICE_NAME = "device_name";
    public static final String PREF_KEY_DEVICE_ID = "device_id";

    private DeviceInfo deviceInfo = new DeviceInfo();
    private static AsyncHttpClient httpClient = new AsyncHttpClient();

    public ViewerWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.viewer_widget_configure);
        setTitle(getDefaultTitle());
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeButtonEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
        }

        appWidgetDeviceName = (Spinner) findViewById(R.id.appwidget_device_name);
        final Button saveButton = (Button) findViewById(R.id.save);
        saveButton.setOnClickListener(onClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // make the result intent and set the result to canceled
        resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED, resultValue);

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = ViewerWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally

            String deviceName = (String) appWidgetDeviceName.getSelectedItem();
            savePref(context, appWidgetId, PREF_KEY_DEVICE_NAME, deviceName);

            String deviceId = deviceInfo.getDevices().get(appWidgetDeviceName.getSelectedItemPosition()).getDeviceId();
            savePref(context, appWidgetId, PREF_KEY_DEVICE_ID, deviceId);

            // It is the responsibility of the configuration activity to update the app widget
            //AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            //ViewerWidget.updateAppWidget(context, appWidgetManager, appWidgetId);

            // Send a broadcast to update the specific widget by passing in the widget id in the intent
            Intent intent = new Intent(context, ViewerWidget.class);
            intent.setAction(ViewerWidget.ACTION_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            sendBroadcast(intent);


            // Make sure we pass back the original appWidgetId
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };


    public static void savePref(Context context, int appWidgetId, String key, String text) {
        AppPreference.getInstance().setValue(PREFS_NAME, PREF_PREFIX_KEY + appWidgetId + "_" + key, text);
    }

    public static String loadPref(Context context, int appWidgetId, String key, String defaultValue) {
        return AppPreference.getInstance().getValue(PREFS_NAME, PREF_PREFIX_KEY + appWidgetId + "_" + key, defaultValue);
    }

    public static void deletePref(Context context, int appWidgetId, String key) {
        AppPreference.getInstance().remove(PREFS_NAME, PREF_PREFIX_KEY + appWidgetId + "_" + key);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupToolbar();

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.msg_title_wait));
        progressDialog.setMessage(getString(R.string.msg_retrieving_devices));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.label_dialog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    dialog.dismiss();
                    finish();
                } catch (Exception ex) {
                }
            }
        });
        progressDialog.show();
        final String url = AppController.getConnectedDevicesUrl(this);
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
                                    final List<Device> devices = deviceInfo.getDevices();
                                    List<String> deviceNames = new ArrayList<String>(1);
                                    for (final Device device : devices) {
                                        deviceNames.add(device.getDeviceName());
                                    }

                                    if (deviceNames.size() > 0) {
                                        appWidgetDeviceName.setAdapter(new ArrayAdapter<String>
                                                (ViewerWidgetConfigureActivity.this, android.R.layout.simple_spinner_item, deviceNames));
                                        appWidgetDeviceName.setSelection(0);
                                    }
                                } else {
                                    ToastUtils.toastLong(ViewerWidgetConfigureActivity.this, R.string.msg_error_retrieving_devices);
                                    finish();
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
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    try {
                        progressDialog.dismiss();
                    } catch (Exception ex) {
                    }
                    ToastUtils.toastLong(ViewerWidgetConfigureActivity.this, R.string.msg_error_retrieving_devices);
                    finish();
                }

            });
        } else {
            try {
                progressDialog.dismiss();
            } catch (Exception ex) {
            }
            ToastUtils.toastLong(this, R.string.msg_error_retrieving_devices);
            finish();
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
    }

    private void setupToolbar() {

        if (actionBarToolbar != null) {
            actionBarToolbar.setNavigationIcon(R.drawable.ic_launcher);
        }
    }

    protected Toolbar getActionBarToolbar() {
        if (actionBarToolbar == null) {
            actionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (actionBarToolbar != null) {
                setSupportActionBar(actionBarToolbar);
            }
        }
        return actionBarToolbar;
    }

    protected String getDefaultTitle() {
        return getString(R.string.label_configure_viewer);
    }
}

