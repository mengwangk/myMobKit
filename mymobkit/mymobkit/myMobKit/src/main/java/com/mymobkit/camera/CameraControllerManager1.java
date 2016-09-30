package com.mymobkit.camera;

import android.hardware.Camera;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Provides support using Android's original camera API
 * android.hardware.Camera.
 */
@SuppressWarnings("deprecation")
public class CameraControllerManager1 extends CameraControllerManager {

    private static final String TAG = makeLogTag(CameraControllerManager1.class);

    public int getNumberOfCameras() {
        return Camera.getNumberOfCameras();
    }

    public boolean isFrontFacing(int cameraId) {
        try {
            Camera.CameraInfo camerInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, camerInfo);
            return (camerInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT);
        } catch (RuntimeException e) {
            // Had a report of this crashing on Galaxy Nexus - may be device specific issue, see http://stackoverflow.com/questions/22383708/java-lang-runtimeexception-fail-to-get-camera-info
            // but good to catch it anyway
            LOGE(TAG, "[isFrontFacing] Failed to set parameters", e);
            return false;
        }
    }
}
