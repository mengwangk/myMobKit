package com.mymobkit.enums;

import com.mymobkit.opencv.motion.detection.BackgroundSubtractorDetector;
import com.mymobkit.opencv.motion.detection.BasicDetector;
import com.mymobkit.opencv.motion.detection.IDetector;

public enum MotionDetectionAlgorithm implements MyMobKitEnumAsString {
    BASIC("1"),
    BACKGROUND_SUBTRACTION("2"),
    MOTION_HISTORY_IMAGE("3");

    public String hashCode;

    MotionDetectionAlgorithm(String hashCode) {
        this.hashCode = hashCode;
    }

    public String getHashCode() {
        return this.hashCode;
    }

    public static MotionDetectionAlgorithm get(final String hashCode) {

        if (hashCode.equals(BASIC.getHashCode())) {
            return BASIC;
        } else if (hashCode.equals(BACKGROUND_SUBTRACTION.getHashCode())) {
            return BACKGROUND_SUBTRACTION;
        } else if (hashCode.equals(MOTION_HISTORY_IMAGE.getHashCode())) {
            return MOTION_HISTORY_IMAGE;
        } else {
            return BASIC;
        }
    }

    public static IDetector getDetector(final String hashCode, int threshold) {
        if (hashCode.equals(BASIC.getHashCode())) {
            return new BasicDetector(threshold);
        } else if (hashCode.equals(BACKGROUND_SUBTRACTION.getHashCode())) {
            return new BackgroundSubtractorDetector(threshold);
        } else if (hashCode.equals(MOTION_HISTORY_IMAGE.getHashCode())) {
           // return new MhiDetector(threshold);
            return null;
        } else {
            return new BasicDetector(threshold);
        }
    }
}
