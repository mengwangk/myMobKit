package com.mymobkit.service.api;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.mymobkit.R;
import com.mymobkit.app.AppController;
import com.mymobkit.common.EntityUtils;
import com.mymobkit.net.AppServer;
import com.mymobkit.service.HttpdService;
import com.mymobkit.service.api.ussd.GetRequest;
import com.mymobkit.service.api.ussd.PostRequest;
import com.mymobkit.service.api.ussd.UssdSession;
import com.mymobkit.service.api.ussd.UssdSessionManager;
import com.mymobkit.service.api.ussd.UssdSessionResponse;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * USSD API handler.
 * <p/>
 * Created by MEKOH on 2/13/2016.
 */
public class UssdApiHandler extends ApiHandler {

    private static final String TAG = makeLogTag(UssdApiHandler.class);

    private static final String USSD_START_PATTERN = "*";
    private static final String USSD_END_PATTERN = "#";

    // USSD command
    public static final String PARAM_COMMAND = "Command";

    // USSD expected response pattern
    public static final String PARAM_RESPONSE_PATTERN = "Pattern";


    /**
     * Constructor.
     *
     * @param service HTTPD service.
     */
    public UssdApiHandler(final HttpdService service) {
        super(service);
    }


    @Override
    public String get(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
        GetRequest getRequest = new GetRequest();
        try {
            maybeAcquireWakeLock();
            final String sessionId = getStringValue(AppServer.URI_PARAM_PREFIX + "0", params, "");
            if (TextUtils.isEmpty(sessionId)) {
                getRequest.setDescription(getContext().getString(R.string.msg_ussd_invalid_session_id));
                getRequest.isSuccessful = false;
            } else {
                final UssdSessionManager sessionManager = AppController.getSessionManager().getUssdSessionManager();
                final UssdSessionResponse response = sessionManager.getResponse(sessionId);
                if (response != null) {
                    getRequest.setSessionId(sessionId);
                    getRequest.setResponse(response.getResponse());
                    getRequest.isSuccessful = true;
                } else {
                    getRequest.setDescription(getContext().getString(R.string.msg_ussd_session_no_response));
                    getRequest.isSuccessful = false;
                }
            }
        } finally {
            releaseWakeLock();
        }
        return gson.toJson(getRequest);
    }

    @Override
    public String post(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
        final PostRequest postRequest = new PostRequest();
        try {
            maybeAcquireWakeLock();
            String command = getStringValue(PARAM_COMMAND, params);
            String expectedResponsePattern = getStringValue(PARAM_RESPONSE_PATTERN, params);
            Pattern pattern = null;
            boolean patternError = false;
            if (!TextUtils.isEmpty(expectedResponsePattern)) {
                try {
                    pattern = Pattern.compile(expectedResponsePattern, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                } catch (PatternSyntaxException pse) {
                    LOGE(TAG, "[post] Pattern syntax error");
                    patternError = true;
                }
            }

            if (TextUtils.isEmpty(command)) {
                postRequest.setDescription(getContext().getString(R.string.msg_invalid_ussd_command));
                postRequest.isSuccessful = false;
            } else if (patternError) {
                postRequest.setDescription(getContext().getString(R.string.msg_invalid_ussd_response_pattern));
                postRequest.isSuccessful = false;
            } else  {
                // Send the USSD command
                final String sessionId = EntityUtils.generateUniqueId();
                if (command.endsWith(USSD_END_PATTERN)) {
                    command = command.substring(0, command.length() - 1);
                }
                command += Uri.encode(USSD_END_PATTERN);
                if (!command.startsWith(USSD_START_PATTERN))
                    command = USSD_START_PATTERN + command;

                dialUssdCommand(command);

                postRequest.isSuccessful = true;
                postRequest.setUssdCommand(command);
                postRequest.setResponsePattern(expectedResponsePattern);
                postRequest.setSessionId(sessionId);

                // Add the session to USSD session manager
                final UssdSessionManager ussdSessionManager = AppController.getSessionManager().getUssdSessionManager();
                final UssdSession ussdSession = new UssdSession(sessionId, command, expectedResponsePattern, pattern);
                ussdSessionManager.addSession(ussdSession);

            }
        } finally {
            releaseWakeLock();
        }
        return gson.toJson(postRequest);
    }

    private void dialUssdCommand(final String command){
        // Send USSD command
        final Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + command));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }
}
