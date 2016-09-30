package com.mymobkit.service;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.mymobkit.R;
import com.mymobkit.app.AppConfig;
import com.mymobkit.app.AppController;
import com.mymobkit.camera.CameraController;
import com.mymobkit.camera.CameraControllerManager;
import com.mymobkit.camera.CameraSize;
import com.mymobkit.common.AppPreference;
import com.mymobkit.common.MimeType;
import com.mymobkit.common.NetworkUtils;
import com.mymobkit.common.Notifier;
import com.mymobkit.common.ServiceUtils;
import com.mymobkit.common.StringUtils;
import com.mymobkit.common.ValidationUtils;
import com.mymobkit.enums.AddressFamily;
import com.mymobkit.enums.CameraFocusMode;
import com.mymobkit.google.RetrieveFileSyncTask;
import com.mymobkit.model.ActionStatus;
import com.mymobkit.model.Surveillance;
import com.mymobkit.net.AppServer;
import com.mymobkit.net.ControlPanelService;
import com.mymobkit.net.provider.Processor;
import com.mymobkit.receiver.NetworkConnectivityListener;
import com.mymobkit.service.api.CallApiHandler;
import com.mymobkit.service.api.ContactApiHandler;
import com.mymobkit.service.api.DriveApiHandler;
import com.mymobkit.service.api.GcmApiHandler;
import com.mymobkit.service.api.LocationApiHandler;
import com.mymobkit.service.api.MediaApiHandler;
import com.mymobkit.service.api.MessagingApiHandler;
import com.mymobkit.service.api.MessagingStatusApiHandler;
import com.mymobkit.service.api.MmsApiHandler;
import com.mymobkit.service.api.ParameterApiHandler;
import com.mymobkit.service.api.SensorApiHandler;
import com.mymobkit.service.api.StatusApiHandler;
import com.mymobkit.service.api.UssdApiHandler;
import com.mymobkit.service.api.drive.HousekeepFileAsyncTask;
import com.mymobkit.service.api.media.MediaAudio;
import com.mymobkit.service.api.media.MediaManager;
import com.mymobkit.service.api.media.MediaVideo;
import com.mymobkit.service.api.vCalendarApiHandler;
import com.mymobkit.service.api.vCardApiHandler;
import com.mymobkit.ui.fragment.DetectionSettingsFragment;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.LOGI;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Android HTTPD service.
 */
