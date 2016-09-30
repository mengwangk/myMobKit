package com.mymobkit.service.api;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonWriter;
import com.mymobkit.R;
import com.mymobkit.data.contact.ContactsResolver;
import com.mymobkit.data.contact.ResolvedContact;
import com.mymobkit.service.HttpdService;
import com.mymobkit.service.api.call.CallHistory;
import com.mymobkit.service.api.call.GetRequest;
import com.mymobkit.service.api.call.PostRequest;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Call management API.
 */
public final class CallApiHandler extends ApiHandler {

    private static final String TAG = makeLogTag(CallApiHandler.class);

    // Call destination
    public static final String PARAM_DESTINATION = "Destination";


    // Contact resolver
    private ContactsResolver contactsResolver;


    /**
     * Constructor.
     *
     * @param service HTTPD service.
     */
    public CallApiHandler(final HttpdService service) {
        super(service);
        contactsResolver = ContactsResolver.getInstance(getContext());
    }


    @Override
    public String get(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
        final GetRequest getRequest = new GetRequest();
        try {
            maybeAcquireWakeLock();
            getRequest.setCallHistories(getCallDetails());
        } catch (Exception ex) {
            getRequest.isSuccessful = false;
            getRequest.setDescription(ex.getMessage());
            LOGE(TAG, "[getCallDetails] Unable to retrieve call logs", ex);
        } finally {
            releaseWakeLock();
        }
        return toJson(getRequest);
    }

    @Override
    public String post(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
        final PostRequest postRequest = new PostRequest();
        try {
            maybeAcquireWakeLock();
            String destination = getStringValue(PARAM_DESTINATION, params);
            if (TextUtils.isEmpty(destination)) {
                // No destination is provided
                postRequest.setDescription(getContext().getString(R.string.msg_no_number));
                postRequest.isSuccessful = false;
            } else {
                ResolvedContact rc = contactsResolver.resolveContact(destination, ContactsResolver.TYPE_CELL);
                String name = destination;
                if (rc != null) {
                    if (rc.isDistinct()) {
                        destination = rc.getNumber();
                        name = rc.getName();
                    } else {
                        destination = rc.getCandidates()[0].getNumber();
                        name = rc.getCandidates()[0].getName();
                    }
                    postRequest.isSuccessful = true;
                    postRequest.setDestination(destination);
                    // Make the call to the number
                    makeCall(destination);

                } else {
                    // Check if it is a valid number
                    if (PhoneNumberUtils.isWellFormedSmsAddress(destination)) {
                        postRequest.isSuccessful = true;
                        postRequest.setDestination(destination);
                        // Make the call to the number
                        makeCall(destination);
                    } else {
                        postRequest.setDescription(String.format(getContext().getString(R.string.msg_invalid_number), destination));
                        postRequest.isSuccessful = false;
                    }
                }
            }
        } finally {
            releaseWakeLock();
        }
        return gson.toJson(postRequest);
    }

    private void makeCall(final String nameOrPhoneNumber) {
        final Intent intent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + nameOrPhoneNumber));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }

    private List<CallHistory> getCallDetails() {
        final List<CallHistory> callHistoryList = new ArrayList<>(1);
        final Cursor managedCursor = getContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
        try {
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            while (managedCursor.moveToNext()) {
                String phNumber = managedCursor.getString(number);
                String callType = managedCursor.getString(type);
                String callDate = managedCursor.getString(date);
                Date callDayTime = new Date(Long.valueOf(callDate));
                String callDuration = managedCursor.getString(duration);
                callHistoryList.add(new CallHistory(phNumber, callType, callDayTime, callDuration));
            }
        } finally {
            managedCursor.close();
        }
        return callHistoryList;
    }

    /**
     * To deal with memory issue on mobile devices.
     * <p/>
     * Refer to https://sites.google.com/site/gson/streaming
     *
     * @param getRequest
     */
    public String toJson(final GetRequest getRequest) {
        if (getRequest == null) return "";

        // for smaller size just use the default toJson method
        if (getRequest.getCallHistories() == null || (getRequest.getCallHistories().size() < 300))
            return gson.toJson(getRequest);

        // Use the streaming method
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        try {
            gson.toJson(getRequest, GetRequest.class, jsonWriter);
            return stringWriter.toString();
        } catch (JsonIOException ioEx) {
            LOGE(TAG, "[toJson] Error generating JSON output", ioEx);
        } finally {
            try {
                jsonWriter.close();
            } catch (IOException ioEx) {
                LOGE(TAG, "[toJson] Error closing JSON writer", ioEx);
            }
        }
        return "";
    }
}
