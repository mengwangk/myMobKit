package com.mymobkit.enums;

public enum TrueFalseEnum implements MyMobKitEnumAsString {
	TRUE("true"),
	FALSE("false");

	public String hashCode;

	TrueFalseEnum(String hashCode) {
		this.hashCode = hashCode;
	}

	public String getHashCode() {
		return this.hashCode;
	}

	public static TrueFalseEnum get(final String hashCode) {

		if (TRUE.getHashCode().equalsIgnoreCase(hashCode))
		{
			return TRUE;
		}
		else if (FALSE.getHashCode().equalsIgnoreCase(hashCode))
		{
			return FALSE;
		} else {
			return FALSE;
		}
	}

}
