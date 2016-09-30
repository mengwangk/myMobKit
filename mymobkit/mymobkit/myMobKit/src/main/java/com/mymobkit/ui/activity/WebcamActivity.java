package com.mymobkit.ui.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filter;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mymobkit.R;
import com.mymobkit.app.AppConfig;
import com.mymobkit.app.AppController;
import com.mymobkit.audio.codec.Mp3Encoder;
import com.mymobkit.common.AppPreference;
import com.mymobkit.common.CvUtils;
import com.mymobkit.common.DateUtils;
import com.mymobkit.common.DeviceUtils;
import com.mymobkit.common.GcmUtils;
import com.mymobkit.common.ImageUtils;
import com.mymobkit.common.MimeType;
import com.mymobkit.common.NetworkUtils;
import com.mymobkit.common.ServiceUtils;
import com.mymobkit.common.SmsUtils;
import com.mymobkit.common.StorageUtils;
import com.mymobkit.common.StringUtils;
import com.mymobkit.common.ToastUtils;
import com.mymobkit.enums.AddressFamily;
import com.mymobkit.enums.MotionDetectionType;
import com.mymobkit.enums.ProcessingAction;
import com.mymobkit.enums.TrueFalseEnum;
import com.mymobkit.enums.WebcamFeature;
import com.mymobkit.enums.WebcamQuery;
import com.mymobkit.gcm.GcmMessage;
import com.mymobkit.gcm.message.MotionMessage;
import com.mymobkit.gcm.message.SurveillanceMessage;
import com.mymobkit.gcm.message.SwitchCameraMessage;
import com.mymobkit.model.ActionStatus;
import com.mymobkit.model.ExposureCompensation;
import com.mymobkit.model.RecordingStatus;
import com.mymobkit.model.Resolution;
import com.mymobkit.net.GMailSender;
import com.mymobkit.net.StreamingServer;
import com.mymobkit.net.provider.Processor;
import com.mymobkit.opencv.image.NightVisionFilter;
import com.mymobkit.opencv.motion.detection.BackgroundSubtractorDetector;
import com.mymobkit.opencv.motion.detection.BaseDetector;
import com.mymobkit.opencv.motion.detection.BasicDetector;
import com.mymobkit.opencv.motion.detection.IDetector;
import com.mymobkit.opencv.motion.detection.data.GlobalData;
import com.mymobkit.service.HttpdService;
import com.mymobkit.ui.adapter.CameraMenuItem;
import com.mymobkit.ui.base.SensorsActivity;
import com.mymobkit.ui.fragment.DetectionSettingsFragment;
import com.mymobkit.ui.fragment.ServiceSettingsFragment;
import com.mymobkit.webcam.ByteBufferStore;
import com.mymobkit.webcam.CameraViewAdapter;
import com.mymobkit.webcam.CameraViewAdapter.CvCameraViewListener2;
import com.mymobkit.webcam.CvMatStore;
import com.mymobkit.webcam.CvVideoFrame;
import com.mymobkit.webcam.DataStream;
import com.mymobkit.webcam.VideoDataStream;
import com.mymobkit.webcam.VideoFrame;
import com.mymobkit.webcam.Webcam;
import com.mymobkit.webcam.WebcamController;
import com.mymobkit.webcam.WebcamOverlay;
import com.squareup.otto.Subscribe;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractor;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;
import haibison.android.lockpattern.LockPatternActivity;
import haibison.android.lockpattern.utils.AlpSettings;

import static com.mymobkit.common.LogUtils.LOGD;
import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.LOGI;
import static com.mymobkit.common.LogUtils.LOGW;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Webcam activity.
 */
