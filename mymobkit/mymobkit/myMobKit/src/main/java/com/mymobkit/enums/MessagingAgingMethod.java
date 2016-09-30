package com.mymobkit.enums;

public enum MessagingAgingMethod implements MyMobKitEnumAsString {
	DAYS("Days"),
	SIZE("Size");

	public String hashCode;

	MessagingAgingMethod(String hashCode) {
		this.hashCode = hashCode;
	}

	public String getHashCode() {
		return this.hashCode;
	}

	public static MessagingAgingMethod get(final String hashCode) {

		if (hashCode.equals(DAYS.getHashCode())) {
			return DAYS;
		} {
			return SIZE;
		}
	}
}

