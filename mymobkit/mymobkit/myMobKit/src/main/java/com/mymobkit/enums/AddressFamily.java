package com.mymobkit.enums;

public enum AddressFamily {
	IPv4(1),
	IPv6(4);

	private int hashCode;

	AddressFamily(int hashCode) {
		this.hashCode = hashCode;
	}

	public int getHashCode() {
		return this.hashCode;
	}

	public static AddressFamily get(final int hashCode) {
		if (IPv4.getHashCode() == hashCode) {
			return IPv4;
		} else if (IPv6.getHashCode() == hashCode) {
			return IPv6;
		} else {
			return null;
		}
	}
}
