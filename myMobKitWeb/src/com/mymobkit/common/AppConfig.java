package com.mymobkit.common;

public final class AppConfig {

	/**
	 * Debug mode indicator.
	 */
	public static boolean DEBUG_MODE = true;
	
	/**
	 * Production hosting domain.
	 */
	public static final String PRODUCTION_APP_NAME = "phone-kit";
	
	/**
	 * Web page to view the captured images.
	 */
	public static final String SURVEILLANCE_IMAGE_VIEWER_PAGE = "viewer.jsp";
	
	// DEV key
	public static final String API_KEY_DEV = "AIzaSyDWGJztJGQ8sTzLiGWrK7AgfXudz84IOHk";

	// PROD key
	public static final String API_KEY_PROD = "AIzaSyA7_0WDum9qQ6KKKzf5aBi6xexDamjChHY";

	// GCM attribute key
	public static final String ATTRIBUTE_GCM_KEY = "apiKey";
	
}
