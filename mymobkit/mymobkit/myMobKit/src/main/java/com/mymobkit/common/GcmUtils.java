package com.mymobkit.common;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Looper;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.mymobkit.BuildConfig;
import com.mymobkit.R;
import com.mymobkit.app.AppConfig;
import com.mymobkit.app.AppController;
import com.mymobkit.enums.MyMobKitEnumAsInt;
import com.mymobkit.gcm.GcmMessage;
import com.mymobkit.model.DeviceStatus;
import com.mymobkit.model.ResponseCode;
import com.mymobkit.model.SendStatus;
import com.mymobkit.ui.fragment.ServiceSettingsFragment;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicInteger;

import cz.msebera.android.httpclient.Header;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.LOGI;
import static com.mymobkit.common.LogUtils.makeLogTag;

public final class GcmUtils {

    public static final String PARAMETER_REG_ID = "registrationId";
    public static final String PARAMETER_REG_VERSION = "registrationVersion";
    public static final String PARAMETER_EMAIL = "email";
    public static final String PARAMETER_DEVICE_ID = "deviceId";
    public static final String PARAMETER_DEVICE_NAME = "deviceName";
    public static final String PARAMETER_ACTION = "action";
    public static final String PARAMETER_EXTRA_DATA = "extraData";
    public static final String PARAMETER_APP_VERSION = "appVersion";
    public static final String PARAMETER_REGISTRATION_STATUS = "registrationStatus";

    public enum RegistrationStatus implements MyMobKitEnumAsInt {
        UNREGISTERED(0), REGISTERED(1);

        private int hashCode;

        RegistrationStatus(int hashCode) {
            this.hashCode = hashCode;
        }

        public int getHashCode() {
            return this.hashCode;
        }

        public static RegistrationStatus get(final int hashCode) {
            if (UNREGISTERED.getHashCode() == hashCode) {
                return UNREGISTERED;
            } else if (REGISTERED.getHashCode() == hashCode) {
                return REGISTERED;
            } else {
                return UNREGISTERED;
            }
        }
    }

    private static final String TAG = makeLogTag(GcmUtils.class);

    private static AtomicInteger messageId = new AtomicInteger();

    private static AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    private static SyncHttpClient syncHttpClient = new SyncHttpClient();

    /**
     * Generate GCM message id.
     *
     * @return Message id.
     */
    public static String generateMessageId() {
        return Integer.toString(messageId.incrementAndGet());
    }

    /**
     * GCM sender id. The project number in AppEngine.
     *
     * @return Sender id.
     */
    public static String getGcmSenderId() {
        if (BuildConfig.DEBUG) {
            return AppConfig.SENDER_ID_DEBUG;
        } else {
            return AppConfig.SENDER_ID_PRODUCTION;
        }
    }

    public static String getRegistrationServerUrl() {
        return (getHostingServerUrl() + AppController.getContext().getString(R.string.app_register_request_url));
    }

    public static String getMessageQueueServerUrl() {
        return (getHostingServerUrl() + AppController.getContext().getString(R.string.app_queue_msg_request_url));
    }

