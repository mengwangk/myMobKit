package com.mymobkit.service.webcam;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.mymobkit.R;
import com.mymobkit.common.PlatformUtils;
import com.mymobkit.common.ToastUtils;
import com.mymobkit.gcm.GcmMessage;
import com.mymobkit.gcm.message.SurveillanceMessage;
import com.mymobkit.gcm.message.SwitchCameraMessage;
import com.mymobkit.model.PreferenceChangedEvent;
import com.mymobkit.ui.activity.ControlPanelActivity;
import com.mymobkit.webcam.CameraViewAdapter;
import com.squareup.otto.Subscribe;

import static com.mymobkit.common.LogUtils.LOGI;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Surveillance camera view.
 */
public class WebcamWrapper extends CameraBase {

    private static final String TAG = makeLogTag(WebcamWrapper.class);

    private WindowManager.LayoutParams layoutParams;
    private WindowManager windowManager;
    private LayoutInflater inflater;
    private SensorService sensorService;
    private volatile boolean isVisible;

    /**
     * Constructor.
     *
     * @param context
     */
    public WebcamWrapper(final Context context) {

        this.context = context;

        // Prepare the camera
        prepareCamera();

        // Initialize the camera
        initializeCamera();
    }

    private void prepareCamera() {
        this.sensorService = new SensorService(getContext());
        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.view = inflater.inflate(R.layout.activity_webcam_service, null);

        this.view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    startControlPanelActivity = true;
                    hide();
                }
                // Consuming the event
                return true;
            }
        });
    }

    /**
     * Create the Windows parameters.
     *
     * @return Layout parameters.
     */
    private WindowManager.LayoutParams createWindowParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.flags =
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                        | WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        //| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        & ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        if (PlatformUtils.isJellyBeanOrHigher()) {
            params.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN; //| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        params.format = PixelFormat.TRANSLUCENT;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        return params;
    }

    public void start() {
        this.sensorService.start();
        this.layoutParams = createWindowParams();
        this.windowManager.addView(this.view, layoutParams);
        this.isVisible = true;
    }

    public void stop() {
        this.sensorService.stop();
        this.windowManager.removeView(view);
        shutdownCamera();
        this.isVisible = false;
    }


    /**
     * Run the service in background.
     */
    public void hide() {
        if (!isVisible) return;

        ToastUtils.toastLong(getContext(), getContext().getString(R.string.msg_camera_in_background));

        if (startControlPanelActivity) {
            Intent intent = new Intent(getContext(), ControlPanelActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
            startControlPanelActivity = false;
        }

        // Hide the camera service
        setInvisible();
        this.layoutParams.flags = this.layoutParams.flags
                & ~WindowManager.LayoutParams.FLAG_FULLSCREEN
                & ~WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        if (PlatformUtils.isJellyBeanOrHigher()) {
            this.layoutParams.systemUiVisibility = 0;
        }
        this.layoutParams.width = 0;
        this.layoutParams.height = 0;
        this.layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
        this.layoutParams.format = PixelFormat.TRANSPARENT;
        this.windowManager.updateViewLayout(this.view, this.layoutParams);
        this.isVisible = false;
    }

    /**
     * Show the service GUI.
     */
    public void show() {
        if (isVisible) return;

        setVisible();
        this.layoutParams.flags = WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                | WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                //| WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                & ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        if (PlatformUtils.isJellyBeanOrHigher()) {
            this.layoutParams.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN; //| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        }
        this.layoutParams.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;   // Always in landscape mode
        this.layoutParams.format = PixelFormat.TRANSLUCENT;
        this.windowManager.updateViewLayout(this.view, this.layoutParams);

        this.isVisible = true;
    }

    public boolean isVisible() {
        return isVisible;
    }

    @Subscribe
    public void onServiceCommand(final GcmMessage message) {
        LOGI(TAG, "[onGcmCommand] Received GCM command");
        if (message instanceof SurveillanceMessage) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startControlPanelActivity = false;
                    shutdown();
                }
            });
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

    @Subscribe
    public void onPreferenceChanged(final PreferenceChangedEvent event) {
        loadPreferences();
    }

}
