package com.mymobkit.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager;
import android.media.CamcorderProfile;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationServices;
import com.mymobkit.R;
import com.mymobkit.app.AppConfig;
import com.mymobkit.app.AppController;
import com.mymobkit.common.AppPreference;
import com.mymobkit.common.DeviceUtils;
import com.mymobkit.common.PlatformUtils;
import com.mymobkit.common.StorageUtils;
import com.mymobkit.common.ToastUtils;
import com.mymobkit.enums.MotionDetectionType;
import com.mymobkit.model.PreferenceChangedEvent;
import com.mymobkit.preference.NonEmptyEditTextPreference;
import com.mymobkit.preference.SeekBarDialogPreference;
import com.mymobkit.ui.base.PreferenceFragmentBase;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.LOGI;
import static com.mymobkit.common.LogUtils.LOGD;
import static com.mymobkit.common.LogUtils.makeLogTag;

public final class DetectionSettingsFragment extends PreferenceFragmentBase
        implements OnSharedPreferenceChangeListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = makeLogTag(DetectionSettingsFragment.class);


    // Motion settings
    public static final String KEY_MOTION_DETECTION_TYPE = "preferences_motion_detection_type";
    public static final String KEY_MOTION_DETECTION = "preferences_motion_detection";
    public static final String KEY_MOTION_DETECTION_RECORD_VIDEO = "preferences_motion_detection_record_video";
    public static final String KEY_MOTION_DETECTION_RECORD_VIDEO_DURATION_SECONDS = "preferences_motion_detection_record_video_duration_seconds";

    // Face detection
    //public static final String KEY_FACE_DETECTION = "preferences_face_detection";
    public static final String KEY_FACE_DETECTION_SIZE = "preferences_face_detection_size";

    // Motion detection
    public static final String KEY_MOTION_DETECTION_THRESHOLD = "preferences_motion_detection_threshold";
    public static final String KEY_MOTION_DETECTION_ALGORITHM = "preferences_motion_detection_algorithm";
    public static final String KEY_MOTION_DETECTION_CONTOUR_THICKNESS = "preferences_motion_detection_contour_thickness";


    public static final String KEY_NOTIFY_BY_GCM = "preferences_notify_by_gcm";
    public static final String KEY_NOTIFY_BY_EMAIL = "preferences_notify_by_email";
    public static final String KEY_NOTIFY_BY_SMS = "preferences_notify_by_sms";

    public static final String KEY_ALARM_TRIGGER_INTERVAL = "preferences_alarm_trigger_interval";
    public static final String KEY_ALARM_NO_OF_TRIGGERS = "preferences_alarm_number_of_triggers";
    public static final String KEY_ALARM_SOUND_TYPE = "preferences_alarm_sound_type";
    public static final String KEY_ALARM_IMAGE_LOCAL_STORAGE = "preferences_alarm_image_local_storage";
    public static final String KEY_ALARM_IMAGE_CLOUD_STORAGE = "preferences_alarm_image_cloud_storage";
    public static final String KEY_ALARM_IMAGE_EMAIL_STORAGE = "preferences_alarm_image_email_storage";
    public static final String KEY_ALARM_IMAGE_DRIVE_STORAGE = "preferences_alarm_image_drive_storage";
    public static final String KEY_GOOGLE_DRIVE_DAYS_TO_KEEP = "preferences_google_drive_days_to_keep";
    public static final String KEY_STORAGE_LOCATION = "preferences_storage_location";

    public static final String KEY_VIDEO_FORMAT = "preferences_video_format";
    public static final String KEY_VIDEO_ENCODER = "preferences_video_encoder";
    public static final String KEY_VIDEO_RESOLUTION = "preferences_video_resolution";
    public static final String KEY_VIDEO_STABILIZATION = "preferences_video_stabilization";
    public static final String KEY_VIDEO_CHUNK_SIZE_MINUTES = "preferences_video_chunk_size_minutes";
    public static final String KEY_VIDEO_BITRATE = "preferences_video_bitrate";
    public static final String KEY_VIDEO_FRAME_RATE = "preferences_video_frame_rate";
    public static final String KEY_VIDEO_HOUSEKEEPING_MB = "preferences_video_housekeeping_mb";

    public static final String KEY_AUDIO_ENCODER = "preferences_audio_encoder";
    public static final String KEY_AUDIO_BITRATE = "preferences_audio_bitrate";
    public static final String KEY_AUDIO_SOURCE = "preferences_audio_src";

    public static final String KEY_SHOW_GCM_NOTIFICATION = "preferences_show_gcm_notification";
    public static final String KEY_CAMERA_RESOLUTION = "preferences_camera_resolution";
    public static final String KEY_BACKGROUND_CAMERA = "preferences_background_camera";

    public static final String KEY_VIDEO_STREAMING_PORT = "preferences_video_streaming_port";
    public static final String KEY_VIDEO_STREAMING_IMAGE_QUALITY = "preferences_video_streaming_image_quality";
    public static final String KEY_STREAM_DETECTED_OBJECT = "preferences_stream_detected_object";
    public static final String KEY_DISGUISE_CAMERA = "preferences_disguise_camera";
    public static final String KEY_STEALTH_MODE = "preferences_stealth_mode";

    public static final String SHARED_PREFS_NAME = "camera_settings";

    private ListPreference motionDetectionType;
    private CheckBoxPreference motionDetect;
    private SeekBarDialogPreference motionDetectionThreshold;
    //private CheckBoxPreference faceDetect;
    private ListPreference faceDetectionSize;
    private ListPreference motionDetectionAlgorithm;
    private SeekBarDialogPreference motionDetectionContourThickness;

    private CheckBoxPreference notifyByGcm;
    private CheckBoxPreference showGcmNotification;
    private CheckBoxPreference notifyByEmail;
    private CheckBoxPreference notifyBySms;
    private ListPreference alarmTriggerInterval;
    private SeekBarDialogPreference noOfTriggers;
    private RingtonePreference alarmSoundType;
    private ListPreference storageLocation;
    private CheckBoxPreference localStorage;
    private CheckBoxPreference cloudStorage;
    private CheckBoxPreference emailStorage;
    private CheckBoxPreference driveStorage;
    private CheckBoxPreference backgroundCamera;
    private SeekBarDialogPreference driveStorageDaysToKeep;
    private CheckBoxPreference motionDetectionRecordVideo;
    private EditTextPreference motionDetectionRecordVideoDurationSeconds;
    private ListPreference videoFormat;
    private ListPreference videoResolution;
    private ListPreference cameraResolution;
    private ListPreference videoEncoder;
    private CheckBoxPreference videoStabilization;
    private EditTextPreference videoChunkSizeMinutes;
    private EditTextPreference videoBitrate;
    private EditTextPreference videoFrameRate;
    private EditTextPreference videoHousekeepingMb;
    private ListPreference audioEncoder;
    private EditTextPreference audioBitrate;
    private ListPreference audioSource;

    private EditTextPreference streamingPort;
    private SeekBarDialogPreference streamingImageQuality;
    private CheckBoxPreference streamDetectedObject;
    private CheckBoxPreference disguiseCamera;
    private CheckBoxPreference stealthMode;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        final AppPreference prefs = AppPreference.getInstance();
        if (!prefs.getValue(AppPreference.APP_SHARED_PREF_NAMES, SHARED_PREFS_NAME, false)) {
            PreferenceManager.setDefaultValues(getActivity(), SHARED_PREFS_NAME, Context.MODE_PRIVATE, R.xml.pref_detection_settings, true);
            prefs.setValue(AppPreference.APP_SHARED_PREF_NAMES, SHARED_PREFS_NAME, true);
        }

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_detection_settings);

        // Configure default settings
        AppController.getDefaultSettings().configure();

        final PreferenceManager preferenceManager = getPreferenceManager();
        preferenceManager.setSharedPreferencesName(SHARED_PREFS_NAME);

        final PreferenceScreen preferences = getPreferenceScreen();
        motionDetectionType = (ListPreference) preferences.findPreference(KEY_MOTION_DETECTION_TYPE);
        motionDetect = (CheckBoxPreference) preferences.findPreference(KEY_MOTION_DETECTION);
        motionDetectionThreshold = (SeekBarDialogPreference) preferences.findPreference(KEY_MOTION_DETECTION_THRESHOLD);
        //faceDetect = (CheckBoxPreference) preferences.findPreference(KEY_FACE_DETECTION);
        faceDetectionSize = (ListPreference) preferences.findPreference(KEY_FACE_DETECTION_SIZE);
        motionDetectionAlgorithm = (ListPreference) preferences.findPreference(KEY_MOTION_DETECTION_ALGORITHM);
        motionDetectionContourThickness = (SeekBarDialogPreference) preferences.findPreference(KEY_MOTION_DETECTION_CONTOUR_THICKNESS);

        notifyByGcm = (CheckBoxPreference) preferences.findPreference(KEY_NOTIFY_BY_GCM);
        showGcmNotification = (CheckBoxPreference) preferences.findPreference(KEY_SHOW_GCM_NOTIFICATION);
        notifyByEmail = (CheckBoxPreference) preferences.findPreference(KEY_NOTIFY_BY_EMAIL);
        notifyBySms = (CheckBoxPreference) preferences.findPreference(KEY_NOTIFY_BY_SMS);
        alarmTriggerInterval = (ListPreference) preferences.findPreference(KEY_ALARM_TRIGGER_INTERVAL);
        noOfTriggers = (SeekBarDialogPreference) preferences.findPreference(KEY_ALARM_NO_OF_TRIGGERS);
        alarmSoundType = (RingtonePreference) preferences.findPreference(KEY_ALARM_SOUND_TYPE);
        storageLocation = (ListPreference) preferences.findPreference(KEY_STORAGE_LOCATION);
        localStorage = (CheckBoxPreference) preferences.findPreference(KEY_ALARM_IMAGE_LOCAL_STORAGE);
        cloudStorage = (CheckBoxPreference) preferences.findPreference(KEY_ALARM_IMAGE_CLOUD_STORAGE);
        emailStorage = (CheckBoxPreference) preferences.findPreference(KEY_ALARM_IMAGE_EMAIL_STORAGE);
        driveStorage = (CheckBoxPreference) preferences.findPreference(KEY_ALARM_IMAGE_DRIVE_STORAGE);
        backgroundCamera = (CheckBoxPreference) preferences.findPreference(KEY_BACKGROUND_CAMERA);
        driveStorageDaysToKeep = (SeekBarDialogPreference) preferences.findPreference(KEY_GOOGLE_DRIVE_DAYS_TO_KEEP);

        motionDetectionRecordVideo = (CheckBoxPreference) preferences.findPreference(KEY_MOTION_DETECTION_RECORD_VIDEO);
        motionDetectionRecordVideoDurationSeconds = (EditTextPreference) preferences.findPreference(KEY_MOTION_DETECTION_RECORD_VIDEO_DURATION_SECONDS);
        videoFormat = (ListPreference) preferences.findPreference(KEY_VIDEO_FORMAT);
        videoResolution = (ListPreference) preferences.findPreference(KEY_VIDEO_RESOLUTION);
        videoEncoder = (ListPreference) preferences.findPreference(KEY_VIDEO_ENCODER);
        videoStabilization = (CheckBoxPreference) preferences.findPreference(KEY_VIDEO_STABILIZATION);
        videoChunkSizeMinutes = (EditTextPreference) preferences.findPreference(KEY_VIDEO_CHUNK_SIZE_MINUTES);
        videoHousekeepingMb = (EditTextPreference) preferences.findPreference(KEY_VIDEO_HOUSEKEEPING_MB);
        audioEncoder = (ListPreference) preferences.findPreference(KEY_AUDIO_ENCODER);
        videoBitrate = (EditTextPreference) preferences.findPreference(KEY_VIDEO_BITRATE);
        videoFrameRate = (EditTextPreference) preferences.findPreference(KEY_VIDEO_FRAME_RATE);
        audioEncoder = (ListPreference) preferences.findPreference(KEY_AUDIO_ENCODER);
        audioBitrate = (EditTextPreference) preferences.findPreference(KEY_AUDIO_BITRATE);
        audioSource = (ListPreference) preferences.findPreference(KEY_AUDIO_SOURCE);
        cameraResolution = (ListPreference) preferences.findPreference(KEY_CAMERA_RESOLUTION);

        streamingPort = (EditTextPreference) preferences.findPreference(KEY_VIDEO_STREAMING_PORT);
        streamingImageQuality = (SeekBarDialogPreference) preferences.findPreference(KEY_VIDEO_STREAMING_IMAGE_QUALITY);
        streamDetectedObject = (CheckBoxPreference) preferences.findPreference(KEY_STREAM_DETECTED_OBJECT);
        disguiseCamera = (CheckBoxPreference) preferences.findPreference(KEY_DISGUISE_CAMERA);
        stealthMode = (CheckBoxPreference) preferences.findPreference(KEY_STEALTH_MODE);

        motionDetect.setChecked(preferenceManager.getSharedPreferences().getBoolean(KEY_MOTION_DETECTION, Boolean.valueOf(this.getString(R.string.default_motion_detection))));
        motionDetectionThreshold.setProgress(preferenceManager.getSharedPreferences().getInt(KEY_MOTION_DETECTION_THRESHOLD, Integer.valueOf(getString(R.string.default_motion_detection_threshold))));
        faceDetectionSize.setValue(preferenceManager.getSharedPreferences().getString(KEY_FACE_DETECTION_SIZE, getString(R.string.default_face_detection_size)));
        motionDetectionType.setValue(preferenceManager.getSharedPreferences().getString(KEY_MOTION_DETECTION_TYPE, getString(R.string.default_motion_detection_type)));
        motionDetectionAlgorithm.setValue(preferenceManager.getSharedPreferences().getString(KEY_MOTION_DETECTION_ALGORITHM, getString(R.string.default_motion_detection_algorithm)));
        motionDetectionContourThickness.setProgress(preferenceManager.getSharedPreferences().getInt(KEY_MOTION_DETECTION_CONTOUR_THICKNESS, Integer.valueOf(getString(R.string.default_motion_detection_contour_thickness))));

        notifyByGcm.setChecked(preferenceManager.getSharedPreferences().getBoolean(KEY_NOTIFY_BY_GCM, Boolean.valueOf(this.getString(R.string.default_notify_by_gcm))));
        showGcmNotification.setChecked(preferenceManager.getSharedPreferences().getBoolean(KEY_SHOW_GCM_NOTIFICATION, Boolean.valueOf(this.getString(R.string.default_show_gcm_notification))));
        notifyByEmail.setChecked(preferenceManager.getSharedPreferences().getBoolean(KEY_NOTIFY_BY_EMAIL, Boolean.valueOf(this.getString(R.string.default_notify_by_email))));
        notifyBySms.setChecked(preferenceManager.getSharedPreferences().getBoolean(KEY_NOTIFY_BY_SMS, Boolean.valueOf(this.getString(R.string.default_notify_by_sms))));
        alarmTriggerInterval.setValue(preferenceManager.getSharedPreferences().getString(KEY_ALARM_TRIGGER_INTERVAL, getString(R.string.default_alarm_trigger_interval)));
        noOfTriggers.setProgress(preferenceManager.getSharedPreferences().getInt(KEY_ALARM_NO_OF_TRIGGERS, Integer.valueOf(getString(R.string.default_alarm_no_of_triggers))));
        alarmSoundType.setDefaultValue(preferenceManager.getSharedPreferences().getString(KEY_ALARM_SOUND_TYPE, ""));

        localStorage.setChecked(preferenceManager.getSharedPreferences().getBoolean(KEY_ALARM_IMAGE_LOCAL_STORAGE, Boolean.valueOf(this.getString(R.string.default_alarm_image_local_storage))));
        cloudStorage.setChecked(preferenceManager.getSharedPreferences().getBoolean(KEY_ALARM_IMAGE_CLOUD_STORAGE, Boolean.valueOf(this.getString(R.string.default_alarm_image_cloud_storage))));
        emailStorage.setChecked(preferenceManager.getSharedPreferences().getBoolean(KEY_ALARM_IMAGE_EMAIL_STORAGE, Boolean.valueOf(this.getString(R.string.default_alarm_image_email_storage))));
        driveStorage.setChecked(preferenceManager.getSharedPreferences().getBoolean(KEY_ALARM_IMAGE_DRIVE_STORAGE, Boolean.valueOf(this.getString(R.string.default_alarm_image_drive_storage))));
        backgroundCamera.setChecked(preferenceManager.getSharedPreferences().getBoolean(KEY_BACKGROUND_CAMERA, Boolean.valueOf(this.getString(R.string.default_background_camera))));
        driveStorageDaysToKeep.setProgress(preferenceManager.getSharedPreferences().getInt(KEY_GOOGLE_DRIVE_DAYS_TO_KEEP, Integer.valueOf(getString(R.string.default_google_drive_days_to_keep))));
        motionDetectionRecordVideo.setChecked(preferenceManager.getSharedPreferences().getBoolean(KEY_MOTION_DETECTION_RECORD_VIDEO, Boolean.valueOf(this.getString(R.string.default_motion_detection_record_video))));
        motionDetectionRecordVideoDurationSeconds.setText(preferenceManager.getSharedPreferences().getString(KEY_MOTION_DETECTION_RECORD_VIDEO_DURATION_SECONDS, this.getString(R.string.default_motion_detection_record_video_duration_seconds)));

        // Set storage location values
        final String[] storages = StorageUtils.getAvailableStorages(getActivity());
        storageLocation.setEntries(storages);
        storageLocation.setEntryValues(storages);
        if (storages != null && storages.length > 0) {
            storageLocation.setValue(preferenceManager.getSharedPreferences().getString(KEY_STORAGE_LOCATION, storages[0]));
        }

        if (AppController.getDefaultSettings().videoSettings != null && AppController.getDefaultSettings().videoSettings.profile != null) {
            videoFormat.setValue(preferenceManager.getSharedPreferences().getString(KEY_VIDEO_FORMAT, String.valueOf(AppController.getDefaultSettings().videoSettings.profile.fileFormat)));
            videoEncoder.setValue(preferenceManager.getSharedPreferences().getString(KEY_VIDEO_ENCODER, String.valueOf(AppController.getDefaultSettings().videoSettings.profile.videoCodec)));
            videoBitrate.setText(preferenceManager.getSharedPreferences().getString(KEY_VIDEO_BITRATE, String.valueOf(AppController.getDefaultSettings().videoSettings.profile.videoBitRate)));
        }

        videoStabilization.setChecked(preferenceManager.getSharedPreferences().getBoolean(KEY_VIDEO_STABILIZATION, Boolean.valueOf(this.getString(R.string.default_video_stabilization))));


        final String[] videoSizeValues = AppController.getDefaultSettings().videoSettings.videoQuality.toArray(new String[AppController.getDefaultSettings().videoSettings.videoQuality.size()]);
        final String[] videoSizeEntries = AppController.getDefaultSettings().videoSettings.videoQualityDesc.toArray(new String[AppController.getDefaultSettings().videoSettings.videoQualityDesc.size()]);
        videoResolution.setEntries(videoSizeEntries);
        videoResolution.setEntryValues(videoSizeValues);
        if (videoSizeValues != null && videoSizeValues.length > 0) {
            videoResolution.setValue(preferenceManager.getSharedPreferences().getString(KEY_VIDEO_RESOLUTION, AppController.getDefaultSettings().videoSettings.previewVideoQuality));
        }

        final String[] resolutions = AppController.getDefaultSettings().cameraSettings.resolutions.toArray(new String[AppController.getDefaultSettings().cameraSettings.resolutions.size()]);
        cameraResolution.setEntries(resolutions);
        cameraResolution.setEntryValues(resolutions);
        if (resolutions != null && resolutions.length > 0) {
            cameraResolution.setValue(preferenceManager.getSharedPreferences().getString(KEY_CAMERA_RESOLUTION, AppController.getDefaultSettings().cameraSettings.previewResolution));
        }

        videoChunkSizeMinutes.setText(preferenceManager.getSharedPreferences().getString(KEY_VIDEO_CHUNK_SIZE_MINUTES, this.getString(R.string.default_video_chunk_size_minutes)));
        videoHousekeepingMb.setText(preferenceManager.getSharedPreferences().getString(KEY_VIDEO_HOUSEKEEPING_MB, this.getString(R.string.default_video_housekeeping_mb)));

        final String fps = String.valueOf(AppController.getDefaultSettings().videoSettings.previewFpsRange[1] / 1000);
        videoFrameRate.setDialogMessage(videoFrameRate.getDialogMessage() + String.format(this.getString(R.string.label_video_frame_rate_summary_description), fps));

        if (AppController.getDefaultSettings().videoSettings != null && AppController.getDefaultSettings().videoSettings.profile != null) {
            videoFrameRate.setText(preferenceManager.getSharedPreferences().getString(KEY_VIDEO_FRAME_RATE, String.valueOf(AppController.getDefaultSettings().videoSettings.profile.videoFrameRate)));
            audioEncoder.setValue(preferenceManager.getSharedPreferences().getString(KEY_AUDIO_ENCODER, String.valueOf(AppController.getDefaultSettings().videoSettings.profile.audioCodec)));
            audioBitrate.setText(preferenceManager.getSharedPreferences().getString(KEY_AUDIO_BITRATE, String.valueOf(AppController.getDefaultSettings().videoSettings.profile.audioBitRate)));
        }

        audioSource.setValue(preferenceManager.getSharedPreferences().getString(KEY_AUDIO_SOURCE, this.getString(R.string.default_audio_src)));

        streamingPort.setText(preferenceManager.getSharedPreferences().getString(KEY_VIDEO_STREAMING_PORT, getString(R.string.default_video_streaming_port)));
        streamingImageQuality.setProgress(preferenceManager.getSharedPreferences().getInt(KEY_VIDEO_STREAMING_IMAGE_QUALITY, Integer.valueOf(getString(R.string.default_video_streaming_image_quality))));
        streamDetectedObject.setChecked(preferenceManager.getSharedPreferences().getBoolean(KEY_STREAM_DETECTED_OBJECT, Boolean.valueOf(this.getString(R.string.default_stream_detected_object))));
        disguiseCamera.setChecked(preferenceManager.getSharedPreferences().getBoolean(KEY_DISGUISE_CAMERA, Boolean.valueOf(this.getString(R.string.default_disguise_camera))));
        stealthMode.setChecked(preferenceManager.getSharedPreferences().getBoolean(KEY_STEALTH_MODE, Boolean.valueOf(this.getString(R.string.default_stealth_mode))));


        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
            initSummary(getPreferenceScreen().getPreference(i));
        }

        // Register listener
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        LOGI(TAG, "[onSharedPreferenceChanged] Key - " + key);
        if (KEY_NOTIFY_BY_EMAIL.equals(key)) {
            if (notifyByEmail.isChecked()) {
                ToastUtils.toastShort(this.getActivity(), getString(R.string.msg_device_email_must_exist));
            }
        } else if (KEY_NOTIFY_BY_SMS.equals(key)) {
            if (notifyBySms.isChecked()) {
                ToastUtils.toastShort(this.getActivity(), getString(R.string.msg_device_phone_number_must_exist));
            }
        } else if (KEY_ALARM_IMAGE_CLOUD_STORAGE.equals(key)) {
            if (cloudStorage.isChecked()) {
                ToastUtils.toastShort(this.getActivity(), getString(R.string.msg_valid_gmail_account));
            }
        } else if (KEY_BACKGROUND_CAMERA.equals(key)) {
            if (backgroundCamera.isChecked()) {
                ToastUtils.toastShort(this.getActivity(), getString(R.string.msg_background_camera));
            }
        } else if (KEY_MOTION_DETECTION_RECORD_VIDEO_DURATION_SECONDS.equals(key)) {
            // Make sure the number of seconds do not exceed integer max
            try {
                int duration = Integer.parseInt(motionDetectionRecordVideoDurationSeconds.getText());
                if (duration <= 0) {
                    motionDetectionRecordVideoDurationSeconds.setText(this.getString(R.string.default_motion_detection_record_video_duration_seconds));
                }
            } catch (NumberFormatException ex) {
                LOGI(TAG, "[onSharedPreferenceChanged] Set the seconds to the default");
                motionDetectionRecordVideoDurationSeconds.setText(this.getString(R.string.default_motion_detection_record_video_duration_seconds));
            }
        } else if (KEY_ALARM_IMAGE_DRIVE_STORAGE.equals(key)) {
            if (driveStorage.isChecked()) {
                setupGoogleDrive();

                // Check if account is authorized
                //Thread task = new Thread(new Runnable() {
                //	@Override
                //	public void run() {
                // AppController.loginToGoogleDriveService(DetectionSettingsFragment.this.getActivity());
                //	}
                //});
                //task.start();
            } else {
                // Disconnect Google Drive
                // revokeGoogleDrive();
            }
        } else if (KEY_MOTION_DETECTION_TYPE.equals(key)) {
            final String detectionType = motionDetectionType.getValue();
            if (!TextUtils.isEmpty(detectionType) &&
                    (detectionType.equals(MotionDetectionType.FACE.getHashCode()) ||
                            detectionType.equals(MotionDetectionType.HUMAN.getHashCode())
                    )) {
                ToastUtils.toastLong(this.getActivity(), getString(R.string.msg_camera_orientation));
            }
        } else if (KEY_VIDEO_RESOLUTION.equals(key)) {
            final CamcorderProfile camcorderProfile = DeviceUtils.getCamcorderProfile(videoResolution.getValue());
            videoFormat.setValue(String.valueOf(camcorderProfile.fileFormat));
            videoEncoder.setValue(String.valueOf(camcorderProfile.videoCodec));
            videoBitrate.setText(String.valueOf(camcorderProfile.videoBitRate));
            videoFrameRate.setText(String.valueOf(camcorderProfile.videoFrameRate));
            audioEncoder.setValue(String.valueOf(camcorderProfile.audioCodec));
            audioBitrate.setText(String.valueOf(camcorderProfile.audioBitRate));
        } else if (KEY_STORAGE_LOCATION.equals(key)) {
            // Request for permission if API >= 23 and need to write to external storage
            if (PlatformUtils.isMarshallowOrHigher() &&
                    !TextUtils.isEmpty(storageLocation.getValue()) &&
                    storageLocation.getValue().contains("/sdcard")) {
                boolean hasPermission = (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                if (!hasPermission) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 6088);
                }
            }
        }

        updatePrefSummary(findPreference(key));

        // Post a message using the service bus
        AppController.bus.post(new PreferenceChangedEvent());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister listener
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);

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

    private void initPreference(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            if (!TextUtils.isEmpty(listPref.getEntry()))
                p.setSummary(listPref.getEntry());
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

    private void updatePrefSummary(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            p.setSummary(listPref.getEntry());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConfig.GOOGLE_REQUEST_CODE_RESOLUTION:
                if (resultCode == Activity.RESULT_OK) {
                    setupGoogleDrive();
                }
                break;
        }
    }

    /**
     * Called when {@code mGoogleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        LOGI(TAG, "GoogleApiClient connected");
        ToastUtils.toastShort(this.getActivity(), R.string.msg_google_drive_connected);
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
        try {
            result.startResolutionForResult(this.getActivity(), AppConfig.GOOGLE_REQUEST_CODE_RESOLUTION);
        } catch (Exception e) {
            LOGE(TAG, "[onConnectionFailed] Exception while starting resolution activity", e);
        }
    }

    public void setupGoogleDrive() {
        // Check if Google Drive integration is enabled
        final boolean isGoogleDriveEnabled = getPreferenceManager().getSharedPreferences().getBoolean(KEY_ALARM_IMAGE_DRIVE_STORAGE, Boolean.valueOf(this.getString(R.string.default_alarm_image_drive_storage)));
        final String gmail = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_DEVICE_EMAIL_ADDRESS, this.getString(R.string.default_device_email_address));
        final boolean isDeviceTrackingEnabled = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_DEVICE_TRACKING, Boolean.valueOf(this.getString(R.string.default_device_tracking)));
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

    /*public void revokeGoogleDrive() {
        if (AppController.googleApiClient != null && AppController.googleApiClient.isConnected()) {
            AppController.googleApiClient.clearDefaultAccountAndReconnect();
            AppController.googleApiClient.disconnect();
            AppController.googleApiClient = null;
        }
    }*/
}