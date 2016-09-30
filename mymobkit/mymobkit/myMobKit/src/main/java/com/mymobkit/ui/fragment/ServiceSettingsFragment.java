package com.mymobkit.ui.fragment;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.Browser;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationServices;
import com.mymobkit.R;
import com.mymobkit.app.AppConfig;
import com.mymobkit.app.AppController;
import com.mymobkit.common.AppPreference;
import com.mymobkit.common.GcmUtils;
import com.mymobkit.common.GcmUtils.RegistrationStatus;
import com.mymobkit.common.ServiceUtils;
import com.mymobkit.common.StringUtils;
import com.mymobkit.common.TelephonyUtils;
import com.mymobkit.common.ToastUtils;
import com.mymobkit.common.ValidationUtils;
import com.mymobkit.common.WidgetUtils;
import com.mymobkit.enums.MessagingAgingMethod;
import com.mymobkit.mms.LegacyMmsConnection;
import com.mymobkit.mms.databases.ApnDatabase;
import com.mymobkit.model.PreferenceChangedEvent;
import com.mymobkit.preference.LockPatternDialogPreference;
import com.mymobkit.preference.NonEmptyEditTextPreference;
import com.mymobkit.preference.SeekBarDialogPreference;
import com.mymobkit.service.HttpdService;
import com.mymobkit.service.IHttpdService;
import com.mymobkit.service.webcam.WebcamService;
import com.mymobkit.ui.base.BaseActivity;
import com.mymobkit.ui.base.PreferenceFragmentBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import haibison.android.lockpattern.LockPatternActivity;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

import static com.mymobkit.common.LogUtils.LOGD;
import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.LOGI;
import static com.mymobkit.common.LogUtils.LOGW;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Web control panel fragment.
 */
