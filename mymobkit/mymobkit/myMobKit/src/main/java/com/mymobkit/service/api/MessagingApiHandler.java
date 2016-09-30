package com.mymobkit.service.api;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonWriter;
import com.mymobkit.R;
import com.mymobkit.app.AppConfig;
import com.mymobkit.common.AppPreference;
import com.mymobkit.common.EntityUtils;
import com.mymobkit.common.IPredicate;
import com.mymobkit.common.Pageable;
import com.mymobkit.common.Predicate;
import com.mymobkit.common.SmsUtils;
import com.mymobkit.common.StringUtils;
import com.mymobkit.common.ValidationUtils;
import com.mymobkit.data.KeyValueHelper;
import com.mymobkit.data.SmsHelper;
import com.mymobkit.data.contact.ContactsResolver;
import com.mymobkit.data.contact.ResolvedContact;
import com.mymobkit.enums.MessageType;
import com.mymobkit.net.AppServer;
import com.mymobkit.receiver.DeliveredIntentReceiver;
import com.mymobkit.receiver.SentIntentReceiver;
import com.mymobkit.service.HttpdService;
import com.mymobkit.service.api.sms.DeleteRequest;
import com.mymobkit.service.api.sms.GetRequest;
import com.mymobkit.service.api.sms.PostRequest;
import com.mymobkit.service.api.sms.PutRequest;
import com.mymobkit.service.api.sms.Sms;
import com.mymobkit.service.api.sms.SmsManager;
import com.mymobkit.ui.fragment.ServiceSettingsFragment;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

public final class MessagingApiHandler extends ApiHandler {

    private static final String TAG = makeLogTag(MessagingApiHandler.class);

    public enum MessageStatus {
        NONE(-1), UNREAD(0), READ(1);

        private int value;

        private MessageStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static MessageStatus get(int value) {
            if (UNREAD.getValue() == value) {
                return UNREAD;
            } else if (READ.getValue() == value) {
                return READ;
            } else {
                return NONE;
            }
        }
    }

    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
    private static final String GREATER_OR_EQUAL = ">=";
    private static final String GREATER_THAN = ">";
    private static final String LESS_THAN_OR_EQUAL = "<=";
    private static final String LESS_THAN = "<";
    private static final String EQUAL = "=";

    private static final List<String> OPERATORS = Collections.unmodifiableList(Arrays.asList(GREATER_THAN, GREATER_OR_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL, EQUAL));

    public static final String PARAM_MESSAGE_STATUS = "Status"; // Unread = 0, read = 1, else all messages
    public static final String PARAM_TO = "To";
    public static final String PARAM_FROM = "From";
    public static final String PARAM_DATE_SENT = "DateSent"; // E.g.
    // DateSent>=YYYY-MM-DD
    // or
    // DateSent<=YYYY-MM-DD
    public static final String PARAM_PAGE = "Page";
    public static final String PARAM_PAGE_SIZE = "PageSize";
    public static final String PARAM_THREAD_ID = "ThreadID";
    public static final String PARAM_MESSAGE = "Message";
    public static final String PARAM_SMS_ID = "smsID";
    public static final String PARAM_PART_NUM = "partNum";
    public static final String PARAM_ID = "id";
    public static final String PARAM_MESSAGE_TYPE = "type";
    public static final String PARAM_DELIVERY_REPORT = "DeliveryReport";
    public static final String PARAM_SERVICE_CENTRE_ADDRESS = "scAddress";
    public static final String PARAM_SIM_SLOT = "slot";


    public static final String ID_SEPARATOR = ",";

    private SmsManager smsManager;
    private ContactsResolver contactsResolver;
    private KeyValueHelper keyValueHelper;
    private SmsHelper smsHelper;

    private static boolean sentIntentReceiverRegistered = false;
    private static boolean delIntentReceiverRegistered = false;
    private static BroadcastReceiver sentSmsReceiver = null;
    private static BroadcastReceiver deliveredSmsReceiver = null;

