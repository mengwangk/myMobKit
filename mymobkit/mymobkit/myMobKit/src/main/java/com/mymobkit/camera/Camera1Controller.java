package com.mymobkit.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.AudioManager;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.mymobkit.app.AppController;
import com.mymobkit.common.AudioUtils;
import com.mymobkit.common.PlatformUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.mymobkit.common.LogUtils.LOGD;
import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Provides support using Android's original camera API android.hardware.Camera.
 */
@SuppressWarnings("deprecation")
public class Camera1Controller extends CameraController {

    private static final String TAG = makeLogTag(Camera1Controller.class);

    private AudioManager audioManager;
    private WindowManager windowManager;
    private AudioUtils audioUtils;

    private static Camera camera = null;
    private int displayOrientation = 0;
    private Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
    private byte[] jpegData = null;
    private AutoFocusCallback autoFocusCallback;

    public Camera1Controller(int cameraId) throws CameraControllerException {
        super(cameraId);
        windowManager = (WindowManager) AppController.getContext().getSystemService(Context.WINDOW_SERVICE);
        audioManager = (AudioManager) AppController.getContext().getSystemService(Context.AUDIO_SERVICE);
        audioUtils = new AudioUtils(audioManager);

        LOGD(TAG, "[Camera1Controller] create new CameraController1: " + cameraId);
        try {
            camera = Camera.open(cameraId);
        } catch (RuntimeException e) {
            LOGD(TAG, "[Camera1Controller] Failed to open camera", e);
            throw new CameraControllerException();
        }
        if (camera == null) {
            // Although the documentation says Camera.open() should throw a RuntimeException, it seems that it some cases it can return null
            // I've seen this in some crashes reported in Google Play; also see:
            // http://stackoverflow.com/questions/12054022/camera-open-returns-null
            LOGD(TAG, "[Camera1Controller] Camera.open returned null");
            throw new CameraControllerException();
        }
        try {
            Camera.getCameraInfo(cameraId, cameraInfo);
        } catch (RuntimeException e) {
            // Had reported RuntimeExceptions from Google Play
            // also see http://stackoverflow.com/questions/22383708/java-lang-runtimeexception-fail-to-get-camera-info
            LOGD(TAG, "[Camera1Controller] Failed to get camera info");
            release();
            throw new CameraControllerException();
        }
        camera.setErrorCallback(new CameraErrorCallback());
    }

    private static class CameraErrorCallback implements Camera.ErrorCallback {
        @Override
        public void onError(int error, Camera camera) {
            LOGE(TAG, "[CameraErrorCallback] Camera onError: " + error);
            if (error == android.hardware.Camera.CAMERA_ERROR_SERVER_DIED) {
                LOGE(TAG, "[onError] Camera error server died");
            } else if (error == android.hardware.Camera.CAMERA_ERROR_UNKNOWN) {
                LOGE(TAG, "[onError] Camera error unknown");
            }
        }
    }

    @Override
    public void release() {
        if (camera != null) {
            try {
                camera.release();
            } catch (Exception e) {
                LOGE(TAG, "[releaseResources] Failed to release camera", e);
            } finally {
                camera = null;
                windowManager = null;
                audioManager = null;
            }
        }
    }

    @Override
    public byte[] takePictureSync(final boolean silent) {
        final CountDownLatch latch = new CountDownLatch(1);
        final Camera.PictureCallback jpegPictureCallback = new Camera.PictureCallback() {
            public void onPictureTaken(byte[] data, Camera camera) {
                try {
                    // n.b., this is automatically run in a different thread
                    jpegData = data;

                    // Unmute and stop camera preview
                    unMuteAll();
                    camera.stopPreview();
                    camera.setPreviewCallback(null);
                } catch (Exception e) {
                    LOGE(TAG, "[onPictureTaken] Error stopping camera", e);
                } finally {
                    // Notify the waiting thread
                    latch.countDown();
                }
            }
        };

        try {
            camera.setDisplayOrientation(getCameraOrientation());
            camera.setPreviewTexture(new SurfaceTexture(0));
            camera.startPreview();

            setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            setOptimumExposure();  // Set exposure
            if (silent) {
                storeAudioStreamSettings();
                muteAll();
            }
            camera.takePicture(null, null, jpegPictureCallback);
            try {
                latch.await();
                return jpegData;
            } catch (InterruptedException e) {
                LOGE(TAG, "takePictureSync] Unable to take picture", e);
            }
        } catch (Exception e) {
            // just in case? We got a RuntimeException report here from 1 user on Google Play; I also encountered it myself once of Galaxy Nexus when starting up
            LOGE(TAG, "[takePictureSync] Unable to take picture", e);
        }
        return null;
    }

