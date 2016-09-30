package com.mymobkit.webcam;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;

import com.mymobkit.opencv.image.NightVisionFilter;

import java.util.List;

import static com.mymobkit.common.LogUtils.LOGD;
import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.LOGI;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * This class is an implementation of the Bridge View between OpenCV and Java Camera.
 * This class relays on the functionality available in base class and only implements required functions:
 *
 * connectCamera - opens Java camera and sets the PreviewCallback to be delivered.
 * disconnectCamera - closes the camera and stops preview.
 *
 * When frame is delivered via callback from Camera - it processed via OpenCV to be converted
 * to RGBA32 and then passed to the external callback for modifications if required.
 */
@SuppressWarnings("deprecation")
public class CameraView extends CameraViewAdapter implements PreviewCallback {

    private static final String TAG = makeLogTag(CameraView.class);
    private byte mBuffer[];
    protected Camera mCamera;

    // Night vision - TODO
    private static final int MAGIC_TEXTURE_ID = 10;
    private SurfaceTexture mSurfaceTexture;
    protected NightVisionFilter nightVision;
    protected boolean isNightVision;

    public static class CameraSizeAccessor implements ListItemAccessor {

        public int getWidth(Object obj) {
            Camera.Size size = (Camera.Size) obj;
            return size.width;
        }

        public int getHeight(Object obj) {
            Camera.Size size = (Camera.Size) obj;
            return size.height;
        }
    }

    public CameraView(Context context, int cameraId) {
        super(context, cameraId);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public boolean isNightVision() {
        return isNightVision;
    }

    public void setNightVision(boolean nightVision) {
        isNightVision = nightVision;
    }

    protected boolean initializeCamera(int width, int height) {
        LOGD(TAG, "Initialize camera");
        if (mMaxHeight != MAX_UNSPECIFIED)
            height = mMaxHeight;
        if (mMaxWidth != MAX_UNSPECIFIED)
            width = mMaxWidth;

        boolean result = true;
        synchronized (this) {
            mCamera = null;
            if (mCameraIndex == CAMERA_ID_ANY) {
                LOGD(TAG, "Trying to open camera with old open()");
                try {
                    mCamera = Camera.open();
                } catch (Exception e) {
                    LOGE(TAG, "Camera is not available (in use or does not exist): " + e.getLocalizedMessage(), e);
                }

                if (mCamera == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    boolean connected = false;
                    for (int camIdx = 0; camIdx < Camera.getNumberOfCameras(); ++camIdx) {
                        LOGD(TAG, "Trying to open camera with new open(" + Integer.valueOf(camIdx) + ")");
                        try {
                            mCamera = Camera.open(camIdx);
                            connected = true;
                        } catch (RuntimeException e) {
                            LOGE(TAG, "Camera #" + camIdx + "failed to open: " + e.getLocalizedMessage(), e);
                        }
                        if (connected)
                            break;
                    }
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    int localCameraIndex = mCameraIndex;
                    if (mCameraIndex == CAMERA_ID_BACK) {
                        LOGI(TAG, "Trying to open back camera");
                        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                        for (int camIdx = 0; camIdx < Camera.getNumberOfCameras(); ++camIdx) {
                            Camera.getCameraInfo(camIdx, cameraInfo);
                            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                                localCameraIndex = camIdx;
                                break;
                            }
                        }
                    } else if (mCameraIndex == CAMERA_ID_FRONT) {
                        LOGI(TAG, "Trying to open front camera");
                        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                        for (int camIdx = 0; camIdx < Camera.getNumberOfCameras(); ++camIdx) {
                            Camera.getCameraInfo(camIdx, cameraInfo);
                            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                                localCameraIndex = camIdx;
                                break;
                            }
                        }
                    }
                    if (localCameraIndex == CAMERA_ID_BACK) {
                        LOGE(TAG, "Back camera not found!");
                    } else if (localCameraIndex == CAMERA_ID_FRONT) {
                        LOGE(TAG, "Front camera not found!");
                    } else {
                        LOGD(TAG, "Trying to open camera with new open(" + Integer.valueOf(localCameraIndex) + ")");
                        try {
                            mCamera = Camera.open(localCameraIndex);
                        } catch (RuntimeException e) {
                            LOGE(TAG, "Camera #" + localCameraIndex + "failed to open: " + e.getLocalizedMessage(), e);
                        }
                    }
                }
            }

            if (mCamera == null)
                return false;

			/* Now set camera parameters */
            try {
                Camera.Parameters params = mCamera.getParameters();
                LOGD(TAG, "getSupportedPreviewSizes()");
                List<android.hardware.Camera.Size> sizes = params.getSupportedPreviewSizes();

                if (sizes != null) {
                    /* Select the size that fits surface considering maximum size allowed */
                    Size frameSize = calculateCameraFrameSize(sizes, new CameraSizeAccessor(), width, height);

                    //List<Integer> previewFormats = params.getSupportedPreviewFormats();
                    //LOGD(TAG, "Preview format " + previewFormats.size());

                    params.setPreviewFormat(ImageFormat.NV21);
                    LOGD(TAG, "Set preview size to " + Integer.valueOf((int) frameSize.width) + "x" + Integer.valueOf((int) frameSize.height));
                    params.setPreviewSize((int) frameSize.width, (int) frameSize.height);

                    // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                    // params.setRecordingHint(true);

                    // List<String> focusModes = params.getSupportedFocusModes();
                    // if (focusModes != null && focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    // params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                    // }

                    mCamera.setParameters(params);
                    params = mCamera.getParameters();

                    mFrameWidth = params.getPreviewSize().width;
                    mFrameHeight = params.getPreviewSize().height;

                    /* Commented - August 18 2015
                    LayoutParams layoutParams = getLayoutParams();
                    layoutParams.width = mFrameWidth;
                    layoutParams.height = mFrameHeight;
                    setLayoutParams(layoutParams);
                    */

                    if ((getLayoutParams().width == LayoutParams.MATCH_PARENT) && (getLayoutParams().height == LayoutParams.MATCH_PARENT))
                        mScale = Math.min(((float) height) / mFrameHeight, ((float) width) / mFrameWidth);
                    else
                        mScale = 0;

                    int size = mFrameWidth * mFrameHeight;
                    size = size * ImageFormat.getBitsPerPixel(params.getPreviewFormat()) / 8;
                    mBuffer = new byte[size];

                    mCamera.addCallbackBuffer(mBuffer);
                    mCamera.setPreviewCallbackWithBuffer(this);
                    mCamera.setPreviewDisplay(getHolder());
                    mCamera.startPreview();

                } else
                    result = false;
            } catch (Exception e) {
                result = false;
                LOGE(TAG, "Camera initialization error", e);
            }
        }