    public MessagingApiHandler(final HttpdService service) {
        super(service);
        smsManager = new SmsManager(getContext());
        contactsResolver = ContactsResolver.getInstance(getContext());
        smsHelper = SmsHelper.getSmsHelper(getContext());
        keyValueHelper = KeyValueHelper.getKeyValueHelper(getContext());

        restoreSmsInformation();

        // Register sent and delivered receiver
        if (!sentIntentReceiverRegistered) {
            if (sentSmsReceiver == null) {
                boolean saveSentMessage = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_SAVE_SENT_MESSAGES, Boolean.valueOf(getContext().getString(R.string.default_save_sent_messages)));
                sentSmsReceiver = new SentIntentReceiver(service, smsHelper, saveSentMessage);
            }
            getContext().registerReceiver(sentSmsReceiver, new IntentFilter(AppConfig.MESSAGE_SENT_ACTION));
            sentIntentReceiverRegistered = true;
        }

        if (!delIntentReceiverRegistered) {
            if (deliveredSmsReceiver == null) {
                deliveredSmsReceiver = new DeliveredIntentReceiver(service, smsHelper);
            }
            getContext().registerReceiver(deliveredSmsReceiver, new IntentFilter(AppConfig.MESSAGE_DELIVERED_ACTION));
            delIntentReceiverRegistered = true;
        }

    }

    @Override
    public String get(final Map<String, String> header, final Map<String, String> params, final Map<String, String> files) {
        GetRequest getRequest = new GetRequest();
        List<Sms> results = null;
        try {
            maybeAcquireWakeLock();
            if (params.containsKey(AppServer.URI_PARAM_PREFIX + "0")) {
                // View a particular SMS
                final String paramValue = params.get(AppServer.URI_PARAM_PREFIX + "0");
                final long id = getLongValue(AppServer.URI_PARAM_PREFIX + "0", params, -1);
                results = smsManager.getSmsById(id);
                if (results == null || results.isEmpty()) {
                    String desc = String.format(getContext().getString(R.string.msg_no_matched_msg), paramValue);
                    getRequest.setDescription(desc);
                    getRequest.isSuccessful = false;
                }
            } else {
                // Get all the parameters
                final String to = getStringValue(PARAM_TO, params);
                final String from = getStringValue(PARAM_FROM, params);
                final String dtParam = getStringValue(PARAM_DATE_SENT, params);
                final int page = getIntegerValue(PARAM_PAGE, params); // Start
                // from
                // 0
                final int pageSize = getIntegerValue(PARAM_PAGE_SIZE, params);
                final int threadID = getIntegerValue(PARAM_THREAD_ID, params, -1);
                final int messageType = getIntegerValue(PARAM_MESSAGE_STATUS, params, -1);
                final MessageStatus messageStatus = MessageStatus.get(messageType);

                String dateOperator = "";
                String dateValue = "";
                if (!TextUtils.isEmpty(dtParam)) {
                    dateOperator = getOperator(dtParam);
                    if (!TextUtils.isEmpty(dateOperator)) {
                        dateValue = dtParam.substring(dateOperator.length());
                    }
                }

                // Get a list of all SMS
                List<Sms> allMessages = getSmsByCriteria(to, from, dateOperator, dateValue, threadID, messageStatus);
                int totalMessages = allMessages.size();
                results = allMessages;
                if (totalMessages > 0) {
                    if (!TextUtils.isEmpty(to)) {
                        results = (List<Sms>) Predicate.filter(results, new IPredicate<Sms>() {
                            public boolean apply(Sms msg) {
                                return (to.equalsIgnoreCase(msg.getNumber()) || to.equalsIgnoreCase(msg.getReceiver()));
                            }
                        });

                    }

                    if (!TextUtils.isEmpty(from)) {
                        results = (List<Sms>) Predicate.filter(results, new IPredicate<Sms>() {
                            public boolean apply(Sms msg) {
                                return (from.equalsIgnoreCase(msg.getNumber()) || from.equalsIgnoreCase(msg.getSender()));
                            }
                        });
                    }

                    if (pageSize > 0) {
                        Pageable<Sms> pageAble = new Pageable<Sms>(results);
                        pageAble.setPageSize(pageSize);
                        pageAble.setPage(page);
                        results = pageAble.getListForPage();
                    }
                }
                if (results == null || results.isEmpty()) {
                    String desc = getContext().getString(R.string.msg_no_matched_criteria_msg);
                    getRequest.setDescription(desc);
                    getRequest.isSuccessful = false;
                }
            }
            getRequest.setMessages(results);
        } finally {
            releaseWakeLock();
        }
        return toJson(getRequest);
    }

    private String getOperator(final String param) {
        String operator = getFirstNCharacters(param, 2);
        if (OPERATORS.contains(operator))
            return operator;
        else {
            operator = getFirstNCharacters(param, 1);
            if (OPERATORS.contains(operator))
                return operator;
        }
        return StringUtils.EMPTY;
    }

    private String getFirstNCharacters(final String s, final int n) {
        return s.substring(0, Math.min(s.length(), n));
    }

    @Override
    public String post(final Map<String, String> header, final Map<String, String> params, final Map<String, String> files) {
        PostRequest msgRequest = new PostRequest();
        try {
            maybeAcquireWakeLock();
            String msg = "";
            String to = getStringValue(PARAM_TO, params);
            final String body = getStringValue(PARAM_MESSAGE, params);
            final String serviceCenterAddress = getStringValue(PARAM_SERVICE_CENTRE_ADDRESS, params);
            final String simSlot = getStringValue(PARAM_SIM_SLOT, params);
            final int type = getIntegerValue(PARAM_MESSAGE_TYPE, params, -1);
            final boolean deliveryReport = getBooleanValue(PARAM_DELIVERY_REPORT, params, true);

            if (TextUtils.isEmpty(to)) {
                msg = String.format(getContext().getString(R.string.msg_no_matched_contact), to);
                msgRequest.setDescription(msg);
                msgRequest.isSuccessful = false;
            } else {
                ResolvedContact rc = contactsResolver.resolveContact(to, ContactsResolver.TYPE_CELL);
                String name = to;
                if (rc != null) {
                    if (rc.isDistinct()) {
                        to = rc.getNumber();
                        name = rc.getName();
                    } else {
                        to = rc.getCandidates()[0].getNumber();
                        name = rc.getCandidates()[0].getName();
                    }
                }

                final Sms sms = sendSmsByPhoneNumber(body, to, name, deliveryReport, serviceCenterAddress, simSlot);
                msgRequest.setMessage(sms);

               /* if (rc == null) {
                    msg = String.format(getContext().getString(R.string.msg_no_matched_contact), to);
                    msgRequest.setDescription(msg);
                    msgRequest.isSuccessful = false;
                } else if (rc.isDistinct()) {
                    final Sms sms = sendSmsByPhoneNumber(body, rc.getNumber(), rc.getName(), deliveryReport);
                    msgRequest.setMessage(sms);
                } else if (!rc.isDistinct()) {
                    msg = String.format(getContext().getString(R.string.msg_contact_is_no_distinct), to);
                    msgRequest.setDescription(msg);
                    msgRequest.isSuccessful = false;
                }*/
            }
        } finally {
            releaseWakeLock();
        }
        //Gson gson = new GsonBuilder().setDateFormat(AppConfig.UNIVERSAL_DATE_FORMAT).excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(msgRequest);
    }

    @Override
    public String delete(final Map<String, String> header, final Map<String, String> params, final Map<String, String> files) {
        DeleteRequest request = new DeleteRequest();
        int deletedRows = 0;
        try {
            maybeAcquireWakeLock();
            String msg = "";
            if (params.containsKey(AppServer.URI_PARAM_PREFIX + "0")) {
                // Delete a particular SMS
                final String paramValue = params.get(AppServer.URI_PARAM_PREFIX + "0");
                final long id = getLongValue(AppServer.URI_PARAM_PREFIX + "0", params, -1);
                int result = smsManager.deleteSmsById(id);
                if (result > 0)
                    deletedRows += result;

                if (deletedRows > 0) {
                    msg = String.format(getContext().getString(R.string.msg_delete_success), paramValue);
                } else {
                    // Unable to delete the message
                    msg = String.format(getContext().getString(R.string.msg_delete_failure), paramValue);
                    request.isSuccessful = false;
                }
            } else {
                final String ids = getStringValue(PARAM_ID, params);
                if (!TextUtils.isEmpty(ids)) {
                    String[] idList = TextUtils.split(ids, ID_SEPARATOR);
                    for (String id : idList) {
                        long idValue = ValidationUtils.getLong(id, -1);
                        if (idValue >= 0) {
                            int result = smsManager.deleteSmsById(idValue);
                            if (result > 0)
                                deletedRows += result;
                        }
                    }
                    msg = getContext().getString(R.string.msg_delete_all);
                } else {
                    final long threadId = getLongValue(PARAM_THREAD_ID, params, -1);
                    if (threadId >= 0) {
                        int result = smsManager.deleteSmsByThreadId(threadId);
                        if (result > 0)
                            deletedRows += result;
                        msg = getContext().getString(R.string.msg_delete_all);
                    } else {
                        msg = getContext().getString(R.string.msg_delete_no_id);
                        request.isSuccessful = false;
                    }
                }
            }
            request.setDescription(msg);
        } finally {
            releaseWakeLock();
        }
        request.setCount(deletedRows);
        //Gson gson = new GsonBuilder().setDateFormat(AppConfig.UNIVERSAL_DATE_FORMAT).excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(request);
    }

    @Override
    public String put(final Map<String, String> header, final Map<String, String> params, final Map<String, String> files) {
        PutRequest request = new PutRequest();
        int updatedRows = 0;
        try {
            maybeAcquireWakeLock();
            String msg = "";
            if (params.containsKey(AppServer.URI_PARAM_PREFIX + "0")) {
                // Update a particular SMS
                final String paramValue = params.get(AppServer.URI_PARAM_PREFIX + "0");
                final String id = getStringValue(AppServer.URI_PARAM_PREFIX + "0", params);
                if (!TextUtils.isEmpty(id))
                    if (smsManager.markAsRead(id))
                        updatedRows++;

                if (updatedRows > 0) {
                    msg = String.format(getContext().getString(R.string.msg_update_success), paramValue);
                } else {
                    // Unable to update the message
                    msg = String.format(getContext().getString(R.string.msg_update_failure), paramValue);
                    request.isSuccessful = false;
                }
            } else {
                final String ids = getStringValue(PARAM_ID, params);
                if (!TextUtils.isEmpty(ids)) {
                    String[] idList = TextUtils.split(ids, ID_SEPARATOR);
                    for (String id : idList) {
                        if (smsManager.markAsRead(id))
                            updatedRows++;
                    }
                } else {
                    // Update all unread to read
                    updatedRows = smsManager.markAllAsRead();
                }
                msg = getContext().getString(R.string.msg_update_all);
            }
            request.setDescription(msg);
        } finally {
            releaseWakeLock();
        }
        request.setCount(updatedRows);
        //Gson gson = new GsonBuilder().setDateFormat(AppConfig.UNIVERSAL_DATE_FORMAT).excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(request);
    }

    @Override
    public void stop() {
        super.stop();
        smsManager = null;

        if (sentSmsReceiver != null && sentIntentReceiverRegistered) {
            getContext().unregisterReceiver(sentSmsReceiver);
            sentIntentReceiverRegistered = false;
        }
        if (deliveredSmsReceiver != null && delIntentReceiverRegistered) {
            getContext().unregisterReceiver(deliveredSmsReceiver);
            delIntentReceiverRegistered = false;
        }
    }

    /**
     * Sends a sms to the specified phone number with a custom receiver name Creates the pendingIntents
     * for send/delivery notifications, if needed. Adds the sent SMS to the systems SentBox
     *
     * @param message
     * @param phoneNumber
     * @param toName
     * @param deliveryReport
     * @param serviceCenterAddress
     * @param simSlot
     */
    private Sms sendSmsByPhoneNumber(String message, String phoneNumber, String toName, boolean deliveryReport, String serviceCenterAddress, String simSlot) {
        ArrayList<PendingIntent> sentPendingIntents;
        ArrayList<PendingIntent> deliveredPendingIntents;
        android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
        ArrayList<String> messages = smsManager.divideMessage(message);

        String smsId = getSmsId();
        Sms sms = new Sms(phoneNumber, toName, message, messages.size(), null, smsId);
        smsHelper.addSms(sms);

        sentPendingIntents = createSPendingIntents(messages.size(), smsId);
        if (deliveryReport)
            deliveredPendingIntents = createDPendingIntents(messages.size(), smsId);
        else
            deliveredPendingIntents = null;

        if (TextUtils.isEmpty(serviceCenterAddress)) {
            serviceCenterAddress = null;
        }
        if (TextUtils.isEmpty(simSlot)) {
            smsManager.sendMultipartTextMessage(phoneNumber, serviceCenterAddress, messages, sentPendingIntents, deliveredPendingIntents);
        } else {
            if (SmsUtils.SIM_SLOT_1.equals(simSlot)) {
                SmsUtils.sendMultipartTextSms(getContext(), 1, phoneNumber, serviceCenterAddress, messages, sentPendingIntents, deliveredPendingIntents);
            } else if (SmsUtils.SIM_SLOT_2.equals(simSlot)) {
                SmsUtils.sendMultipartTextSms(getContext(), 2, phoneNumber, serviceCenterAddress, messages, sentPendingIntents, deliveredPendingIntents);
            } else if (SmsUtils.SIM_SLOT_0.equals(simSlot)) {
                SmsUtils.sendMultipartTextSms(getContext(), 0, phoneNumber, serviceCenterAddress, messages, sentPendingIntents, deliveredPendingIntents);
            } else {
                smsManager.sendMultipartTextMessage(phoneNumber, serviceCenterAddress, messages, sentPendingIntents, deliveredPendingIntents);
            }
        }
        return sms;
    }

	/*
     * private Sms sendvCardByPhoneNumber(final String message, final String phoneNumber, final String toName) { final short DESTINATION_PORT = 9204; ArrayList<PendingIntent> sentPendingIntents = new
	 * ArrayList<PendingIntent>(); ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>(); android.telephony.SmsManager smsManager =
	 * android.telephony.SmsManager.getDefault();
	 * 
	 * String smsId = getSmsId(); Sms s = new Sms(phoneNumber, toName, message, 1, null, smsId); smsHelper.addSms(s);
	 * 
	 * //SubmitPdu pdu = SmsMessage.getSubmitPdu(null, phoneNumber, DESTINATION_PORT, message.getBytes(Charset.forName("UTF-8")), true); sentPendingIntents = createSPendingIntents(1, smsId);
	 * deliveredPendingIntents = createDPendingIntents(1, smsId); smsManager.sendDataMessage(phoneNumber, null, DESTINATION_PORT, message.getBytes(Charset.forName("UTF-8")), sentPendingIntents.get(0),
	 * deliveredPendingIntents.get(0)); return s; }
	 */

    private ArrayList<PendingIntent> createSPendingIntents(int size, String smsID) {
        ArrayList<PendingIntent> pendingIntents = new ArrayList<PendingIntent>();
        int startSIntentNumber = getSIntentStart(size);
        for (int i = 0; i < size; i++) {
            int p = startSIntentNumber++;
            Intent sentIntent = new Intent(AppConfig.MESSAGE_SENT_ACTION);
            sentIntent.putExtra(PARAM_PART_NUM, i);
            sentIntent.putExtra(PARAM_SMS_ID, smsID);
            PendingIntent sentPenIntent = PendingIntent.getBroadcast(getContext(), p, sentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            pendingIntents.add(sentPenIntent);
        }
        return pendingIntents;
    }

    private ArrayList<PendingIntent> createDPendingIntents(int size, String smsID) {
        ArrayList<PendingIntent> pendingIntents = new ArrayList<PendingIntent>();
        int startDIntentNumber = getDIntentStart(size);
        for (int i = 0; i < size; i++) {
            int p = startDIntentNumber++;
            Intent deliveredIntent = new Intent(AppConfig.MESSAGE_DELIVERED_ACTION);
            deliveredIntent.putExtra(PARAM_PART_NUM, i);
            deliveredIntent.putExtra(PARAM_SMS_ID, smsID);
            PendingIntent deliveredPenIntent = PendingIntent.getBroadcast(getContext(), p, deliveredIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            pendingIntents.add(deliveredPenIntent);
        }
        return pendingIntents;
    }

    private int getSIntentStart(int size) {
        Integer res = keyValueHelper.getIntegerValue(KeyValueHelper.KEY_SINTENT);
        Integer newValue = res + size;
        keyValueHelper.addKey(KeyValueHelper.KEY_SINTENT, newValue.toString());
        return res;
    }

    private int getDIntentStart(int size) {
        Integer res = keyValueHelper.getIntegerValue(KeyValueHelper.KEY_DINTENT);
        Integer newValue = res + size;
        keyValueHelper.addKey(KeyValueHelper.KEY_DINTENT, newValue.toString());
        return res;
    }

    private List<Sms> getSmsByCriteria(final String to, final String from, final String dateOperator, final String dateValue, final int threadID, final MessageStatus messageStatus) {
        List<Integer> msgTypes = new ArrayList<Integer>(1);
        if (!TextUtils.isEmpty(to)) {
            msgTypes.add(MessageType.MESSAGE_TYPE_SENT.getHashCode());
        }
        if (!TextUtils.isEmpty(from)) {
            msgTypes.add(MessageType.MESSAGE_TYPE_INBOX.getHashCode());
        }
        String where = "";
        if (msgTypes.size() > 0)
            where = "type in (" + TextUtils.join(", ", msgTypes) + ")";

        // timestamp -= timestamp % (24 * 60 * 60 * 1000) - remove time part
        if (!TextUtils.isEmpty(dateOperator) && !TextUtils.isEmpty(dateValue)) {
            ParsePosition pp = new ParsePosition(0);
            //final Date dt = DATE_FORMATTER.parse(dateValue, pp);
            //if (dt != null) {
            if (!TextUtils.isEmpty(where))
                where += " and ";
            //where += "date " + dateOperator + dt.getTime();
            //where += "cast([date] / (24.0 * 60 * 60 * 1000) as int) " + dateOperator + (int)(dt.getTime() / (24.0 * 60 * 60 * 1000));
            where += "strftime('%Y-%m-%d', [date]/1000, 'unixepoch')" + dateOperator + "'" + dateValue + "'";
            //}
        }
        if (threadID > 0) {
            if (!TextUtils.isEmpty(where))
                where += " and ";
            where += "thread_id = " + threadID;
        }

        if (messageStatus != MessageStatus.NONE) {
            if (!TextUtils.isEmpty(where))
                where += " and ";
            where += "read = " + messageStatus.getValue();
        }
        return smsManager.getAllSms(where);
    }

   /* public void housekeep() {
        MessagingAgingMethod method = MessagingAgingMethod.get(AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_MESSAGING_AGING_METHOD, getContext().getString(R.string.default_messaging_aging_method)));
        if (method == MessagingAgingMethod.DAYS) {
            int days = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_MESSAGING_AGING_DAYS, Integer.valueOf(getContext().getString(R.string.default_messaging_aging_days)));
            smsHelper.deleteOldSms(days);
        } else {
            int totalRecords = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_MESSAGING_AGING_SIZE, Integer.valueOf(getContext().getString(R.string.default_messaging_aging_size)));
            smsHelper.deleteOldSmsByNumber(totalRecords);
        }
    }*/

    /*
     * Restores the SMS information from the database.
     *
     * Creates the smsMap object and fills it if there are any old SMS from the database.
     *
     */
    private void restoreSmsInformation() {
        if (!keyValueHelper.containsKey(KeyValueHelper.KEY_SINTENT)) {
            keyValueHelper.addKey(KeyValueHelper.KEY_SINTENT, "0");
        }
        if (!keyValueHelper.containsKey(KeyValueHelper.KEY_DINTENT)) {
            keyValueHelper.addKey(KeyValueHelper.KEY_DINTENT, "0");
        }

    }

    private String getSmsId() {
        return EntityUtils.generateUniqueId();
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
        if (getRequest.getMessages() == null || (getRequest.getMessages().size() < 300))
            return gson.toJson(getRequest);

        // Use the streaming method
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        try {

            //writer.setIndent("  ");
            //writer.beginArray();
            //jsonWriter.beginObject();
            // jsonWriter.name(type.getName());
            gson.toJson(getRequest, GetRequest.class, jsonWriter);
            //writer.endArray();
            //jsonWriter.endObject();
            return stringWriter.toString();
        } catch (JsonIOException ioEx) {
            LOGE(TAG, "[toJson] Error generating JSON output", ioEx);
        } finally {
            try {
                jsonWriter.close();
            } catch (IOException ioEx){
                LOGE(TAG, "[toJson] Error closing JSON writer", ioEx);
            }
        }
        return "";
    }
}
