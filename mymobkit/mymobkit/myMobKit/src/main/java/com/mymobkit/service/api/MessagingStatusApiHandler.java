package com.mymobkit.service.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mymobkit.R;
import com.mymobkit.app.AppConfig;
import com.mymobkit.data.SmsHelper;
import com.mymobkit.enums.MessageStatus;
import com.mymobkit.net.AppServer;
import com.mymobkit.receiver.SmsPendingIntentReceiver;
import com.mymobkit.service.HttpdService;
import com.mymobkit.service.api.sms.GetStatusRequest;
import com.mymobkit.service.api.sms.Sms;

import java.util.Map;

public final class MessagingStatusApiHandler extends ApiHandler {

	private SmsHelper smsHelper;

	public MessagingStatusApiHandler(final HttpdService service) {
		super(service);
		smsHelper = SmsHelper.getSmsHelper(getContext());
	}

	@Override
	public String get(final Map<String, String> header, final Map<String, String> params, final Map<String, String> files) {
		GetStatusRequest request = new GetStatusRequest();
		Sms msg = null;
		String id = "";
		try {
			maybeAcquireWakeLock();
			if (params.containsKey(AppServer.URI_PARAM_PREFIX + "0")) {
				// View a particular SMS
				id = getStringValue(AppServer.URI_PARAM_PREFIX + "0", params, "-1");
				msg = smsHelper.getSms(id);
			}
			request.setMessage(msg);
			if (msg == null) {
				String desc = String.format(getContext().getString(R.string.msg_no_matched_msg), id);
				request.setDescription(desc);
				request.isSuccessful = false;
			} else {
				// Set the correct message status
				if (msg.delIntentsComplete()) {
					if (msg.getDelIntentResult() == SmsPendingIntentReceiver.SUCCESS_STATUS) {
						request.setStatus(MessageStatus.DELIVERED.getHashCode());
					} else if (msg.getDelIntentResult() != SmsPendingIntentReceiver.IN_PROGRESS) {
						request.setStatus(MessageStatus.FAILED.getHashCode());
						request.setDescription(msg.getDelIntentResult() + "");
					}
				} else if (msg.sentIntentsComplete()) {
					if (msg.getSentIntentResult() == SmsPendingIntentReceiver.SUCCESS_STATUS) {
						request.setStatus(MessageStatus.SENT.getHashCode());
					} else if (msg.getSentIntentResult() != SmsPendingIntentReceiver.IN_PROGRESS) {
						request.setStatus(MessageStatus.FAILED.getHashCode());
						request.setDescription(msg.getSentIntentResult() + "");
					}
				} else {
					request.setStatus(MessageStatus.QUEUED.getHashCode());
				}
			}
		} finally {
			releaseWakeLock();
		}
		Gson gson = new GsonBuilder().setDateFormat(AppConfig.UNIVERSAL_DATE_FORMAT).excludeFieldsWithoutExposeAnnotation().create();
		return gson.toJson(request);
	}

	@Override
	public void stop() {
		super.stop();
		smsHelper = null;
	}
}