@RuntimePermissions
public final class ServiceSettingsFragment extends PreferenceFragmentBase
        implements OnSharedPreferenceChangeListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = makeLogTag(ServiceSettingsFragment.class);

    public static final String SHARED_PREFS_NAME = "control_panel_settings";

    public static final String KEY_CONTROL_PANEL_LOGIN_REQUIRED = "preferences_control_panel_login_required";
    public static final String KEY_CONTROL_PANEL_USER_NAME = "preferences_control_panel_user_name";
    public static final String KEY_CONTROL_PANEL_USER_PASSWORD = "preferences_control_panel_user_password";
    public static final String KEY_CONTROL_PANEL_PORT = "preferences_control_panel_port";
    public static final String KEY_CONTROL_PANEL_STATUS = "preferences_control_panel_status";
    public static final String KEY_CONTROL_PANEL_URL = "preferences_control_panel_url";
    public static final String KEY_CONTROL_PANEL_AUTO_START = "preferences_control_panel_auto_start";
    //public static final String KEY_CONTROL_PANEL_IN_SURVEILLANCE_MODE = "preferences_control_panel_in_surveillance_mode";
    public static final String KEY_PRIMARY_ADDRESS_FAMILY = "preferences_primary_address_family";
    public static final String KEY_LOCK_PATTERN_SETUP = "preferences_lock_pattern_setup";
    public static final String KEY_LOCK_PATTERN_REQUIRED = "preferences_lock_pattern_required";
    public static final String KEY_CONTROL_PANEL_DISABLE_NOTIFICATION = "preferences_control_panel_disable_notification";
    public static final String KEY_DEVICE_UNIQUE_NAME = "preferences_device_unique_name";
    public static final String KEY_DEVICE_PHONE_NUMBER = "preferences_device_phone_number";
    public static final String KEY_REMOTE_STARTUP = "preferences_remote_startup";
    public static final String KEY_REMOTE_STARTUP_PASSWORD = "preferences_remote_startup_password";
    public static final String KEY_DEVICE_EMAIL_ADDRESS = "preferences_device_email_address";
    //public static final String KEY_DEVICE_EMAIL_PASSWORD = "preferences_device_email_password";

    public static final String KEY_SAVE_SENT_MESSAGES = "preferences_saved_sent_messages";
    public static final String KEY_MESSAGING_AGING_METHOD = "preferences_messaging_aging_method";
    public static final String KEY_MESSAGING_AGING_DAYS = "preferences_messaging_aging_days";
    public static final String KEY_MESSAGING_AGING_SIZE = "preferences_messaging_aging_size";

    public static final String KEY_SHOW_DEFAULT_SMS_ALERT = "preferences_show_default_sms_alert";

    public static final String KEY_DEVICE_TRACKING = "preferences_device_tracking";

    public static final String KEY_APN_MMSC = "preferences_apn_mmsc";
    public static final String KEY_APN_MMS_PROXY = "preferences_apn_mms_proxy";
    public static final String KEY_APN_MMS_PORT = "preferences_apn_mms_port";
    public static final String KEY_APN_MMS_USER = "preferences_apn_mms_user";
    public static final String KEY_APN_MMS_PASSWORD = "preferences_apn_mms_password";
    public static final String KEY_APN_MMS_USER_AGENT = "preferences_apn_mms_user_agent";

    public static final String KEY_WIDGET_UPDATE_INTERVAL = "preferences_widget_update_interval";

    // Interface to the service
    private IHttpdService httpdServiceProvider;

    private CheckBoxPreference loginRequired;
    private EditTextPreference userName;
    private EditTextPreference userPassword;
    private EditTextPreference port;
    private CheckBoxPreference status;
    private EditTextPreference url;
    private CheckBoxPreference autoStart;
    private LockPatternDialogPreference lockPatternDialog;
    private CheckBoxPreference lockPatternRequired;
    private CheckBoxPreference disableNotification;
    private EditTextPreference deviceUniqueName;
    private EditTextPreference devicePhoneNumber;
    private CheckBoxPreference remoteStartup;
    private EditTextPreference remoteStartupPassword;
    private ListPreference deviceEmailAddress;
    //private EditTextPreference deviceEmailPassword;
    private ListPreference primaryAddressFamily;
    private EditTextPreference apnMmsc;
    private EditTextPreference apnMmsProxy;
    private EditTextPreference apnMmsPort;
    private EditTextPreference apnMmsUser;
    private EditTextPreference apnMmsPassword;
    private EditTextPreference apnMmsUserAgent;
    private CheckBoxPreference deviceTracking;

    private CheckBoxPreference saveSentMessages;
    private ListPreference messagingAgingMethod;
    private SeekBarDialogPreference messagingAgingDays;
    private SeekBarDialogPreference messagingAgingSize;

    private EditTextPreference widgetUpdateInterval;

    protected static final int HTTPD_SERVICE_CONNECTED = 100;
    protected static final int HTTPD_SERVICE_DISCONNECTED = 200;

    private String lockPattern = StringUtils.EMPTY;

    private AppBroadcastReceiver appBroadcastReceiver = null;
    boolean isAppReceiverRegistered = false;

    protected Handler statusHandler = new Handler() {

        @Override
        public void handleMessage(final Message msg) {
            LOGD(TAG, "[handleMessage] Received service status");
            switch (msg.what) {
                case HTTPD_SERVICE_CONNECTED:
                    try {
                        if (httpdServiceProvider != null) {
                            if (!httpdServiceProvider.isError()) { // &&
                                // httpdServiceProvider.isAlive())
                                // {
                                String uri = httpdServiceProvider.getUri();
                                if (TextUtils.isEmpty(uri)) uri = " ";
                                url.setText(uri);
                            } else {
                                String errorMsg = httpdServiceProvider.getErrorMsg();
                                if (TextUtils.isEmpty(errorMsg)) errorMsg = " ";
                                url.setText(errorMsg);
                            }
                        }
                    } catch (Exception ex) {
                        LOGE(TAG, "[handleMessage] Error retrieving service status", ex);
                    }
                    break;
                case HTTPD_SERVICE_DISCONNECTED:
                    url.setText(" ");
                    break;
                default:
                    break;
            }
        }
    };

    private class AppBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(AppConfig.INTENT_IP_ADDRESS_CHANGE_ACTION)) {
                LOGD(TAG, "received IP change request");
                statusHandler.sendEmptyMessage(HTTPD_SERVICE_CONNECTED);
            } else if (action.equalsIgnoreCase(AppConfig.INTENT_SHUTDOWN_SERVICE_ACTION)) {
                stopService();
            }
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        final AppPreference prefs = AppPreference.getInstance();
        if (!prefs.getValue(AppPreference.APP_SHARED_PREF_NAMES, SHARED_PREFS_NAME, false)) {
            PreferenceManager.setDefaultValues(getActivity(), SHARED_PREFS_NAME, Context.MODE_PRIVATE, R.xml.pref_control_panel_services, true);
            prefs.setValue(AppPreference.APP_SHARED_PREF_NAMES, SHARED_PREFS_NAME, true);
        }

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_control_panel_services);
        final PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setSharedPreferencesName(SHARED_PREFS_NAME);

        final PreferenceScreen preferences = getPreferenceScreen();
        loginRequired = (CheckBoxPreference) preferences.findPreference(KEY_CONTROL_PANEL_LOGIN_REQUIRED);
        userName = (EditTextPreference) preferences.findPreference(KEY_CONTROL_PANEL_USER_NAME);
        userPassword = (EditTextPreference) preferences.findPreference(KEY_CONTROL_PANEL_USER_PASSWORD);
        port = (EditTextPreference) preferences.findPreference(KEY_CONTROL_PANEL_PORT);
        status = (CheckBoxPreference) preferences.findPreference(KEY_CONTROL_PANEL_STATUS);
        url = (EditTextPreference) preferences.findPreference(KEY_CONTROL_PANEL_URL);
        autoStart = (CheckBoxPreference) preferences.findPreference(KEY_CONTROL_PANEL_AUTO_START);
        lockPatternRequired = (CheckBoxPreference) preferences.findPreference(KEY_LOCK_PATTERN_REQUIRED);
        lockPatternDialog = (LockPatternDialogPreference) preferences.findPreference(KEY_LOCK_PATTERN_SETUP);
        disableNotification = (CheckBoxPreference) preferences.findPreference(KEY_CONTROL_PANEL_DISABLE_NOTIFICATION);
        deviceUniqueName = (EditTextPreference) preferences.findPreference(KEY_DEVICE_UNIQUE_NAME);
        devicePhoneNumber = (EditTextPreference) preferences.findPreference(KEY_DEVICE_PHONE_NUMBER);
        remoteStartup = (CheckBoxPreference) preferences.findPreference(KEY_REMOTE_STARTUP);
        remoteStartupPassword = (EditTextPreference) preferences.findPreference(KEY_REMOTE_STARTUP_PASSWORD);
        deviceEmailAddress = (ListPreference) preferences.findPreference(KEY_DEVICE_EMAIL_ADDRESS);
        //deviceEmailPassword = (EditTextPreference) preferences.findPreference(KEY_DEVICE_EMAIL_PASSWORD);
        primaryAddressFamily = (ListPreference) preferences.findPreference(KEY_PRIMARY_ADDRESS_FAMILY);
        saveSentMessages = (CheckBoxPreference) preferences.findPreference(KEY_SAVE_SENT_MESSAGES);
        messagingAgingMethod = (ListPreference) preferences.findPreference(KEY_MESSAGING_AGING_METHOD);
        messagingAgingDays = (SeekBarDialogPreference) preferences.findPreference(KEY_MESSAGING_AGING_DAYS);
        messagingAgingSize = (SeekBarDialogPreference) preferences.findPreference(KEY_MESSAGING_AGING_SIZE);
        apnMmsc = (EditTextPreference) preferences.findPreference(KEY_APN_MMSC);
        apnMmsProxy = (EditTextPreference) preferences.findPreference(KEY_APN_MMS_PROXY);
        apnMmsPort = (EditTextPreference) preferences.findPreference(KEY_APN_MMS_PORT);
        apnMmsUser = (EditTextPreference) preferences.findPreference(KEY_APN_MMS_USER);
        apnMmsPassword = (EditTextPreference) preferences.findPreference(KEY_APN_MMS_PASSWORD);
        apnMmsUserAgent = (EditTextPreference) preferences.findPreference(KEY_APN_MMS_USER_AGENT);
        deviceTracking = (CheckBoxPreference) preferences.findPreference(KEY_DEVICE_TRACKING);
        widgetUpdateInterval = (EditTextPreference) preferences.findPreference(KEY_WIDGET_UPDATE_INTERVAL);

        String[] emails = getConfiguredEmails().toArray(new String[0]);
        String defaultEmail = StringUtils.EMPTY;
        if (emails.length > 0)
            defaultEmail = emails[0];

        loginRequired.setChecked(preferenceManager.getSharedPreferences().getBoolean(KEY_CONTROL_PANEL_LOGIN_REQUIRED, Boolean.valueOf(this.getString(R.string.default_login_required))));
        userName.setText(preferenceManager.getSharedPreferences().getString(KEY_CONTROL_PANEL_USER_NAME, this.getString(R.string.default_http_user_name)));
        userPassword.setText(preferenceManager.getSharedPreferences().getString(KEY_CONTROL_PANEL_USER_PASSWORD, this.getString(R.string.default_http_user_password)));
        port.setText(preferenceManager.getSharedPreferences().getString(KEY_CONTROL_PANEL_PORT, this.getString(R.string.default_control_panel_http_port)));
        deviceUniqueName.setText(preferenceManager.getSharedPreferences().getString(KEY_DEVICE_UNIQUE_NAME, AppController.getDeviceName()));
        devicePhoneNumber.setText(preferenceManager.getSharedPreferences().getString(KEY_DEVICE_PHONE_NUMBER, this.getString(R.string.default_device_phone_number)));
        remoteStartupPassword.setText(preferenceManager.getSharedPreferences().getString(KEY_REMOTE_STARTUP_PASSWORD, this.getString(R.string.default_remote_startup_password)));

        url.setText(preferenceManager.getSharedPreferences().getString(KEY_CONTROL_PANEL_URL, " "));
        url.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Dialog dialog = url.getDialog();
                if (dialog != null)
                    dialog.dismiss();
                final String controlPanelUrl = url.getText();
                if (!StringUtils.isNullOrBlank(controlPanelUrl)) {
                    try {
                        Uri uriUrl = Uri.parse(controlPanelUrl);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uriUrl);
                        intent.putExtra(Browser.EXTRA_APPLICATION_ID, getActivity().getPackageName());
                        startActivity(intent);
                    } catch (Exception e) {
                        LOGE(TAG, "[onPreferenceClick] Unable to go to control panel", e);
                    }
                }
                return true;
            }

        });

        autoStart.setChecked(preferenceManager.getSharedPreferences().getBoolean(KEY_CONTROL_PANEL_AUTO_START, Boolean.valueOf(this.getString(R.string.default_auto_start_service))));
        lockPatternRequired.setChecked(preferenceManager.getSharedPreferences().getBoolean(KEY_LOCK_PATTERN_REQUIRED, false));
        lockPattern = AppController.getLockPattern();
        if (TextUtils.isEmpty(lockPattern)) {
            lockPatternRequired.setEnabled(false);
        }
        disableNotification.setChecked(preferenceManager.getSharedPreferences().getBoolean(KEY_CONTROL_PANEL_DISABLE_NOTIFICATION, Boolean.valueOf(this.getString(R.string.default_disable_notification))));
        remoteStartup.setChecked(preferenceManager.getSharedPreferences().getBoolean(KEY_REMOTE_STARTUP, Boolean.valueOf(this.getString(R.string.default_remote_startup))));
        primaryAddressFamily.setValue(preferenceManager.getSharedPreferences().getString(KEY_PRIMARY_ADDRESS_FAMILY, getString(R.string.default_primary_address_family)));
        deviceTracking.setChecked(preferenceManager.getSharedPreferences().getBoolean(KEY_DEVICE_TRACKING, Boolean.valueOf(this.getString(R.string.default_device_tracking))));
        widgetUpdateInterval.setText(preferenceManager.getSharedPreferences().getString(KEY_WIDGET_UPDATE_INTERVAL, getString(R.string.default_widget_update_interval)));

        saveSentMessages.setChecked(preferenceManager.getSharedPreferences().getBoolean(KEY_SAVE_SENT_MESSAGES, Boolean.valueOf(this.getString(R.string.default_save_sent_messages))));
        messagingAgingMethod.setValue(preferenceManager.getSharedPreferences().getString(KEY_MESSAGING_AGING_METHOD, getString(R.string.default_messaging_aging_method)));
        messagingAgingSize.setProgress(preferenceManager.getSharedPreferences().getInt(KEY_MESSAGING_AGING_SIZE, Integer.valueOf(getString(R.string.default_messaging_aging_size))));
        messagingAgingDays.setProgress(preferenceManager.getSharedPreferences().getInt(KEY_MESSAGING_AGING_DAYS, Integer.valueOf(getString(R.string.default_messaging_aging_days))));
        setAgingMethodControls(messagingAgingMethod.getValue());

        deviceEmailAddress.setEntries(emails);
        deviceEmailAddress.setEntryValues(emails);
        deviceEmailAddress.setValue(preferenceManager.getSharedPreferences().getString(KEY_DEVICE_EMAIL_ADDRESS, defaultEmail));
        //deviceEmailPassword.setText(preferenceManager.getSharedPreferences().getString(KEY_DEVICE_EMAIL_PASSWORD, this.getString(R.string.default_device_email_password)));

        String mmsc = preferenceManager.getSharedPreferences().getString(KEY_APN_MMSC, this.getString(R.string.default_mmsc));
        String mmsProxy = preferenceManager.getSharedPreferences().getString(KEY_APN_MMS_PROXY, this.getString(R.string.default_mms_proxy));
        String mmsPort = preferenceManager.getSharedPreferences().getString(KEY_APN_MMS_PORT, this.getString(R.string.default_mms_port));
        String mmsUser = preferenceManager.getSharedPreferences().getString(KEY_APN_MMS_USER, "");
        String mmsPassword = preferenceManager.getSharedPreferences().getString(KEY_APN_MMS_PASSWORD, "");
        String mmsUserAgent = preferenceManager.getSharedPreferences().getString(KEY_APN_MMS_USER_AGENT, LegacyMmsConnection.USER_AGENT);

        if (TextUtils.isEmpty(mmsc)) {
            // Try to get the default configured device settings
            /*
            ApnUtils apnUtils = new ApnUtils(getActivity());
            List<ApnUtils.Apn> apns = apnUtils.getMMSApns();
            if (!apns.isEmpty()) {
                mmsc = ValidationUtils.getString(apns.get(0).mmsc);
                mmsProxy = ValidationUtils.getString(apns.get(0).mmsProxy);
                mmsPort = ValidationUtils.getString(apns.get(0).mmsPort);
            }
            */
            new LoadApnDefaultsTask().execute();
        } else {
            apnMmsc.setText(mmsc);
            apnMmsProxy.setText(mmsProxy);
            apnMmsPort.setText(mmsPort);
            apnMmsUser.setText(mmsUser);
            apnMmsPassword.setText(mmsPassword);
            apnMmsUserAgent.setText(mmsUserAgent);
        }

        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            initSummary(getPreferenceScreen().getPreference(i));
        }

        lockPatternDialog.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                lockPatternDialog.getDialog().dismiss();
                Intent intent = new Intent(LockPatternActivity.ACTION_CREATE_PATTERN, null, getActivity(), LockPatternActivity.class);
                getActivity().startActivityForResult(intent, AppConfig.SECURITY_REQ_CREATE_PATTERN);
                return true;
            }
        });

        boolean isServiceRunning = ServiceUtils.isServiceRunning(this.getActivity(), HttpdService.class);
        boolean savedStatus = preferenceManager.getSharedPreferences().getBoolean(KEY_CONTROL_PANEL_STATUS, isServiceRunning);
        if (isServiceRunning || savedStatus) {
            isServiceRunning = true;
            startService();
        } else {
            isServiceRunning = false;
            url.setText(" ");
        }

        status.setChecked(isServiceRunning);

        // Register listener
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        appBroadcastReceiver = new AppBroadcastReceiver();

        showCoachMark();
    }

    private List<String> getConfiguredEmails() {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(getActivity()).getAccounts();
        List<String> emails = new ArrayList<String>(1);
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches() && !emails.contains(account.name) && account.name.toLowerCase().endsWith("gmail.com")) {
                emails.add(account.name);
            }
        }
        return emails;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConfig.SECURITY_REQ_CREATE_PATTERN: {
                if (resultCode == Activity.RESULT_OK) {
                    char[] pattern = data.getCharArrayExtra(LockPatternActivity.EXTRA_PATTERN);
                    lockPattern = String.valueOf(pattern);
                    AppController.setLockPattern(lockPattern);
                    lockPatternRequired.setEnabled(true);
                }
                break;
            }
            case AppConfig.GOOGLE_REQUEST_CODE_RESOLUTION:
                if (resultCode == Activity.RESULT_OK) {
                    setupGoogleDrive();
                }
                break;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        LOGD(TAG, "[onSharedPreferenceChanged] Key - " + key);
        if (KEY_CONTROL_PANEL_STATUS.equals(key)) {
            if (status.isChecked()) {
                startService();
            } else {
                stopService();
            }
        } else if (KEY_DEVICE_EMAIL_ADDRESS.equals(key)) {
            // Revoke the access first
            revokeGoogleDrive();

            // Setup Google Drive again
            setupGoogleDrive();

            // Reset registration status
            GcmUtils.saveRegistrationStatus(this.getActivity(), RegistrationStatus.UNREGISTERED);

            // Reregister if necessary
            ((BaseActivity) getActivity()).setupGoogleService();

        } else if (KEY_MESSAGING_AGING_METHOD.equals(key)) {
            String method = messagingAgingMethod.getValue();
            setAgingMethodControls(method);
        } else if (KEY_DEVICE_UNIQUE_NAME.equals(key)) {
            // Reset registration status
            GcmUtils.saveRegistrationStatus(this.getActivity(), RegistrationStatus.UNREGISTERED);

            // Reregister if necessary
            ((BaseActivity) getActivity()).setupGoogleService();

        } else if (KEY_DEVICE_TRACKING.equals(key)) {
            // Setup Google Drive
            setupGoogleDrive();

            if (deviceTracking.isChecked()) {
                try {
                    // By default set Google Drive storage to true also
                    AppPreference.getInstance().setValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_ALARM_IMAGE_DRIVE_STORAGE, true);

                    // Track device if service is running
                    boolean isServiceRunning = ServiceUtils.isServiceRunning(this.getActivity(), HttpdService.class);
                    if (isServiceRunning) {
                        ServiceUtils.trackDevice(getActivity(), true, url.getText());
                    }
                } catch (Exception ex) {
                    LOGE(TAG, "[onConnected] Unable to set Google Drive storage to true", ex);
                }
            }
        } else if (KEY_WIDGET_UPDATE_INTERVAL.equals(key)) {
            final int value = ValidationUtils.parseInt(widgetUpdateInterval.getText(), 0);
            if (value <= 0) {
                widgetUpdateInterval.setText(getPreferenceManager().getSharedPreferences().getString(KEY_WIDGET_UPDATE_INTERVAL, getString(R.string.default_widget_update_interval)));
                return;
            }
            if (WidgetUtils.hasInstances(getActivity())) {
                WidgetUtils.clearUpdate(getActivity());
                WidgetUtils.scheduleUpdate(getActivity());
            }
        }
        updatePrefSummary(findPreference(key));

        // Post a message using the service bus
        AppController.bus.post(new PreferenceChangedEvent());
    }

    private void setAgingMethodControls(final String method) {
        if (MessagingAgingMethod.DAYS.getHashCode().equals(method)) {
            messagingAgingDays.setEnabled(true);
            messagingAgingSize.setEnabled(false);
        } else {
            messagingAgingDays.setEnabled(false);
            messagingAgingSize.setEnabled(true);
        }
    }

    void startService() {
        ServiceSettingsFragmentPermissionsDispatcher.requestForSystemAlertWindowPermissionWithCheck(this);
        ServiceSettingsFragmentPermissionsDispatcher.requestForCameraPermissionWithCheck(this);
        ServiceSettingsFragmentPermissionsDispatcher.requestForSmsPermissionWithCheck(this);
        ServiceSettingsFragmentPermissionsDispatcher.requestForStoragePermissionWithCheck(this);
        ServiceSettingsFragmentPermissionsDispatcher.requestForContactPermissionWithCheck(this);
        ServiceSettingsFragmentPermissionsDispatcher.requestForAudioPermissionWithCheck(this);
        ServiceSettingsFragmentPermissionsDispatcher.requestForCallPermissionWithCheck(this);
        ServiceSettingsFragmentPermissionsDispatcher.requestForLocationPermissionWithCheck(this);
        startServiceWithPermissions();
    }

    private void stopService() {
        // Unbind the service
        unBindService();

        // Check if camera service is running
        boolean isRunning = ServiceUtils.isServiceRunning(getActivity(), WebcamService.class);
        if (isRunning) {
            ServiceUtils.stopCameraService(getActivity());
        }

        // Stop the service
        boolean isStopped = ServiceUtils.stopHttpdService(getActivity());
        if (!isStopped) {
            LOGW(TAG, "[onSharedPreferenceChanged] Unable to stop service");
        }
        // Reset the URL
        url.setText(" ");
    }

    @Override
    public void onPause() {
        super.onPause();

        /*
        if (isAppReceiverRegistered) {
            getActivity().unregisterReceiver(appBroadcastReceiver);
            isAppReceiverRegistered = false;
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isAppReceiverRegistered) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(AppConfig.INTENT_IP_ADDRESS_CHANGE_ACTION);
            filter.addAction(AppConfig.INTENT_SHUTDOWN_SERVICE_ACTION);
            getActivity().registerReceiver(appBroadcastReceiver, filter);
            isAppReceiverRegistered = true;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isAppReceiverRegistered) {
            getActivity().unregisterReceiver(appBroadcastReceiver);
            isAppReceiverRegistered = false;
        }

        // Unregister listener
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        unBindService();
    }

    private void initSummary(Preference p) {
        if (p instanceof PreferenceCategory) {
            PreferenceCategory pCat = (PreferenceCategory) p;
            for (int i = 0; i < pCat.getPreferenceCount(); i++) {
                initSummary(pCat.getPreference(i));
            }
        } else if (p instanceof PreferenceScreen) {
            LOGD(TAG, "Nested preference screen");
            PreferenceScreen preferenceScreen = (PreferenceScreen) p;
            for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
                initSummary(preferenceScreen.getPreference(i));
            }
        } else {
            initPreference(p);
        }
    }

    private void updatePrefSummary(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            p.setSummary(listPref.getEntry());
        } else if (p instanceof NonEmptyEditTextPreference) {
            NonEmptyEditTextPreference editTextPref = (NonEmptyEditTextPreference) p;
            p.setSummary(editTextPref.getText());
        } else if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            p.setSummary(editTextPref.getText());
        } else if (p instanceof SeekBarDialogPreference) {
            SeekBarDialogPreference seekBar = (SeekBarDialogPreference) p;
            seekBar.setSummary(String.valueOf(seekBar.getProgress()));
        } else if (p instanceof NonEmptyEditTextPreference) {
            NonEmptyEditTextPreference nonEmptyEditTextPreference = (NonEmptyEditTextPreference) p;
            p.setSummary(nonEmptyEditTextPreference.getText());
        }
    }

    private void initPreference(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            if (!TextUtils.isEmpty(listPref.getEntry()))
                p.setSummary(listPref.getEntry());
        } else if (p instanceof NonEmptyEditTextPreference) {
            NonEmptyEditTextPreference editTextPref = (NonEmptyEditTextPreference) p;
            if (!TextUtils.isEmpty(editTextPref.getText())) {
                p.setSummary(editTextPref.getText());
            }
        } else if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            if (!TextUtils.isEmpty(editTextPref.getText()))
                p.setSummary(editTextPref.getText());
        } else if (p instanceof SeekBarDialogPreference) {
            SeekBarDialogPreference seekBar = (SeekBarDialogPreference) p;
            seekBar.setSummary(String.valueOf(seekBar.getProgress()));
        } else if (p instanceof NonEmptyEditTextPreference) {
            NonEmptyEditTextPreference nonEmptyEditTextPreference = (NonEmptyEditTextPreference) p;
            if (!TextUtils.isEmpty(nonEmptyEditTextPreference.getText()))
                p.setSummary(nonEmptyEditTextPreference.getText());
        }
    }


    protected void bindService() {
        if (httpdServiceProvider == null) {
            // Intent intent = UIUtils.createExplicitFromImplicitIntent(getActivity(), new Intent(IHttpdService.class.getName()));
            Intent intent = new Intent(IHttpdService.class.getName());
            intent.setPackage(AppConfig.PACKAGE_NAME);
            getActivity().bindService(intent, this.serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    protected void unBindService() {
        if (httpdServiceProvider != null) {
            getActivity().unbindService(serviceConnection);
            httpdServiceProvider = null;
        }
    }

    protected ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            httpdServiceProvider = IHttpdService.Stub.asInterface(binder);
            statusHandler.sendEmptyMessage(HTTPD_SERVICE_CONNECTED);
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            httpdServiceProvider = null;
            statusHandler.sendEmptyMessage(HTTPD_SERVICE_DISCONNECTED);
        }
    };

    /**
     * Called when {@code mGoogleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        LOGI(TAG, "GoogleApiClient connected");
    }

    /**
     * Called when {@code mGoogleApiClient} is disconnected.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        LOGI(TAG, "GoogleApiClient connection suspended");
    }

    /**
     * Called when {@code googleApiClient} is trying to connect but failed.
     * Handle {@code result.parse()} if there is a resolution is
     * available.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        try {
            LOGI(TAG, "GoogleApiClient connection failed: " + result.toString());
            if (!result.hasResolution()) {
                try {
                    // show the localized error dialog.
                    GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this.getActivity(), 0).show();
                } catch (Exception ex) {
                    LOGE(TAG, "[onConnectionFailed] Error showing error dialog", ex);
                }
                return;
            }
        } catch (Exception ex) {
            LOGE(TAG, "[onConnectionFailed] Exception", ex);
        }
        try {
            result.startResolutionForResult(this.getActivity(), AppConfig.GOOGLE_REQUEST_CODE_RESOLUTION);
        } catch (Exception e) {
            LOGE(TAG, "[onConnectionFailed] Exception while starting resolution activity", e);
        }
    }

    public void revokeGoogleDrive() {
        if (AppController.googleApiClient != null && AppController.googleApiClient.isConnected()) {
            AppController.googleApiClient.clearDefaultAccountAndReconnect();
            AppController.googleApiClient.disconnect();
            AppController.googleApiClient = null;
        }
    }

    public void setupGoogleDrive() {

        if (AppController.googleApiClient != null && (AppController.googleApiClient.isConnected() || AppController.googleApiClient.isConnecting())) {
            return;
        }

        // Check if Google Drive integration is enabled
        final String gmail = getPreferenceManager().getSharedPreferences().getString(KEY_DEVICE_EMAIL_ADDRESS, this.getString(R.string.default_device_email_address));
        final AppPreference prefs = AppPreference.getInstance();
        final boolean isGoogleDriveEnabled = prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_ALARM_IMAGE_CLOUD_STORAGE, Boolean.valueOf(this.getString(R.string.default_alarm_image_drive_storage)));
        final boolean isDeviceTrackingEnabled = getPreferenceManager().getSharedPreferences().getBoolean(KEY_DEVICE_TRACKING, Boolean.valueOf(this.getString(R.string.default_device_tracking)));

        if ((isGoogleDriveEnabled || isDeviceTrackingEnabled) && !TextUtils.isEmpty(gmail)) {
            if (AppController.googleApiClient == null) {
                AppController.googleApiClient = new GoogleApiClient.Builder(this.getActivity())
                        .addApi(Drive.API).addScope(Drive.SCOPE_FILE)
                        .addApi(LocationServices.API)
                        .setAccountName(gmail)
                        .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
            }
            if (AppController.googleApiClient != null) {
                if (!AppController.googleApiClient.isConnected() && !AppController.googleApiClient.isConnecting()) {
                    AppController.googleApiClient.connect();
                }
            }
        }
    }

    public void showCoachMark() {
        try {
            final AppPreference prefs = AppPreference.getInstance();
            boolean ranBefore = prefs.getValue(AppPreference.APP_SHARED_PREF_NAMES, AppPreference.KEY_SHOWN_COACH_MARK, false);
            if (!ranBefore) {
                final int orientation = getActivity().getRequestedOrientation();
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.coach_mark);
                dialog.setCanceledOnTouchOutside(true);
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                View masterView = dialog.findViewById(R.id.coach_mark_master_view);
                masterView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        prefs.setValue(AppPreference.APP_SHARED_PREF_NAMES, AppPreference.KEY_SHOWN_COACH_MARK, true);
                        getActivity().setRequestedOrientation(orientation);
                    }
                });
                dialog.show();
            }
        } catch (Exception ex) {
            LOGE(TAG, "[showCoachMark] Exception showing coach mark", ex);
        }
    }

    private class LoadApnDefaultsTask extends AsyncTask<Void, Void, LegacyMmsConnection.Apn> {

        @Override
        protected LegacyMmsConnection.Apn doInBackground(Void... params) {
            try {
                final Context context = getActivity();
                if (context != null) {
                    return ApnDatabase.getInstance(context).getDefaultApnParameters(TelephonyUtils.getMccMnc(context), TelephonyUtils.getApn(context));
                }
            } catch (IOException e) {
                LOGE(TAG, "[doInBackground] Unable to retrieve default APN", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(LegacyMmsConnection.Apn apnDefaults) {
            if (apnDefaults != null) {
                apnMmsc.setText(ValidationUtils.getString(apnDefaults.getMmsc()));
                apnMmsProxy.setText(ValidationUtils.getString(apnDefaults.getProxy()));
                apnMmsPort.setText(String.valueOf(apnDefaults.getPort()));
                apnMmsUser.setText(ValidationUtils.getString(apnDefaults.getUsername()));
                apnMmsPassword.setText(ValidationUtils.getString(apnDefaults.getPassword()));
                apnMmsUserAgent.setText(LegacyMmsConnection.USER_AGENT);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // NOTE: delegate the permission handling to generated method
        ServiceSettingsFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @NeedsPermission(Manifest.permission.SYSTEM_ALERT_WINDOW)
    void requestForSystemAlertWindowPermission() {

    }

    @NeedsPermission(Manifest.permission.CAMERA)
    void requestForCameraPermission() {

    }


    @NeedsPermission({Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS})
    void requestForContactPermission() {

    }

    @NeedsPermission({Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS})
    void requestForAudioPermission() {

    }

    @NeedsPermission({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void requestForLocationPermission() {

    }

    @NeedsPermission({Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG})
    void requestForCallPermission() {

    }

    @NeedsPermission({Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS})
    void requestForSmsPermission() {

    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void requestForStoragePermission() {

    }

    void startServiceWithPermissions() {
        int controlPanelPort = Integer.parseInt(getPreferenceManager().getSharedPreferences().getString(KEY_CONTROL_PANEL_PORT, this.getString(R.string.default_control_panel_http_port)));
        // Start the service
        ComponentName cn = ServiceUtils.startHttpdService(getActivity(), controlPanelPort, loginRequired.isChecked(), userName.getText(), userPassword.getText(), disableNotification.isChecked(), primaryAddressFamily.getValue());
        if (cn != null) {

            // Bind to the service
            bindService();

            // Show the service status
            // showStatus(); // Changed to use handler
        } else {
            LOGW(TAG, "[onSharedPreferenceChanged] Unable to start service");
        }


        // Testing
        //if (!ServiceUtils.isServiceRunning(getActivity(), WebcamService.class)) {
        //    cn = ServiceUtils.startCameraService(getActivity());
        //    if (cn == null) {
        //        LOGE(TAG, "Problem starting service");
        //    }
        //}
    }

    @OnShowRationale(Manifest.permission.SYSTEM_ALERT_WINDOW)
    void showSystemAlertWindowPermissionRationale(PermissionRequest request) {
        showRationaleDialog(R.string.msg_system_alert_window_permission_rationale, request);
    }

    @OnPermissionDenied(Manifest.permission.SYSTEM_ALERT_WINDOW)
    void onSystemAlertWindowPermissionDenied() {
        ToastUtils.toastShort(getActivity(), R.string.msg_system_alert_window_permission_denied);
    }

    @OnNeverAskAgain(Manifest.permission.SYSTEM_ALERT_WINDOW)
    void onSystemAlertWindowPermissionNeverAskAgain() {
        ToastUtils.toastShort(getActivity(), R.string.msg_system_alert_window_permission_never_ask_again);
    }

    @OnShowRationale({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void showLocationPermissionRationale(PermissionRequest request) {
        showRationaleDialog(R.string.msg_location_permission_rationale, request);
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void onLocationPermissionDenied() {
        ToastUtils.toastShort(getActivity(), R.string.msg_location_permission_denied);
    }

    @OnNeverAskAgain({Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION})
    void onLocationPermissionNeverAskAgain() {
        ToastUtils.toastShort(getActivity(), R.string.msg_location_permission_never_ask_again);
    }

    @OnShowRationale({Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS})
    void showAudioPermissionRationale(PermissionRequest request) {
        showRationaleDialog(R.string.msg_audio_permission_rationale, request);
    }

    @OnPermissionDenied({Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS})
    void onAudioPermissionDenied() {
        ToastUtils.toastShort(getActivity(), R.string.msg_audio_permission_denied);
    }

    @OnNeverAskAgain({Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS})
    void onAudioPermissionNeverAskAgain() {
        ToastUtils.toastShort(getActivity(), R.string.msg_audio_permission_never_ask_again);
    }

    @OnShowRationale({Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG})
    void showCallPermissionRationale(PermissionRequest request) {
        showRationaleDialog(R.string.msg_call_permission_rationale, request);
    }

    @OnPermissionDenied({Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG})
    void onCallPermissionDenied() {
        ToastUtils.toastShort(getActivity(), R.string.msg_call_permission_denied);
    }

    @OnNeverAskAgain({Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG})
    void onCallPermissionNeverAskAgain() {
        ToastUtils.toastShort(getActivity(), R.string.msg_call_permission_never_ask_again);
    }

    @OnShowRationale({Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS})
    void showSmsPermissionRationale(PermissionRequest request) {
        showRationaleDialog(R.string.msg_sms_permission_rationale, request);
    }

    @OnPermissionDenied({Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS})
    void onSmsPermissionDenied() {
        ToastUtils.toastShort(getActivity(), R.string.msg_sms_permission_denied);
    }

    @OnNeverAskAgain({Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS})
    void onSmsPermissionNeverAskAgain() {
        ToastUtils.toastShort(getActivity(), R.string.msg_sms_permission_never_ask_again);
    }

    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void showStoragePermissionRationale(PermissionRequest request) {
        showRationaleDialog(R.string.msg_write_external_storage_permission_rationale, request);
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void onStoragePermissionDenied() {
        ToastUtils.toastShort(getActivity(), R.string.msg_write_external_storage_permission_denied);
    }

    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void onStoragePermissionNeverAskAgain() {
        ToastUtils.toastShort(getActivity(), R.string.msg_write_external_storage_permission_never_ask_again);
    }


    @OnShowRationale(Manifest.permission.CAMERA)
    void showCameraPermissionRationale(PermissionRequest request) {
        showRationaleDialog(R.string.msg_camera_permission_rationale, request);
    }

    @OnPermissionDenied(Manifest.permission.CAMERA)
    void onCameraPermissionDenied() {
        ToastUtils.toastShort(getActivity(), R.string.msg_camera_permission_denied);
    }

    @OnNeverAskAgain(Manifest.permission.CAMERA)
    void onCameraPermissionNeverAskAgain() {
        ToastUtils.toastShort(getActivity(), R.string.msg_camera_permission_never_ask_again);
    }

    @OnShowRationale({Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS})
    void showContactPermissionRationale(PermissionRequest request) {
        // NOTE: Show a rationale to explain why the permission is needed, e.g. with a dialog.
        // Call proceed() or cancel() on the provided PermissionRequest to continue or abort
        showRationaleDialog(R.string.msg_contact_permission_rationale, request);
    }

    @OnPermissionDenied({Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS})
    void onContactPermissionDenied() {
        // NOTE: Deal with a denied permission, e.g. by showing specific UI
        // or disabling certain functionality
        ToastUtils.toastShort(getActivity(), R.string.msg_contact_permission_denied);
    }

    @OnNeverAskAgain({Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS})
    void onContactPermissionNeverAskAgain() {
        ToastUtils.toastShort(getActivity(), R.string.msg_contact_permission_never_ask_again);
    }

    void showRationaleDialog(@StringRes int messageResId, final PermissionRequest request) {
        new AlertDialog.Builder(this.getActivity())
                .setPositiveButton(R.string.label_button_allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.label_button_deny, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        request.cancel();
                    }
                })
                .setCancelable(false)
                .setMessage(messageResId)
                .show();
    }
}

