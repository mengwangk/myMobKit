package com.mymobkit.camera;

import android.graphics.SurfaceTexture;
import android.view.SurfaceHolder;

import java.util.List;

import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * CameraController is an abstract class that wraps up the access/control to
 * the Android camera, so that the rest of the application doesn't have to
 * deal directly with the Android camera API. It also allows us to support
 * more than one camera API through the same API (this is used to support both
 * the original camera API, and Android 5's Camera2 API).
 * The class is fairly low level wrapper about the APIs - there is some
 * additional logical/workarounds where such things are API-specific, but
 * otherwise the calling application still controls the behaviour of the
 * camera.
 */
public abstract class CameraController {

    private static final String TAG = makeLogTag(CameraController.class);


    public static CameraController getController(final int cameraId) throws CameraControllerException {
        return new Camera1Controller(cameraId);
    }

    protected int cameraId = 0;

    public static interface PictureCallback {
        public abstract void onPictureTaken(byte[] data);
    }

    public static interface AutoFocusCallback {
        public abstract void onAutoFocus(boolean success);
    }

    public static interface ErrorCallback {
        public abstract void onError();
    }

    public abstract void release();

    public CameraController(int cameraId) {
        this.cameraId = cameraId;
    }

    public int getCameraId() {
        return cameraId;
    }

    public abstract void setPreviewDisplay(SurfaceHolder holder) throws CameraControllerException;

    public abstract void setPreviewTexture(SurfaceTexture texture) throws CameraControllerException;

    public abstract void startPreview() throws CameraControllerException;

    public abstract void stopPreview();

    public abstract void setFlashMode(String mode);

    public abstract void setDisplayOrientation(int degrees);

    public abstract List<CameraSize> getSupportedPreviewSizes();

    public abstract List<CameraSize> getSupportedPictureSizes();

    public abstract void setPreviewSize(int width, int height);

    public abstract void setPictureSize(int width, int height);

    public abstract void setFocusMode(String mode);

    public abstract byte[] takePictureSync(boolean silent);

    public abstract void autoFocus(final AutoFocusCallback callback);

    public abstract void cancelAutoFocus();
}
