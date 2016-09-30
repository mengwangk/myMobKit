package com.mymobkit.enums;

/**
 * Webcam feature.
 *
 */
public enum WebcamFeature implements MyMobKitEnumAsInt {
    SHUTDOWN(0),
    TOGGLE_CAMERA(1),
    VIEW_MOTION(2),
    TOGGLE_LED(3),
    LOCK_CAMERA(4),
    HEART_BEAT(5),
    VIDEO_STREAMING(6),
    AUDIO_STREAMING(7),
    DISGUISE(8),
    SCENE_MODE(9),
    COLOR_EFFECT(10),
    ZOOM(11),
    IMAGE_QUALITY(12),
    FLASH_MODE(13),
    FOCUS_MODE(14),
    WHITE_BALANCE(15),
    ANTI_BANDING(16),
    AUTO_EXPOSURE_LOCK(17),
    EXPOSURE_COMPENSATION(18),
    RECORD_VIDEO_START(19),
    RECORD_VIDEO_STOP(20),
    NIGHT_VISION_MODE(21),
    TAKE_PHOTO(22),
    NIGHT_VISION_HISTOGRAM_EQUALIZATION(23),
    NIGHT_VISION_GAMMA_CORRECTION(24),
    NIGHT_VISION_HISTOGRAM_EQUALIZATION_COLOR(25),
    NIGHT_VISION_GAMMA_LEVEL(26),
    MOTION_DETECTION(27),
    MOTION_DETECTION_THRESHOLD(28);

    private int hashCode;

    WebcamFeature(int hashCode) {
        this.hashCode = hashCode;
    }

    public int getHashCode() {
        return this.hashCode;
    }

    public static WebcamFeature get(final int hashCode) {
        if (TOGGLE_CAMERA.getHashCode() == hashCode) {
            return TOGGLE_CAMERA;
        } else if (VIEW_MOTION.getHashCode() == hashCode) {
            return VIEW_MOTION;
        } else if (TOGGLE_LED.getHashCode() == hashCode) {
            return TOGGLE_LED;
        } else if (SHUTDOWN.getHashCode() == hashCode) {
            return SHUTDOWN;
        } else if (LOCK_CAMERA.getHashCode() == hashCode) {
            return LOCK_CAMERA;
        } else if (HEART_BEAT.getHashCode() == hashCode) {
            return HEART_BEAT;
        } else if (VIDEO_STREAMING.getHashCode() == hashCode) {
            return VIDEO_STREAMING;
        } else if (AUDIO_STREAMING.getHashCode() == hashCode) {
            return AUDIO_STREAMING;
        } else if (DISGUISE.getHashCode() == hashCode) {
            return DISGUISE;
        } else if (SCENE_MODE.getHashCode() == hashCode) {
            return SCENE_MODE;
        } else if (COLOR_EFFECT.getHashCode() == hashCode) {
            return COLOR_EFFECT;
        } else if (ZOOM.getHashCode() == hashCode) {
            return ZOOM;
        } else if (IMAGE_QUALITY.getHashCode() == hashCode) {
            return IMAGE_QUALITY;
        } else if (FLASH_MODE.getHashCode() == hashCode) {
            return FLASH_MODE;
        } else if (FOCUS_MODE.getHashCode() == hashCode) {
            return FOCUS_MODE;
        } else if (WHITE_BALANCE.getHashCode() == hashCode) {
            return WHITE_BALANCE;
        } else if (ANTI_BANDING.getHashCode() == hashCode) {
            return ANTI_BANDING;
        } else if (AUTO_EXPOSURE_LOCK.getHashCode() == hashCode) {
            return AUTO_EXPOSURE_LOCK;
        } else if (EXPOSURE_COMPENSATION.getHashCode() == hashCode) {
            return EXPOSURE_COMPENSATION;
        } else if (RECORD_VIDEO_START.getHashCode() == hashCode) {
            return RECORD_VIDEO_START;
        } else if (RECORD_VIDEO_STOP.getHashCode() == hashCode) {
            return RECORD_VIDEO_STOP;
        } else if (NIGHT_VISION_MODE.getHashCode() == hashCode) {
            return NIGHT_VISION_MODE;
        } else if (TAKE_PHOTO.getHashCode() == hashCode) {
            return TAKE_PHOTO;
        } else if (NIGHT_VISION_HISTOGRAM_EQUALIZATION.getHashCode() == hashCode) {
            return NIGHT_VISION_HISTOGRAM_EQUALIZATION;
        } else if (NIGHT_VISION_GAMMA_CORRECTION.getHashCode() == hashCode) {
            return NIGHT_VISION_GAMMA_CORRECTION;
        } else if (NIGHT_VISION_HISTOGRAM_EQUALIZATION_COLOR.getHashCode() == hashCode) {
            return NIGHT_VISION_HISTOGRAM_EQUALIZATION_COLOR;
        } else if (NIGHT_VISION_GAMMA_LEVEL.getHashCode() == hashCode) {
            return NIGHT_VISION_GAMMA_LEVEL;
        } else if (MOTION_DETECTION.getHashCode() == hashCode) {
            return MOTION_DETECTION;
        } else if (MOTION_DETECTION_THRESHOLD.getHashCode() == hashCode) {
            return MOTION_DETECTION_THRESHOLD;
        } else {
            return null;
        }
    }
}
