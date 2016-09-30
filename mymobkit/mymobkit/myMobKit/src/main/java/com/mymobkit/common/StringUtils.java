package com.mymobkit.common;

import java.util.Arrays;
import java.util.List;

/**
 * String utility class.
 * 
 */
public final class StringUtils {

	/**
	 * Empty string constant.
	 */
	public static final String EMPTY = "";

	/**
	 * Limit a string to a defined length.
	 * 
	 * @param value
	 *            - the string to limit.
	 * @param length
	 *            - the total length of the string.
	 * @return the limited string
	 */
	public static String limitString(String value, int length) {
		StringBuilder buf = new StringBuilder(value);
		if (buf.length() > length) {
			buf.setLength(length);
			buf.append(" ...");
		}
		return buf.toString();
	}

	public static String join(String[] list, String sep) {
		return join(Arrays.asList(list), sep);
	}

	public static String join(List<String> list, String sep) {
		String res = "";

		for (String s : list) {
			res += s + sep;
		}

		return delLastChar(res, sep.length());
	}

	public static String delLastChar(String s, int nb) {
		try {
			return s.substring(0, s.length() - nb);
		} catch (Exception e) {
			return "";
		}
	}

	public static boolean isNullOrBlank(String s) {
		return (s == null || s.trim().equals(""));
	}
}
