package com.mymobkit.service.api;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mymobkit.R;
import com.mymobkit.app.AppConfig;
import com.mymobkit.common.GcmUtils;
import com.mymobkit.data.NotificationMsgHelper;
import com.mymobkit.gcm.GcmMessage;
import com.mymobkit.gcm.message.ServiceMessage;
import com.mymobkit.gcm.message.SurveillanceMessage;
import com.mymobkit.gcm.message.SwitchCameraMessage;
import com.mymobkit.model.NotificationMsg;
import com.mymobkit.service.HttpdService;
import com.mymobkit.service.api.gcm.DeleteRequest;
import com.mymobkit.service.api.gcm.GetRequest;
import com.mymobkit.service.api.gcm.PostRequest;

import java.util.List;
import java.util.Map;

import static com.mymobkit.common.LogUtils.makeLogTag;

public final class GcmApiHandler extends ApiHandler {

    private static final String TAG = makeLogTag(GcmApiHandler.class);

    public static final String PARAM_DEVICE_ID = "DeviceId";
    public static final String PARAM_ACTION_TYPE = "ActionType";
    public static final String PARAM_ACTION_COMMAND = "ActionCommand";
    public static final String PARAM_ACTION_COMMAND_VALUE = "ActionCommandValue";

    private NotificationMsgHelper messageHelper;

    /**
     * Constructor.
     *
     * @param service HTTPD service.
     */
    public GcmApiHandler(final HttpdService service) {
        super(service);
        this.messageHelper = NotificationMsgHelper.getNotificationMsgHelper(getContext());
    }


    @Override
    public String get(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
        GetRequest getRequest = new GetRequest();
        List<NotificationMsg> results = null;
        try {
            maybeAcquireWakeLock();
            results = this.messageHelper.getAllMsgs();
            if (results == null || results.size() == 0) {
                String desc = getContext().getString(R.string.msg_no_gcm_message);
                getRequest.setDescription(desc);
                getRequest.isSuccessful = false;
            } else {
                getRequest.isSuccessful = true;
            }
            getRequest.setMessages(results);
        } finally {
            releaseWakeLock();
        }
        //Gson gson = new GsonBuilder().setDateFormat(AppConfig.UNIVERSAL_DATE_FORMAT).excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(getRequest);
    }

    @Override
    public String delete(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
        final DeleteRequest request = new DeleteRequest();
        int deletedRows = 0;
        try {
            maybeAcquireWakeLock();
            String msg = "";
            int result = messageHelper.purge();
            if (result >= 0) {
                deletedRows += result;
                request.isSuccessful = true;
            } else {
                request.isSuccessful = false;
                msg = getContext().getString(R.string.msg_gcm_deleted_notif_failure);
                request.setDescription(msg);
            }
        } finally {
            releaseWakeLock();
        }
        request.setCount(deletedRows);
        //Gson gson = new GsonBuilder().setDateFormat(AppConfig.UNIVERSAL_DATE_FORMAT).excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(request);
    }

    @Override
    public String post(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
        final PostRequest request = new PostRequest();
        try {
            maybeAcquireWakeLock();

            final String deviceId = getStringValue(PARAM_DEVICE_ID, params);
            final String actionType = getStringValue(PARAM_ACTION_TYPE, params);
            final String actionCommand = getStringValue(PARAM_ACTION_COMMAND, params);
            final String actionCommandValue = getStringValue(PARAM_ACTION_COMMAND_VALUE, params);

            if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(actionType) || TextUtils.isEmpty(actionCommand)) {
                request.isSuccessful = false;
                request.setDescription(getContext().getString(R.string.msg_gcm_invalid_request));
            } else {

                final GcmMessage.ActionType gcmActionType = GcmMessage.ActionType.get(actionType);
                GcmMessage gcmMessage = null;
                if (gcmActionType == GcmMessage.ActionType.SERVICE) {
                    ServiceMessage serviceMessage = new ServiceMessage(getContext());
                    serviceMessage.setActionCommand(actionCommand);
                    gcmMessage = serviceMessage;
                } else if (gcmActionType == GcmMessage.ActionType.SURVEILLANCE) {
                    SurveillanceMessage surveillanceMessage = new SurveillanceMessage(getContext());
                    surveillanceMessage.setActionCommand(actionCommand);
                    gcmMessage = surveillanceMessage;
                } else if (gcmActionType == GcmMessage.ActionType.SWITCH_CAMERA) {
                    SwitchCameraMessage switchCameraMessage = new SwitchCameraMessage(getContext());
                    switchCameraMessage.setActionCommand(actionCommand);
                    gcmMessage = switchCameraMessage;
                }
                if (gcmMessage != null) {
                    // Use the GCM sender helper class to send
                    final GcmUtils.RegistrationStatus regStatus = GcmUtils.getRegistrationStatus(getContext());
                    if (regStatus == GcmUtils.RegistrationStatus.REGISTERED) {
                        GcmUtils.send(deviceId, gcmMessage);
                        request.isSuccessful = true;
                    } else {
                        request.isSuccessful = false;
                        request.setDescription(getContext().getString(R.string.msg_gcm_device_not_registered));
                    }
                } else {
                    request.isSuccessful = false;
                    request.setDescription(getContext().getString(R.string.msg_gcm_invalid_request));
                }
            }
        } finally {
            releaseWakeLock();
        }
        //Gson gson = new GsonBuilder().setDateFormat(AppConfig.UNIVERSAL_DATE_FORMAT).excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(request);
    }
}
