package com.mymobkit.service.api;

import android.text.TextUtils;

import com.mymobkit.R;
import com.mymobkit.app.AppController;
import com.mymobkit.common.EntityUtils;
import com.mymobkit.common.ValidationUtils;
import com.mymobkit.data.MmsHelper;
import com.mymobkit.data.contact.ContactsResolver;
import com.mymobkit.data.contact.ResolvedContact;
import com.mymobkit.mms.job.MmsSendJob;
import com.mymobkit.model.Mms;
import com.mymobkit.model.MmsAttachment;
import com.mymobkit.net.AppServer;
import com.mymobkit.service.HttpdService;
import com.mymobkit.service.api.mms.GetRequest;
import com.mymobkit.service.api.vcalendar.PostRequest;

import org.whispersystems.jobqueue.JobManager;

import java.util.Map;

import ws.com.google.android.mms.ContentType;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * vCalendar API handler.
 */
public final class vCalendarApiHandler extends ApiHandler {

    private static final String TAG = makeLogTag(vCalendarApiHandler.class);

    private ContactsResolver contactsResolver;

    private MmsHelper mmsHelper;

    public static final String PARAM_SUPPORTED_CONTENT_TYPES = "SupportedContentTypes";

    public static final String PARAM_TO = "To";
    public static final String PARAM_CC = "CC";
    public static final String PARAM_BCC = "BCC";
    public static final String PARAM_SUBJECT = "Subject";
    public static final String PARAM_BODY = "Body";
    public static final String PARAM_DELIVERY_REPORT = "DeliveryReport";
    public static final String PARAM_READ_REPORT = "ReadReport";
    public static final String PARAM_VCALENDAR = "vCalendar";

    /**
     * vCalendar API handler.
     *
     * @param service HTTPD service.
     */
    public vCalendarApiHandler(final HttpdService service) {
        super(service);
        contactsResolver = ContactsResolver.getInstance(getContext());
        mmsHelper = MmsHelper.getMmsHelper(getContext());
    }

    @Override
    public String get(final Map<String, String> header, final Map<String, String> params,
                      final Map<String, String> files) {
        final GetRequest request = new GetRequest();
        String id = "";
        try {
            maybeAcquireWakeLock();
            if (params.containsKey(AppServer.URI_PARAM_PREFIX + "0")) {
                // Check if the id exists
                id = getStringValue(AppServer.URI_PARAM_PREFIX + "0", params, "-1");
                Mms mms = null;
                if (ValidationUtils.isNumberString(id)) {
                    mms = mmsHelper.getMms(id);
                }
                if (mms != null) {
                    request.setMessage(mms);
                    request.isSuccessful = true;
                } else {
                    String requestType = getStringValue(AppServer.URI_PARAM_PREFIX + "0", params);
                    if (PARAM_SUPPORTED_CONTENT_TYPES.equalsIgnoreCase(requestType)) {
                        request.setSupportedContentTypes(ContentType.getSupportedTypes());
                        request.isSuccessful = true;
                    } else {
                        if (ValidationUtils.isNumberString(id)) {
                            request.isSuccessful = false;
                            request.setDescription(String.format(getContext().getString(R.string.mms_no_matched_mms), id));
                        } else {
                            request.isSuccessful = false;
                            request.setDescription(String.format(getContext().getString(R.string.msg_unsupported_request)));
                        }
                    }
                }
            } else {
                request.isSuccessful = false;
                request.setDescription(String.format(getContext().getString(R.string.msg_unsupported_request)));
            }
        } catch (Exception ex) {
            request.isSuccessful = false;
            request.setDescription(ex.getMessage());
        } finally {
            releaseWakeLock();
        }
        return gson.toJson(request);
    }

    @Override
    public String post(final Map<String, String> header, final Map<String, String> params, final Map<String, String> files) {
        final PostRequest request = new PostRequest();
        try {
            maybeAcquireWakeLock();

            // Get all the parameters
            final String to = getStringValue(PARAM_TO, params);
            final String cc = getStringValue(PARAM_CC, params);
            final String bcc = getStringValue(PARAM_BCC, params);
            final String subject = getStringValue(PARAM_SUBJECT, params);
            final String body = getStringValue(PARAM_BODY, params);
            final boolean deliveryReport = getBooleanValue(PARAM_DELIVERY_REPORT, params, false);
            final boolean readReport = getBooleanValue(PARAM_READ_REPORT, params, false);
            final String vCalendar = getStringValue(PARAM_VCALENDAR, params);

            if (TextUtils.isEmpty(to) && TextUtils.isEmpty(cc) && TextUtils.isEmpty(bcc)) {
                request.isSuccessful = false;
                request.setDescription(String.format(getContext().getString(R.string.msg_no_contact_found)));
            } else {
                if (TextUtils.isEmpty(vCalendar)) {
                    request.isSuccessful = false;
                    request.setDescription(String.format(getContext().getString(R.string.msg_no_vcalendar_found)));
                } else {
                    final Mms mms = new Mms(generateMmsId(), resolveContact(to), subject);
                    mms.setCc(resolveContact(cc));
                    mms.setBcc(resolveContact(bcc));
                    mms.setDeliveryReport(deliveryReport);
                    mms.setReadReport(readReport);

                    if (!TextUtils.isEmpty(body)) {
                        mms.setBody(body);
                    }
                    mms.addAttachment(new MmsAttachment(ContentType.TEXT_VCALENDAR, vCalendar.getBytes()));

                    // Send the MMS message
                    sendMms(mms);
                    request.setvCalendar(mms);
                }
            }
        } catch (Exception ex) {
            request.isSuccessful = false;
            request.setDescription(String.format(getContext().getString(R.string.msg_mms_sending_error), ex.getMessage()));
        } finally {
            releaseWakeLock();
        }
        return gson.toJson(request);
    }

    @Override
    public String put(final Map<String, String> header, final Map<String, String> params,
                      final Map<String, String> files) {
        return super.put(header, params, files);
    }

    @Override
    public String delete(final Map<String, String> header, final Map<String, String> params,
                         final Map<String, String> files) {
        return super.delete(header, params, files);
    }


    /**
     * Resolve the contact no.
     *
     * @param contact
     * @return
     */

    private String resolveContact(final String contact) {
        if (TextUtils.isEmpty(contact)) return "";

        final ResolvedContact rc = contactsResolver.resolveContact(contact, ContactsResolver.TYPE_CELL);
        String address = contact;
        if (rc != null) {
            if (rc.isDistinct()) {
                address = rc.getNumber();
            } else {
                address = rc.getCandidates()[0].getNumber();
            }
        }
        return address;
    }

    private boolean sendMms(final Mms message) {
        try {
            JobManager jobManager = AppController.getInstance(getContext()).getJobManager();
            jobManager.add(new MmsSendJob(getContext(), message));
        } catch (Exception ex) {
            LOGE(TAG, "[sendMms] Unable to send MMS", ex);
            return false;
        }
        return true;
    }

    private String generateMmsId() {
        return EntityUtils.generateUniqueId();
    }
}

