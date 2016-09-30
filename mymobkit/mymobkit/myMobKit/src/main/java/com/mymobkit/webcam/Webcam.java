package com.mymobkit.webcam;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.mymobkit.R;
import com.mymobkit.app.AppController;
import com.mymobkit.common.AppPreference;
import com.mymobkit.common.AudioUtils;
import com.mymobkit.common.DateUtils;
import com.mymobkit.common.DeviceUtils;
import com.mymobkit.common.StorageUtils;
import com.mymobkit.enums.VideoFormat;
import com.mymobkit.model.Resolution;
import com.mymobkit.ui.fragment.DetectionSettingsFragment;
import com.mymobkit.ui.fragment.ServiceSettingsFragment;

import org.opencv.android.CameraBridgeViewBase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mymobkit.common.LogUtils.LOGD;
import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.LOGW;
import static com.mymobkit.common.LogUtils.makeLogTag;


@SuppressWarnings("deprecation")
/**
 *
 * @see <a href="https://github.com/columbia/helios_android/blob/master/src/com/Helios/BackgroundVideoRecorder.java">BackgroundVidoeRecorder.java</a>
 */
public class Webcam extends CameraView implements PictureCallback, MediaRecorder.OnInfoListener {

    private static final String TAG = makeLogTag(Webcam.class);

    private static final String VIDEO_FILE_PREFIX = "VID_";

    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private boolean isMediaRecorderPrepared = false;

    private String deviceName;
    private String fileExtension;
    private VideoFormat videoFormat;
    private int outputMediaFormat;
    private boolean videoStabilization;
    private CamcorderProfile profile;
    private int videoCodec;
    private int audioCodec;
    private int videoChunkSizeMinutes;
    private int videoBitRate;
    private int audioBitRate;
    private String mediaFilePath = "";
    private String mediaFileName = "";
    private int videoFrameRate;
    private int audioSource;
    private String videoPrefix = VIDEO_FILE_PREFIX;

    private AudioManager audioManager;
    private AudioUtils audioUtils;

    //private int[] audioStreams;
    //private int[] audioStreamVolumes;

    private Resolution cameraResolution;

