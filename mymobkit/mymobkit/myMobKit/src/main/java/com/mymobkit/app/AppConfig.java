package com.mymobkit.app;


/**
 * Application specific configuration class.
 * 
 */
public final class AppConfig {


	public static final String PACKAGE_NAME = "com.mymobkit";

	public static final String PAGE_PARAM = "page";
	
	public static final String UPLOAD_ACTION_PARAM = "upload_action";

	/**
	 * Parameter used to indicate the HTTP port
	 */
	public static final String HTTPD_SERVICE_ACTION_PARAM = "httpd_service";

	public static final String CONTROL_PANEL_LISTENING_PORT_PARAM = "control_panel_http_listening_port";

	public static final String PRIMARY_ADDRESS_FAMILY_PARAM = "primary_address_family";

	public static final String LOGIN_REQUIRED_PARAM = "login_required";

	public static final String LOGIN_USER_NAME_PARAM = "login_user_name";

	public static final String LOGIN_USER_PASSWORD_PARAM = "login_user_password";

	public static final String DEVICE_ID_PARAM = "device_id";

	public static final String FILE_NAME_PARAM = "file_name";

	public static final String DRIVE_ID_PARAM = "drive_id";

	public static final String POSITION_PARAM = "record_position";

	public static final String DRIVE_FILES_PARAM = "drive_files";

	public static final String DISABLE_NOTIFICATION = "disable_notification";

	public static final String INTENT_START_HTTPD_ACTION = "com.mymobkit.START_HTTPD_SERVICE";

    public static final String INTENT_SHUTDOWN_SURVEILLANCE_ACTION = "com.mymobkit.SHUTDOWN_SURVEILLANCE";

	public static final String INTENT_IP_ADDRESS_CHANGE_ACTION = "com.mymobkit.IP_ADDRESS_CHANGE";

	public static final String INTENT_SHUTDOWN_SERVICE_ACTION = "com.mymobkit.SHUTDOWN_SERVICE";

	public static final int SECURITY_REQ_CREATE_PATTERN = 101;

	public static final int SECURITY_REQ_ENTER_PATTERN = 102;

	//public static final int REQUEST_TOKEN_RESULT_CODE = 103;

	public static String MESSAGE_SENT_ACTION = "MESSAGES_SENT";

	public static String MESSAGE_DELIVERED_ACTION = "MESSAGES_DELIVERED";
	
	public static String MESSAGE_RECEIVED_ACTION = "MESSAGE_RECEIVED";
	
	// public static final int GOOLE_DRIVE_LOGIN_REQUEST = 3344;

	/**
     * Request code for auto Google Play Services error resolution.
     */
    public static final int GOOGLE_REQUEST_CODE_RESOLUTION = 3344;

    
	public static final int CHANGED_DEFAULT_SMS_APP = 4444;
	
	// Is this an internal dogfood build?
    public static final boolean IS_DOGFOOD_BUILD = false;

    public static final String UNIVERSAL_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssz";
    
    
    // --------------- GCM ----------------------------//
    
    // For use with GCM - DEBUG
    public static final String SENDER_ID_DEBUG = "XXX - Removed";
    
    // For use with GCM - PRODUCTION
    public static final String SENDER_ID_PRODUCTION = "XXX - Removed";
    
	public static final String INTENT_GCM_REGISTRATION_COMPLETE_ACTION = "com.mymobkit.GCM_REGISTRATION_COMPLETE";

    // ------------- GCM -------------------------------//

	public static final long HOUSEKEEP_INTERVAL = 6 * 60 * 60 * 1000; // 6 hours


	// --------------- Google Talk -----------------------//

	public final static String LINE_SEPARATOR = System.getProperty("line.separator");

	// Google Talk
	/*public static final String GTALK_OAUTH_TOKEN_URL = "oauth2:https://www.googleapis.com/auth/googletalk";

	public static final String GTALK_EXTRA_KEY_REAUTHORIZE = "reauthorize";*/

	// Intent action for Google Talk
	/*public static final String INTENT_GTALK_ACTION_INIT = "com.mymobkit.xmpp.action.init";
	public static final String INTENT_GTALK_ACTION_START = INTENT_GTALK_ACTION_INIT + ".START";
	public static final String INTENT_GTALK_ACTION_CONNECT = INTENT_GTALK_ACTION_INIT + ".CONNECT";

	public static final String INTENT_GTALK_ACTION_SEND = "com.mymobkit.xmpp.action.SEND";
	public static final String INTENT_GTALK_ACTION_COMMAND = "com.mymobkit.xmpp.action.COMMAND";
	public static final String INTENT_GTALK_ACTION_MESSAGE_RECEIVED = "com.mymobkit.xmpp.action.XMPP.MESSAGE_RECEIVED";*/


}