@SuppressWarnings("deprecation")
public final class WebcamActivity extends SensorsActivity
        implements View.OnTouchListener, CvCameraViewListener2,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = makeLogTag(WebcamActivity.class);

    public class AppBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(AppConfig.INTENT_SHUTDOWN_SURVEILLANCE_ACTION)) {
                LOGD(TAG, "received shutdown request");
                Message msg = featureHandler.obtainMessage();
                msg.what = WebcamFeature.SHUTDOWN.getHashCode();
                Bundle bundle = new Bundle();
                bundle.putString("value", StringUtils.EMPTY);
                msg.setData(bundle);
                featureHandler.sendMessage(msg);
            } else if (action.equalsIgnoreCase(AppConfig.INTENT_IP_ADDRESS_CHANGE_ACTION)) {
                LOGD(TAG, "received IP change request");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (streamingServer != null) {
                            streamingServer.stop();
                        }
                        setupStreamingServer();
                    }
                });
            }
        }
    }

    // OpenCV initializer
    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
            LOGW(TAG, "[Initializer] Unable to initialize OpenCV");
        }
    }

    private AppBroadcastReceiver appBroadcastReceiver = null;
    boolean isAppReceiverRegistered = false;

    private StreamingServer streamingServer = null;
    private WebcamController webcamController = null;
    private TextView tvMessage1;
    private TextView tvOption;

    private int streamingPort;

    private IDetector motionDetector;
    private DrawerLayout drawerLayout;
 	private NightVisionFilter nightVision;
    private boolean isNightVisionStreaming;
    private ListView drawerList;
    private CameraMenuAdapter menuAdapter;
    private ActionBarDrawerToggle drawerToggle;
    private String[] cameraMenu;
    private int chosenResolution;
    private float faceDetectionSize;
    private boolean motionDetectionRequired;
    private int motionDetectionThreshold;
    private volatile int jpegQuality;
    private String motionDetectionAlgorithm;
    private String motionDetectionType;
    private int motionDetectionContourThickness;
    private volatile boolean streamDetectedObject;
    private static boolean isScreenLocked;
    private boolean isUseIPv4;

    private static final int AUDIO_FREQUENCY = 44100;
    private static final int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private volatile boolean isStreamingAudio;

    private static final int AUDIO_TIMER_INTERVAL = 120;
    private short bSamples;
    private int nChannels;
    private int framePeriod;
    private int audioBufferSize;

    private MediaPlayer mediaPlayer;
    private Mp3Encoder mp3Encoder;
    private AudioRecord audioRecorder = null;
    private List<DataStream> audioStreams = null;
    private List<DataStream> newAudioStreams = null;

    private List<VideoDataStream> videoStreams = null;
    private volatile boolean isStreamingVideo;

    private boolean isScreenFaded = false;

    private static AsyncHttpClient httpClient = new AsyncHttpClient();

    private WebView webView;
    private boolean disguiseCamera;
    private boolean stealthMode;
    private boolean motionDetectionRecordVideo;
    private int motionDetectionRecordVideoDurationSeconds;
    //private int videoHousekeepingMb;

    private Timer videoTimer = new Timer();
    private TimerTask videoTimerTask = null;

    private volatile boolean isUploadInProgress;

    private String deviceName;
    private String deviceId;
    private String emailAddress;
    //private String emailPassword;
    private String phoneNumber;
    private boolean notifyByEmail;
    private boolean notifyBySms;
    private boolean notifyByGcm;
    private int alarmTriggerInterval;
    private int noOfTriggers;
    private boolean localStorage;
    private boolean cloudStorage;
    private boolean emailStorage;
    private boolean googleDriveStorage;
    private String alarmSoundType;

    private long triggerTimestamp;
    private int currentNoOfTriggers;
    private String urlRequestBlobUrl;

    private ByteBufferStore byteBufferStore;
    private CvMatStore cvFrameStore;
    private Mat yuvFrame;

    private int frameWidth;
    private int frameHeight;
    private Rect rect;
    private int previewFormat;
    private int previewBufferSize;

    private boolean frameProcessing = false;
    private String controlPanelUrl;
    private int triggeredCount = 0;


    private boolean isServiceStartRequired;

    private PowerManager powerMgr;
    private PowerManager.WakeLock wakeLock;

    private DriveId driveFolderId;

    private Thread cameraWorkerThread;
    private boolean stopCameraWorkerThread;

    private void setSceneMode(final String sceneMode) {
        webcamController.setSceneMode(sceneMode);
    }

    private void setColorEffect(final String colorEffect) {
        webcamController.setColorEffect(colorEffect);
    }

    private void setFlashMode(final String flashMode) {
        webcamController.setFlashMode(flashMode);
    }

    private void setFocusMode(final String focusMode) {
        webcamController.setFocusMode(focusMode);
    }

    private void setWhiteBalance(final String whiteBalance) {
        webcamController.setWhiteBalance(whiteBalance);
    }

    private void setAntibanding(final String antibanding) {
        webcamController.setAntibanding(antibanding);
    }

    private void setAutoExposureLock(final String enabled) {
        TrueFalseEnum tf = TrueFalseEnum.get(enabled);
        if (tf == TrueFalseEnum.TRUE) {
            webcamController.setAutoExposureLock(true);
        } else {
            webcamController.setAutoExposureLock(false);
        }
    }

    private void setNightVisionMode(final String enabled) {
        TrueFalseEnum tf = TrueFalseEnum.get(enabled);
        if (tf == TrueFalseEnum.TRUE) {
            isNightVisionStreaming = true;
        } else {
            isNightVisionStreaming = false;
        }
    }

    private void setNightVisionHistogramEqualization(final String enabled){
        TrueFalseEnum tf = TrueFalseEnum.get(enabled);
        if (tf == TrueFalseEnum.TRUE) {
            nightVision.setUseHistogramEqualization(true);
        } else {
            nightVision.setUseHistogramEqualization(false);
        }
    }

    private void setNightVisionHistogramEqualizationColor(final String enabled){
        TrueFalseEnum tf = TrueFalseEnum.get(enabled);
        if (tf == TrueFalseEnum.TRUE) {
            if (nightVision.isColor()) return;
            nightVision.setColor(true);
            nightVision.configure();
        } else {
            if (!nightVision.isColor()) return;
            nightVision.setColor(false);
            nightVision.configure();
        }
    }

    private void setNightVisionGammaCorrection(final String enabled){
        TrueFalseEnum tf = TrueFalseEnum.get(enabled);
        if (tf == TrueFalseEnum.TRUE) {
            nightVision.setUseGammaCorrection(true);
        } else {
            nightVision.setUseGammaCorrection(false);
        }
    }

    private void setMotionDetectionMode(final String enabled){
        TrueFalseEnum tf = TrueFalseEnum.get(enabled);
        if (tf == TrueFalseEnum.TRUE) {
            motionDetectionRequired = true;
        } else {
            motionDetectionRequired = false;
        }
    }

    private void setNightVisionGammaLevel(String level) {
        try {
            int value = Integer.parseInt(level);
            nightVision.setGamma(value);
        } catch (Exception ex) {
            LOGE(TAG, "[setNightVisionGammaLevel] Error setting gamma level", ex);
        }
    }
    private void takePhoto() {
        final byte[] fileData = getCurrentSnapshot();
        if (fileData != null) {
            final String currentDateString = DateUtils.getCurrentDateString();
            final String filePath = saveToLocalStorage(fileData, currentDateString);
            saveToGoogleDrive(filePath, fileData, currentDateString);
            triggerCloudStorage(fileData);
        }
    }

    private void setExposureCompensation(final String exposure) {
        try {
            int value = Integer.parseInt(exposure);
            webcamController.setExposureCompensation(value);
        } catch (Exception ex) {
            LOGE(TAG, "[setExposureCompensation] Error setting exposure compensation", ex);
        }
    }

    private void startVideoRecording() {
        try {
            webcamController.startRecording();
        } catch (Exception ex) {
            LOGE(TAG, "[startVideoRecording] Error recording video", ex);
        }
    }

    private void startVideoRecording(final String prefix) {
        try {
            webcamController.startRecording(prefix);
        } catch (Exception ex) {
            LOGE(TAG, "[startVideoRecording] Error recording video", ex);
        }
    }

    private void stopVideoRecording() {
        stopVideoRecorder();
        if (videoTimerTask != null) {
            if (!videoTimerTask.cancel()) {
                LOGW(TAG, "[stopVideoRecording] Unable to cancel video timer task");
            } else {
                videoTimerTask = null;
            }
        }
    }

    private void stopVideoRecorder() {
        try {
            if (webcamController.isRecording()) {
                webcamController.stopRecording();
            }
        } catch (Exception ex) {
            LOGE(TAG, "[stopVideoRecorder] Error stopping video recording", ex);
        }
    }

    private void setImageQuality(final String quality) {
        try {
            int value = Integer.parseInt(quality);
            if (value > 0 && value <= 100)
                jpegQuality = value;
        } catch (Exception ex) {
            LOGE(TAG, "[setImageQuality] Error setting image quality", ex);
        }
    }

    private void setMotionDetectionThreshold(final String threshold){
        try {
            int value = Integer.parseInt(threshold);
            if (value > 0 && value <= 255) {
                motionDetectionThreshold = value;
                if (motionDetector instanceof BaseDetector){
                    ((BasicDetector)motionDetector).setThreshold(value);
                } else if (motionDetector instanceof BackgroundSubtractor){
                    ((BackgroundSubtractorDetector)motionDetector).setThreshold(value);
                }
            }
        } catch (Exception ex) {
            LOGE(TAG, "[setMotionDetectionThreshold] Error setting motion detection threshold", ex);
        }
    }

    private void setZoom(final String zoom) {
        try {
            int zoomLevel = Integer.parseInt(zoom);
            webcamController.setZoom(zoomLevel);
        } catch (Exception ex) {
            LOGE(TAG, "[setZoom] Error setting zoom", ex);
        }
    }

    private void toggleCamera() {
        stopVideoRecording();
        webcamController.switchCamera();
    }

    private void toggleMotionView() {
        streamDetectedObject = !streamDetectedObject;
    }

    private void toggleLed(final String value) {
        TrueFalseEnum tf = TrueFalseEnum.get(value);
        if (tf == TrueFalseEnum.TRUE) {
            toggleCameraLed(true);
        } else {
            toggleCameraLed(false);
        }
    }

    private void shutdownCamera() {
        onBackPressed();
    }

    private void startVideoStreamingTask() {
        for (VideoDataStream stream : videoStreams) {
            if (!stream.isStreaming()) {
                VideoStreamingTask streamingTask = new VideoStreamingTask();
                stream.setStreamingMode(true);
                streamingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, stream);
            }
        }
    }

    private void startAudioStreamingTask() {
        for (DataStream stream : audioStreams) {
            if (!stream.isStreaming()) {
                AudioStreamingTask streamingTask = new AudioStreamingTask();
                stream.setStreamingMode(true);
                streamingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, stream);
            }
        }
    }

    private void disguiseCamera(final String value) {
        TrueFalseEnum tf = TrueFalseEnum.get(value);
        if (tf == TrueFalseEnum.TRUE) {
            webView.setVisibility(View.VISIBLE);
            setMessageVisibility(View.INVISIBLE);
        } else {
            webView.setVisibility(View.INVISIBLE);
            setMessageVisibility(View.VISIBLE);
        }
    }

    protected Handler featureHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            LOGD(TAG, "[handleMessage] Received feature request");
            final WebcamFeature feature = WebcamFeature.get(msg.what);
            String value = msg.getData().getString("value");
            if (feature != null) {
                if (feature == WebcamFeature.TOGGLE_CAMERA) {
                    toggleCamera();
                } else if (feature == WebcamFeature.VIEW_MOTION) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleMotionView();
                        }
                    });
                } else if (feature == WebcamFeature.TOGGLE_LED) {
                    toggleLed(value);
                } else if (feature == WebcamFeature.SHUTDOWN) {
                    shutdownCamera();
                } else if (feature == WebcamFeature.LOCK_CAMERA) {
                    lockScreen();
                } else if (feature == WebcamFeature.HEART_BEAT) {
                    // Do nothing
                } else if (feature == WebcamFeature.VIDEO_STREAMING) {
                    startVideoStreamingTask();
                } else if (feature == WebcamFeature.AUDIO_STREAMING) {
                    startAudioStreamingTask();
                } else if (feature == WebcamFeature.DISGUISE) {
                    disguiseCamera(value);
                } else if (feature == WebcamFeature.SCENE_MODE) {
                    setSceneMode(value);
                } else if (feature == WebcamFeature.COLOR_EFFECT) {
                    setColorEffect(value);
                } else if (feature == WebcamFeature.IMAGE_QUALITY) {
                    setImageQuality(value);
                } else if (feature == WebcamFeature.ZOOM) {
                    setZoom(value);
                } else if (feature == WebcamFeature.FLASH_MODE) {
                    setFlashMode(value);
                } else if (feature == WebcamFeature.FOCUS_MODE) {
                    setFocusMode(value);
                } else if (feature == WebcamFeature.WHITE_BALANCE) {
                    setWhiteBalance(value);
                } else if (feature == WebcamFeature.ANTI_BANDING) {
                    setAntibanding(value);
                } else if (feature == WebcamFeature.AUTO_EXPOSURE_LOCK) {
                    setAutoExposureLock(value);
                } else if (feature == WebcamFeature.EXPOSURE_COMPENSATION) {
                    setExposureCompensation(value);
                } else if (feature == WebcamFeature.RECORD_VIDEO_START) {
                    startVideoRecording(value);
                } else if (feature == WebcamFeature.RECORD_VIDEO_STOP) {
                    stopVideoRecording();
                } else if (feature == WebcamFeature.NIGHT_VISION_MODE) {
                    setNightVisionMode(value);
                } else if (feature == WebcamFeature.TAKE_PHOTO) {
                    takePhoto();
                } else if (feature == WebcamFeature.NIGHT_VISION_HISTOGRAM_EQUALIZATION) {
                    setNightVisionHistogramEqualization(value);
                } else if (feature == WebcamFeature.NIGHT_VISION_HISTOGRAM_EQUALIZATION_COLOR) {
                    setNightVisionHistogramEqualizationColor(value);
                } else if (feature == WebcamFeature.NIGHT_VISION_GAMMA_CORRECTION) {
                    setNightVisionGammaCorrection(value);
                } else if (feature == WebcamFeature.NIGHT_VISION_GAMMA_LEVEL) {
                    setNightVisionGammaLevel(value);
                } else if (feature == WebcamFeature.MOTION_DETECTION) {
                    setMotionDetectionMode(value);
                } else if (feature == WebcamFeature.MOTION_DETECTION_THRESHOLD) {
                    setMotionDetectionThreshold(value);
                }
            }
        }
    };

    private void processFrame(final byte[] frame) {
        try {
            boolean isMotionDetected = false;
            Mat bgraFrame = cvFrameStore.current();
            yuvFrame.put(0, 0, frame);
            Imgproc.cvtColor(yuvFrame, bgraFrame, Imgproc.COLOR_YUV2BGRA_NV21);

            if (motionDetectionRequired) {
                if (!GlobalData.isPhoneInMotion()) {
                    bgraFrame = motionDetector.detect(bgraFrame);
                    if (motionDetector.isDetected()) {
                        isMotionDetected = true;
                    }
                }
            }
            //if (faceDetectionRequired) {
            //    currentFrame = faceDetector.detect(currentFrame);
            //   if (faceDetector.isDetected()) {
            //        isFaceDetected = true;
            //    }
            //}
            appendCvVideoFrame(bgraFrame);
            checkMotionDetection(frame, isMotionDetected);
        } catch (Exception ex) {
            LOGE(TAG, "[handleMessage] Unable to process frame", ex);
        } finally {
            frameProcessing = false;
        }
    }

    private String saveToLocalStorage(final byte[] fileData, final String currentDateString) {
        String filePath = StringUtils.EMPTY;
        if (localStorage) {
            try {
                String name = StorageUtils.fixLocalImageName(currentDateString);
                File dir = StorageUtils.getStorageDir(this, deviceName);
                filePath = StorageUtils.saveFile(dir, name, fileData);
                StorageUtils.addImageToGallery(name, filePath, WebcamActivity.this, MimeType.IMAGE_JPEG);
            } catch (final Exception ex) {
                LOGE(TAG, "[handleMessage] Unable to save to local storage", ex);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.toastShort(WebcamActivity.this, ex.getMessage());
                    }
                });
            }
        }
        return filePath;
    }

    private void sendSmsNotification(final String content) {
        if (notifyBySms && !TextUtils.isEmpty(phoneNumber)) {
            new AsyncTask<Void, Void, String>() {
                @Override
                public String doInBackground(Void... args) {
                    try {
                        SmsUtils.sendSms(WebcamActivity.this, phoneNumber, content);
                    } catch (Exception e) {
                        LOGE(TAG, "[SmsSender] Error sending SMS", e);
                        return getString(R.string.msg_sms_error);
                    }
                    return StringUtils.EMPTY;
                }

                @Override
                protected void onPostExecute(String result) {
                    if (!TextUtils.isEmpty(result)) {
                        ToastUtils.toastShort(WebcamActivity.this, result);
                    }
                }

            }.execute();
        }
    }

    private void sendEmailNotification(final String subject, final String content, final String filePath, final byte[] fileData, final String currentDateString) {
        if (notifyByEmail && !TextUtils.isEmpty(emailAddress)) {
            final GMailSender sender = new GMailSender();
            new AsyncTask<String, Void, String>() {

                @Override
                public String doInBackground(String... args) {
                    try {
                        String filePath = args[0];
                        if (!emailStorage) {
                            sender.sendMail(subject, content, emailAddress);
                        } else {
                            File imageFile;
                            if (TextUtils.isEmpty(filePath)) {
                                // Local storage not enabled,
                                // create a temporary file and
                                // store the image temporarily
                                File outputDir = WebcamActivity.this.getCacheDir();
                                imageFile = File.createTempFile(StorageUtils.fixLocalFileName(currentDateString), StorageUtils.IMAGE_EXTENSION, outputDir);
                                FileOutputStream fos = new FileOutputStream(imageFile);
                                try {
                                    fos.write(fileData);
                                } finally {
                                    fos.close();
                                }
                                try {
                                    sender.sendMail(subject, content, emailAddress, imageFile);
                                } finally {
                                    imageFile.delete();
                                }
                            } else {
                                imageFile = new File(filePath);
                                sender.sendMail(subject, content, emailAddress, imageFile);
                            }
                        }
                    } catch (Exception e) {
                        LOGE(TAG, "[GMailSender] Error sending email", e);
                        return getString(R.string.msg_email_error);
                    }
                    return StringUtils.EMPTY;
                }

                @Override
                protected void onPostExecute(String result) {
                    if (!TextUtils.isEmpty(result)) {
                        ToastUtils.toastShort(WebcamActivity.this, result);
                    }
                }

            }.execute(filePath);
        }
    }

    private void saveToGoogleDrive(final String filePath, final byte[] fileData, final String currentDateString) {
        if (googleDriveStorage && !TextUtils.isEmpty(emailAddress)) {
            new AsyncTask<byte[], Void, String>() {
                @Override
                public String doInBackground(byte[]... args) {
                    try {
                        final byte[] fileData = args[0];
                        final String fileName = StorageUtils.fixPublicImageName(currentDateString);
                        saveFileToGoogleDrive(fileName, fileData);
                    } catch (Exception e) {
                        LOGE(TAG, "[Google Drive] Error storing to Google Drive", e);
                        return getString(R.string.msg_google_drive_folder_save_error);
                    }
                    return StringUtils.EMPTY;
                }

                @Override
                protected void onPostExecute(String result) {
                    if (!TextUtils.isEmpty(result)) {
                        ToastUtils.toastShort(WebcamActivity.this, result);
                    }
                }
            }.execute(fileData);
        }
    }

    private void triggerCloudStorage(final byte[] frame) {
        if (cloudStorage) {
            fireProcessingAction(ProcessingAction.REQUEST_UPLOAD_URL, frame);
        } else {
            resetAlarmTrigger();
        }
    }

    private void preUpload(final byte[] frame) {
        playSound(WebcamActivity.this, alarmSoundType);
        byte[] fileData;
        if (!isNightVisionStreaming) {
            fileData = ImageUtils.yuvToJpeg(frame, frameWidth, frameHeight, previewFormat, jpegQuality, rect);
        } else {
            fileData = nightVision.process(frame);
        }
        final String currentDateString = DateUtils.getCurrentDateString();
        final String filePath = saveToLocalStorage(fileData, currentDateString);
        final String subject = String.format(getString(R.string.notification_msg_subject), deviceName);
        final String content = String.format(getString(R.string.notification_msg_content), deviceName, currentDateString);
        sendSmsNotification(content);
        sendEmailNotification(subject, content, filePath, fileData, currentDateString);
        sendGcmNotification();
        saveToGoogleDrive(filePath, fileData, currentDateString);
        recordMotionVideo();
        //triggerCloudStorage(frame);
        triggerCloudStorage(fileData);
    }

    private void sendGcmNotification() {
        if (!notifyByGcm) return;

        // Use the GCM sender helper class to send
        final GcmUtils.RegistrationStatus regStatus = GcmUtils.getRegistrationStatus(this);
        if (regStatus == GcmUtils.RegistrationStatus.REGISTERED) {
            final GcmMessage message = new MotionMessage(this);
            GcmUtils.broadcast(message);
        }
    }

    private void recordMotionVideo() {
        if (motionDetectionRecordVideo) {

            if (!webcamController.isRecording()) {
                startVideoRecording();

                // Stop the recording after configured seconds
                class VideoTimerTask extends TimerTask {
                    public void run() {
                        stopVideoRecorder();
                    }
                }
                // Add some buffer seconds
                videoTimer.schedule(videoTimerTask = new VideoTimerTask(), (motionDetectionRecordVideoDurationSeconds + 5) * 1000);
            }
        }
    }

    private void requestCloudUploadUrl(final byte[] frame) {
        try {
            httpClient.get(urlRequestBlobUrl, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        final String response = responseBody == null ? null : new String(responseBody, this.getCharset());
                        LOGI(TAG, "Post status " + response);
                        if (!TextUtils.isEmpty(response)) {
                            // Upload image
                            Bundle bundle = new Bundle();
                            bundle.putString(AppConfig.UPLOAD_ACTION_PARAM, response);
                            fireProcessingAction(ProcessingAction.UPLOAD_IMAGE, bundle, frame);
                        }
                    } catch (UnsupportedEncodingException e1) {
                        LOGE(TAG, "[onSuccess] Unsupported charset", e1);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    try {

                        final String response = responseBody == null ? null : new String(responseBody, this.getCharset());

                        LOGE(TAG, "Failed to upload image. Response received: " + response, error);
                        resetAlarmTrigger();

                        final String errorMsg = (error != null ? error.getMessage() : null);

                        // Show failure message if not in stealth
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (errorMsg != null)
                                    ToastUtils.toastShort(WebcamActivity.this, errorMsg);
                            }
                        });

                    } catch (UnsupportedEncodingException e1) {
                        LOGE(TAG, "[onFailure] Unsupported charset", e1);
                    }
                }
            });
        } catch (Exception ex) {
            LOGE(TAG, "[handleMessage] Error request BLOB url", ex);
            resetAlarmTrigger();
        }
    }

    private void uploadCloudImage(final Message msg) {
        try {
            final String blobUrl = msg.getData().getString(AppConfig.UPLOAD_ACTION_PARAM);
            final Object[] args = (Object[]) msg.obj;
            final byte[] frame = (byte[]) args[0];
            LOGD(TAG, "Uploading image to " + blobUrl);
            final String fileName = String.format(getString(R.string.upload_image_name_pattern), deviceName, StorageUtils.fixPublicImageName(DateUtils.getCurrentDateString()));
            RequestParams params = new RequestParams();
            //params.put("images_1", new ByteArrayInputStream(ImageUtils.yuvToJpeg(frame, frameWidth, frameHeight, previewFormat, jpegQuality, rect)), fileName);
            params.put("images_1", new ByteArrayInputStream(frame), fileName);
            params.put("email", emailAddress);
            params.put("name", fileName);

            httpClient.post(blobUrl, params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        final String response = responseBody == null ? null : new String(responseBody, this.getCharset());
                        LOGI(TAG, "Post status " + response);
                        if (!TextUtils.isEmpty(response)) {
                            LOGI(TAG, "Post status " + response);
                        }
                    } catch (UnsupportedEncodingException e1) {
                        LOGE(TAG, "[onSuccess] Unsupported charset", e1);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    try {

                        final String response = responseBody == null ? null : new String(responseBody, this.getCharset());

                        LOGE(TAG, "Failed to upload image. Response received: " + response, error);

                        // Show failure message if not in stealth mode
                        final String errorMsg = (error != null ? error.getMessage() : null);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (errorMsg != null)
                                    ToastUtils.toastShort(WebcamActivity.this, errorMsg);
                            }
                        });

                    } catch (UnsupportedEncodingException e1) {
                        LOGE(TAG, "[onFailure] Unsupported charset", e1);
                    }
                }

                @Override
                public void onFinish() {
                    resetAlarmTrigger();
                }

            });
        } catch (Exception ex) {
            LOGE(TAG, "[handleMessage] Failed to upload image", ex);
            resetAlarmTrigger();
            // Show failure message if not in stealth mode??

        }
    }

    private void postUpload(final Message msg) {
        if (msg.getData().getBoolean("reset")) {
            triggerTimestamp = System.currentTimeMillis();
            currentNoOfTriggers = 0;
        }
        isUploadInProgress = false;
    }

    protected Handler processingHandler;

    private void fireProcessingAction(final ProcessingAction feature, final byte[] frame) {
        Message msg = processingHandler.obtainMessage();
        msg.what = feature.getHashCode();
        msg.obj = new Object[]{frame};
        msg.sendToTarget();
    }

    private void fireProcessingAction(final ProcessingAction feature, final Bundle bundle, final byte[] frame) {
        Message msg = processingHandler.obtainMessage();
        msg.what = feature.getHashCode();
        msg.obj = new Object[]{frame};
        msg.setData(bundle);
        msg.sendToTarget();
    }

    private void fireProcessingAction(final ProcessingAction feature, final Bundle bundle) {
        Message msg = processingHandler.obtainMessage();
        msg.what = feature.getHashCode();
        msg.setData(bundle);
        msg.sendToTarget();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        powerMgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //wakeLock = powerMgr.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, TAG + " WakeLock");
        wakeLock = powerMgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG + " WakeLock");
        wakeLock.acquire();
        setContentView(R.layout.activity_webcam);

        // Configure default settings
        AppController.getDefaultSettings().configure();

        HandlerThread processingThread = new HandlerThread("ProcessingHandler", android.os.Process.THREAD_PRIORITY_MORE_FAVORABLE);
        processingThread.start();
        processingHandler = new Handler(processingThread.getLooper()) {
            @Override
            public void handleMessage(final Message msg) {
                ProcessingAction feature = ProcessingAction.get(msg.what);
                if (feature != null) {
                    if (feature == ProcessingAction.PROCESS_FRAME) {
                        final Object[] args = (Object[]) msg.obj;
                        processFrame((byte[]) args[0]);
                    } else if (feature == ProcessingAction.PRE_UPLOAD) {
                        final Object[] args = (Object[]) msg.obj;
                        preUpload((byte[]) args[0]);
                    } else if (feature == ProcessingAction.REQUEST_UPLOAD_URL) {
                        final Object[] args = (Object[]) msg.obj;
                        requestCloudUploadUrl((byte[]) args[0]);
                    } else if (feature == ProcessingAction.UPLOAD_IMAGE) {
                        uploadCloudImage(msg);
                    } else if (feature == ProcessingAction.POST_UPLOAD) {
                        postUpload(msg);
                    }
                }
            }
        };
        cameraMenu = getResources().getStringArray(R.array.camera_video_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        menuAdapter = new CameraMenuAdapter(this);
        for (String menuItem : cameraMenu) {
            menuAdapter.add(new CameraMenuItem(menuItem));
        }
        drawerList.setAdapter(menuAdapter);
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        tvMessage1 = (TextView) findViewById(R.id.tv_message1);
        tvOption = (TextView) findViewById(R.id.tv_option);

        tvOption.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    drawerLayout.closeDrawer(Gravity.LEFT);
                } else {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            }
        });


        setupActivity();
        setupVideo();
        setupAudio();
        setupCamera();
        setupEventBus();

        appBroadcastReceiver = new AppBroadcastReceiver();
        isScreenLocked = false;
    }

    private void resetAlarmTrigger() {
        Bundle data = new Bundle();
        data.putBoolean("reset", true);
        fireProcessingAction(ProcessingAction.POST_UPLOAD, data);
    }

    private void setupActivity() {
        final AppPreference prefs = AppPreference.getInstance();
        streamingPort = Integer.valueOf(prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_VIDEO_STREAMING_PORT, getString(R.string.default_video_streaming_port)));
        motionDetectionRequired = prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_MOTION_DETECTION, Boolean.valueOf(getString(R.string.default_motion_detection)));
        jpegQuality = prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_VIDEO_STREAMING_IMAGE_QUALITY, Integer.valueOf(getString(R.string.default_video_streaming_image_quality)));
        motionDetectionThreshold = prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_MOTION_DETECTION_THRESHOLD, Integer.valueOf(getString(R.string.default_motion_detection_threshold)));
        faceDetectionSize = Float.valueOf(prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_FACE_DETECTION_SIZE, getString(R.string.default_face_detection_size)));
        motionDetectionType = prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_MOTION_DETECTION_TYPE, getString(R.string.default_motion_detection_type));
        motionDetectionAlgorithm = prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_MOTION_DETECTION_ALGORITHM, getString(R.string.default_motion_detection_algorithm));
        motionDetectionContourThickness = prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_MOTION_DETECTION_CONTOUR_THICKNESS, Integer.valueOf(getString(R.string.default_motion_detection_contour_thickness)));
        streamDetectedObject = prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_STREAM_DETECTED_OBJECT, Boolean.valueOf(getString(R.string.default_stream_detected_object)));
        disguiseCamera = prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_DISGUISE_CAMERA, Boolean.valueOf(getString(R.string.default_disguise_camera)));
        stealthMode = prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_STEALTH_MODE, Boolean.valueOf(getString(R.string.default_stealth_mode)));

        String primaryAddressFamily = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_PRIMARY_ADDRESS_FAMILY, getString(R.string.default_primary_address_family));
        AddressFamily addressFamily = AddressFamily.IPv4;
        if (!TextUtils.isEmpty(primaryAddressFamily)) {
            addressFamily = AddressFamily.get(Integer.valueOf(primaryAddressFamily));
        }
        if (addressFamily == AddressFamily.IPv4)
            isUseIPv4 = true;
        else
            isUseIPv4 = false;

        motionDetector = MotionDetectionType.getDetector(motionDetectionType, motionDetectionAlgorithm, faceDetectionSize, motionDetectionThreshold);
        motionDetector.setContourThickness(motionDetectionContourThickness);

        setControlPanelUrl();

        webView = (WebView) findViewById(R.id.disguise);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }

            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }
        });
        if (disguiseCamera) {
            webView.setVisibility(View.VISIBLE);
            setMessageVisibility(View.INVISIBLE);
        } else {
            webView.setVisibility(View.INVISIBLE);
            setMessageVisibility(View.VISIBLE);
        }
        if (stealthMode) {
            webView.setOnTouchListener(this);
        }
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl("http://www.google.com");

        // Get device information
        deviceName = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_DEVICE_UNIQUE_NAME, AppController.getDeviceName());
        deviceId = DeviceUtils.getDeviceId(this);
        emailAddress = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_DEVICE_EMAIL_ADDRESS, this.getString(R.string.default_device_email_address));
        //emailPassword = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_DEVICE_EMAIL_PASSWORD, this.getString(R.string.default_device_email_password));
        phoneNumber = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_DEVICE_PHONE_NUMBER, this.getString(R.string.default_device_phone_number));

        // Get detection settings
        notifyByEmail = prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_NOTIFY_BY_EMAIL, Boolean.valueOf(getString(R.string.default_notify_by_email)));
        notifyBySms = prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_NOTIFY_BY_SMS, Boolean.valueOf(getString(R.string.default_notify_by_sms)));
        notifyByGcm = prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_NOTIFY_BY_GCM, Boolean.valueOf(getString(R.string.default_notify_by_gcm)));
        alarmTriggerInterval = Integer.valueOf(prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_ALARM_TRIGGER_INTERVAL, getString(R.string.default_alarm_trigger_interval)));
        noOfTriggers = prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_ALARM_NO_OF_TRIGGERS, Integer.valueOf(getString(R.string.default_alarm_no_of_triggers)));
        localStorage = prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_ALARM_IMAGE_LOCAL_STORAGE, Boolean.valueOf(getString(R.string.default_alarm_image_local_storage)));
        cloudStorage = prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_ALARM_IMAGE_CLOUD_STORAGE, Boolean.valueOf(getString(R.string.default_alarm_image_cloud_storage)));
        emailStorage = prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_ALARM_IMAGE_EMAIL_STORAGE, Boolean.valueOf(getString(R.string.default_alarm_image_email_storage)));
        googleDriveStorage = prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_ALARM_IMAGE_DRIVE_STORAGE, Boolean.valueOf(getString(R.string.default_alarm_image_drive_storage)));
        alarmSoundType = prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_ALARM_SOUND_TYPE, "");
        motionDetectionRecordVideo = prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_MOTION_DETECTION_RECORD_VIDEO, Boolean.valueOf(getString(R.string.default_motion_detection_record_video)));
        motionDetectionRecordVideoDurationSeconds = Integer.parseInt(prefs.getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_MOTION_DETECTION_RECORD_VIDEO_DURATION_SECONDS, getString(R.string.default_motion_detection_record_video_duration_seconds)));

        urlRequestBlobUrl = AppController.getUploadBlobUrl();
        triggerTimestamp = System.currentTimeMillis();
    }


    private void release() {
        try {

            isStreamingAudio = false;
            isStreamingVideo = false;

            // Unregister event bus
            AppController.bus.unregister(this);

            stopVideoRecording();

            stopService();

            if (isAppReceiverRegistered) {
                unregisterReceiver(appBroadcastReceiver);
                isAppReceiverRegistered = false;
            }

            mediaPlayer.release();

            if (streamingServer != null) {
                streamingServer.stop();
                streamingServer = null;
            }

            for (DataStream audioStream : audioStreams) {
                audioStream.release();
            }
            audioRecorder.release();

            for (VideoDataStream videoStream : videoStreams) {
                videoStream.release();
            }

            webcamController.disconnectCamera();
            webcamController.releaseAll();

            AppController.setSurveillanceMode(false);
            AppController.setSurveillanceShutdown(true);

            processingHandler.getLooper().quit();

            releaseFrameBuffer();

            finish();

        } catch (Exception e) {
            LOGE(TAG, "[release] General exception", e);
        } finally {
            if (wakeLock.isHeld()) {
                try {
                    wakeLock.release();
                } catch (Throwable th) {
                    // ignoring this exception, probably wakeLock was already released
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AppConfig.SECURITY_REQ_ENTER_PATTERN: {
                isScreenLocked = false;
                switch (resultCode) {
                    case RESULT_OK:
                        // The user passed
                        break;
                    case RESULT_CANCELED:
                        // The user cancelled the task
                        lockScreen();
                        break;
                    case LockPatternActivity.RESULT_FAILED:
                        // The user failed to enter the pattern
                        break;
                    case LockPatternActivity.RESULT_FORGOT_PATTERN:
                        // The user forgot the pattern and invoked your recovery activity
                        break;
                }
                break;
            }
            case AppConfig.GOOGLE_REQUEST_CODE_RESOLUTION: {
                if (resultCode == RESULT_OK) {
                    setupGoogleDrive();
                }
                break;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        /*stopVideoRecording();

        stopService();

        if (isAppReceiverRegistered) {
            unregisterReceiver(appBroadcastReceiver);
            isAppReceiverRegistered = false;
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isAppReceiverRegistered) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(AppConfig.INTENT_SHUTDOWN_SURVEILLANCE_ACTION);
            filter.addAction(AppConfig.INTENT_IP_ADDRESS_CHANGE_ACTION);
            registerReceiver(appBroadcastReceiver, filter);
            isAppReceiverRegistered = true;
        }
        setupService();
        setupGoogleDrive();
        googleDriveCheck();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
    }

    @Override
    public boolean onTouch(View v, MotionEvent evt) {
        if (stealthMode) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        fadeScreen(false);
        return false;
    }

    private void setupVideo() {
        isStreamingVideo = false;
        if (videoStreams == null)
            videoStreams = new ArrayList<VideoDataStream>(1);
    }

    private void setupAudio() {

        mediaPlayer = new MediaPlayer();

        isStreamingAudio = false;
        mp3Encoder = new Mp3Encoder();

        bSamples = 16;
        nChannels = 1;
        framePeriod = AUDIO_FREQUENCY * AUDIO_TIMER_INTERVAL / 1000;
        audioBufferSize = framePeriod * 2 * bSamples * nChannels / 8;

        int minBufferSize = AudioRecord.getMinBufferSize(AUDIO_FREQUENCY, AUDIO_CHANNEL, AUDIO_ENCODING);
        if (audioBufferSize < minBufferSize) {
            // Check to make sure buffer size is not smaller than the smallest allowed one
            audioBufferSize = minBufferSize;

            // Set frame period and timer interval accordingly
            framePeriod = audioBufferSize / (2 * bSamples * nChannels / 8);
        }

        if (audioRecorder == null) {
            audioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC, AUDIO_FREQUENCY, AUDIO_CHANNEL, AUDIO_ENCODING, audioBufferSize);
        }

        if (audioStreams == null)
            audioStreams = new ArrayList<DataStream>(1);

        if (newAudioStreams == null)
            newAudioStreams = new ArrayList<DataStream>(1);

    }

    /**
     * Register the service bus.
     */
    private void setupEventBus() {
        AppController.bus.register(this);
    }

    /**
     * Set up the camera.
     */
    private void setupCamera() {
        final Webcam webcamView = (Webcam) findViewById(R.id.webcam);
        webcamView.setPreferences();
        final WebcamOverlay webcamOverlay = (WebcamOverlay) findViewById(R.id.webcam_overlay);
        webcamOverlay.setOnTouchListener(this);
        webcamView.setCvCameraViewListener(this);
        webcamView.setVisibility(SurfaceView.VISIBLE);
        webcamView.enableView();
        webcamController = new WebcamController(this, webcamView, webcamOverlay);
    }

    private void setupService() {
        if (!ServiceUtils.isServiceRunning(this, HttpdService.class)) {
            /*
            final AppPreference prefs = AppPreference.getInstance();
            int controlPanelPort = Integer.parseInt(prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_CONTROL_PANEL_PORT, this.getString(R.string.default_control_panel_http_port)));
            boolean loginRequired = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_CONTROL_PANEL_LOGIN_REQUIRED, Boolean.valueOf(this.getString(R.string.default_login_required)));
            String userName = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_CONTROL_PANEL_USER_NAME, this.getString(R.string.default_http_user_name));
            String userPassword = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_CONTROL_PANEL_USER_PASSWORD, this.getString(R.string.default_http_user_password));
            boolean disableNotification = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_CONTROL_PANEL_DISABLE_NOTIFICATION, Boolean.valueOf(this.getString(R.string.default_disable_notification)));
            String primaryAddressFamily = prefs.getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_PRIMARY_ADDRESS_FAMILY, this.getString(R.string.default_primary_address_family));
            ServiceUtils.startHttpdService(this, controlPanelPort, loginRequired, userName, userPassword, disableNotification, primaryAddressFamily);
            */
            ServiceUtils.startHttpdService(this);
            isServiceStartRequired = true;
        } else {
            isServiceStartRequired = false;
        }
    }

    private void stopService() {
        if (isServiceStartRequired) {
            ServiceUtils.stopHttpdService(this);
        }
    }

    private boolean setupStreamingServer() {
        if (streamingServer != null && streamingServer.isAlive())
            return true;
        String ipAddr = NetworkUtils.getLocalIpAddress(isUseIPv4);
        if (!TextUtils.isEmpty(ipAddr)) {
            try {
                streamingServer = new StreamingServer(null, streamingPort, this.getAssets());
                streamingServer.registerProcessor("/processor/query", queryProcessor);
                streamingServer.registerProcessor("/processor/setup", setupProcessor);
                streamingServer.registerProcessor("/processor/features", featuresProcessor);
                streamingServer.registerStreaming("/video_stream/live.jpg", captureProcessor);
                streamingServer.registerStreaming("/video/live.mjpg", videoProcessor);
                streamingServer.registerStreaming("/audio_stream/live.mp3", broadcastProcessor);
                streamingServer.start();
            } catch (IOException e) {
                streamingServer = null;
            }
        }
        setControlPanelUrl();
        if (streamingServer != null) {
            tvMessage1.setText(String.format(getString(R.string.msg_camera_caption), triggeredCount, controlPanelUrl));
            return true;
        } else {
            tvMessage1.setText(getString(R.string.msg_error));
            return false;
        }

    }

    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> queryProcessor = new Processor<Map<String, String>, Map<String, String>, Map<String, String>, String>() {

        @Override
        public String process(Map<String, String> headers, Map<String, String> params, Map<String, String> files) {

            final String type = params.get("type");
            WebcamQuery query = WebcamQuery.RESOLUTION;
            if (!TextUtils.isEmpty(type)) {
                query = WebcamQuery.get(type);
            }
            if (query == WebcamQuery.SCENE_MODE) {
                List<String> sceneModes = webcamController.getSceneModes();
                return new Gson().toJson(sceneModes);
            } else if (query == WebcamQuery.COLOR_EFFECT) {
                List<String> colorEffects = webcamController.getSupportedColorEffects();
                return new Gson().toJson(colorEffects);
            } else if (query == WebcamQuery.FLASH_MODE) {
                List<String> flashModes = webcamController.getSupportedFlashModes();
                return new Gson().toJson(flashModes);
            } else if (query == WebcamQuery.FOCUS_MODE) {
                List<String> focusModes = webcamController.getSupportedFocusModes();
                return new Gson().toJson(focusModes);
            } else if (query == WebcamQuery.WHITE_BALANCE) {
                List<String> whiteBalance = webcamController.getSupportedWhiteBalance();
                return new Gson().toJson(whiteBalance);
            } else if (query == WebcamQuery.ANTI_BANDING) {
                List<String> antibanding = webcamController.getSupportedAntibanding();
                return new Gson().toJson(antibanding);
            } else if (query == WebcamQuery.AUTO_EXPOSURE_LOCK) {
                boolean autoExposureLock = webcamController.getAutoExposureLock();
                return new Gson().toJson(autoExposureLock);
            } else if (query == WebcamQuery.EXPOSURE_COMPENSATION) {
                ExposureCompensation es = webcamController.getExposureCompensationSettings();
                return new Gson().toJson(es);
            } else if (query == WebcamQuery.IMAGE_QUALITY) {
                return new Gson().toJson(jpegQuality);
            } else if (query == WebcamQuery.MOTION_DETECTION) {
                return new Gson().toJson(motionDetectionRequired);
            } else if (query == WebcamQuery.MOTION_DETECTION_THRESHOLD) {
                return new Gson().toJson(motionDetectionThreshold);
            } else if (query == WebcamQuery.NIGHT_VISION) {
                return new Gson().toJson(isNightVisionStreaming);
            } else if (query == WebcamQuery.RECORD_VIDEO_STATUS) {
                if (webcamController.isRecording()) {
                    return new Gson().toJson(new RecordingStatus(TrueFalseEnum.TRUE.getHashCode(), webcamController.getRecordingFileName()));
                } else {
                    return new Gson().toJson(new RecordingStatus(TrueFalseEnum.FALSE.getHashCode(), ""));
                }
            } else {
                List<Resolution> resList = new ArrayList<Resolution>(5);
                try {
                    List<Camera.Size> supportSize = webcamController.getSupportedPreviewSizes();
                    resList.add(new Resolution(webcamController.getPreviewWidth(), webcamController.getPreviewHeight()));
                    for (int i = 0; i < supportSize.size(); i++) {
                        resList.add(new Resolution(supportSize.get(i).width, supportSize.get(i).height));
                    }
                } catch (Exception ex) {
                }
                return new Gson().toJson(resList);
            }
        }
    };

    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> setupProcessor = new Processor<Map<String, String>, Map<String, String>, Map<String, String>, String>() {

        @Override
        public String process(Map<String, String> headers, Map<String, String> params, Map<String, String> files) {
            final int width = Integer.parseInt(params.get("width"));
            final int height = Integer.parseInt(params.get("height"));

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    changeResolution(width, height);
                }
            });

            return new Gson().toJson(ActionStatus.OK);
        }
    };

    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> featuresProcessor = new Processor<Map<String, String>, Map<String, String>, Map<String, String>, String>() {

        @Override
        public String process(Map<String, String> headers, Map<String, String> params, Map<String, String> files) {
            String param = params.get("feature");
            String value = params.get("value");
            if (!TextUtils.isEmpty(param)) {
                Message msg = featureHandler.obtainMessage();
                msg.what = Integer.valueOf(param);
                Bundle bundle = new Bundle();
                bundle.putString("value", value);
                msg.setData(bundle);
                featureHandler.sendMessage(msg);
            }
            return new Gson().toJson(ActionStatus.OK);
        }
    };

    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, InputStream> broadcastProcessor = new Processor<Map<String, String>, Map<String, String>, Map<String, String>, InputStream>() {

        @Override
        public InputStream process(Map<String, String> headers, Map<String, String> params, Map<String, String> files) {
            final Random rnd = new Random();
            final DataStream audioStream = new DataStream("com.mymobkit." + Integer.toHexString(rnd.nextInt()));

            audioStream.prepare(128, 8192);
            InputStream is;
            try {
                is = audioStream.getInputStream();
            } catch (IOException e) {
                audioStream.release();
                return null;
            }

            newAudioStreams.add(audioStream);

            int state = audioRecorder.getState();
            if (state != AudioRecord.RECORDSTATE_RECORDING) {
                audioRecorder.startRecording();
            }

            if (!isStreamingAudio) {
                AudioStreaming streamingTask = new AudioStreaming();
                streamingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

            params.put("mime", "application/octet-stream");
            return is;
        }
    };

    private void googleDriveCheck() {

        if (driveFolderId != null) return;

        if (googleDriveStorage && AppController.googleApiClient != null && AppController.googleApiClient.isConnected()) {
            // Check if folder exists
            new AsyncTask<Void, Void, String>() {
                @Override
                public String doInBackground(Void... args) {
                    try {
                        checkGoogleDriveFolder(deviceId);
                    } catch (Exception e) {
                        LOGE(TAG, "[Google Drive] Error checking Google Drive", e);
                        return getString(R.string.msg_google_drive_folder_create_error);
                    }
                    return StringUtils.EMPTY;
                }

                @Override
                protected void onPostExecute(String result) {
                    if (!TextUtils.isEmpty(result)) {
                        ToastUtils.toastShort(WebcamActivity.this, result);
                    }
                }
            }.execute();
        }
    }

    private void checkGoogleDriveFolder(final String folderName) {
        try {
            final DriveFolder folder = Drive.DriveApi.getRootFolder(AppController.googleApiClient);
            List<Filter> filters = new ArrayList<Filter>();
            filters.add(Filters.eq(SearchableField.TRASHED, false));
            filters.add(Filters.eq(SearchableField.TITLE, folderName));
            Query query = new Query.Builder().addFilter(Filters.and(filters)).build();
            DriveApi.MetadataBufferResult result = (folder == null)
                    ? Drive.DriveApi.query(AppController.googleApiClient, query).await()
                    : folder.queryChildren(AppController.googleApiClient, query).await();
            boolean isFolderFound = false;
            if (result.getStatus().isSuccess()) {
                MetadataBuffer mdb = null;
                try {
                    mdb = result.getMetadataBuffer();
                    if (mdb != null) {
                        for (Metadata md : mdb) {
                            if (md == null) continue;
                            if (md.isFolder()) {
                                driveFolderId = md.getDriveId();
                                isFolderFound = true;
                                break;
                            }
                        }
                    }
                } finally {
                    if (mdb != null) mdb.release();
                }
            }
            if (!isFolderFound) {
                // Folder is not found, create the folder
                createGoogleDriveFolder(folderName);
            }
        } catch (Exception ex) {
            LOGE(TAG, "[checkGoogleDriveFolder] Failed to check folder", ex);
        }
    }

    private void createGoogleDriveFolder(final String folderName) {
        try {
            final MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(folderName).build();

            // Create the folder
            DriveFolder.DriveFolderResult result = Drive.DriveApi.getRootFolder(AppController.googleApiClient).createFolder(AppController.googleApiClient, changeSet).await();
            if (!result.getStatus().isSuccess()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.toastShort(WebcamActivity.this, R.string.msg_google_drive_folder_create_error);
                    }
                });
                return;
            }
            driveFolderId = result.getDriveFolder().getDriveId();
            LOGI(TAG, "[createGoogleDriveFolder] Created a folder: " + result.getDriveFolder().getDriveId());

        } catch (Exception ex) {
            LOGE(TAG, "[createGoogleDriveFolder] Failed to create folder", ex);
        }
    }

    private void saveFileToGoogleDrive(final String fileName, final byte[] fileData) {

        if (driveFolderId == null || AppController.googleApiClient == null || !AppController.googleApiClient.isConnected()) {
            LOGW(TAG, "[saveFileToGoogleDrive] Unable to save to Google Drive");
            setupGoogleDrive();
            googleDriveCheck();
            return;
        }

        final DriveApi.DriveContentsResult driveContentsResult = Drive.DriveApi.newDriveContents(AppController.googleApiClient).await();

        if (!driveContentsResult.getStatus().isSuccess()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.toastShort(WebcamActivity.this, R.string.msg_google_drive_folder_save_error);
                }
            });
            return;
        }

        // Perform I/O off the UI thread.
        new Thread() {
            @Override
            public void run() {
                // write content to DriveContents
                OutputStream outputStream = driveContentsResult.getDriveContents().getOutputStream();
                try {
                    outputStream.write(fileData);
                } catch (IOException e) {
                    LOGE(TAG, "[saveFileToGoogleDrive] Error writing to Google Drive", e);
                    return;
                }

                //DriveFolder folder = Drive.DriveApi.getFolder(AppController.googleApiClient, driveFolderId);
                DriveFolder folder = driveFolderId.asDriveFolder();
                MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                        .setTitle(fileName)
                        .setMimeType(MimeType.IMAGE_JPEG)
                        .setStarred(true).build();
                DriveFolder.DriveFileResult driveFileResult = folder.createFile(AppController.googleApiClient, changeSet, driveContentsResult.getDriveContents()).await();
                if (!driveFileResult.getStatus().isSuccess()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.toastShort(WebcamActivity.this, R.string.msg_google_drive_folder_save_error);

                        }
                    });
                }
            }
        }.start();
    }

    private class AudioStreaming extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... noArgs) {

            if (isStreamingAudio)
                return null;
            isStreamingAudio = true;

            byte[] audioData = new byte[framePeriod * bSamples / 8 * nChannels];
            int readSize = audioData.length;
            byte[] mp3Data = new byte[1024 * 8];

            mp3Encoder.nativeOpenEncoder();

            List<DataStream> invalidAudioStreams = new ArrayList<DataStream>(1);
            while (true) {
                if (!isStreamingAudio)
                    break;

                int len = audioRecorder.read(audioData, 0, readSize);
                if (len == AudioRecord.ERROR_INVALID_OPERATION || len == AudioRecord.ERROR_BAD_VALUE) {
                    continue;
                }
                len = mp3Encoder.nativeEncodingPCM(audioData, len, mp3Data);
                for (DataStream audioStream : audioStreams) {
                    try {
                        OutputStream os = audioStream.getOutputStream();
                        if (os != null)
                            os.write(mp3Data, 0, len);
                        os.flush();
                    } catch (IOException e) {
                        LOGE(TAG, "[run] Error writing stream", e);
                        invalidAudioStreams.add(audioStream);
                        break;
                    } catch (Exception e) {
                        LOGE(TAG, "[run] General exception", e);
                        invalidAudioStreams.add(audioStream);
                        break;
                    }
                }
                if (invalidAudioStreams.size() > 0) {
                    for (DataStream audioStream : invalidAudioStreams) {
                        audioStream.release();
                        audioStreams.remove(audioStream);
                    }
                    invalidAudioStreams.clear();
                }

                if (newAudioStreams.size() > 0) {
                    audioStreams.addAll(newAudioStreams);
                    newAudioStreams.clear();
                }
            }
            mp3Encoder.nativeCloseEncoder();
            isStreamingAudio = false;

            return null;
        }
    }

    private class AudioStreamingTask extends AsyncTask<DataStream, Void, Void> {
        @Override
        protected Void doInBackground(DataStream... audioStreams) {

            isStreamingAudio = true;

            byte[] audioData = new byte[framePeriod * bSamples / 8 * nChannels];
            int readSize = audioData.length;
            byte[] mp3Data = new byte[1024 * 8];

            while (true) {
                if (!isStreamingAudio)
                    break;

                final DataStream audioStream = audioStreams[0];
                int len = audioRecorder.read(audioData, 0, readSize);
                if (len == AudioRecord.ERROR_INVALID_OPERATION || len == AudioRecord.ERROR_BAD_VALUE) {
                    continue;
                }
                len = mp3Encoder.nativeEncodingPCM(audioData, len, mp3Data);
                try {
                    OutputStream os = audioStream.getOutputStream();
                    if (os != null)
                        os.write(mp3Data, 0, len);
                    os.flush();
                } catch (IOException e) {
                    LOGE(TAG, "[doInBackground] Error writing stream", e);
                    break;
                } catch (Exception e) {
                    LOGE(TAG, "[doInBackground] General exception", e);
                    break;
                }
            }
            return null;
        }
    }

    private class VideoStreamingTask extends AsyncTask<VideoDataStream, Void, Void> {
        @Override
        protected Void doInBackground(VideoDataStream... videoStreams) {
            isStreamingVideo = true;
            byte[] data = null;
            boolean isSent = false;
            while (true) {
                if (!isStreamingVideo)
                    break;

                final VideoDataStream videoStream = videoStreams[0];
                try {
                    final OutputStream os = videoStream.getOutputStream();
                    if (!streamDetectedObject) {
                        final VideoFrame frame = videoStream.getVideoFrame();
                        isSent = frame.isSent();
                        frame.setSent(true);
                        if (!isNightVisionStreaming)
                            data = frame.getPicture(rect, frameWidth, frameHeight, previewFormat, jpegQuality);
                        else
                            data = nightVision.process(frame.get());
                    } else {
                        final CvVideoFrame frame = videoStream.getCvVideoFrame();
                        isSent = frame.isSent();
                        frame.setSent(true);
                        if (!isNightVisionStreaming)
                            data = frame.getPicture(jpegQuality);
                        else
                            data = frame.getPicture(nightVision);
                    }
                    if (os != null && !isSent) {
                        if (data == null)
                            continue;
                        os.write(("Content-type: image/jpeg\r\n" + "Content-Length: " + data.length + "\r\n\r\n").getBytes());
                        os.write(data);
                        os.write(("\r\n--" + MimeType.MULTIPART_BOUNDARY + "\r\n").getBytes());
                        os.flush();
                    }
                } catch (IOException e) {
                    LOGE(TAG, "[doInBackground] Error writing stream", e);
                    break;
                } catch (Exception e) {
                    LOGE(TAG, "[doInBackground] General exception", e);
                    break;
                }
            }
            return null;
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        if (setupStreamingServer()) {
        }
        initializeFrameBuffer(width, height);
        AppController.setSurveillanceMode(true);
        AppController.setSurveillanceShutdown(false);
        startCameraWorker();
    }

    @Override
    public void onCameraViewStopped() {
        stopCameraWorker();
        releaseFrameBuffer();

        AppController.setSurveillanceMode(false);
        AppController.setSurveillanceShutdown(true);
    }

    private void changeResolution(final int width, final int height) {
        stopVideoRecording();
        webcamController.setupCamera(width, height);
    }

    private void initializeFrameBuffer(int width, int height) {
        frameWidth = width;
        frameHeight = height;

        previewFormat = webcamController.getPreviewFormat();
        rect = new Rect(0, 0, frameWidth, frameHeight);

        previewBufferSize = frameWidth * frameHeight;
        previewBufferSize = previewBufferSize * ImageFormat.getBitsPerPixel(previewFormat) / 8;
        byteBufferStore = new ByteBufferStore(previewBufferSize);
        cvFrameStore = new CvMatStore(frameWidth, frameHeight);
        yuvFrame = new Mat(frameHeight + (frameHeight / 2), frameWidth, CvType.CV_8UC1);

        // Set up video filter
        nightVision = new NightVisionFilter(jpegQuality, frameWidth, frameHeight);
        nightVision.configure();
    }

    private void releaseFrameBuffer() {
        byteBufferStore.release();
        cvFrameStore.release();
        nightVision.release();
    }

    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, InputStream> captureProcessor = new Processor<Map<String, String>, Map<String, String>, Map<String, String>, InputStream>() {

        @Override
        public InputStream process(Map<String, String> headers, Map<String, String> params, Map<String, String> files) {
            try {
                byte[] data = getCurrentSnapshot();
                if (data != null) {
                    InputStream is = new ByteArrayInputStream(data);
                    params.put("mime", MimeType.IMAGE_JPEG);
                    return is;
                }
            } catch (Exception e) {
                LOGE(TAG, "[process] General exception", e);
            }
            return null;
        }
    };

    private byte[] getCurrentSnapshot() {
        try {
            if (streamDetectedObject) {
                final Mat frame = cvFrameStore.current();
                cvFrameStore.moveToNext();
                if (!isNightVisionStreaming)
                    return CvUtils.toJpegByteArray(frame, jpegQuality);
                else
                    return nightVision.process(frame);
            } else {
                final byte[] frame = Arrays.copyOf(byteBufferStore.current().array(), byteBufferStore.current().array().length);
                if (!isNightVisionStreaming)
                    return ImageUtils.yuvToJpeg(frame, frameWidth, frameHeight, previewFormat, jpegQuality, rect);
                else
                    return nightVision.process(frame);
            }
        } catch (Exception ex) {
            LOGE(TAG, "[getCurrentSnapshot] Unable to get a snapshot", ex);
        }

        return null;
    }

    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, InputStream> videoProcessor = new Processor<Map<String, String>, Map<String, String>, Map<String, String>, InputStream>() {

        @Override
        public InputStream process(final Map<String, String> headers, final Map<String, String> params, final Map<String, String> files) {
            final Random rnd = new Random();
            final String addr = "com.mymobkit." + Integer.toHexString(rnd.nextInt());
            final VideoDataStream videoStream = new VideoDataStream(addr);

            videoStream.prepare(1024 * 4, 1024 * 8);
            InputStream is;
            try {
                is = videoStream.getInputStream();
                OutputStream os = videoStream.getOutputStream();
                os.write(("Server: myMobKit Server\r\n" + "Connection: close\r\n" + "Cache-Control: no-store, no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0\r\n"
                        + "Pragma: no-cache\r\n" + "Expires: -1\r\n" + "Access-Control-Allow-Origin: *\r\n" + "Content-Type: multipart/x-mixed-replace; " + "boundary=" + MimeType.MULTIPART_BOUNDARY
                        + "\r\n" + "\r\n" + "--" + MimeType.MULTIPART_BOUNDARY + "\r\n").getBytes());
                //os.flush();
            } catch (IOException e) {
                LOGE(TAG, "[process] Unable to serve video stream", e);
                videoStream.release();
                return null;
            }
            videoStreams.add(videoStream);

            // Create new async task to handle the video stream
            Message msg = featureHandler.obtainMessage();
            msg.what = WebcamFeature.VIDEO_STREAMING.getHashCode();
            Bundle bundle = new Bundle();
            bundle.putString("value", StringUtils.EMPTY);
            msg.setData(bundle);
            featureHandler.sendMessage(msg);
            return is;
        }
    };

    @Override
    public void onCameraFrame(byte[] frame) {
        try {
            synchronized (this) {
                byteBufferStore.assign(frame);
                this.notify();
            }
        } catch (Exception ex) {
            LOGE(TAG, "[onCameraFrame] Problem capturing frame", ex);
        }

    }

    private void checkMotionDetection(final byte[] frame, final boolean isMotionDetected) {
        if ((isMotionDetected) && !isUploadInProgress) {
            long currentTimeStamp = System.currentTimeMillis();
            int elapsedIntervalSeconds = (int) (currentTimeStamp - triggerTimestamp) / 1000;
            if (elapsedIntervalSeconds > alarmTriggerInterval && ++currentNoOfTriggers >= noOfTriggers) {
                isUploadInProgress = true;
                fireProcessingAction(ProcessingAction.PRE_UPLOAD, frame);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvMessage1.setText(String.format(getString(R.string.msg_camera_caption), ++triggeredCount, controlPanelUrl));
                    }
                });
            }
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CameraMenuItem item = (CameraMenuItem) parent.getItemAtPosition(position);
            selectItem(position, item);
        }
    }

    private void setMessageVisibility(final int visibility) {
        tvMessage1.setVisibility(visibility);
        tvOption.setVisibility(visibility);
        if (visibility == View.VISIBLE) {
            menuAdapter.getItem(0).setLabel(this.getString(R.string.camera_url_hide));
        } else {
            menuAdapter.getItem(0).setLabel(this.getString(R.string.camera_url_show));
        }
    }

    private void toggleMessageVisibility(CameraMenuItem item) {
        // Toggle URL display
        int visibility = tvMessage1.getVisibility();
        if (visibility == View.VISIBLE) {
            item.setLabel(this.getString(R.string.camera_url_show));
            tvMessage1.setVisibility(View.INVISIBLE);
            tvOption.setVisibility(View.INVISIBLE);
        } else {
            item.setLabel(this.getString(R.string.camera_url_hide));
            tvMessage1.setVisibility(View.VISIBLE);
            tvOption.setVisibility(View.VISIBLE);
        }
    }

    private void selectItem(int position, CameraMenuItem item) {

        switch (position) {
            case 0:
                toggleMessageVisibility(item);
                break;
            case 1:
                // Toggle camera
                toggleCamera(item);
                break;
            case 2:
                // Resolutions
                configureResolution();
                break;
            case 3:
                // Fade
                fadeScreen(true);
                break;
            case 4:
                // Lock
                lockScreen();
                break;
            case 5:
            case 6:
                onBackPressed();
                break;
        }

        // update selected item and title, then close the drawer
        drawerList.setItemChecked(position, true);
        drawerLayout.closeDrawer(drawerList);
    }

    private void lockScreen() {
        if (isScreenLocked)
            return;
        String savedPattern = AppController.getLockPattern();
        if (TextUtils.isEmpty(savedPattern)) {
            ToastUtils.toastShort(this, getString(R.string.msg_lock_pattern_not_configured));
            return;
        }
        Intent intent = new Intent(LockPatternActivity.ACTION_COMPARE_PATTERN, null, this, LockPatternActivity.class);
        intent.putExtra(LockPatternActivity.EXTRA_PATTERN, savedPattern.toCharArray());
        //intent.putExtra(LockPatternActivity.ACTION_CANCEL_BACK, true);
        AlpSettings.Display.setMaxRetries(this, 10000); // Lock forever
        isScreenLocked = true;
        startActivityForResult(intent, AppConfig.SECURITY_REQ_ENTER_PATTERN);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    private void fadeScreen(boolean on) {
        if (on) {
            if (!isScreenFaded) {
                Window win = getWindow();
                win.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                win.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                isScreenFaded = true;
            }
        } else {
            if (isScreenFaded) {
                Window win = getWindow();
                win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                win.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                isScreenFaded = false;
            }
        }
    }

    private void toggleCamera(final CameraMenuItem item) {
        if (Camera.getNumberOfCameras() == 1) {
            return;
        }
        final String label = item.getLabel();
        final boolean isFrontCamera = label.equalsIgnoreCase(getString(R.string.camera_front));
        if (isFrontCamera) {
            webcamController.setCamera(CameraViewAdapter.CAMERA_ID_FRONT);
            item.setLabel(getString(R.string.camera_back));
        } else {
            webcamController.setCamera(CameraViewAdapter.CAMERA_ID_BACK);
            item.setLabel(getString(R.string.camera_front));
        }
    }


    private void toggleCameraLed(final boolean onOff) {
        webcamController.setFlashMode(onOff);
    }

    private void configureResolution() {
        final List<Size> resolutions = webcamController.getSupportedPreviewSizes();
        String[] possibleSizes = new String[resolutions.size()];
        int idx = 0;
        Size currentResolution = webcamController.getPreviewSize();
        for (int i = 0; i < resolutions.size(); i++) {
            Size s = resolutions.get(i);
            possibleSizes[i] = s.width + " x " + s.height;
            if (s.height == currentResolution.height && s.width == currentResolution.width)
                idx = i;
        }
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setSingleChoiceItems(possibleSizes, idx, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                chosenResolution = item;
            }
        });

        alert.setIcon(R.drawable.ic_launcher);
        alert.setTitle(R.string.label_title_resolution);

        alert.setPositiveButton(R.string.label_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Size chosenSize = resolutions.get(chosenResolution);
                changeResolution(chosenSize.width, chosenSize.height);
            }
        });
        alert.setNegativeButton(R.string.label_dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    private void setControlPanelUrl() {
        final String port = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_CONTROL_PANEL_PORT, this.getString(R.string.default_control_panel_http_port));
        controlPanelUrl = "http://" + NetworkUtils.getLocalIpAddress(isUseIPv4) + ":" + port;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed(); // July 14th
        Intent intent = new Intent(WebcamActivity.this, ControlPanelActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    /**
     * Camera menu adapter.
     */
    public class CameraMenuAdapter extends ArrayAdapter<CameraMenuItem> {

        public CameraMenuAdapter(Context context) {
            super(context, 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.drawer_list_item, null);
            TextView title = (TextView) convertView.findViewById(R.id.menu_label);
            CameraMenuItem item = getItem(position);
            title.setText(item.getLabel());
            return convertView;
        }
    }

    private void startCameraWorker() {
        LOGD(TAG, "[startCameraWorker] Starting camera worker processing thread");
        stopCameraWorkerThread = false;
        cameraWorkerThread = new Thread(new CameraWorker());
        cameraWorkerThread.start();
    }

    private void stopCameraWorker() {
        try {
            stopCameraWorkerThread = true;
            LOGD(TAG, "[stopCameraWorker] Notify thread");
            synchronized (this) {
                this.notify();
            }
            Log.d(TAG, "Waiting for thread");
            if (cameraWorkerThread != null)
                cameraWorkerThread.join();
        } catch (InterruptedException e) {
            LOGE(TAG, "[stopCameraWorker] Thread interrupted exception", e);
        } finally {
            cameraWorkerThread = null;
        }
    }

    /**
     * Camera worker.
     */
    private class CameraWorker implements Runnable {

        public void run() {
            do {
                synchronized (WebcamActivity.this) {
                    try {
                        WebcamActivity.this.wait();
                    } catch (InterruptedException e) {
                        LOGE(TAG, "[CameraWorker] Thread exception", e);
                    }
                }

                if (!stopCameraWorkerThread) {
                    ByteBuffer frame = byteBufferStore.current();
                    if (frame != null) {
                        deliverFrame(frame);
                    }
                    byteBufferStore.moveToNext();
                }
            } while (!stopCameraWorkerThread);
        }
    }

    private void deliverFrame(final ByteBuffer frame) {
        if (frame != null) {
            byte[] frameBytes = Arrays.copyOf(frame.array(), frame.array().length);
            appendVideoFrame(frameBytes);
            if (!frameProcessing && !isUploadInProgress) {
                frameProcessing = true;
                fireProcessingAction(ProcessingAction.PROCESS_FRAME, frameBytes);
            }
        }
    }

    private void appendVideoFrame(final byte[] frame) {
        try {
            for (VideoDataStream stream : videoStreams) {
                stream.setVideoFrame(frame);
            }
        } catch (Exception ex) {
            LOGE(TAG, "[appendVideoFrame] Error appending frame", ex);
        }
    }

    private void appendCvVideoFrame(final Mat frame) {
        try {
            for (VideoDataStream stream : videoStreams) {
                stream.setCvVideoFrame(frame);
                if (!streamDetectedObject) {
                    stream.getCvVideoFrame();
                }
            }
        } catch (Exception ex) {
            LOGE(TAG, "[appendCvVideoFrame] Error appending frame", ex);
        }
    }

    private void playSound(Context context, String uriString) {
        if (TextUtils.isEmpty(uriString))
            return;
        try {
            Uri uri = Uri.parse(uriString);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(context, uri);
            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (IOException e) {
            LOGE(TAG, "[playSound] Error playing device sound", e);
        }
    }


    /**
     * Called when {@code mGoogleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        LOGI(TAG, "GoogleApiClient connected");
        googleDriveCheck();
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
                GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            } catch (Exception ex) {
                LOGE(TAG, "[onConnectionFailed] Error showing error dialog", ex);
            }
            return;
        }
        try {
            result.startResolutionForResult(this, AppConfig.GOOGLE_REQUEST_CODE_RESOLUTION);
        } catch (Exception e) {
            LOGE(TAG, "[onConnectionFailed] Exception while starting resolution activity", e);
        }
    }

    public void setupGoogleDrive() {

        if (AppController.googleApiClient != null && (AppController.googleApiClient.isConnected() || AppController.googleApiClient.isConnecting())) {
            return;
        }

        // Check if Google Drive integration is enabled
        final String gmail = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_DEVICE_EMAIL_ADDRESS, this.getString(R.string.default_device_email_address));
        final boolean isGoogleDriveEnabled = AppPreference.getInstance().getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_ALARM_IMAGE_CLOUD_STORAGE, Boolean.valueOf(this.getString(R.string.default_alarm_image_drive_storage)));

        if (isGoogleDriveEnabled && !TextUtils.isEmpty(gmail)) {
            if (AppController.googleApiClient == null) {
                AppController.googleApiClient = new GoogleApiClient.Builder(this)
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

    /*@Subscribe
    public void onSendStatus(final SendStatus sendStatus) {
        LOGI(TAG, "[onSendStatus] Message sending status is " + sendStatus.getResponseCode());
    }*/

    @Subscribe
    public void onGcmCommand(final GcmMessage message) {
        LOGI(TAG, "[onGcmCommand] Received GCM command");
        if (message instanceof SurveillanceMessage) {
            // Stop surveillance
            //onBackPressed();
            finish();
        } else if (message instanceof SwitchCameraMessage) {
            SwitchCameraMessage switchCameraMessage = (SwitchCameraMessage) message;
            GcmMessage.ActionCommand command = GcmMessage.ActionCommand.get(switchCameraMessage.getActionCommand());
            final int cameraIndex = webcamController.getCameraIndex();

            if (command == GcmMessage.ActionCommand.FRONT_CAMERA) {

                if (cameraIndex != CameraViewAdapter.CAMERA_ID_FRONT) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            webcamController.setCamera(CameraViewAdapter.CAMERA_ID_FRONT);
                        }
                    });
                }

            } else if (command == GcmMessage.ActionCommand.REAR_CAMERA) {

                if (cameraIndex != CameraViewAdapter.CAMERA_ID_BACK) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            webcamController.setCamera(CameraViewAdapter.CAMERA_ID_BACK);
                        }
                    });
                }
            }
        }
    }
}
