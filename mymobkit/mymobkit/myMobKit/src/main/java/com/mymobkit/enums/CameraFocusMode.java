package com.mymobkit.enums;


import android.hardware.Camera;

/**
 * Camera possible focus modes
 */

public enum CameraFocusMode implements MyMobKitEnumAsInt {

    FOCUS_MODE_AUTO(0),
    FOCUS_MODE_CONTINUOUS_PICTURE(1),
    FOCUS_MODE_CONTINUOUS_VIDEO(2),
    FOCUS_MODE_EDOF(3),
    FOCUS_MODE_FIXED(4),
    FOCUS_MODE_INFINITY(5),
    FOCUS_MODE_MACRO(6);

    private int hashCode;

    CameraFocusMode(int hashCode) {
        this.hashCode = hashCode;
    }

    public int getHashCode() {
        return this.hashCode;
    }

    public static CameraFocusMode get(final int hashCode) {
        if (FOCUS_MODE_AUTO.getHashCode() == hashCode) {
            return FOCUS_MODE_AUTO;
        } else if (FOCUS_MODE_CONTINUOUS_PICTURE.getHashCode() == hashCode) {
            return FOCUS_MODE_CONTINUOUS_PICTURE;
        } else if (FOCUS_MODE_CONTINUOUS_VIDEO.getHashCode() == hashCode) {
            return FOCUS_MODE_CONTINUOUS_VIDEO;
        } else if (FOCUS_MODE_EDOF.getHashCode() == hashCode) {
            return FOCUS_MODE_EDOF;
        } else if (FOCUS_MODE_FIXED.getHashCode() == hashCode) {
            return FOCUS_MODE_FIXED;
        } else if (FOCUS_MODE_INFINITY.getHashCode() == hashCode) {
            return FOCUS_MODE_INFINITY;
        } else if (FOCUS_MODE_MACRO.getHashCode() == hashCode) {
            return FOCUS_MODE_MACRO;
        } else {
            return FOCUS_MODE_AUTO;
        }
    }

    public static String getValue(final CameraFocusMode mode){
        if (FOCUS_MODE_AUTO == mode) {
            return Camera.Parameters.FOCUS_MODE_AUTO;
        } else if (FOCUS_MODE_CONTINUOUS_PICTURE == mode) {
            return Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE;
        } else if (FOCUS_MODE_CONTINUOUS_VIDEO == mode) {
            return Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO;
        } else if (FOCUS_MODE_EDOF == mode) {
            return Camera.Parameters.FOCUS_MODE_EDOF;
        } else if (FOCUS_MODE_FIXED == mode) {
            return Camera.Parameters.FOCUS_MODE_FIXED;
        } else if (FOCUS_MODE_INFINITY == mode) {
            return Camera.Parameters.FOCUS_MODE_INFINITY;
        } else if (FOCUS_MODE_MACRO == mode) {
            return Camera.Parameters.FOCUS_MODE_MACRO;
        } else {
            return Camera.Parameters.FOCUS_MODE_AUTO;
        }
    }
}