    @Override
    public void setPreviewDisplay(SurfaceHolder holder) throws CameraControllerException {
        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            throw new CameraControllerException();
        }
    }

    @Override
    public void setPreviewTexture(SurfaceTexture texture) throws CameraControllerException {
        try {
            camera.setPreviewTexture(texture);
        } catch (IOException e) {
            throw new CameraControllerException();
        }
    }

    @Override
    public void setDisplayOrientation(int degrees) {
        int result;
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (cameraInfo.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {
            result = (cameraInfo.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
        this.displayOrientation = result;
    }

    @Override
    public void startPreview() throws CameraControllerException {
        try {
            LOGD(TAG, "[startPreview] Start camera preview");
            camera.startPreview();
        } catch (RuntimeException e) {
            throw new CameraControllerException();
        }
    }

    @Override
    public void stopPreview() {
        camera.stopPreview();
    }

    @Override
    public void setFlashMode(String mode) {
        try {
            Camera.Parameters params = camera.getParameters();
            final List<String> supportedModes = params.getSupportedFlashModes();
            if (supportedModes != null && supportedModes.contains(mode)) {
                params.setFlashMode(mode);
                camera.setParameters(params);
            }
        } catch (Exception e) {
            LOGE(TAG, "[setFlashMode] Unable to set flash mode", e);
        }
    }

    @Override
    public List<CameraSize> getSupportedPreviewSizes() {
        final List<CameraSize> cameraSizes = new ArrayList<>(1);
        if (camera != null) {
            List<Camera.Size> supportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
            for (Camera.Size size : supportedPreviewSizes) {
                cameraSizes.add(new CameraSize(size.width, size.height));
            }
        }
        if (cameraSizes.size() > 1) {
            // Sort in ascending order
            Collections.sort(cameraSizes);
        }
        return cameraSizes;
    }

    @Override
    public List<CameraSize> getSupportedPictureSizes() {
        final List<CameraSize> cameraSizes = new ArrayList<>(1);
        if (camera != null) {
            List<Camera.Size> supportedPreviewSizes = camera.getParameters().getSupportedPictureSizes();
            for (Camera.Size size : supportedPreviewSizes) {
                cameraSizes.add(new CameraSize(size.width, size.height));
            }
        }
        if (cameraSizes.size() > 1) {
            // Sort in ascending order
            Collections.sort(cameraSizes);
        }
        return cameraSizes;
    }

    @Override
    public void setPreviewSize(int width, int height) {
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(width, height);
            camera.setParameters(parameters);
        }
    }

    @Override
    public void setPictureSize(int width, int height) {
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPictureSize(width, height);
            camera.setParameters(parameters);
        }
    }


    final Camera.AutoFocusCallback cameraAutoFocusCallback = new Camera.AutoFocusCallback() {
        // boolean done = false;

        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            // in theory we should only ever get one call to onAutoFocus(), but some Samsung phones at least can call the callback multiple times
            // see http://stackoverflow.com/questions/36316195/take-picture-fails-on-samsung-phones
            // needed to fix problem on Samsung S7 with flash auto/on and continuous picture focus where it would claim failed to take picture even though it'd succeeded,
            // because we repeatedly call takePicture(), and the subsequent ones cause a runtime exception
            //if (!done) {
            //    done = true;
            if (autoFocusCallback != null) {
                autoFocusCallback.onAutoFocus(success);
            }
            //}
        }
    };

    @Override
    public void autoFocus(final AutoFocusCallback callback) {
        try {
            autoFocusCallback = callback;
            camera.autoFocus(cameraAutoFocusCallback);
        } catch (Exception e) {
            LOGE(TAG, "[autoFocus] Unable to auto focus", e);
            // should call the callback, so the application isn't left waiting (e.g., when we autofocus before trying to take a photo)
            autoFocusCallback.onAutoFocus(false);
        }
    }

    @Override
    public void cancelAutoFocus() {
        try {
            camera.cancelAutoFocus();
        } catch (Exception e) {
            LOGE(TAG, "[cancelAutoFocus] Unable to cancel auto focus", e);
        }
    }

    @Override
    public void setFocusMode(String mode) {
        try {
            Camera.Parameters parameters = camera.getParameters();
            List<String> supportedModes = camera.getParameters().getSupportedFocusModes();
            if (supportedModes != null && supportedModes.contains(mode)) {
                parameters.setFocusMode(mode);
                camera.setParameters(parameters);
            }
        } catch (Exception e) {
            LOGE(TAG, "[setFocusMode] Unable to set focus mode", e);
        }
    }

    public void setOptimumExposure() {
        try {
            Camera.Parameters parameters = camera.getParameters();
            if (!parameters.isAutoExposureLockSupported())
                return;
            parameters.setExposureCompensation(parameters.getMaxExposureCompensation());
            parameters.setAutoExposureLock(false);
            camera.setParameters(parameters);
        } catch (Exception e) {
            LOGE(TAG, "[setOptimumExposure] Unable to set optimum exposure", e);
        }
    }

    private int getCameraOrientation() {
        int rotation = windowManager.getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (cameraInfo.orientation + degrees) % 360;
            result = (360 - result) % 180;  // compensate the mirror
        } else {  // back-facing
            result = (cameraInfo.orientation - degrees + 360) % 180;
        }
        return result;
    }

    private void storeAudioStreamSettings() {
        audioUtils.storeAudioStreamSettings();
    }

    private void setShutterSound(final boolean flag) {
        if (PlatformUtils.isJellyBeanMr1OrHigher()) {   // version 17 and above
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId, info);
            if (info.canDisableShutterSound) {
                camera.enableShutterSound(flag);
            }
        }
    }

    public void muteAll() {
        audioUtils.muteAll();
        setShutterSound(false);
    }

    public void unMuteAll() {
        audioUtils.unMuteAll();
        setShutterSound(true);
    }

    public int getDisplayOrientation() {
        return displayOrientation;
    }
}