        return result;
    }

    protected void releaseCamera() {
        synchronized (this) {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.setPreviewCallback(null);
                mCamera.setPreviewCallbackWithBuffer(null);

                mCamera.release();
            }
            mCamera = null;
        }

        //unExpectedTerminationHelper.finish();
    }

    @Override
    protected boolean connectCamera(int width, int height) {
        LOGD(TAG, "Connecting to camera");
        if (!initializeCamera(width, height))
            return false;

        //unExpectedTerminationHelper.init();

        return true;
    }

    protected void disconnectCamera() {
        releaseCamera();
    }

    public void onPreviewFrame(byte[] frame, Camera camera) {
        if (mListener != null) {
            mListener.onCameraFrame(frame);
        }
        if (mCamera != null)
            mCamera.addCallbackBuffer(mBuffer);
    }

    protected void previewCallback() {
        try {
            if (mCamera != null) {
                Camera.Parameters params = mCamera.getParameters();
                params.setPreviewSize(mFrameWidth, mFrameHeight);
                //params.setPictureSize(mFrameWidth, mFrameHeight);
                mCamera.setParameters(params);
                mCamera.addCallbackBuffer(mBuffer);
                mCamera.setPreviewCallbackWithBuffer(this);
                mCamera.setPreviewDisplay(getHolder());
                mCamera.startPreview();
            }
        } catch (Exception e) {
            LOGE(TAG, "[previewCallback] Camera initialization error", e);
        }
    }

	/*
	private UnexpectedTerminationHelper unExpectedTerminationHelper = new UnexpectedTerminationHelper();

	private class UnexpectedTerminationHelper {
		private Thread mainThread;
		private Thread.UncaughtExceptionHandler oldUncaughtExceptionHandler = null;
		private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable ex) { // gets called on the same (main) thread

				// Close the camera
				releaseCamera();

				if (oldUncaughtExceptionHandler != null) {
					// it displays the "force close" dialog
					oldUncaughtExceptionHandler.uncaughtException(thread, ex);
				}
			}
		};

		void init() {
			mainThread = Thread.currentThread();
			oldUncaughtExceptionHandler = mainThread.getUncaughtExceptionHandler();
			mainThread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
		}

		void finish() {
			mainThread.setUncaughtExceptionHandler(oldUncaughtExceptionHandler);
			oldUncaughtExceptionHandler = null;
			mainThread = null;
		}
	}
	*/
}
