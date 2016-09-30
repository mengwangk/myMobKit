package com.mymobkit.camera;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.os.Build;

import static com.mymobkit.common.LogUtils.makeLogTag;
import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.LOGD;

/**
 * Provides support using Android 5's Camera 2 API
 * android.hardware.camera2.*.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class CameraControllerManager2 extends CameraControllerManager {

    private static final String TAG = makeLogTag(CameraControllerManager2.class);

    private Context context = null;

    public CameraControllerManager2(Context context) {
        this.context = context;
    }

    @Override
    public int getNumberOfCameras() {
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            return manager.getCameraIdList().length;
        } catch (CameraAccessException e) {
            LOGE(TAG, "[getNumberOfCameras] Unable to retrieve number of cameras", e);
        } catch (AssertionError e) {
            // had reported java.lang.AssertionError on Google Play, "Expected to get non-empty characteristics" from CameraManager.getOrCreateDeviceIdListLocked(CameraManager.java:465)
            // yes, in theory we shouldn't catch AssertionError as it represents a programming error, however it's a programming error by Google (a condition they thought couldn't happen)
            LOGE(TAG, "[getNumberOfCameras] Unable to retrieve number of cameras", e);
        }
        return 0;
    }

    @Override
    public boolean isFrontFacing(int cameraId) {
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraIdS = manager.getCameraIdList()[cameraId];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraIdS);
            return characteristics.get(CameraCharacteristics.LENS_FACING) == CameraMetadata.LENS_FACING_FRONT;
        } catch (CameraAccessException e) {
            LOGE(TAG, "[isFrontFacing] Unable to check if front facing camera", e);
        }
        return false;
    }

    /*
     * Returns true if the device supports the required hardware level, or better.
     * From http://msdx.github.io/androiddoc/docs//reference/android/hardware/camera2/CameraCharacteristics.html#INFO_SUPPORTED_HARDWARE_LEVEL
     * From Android N, higher levels than "FULL" are possible, that will have higher integer values.
     * Also see https://sourceforge.net/p/opencamera/tickets/141/ .
     */
    private boolean isHardwareLevelSupported(CameraCharacteristics c, int requiredLevel) {
        int deviceLevel = c.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);

        if (deviceLevel == CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY)
            LOGD(TAG, "[isHardwareLevelSupported] Camera has LEGACY Camera2 support");
        else if (deviceLevel == CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED)
            LOGD(TAG, "[isHardwareLevelSupported] Camera has LIMITED Camera2 support");
        else if (deviceLevel == CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_FULL)
            LOGD(TAG, "[isHardwareLevelSupported] Camera has FULL Camera2 support");
        else
            LOGD(TAG, "[isHardwareLevelSupported] Camera has unknown Camera2 support: " + deviceLevel);
        if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
            return requiredLevel == deviceLevel;
        }
        // deviceLevel is not LEGACY, can use numerical sort
        return requiredLevel <= deviceLevel;
    }

    /*
      * Rather than allowing Camera2 API on all Android 5+ devices, we restrict it to cases where all cameras have at least LIMITED support.
     * (E.g., Nexus 6 has FULL support on back camera, LIMITED support on front camera.)
     * For now, devices with only LEGACY support should still with the old API.
     */
    public boolean allowCamera2Support(int cameraId) {
        CameraManager manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraIdS = manager.getCameraIdList()[cameraId];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraIdS);
            boolean supported = isHardwareLevelSupported(characteristics, CameraMetadata.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED);
            return supported;
        } catch (CameraAccessException e) {
            LOGE(TAG, "[allowCamera2Support] Unable check for new camera 2 API support", e);
        }
        return false;
    }
}