public final class HttpdService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = makeLogTag(HttpdService.class);

    private static int ONGOING_NOTIFICATION_ID = 1688;

    protected ControlPanelService httpServer;
    protected boolean hasError;
    protected String status;
    protected String uri;
    protected int controlPanelPort;
    protected boolean disableNotification;
    protected boolean isUseIPv4;

    private PowerManager powerMgr;
    private PowerManager.WakeLock wakeLock;

    private NetworkConnectivityListener networkStateListener;
    private String currentIPAddress = "";

    // protected Component component = null;
    private MessagingApiHandler smsApiHandler;
    private MessagingStatusApiHandler smsStatusApiHandler;
    private ParameterApiHandler parameterApiHandler;
    private MediaApiHandler mediaApiHandler;
    private MediaManager mediaUtils;
    private StatusApiHandler statusApiHandler;
    private ContactApiHandler contactApiHandler;
    private MmsApiHandler mmsApiHandler;
    private vCardApiHandler vCardApiHandler;
    private vCalendarApiHandler vCalendarApiHandler;
    private LocationApiHandler locationApiHandler;
    private SensorApiHandler sensorApiHandler;
    private GcmApiHandler gcmApiHandler;
    private DriveApiHandler driveApiHandler;
    private UssdApiHandler ussdApiHandler;
    private CallApiHandler callApiHandler;

    private static final int WHAT_NETWORK_CHANGED = 5688;

    protected Handler networkStateHandler = new Handler() {

        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case WHAT_NETWORK_CHANGED:
                    try {
                        String newIPAddress = NetworkUtils.getLocalIpAddress(isUseIPv4);
                        if (!TextUtils.isEmpty(newIPAddress) && !newIPAddress.equals(currentIPAddress)) {
                            currentIPAddress = newIPAddress;
                            restartApp();

                            // Send network change
                            Intent intent = new Intent(AppConfig.INTENT_IP_ADDRESS_CHANGE_ACTION);
                            sendBroadcast(intent);

                            // Send the change for tracking
                            ServiceUtils.trackDevice(HttpdService.this, true, getUri());
                        }
                    } catch (Exception e) {
                        LOGE(TAG, "[handleMessage] Network change error", e);
                    }
                    break;
            }
        }
    };

    /*
     * Remote methods.
     */
    private final class HttpdServiceProvider extends IHttpdService.Stub {

        @Override
        public String getUri() throws RemoteException {
            return HttpdService.this.getUri();
        }

        @Override
        public boolean isAlive() throws RemoteException {
            if (httpServer != null && httpServer.isAlive()) {
                return true;
            }
            return false;
        }

        @Override
        public boolean isError() throws RemoteException {
            return hasError;
        }

        @Override
        public String getErrorMsg() throws RemoteException {
            return status;
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return new HttpdServiceProvider();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        powerMgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerMgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG + " WakeLock");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            LOGI(TAG, "[onDestroy] Stopping service");

            if (httpServer != null && httpServer.isAlive()) {
                httpServer.stop();
                httpServer = null;
            }

            // Stop all API handlers
            stopApiHandler();

            networkStateListener.unregisterHandler(networkStateHandler);
            networkStateListener.stopListening();

            if (!disableNotification) {
                // Stop service and remove notification
                stopForeground(true);
            }
        } catch (Exception ex) {
            LOGE(TAG, "[onDestroy] Error stopping service", ex);
        } finally {
            // Service is off
            serviceOff();

            // Untrack device
            ServiceUtils.trackDevice(this, false, getUri());

        }
    }

    private String getUri() {
        if (TextUtils.isEmpty(uri)) {
            uri = "http://" + NetworkUtils.getLocalIpAddress(isUseIPv4) + ":" + controlPanelPort;
        }
        return uri;
    }

    private void restartApp() {
        try {
            httpServer.stop();
            httpServer.start();
            this.uri = "http://" + currentIPAddress + ":" + controlPanelPort;
            LOGI(TAG, "[restartApp] HTTP server is restarted - " + uri);
            status = String.format(getContext().getString(R.string.notif_http_service), uri);

        } catch (Exception ex) {
            LOGE(TAG, "[restartApp] Error restarting service", ex);
            status = ex.getMessage();
            hasError = true;
        }

        // Stop notification
        if (!disableNotification) {
            // Stop service and remove notification
            stopForeground(true);
            final Notification notification = Notifier.buildNotification(getContext(), status);
            startForeground(ONGOING_NOTIFICATION_ID, notification);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        LOGI(TAG, "[onStartCommand] Starting myMobKit server");

        if (intent == null) {
            LOGI(TAG, "[onStartCommand] Intent is null");
            return Service.START_NOT_STICKY;
        }

        if (httpServer != null && httpServer.isAlive()) {
            return Service.START_REDELIVER_INTENT;
        }

        // Get HTTP listening port from the passed in intent
        controlPanelPort = intent.getIntExtra(AppConfig.CONTROL_PANEL_LISTENING_PORT_PARAM, Integer.parseInt(getContext().getString(com.mymobkit.R.string.default_control_panel_http_port)));
        boolean loginRequired = intent.getBooleanExtra(AppConfig.LOGIN_REQUIRED_PARAM, Boolean.valueOf(getContext().getString(com.mymobkit.R.string.default_login_required)));
        String loginUserName = intent.getStringExtra(AppConfig.LOGIN_USER_NAME_PARAM);
        String loginUserPassword = intent.getStringExtra(AppConfig.LOGIN_USER_PASSWORD_PARAM);
        disableNotification = intent.getBooleanExtra(AppConfig.DISABLE_NOTIFICATION, Boolean.valueOf(getContext().getString(com.mymobkit.R.string.default_disable_notification)));
        String primaryAddressFamily = intent.getStringExtra(AppConfig.PRIMARY_ADDRESS_FAMILY_PARAM);
        AddressFamily addressFamily = AddressFamily.IPv4;
        if (!TextUtils.isEmpty(primaryAddressFamily)) {
            addressFamily = AddressFamily.get(Integer.valueOf(primaryAddressFamily));
        }
        if (addressFamily == AddressFamily.IPv4)
            isUseIPv4 = true;
        else
            isUseIPv4 = false;

        // Instantiate the HTTP server
        httpServer = new ControlPanelService(null, controlPanelPort, getContext().getAssets());
        httpServer.setAuthenticationRequired(loginRequired);
        httpServer.addAuthorizedUser(loginUserName, loginUserPassword);
        try {

            // Register the services
            // httpServer.registerService("/services", serviceHelp);
            // httpServer.registerService("/services/", serviceHelp);
            httpServer.registerService("/services/surveillance/url", surveillanceUrl);
            httpServer.registerService("/services/surveillance/start", startSurveillance);
            httpServer.registerService("/services/surveillance/shutdown", shutdownSurveillance);
            httpServer.registerService("/services/surveillance/status", surveillanceStatus);

            // Messaging API
            httpServer.registerService("/services/api/messaging", smsProcessor);
            httpServer.registerService("/services/api/messaging/", smsProcessor);
            httpServer.registerService("/services/api/messaging/status", smsStatusProcessor);
            httpServer.registerService("/services/api/messaging/status/", smsStatusProcessor);

            // Parameter API
            httpServer.registerService("/services/api/parameter", parameterProcessor);
            httpServer.registerService("/services/api/parameter/", parameterProcessor);

            // Media API
            httpServer.registerService("/services/api/media", mediaProcessor);
            httpServer.registerService("/services/api/media/", mediaProcessor);

            httpServer.registerStreaming("/services/stream", mediaStreamingProcessor);
            httpServer.registerStreaming("/services/stream/", mediaStreamingProcessor);

            // Status API
            httpServer.registerService("/services/api/status", statusProcessor);
            httpServer.registerService("/services/api/status/", statusProcessor);

            // Contact API
            httpServer.registerService("/services/api/contact", contactProcessor);
            httpServer.registerService("/services/api/contact/", contactProcessor);

            // MMS API
            httpServer.registerService("/services/api/mms", mmsProcessor);
            httpServer.registerService("/services/api/mms/", mmsProcessor);

            // vCard API
            httpServer.registerService("/services/api/vcard", vCardProcessor);
            httpServer.registerService("/services/api/vcard/", vCardProcessor);

            // vCalendar API
            httpServer.registerService("/services/api/vcalendar", vCalendarProcessor);
            httpServer.registerService("/services/api/vcalendar/", vCalendarProcessor);

            // Location API
            httpServer.registerService("/services/api/location", locationProcessor);
            httpServer.registerService("/services/api/location/", locationProcessor);

            // Sensor API
            httpServer.registerService("/services/api/sensor", sensorProcessor);
            httpServer.registerService("/services/api/sensor/", sensorProcessor);

            // GCM API
            httpServer.registerService("/services/api/gcm", gcmProcessor);
            httpServer.registerService("/services/api/gcm/", gcmProcessor);

            // Drive API
            httpServer.registerService("/services/api/drive", driveProcessor);
            httpServer.registerService("/services/api/drive/", driveProcessor);

            // USSD  API
            httpServer.registerService("/services/api/ussd", ussdProcessor);
            httpServer.registerService("/services/api/ussd/", ussdProcessor);

            // Call  API
            httpServer.registerService("/services/api/call", callProcessor);
            httpServer.registerService("/services/api/call/", callProcessor);

            httpServer.start();

            // Log.d(TAG, "IP address use: " + httpServer.getIPAddress());

            this.currentIPAddress = NetworkUtils.getLocalIpAddress(isUseIPv4);
            this.uri = "http://" + currentIPAddress + ":" + controlPanelPort;
            LOGI(TAG, "[onStartCommand] HTTP server is started - " + uri);
            status = String.format(getContext().getString(R.string.notif_http_service), uri);

            // start messaging service
            // startAPIExplorer(MyMobKitApp.getContext(), restServicePort);

            startApiHandler();

            networkStateListener = new NetworkConnectivityListener();
            networkStateListener.startListening(this);
            networkStateListener.registerHandler(networkStateHandler, WHAT_NETWORK_CHANGED);

            hasError = false;
        } catch (Exception ex) {
            LOGE(TAG, "[onStartCommand] Error starting service", ex);
            status = ex.getMessage();
            hasError = true;
        }
        if (!disableNotification) {
            Notification notification = Notifier.buildNotification(getContext(), status);
            startForeground(ONGOING_NOTIFICATION_ID, notification);
        }

        // Service is on
        serviceOn();

        // Track device
        ServiceUtils.trackDevice(this, true, getUri());

        return Service.START_REDELIVER_INTENT;
    }

    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> startSurveillance = new Processor<Map<String, String>, Map<String, String>, Map<String, String>, String>() {

        @Override
        public String process(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
            boolean mode = AppController.isSurveillanceMode();
            if (mode) {
                LOGI(TAG, "[process] Already in surveillance mode");
                return new Gson().toJson(ActionStatus.OK);
            }

            final WakeLock screenLock = ((PowerManager) getSystemService(POWER_SERVICE)).newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
            try {
                screenLock.acquire();
                LOGI(TAG, "[process] Starting surveillance mode");
                startWebcam();

                // Check is in surveillance mode
                int counter = 0;
                while (true) {
                    Thread.sleep(1000);
                    mode = AppController.isSurveillanceMode();
                    if (mode || counter == 13)
                        break;
                    else
                        counter++;
                }
            } catch (Exception e) {
                LOGE(TAG, "[process] Failed to start surveillance mode", e);
                return new Gson().toJson(ActionStatus.ERROR);
            } finally {
                screenLock.release();
            }
            return new Gson().toJson(ActionStatus.OK);
        }
    };

    private void startWebcam() {
        ServiceUtils.startWebcam(this);
        /*
        Intent dialogIntent = new Intent(getBaseContext(), WebcamActivity.class);
        dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        getApplication().startActivity(dialogIntent);
        */
    }

    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> shutdownSurveillance = new Processor<Map<String, String>, Map<String, String>, Map<String, String>, String>() {

        @Override
        public String process(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
            boolean isShutdown = AppController.isSurveillanceShutdown();
            if (isShutdown) {
                LOGI(TAG, "[process] Surveillance camera is already shutdown.");
                return new Gson().toJson(ActionStatus.OK);
            }

            try {
                LOGI(TAG, "[process] Shutting down surveillance camera");
                Intent intent = new Intent(AppConfig.INTENT_SHUTDOWN_SURVEILLANCE_ACTION);
                sendBroadcast(intent);

                int counter = 0;
                while (true) {
                    Thread.sleep(1000);
                    isShutdown = AppController.isSurveillanceShutdown();
                    // LOGE(TAG, "shutdown mode " + isShutdown);
                    if (isShutdown || counter == 5)
                        break;
                    else
                        counter++;
                }
            } catch (Exception e) {
                LOGE(TAG, "[process] Failed to shutdown surveillance camera", e);
                return new Gson().toJson(ActionStatus.ERROR);
            }
            return new Gson().toJson(ActionStatus.OK);
        }

    };

    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> surveillanceStatus = new Processor<Map<String, String>, Map<String, String>, Map<String, String>, String>() {

        @Override
        public String process(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
            boolean isShutdown = AppController.isSurveillanceShutdown();
            if (isShutdown) {
                LOGI(TAG, "[process] Surveillance camera is already shutdown.");
                return new Gson().toJson(ActionStatus.ERROR);
            }
            LOGI(TAG, "[process] Surveillance camera is running.");
            return new Gson().toJson(ActionStatus.OK);
        }

    };

    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> surveillanceUrl = new Processor<Map<String, String>, Map<String, String>, Map<String, String>, String>() {

        @Override
        public String process(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
            String port = AppPreference.getInstance().getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_VIDEO_STREAMING_PORT, getContext().getString(R.string.default_video_streaming_port));
            String url = "http://" + currentIPAddress + ":" + port;
            Surveillance s = new Surveillance();
            s.setUrl(url);
            return new Gson().toJson(s);
        }
    };

    private void startApiHandler() {
        smsApiHandler = new MessagingApiHandler(this);
        smsStatusApiHandler = new MessagingStatusApiHandler(this);
        parameterApiHandler = new ParameterApiHandler(this);
        mediaApiHandler = new MediaApiHandler(this);
        mediaUtils = new MediaManager(this);
        statusApiHandler = new StatusApiHandler(this);
        contactApiHandler = new ContactApiHandler(this);
        mmsApiHandler = new MmsApiHandler(this);
        vCardApiHandler = new vCardApiHandler(this);
        vCalendarApiHandler = new vCalendarApiHandler(this);
        locationApiHandler = new LocationApiHandler(this);
        sensorApiHandler = new SensorApiHandler(this);
        gcmApiHandler = new GcmApiHandler(this);
        driveApiHandler = new DriveApiHandler(this);
        ussdApiHandler = new UssdApiHandler(this);
        callApiHandler = new CallApiHandler(this);
    }

    private void stopApiHandler() {
        if (smsApiHandler != null) {
            smsApiHandler.stop();
        }

        if (smsStatusApiHandler != null) {
            smsStatusApiHandler.stop();
        }

        if (parameterApiHandler != null) {
            parameterApiHandler.stop();
        }

        if (mediaApiHandler != null) {
            mediaApiHandler.stop();
        }

        if (statusApiHandler != null) {
            statusApiHandler.stop();
        }

        if (contactApiHandler != null) {
            contactApiHandler.stop();
        }

        if (mmsApiHandler != null) {
            mmsApiHandler.stop();
        }

        if (vCardApiHandler != null) {
            vCardApiHandler.stop();
        }

        if (vCalendarApiHandler != null) {
            vCalendarApiHandler.stop();
        }

        if (locationApiHandler != null) {
            locationApiHandler.stop();
        }

        if (sensorApiHandler!= null) {
            sensorApiHandler.stop();
        }

        if (gcmApiHandler != null) {
            gcmApiHandler.stop();
        }

        if (driveApiHandler != null) {
            driveApiHandler.stop();
        }

        if (ussdApiHandler != null) {
            ussdApiHandler.stop();
        }

        if (callApiHandler != null) {
            callApiHandler.stop();
        }
    }

    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> smsProcessor = new Processor<Map<String, String>, Map<String, String>, Map<String, String>, String>() {

        //private static final long ELAPSED_TIME = 3600000; // 1 hour

        //private long previousTime = System.currentTimeMillis() - ELAPSED_TIME;

        @Override
        public String process(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
            //long currentTime = System.currentTimeMillis();
            //if ((currentTime - previousTime) >= ELAPSED_TIME) {
            //    previousTime = currentTime;
            //    try {
            //        smsApiHandler.housekeep();
            //        mmsApiHandler.housekeep();
            //    } catch (Exception ex) {
            //        LOGE(TAG, "[process] Error in housekeeping", ex);
            //    }
            //}
            return smsApiHandler.handle(header, params, files);
        }
    };

    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> smsStatusProcessor = new Processor<Map<String, String>, Map<String, String>, Map<String, String>, String>() {

        @Override
        public String process(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
            return smsStatusApiHandler.handle(header, params, files);
        }
    };

    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> mmsProcessor = new Processor<Map<String, String>, Map<String, String>, Map<String, String>, String>() {

        @Override
        public String process(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
            return mmsApiHandler.handle(header, params, files);
        }
    };


    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> vCardProcessor = new Processor<Map<String, String>, Map<String, String>, Map<String, String>, String>() {

        @Override
        public String process(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
            return vCardApiHandler.handle(header, params, files);
        }
    };

    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> vCalendarProcessor = new Processor<Map<String, String>, Map<String, String>, Map<String, String>, String>() {

        @Override
        public String process(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
            return vCalendarApiHandler.handle(header, params, files);
        }
    };

    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> locationProcessor = new Processor<Map<String, String>, Map<String, String>, Map<String, String>, String>() {

        @Override
        public String process(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
            return locationApiHandler.handle(header, params, files);
        }
    };

    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> sensorProcessor = new Processor<Map<String, String>, Map<String, String>, Map<String, String>, String>() {

        @Override
        public String process(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
            return sensorApiHandler.handle(header, params, files);
        }
    };

    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> parameterProcessor = new Processor<Map<String, String>, Map<String, String>, Map<String, String>, String>() {

        @Override
        public String process(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
            return parameterApiHandler.handle(header, params, files);
        }
    };

    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> mediaProcessor = new Processor<Map<String, String>, Map<String, String>, Map<String, String>, String>() {

        @Override
        public String process(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
            return mediaApiHandler.handle(header, params, files);
        }
    };

    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> statusProcessor = new Processor<Map<String, String>, Map<String, String>, Map<String, String>, String>() {

        @Override
        public String process(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
            return statusApiHandler.handle(header, params, files);
        }
    };

    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> gcmProcessor = new Processor<Map<String, String>, Map<String, String>, Map<String, String>, String>() {

        @Override
        public String process(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
            return gcmApiHandler.handle(header, params, files);
        }
    };

    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> ussdProcessor = new Processor<Map<String, String>, Map<String, String>, Map<String, String>, String>() {

        @Override
        public String process(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
            return ussdApiHandler.handle(header, params, files);
        }
    };

    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> callProcessor = new Processor<Map<String, String>, Map<String, String>, Map<String, String>, String>() {

        @Override
        public String process(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
            return callApiHandler.handle(header, params, files);
        }
    };

    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> driveProcessor = new Processor<Map<String, String>, Map<String, String>, Map<String, String>, String>() {

        private static final long ELAPSED_TIME = AppConfig.HOUSEKEEP_INTERVAL; // 6 hours

        private List<FolderHousekeepingTracking> folderTrackers = Collections.synchronizedList(new ArrayList<FolderHousekeepingTracking>());

        @Override
        public String process(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
            final long currentTime = System.currentTimeMillis();
            final String folderName = ValidationUtils.getStringValue(DriveApiHandler.PARAM_FOLDER_NAME, params, StringUtils.EMPTY);
            final String mimeType = ValidationUtils.getStringValue(DriveApiHandler.PARAM_MIME_TYPE, params, StringUtils.EMPTY);

            try {
                if (!TextUtils.isEmpty(folderName) && !TextUtils.isEmpty(mimeType)) {
                    FolderHousekeepingTracking folderTracker = null;
                    for (FolderHousekeepingTracking ft : folderTrackers) {
                        if (ft.getFolder().equalsIgnoreCase(folderName) && ft.getMimeType().equalsIgnoreCase(mimeType)) {
                            folderTracker = ft;
                            break;
                        }
                    }
                    if (folderTracker != null) {
                        if (currentTime - folderTracker.getTimestamp() >= ELAPSED_TIME) {
                            folderTracker.setTimestamp(currentTime);
                            try {
                                new HousekeepFileAsyncTask(getContext()).execute(folderName, mimeType);
                            } catch (Exception ex) {
                                LOGE(TAG, "[process] Error in housekeeping", ex);
                            }
                        }
                    } else {
                        // First time, proceed with housekeeping
                        folderTracker = new FolderHousekeepingTracking();
                        folderTracker.setFolder(folderName);
                        folderTracker.setMimeType(mimeType);
                        folderTracker.setTimestamp(currentTime);
                        folderTrackers.add(folderTracker);
                        try {
                            new HousekeepFileAsyncTask(getContext()).execute(folderName, mimeType);
                        } catch (Exception ex) {
                            LOGE(TAG, "[process] Error in housekeeping", ex);
                        }
                    }
                }
            } catch (Exception ex) {
                LOGE(TAG, "[process] Error in housekeeping", ex);
            }

            return driveApiHandler.handle(header, params, files);
        }
    };
    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> contactProcessor = new Processor<Map<String, String>, Map<String, String>, Map<String, String>, String>() {

        @Override
        public String process(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
            return contactApiHandler.handle(header, params, files);
        }
    };

    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, InputStream> mediaStreamingProcessor = new Processor<Map<String, String>, Map<String, String>, Map<String, String>, InputStream>() {

        private static final String PARAM_URI = "uri";
        private static final String PARAM_KIND = "kind";
        private static final String PARAM_ID = "id";

        private static final String PARAM_MIME_TYPE = "mime";

        private static final String KIND_ORIGINAL = "0";
        private static final String KIND_MICRO = "1";
        private static final String KIND_MINI = "2";

        private static final String RESOLUTION_HIGH = "3";
        private static final String RESOLUTION_LOW = "1";
        private static final String RESOLUTION_MEDIUM = "2";
        private static final String RESOLUTION_DEFAULT = "0";

        static final String MEDIA_CAMERA = "camera";
        private static final String PARAM_FLASH = "flash";
        private static final String PARAM_FRONT = "front";
        private static final String PARAM_RESOLUTION = "resolution";
        private static final String PARAM_FOCUS_MODE = "focus";


        @Override
        public InputStream process(Map<String, String> headers, Map<String, String> params, Map<String, String> files) {

            try {
                if (params.containsKey(AppServer.URI_PARAM_PREFIX + "0")) {
                    final String mediaType = ValidationUtils.getStringValue(AppServer.URI_PARAM_PREFIX + "0", params, StringUtils.EMPTY);
                    final String uri = ValidationUtils.getStringValue(PARAM_URI, params, StringUtils.EMPTY);
                    String kind = ValidationUtils.getStringValue(PARAM_KIND, params, KIND_MINI);
                    final long id = ValidationUtils.getLongValue(PARAM_ID, params, -1l);
                    if (!KIND_ORIGINAL.equals(kind) && !KIND_MICRO.equals(kind) && !KIND_MINI.equals(kind)) {
                        kind = KIND_MICRO;
                    }

                    if (!MEDIA_CAMERA.equalsIgnoreCase(mediaType) &&
                            (TextUtils.isEmpty(uri) || TextUtils.isEmpty(mediaType) || id <= 0)) {
                        throw new Exception(getContext().getString(R.string.media_not_found));
                    }
                    byte[] data;
                    if (MediaApiHandler.MEDIA_TYPE_VIDEO.equalsIgnoreCase(mediaType)) {
                        if (KIND_ORIGINAL.equals(kind)) {
                            // Get the video information
                            MediaVideo video = mediaUtils.getVideo(id);
                            if (video == null) {
                                throw new Exception(getContext().getString(R.string.media_not_found));
                            }
                            InputStream is = getContentResolver().openInputStream(Uri.parse(video.getContentUri()));
                            params.put(MimeType.PARAM_MIME, video.getMimeType());
                            return is;
                        } else {
                            int option = MediaStore.Video.Thumbnails.MICRO_KIND;
                            if (KIND_MINI.equals(kind)) {
                                option = MediaStore.Video.Thumbnails.MINI_KIND;
                            }
                            data = mediaUtils.getVideoThumbnail(id, option);
                            InputStream is = new ByteArrayInputStream(data);
                            params.put(MimeType.PARAM_MIME, MimeType.IMAGE_JPEG);
                            return is;
                        }
                    } else if (MediaApiHandler.MEDIA_TYPE_AUDIO.equalsIgnoreCase(mediaType)) {
                        if (KIND_ORIGINAL.equals(kind)) {
                            // Get the audio information
                            MediaAudio audio = mediaUtils.getAudio(id);
                            if (audio == null) {
                                throw new Exception(getContext().getString(R.string.media_not_found));
                            }
                            InputStream is = getContentResolver().openInputStream(Uri.parse(audio.getContentUri()));
                            params.put(MimeType.PARAM_MIME, audio.getMimeType());
                            return is;
                        } else {
                            data = mediaUtils.getAlbumArt(id);
                            if (data != null) {
                                InputStream is = new ByteArrayInputStream(data);
                                params.put(MimeType.PARAM_MIME, MimeType.IMAGE_JPEG);
                                return is;
                            }
                            return null;
                        }
                    } else if (DriveApiHandler.MEDIA_TYPE_DRIVE.equalsIgnoreCase(mediaType)) {
                        // Stream from drive
                        final String mimeType = ValidationUtils.getStringValue(PARAM_MIME_TYPE, params, MimeType.IMAGE_JPEG);
                        params.put(MimeType.PARAM_MIME, mimeType);
                        data = new RetrieveFileSyncTask(getContext()).execute(uri);
                        if (data != null) {
                            return new ByteArrayInputStream(data);
                        }
                        return null;
                    } else if (MEDIA_CAMERA.equalsIgnoreCase(mediaType)) {
                        // Capture a photo from camera
                        final boolean flash = ValidationUtils.getBooleanValue(PARAM_FLASH, params, false);
                        final boolean front = ValidationUtils.getBooleanValue(PARAM_FRONT, params, false);
                        final String resolution = ValidationUtils.getStringValue(PARAM_RESOLUTION, params, "");
                        final int focus = ValidationUtils.getIntegerValue(PARAM_FOCUS_MODE, params, -1);

                        final CameraControllerManager cameraManager = CameraControllerManager.getManager();
                        int cameraIdx = Camera.CameraInfo.CAMERA_FACING_BACK;
                        if (front && cameraManager.getNumberOfCameras() > 1) {
                            // Switch to front camera
                            cameraIdx = Camera.CameraInfo.CAMERA_FACING_FRONT;
                        }
                        CameraController cameraController = CameraController.getController(cameraIdx);
                        try {
                            if (flash) {
                                cameraController.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                            } //else {
                            //  cameraController.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                            //}

                            if (!RESOLUTION_DEFAULT.equalsIgnoreCase(resolution)) {
                                List<CameraSize> cameraSizes = cameraController.getSupportedPictureSizes();
                                final int availableSizes = cameraSizes.size();
                                int index = -1;
                                if (RESOLUTION_MEDIUM.equalsIgnoreCase(resolution)) {
                                    index = availableSizes / 2;
                                } else if (RESOLUTION_LOW.equalsIgnoreCase(resolution)) {
                                    index = 0;
                                } else if (RESOLUTION_HIGH.equalsIgnoreCase(resolution)) {
                                    index = availableSizes - 1;
                                }
                                if (index >= 0) {
                                    final CameraSize targetSize = cameraSizes.get(index);
                                    cameraController.setPictureSize(targetSize.getWidth(), targetSize.getHeight());
                                }
                            }
                            if (focus >= 0) {
                                final CameraFocusMode focusMode = CameraFocusMode.get(focus);
                                final String focusModeValue = CameraFocusMode.getValue(focusMode);
                                cameraController.setFocusMode(focusModeValue);
                                if (focusMode == CameraFocusMode.FOCUS_MODE_AUTO || focusMode == CameraFocusMode.FOCUS_MODE_CONTINUOUS_PICTURE ||
                                        focusMode == CameraFocusMode.FOCUS_MODE_CONTINUOUS_VIDEO || focusMode == CameraFocusMode.FOCUS_MODE_MACRO) {
                                    final CountDownLatch latch = new CountDownLatch(1);
                                    final CameraController.AutoFocusCallback autoFocusCallback = new CameraController.AutoFocusCallback() {
                                        @Override
                                        public void onAutoFocus(boolean success) {
                                            latch.countDown();
                                        }
                                    };
                                    cameraController.autoFocus(autoFocusCallback);
                                    latch.await();
                                }
                            }
                            data = cameraController.takePictureSync(true);

                            if (data != null) {
                                InputStream is = new ByteArrayInputStream(data);
                                params.put(MimeType.PARAM_MIME, MimeType.IMAGE_JPEG);
                                return is;
                            } else {
                                return null;
                            }
                        } finally {
                            if (flash) {
                                cameraController.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                            }
                            cameraController.release();
                            cameraController = null;
                        }
                    } else {
                        if (KIND_ORIGINAL.equals(kind)) {
                            data = mediaUtils.getImage(uri);
                        } else {
                            int option = MediaStore.Images.Thumbnails.MICRO_KIND;
                            if (KIND_MINI.equals(kind)) {
                                option = MediaStore.Images.Thumbnails.MINI_KIND;
                            }
                            data = mediaUtils.getImageThumbnail(id, option);
                        }
                        if (data != null) {
                            InputStream is = new ByteArrayInputStream(data);
                            params.put(MimeType.PARAM_MIME, MimeType.IMAGE_JPEG);
                            return is;
                        } else {
                            return null;
                        }
                    }
                } else {
                    throw new Exception(getContext().getString(R.string.media_not_found));
                }
            } catch (Exception ex) {
                return null;
            }
        }
    };

    protected void maybeAcquireWakeLock() {
        if (!wakeLock.isHeld()) {
            wakeLock.acquire();
        }
    }

    protected void releaseWakeLock() {
        if (wakeLock.isHeld()) {
            try {
                wakeLock.release();
            } catch (Throwable th) {
                // ignoring this exception, probably wakeLock was already released
            }
        }
    }

    public Context getContext() {
        // return AppController.getContext();
        return this;
    }

    /**
     * This is where we initialize. We call this when onStart/onStartCommand is
     * called by the system. We won't do anything with the intent here, and you
     * probably won't, either.
     */
    private void serviceOn() {
        maybeAcquireWakeLock();
    }

    private void serviceOff() {
        releaseWakeLock();
    }

    /**
     * Called when {@code googleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        LOGI(TAG, "GoogleApiClient connected");
    }

    /**
     * Called when {@code googleApiClient} is disconnected.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        LOGI(TAG, "GoogleApiClient connection suspended");
    }

    /**
     * Called when {@code googleApiClient} is trying to connect but failed.
     * Handle {@code result.getResolution()} if there is a resolution is
     * available.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        LOGI(TAG, "GoogleApiClient connection failed: " + result.toString());
    }

    public class FolderHousekeepingTracking {
        private long timestamp;
        private String folder;
        private String mimeType;

        public FolderHousekeepingTracking() {
            timestamp = 0;
            folder = "";
            mimeType = "";
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public String getFolder() {
            return folder;
        }

        public void setFolder(String folder) {
            this.folder = folder;
        }

        public String getMimeType() {
            return mimeType;
        }

        public void setMimeType(String mimeType) {
            this.mimeType = mimeType;
        }
    }
}
