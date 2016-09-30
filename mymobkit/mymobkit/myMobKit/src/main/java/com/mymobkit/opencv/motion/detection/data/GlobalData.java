package com.mymobkit.opencv.motion.detection.data;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * This class is used to store global data.
 * 
 */
public abstract class GlobalData {

    private GlobalData() {
    }

    private static final AtomicBoolean phoneInMotion = new AtomicBoolean(false);

    public static boolean isPhoneInMotion() {
        return phoneInMotion.get();
    }

    public static void setPhoneInMotion(boolean bool) {
        phoneInMotion.set(bool);
    }
}
