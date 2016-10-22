package com.mymobkit.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public enum RTCMode {

	WEBCAM("0"),
	PHONE("1"),
	VIDEO_CALL("2");

	private String mode;

	private RTCMode(final String mode) {
		this.mode = mode;
	}

	public static RTCMode fromMode(final String mode) {
		if (StringUtils.isBlank(mode))
			return null;
		return codeToEnum.get(mode);
	}

	private static final Map<String, RTCMode> codeToEnum = new HashMap<String, RTCMode>(3);
	static {
		for (RTCMode mode : values())
			codeToEnum.put(mode.getMode(), mode);
	}

	public String getMode() {
		return mode;
	}

}
