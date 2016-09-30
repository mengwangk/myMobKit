package com.mymobkit.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

/**
 * Handles any locale-specific logic for the client.
 * 
 */
public final class LocaleManager {

	private static final String DEFAULT_COUNTRY = "US";
	private static final String DEFAULT_LANGUAGE = "en";

	private static final Collection<String> TRANSLATED_HELP_ASSET_LANGUAGES = Arrays.asList("en");
	// Arrays.asList("de", "en", "es", "fr", "it", "ja", "ko", "nl", "pt", "ru",
	// "zh-rCN", "zh-rTW");

	private LocaleManager() {
	}

	private static String getSystemCountry() {
		Locale locale = Locale.getDefault();
		return locale == null ? DEFAULT_COUNTRY : locale.getCountry();
	}

	private static String getSystemLanguage() {
		Locale locale = Locale.getDefault();
		if (locale == null) {
			return DEFAULT_LANGUAGE;
		}
		String language = locale.getLanguage();
		// Special case Chinese
		if (Locale.SIMPLIFIED_CHINESE.getLanguage().equals(language)) {
			return language + "-r" + getSystemCountry();
		}
		return language;
	}

	public static String getTranslatedAssetLanguage() {
		String language = getSystemLanguage();
		return TRANSLATED_HELP_ASSET_LANGUAGES.contains(language) ? language : DEFAULT_LANGUAGE;
	}

}
