package com.mymobkit.app;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.google.android.gms.common.api.GoogleApiClient;
import com.mymobkit.BuildConfig;
import com.mymobkit.R;
import com.mymobkit.common.AppPreference;
import com.mymobkit.common.DeviceUtils;
import com.mymobkit.common.StringUtils;
import com.mymobkit.common.ValidationUtils;
import com.mymobkit.data.AppConfigHelper;
import com.mymobkit.job.requirement.MediaNetworkRequirementProvider;
import com.mymobkit.job.requirement.ServiceRequirementProvider;
import com.mymobkit.model.ConfigParam;
import com.mymobkit.model.DefaultSettings;
import com.mymobkit.ui.fragment.ServiceSettingsFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;
import org.whispersystems.jobqueue.JobManager;
import org.whispersystems.jobqueue.persistence.JavaJobSerializer;
import org.whispersystems.jobqueue.requirements.NetworkRequirementProvider;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Application level instance.
 */
@ReportsCrashes(
        formUri = "cloudant URL ** Removed",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.POST,
        formUriBasicAuthLogin = "XXX - Removed",
        formUriBasicAuthPassword = "XXX - Removed",
        customReportContent = {
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PACKAGE_NAME,
                ReportField.REPORT_ID,
                ReportField.BUILD,
                ReportField.STACK_TRACE},
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.toast_crash
)
public final class AppController extends Application {
//public final class AppController extends MultiDexApplication {

    private static final String TAG = makeLogTag(AppController.class);

    // Global application context
    private static Context context = null;

    public static AppConfigHelper appConfig;

    public static Bus bus = new Bus(ThreadEnforcer.ANY);

    public static GoogleApiClient googleApiClient = null;

    public static DefaultSettings defaultSettings;

    public static long lastDriveHousekeepTime;

    private static SessionManager sessionManager;

    // private static GTalkSessionManager gTalkSessionManager;

    private JobManager jobManager;

    private MediaNetworkRequirementProvider mediaNetworkRequirementProvider = new MediaNetworkRequirementProvider();

    public static AppController getInstance(Context context) {
        return (AppController) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initApp();
        initJobManager();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        // Disconnect Google API client
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
            googleApiClient = null;
        }

        if (sessionManager != null) {
            sessionManager.clear();
            sessionManager = null;
        }
    }

    public void initApp() {
        if (context == null)
            context = this.getApplicationContext();

        appConfig = AppConfigHelper.getConfigHelper(context);

        defaultSettings = new DefaultSettings();

        // Initialize application preference manager
        AppPreference.initialize(context);

        // Initialize session manager
        sessionManager = new SessionManager(context);

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }

    private void initJobManager() {
        this.jobManager = JobManager.newBuilder(this)
                .withName("myMobKit Jobs")
                        //.withDependencyInjector(this)
                .withJobSerializer(new JavaJobSerializer())
                .withRequirementProviders(
                        new ServiceRequirementProvider(this),
                        new NetworkRequirementProvider(this),
                        mediaNetworkRequirementProvider)
                .withConsumerThreads(5)
                .build();
    }

    public static Context getContext() {
        return context;
    }

    public static void setSurveillanceMode(boolean mode) {
        try {
            appConfig.updateConfig("surveillance_mode", "surveillance", String.valueOf(mode));
        } catch (Exception e) {
            LOGE(TAG, "[setSurveillanceMode] Error setting config param", e);
        }
    }

    public static boolean isSurveillanceMode() {
        try {
            ConfigParam config = appConfig.getConfig("surveillance_mode", "surveillance");
            return ValidationUtils.getBoolean(config.getValue());
        } catch (Exception e) {
            LOGE(TAG, "[isSurveillanceMode] Error retrieving config param", e);
        }
        return false;
    }

    public static void setSurveillanceShutdown(boolean mode) {
        try {
            appConfig.updateConfig("surveillance_shutdown", "surveillance", String.valueOf(mode));
        } catch (Exception e) {
            LOGE(TAG, "[setSurveillanceShutdown] Error setting config param", e);
        }
    }

    public static boolean isSurveillanceShutdown() {
        try {
            ConfigParam config = appConfig.getConfig("surveillance_shutdown", "surveillance");
            return ValidationUtils.getBoolean(config.getValue());
        } catch (Exception e) {
            LOGE(TAG, "[isSurveillanceShutdown] Error retrieving config param", e);
        }
        return false;
    }

    public static void setLockPattern(String lockPattern) {
        try {
            appConfig.updateConfig("lock_pattern", "security", lockPattern);
        } catch (Exception e) {
            LOGE(TAG, "[setLockPattern] Error setting config param", e);
        }
    }

    public static String getLockPattern() {
        try {
            ConfigParam config = appConfig.getConfig("lock_pattern", "security");
            return (config.getValue() == null ? StringUtils.EMPTY : config.getValue().trim());
        } catch (Exception e) {
            LOGE(TAG, "[getLockPattern] Error retrieving config param", e);
        }
        return StringUtils.EMPTY;
    }

    public static DefaultSettings getDefaultSettings() {
        if (defaultSettings == null || defaultSettings.videoSettings == null ||
                defaultSettings.videoSettings.profile == null) {
            defaultSettings = new DefaultSettings();
        }
        return defaultSettings;
    }

    public static String getDeviceName() {
        String deviceName = DeviceUtils.getDeviceName();
        if (TextUtils.isEmpty(deviceName) && context != null) {
            deviceName = context.getString(R.string.default_device_unique_name);
        }
        return deviceName;
    }

    public static String getAppServer() {
        if (BuildConfig.DEBUG) {
            // Debug mode
            return context.getString(R.string.app_server_debug);
        } else {
            return context.getString(R.string.app_server_prod);
        }
    }

    public static String getUploadBlobUrl() {
        return getAppServer() + context.getString(R.string.app_upload_request_blob_url);
    }

    public static String getConnectedDevicesUrl(final Context context) {
        final String email = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_DEVICE_EMAIL_ADDRESS, context.getString(R.string.default_device_email_address));
        final String deviceId = DeviceUtils.getDeviceId(context);
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(deviceId)) {
            return StringUtils.EMPTY;
        }
        return getAppServer() + "/service/device/group/" + email + "/" + deviceId;
    }

    public static String appName() {
        return getContext().getString(R.string.app_name);
    }


    public static SessionManager getSessionManager() {
        return sessionManager;
    }

    public JobManager getJobManager() {
        return jobManager;
    }

   /* @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }*/
}