    private static String getHostingServerUrl() {
        String serverUrl;
        if (BuildConfig.DEBUG) {
            serverUrl = AppController.getContext().getString(R.string.app_server_debug);
        } else {
            serverUrl = AppController.getContext().getString(R.string.app_server_prod);
        }
        return serverUrl;
    }

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p/>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing registration ID.
     */
    public static String getRegistrationId(final Context context) {
        String registrationId = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, GcmUtils.PARAMETER_REG_ID, "");
        if (registrationId.isEmpty()) {
            LOGI(TAG, "GCM registration id not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing regID is not guaranteed to work with the new app version.
        int registrationVersion = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, GcmUtils.PARAMETER_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registrationVersion != currentVersion) {
            LOGI(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    public static RegistrationStatus getRegistrationStatus(final Context context) {
        try {
            String status = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, GcmUtils.PARAMETER_REGISTRATION_STATUS, "");
            if (status.isEmpty()) {
                LOGI(TAG, "Server registration status not found.");
                return RegistrationStatus.UNREGISTERED;
            }
            return RegistrationStatus.get(Integer.parseInt(status));
        } catch (Exception ex) {
            LOGE(TAG, "[getRegistrationStatus] Failed to get server registration status");
        }
        return RegistrationStatus.UNREGISTERED;
    }

    public static boolean saveRegistrationStatus(Context context, RegistrationStatus regStatus) {
        try {
            AppPreference.getInstance().setValue(ServiceSettingsFragment.SHARED_PREFS_NAME, GcmUtils.PARAMETER_REGISTRATION_STATUS, String.valueOf(regStatus.getHashCode()));
        } catch (Exception ex) {
            LOGE(TAG, "Unable to save server registration status", ex);
            return false;
        }
        return true;
    }

    /**
     * Stores the registration ID and the app versionCode in the application's {@code SharedPreferences}.
     *
     * @param context  application's context.
     * @param gcmRegId registration ID
     * @return
     */
    public static boolean saveRegistrationId(Context context, String gcmRegId) {
        if (TextUtils.isEmpty(gcmRegId)) return false;
        int appVersion = getAppVersion(context);
        try {
            AppPreference.getInstance().setValue(ServiceSettingsFragment.SHARED_PREFS_NAME, GcmUtils.PARAMETER_REG_ID, gcmRegId);
            AppPreference.getInstance().setValue(ServiceSettingsFragment.SHARED_PREFS_NAME, GcmUtils.PARAMETER_APP_VERSION, appVersion);
        } catch (Exception ex) {
            LOGE(TAG, "Unable to save GCM registration information", ex);
            return false;
        }
        LOGI(TAG, "Saved gcmRegId " + gcmRegId + " on app version " + appVersion);
        return true;
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(final Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Register this device to the your own hosted GCM server.
     *
     * @param context
     * @return
     */
    public static boolean registerToGcmServer(final Context context) {
        // Registration id, app version, email and device id
        final AppPreference prefs = AppPreference.getInstance();
        final String registrationId = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, GcmUtils.PARAMETER_REG_ID, "");
        final int registrationVersion = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, GcmUtils.PARAMETER_APP_VERSION, Integer.MIN_VALUE);
        final String email = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_DEVICE_EMAIL_ADDRESS, "");
        final String deviceName = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_DEVICE_UNIQUE_NAME, AppController.getDeviceName());
        final String deviceId = DeviceUtils.getDeviceId(context);
        if (registrationId.isEmpty() || registrationVersion < 0 || email.isEmpty() || deviceId == null) {
            LOGE(TAG, "[registerToGcmServer] Unable to register. Information is not complete.");
            LOGE(TAG, "Registration id: " + registrationId);
            LOGE(TAG, "Registration version: " + registrationVersion);
            LOGE(TAG, "Email: " + email);
            LOGE(TAG, "Device ID: " + deviceId);
            return false;
        }
        try {

            RequestParams params = new RequestParams();
            params.put(PARAMETER_REG_ID, registrationId);
            params.put(PARAMETER_REG_VERSION, String.valueOf(registrationVersion));
            params.put(PARAMETER_EMAIL, email);
            params.put(PARAMETER_DEVICE_ID, deviceId);
            params.put(PARAMETER_DEVICE_NAME, deviceName);

            // final JSONObject jsonParams = new JSONObject();
            // jsonParams.put("registrationId", registrationId);
            // jsonParams.put("registrationVersion", registrationVersion);
            // jsonParams.put("email", email);
            // jsonParams.put("deviceId", deviceId);
            // final StringEntity entity = new StringEntity(jsonParams.toString());

            // asyncHttpClient.post(context, getRegistrationServerUrl(), entity, MimeType.JSON, new AsyncHttpResponseHandler() {
            asyncHttpClient.post(getRegistrationServerUrl(), params, new AsyncHttpResponseHandler(true) {

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    try {

                        final String response = responseBody == null ? null : new String(responseBody, this.getCharset());

                        LOGE(TAG, "Failed to register to server. Response received: " + response, error);

                        // Show failure message if not in stealth mode
                        //final String errorMsg = (e != null ? e.getMessage() : null);

                        // Not registered
                        saveRegistrationStatus(context, RegistrationStatus.UNREGISTERED);

                    } catch (UnsupportedEncodingException e1) {
                        LOGE(TAG, "[onFailure] Unsupported charset", e1);
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        final String response = responseBody == null ? null : new String(responseBody, this.getCharset());
                        LOGI(TAG, "Post status " + response);
                        if (!TextUtils.isEmpty(response)) {
                            try {
                                final DeviceStatus status = new Gson().fromJson(response, DeviceStatus.class);
                                if (status != null && status.getResponseCode() == ResponseCode.DEVICE_REGISTERED.getCode()) {
                                    // Registered
                                    saveRegistrationStatus(context, RegistrationStatus.REGISTERED);
                                }
                            } catch (Exception ex) {
                                LOGE(TAG, "[onSuccess] Unable to check response", ex);
                            }
                        }
                    } catch (UnsupportedEncodingException e1) {
                        LOGE(TAG, "[onSuccess] Unsupported charset", e1);
                    }
                }
            });
        } catch (Exception ex) {
            LOGE(TAG, "[registerToGcmServer] Failed to register to server", ex);
            return false;
        }
        return true;
    }


    /**
     * Register this device to the your own hosted GCM server.
     *
     * @param context
     * @return
     */
    public static boolean syncRegisterToGcmServer(final Context context) {
        // Registration id, app version, email and device id
        final AppPreference prefs = AppPreference.getInstance();
        final String registrationId = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, GcmUtils.PARAMETER_REG_ID, "");
        final int registrationVersion = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, GcmUtils.PARAMETER_APP_VERSION, Integer.MIN_VALUE);
        final String email = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_DEVICE_EMAIL_ADDRESS, "");
        final String deviceName = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_DEVICE_UNIQUE_NAME, AppController.getDeviceName());
        final String deviceId = DeviceUtils.getDeviceId(context);
        if (registrationId.isEmpty() || registrationVersion < 0 || email.isEmpty() || deviceId == null) {
            LOGE(TAG, "[syncRegisterToGcmServer] Unable to register. Information is not complete.");
            return false;
        }
        try {

            RequestParams params = new RequestParams();
            params.put(PARAMETER_REG_ID, registrationId);
            params.put(PARAMETER_REG_VERSION, String.valueOf(registrationVersion));
            params.put(PARAMETER_EMAIL, email);
            params.put(PARAMETER_DEVICE_ID, deviceId);
            params.put(PARAMETER_DEVICE_NAME, deviceName);

            syncHttpClient.post(getRegistrationServerUrl(), params, new AsyncHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    try {

                        final String response = responseBody == null ? null : new String(responseBody, this.getCharset());

                        LOGE(TAG, "Failed to register to server. Response received: " + response, error);

                        // Show failure message if not in stealth mode
                        //final String errorMsg = (e != null ? e.getMessage() : null);

                        // Not registered
                        saveRegistrationStatus(context, RegistrationStatus.UNREGISTERED);

                    } catch (UnsupportedEncodingException e1) {
                        LOGE(TAG, "[onFailure] Unsupported charset", e1);
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        final String response = responseBody == null ? null : new String(responseBody, this.getCharset());
                        LOGI(TAG, "Post status " + response);
                        if (!TextUtils.isEmpty(response)) {
                            try {
                                final DeviceStatus status = new Gson().fromJson(response, DeviceStatus.class);
                                if (status != null && status.getResponseCode() == ResponseCode.DEVICE_REGISTERED.getCode()) {
                                    // Registered
                                    saveRegistrationStatus(context, RegistrationStatus.REGISTERED);
                                }
                            } catch (Exception ex) {
                                LOGE(TAG, "[onSuccess] Unable to check response", ex);
                            }
                        }
                    } catch (UnsupportedEncodingException e1) {
                        LOGE(TAG, "[onSuccess] Unsupported charset", e1);
                    }
                }
            });
        } catch (Exception ex) {
            LOGE(TAG, "[syncRegisterToGcmServer] Failed to register to server", ex);
            return false;
        }
        return true;
    }

    public static void send(final String deviceId, final GcmMessage message) {
        try {
            // Set device id
            RequestParams params = new RequestParams();
            params.put(PARAMETER_REG_VERSION, message.getRegistrationVersion());
            params.put(PARAMETER_REG_ID, message.getRegistrationId());
            params.put(PARAMETER_EMAIL, message.getEmail());
            params.put(PARAMETER_ACTION, message.getAction());
            params.put(PARAMETER_EXTRA_DATA, message.toJson());
            params.put(PARAMETER_DEVICE_ID, deviceId);

            asyncHttpClient.post(getMessageQueueServerUrl(), params, new AsyncHttpResponseHandler(true) {

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    try {
                        final String response = responseBody == null ? null : new String(responseBody, this.getCharset());
                        LOGE(TAG, "Failed to queue to server. Response received: " + response, error);
                    } catch (UnsupportedEncodingException e1) {
                        LOGE(TAG, "[onFailure] Unsupported charset", e1);
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        final String response = responseBody == null ? null : new String(responseBody, this.getCharset());
                        LOGI(TAG, "Post status " + response);
                        if (!TextUtils.isEmpty(response)) {
                            try {
                                final SendStatus status = new Gson().fromJson(response, SendStatus.class);
                                if (status != null) {
                                }
                            } catch (Exception ex) {
                                LOGE(TAG, "[onSuccess] Unable to check response", ex);
                            }
                        }
                    } catch (UnsupportedEncodingException e1) {
                        LOGE(TAG, "[onSuccess] Unsupported charset", e1);
                    }
                }
            });
        } catch (Exception ex) {
            LOGE(TAG, "[send] Failed to queue message", ex);
        }
    }

    public static void broadcast(final GcmMessage message) {
        try {
            // Do not set device id. Email is used to broadcast to devices under the same email
            RequestParams params = new RequestParams();
            params.put(PARAMETER_REG_VERSION, message.getRegistrationVersion());
            params.put(PARAMETER_REG_ID, message.getRegistrationId());
            params.put(PARAMETER_EMAIL, message.getEmail());
            params.put(PARAMETER_ACTION, message.getAction());
            params.put(PARAMETER_EXTRA_DATA, message.toJson());

            asyncHttpClient.post(getMessageQueueServerUrl(), params, new AsyncHttpResponseHandler(true) {

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    try {
                        final String response = responseBody == null ? null : new String(responseBody, this.getCharset());
                        LOGE(TAG, "Failed to queue to server. Response received: " + response, error);
                    } catch (UnsupportedEncodingException e1) {
                        LOGE(TAG, "[onFailure] Unsupported charset", e1);
                    }
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        final String response = responseBody == null ? null : new String(responseBody, this.getCharset());
                        LOGI(TAG, "Post status " + response);
                        if (!TextUtils.isEmpty(response)) {
                            try {
                                final SendStatus status = new Gson().fromJson(response, SendStatus.class);
                                if (status != null) {
                                    //AppController.bus.post(status);
                                }
                            } catch (Exception ex) {
                                LOGE(TAG, "[onSuccess] Unable to check response", ex);
                                //AppController.bus.post(new SendStatus());
                            }
                        }
                    } catch (UnsupportedEncodingException e1) {
                        LOGE(TAG, "[onSuccess] Unsupported charset", e1);
                        //AppController.bus.post(new SendStatus());
                    }
                }
            });
        } catch (Exception ex) {
            LOGE(TAG, "[broadcast] Failed to queue message", ex);

            // Post to event bus
            //AppController.bus.post(new SendStatus());
        }
    }

    /**
     * @return an async client when calling from the main thread, otherwise a sync client.
     */
    private static AsyncHttpClient getHttpClient() {
        // Return the synchronous HTTP client when the thread is not prepared
        if (Looper.myLooper() == null)
            return syncHttpClient;
        return asyncHttpClient;
    }

    public static void broadcastHeartBeat(final Context context) {
        if (context == null) return;
        context.sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
        context.sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));
    }

    /*public static boolean triggerGcmHeartBeat() {
        final Context context = AppController.getContext();
        if (context == null) return false;
        boolean alarmUp = (PendingIntent.getBroadcast(context, 0, new Intent(context, GcmHeartBeatReceiver.class), PendingIntent.FLAG_NO_CREATE) != null);

        if (!alarmUp) {
            GcmHeartBeatReceiver gcmHeartBeatReceiver = new GcmHeartBeatReceiver();
            gcmHeartBeatReceiver.setAlarm(context);
            return true;
        }
        return false;
    }*/
}
