package com.mymobkit.webcam;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.AsyncTask;

import com.mymobkit.R;
import com.mymobkit.app.AppController;
import com.mymobkit.common.AppPreference;
import com.mymobkit.common.StorageUtils;
import com.mymobkit.model.ExposureCompensation;
import com.mymobkit.ui.fragment.DetectionSettingsFragment;
import com.mymobkit.ui.fragment.ServiceSettingsFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.mymobkit.common.LogUtils.LOGD;
import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

@SuppressWarnings("deprecation")
public final class WebcamController {

    private static final String TAG = makeLogTag(WebcamController.class);
    private static int DEFAULT_WIDTH = 640;
    private static int DEFAULT_HEIGHT = 480;

    private Webcam webcam;
    private WebcamOverlay webcamOverlay;
    private Context context;
    private int videoHousekeepingMb;
    private String deviceName;

    public WebcamController(final Context context, final Webcam webcam, final WebcamOverlay webcamOverlay) {
        this.context = context;
        this.webcam = webcam;
        this.webcamOverlay = webcamOverlay;
        this.deviceName = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_DEVICE_UNIQUE_NAME, AppController.getDeviceName());
        this.videoHousekeepingMb = Integer.parseInt(AppPreference.getInstance().getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_VIDEO_HOUSEKEEPING_MB, context.getString(R.string.default_video_housekeeping_mb)));
    }

    public List<Camera.Size> getSupportedPreviewSizes() {
        Camera.Parameters p = this.webcam.getParameters();
        if (p != null) {
            return p.getSupportedPreviewSizes();
        } else {
            return new ArrayList<Camera.Size>(1);
        }
    }

    public Camera.Size getPreviewSize() {
        return webcam.getPreviewSize();
    }

    public int getPreviewWidth() {
        Camera.Parameters p = this.webcam.getParameters();
        if (p != null) {
            return this.webcam.getPreviewSize().width;
        } else {
            return DEFAULT_WIDTH;
        }
    }

    public int getPreviewHeight() {
        Camera.Parameters p = this.webcam.getParameters();
        if (p != null) {
            return this.webcam.getPreviewSize().height;
        } else {
            return DEFAULT_HEIGHT;
        }
    }

    public void setupCamera(final int width, final int height) {
        webcam.setupCamera(width, height);
    }

    public void switchCamera() {
        webcam.switchCamera();
    }

    public int getCameraIndex() {
        return webcam.getCameraIndex();
    }

    public void disconnectCamera() {
        webcam.disable();
    }

    public void releaseAll() {
        webcam.releaseAll();
    }

    public void setFlashMode(final boolean onOff) {
        try {
            Parameters p = webcam.getParameters();
            if (onOff == true) {
                p.setFlashMode(Parameters.FLASH_MODE_TORCH);
            } else {
                p.setFlashMode(Parameters.FLASH_MODE_OFF);
            }
            webcam.setParameters(p);
        } catch (Exception e) {
            LOGD(TAG, "[setFlashMode] Failed to set LED", e);
        }
    }

    public int getPreviewFormat() {
        if (webcam != null && webcam.getParameters() != null) {
            return webcam.getParameters().getPreviewFormat();
        }
        return ImageFormat.NV21;
    }

    public void setCamera(final int type) {
        webcam.configureCamera(type);
    }

    public List<String> getSceneModes() {
        return webcam.getSceneModes();
    }

    public List<String> getSupportedColorEffects() {
        return webcam.getSupportedColorEffects();
    }

    public void setSceneMode(final String sceneMode) {
        webcam.setSceneMode(sceneMode);
    }

    public void setColorEffect(final String colorEffect) {
        webcam.setColorEffect(colorEffect);
    }

    public boolean isZoomSupported() {
        return webcam.isZoomSupported();
    }

    public void setZoom(final int zoom) {
        webcam.setZoom(zoom);
    }

    public void setFocusMode(final String focusMode) {
        webcam.setFocusMode(focusMode);
    }

    public List<String> getSupportedFlashModes() {
        return webcam.getSupportedFlashModes();
    }

    public void setFlashMode(final String flashMode) {
        webcam.setFlashMode(flashMode);
    }

    public List<String> getSupportedFocusModes() {
        return webcam.getSupportedFocusModes();
    }

    public void setWhiteBalance(final String whiteBalance) {
        webcam.setWhiteBalance(whiteBalance);
    }

    public List<String> getSupportedWhiteBalance() {
        return webcam.getSupportedWhiteBalance();
    }

    public List<String> getSupportedAntibanding() {
        return webcam.getSupportedAntibanding();
    }

    public void setAntibanding(final String antibanding) {
        webcam.setAntibanding(antibanding);
    }

    public void setAutoExposureLock(final boolean enabled) {
        webcam.setAutoExposureLock(enabled);
    }

    public boolean getAutoExposureLock() {
        return webcam.getAutoExposureLock();
    }

    public ExposureCompensation getExposureCompensationSettings() {
        return new ExposureCompensation(webcam.getMinExposureCompensation(), webcam.getMaxExposureCompensation(), webcam.getExposureCompensation());
    }

    public void setExposureCompensation(int exposure) {
        webcam.setExposureCompensation(exposure);
    }

    public boolean isRecording() {
        return webcam.isRecording();
    }

    public String getRecordingFileName() {
        return webcam.getRecordingFileName();
    }

    public void startRecording() {

        // Added June 20th 2016
        //setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

        webcam.startRecording();

        // Start housekeeping task
        new HousekeepingTask(context).execute(null, null, null);
    }

    public void startRecording(final String prefix) {

        // Added June 20th 2016
       // setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

        webcam.startRecording(prefix);

        // Start housekeeping task
        new HousekeepingTask(context).execute(null, null, null);
    }

    public void stopRecording() {
        webcam.stopRecording();
    }

    public void takePicture() {
        webcam.takePicture();
    }

    /**
     * Housekeeping task
     */
    class HousekeepingTask extends AsyncTask<Void, Void, Boolean> {

        private Context context;

        public HousekeepingTask(final Context context) {
            this.context = context;
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                long freeSpace = StorageUtils.getFreeSpace();
                LOGD(TAG, "Free space remaining is " + freeSpace + " MB");
                if (freeSpace < videoHousekeepingMb) {
                    File dir = StorageUtils.getStorageDir(context, deviceName);
                    if (dir.isDirectory()) {
                        File[] files = dir.listFiles();
                        Arrays.sort(files, new Comparator<File>() {
                            public int compare(File f1, File f2) {
                                return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
                            }
                        });

                        long kbToDelete = (videoHousekeepingMb - freeSpace) * 1024 * 1024;
                        for (File f : files) {
                            long fileSize = f.length();
                            if (f.delete()) {
                                kbToDelete -= fileSize;
                                if (kbToDelete <= 0)
                                    return true;
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                LOGE(TAG, "[doInBackground] Exception performing housekeeping", ex);
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {

        }
    }
}
