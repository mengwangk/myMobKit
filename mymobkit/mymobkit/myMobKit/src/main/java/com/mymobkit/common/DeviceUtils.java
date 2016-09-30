package com.mymobkit.common;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.LOGW;
import static com.mymobkit.common.LogUtils.makeLogTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.os.BatteryManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseArray;

import com.mymobkit.app.AppController;
import com.mymobkit.model.DefaultSettings;
import com.mymobkit.model.Resolution;
import com.mymobkit.service.api.status.DeviceBatteryInfo;
import com.mymobkit.service.api.status.DeviceBatteryInfo.Health;
import com.mymobkit.service.api.status.DeviceBatteryInfo.PlugType;
import com.mymobkit.service.api.status.DeviceBatteryInfo.Status;
import com.mymobkit.ui.fragment.ServiceSettingsFragment;

@SuppressWarnings("deprecation")
public final class DeviceUtils {

    public static final String TAG = makeLogTag(DeviceUtils.class);

    public static DeviceBatteryInfo getBatteryInfo(final Context context) {

        DeviceBatteryInfo batteryInfo = new DeviceBatteryInfo();
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        batteryInfo.setPresent(batteryIntent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false));
        if (batteryInfo.isPresent()) {
            // Battery technology
            batteryInfo.setTechnology(batteryIntent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY));

