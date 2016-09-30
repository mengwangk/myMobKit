package com.mymobkit.data;

import android.content.Context;

import com.mymobkit.service.api.MessagingApiHandler;

/**
 * Middle-end Helper, if we ever want to integrate the Alias function also in
 * the App itself: This is the way to go. At one place in the code, the
 * constructor needs to be called, to make sure that the Database is setup
 * correctly.
 * 
 */
public final class KeyValueHelper {
	public static final String KEY_LAST_RECIPIENT = "lastRecipient";
	public static final String KEY_SEND_DIR = "sendDir";
	public static final String KEY_SMS_ID = MessagingApiHandler.PARAM_SMS_ID;
	public static final String KEY_SINTENT = "sIntent";
	public static final String KEY_DINTENT = "dIntent";

	private static KeyValueHelper keyValueHelper = null;

	/**
	 * This constructor ensures that the database is setup correctly
	 * 
	 * @param ctx
	 */
	private KeyValueHelper(Context ctx) {
		new KeyValueTable(ctx);
	}

	public static KeyValueHelper getKeyValueHelper(Context ctx) {
		if (keyValueHelper == null) {
			keyValueHelper = new KeyValueHelper(ctx);
		}
		return keyValueHelper;
	}

	public boolean addKey(String key, String value) {
		if (key.contains("'") || value.contains("'"))
			return false;

		addOrUpdate(key, value);
		return true;
	}

	public boolean deleteKey(String key) {
		if (!key.contains("'") && KeyValueTable.containsKey(key)) {
			return KeyValueTable.deleteKey(key);
		} else {
			return false;
		}
	}

	public boolean containsKey(String key) {
		if (!key.contains("'")) {
			return KeyValueTable.containsKey(key);
		} else {
			return false;
		}

	}

	public String getValue(String key) {
		if (!key.contains("'")) {
			return KeyValueTable.getValue(key);
		} else {
			return null;
		}
	}

	public Integer getIntegerValue(String key) {
		String value = getValue(key);
		Integer res;
		try {
			res = Integer.parseInt(value);
		} catch (Exception e) {
			res = null;
		}
		return res;
	}

	public Long getLongValue(String key) {
		String value = getValue(key);
		Long res;
		try {
			res = Long.parseLong(value);
		} catch (Exception e) {
			res = null;
		}
		return res;
	}

	public String[][] getAllKeyValue() {
		String[][] res = KeyValueTable.getFullDatabase();
		if (res.length == 0)
			res = null;
		return res;
	}

	private void addOrUpdate(String key, String value) {
		if (KeyValueTable.containsKey(key)) {
			KeyValueTable.updateKey(key, value);
		} else {
			KeyValueTable.addKey(key, value);
		}
	}
}
