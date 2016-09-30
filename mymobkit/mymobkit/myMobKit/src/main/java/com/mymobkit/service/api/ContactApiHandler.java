package com.mymobkit.service.api;

import android.text.TextUtils;

import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonWriter;
import com.mymobkit.R;
import com.mymobkit.net.AppServer;
import com.mymobkit.service.HttpdService;
import com.mymobkit.service.api.contact.Contact;
import com.mymobkit.service.api.contact.ContactInfo;
import com.mymobkit.service.api.contact.ContactManager;
import com.mymobkit.service.api.contact.DeleteRequest;
import com.mymobkit.service.api.contact.GetRequest;
import com.mymobkit.service.api.contact.PostRequest;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Contact management REST APIs.
 */
public final class ContactApiHandler extends ApiHandler {

    private static final String TAG = makeLogTag(ContactApiHandler.class);

    public static final String PARAM_NAME = "Name";
    public static final String PARAM_PHONE_NUMBER = "Number";

    public static final String PARAM_MOBILE_NO = "mobile";
    public static final String PARAM_HOME_NO = "home";
    public static final String PARAM_WORK_NO = "work";
    public static final String PARAM_EMAIL = "email";
    public static final String PARAM_COMPANY = "company";
    public static final String PARAM_JOB_TITLE = "jobtitle";

    private ContactManager contactManager;

    public ContactApiHandler(HttpdService service) {
        super(service);
        contactManager = new ContactManager(getContext());
    }

    @Override
    public String get(final Map<String, String> header, final Map<String, String> params, final Map<String, String> files) {
        GetRequest getRequest = new GetRequest();
        List<ContactInfo> contacts = null;
        try {
            maybeAcquireWakeLock();
            if (params.containsKey(AppServer.URI_PARAM_PREFIX + "0")) {
                final String contactId = getStringValue(AppServer.URI_PARAM_PREFIX + "0", params);
                contacts = new ArrayList<ContactInfo>(1);
                if (!TextUtils.isEmpty(contactId)) {
                    ContactInfo contactInfo = contactManager.getContact(contactId);
                    if (contactInfo == null) {
                        // No contact found
                        getRequest.isSuccessful = false;
                        getRequest.setDescription(String.format(getContext().getString(R.string.media_no_matched_contact), String.valueOf(contactId)));
                    } else {
                        contacts.add(contactInfo);
                    }
                } else {
                    getRequest.isSuccessful = false;
                    getRequest.setDescription(String.format(getContext().getString(R.string.media_no_matched_contact), String.valueOf(contactId)));
                }
            } else {
                final String contactName = getStringValue(PARAM_NAME, params);
                final String contactPhoneNo = getStringValue(PARAM_PHONE_NUMBER, params);

                if (!TextUtils.isEmpty(contactName)) {
                    contacts = contactManager.getContactByName(contactName);
                } else if (!TextUtils.isEmpty(contactPhoneNo)) {
                    contacts = contactManager.getContactByPhoneNumber(contactPhoneNo);
                } else {
                    // Default to return all contacts
                    contacts = contactManager.getAllContacts();
                }
                if (contacts == null || contacts.isEmpty()) {
                    String desc = getContext().getString(R.string.msg_no_contact_found);
                    getRequest.setDescription(desc);
                    getRequest.isSuccessful = false;
                }
            }
            getRequest.setContacts(contacts);
        } catch (Exception ex) {
            getRequest.setDescription(ex.getMessage());
            getRequest.isSuccessful = false;
        } finally {
            releaseWakeLock();
        }
        //Gson gson = new GsonBuilder().setDateFormat(AppConfig.UNIVERSAL_DATE_FORMAT).excludeFieldsWithoutExposeAnnotation().create();
        //return gson.toJson(getRequest);
        return toJson(getRequest);
    }

    @Override
    public String post(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
        final PostRequest postRequest = new PostRequest();
        try {
            maybeAcquireWakeLock();
            final String name = getStringValue(PARAM_NAME, params);
            final String mobileNo = getStringValue(PARAM_MOBILE_NO, params);
            final String homeNo = getStringValue(PARAM_HOME_NO, params);
            final String workNo = getStringValue(PARAM_WORK_NO, params);
            final String email = getStringValue(PARAM_EMAIL, params);
            final String company = getStringValue(PARAM_COMPANY, params);
            final String jobTitle = getStringValue(PARAM_JOB_TITLE, params);

            if (TextUtils.isEmpty(name)) {
                postRequest.setDescription(getContext().getString(R.string.msg_invalid_contact_name));
                postRequest.isSuccessful = false;
            } else {
                // Add the contact
                final Contact contact = new Contact(name);
                contact.setMobileNumber(mobileNo);
                contact.setHomeNumber(homeNo);
                contact.setWorkNumber(workNo);
                contact.setEmail(email);
                contact.setCompany(company);
                contact.setJobTitle(jobTitle);
                if (contactManager.addContact(contact)) {
                    postRequest.isSuccessful = true;
                    postRequest.setContact(contact);
                } else {
                    postRequest.setDescription(getContext().getString(R.string.msg_error_add_contact));
                    postRequest.isSuccessful = false;
                }
            }
        } finally {
            releaseWakeLock();
        }
        return gson.toJson(postRequest);
    }

    @Override
    public String put(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
        return super.put(header, params, files);
    }

    @Override
    public String delete(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
        DeleteRequest deleteRequest = new DeleteRequest();
        try {
            maybeAcquireWakeLock();
            if (params.containsKey(AppServer.URI_PARAM_PREFIX + "0")) {
                // Delete a particular contact
                final String paramValue = params.get(AppServer.URI_PARAM_PREFIX + "0");
                final long id = getLongValue(AppServer.URI_PARAM_PREFIX + "0", params, -1);

                if (id >= 0 && contactManager.deleteContact(id)) {
                    deleteRequest.setCount(1);
                }

                if (deleteRequest.getCount() > 0) {
                    deleteRequest.setDescription(String.format(getContext().getString(R.string.msg_contact_delete_success), paramValue));
                    deleteRequest.isSuccessful = true;
                } else {
                    // Unable to delete the contact
                    deleteRequest.setDescription(String.format(getContext().getString(R.string.msg_contact_delete_failure), paramValue));
                    deleteRequest.isSuccessful = false;
                }
            }
        } finally {
            releaseWakeLock();
        }
        return gson.toJson(deleteRequest);
    }

    /**
     * To deal with memory issue on mobile devices.
     * <p/>
     * Refer to https://sites.google.com/site/gson/streaming
     *
     * @param getRequest Request to be serialized.
     */
    public String toJson(final GetRequest getRequest) {
        if (getRequest == null) return "";

        // for smaller size just use the default toJson method
        if (getRequest.getContacts() == null || (getRequest.getContacts().size() < 300))
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
