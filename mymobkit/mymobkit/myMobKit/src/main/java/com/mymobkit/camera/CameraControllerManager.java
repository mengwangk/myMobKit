package com.mymobkit.camera;

/**
 * Provides additional support related to the Android camera APIs. This is to
 * support functionality that doesn't require a camera to have been opened.
 */
public abstract class CameraControllerManager {

    public static final int DEFAULT_CAMERA_ID = 0;

    public abstract int getNumberOfCameras();

    public abstract boolean isFrontFacing(int cameraId);


    public static CameraControllerManager getManager() {
        // TODO
        return new CameraControllerManager1();
    }

}

