package com.mymobkit.data;

import com.mymobkit.model.ConfigParam;

import android.content.Context;

public final class AppConfigHelper {
	private final Context context;
	private static AppConfigHelper configHelper;

	/**
	 * This constructor ensures that the database is setup correctly
	 * 
	 * @param context
	 */
	private AppConfigHelper(Context context) {
		new AppConfigTable(context);
		this.context = context;
	}

	public static AppConfigHelper getConfigHelper(Context context) {
		if (configHelper == null) {
			configHelper = new AppConfigHelper(context);
		}
		return configHelper;
	}
	
	public ConfigParam getConfig(final String name, final String module) {
		return AppConfigTable.getAppConfig(name, module);
	}
	
	public  boolean updateConfig(final String name, final String module, final String value) {
		return AppConfigTable.updateConfigValue(name, module, value);
	}
	
}