    public Webcam(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPreferences() {
        AppPreference prefs = AppPreference.getInstance();
        this.audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        this.audioUtils = new AudioUtils(audioManager);
        this.deviceName = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_DEVICE_UNIQUE_NAME, AppController.getDeviceName());
        final int format = Integer.parseInt(prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_VIDEO_FORMAT, String.valueOf(AppController.getDefaultSettings().videoSettings.profile.fileFormat)));
        this.videoFormat = VideoFormat.get(format);
        this.fileExtension = VideoFormat.getFileExtension(videoFormat);
        this.outputMediaFormat = VideoFormat.getOutputMediaFormat(videoFormat);
        this.videoStabilization = prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_VIDEO_STABILIZATION, Boolean.valueOf(this.getContext().getString(R.string.default_video_stabilization)));
        this.profile = getConfiguredResolution();
        this.videoCodec = Integer.parseInt(prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_VIDEO_ENCODER, String.valueOf(AppController.getDefaultSettings().videoSettings.profile.videoCodec)));
        this.audioCodec = Integer.parseInt(prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_AUDIO_ENCODER, String.valueOf(AppController.getDefaultSettings().videoSettings.profile.audioCodec)));
        this.videoChunkSizeMinutes = Integer.parseInt(prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_VIDEO_CHUNK_SIZE_MINUTES, this.getContext().getString(R.string.default_video_chunk_size_minutes)));

        final String resString = prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_CAMERA_RESOLUTION, AppController.getDefaultSettings().cameraSettings.previewResolution);
        this.cameraResolution = Resolution.parse(resString);
        this.mMaxHeight = cameraResolution.getHeight();
        this.mMaxWidth = cameraResolution.getWidth();

        String bitrateValue = prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_VIDEO_BITRATE, String.valueOf(AppController.getDefaultSettings().videoSettings.profile.videoBitRate));
        if (!this.getContext().getString(R.string.default_video_bitrate).equals(bitrateValue)) {
            try {
                this.videoBitRate = Integer.parseInt(bitrateValue);
                profile.videoBitRate = videoBitRate;
            } catch (NumberFormatException exception) {
                LOGE(TAG, "Video bitrate invalid format, can't parse to int: " + bitrateValue);
            }
        }

        bitrateValue = prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_AUDIO_BITRATE, String.valueOf(AppController.getDefaultSettings().videoSettings.profile.audioBitRate));
        if (!this.getContext().getString(R.string.default_audio_bitrate).equals(bitrateValue)) {
            try {
                this.audioBitRate = Integer.parseInt(bitrateValue);
                profile.audioBitRate = audioBitRate;
            } catch (NumberFormatException exception) {
                LOGE(TAG, "Audio bitrate invalid format, can't parse to int: " + bitrateValue);
            }
        }
        profile.fileFormat = this.outputMediaFormat;
        profile.videoCodec = this.videoCodec;
        profile.audioCodec = this.audioCodec;

        final String fps = String.valueOf(AppController.getDefaultSettings().videoSettings.previewFpsRange[1] / 1000);
        try {
            this.videoFrameRate = Integer.parseInt(prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_VIDEO_FRAME_RATE, fps));
        } catch (NumberFormatException exception) {
            LOGE(TAG, "fps invalid format, can't parse to int");
        }

        getAudioManagerStreams();
    }

    public List<String> getSupportedColorEffects() {
        try {
            return mCamera.getParameters().getSupportedColorEffects();
        } catch (Exception ex) {
            return new ArrayList<String>();
        }
    }

    public boolean isColorEffectSupported() {
        return (mCamera.getParameters().getColorEffect() != null);
    }

    public String getColorEffect() {
        return mCamera.getParameters().getColorEffect();
    }

    public void setColorEffect(final String effect) {
        try {
            synchronized (this) {
                Camera.Parameters params = mCamera.getParameters();
                params.setColorEffect(effect);
                mCamera.setParameters(params);
            }
        } catch (Exception e) {
            LOGE(TAG, "[setColorEffect] Error setting color effect", e);
        }
    }

    public void setSceneMode(final String sceneMode) {
        try {
            synchronized (this) {
                Camera.Parameters params = mCamera.getParameters();
                params.setSceneMode(sceneMode);
                mCamera.setParameters(params);
            }
        } catch (Exception e) {
            LOGE(TAG, "[setSceneMode] Error setting scene mode", e);
        }
    }

    public void setZoom(final int zoom) {
        try {
            if (!isZoomSupported())
                return;

            synchronized (this) {
                Camera.Parameters params = mCamera.getParameters();
                params.setZoom(params.getMaxZoom() * zoom / 100);
                mCamera.setParameters(params);
            }
        } catch (Exception e) {
            LOGE(TAG, "[setZoom] Error setting zoom level", e);
        }
    }

    public List<Size> getSupportedPreviewSizes() {
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    private void setCamera(final int type) {
        setCameraIndex(type);
    }

    public void setCameraSize(final Size size) {
        synchronized (this) {
            disableView();
            mMaxHeight = size.height;
            mMaxWidth = size.width;
            enableView();
        }
    }

    public Parameters getParameters() {
        if (mCamera == null)
            return null;
        return mCamera.getParameters();
    }

    public void setParameters(final Parameters params) {
        synchronized (this) {
            mCamera.setParameters(params);
        }
    }

    public void setPreviewCallbackWithBuffer(final PreviewCallback cb) {
        mCamera.setPreviewCallbackWithBuffer(cb);
    }

    public int getCameraIndex() {
        return this.mCameraIndex;
    }

    public Size getPreviewSize() {
        return mCamera.getParameters().getPreviewSize();
    }

    public void takePicture() {
        LOGD(TAG, "Taking picture");

        // Post view and JPG are sent in the same buffers if the queue is not
        // empty when performing a capture.

        // Clear up buffers to avoid mCamera.takePicture to be stuck because of
        // a memory issue
        // mCamera.setPreviewCallback(null);

        // PictureCallback is implemented by the current class
        mCamera.takePicture(null, null, this);
    }

    public void autoFocus(final AutoFocusCallback cb) {
        mCamera.autoFocus(cb);
    }

    @Override
    public void onPictureTaken(final byte[] data, final Camera camera) {
        if (data != null) {
            LOGD(TAG, "[onPictureTaken] Saving picture");
        }

        // The camera preview was automatically stopped. Start it again.
        // mCamera.startPreview();
        // mCamera.setPreviewCallback(this);

    }

    public List<String> getSceneModes() {
        try {
            return mCamera.getParameters().getSupportedSceneModes();
        } catch (Exception ex) {
            return new ArrayList<String>();
        }
    }

    public boolean isZoomSupported() {
        return mCamera.getParameters().isZoomSupported();
    }

    public void setFocusMode(final String focusMode) {
        try {
            synchronized (this) {
                Camera.Parameters params = mCamera.getParameters();
                params.setFocusMode(focusMode);
                mCamera.setParameters(params);
            }
        } catch (Exception e) {
            LOGE(TAG, "[setFocusMode] Error setting focus mode", e);
        }
    }

    public List<String> getSupportedFlashModes() {
        try {
            Camera.Parameters params = mCamera.getParameters();
            return params.getSupportedFlashModes();
        } catch (Exception e) {
            LOGE(TAG, "[getSupportedFlashModes] Error retrieving flash modes", e);
        }
        return new ArrayList<String>();
    }

    public void setFlashMode(final String flashMode) {
        try {
            synchronized (this) {
                Camera.Parameters params = mCamera.getParameters();
                params.setFlashMode(flashMode);
                mCamera.setParameters(params);
            }
        } catch (Exception e) {
            LOGE(TAG, "[setFlashMode] Error setting flash mode", e);
        }
    }

    public List<String> getSupportedFocusModes() {
        try {
            Camera.Parameters params = mCamera.getParameters();
            return params.getSupportedFocusModes();
        } catch (Exception e) {
            LOGE(TAG, "[getSupportedFocusModes] Error retrieving focus modes", e);
        }
        return new ArrayList<String>();
    }

    public List<String> getSupportedWhiteBalance() {
        try {
            Camera.Parameters params = mCamera.getParameters();
            return params.getSupportedWhiteBalance();
        } catch (Exception e) {
            LOGE(TAG, "[getSupportedWhiteBalance] Error retrieving white balance", e);
        }
        return new ArrayList<String>();
    }

    public void setWhiteBalance(final String whiteBalance) {
        try {
            synchronized (this) {
                Camera.Parameters params = mCamera.getParameters();
                params.setWhiteBalance(whiteBalance);
                mCamera.setParameters(params);
            }
        } catch (Exception e) {
            LOGE(TAG, "[setWhiteBalance] Error setting white balance", e);
        }
    }

    public List<String> getSupportedAntibanding() {
        try {
            Camera.Parameters params = mCamera.getParameters();
            return params.getSupportedAntibanding();
        } catch (Exception e) {
            LOGE(TAG, "[getSupportedAntibanding] Error retrieving antibanding", e);
        }
        return new ArrayList<String>();
    }

    public void setAntibanding(final String antibanding) {
        try {
            synchronized (this) {
                Camera.Parameters params = mCamera.getParameters();
                params.setAntibanding(antibanding);
                mCamera.setParameters(params);
            }
        } catch (Exception e) {
            LOGE(TAG, "[setAntibanding] Error setting antibanding", e);
        }
    }

    public void setAutoExposureLock(final boolean enabled) {
        try {
            synchronized (this) {
                Camera.Parameters params = mCamera.getParameters();
                if (!params.isAutoExposureLockSupported())
                    return;
                params.setAutoExposureLock(enabled);
                mCamera.setParameters(params);
            }
        } catch (Exception e) {
            LOGE(TAG, "[setAutoExposureLock] Error setting auto exposure lock", e);
        }
    }

    public boolean getAutoExposureLock() {
        Camera.Parameters params = mCamera.getParameters();
        if (!params.isAutoExposureLockSupported())
            return false;
        return params.getAutoExposureLock();
    }

    public int getExposureCompensation() {
        Camera.Parameters params = mCamera.getParameters();
        return params.getExposureCompensation();
    }

    public int getMinExposureCompensation() {
        Camera.Parameters params = mCamera.getParameters();
        return params.getMinExposureCompensation();
    }

    public int getMaxExposureCompensation() {
        Camera.Parameters params = mCamera.getParameters();
        return params.getMaxExposureCompensation();
    }

    public void setExposureCompensation(int exposure) {
        try {
            synchronized (this) {
                Camera.Parameters params = mCamera.getParameters();
                int currentExposure = params.getExposureCompensation();
                if (exposure != currentExposure) {
                    params.setExposureCompensation(exposure);
                    mCamera.setParameters(params);
                }
            }
        } catch (Exception e) {
            LOGE(TAG, "[setExposureCompensation] Error setting exposure compensation", e);
        }
    }

    public void setupCamera(final int width, final int height) {
        LOGD(TAG, "[setupCamera] Setting up camera with " + width + "x" + height);
        Camera.Parameters p = getParameters();
        if (p != null) {
            Camera.Size size = getPreviewSize();
            size.width = width;
            size.height = height;
            setCameraSize(size);
        }
    }

    public void switchCamera() {
        synchronized (this) {
            if (Camera.getNumberOfCameras() > 1) {
                disableView();
                if (getCameraIndex() == CameraBridgeViewBase.CAMERA_ID_FRONT) {
                    setCamera(CameraBridgeViewBase.CAMERA_ID_BACK);
                } else if (getCameraIndex() == CameraBridgeViewBase.CAMERA_ID_BACK) {
                    setCamera(CameraBridgeViewBase.CAMERA_ID_FRONT);
                } else {
                    setCamera(CameraBridgeViewBase.CAMERA_ID_FRONT);
                }
                enableView();
            }
        }
    }

    public void configureCamera(final int type) {
        synchronized (this) {
            disableView();
            setCamera(type);
            enableView();
        }
    }

    public void disable() {
        synchronized (this) {
            disableView();
        }
    }

    public boolean isRecording() {
        return this.isRecording || this.isMediaRecorderPrepared;
    }

    public String getRecordingFileName() {
        return this.mediaFilePath;
    }

    public void startRecording() {
        if (!isRecording && !isMediaRecorderPrepared) {
            this.videoPrefix = VIDEO_FILE_PREFIX;
            new MediaPrepareTask().execute(null, null, null);
        }
    }

    public void startRecording(final String prefix) {
        if (!isRecording && !isMediaRecorderPrepared) {
            this.videoPrefix = processVideoPrefix(prefix);
            new MediaPrepareTask().execute(null, null, null);
        }
    }

    private String processVideoPrefix(final String prefix) {
        String videoPrefix = prefix;
        if (videoPrefix == null || TextUtils.isEmpty(videoPrefix)) {
            return VIDEO_FILE_PREFIX;
        }
        if (!videoPrefix.endsWith("_")) {
            videoPrefix += "_";
        }
        videoPrefix = videoPrefix.replace(" ", "_");
        return videoPrefix;
    }

    public void stopRecording() {
        if (isRecording || isMediaRecorderPrepared) {
            synchronized (this) {
                Thread t = new Thread() {
                    @Override
                    public void run() {
                        try {
                            if (!isRecording && !isMediaRecorderPrepared) return;
                            stopRecorder();
                        } catch (Exception ex) {
                            LOGE(TAG, "[run] Exception releasing media recorder", ex);
                        }
                    }
                };
                t.start();
                try {
                    t.join();
                } catch (InterruptedException ite) {
                    LOGE(TAG, "[stopRecording] Thread is interrupted", ite);
                }
            }
        }
    }

    private void stopRecorder() {
        boolean hasError = true;
        try {
            if (isRecording) {

                LOGW(TAG, "[stopRecorder] Stopping media recorder");

                // stop recording and release camera
                mediaRecorder.stop(); // stop the recording

                hasError = false;

                LOGW(TAG, "[stopRecorder] Media recorder stopped");
            }
        } catch (RuntimeException ex) {
            LOGE(TAG, "[stopRecorder] Error stop recording", ex);
            File f = new File(this.mediaFilePath);
            if (f != null && f.exists()) {
                f.delete();
            }
        } finally {
            releaseMediaRecorder(); // release the MediaRecorder object
            if (!hasError) {
                LOGE(TAG, "[stopRecorder] Adding video to gallery");
                // Add the media file to the gallery
                StorageUtils.addVideoToGallery(this.mediaFileName, this.mediaFilePath, this.getContext(), VideoFormat.getContentType(this.videoFormat));
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private boolean prepareVideoRecorder() {

        if (isRecording || isMediaRecorderPrepared) return false;

        LOGW(TAG, "[prepareVideoRecorder] Start preparing video recorder");

        // We need to make sure that our preview and recording video size are supported by the
        // camera. Query camera to find all the sizes and choose the optimal size given the
        // dimensions of our preview surface.
        Camera.Parameters parameters = mCamera.getParameters();
        final List<Size> videoSizes = getSupportedVideoSizes();
        final List<int[]> fpsRanges = mCamera.getParameters().getSupportedPreviewFpsRange();
        final int[] fpsRange = getPreviewFpsRange(fpsRanges, this.videoFrameRate);

        if (fpsRange[0] > 0 && fpsRange[1] > 0) {
            parameters.setPreviewFpsRange(fpsRange[0], fpsRange[1]);
        }

        // Set video stabilization
        parameters.setVideoStabilization(this.videoStabilization);
        mCamera.setParameters(parameters);

        // Use the same size for recording profile.
        // CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        // this.resolution = getSupportedResolution(this.resolution, mCamera);
        // profile.videoFrameWidth = resolution.getWidth();
        // profile.videoFrameHeight = resolution.getHeight();

        mediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder

        mCamera.stopPreview();
        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        final String audioSrcValue = AppPreference.getInstance().getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_AUDIO_SOURCE, this.getContext().getString(R.string.default_audio_src));
        try {
            this.audioSource = Integer.parseInt(audioSrcValue);
            mediaRecorder.setAudioSource(this.audioSource);
        } catch (NumberFormatException exception) {
            LOGE(TAG, "Audio source invalid format, can't parse to int: " + audioSrcValue);
        }
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        // int cameraIdx = Camera.CameraInfo.CAMERA_FACING_BACK;
        // if (mCameraIndex == CAMERA_ID_FRONT) {
        // cameraIdx = Camera.CameraInfo.CAMERA_FACING_FRONT;
        // }

        if (isProfileSupported(videoSizes)) {
            mediaRecorder.setProfile(profile);
        } else {
            mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
        }

        try {
            // Step 4: Set output file
            // mediaRecorder.setMaxDuration(videoChunkSizeMinutes * 60 * 1000);
            // mediaRecorder.setMaxFileSize(5000000);

            File mediaFile = getOutputMediaFile();
            this.mediaFilePath = mediaFile.getAbsolutePath();
            this.mediaFileName = mediaFile.getName();
            mediaRecorder.setOutputFile(this.mediaFilePath);

            mediaRecorder.setMaxDuration(videoChunkSizeMinutes * 60 * 1000);
            mediaRecorder.setOnInfoListener(Webcam.this);

            // Step 5: Prepare configured MediaRecorder
            if (isRecording || isMediaRecorderPrepared) return false;

            mediaRecorder.prepare();

            isMediaRecorderPrepared = true;

        } catch (IllegalStateException e) {
            LOGE(TAG, "[prepareVideoRecorder] IllegalStateException preparing media recorder", e);
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            LOGE(TAG, "[prepareVideoRecorder] IOException preparing media recorder", e);
            releaseMediaRecorder();
            return false;
        }
        LOGW(TAG, "[prepareVideoRecorder] Done preparing video recorder");

        return true;
    }

    public void releaseAll() {
        stopRecording();
    }

    private void releaseMediaRecorder() {
        try {
            if (!isRecording && !isMediaRecorderPrepared) return;

            if (mediaRecorder != null) {

                // clear recorder configuration
                mediaRecorder.reset();

                // release the recorder object
                mediaRecorder.release();
                mediaRecorder = null;

                isRecording = false;
                isMediaRecorderPrepared = false;

                // Lock camera for later use i.e taking it back from MediaRecorder.
                // MediaRecorder doesn't need it anymore and we will release it if the activity pauses.
                mCamera.lock();

                setMuteAll(false);

                // Reset preview call back
                previewCallback();
            }
        } catch (Exception ex) {
            LOGE(TAG, "[releaseMediaRecorder] IOException releasing media recorder", ex);
        }
    }

    /**
     * Asynchronous task for preparing the {@link android.media.MediaRecorder} since it's a long blocking operation.
     */
    class MediaPrepareTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {

                if (isRecording || isMediaRecorderPrepared) return false;

                synchronized (this) {
                    // initialize video camera
                    if (prepareVideoRecorder()) {

                        LOGW(TAG, "[MediaPrepareTask] Starting video recorder");

                        setMuteAll(true);
                        if (!isRecording) {

                            // Camera is available and unlocked, MediaRecorder is prepared, now you can start recording
                            mediaRecorder.start();

                            isRecording = true;
                        }

                        LOGW(TAG, "[MediaPrepareTask] Done starting video recorder");
                    } else {
                        // prepare didn't work, release the camera
                        releaseMediaRecorder();
                        return false;
                    }
                }
            } catch (Exception ex) {
                LOGE(TAG, "[doInBackground] Exception preparing media recorder", ex);
                return false;
            }
            return true;
        }
    }

    public File getOutputMediaFile() throws IOException {
        File dir = StorageUtils.getStorageDir(getContext(), deviceName);
        //File file = new File(dir.getPath() + File.separator + this.videoPrefix + DateUtils.getCurrentDateString().replaceAll(" ", "_") + fileExtension);
        File file = new File(dir, this.videoPrefix + StorageUtils.fixLocalFileName(DateUtils.getCurrentDateString()) + fileExtension);
        return file;
    }

    private CamcorderProfile getConfiguredResolution() {
        try {
            final String res = AppPreference.getInstance().getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_VIDEO_RESOLUTION, AppController.getDefaultSettings().videoSettings.videoQuality.get(0));
            return DeviceUtils.getCamcorderProfile(res);
        } catch (Exception ex) {
            return CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        }
    }

    private List<Size> getSupportedVideoSizes() {
        List<Size> supportedVideoSizes = null;
        if (mCamera.getParameters().getSupportedVideoSizes() != null) {
            supportedVideoSizes = mCamera.getParameters().getSupportedVideoSizes();
        } else {
            supportedVideoSizes = mCamera.getParameters().getSupportedPreviewSizes();
        }
        return supportedVideoSizes;
    }

    private boolean isProfileSupported(final List<Size> supportedVideoSizes) {
        for (Size size : supportedVideoSizes) {
            if (size.width == profile.videoFrameWidth && size.height == profile.videoFrameHeight) {
                return true;
            }
        }
        return false;
    }

    private int[] getPreviewFpsRange(final List<int[]> fpsRanges, int videoFrameRate) {
        int selectedMinFps = -1, selectedMaxFps = -1, selectedDiff = -1;
        for (int[] fpsRange : fpsRanges) {
            int minFps = fpsRange[0];
            int maxFps = fpsRange[1];
            if (minFps <= videoFrameRate * 1000 && maxFps >= videoFrameRate * 1000) {
                int diff = maxFps - minFps;
                if (selectedDiff == -1 || diff < selectedDiff) {
                    selectedMinFps = minFps;
                    selectedMaxFps = maxFps;
                    selectedDiff = diff;
                }
            }
        }
        if (selectedMinFps == -1) {
            selectedDiff = -1;
            int selectedDist = -1;
            for (int[] fpsRange : fpsRanges) {
                int minFps = fpsRange[0];
                int maxFps = fpsRange[1];
                int diff = maxFps - minFps;
                int dist = -1;
                if (maxFps < videoFrameRate * 1000)
                    dist = videoFrameRate * 1000 - maxFps;
                else
                    dist = minFps - videoFrameRate * 1000;
                if (selectedDist == -1 || dist < selectedDist || (dist == selectedDist && diff < selectedDiff)) {
                    selectedMinFps = minFps;
                    selectedMaxFps = maxFps;
                    selectedDist = dist;
                    selectedDiff = diff;
                }
            }
            return new int[]{selectedMinFps, selectedMaxFps};
        } else {
            return new int[]{selectedMinFps, selectedMaxFps};
        }
    }

    private void setMuteAll(boolean mute) {
        try {
            if (mute) {
                setMuteAll();
            } else {
                unMuteAll();
            }
        } catch (Exception ex) {
            LOGE(TAG, "[setMuteAll] Error in muting", ex);
        }
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            LOGW(TAG, "[onInfo] Restarting video recording");
            stopRecording();
            startRecording();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        stopRecording();
        super.surfaceChanged(holder, format, width, height);
    }

    private void getAudioManagerStreams() {
        // different versions of Android use different streams
        // so we have to mute them all
        audioUtils.storeAudioStreamSettings();
    }

    private void setMuteAll() {
        audioUtils.muteAll();
    }

    private void unMuteAll() {
        audioUtils.unMuteAll();
    }
}