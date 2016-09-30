package com.mymobkit.enums;

public enum ProcessingAction implements MyMobKitEnumAsInt {
	
	PRE_UPLOAD(0),
	REQUEST_UPLOAD_URL(1),
	UPLOAD_IMAGE(2),
	POST_UPLOAD(3),
	PROCESS_FRAME(4);

	private int hashCode;

	ProcessingAction(int hashCode) {
		this.hashCode = hashCode;
	}

	public int getHashCode() {
		return this.hashCode;
	}

	public static ProcessingAction get(final int hashCode) {
		if (REQUEST_UPLOAD_URL.getHashCode() == hashCode) {
			return REQUEST_UPLOAD_URL;
		} else if (UPLOAD_IMAGE.getHashCode() == hashCode) {
			return UPLOAD_IMAGE;
		} else if (PRE_UPLOAD.getHashCode() == hashCode) {
			return PRE_UPLOAD;
		} else if (POST_UPLOAD.getHashCode() == hashCode) {
			return POST_UPLOAD;
		} else if (PROCESS_FRAME.getHashCode() == hashCode) {
			return PROCESS_FRAME;
		} else {
			return null;
		}
	}
}
