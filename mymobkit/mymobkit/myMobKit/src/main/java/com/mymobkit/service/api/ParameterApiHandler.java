package com.mymobkit.service.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mymobkit.R;
import com.mymobkit.app.AppConfig;
import com.mymobkit.common.AppPreference;
import com.mymobkit.common.DeviceUtils;
import com.mymobkit.model.PreferenceChangedEvent;
import com.mymobkit.service.HttpdService;
import com.mymobkit.service.api.parameter.GetRequest;
import com.mymobkit.service.api.parameter.Parameter;
import com.mymobkit.service.api.parameter.Parameter.DataType;
import com.mymobkit.service.api.parameter.PostRequest;
import com.mymobkit.ui.fragment.DetectionSettingsFragment;
import com.mymobkit.ui.fragment.ServiceSettingsFragment;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ParameterApiHandler extends ApiHandler {

    private Map<String, Parameter> parameters;

    private List<String> systemParameters;

    private static final String SYSTEM_PARAMETER_DEVICE_ID = "system_device_id";

    public static final String PARAM_KEY = "key";

    public ParameterApiHandler(final HttpdService service) {
        super(service);
        parameters = new HashMap<String, Parameter>(3);
        systemParameters = new ArrayList<String>(1);
        loadSettings();
    }

    @Override
    public String get(final Map<String, String> header, final Map<String, String> params, final Map<String, String> files) {
        GetRequest request = new GetRequest();
        String key = getStringValue(PARAM_KEY, params);
        try {
            maybeAcquireWakeLock();
            if (parameters.containsKey(key)) {
                Parameter param = parameters.get(key);
                AppPreference prefs = AppPreference.getInstance();
                String value = "";
                if (param.getDataType() == DataType.STRING) {
                    value = prefs.getValue(param.getSharedPrefsName(), param.getName(), "");
                } else if (param.getDataType() == DataType.INTEGER) {
                    value = String.valueOf(prefs.getValue(param.getSharedPrefsName(), param.getName(), -1));
                } else if (param.getDataType() == DataType.BOOLEAN) {
                    value = Boolean.valueOf(prefs.getValue(param.getSharedPrefsName(), param.getName(), false)).toString();
                }
                request.addParameter(key, value);
                request.isSuccessful = true;
            } else {
                if (systemParameters.contains(key)) {
                    if (SYSTEM_PARAMETER_DEVICE_ID.equalsIgnoreCase(key)) {
                        final String deviceId = DeviceUtils.getDeviceId(getContext());
                        request.addParameter(key, deviceId);
                    }
                    request.isSuccessful = true;
                } else {
                    request.isSuccessful = false;
                    request.setDescription(String.format(getContext().getString(R.string.msg_no_matched_parameter), String.valueOf(key)));
                }
            }
        } catch (Exception ex) {
            request.isSuccessful = false;
            request.setDescription(String.format(getContext().getString(R.string.msg_error_retrieve_parameter), String.valueOf(key)));
        } finally {
            releaseWakeLock();
        }
        Gson gson = new GsonBuilder().setDateFormat(AppConfig.UNIVERSAL_DATE_FORMAT).excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(request);
    }

    @Override
    public String post(final Map<String, String> header, final Map<String, String> params, final Map<String, String> files) {
        PostRequest request = new PostRequest();
        try {
            maybeAcquireWakeLock();
            for (String key : params.keySet()) {
                if (parameters.containsKey(key)) {
                    Parameter param = parameters.get(key);
                    String value = params.get(key);
                    AppPreference prefs = AppPreference.getInstance();
                    //Editor editor = prefs.edit();
                    if (param.getDataType() == DataType.BOOLEAN) {
                        prefs.setValue(param.getSharedPrefsName(), key, Boolean.valueOf(value));
                    } else if (param.getDataType() == DataType.INTEGER) {
                        prefs.setValue(param.getSharedPrefsName(), key, Integer.valueOf(value));
                    } else if (param.getDataType() == DataType.STRING) {
                        prefs.setValue(param.getSharedPrefsName(), key, value);
                    } else {
                        // Default to string
                        prefs.setValue(param.getSharedPrefsName(), key, value);
                    }

                    //if (editor.commit()) {
                    request.isSuccessful = true;
                    //} else {
                    //    request.isSuccessful = false;
                    //    request.setDescription(String.format(getContext().getString(R.string.msg_error_commit_parameter)));
                    //}
                } else {
                    request.setDescription(String.format(getContext().getString(R.string.msg_no_matched_parameter), String.valueOf(key)));
                }
            }
        } catch (Exception ex) {
            request.isSuccessful = false;
            request.setDescription(String.format(getContext().getString(R.string.msg_error_commit_parameter)));
        } finally {
            releaseWakeLock();
        }
        Gson gson = new GsonBuilder().setDateFormat(AppConfig.UNIVERSAL_DATE_FORMAT).excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(request);
    }

    private void loadSettings(){
        parameters.clear();
        parameters.put(ServiceSettingsFragment.KEY_SAVE_SENT_MESSAGES.toLowerCase(), new Parameter(ServiceSettingsFragment.KEY_SAVE_SENT_MESSAGES, ServiceSettingsFragment.SHARED_PREFS_NAME, DataType.BOOLEAN));
        parameters.put(ServiceSettingsFragment.KEY_MESSAGING_AGING_METHOD.toLowerCase(), new Parameter(ServiceSettingsFragment.KEY_MESSAGING_AGING_METHOD, ServiceSettingsFragment.SHARED_PREFS_NAME, DataType.STRING));
        parameters.put(ServiceSettingsFragment.KEY_MESSAGING_AGING_DAYS.toLowerCase(), new Parameter(ServiceSettingsFragment.KEY_MESSAGING_AGING_DAYS, ServiceSettingsFragment.SHARED_PREFS_NAME, DataType.INTEGER));
        parameters.put(ServiceSettingsFragment.KEY_MESSAGING_AGING_SIZE.toLowerCase(), new Parameter(ServiceSettingsFragment.KEY_MESSAGING_AGING_SIZE, ServiceSettingsFragment.SHARED_PREFS_NAME, DataType.INTEGER));
        parameters.put(ServiceSettingsFragment.KEY_APN_MMSC.toLowerCase(), new Parameter(ServiceSettingsFragment.KEY_APN_MMSC, ServiceSettingsFragment.SHARED_PREFS_NAME, DataType.STRING));
        parameters.put(ServiceSettingsFragment.KEY_APN_MMS_PROXY.toLowerCase(), new Parameter(ServiceSettingsFragment.KEY_APN_MMS_PROXY, ServiceSettingsFragment.SHARED_PREFS_NAME, DataType.STRING));
        parameters.put(ServiceSettingsFragment.KEY_APN_MMS_PORT.toLowerCase(), new Parameter(ServiceSettingsFragment.KEY_APN_MMS_PORT, ServiceSettingsFragment.SHARED_PREFS_NAME, DataType.STRING));
        parameters.put(ServiceSettingsFragment.KEY_APN_MMS_USER.toLowerCase(), new Parameter(ServiceSettingsFragment.KEY_APN_MMS_USER, ServiceSettingsFragment.SHARED_PREFS_NAME, DataType.STRING));
        parameters.put(ServiceSettingsFragment.KEY_APN_MMS_PASSWORD.toLowerCase(), new Parameter(ServiceSettingsFragment.KEY_APN_MMS_PASSWORD, ServiceSettingsFragment.SHARED_PREFS_NAME, DataType.STRING));
        parameters.put(ServiceSettingsFragment.KEY_APN_MMS_USER_AGENT.toLowerCase(), new Parameter(ServiceSettingsFragment.KEY_APN_MMS_USER_AGENT, ServiceSettingsFragment.SHARED_PREFS_NAME, DataType.STRING));
        parameters.put(ServiceSettingsFragment.KEY_DEVICE_UNIQUE_NAME.toLowerCase(), new Parameter(ServiceSettingsFragment.KEY_DEVICE_UNIQUE_NAME, ServiceSettingsFragment.SHARED_PREFS_NAME, DataType.STRING));
        parameters.put(ServiceSettingsFragment.KEY_DEVICE_EMAIL_ADDRESS.toLowerCase(), new Parameter(ServiceSettingsFragment.KEY_DEVICE_EMAIL_ADDRESS, ServiceSettingsFragment.SHARED_PREFS_NAME, DataType.STRING));
        parameters.put(ServiceSettingsFragment.KEY_DEVICE_TRACKING.toLowerCase(), new Parameter(ServiceSettingsFragment.KEY_DEVICE_TRACKING, ServiceSettingsFragment.SHARED_PREFS_NAME, DataType.BOOLEAN));
        parameters.put(DetectionSettingsFragment.KEY_ALARM_IMAGE_DRIVE_STORAGE.toLowerCase(), new Parameter(DetectionSettingsFragment.KEY_ALARM_IMAGE_DRIVE_STORAGE, DetectionSettingsFragment.SHARED_PREFS_NAME, DataType.BOOLEAN));

        systemParameters.clear();
        systemParameters.add(SYSTEM_PARAMETER_DEVICE_ID);
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Subscribe
    public void onPreferenceChanged(final PreferenceChangedEvent event) {
        loadSettings();
    }
}
