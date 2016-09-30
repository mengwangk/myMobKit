package com.mymobkit.enums;

/**
 * Media view kind.
 * 
 */
public enum ViewKind implements MyMobKitEnumAsInt {
	ORIGINAL(0), MINI(1), MICRO(3);

	public int hashCode;

	ViewKind(int hashCode) {
		this.hashCode = hashCode;
	}

	public int getHashCode() {
		return this.hashCode;
	}

	public static ViewKind get(final int hashCode) {

		if (ORIGINAL.getHashCode() == hashCode) {
			return ORIGINAL;
		} else if (MINI.getHashCode() == hashCode) {
			return MINI;
		} else {
			return MICRO;
		}
	}
}
