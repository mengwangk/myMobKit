package com.mymobkit.enums;

/**
 * Webcam query.
 *
 */
public enum WebcamQuery implements MyMobKitEnumAsString {
	RESOLUTION("0"), 
	SCENE_MODE("1"), 
	COLOR_EFFECT("2"), 
	IMAGE_QUALITY("3"), 
	FLASH_MODE("4"), 
	FOCUS_MODE("5"), 
	WHITE_BALANCE("6"), 
	ANTI_BANDING("7"), 
	AUTO_EXPOSURE_LOCK("8"), 
	EXPOSURE_COMPENSATION("9"),
	RECORD_VIDEO_STATUS("10"),
	MOTION_DETECTION("11"),
	NIGHT_VISION("12"),
	MOTION_DETECTION_THRESHOLD("13");

	private String hashCode;

	WebcamQuery(final String hashCode) {
		this.hashCode = hashCode;
	}

	public String getHashCode() {
		return this.hashCode;
	}

	public static WebcamQuery get(final String hashCode) {

		if (RESOLUTION.getHashCode().equals(hashCode)) {
			return RESOLUTION;
		} else if (SCENE_MODE.getHashCode().equals(hashCode)) {
			return SCENE_MODE;
		} else if (COLOR_EFFECT.getHashCode().equals(hashCode)) {
			return COLOR_EFFECT;
		} else if (IMAGE_QUALITY.getHashCode().equals(hashCode)) {
			return IMAGE_QUALITY;
		} else if (FLASH_MODE.getHashCode().equals(hashCode)) {
			return FLASH_MODE;
		} else if (FOCUS_MODE.getHashCode().equals(hashCode)) {
			return FOCUS_MODE;
		} else if (WHITE_BALANCE.getHashCode().equals(hashCode)) {
			return WHITE_BALANCE;
		} else if (ANTI_BANDING.getHashCode().equals(hashCode)) {
			return ANTI_BANDING;
		} else if (AUTO_EXPOSURE_LOCK.getHashCode().equals(hashCode)) {
			return AUTO_EXPOSURE_LOCK;
		} else if (EXPOSURE_COMPENSATION.getHashCode().equals(hashCode)) {
			return EXPOSURE_COMPENSATION;
		} else if (RECORD_VIDEO_STATUS.getHashCode().equals(hashCode)) {
			return RECORD_VIDEO_STATUS;
		} else if (MOTION_DETECTION.getHashCode().equals(hashCode)) {
			return MOTION_DETECTION;
		} else if (NIGHT_VISION.getHashCode().equals(hashCode)) {
			return NIGHT_VISION;
		} else if (MOTION_DETECTION_THRESHOLD.getHashCode().equals(hashCode)) {
			return MOTION_DETECTION_THRESHOLD;
		} else {
			return RESOLUTION;
		}
	}

}