            // Temperature
            batteryInfo.setTemperature(batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1));

            // Voltage
            batteryInfo.setVoltage(batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1));

            // Battery plugged info
            int plugged = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

            switch (plugged) {
                case BatteryManager.BATTERY_PLUGGED_AC:
                    batteryInfo.setPlugType(PlugType.AC);
                    break;
                case BatteryManager.BATTERY_PLUGGED_USB:
                    batteryInfo.setPlugType(PlugType.USB);
                    break;
                case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                    batteryInfo.setPlugType(PlugType.WIRELESS);
                    break;
                default:
                    batteryInfo.setPlugType(PlugType.UNKNOWN);
                    break;
            }

            // Battery scale
            int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            // Battery charging level
            int rawlevel = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

            if (rawlevel >= 0 && scale > 0) {
                batteryInfo.setLevel((rawlevel * 100) / scale);
            }

            // Battery health
            int health = batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
            switch (health) {
                case BatteryManager.BATTERY_HEALTH_COLD:
                    batteryInfo.setHealth(Health.COLD);
                    break;
                case BatteryManager.BATTERY_HEALTH_DEAD:
                    batteryInfo.setHealth(Health.DEAD);
                    break;
                case BatteryManager.BATTERY_HEALTH_GOOD:
                    batteryInfo.setHealth(Health.GOOD);
                    break;
                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                    batteryInfo.setHealth(Health.OVER_VOLTAGE);
                    break;
                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    batteryInfo.setHealth(Health.OVERHEAT);
                    break;
                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                    batteryInfo.setHealth(Health.UNSPECIFIED_FAILURE);
                    break;
                default:
                    batteryInfo.setHealth(Health.UNKNOWN);
            }
            // Battery charging status
            int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
            switch (status) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    batteryInfo.setStatus(Status.CHARGING);
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    batteryInfo.setStatus(Status.DISCHARGING);
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    batteryInfo.setStatus(Status.FULL);
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    batteryInfo.setStatus(Status.NOT_CHARGING);
                    break;
                default:
                    batteryInfo.setStatus(Status.UNKNOWN);
                    break;
            }
        }
        return batteryInfo;
    }

    /**
     * @return the default camera on the device. Return null if there is no camera on the device.
     */
    public static Camera getDefaultCameraInstance() {
        try {
            return Camera.open();
        } catch (Exception e) {
            LOGE(TAG, "Camera is not available (in use or does not exist): " + e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * @return the default rear/back facing camera on the device. Returns null if camera is not available.
     */
    public static Camera getDefaultBackFacingCameraInstance() {
        return getDefaultCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
    }

    /**
     * @return the default front facing camera on the device. Returns null if camera is not available.
     */
    public static Camera getDefaultFrontFacingCameraInstance() {
        return getDefaultCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    /**
     * @param position Physical position of the camera i.e Camera.CameraInfo.CAMERA_FACING_FRONT or Camera.CameraInfo.CAMERA_FACING_BACK.
     * @return the default camera on the device. Returns null if camera is not available.
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private static Camera getDefaultCamera(int position) {
        // Find the total number of cameras available
        int mNumberOfCameras = Camera.getNumberOfCameras();

        // Find the ID of the back-facing ("default") camera
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < mNumberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == position) {
                return Camera.open(i);
            }
        }
        return null;
    }

    public static void configureSupportedSizes(DefaultSettings settings) {
        Camera camera = getDefaultCameraInstance();
        try {
            if (camera != null && camera.getParameters() != null) {
                if (camera.getParameters().getSupportedVideoSizes() != null) {
                    settings.videoSettings.supportedVideoSizes = camera.getParameters().getSupportedVideoSizes();
                } else {
                    // Video sizes may be null, which indicates that all the supported preview sizes are supported for video recording
                    settings.videoSettings.supportedVideoSizes = camera.getParameters().getSupportedPreviewSizes();
                }
                settings.videoSettings.previewSize = camera.getParameters().getPreviewSize();
                settings.videoSettings.supportedFpsRange = camera.getParameters().getSupportedPreviewFpsRange();
                getPreviewFpsRange(camera, settings.videoSettings.previewFpsRange);

                // Get supported camera resolutions
                List<Size> supportedResolutions = camera.getParameters().getSupportedPreviewSizes();
                settings.cameraSettings.resolutions = new ArrayList<String>(supportedResolutions.size());
                for (int i = 0; i < supportedResolutions.size(); i++) {
                    Resolution resolution = new Resolution(supportedResolutions.get(i).width, supportedResolutions.get(i).height);
                    settings.cameraSettings.resolutions.add(resolution.toString());
                }
                Resolution previewResolution = new Resolution(camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height);
                settings.cameraSettings.previewResolution = previewResolution.toString();
            } else {
                settings.videoSettings.supportedVideoSizes = new ArrayList<Size>();
                settings.videoSettings.supportedFpsRange = new ArrayList<int[]>();
                settings.cameraSettings.resolutions = new ArrayList<String>();
                settings.cameraSettings.previewResolution =  new Resolution(640, 480).toString();
            }
        } finally {
            if (camera != null) {
                camera.release();
                camera = null;
            }
        }
    }

    private static void getPreviewFpsRange(final Camera camera, int[] fpsRange) {
        try {
            camera.getParameters().getPreviewFpsRange(fpsRange);
        } catch (NumberFormatException e) {
            LOGE(TAG, "[getPreviewFpsRange] Failed to get preview FPS range");
            fpsRange[0] = 0;
            fpsRange[1] = 0;
        }
    }

    private static String getAspectRatio(int width, int height) {
        int gcf = greatestCommonFactor(width, height);
        width /= gcf;
        height /= gcf;
        return width + ":" + height;
    }

    public static String getAspectRatioMPString(int width, int height) {
        float mp = (width * height) / 1000000.0f;
        return "(" + getAspectRatio(width, height) + ", " + formatFloatToString(mp) + "MP)";
    }

    private static String formatFloatToString(final float f) {
        final int i = (int) f;
        if (f == i)
            return Integer.toString(i);
        return String.format(Locale.getDefault(), "%.2f", f);
    }

    private static int greatestCommonFactor(int a, int b) {
        while (b > 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    public static String getCamcorderProfileDescription(String quality) {
        CamcorderProfile profile = getCamcorderProfile(quality);
        String highest = "";
        if (profile.quality == CamcorderProfile.QUALITY_HIGH) {
            highest = "Highest: ";
        }
        String type = "";
        if (profile.videoFrameWidth == 3840 && profile.videoFrameHeight == 2160) {
            type = "4K Ultra HD ";
        } else if (profile.videoFrameWidth == 1920 && profile.videoFrameHeight == 1080) {
            type = "Full HD ";
        } else if (profile.videoFrameWidth == 1280 && profile.videoFrameHeight == 720) {
            type = "HD ";
        } else if (profile.videoFrameWidth == 720 && profile.videoFrameHeight == 480) {
            type = "SD ";
        } else if (profile.videoFrameWidth == 640 && profile.videoFrameHeight == 480) {
            type = "VGA ";
        } else if (profile.videoFrameWidth == 352 && profile.videoFrameHeight == 288) {
            type = "CIF ";
        } else if (profile.videoFrameWidth == 320 && profile.videoFrameHeight == 240) {
            type = "QVGA ";
        } else if (profile.videoFrameWidth == 176 && profile.videoFrameHeight == 144) {
            type = "QCIF ";
        }
        String desc = highest + type + profile.videoFrameWidth + "x" + profile.videoFrameHeight + " " + getAspectRatioMPString(profile.videoFrameWidth, profile.videoFrameHeight);
        return desc;
    }

    public static CamcorderProfile getCamcorderProfile(String quality) {
        CamcorderProfile camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH); // default
        try {
            String profileString = quality;
            int index = profileString.indexOf('_');
            if (index != -1) {
                profileString = quality.substring(0, index);
            }
            int profile = Integer.parseInt(profileString);
            camcorderProfile = CamcorderProfile.get(profile);
            if (index != -1 && index + 1 < quality.length()) {
                String overrideString = quality.substring(index + 1);
                if (overrideString.charAt(0) == 'r' && overrideString.length() >= 4) {
                    index = overrideString.indexOf('x');
                    if (index == -1) {
                    } else {
                        String resWS = overrideString.substring(1, index); // skip first 'r'
                        String resHS = overrideString.substring(index + 1);
                        // copy to local variable first, so that if we fail to parse height, we don't set the width either
                        int resW = Integer.parseInt(resWS);
                        int resH = Integer.parseInt(resHS);
                        camcorderProfile.videoFrameWidth = resW;
                        camcorderProfile.videoFrameHeight = resH;
                    }
                } else {
                    LOGW(TAG, "Unknown override_string initial code, or otherwise invalid format");
                }
            }
        } catch (NumberFormatException e) {
            LOGE(TAG, "[getCamcorderProfile] Failed to parse video quality: " + quality, e);
        }
        return camcorderProfile;
    }

    public static void configureVideoQuality(DefaultSettings settings) {
        final int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        SparseArray<Pair<Integer, Integer>> profiles = new SparseArray<Pair<Integer, Integer>>();
        if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_HIGH)) {
            CamcorderProfile profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_HIGH);
            profiles.put(CamcorderProfile.QUALITY_HIGH, new Pair<Integer, Integer>(profile.videoFrameWidth, profile.videoFrameHeight));
        }
        if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_1080P)) {
            CamcorderProfile profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_1080P);
            profiles.put(CamcorderProfile.QUALITY_1080P, new Pair<Integer, Integer>(profile.videoFrameWidth, profile.videoFrameHeight));
        }
        if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_720P)) {
            CamcorderProfile profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_720P);
            profiles.put(CamcorderProfile.QUALITY_720P, new Pair<Integer, Integer>(profile.videoFrameWidth, profile.videoFrameHeight));
        }
        if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_480P)) {
            CamcorderProfile profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_480P);
            profiles.put(CamcorderProfile.QUALITY_480P, new Pair<Integer, Integer>(profile.videoFrameWidth, profile.videoFrameHeight));
        }
        if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_CIF)) {
            CamcorderProfile profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_CIF);
            profiles.put(CamcorderProfile.QUALITY_CIF, new Pair<Integer, Integer>(profile.videoFrameWidth, profile.videoFrameHeight));
        }
        if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_QVGA)) {
            CamcorderProfile profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_QVGA);
            profiles.put(CamcorderProfile.QUALITY_QVGA, new Pair<Integer, Integer>(profile.videoFrameWidth, profile.videoFrameHeight));
        }
        if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_QCIF)) {
            CamcorderProfile profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_QCIF);
            profiles.put(CamcorderProfile.QUALITY_QCIF, new Pair<Integer, Integer>(profile.videoFrameWidth, profile.videoFrameHeight));
        }
        if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_LOW)) {
            CamcorderProfile profile = CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_LOW);
            profiles.put(CamcorderProfile.QUALITY_LOW, new Pair<Integer, Integer>(profile.videoFrameWidth, profile.videoFrameHeight));
        }
        initialiseVideoQualityFromProfiles(settings, profiles);
    }

    private static void initialiseVideoQualityFromProfiles(DefaultSettings settings, SparseArray<Pair<Integer, Integer>> profiles) {
        settings.videoSettings.videoQuality = new ArrayList<String>();
        settings.videoSettings.videoQualityDesc = new ArrayList<String>();
        boolean donVideoSize[] = null;
        if (settings.videoSettings.supportedVideoSizes != null) {
            donVideoSize = new boolean[settings.videoSettings.supportedVideoSizes.size()];
            for (int i = 0; i < settings.videoSettings.supportedVideoSizes.size(); i++)
                donVideoSize[i] = false;
        }
        if (profiles.get(CamcorderProfile.QUALITY_HIGH) != null) {
            Pair<Integer, Integer> pair = profiles.get(CamcorderProfile.QUALITY_HIGH);
            addVideoResolutions(settings, donVideoSize, CamcorderProfile.QUALITY_HIGH, pair.first, pair.second);
        }
        if (profiles.get(CamcorderProfile.QUALITY_1080P) != null) {
            Pair<Integer, Integer> pair = profiles.get(CamcorderProfile.QUALITY_1080P);
            addVideoResolutions(settings, donVideoSize, CamcorderProfile.QUALITY_1080P, pair.first, pair.second);
        }
        if (profiles.get(CamcorderProfile.QUALITY_720P) != null) {
            Pair<Integer, Integer> pair = profiles.get(CamcorderProfile.QUALITY_720P);
            addVideoResolutions(settings, donVideoSize, CamcorderProfile.QUALITY_720P, pair.first, pair.second);
        }
        if (profiles.get(CamcorderProfile.QUALITY_480P) != null) {
            Pair<Integer, Integer> pair = profiles.get(CamcorderProfile.QUALITY_480P);
            addVideoResolutions(settings, donVideoSize, CamcorderProfile.QUALITY_480P, pair.first, pair.second);
        }
        if (profiles.get(CamcorderProfile.QUALITY_CIF) != null) {
            Pair<Integer, Integer> pair = profiles.get(CamcorderProfile.QUALITY_CIF);
            addVideoResolutions(settings, donVideoSize, CamcorderProfile.QUALITY_CIF, pair.first, pair.second);
        }
        if (profiles.get(CamcorderProfile.QUALITY_QVGA) != null) {
            Pair<Integer, Integer> pair = profiles.get(CamcorderProfile.QUALITY_QVGA);
            addVideoResolutions(settings, donVideoSize, CamcorderProfile.QUALITY_QVGA, pair.first, pair.second);
        }
        if (profiles.get(CamcorderProfile.QUALITY_QCIF) != null) {
            Pair<Integer, Integer> pair = profiles.get(CamcorderProfile.QUALITY_QCIF);
            addVideoResolutions(settings, donVideoSize, CamcorderProfile.QUALITY_QCIF, pair.first, pair.second);
        }
        if (profiles.get(CamcorderProfile.QUALITY_LOW) != null) {
            Pair<Integer, Integer> pair = profiles.get(CamcorderProfile.QUALITY_LOW);
            addVideoResolutions(settings, donVideoSize, CamcorderProfile.QUALITY_LOW, pair.first, pair.second);
        }
    }

    private static void addVideoResolutions(DefaultSettings settings, boolean doneVideoSize[], int baseProfile, int minResWidth, int minResHeight) {
        if (settings.videoSettings.supportedVideoSizes == null || settings.videoSettings.supportedVideoSizes.size() == 0) {
            return;
        }
        for (int i = 0; i < settings.videoSettings.supportedVideoSizes.size(); i++) {
            if (doneVideoSize[i])
                continue;
            Size size = settings.videoSettings.supportedVideoSizes.get(i);
            if (size.width == minResWidth && size.height == minResHeight) {
                String str = "" + baseProfile;
                settings.videoSettings.videoQuality.add(str);
                settings.videoSettings.videoQualityDesc.add(getCamcorderProfileDescription(str));

                //if (settings.videoSettings.previewSize != null &&
                //        settings.videoSettings.previewSize.width == size.width
                //        && settings.videoSettings.previewSize.height == size.height) {
                if (settings.videoSettings.profile.videoFrameWidth == size.width && settings.videoSettings.profile.videoFrameHeight == size.height) {
                    settings.videoSettings.previewVideoQuality = str;
                }
                doneVideoSize[i] = true;
            } else if (baseProfile == CamcorderProfile.QUALITY_LOW || size.width * size.height >= minResWidth * minResHeight) {
                String str = "" + baseProfile + "_r" + size.width + "x" + size.height;
                settings.videoSettings.videoQuality.add(str);
                settings.videoSettings.videoQualityDesc.add(getCamcorderProfileDescription(str));

                /*  if (settings.videoSettings.previewSize != null &&
                        settings.videoSettings.previewSize.width == size.width &&
                        settings.videoSettings.previewSize.height == size.height) {
                */
                if (settings.videoSettings.profile.videoFrameWidth == size.width && settings.videoSettings.profile.videoFrameHeight == size.height) {
                    settings.videoSettings.previewVideoQuality = str;
                }
                doneVideoSize[i] = true;
            }
        }
    }

    public static String getDeviceName() {
        final String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String deviceName;
        if (model.startsWith(manufacturer)) {
            deviceName = capitalize(model);
        } else {
            deviceName = capitalize(manufacturer) + "-" + model;
        }
        if (!TextUtils.isEmpty(deviceName)) {
            return deviceName.replace(" ", "-");
        }
        return deviceName;
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static String getDeviceId(final Context context) {
        String deviceId = "";
        try {
            final TelephonyManager teleMan = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            deviceId = teleMan.getDeviceId();
        } catch (Exception ex) {
            LOGE(TAG, "[getDeviceId] Unable to get device id", ex);
        }
        if (TextUtils.isEmpty(deviceId)) {
            deviceId = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_DEVICE_UNIQUE_NAME, AppController.getDeviceName());
        }
        return deviceId;
    }
}
