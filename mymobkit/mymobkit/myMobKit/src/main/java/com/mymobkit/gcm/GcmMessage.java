package com.mymobkit.gcm;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.mymobkit.app.AppConfig;
import com.mymobkit.app.AppController;
import com.mymobkit.common.AppPreference;
import com.mymobkit.common.DeviceUtils;
import com.mymobkit.common.GcmUtils;
import com.mymobkit.ui.fragment.ServiceSettingsFragment;

public abstract class GcmMessage {

    public enum ActionType {

        WAKE_UP("0"),
        MOTION_DETECTION("1"),
        DEVICE_INFO("2"),
        SERVICE("3"),
        SURVEILLANCE("4"),
        SWITCH_CAMERA("5");

        private String type;

        ActionType(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }

        public static ActionType get(final String code) {
            if (MOTION_DETECTION.getType().equals(code)) {
                return MOTION_DETECTION;
            } else if (DEVICE_INFO.getType().equals(code)) {
                return DEVICE_INFO;
            } else if (SERVICE.getType().equals(code)) {
                return SERVICE;
            } else if (SURVEILLANCE.getType().equals(code)) {
                return SURVEILLANCE;
            } else if (SWITCH_CAMERA.getType().equals(code)) {
                return SWITCH_CAMERA;
            } else {
                return WAKE_UP;
            }
        }
    }

    public enum ActionCommand {

        UNKNOWN("-1"),
        START("0"),
        STOP("1"),
        FRONT_CAMERA("2"),
        REAR_CAMERA("3");

        private String type;

        ActionCommand(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }

        public static ActionCommand get(final String code) {
            if (START.getType().equals(code)) {
                return START;
            } else if (STOP.getType().equals(code)) {
                return STOP;
            } else if (FRONT_CAMERA.getType().equals(code)) {
                return FRONT_CAMERA;
            } else if (REAR_CAMERA.getType().equals(code)) {
                return REAR_CAMERA;
            } else {
                return UNKNOWN;
            }
        }
    }

    @Expose
    protected String action;

    @Expose
    protected String deviceId;

    @Expose
    protected String deviceName;

    @Expose
    protected String registrationId;

    @Expose
    protected int registrationVersion;

    @Expose
    protected String email;

    @Expose
    protected Long timestamp;

    protected Context context;

    public GcmMessage(final Context context) {
        this.context = context;

        // Registration id, app version, email and device id
        final AppPreference prefs = AppPreference.getInstance();
        this.registrationId = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, GcmUtils.PARAMETER_REG_ID, "");
        this.registrationVersion = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, GcmUtils.PARAMETER_APP_VERSION, Integer.MIN_VALUE);
        this.email = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_DEVICE_EMAIL_ADDRESS, "");
        this.deviceName = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_DEVICE_UNIQUE_NAME, AppController.getDeviceName());
        this.deviceId = DeviceUtils.getDeviceId(context);
        this.timestamp = System.currentTimeMillis();
    }

    public String getAction() {
        return action;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public int getRegistrationVersion() {
        return registrationVersion;
    }

    public String getEmail() {
        return email;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String toJson() {
        Gson gson = new GsonBuilder().setDateFormat(AppConfig.UNIVERSAL_DATE_FORMAT).excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

    @Override
    public String toString() {
        return "GcmMessage{" +
                "action='" + action + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", registrationId='" + registrationId + '\'' +
                ", registrationVersion=" + registrationVersion +
                ", email='" + email + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    public abstract String getDescription();
}
