package com.mymobkit.enums;

public enum MessageStatus implements MyMobKitEnumAsString {
	SENT("Sent"), DELIVERED("Delivered"), QUEUED("Queued"), FAILED("Failed");

	public String hashCode;

	MessageStatus(String hashCode) {
		this.hashCode = hashCode;
	}

	public String getHashCode() {
		return this.hashCode;
	}

	public static MessageStatus get(final String hashCode) {

		if (hashCode.equals(SENT.getHashCode())) {
			return SENT;
		} else if (hashCode.equals(DELIVERED.getHashCode())) {
			return DELIVERED;
		} else if (hashCode.equals(QUEUED.getHashCode())) {
			return QUEUED;
		} else {
			return FAILED;
		}
	}
}
