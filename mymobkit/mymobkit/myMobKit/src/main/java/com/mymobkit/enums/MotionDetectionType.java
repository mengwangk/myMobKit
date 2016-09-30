package com.mymobkit.enums;

import com.mymobkit.opencv.motion.detection.BasicDetector;
import com.mymobkit.opencv.motion.detection.FaceDetector;
import com.mymobkit.opencv.motion.detection.HumanDetector;
import com.mymobkit.opencv.motion.detection.IDetector;

/**
 * Motion detection type.
 */
public enum MotionDetectionType implements MyMobKitEnumAsString {
    FACE("0"),
    HUMAN("1"),
    MOTION("2");

    public String hashCode;

    MotionDetectionType(String hashCode) {
        this.hashCode = hashCode;
    }

    public String getHashCode() {
        return this.hashCode;
    }

    public static MotionDetectionType get(final String hashCode) {

        if (hashCode.equals(FACE.getHashCode())) {
            return FACE;
        } else if (hashCode.equals(HUMAN.getHashCode())) {
            return HUMAN;
        } else if (hashCode.equals(MOTION.getHashCode())) {
            return MOTION;
        } else {
            return MOTION;
        }
    }

    public static IDetector getDetector(final String hashCode, final String algorithm,
                                        final float faceSize, int threshold) {
        if (hashCode.equals(FACE.getHashCode())) {
            return new FaceDetector(faceSize);
        } else if (hashCode.equals(HUMAN.getHashCode())) {
            return new HumanDetector();
        } else if (hashCode.equals(MOTION.getHashCode())) {
            return MotionDetectionAlgorithm.getDetector(algorithm, threshold);
        } else {
            return new BasicDetector(threshold);
        }
    }
}
