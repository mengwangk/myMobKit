package com.mymobkit.service;

import static com.mymobkit.datastore.OfyService.ofy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.inject.Inject;
import com.googlecode.objectify.TxnType;
import com.mymobkit.common.AppConfig;
import com.mymobkit.model.Device;
import com.mymobkit.model.MulticastMessage;
import com.mymobkit.service.message.DeviceInfo;
import com.mymobkit.service.message.DeviceStatus;
import com.mymobkit.service.message.ResponseCode;
import com.mymobkit.service.message.SendStatus;
import com.mymobkit.util.txn.Transact;

@Path("/device")
@Slf4j
public class DeviceAction {

	// Time to live for the message
	private static final int TTL = (int) TimeUnit.MINUTES.toSeconds(300);

	private static final String PARAMETER_REG_ID = "registrationId";
	private static final String PARAMETER_REG_VERSION = "registrationVersion";
	private static final String PARAMETER_EMAIL = "email";
	private static final String PARAMETER_DEVICE_ID = "deviceId";
	private static final String PARAMETER_DEVICE_NAME = "deviceName";
	private static final String PARAMETER_ACTION = "action";
	private static final String PARAMETER_EXTRA_DATA = "extraData";

	private static final String HEADER_QUEUE_COUNT = "X-AppEngine-TaskRetryCount";
	private static final String HEADER_QUEUE_NAME = "X-AppEngine-QueueName";
	private static final int MAX_RETRY = 3;

	private static final String PARAMETER_MULTICAST_KEY = "multicastKey";

	private static final String GCM_QUEUE_NAME = "gcm";

	private static final int MULTICAST_SIZE = 1000;

	@Inject
	HttpServletRequest request;

	@Inject
	HttpServletResponse response;

	@Inject
	ServletContext context;

	private Sender sender;

	/**
	 * Register the device.
	 * 
	 * @param registrationId
	 * @param registrationVersion
	 * @param email
	 * @param deviceId
	 * @param deviceName
	 * @return
	 */
	@POST
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Transact(TxnType.REQUIRED)
	public DeviceStatus register(@FormParam(PARAMETER_REG_ID) String registrationId, @FormParam(PARAMETER_REG_VERSION) String registrationVersion, @FormParam(PARAMETER_EMAIL) String email,
			@FormParam(PARAMETER_DEVICE_ID) String deviceId, @FormParam(PARAMETER_DEVICE_NAME) String deviceName) {

		final DeviceStatus deviceStatus = new DeviceStatus();

		log.info("registration id: " + registrationId);
		log.info("registration version: " + registrationVersion);
		log.info("email: " + email);
		log.info("device id: " + deviceId);
		log.info("device name: " + deviceName);

		if (StringUtils.isBlank(registrationId) || StringUtils.isBlank(registrationVersion) || StringUtils.isBlank(email) || StringUtils.isBlank(deviceId) || StringUtils.isBlank(deviceName)) {
			deviceStatus.setResponseCode(ResponseCode.REGISTER_FAILURE.getCode());
			return deviceStatus;
		}

		// Check if existing registration exists for this device
		Device device = ofy().load().type(Device.class).id(deviceId).now();
		if (device == null) {
			// first time registration
			device = new Device(deviceId, deviceName, email, registrationId, registrationVersion);
		} else {
			device.setDeviceName(deviceName);
			device.setEmail(email);
			device.setRegId(registrationId);
			device.setRegVersion(registrationVersion);
		}
		ofy().save().entities(device).now();
		deviceStatus.setResponseCode(ResponseCode.DEVICE_REGISTERED.getCode());
		return deviceStatus;
	}

	/**
	 * Unregister device.
	 * 
	 * @param registrationId
	 * @param registrationVersion
	 * @param email
	 * @param deviceId
	 * @return
	 */
	@POST
	@Path("/unregister")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public DeviceStatus unregister(@FormParam(PARAMETER_REG_ID) String registrationId, @FormParam(PARAMETER_REG_VERSION) String registrationVersion, @FormParam(PARAMETER_EMAIL) String email,
			@FormParam(PARAMETER_DEVICE_ID) String deviceId, @FormParam(PARAMETER_DEVICE_NAME) String deviceName) {

		final DeviceStatus deviceStatus = new DeviceStatus();
		if (StringUtils.isBlank(registrationId) || StringUtils.isBlank(deviceId)) {
			deviceStatus.setResponseCode(ResponseCode.UNREGISTER_FAILURE.getCode());
			return deviceStatus;
		}
		log.debug("Unregistering device with id " + deviceId);

		unregister(deviceId, registrationId);

		deviceStatus.setResponseCode(ResponseCode.DEVICE_UNREGISTERED.getCode());
		return deviceStatus;
	}

	@GET
	@Path("/group/{email}/{deviceId}")
	@Produces(MediaType.APPLICATION_JSON)
	public DeviceInfo group(@PathParam("email") String email, @PathParam("deviceId") String deviceId) {

		allowCORS();
		
		final DeviceInfo deviceInfo = new DeviceInfo();

		if (StringUtils.isBlank(email) || StringUtils.isBlank(deviceId)) {
			deviceInfo.setResponseCode(ResponseCode.DEVICE_NOT_FOUND.getCode());
			return deviceInfo;
		}

		// Send to all devices associated with this email
		List<Device> devices = ofy().load().type(Device.class).filter("email =", email).list();
		if (devices == null || devices.size() == 0) {
			// No devices found
			deviceInfo.setResponseCode(ResponseCode.DEVICE_NOT_FOUND.getCode());
			return deviceInfo;
		}

		// Check if authorized
		for (Device device : devices) {
			if (deviceId.equals(device.getDeviceId())) {
				deviceInfo.setDevices(devices);
				deviceInfo.setResponseCode(ResponseCode.SUCCESS.getCode());
				return deviceInfo;
			}
		}

		// Not authorized
		deviceInfo.setResponseCode(ResponseCode.NOT_AUTHORIZED.getCode());
		return deviceInfo;
	}

	
	@POST
	@Path("/queue")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public SendStatus queue(@FormParam(PARAMETER_REG_ID) String registrationId, @FormParam(PARAMETER_REG_VERSION) String registrationVersion, @FormParam(PARAMETER_EMAIL) String email,
			@FormParam(PARAMETER_DEVICE_ID) String deviceId, @FormParam(PARAMETER_ACTION) String action, @FormParam(PARAMETER_EXTRA_DATA) String extraData) {

		// log.error("Device id -------- " + deviceId);
		// log.error("Email --------- " + email);
		// log.error("Action --------- " + action);

		final SendStatus sendStatus = new SendStatus();
		if ((StringUtils.isBlank(deviceId) && StringUtils.isBlank(email)) || StringUtils.isBlank(action)) {
			sendStatus.setResponseCode(ResponseCode.SEND_FAILURE.getCode());
			return sendStatus;
		}

		final Queue queue = QueueFactory.getQueue(GCM_QUEUE_NAME);
		if (StringUtils.isNotBlank(deviceId)) {
			// Send to this device
			final Device device = ofy().load().type(Device.class).id(deviceId).now();
			if (device != null) {
				final TaskOptions taskOptions = TaskOptions.Builder.withUrl("/service/device/send").param(PARAMETER_DEVICE_ID, device.getDeviceId()).param(PARAMETER_REG_ID, device.getRegId())
						.param(PARAMETER_ACTION, action).param(PARAMETER_EXTRA_DATA, extraData).method(Method.POST);
				queue.add(taskOptions);
				sendStatus.setResponseCode(ResponseCode.MESSAGE_QUEUED.getCode());
			}
		} else {
			if (StringUtils.isNotBlank(email)) {
				// Send to all devices associated with this email
				List<Device> devices = ofy().load().type(Device.class).filter("email =", email).list();
				if (devices == null || devices.size() == 0) {
					// No devices found
					sendStatus.setResponseCode(ResponseCode.DEVICE_NOT_FOUND.getCode());
					return sendStatus;
				}
				if (devices.size() == 1) { // Only 1 device
					final Device device = devices.get(0);
					final TaskOptions taskOptions = TaskOptions.Builder.withUrl("/service/device/send").param(PARAMETER_DEVICE_ID, device.getDeviceId()).param(PARAMETER_REG_ID, device.getRegId())
							.param(PARAMETER_ACTION, action).param(PARAMETER_EXTRA_DATA, extraData).method(Method.POST);
					queue.add(taskOptions);
					sendStatus.setResponseCode(ResponseCode.MESSAGE_QUEUED.getCode());
				} else { // 2 or more
					// send a multicast message using JSON
					// must split in chunks of 1000 devices (GCM limit)
					int total = devices.size();
					List<Device> partialDevices = new ArrayList<Device>(total);
					int counter = 0;
					for (Device device : devices) {
						counter++;
						partialDevices.add(device);
						int partialSize = partialDevices.size();
						if (partialSize == MULTICAST_SIZE || counter == total) {
							Long multicastKey = createMulticast(partialDevices, action, extraData);
							log.info("Queuing " + partialSize + " devices on multicast " + multicastKey);
							TaskOptions taskOptions = TaskOptions.Builder.withUrl("/service/device/send").param(PARAMETER_MULTICAST_KEY, String.valueOf(multicastKey)).param(PARAMETER_ACTION, action)
									.param(PARAMETER_EXTRA_DATA, extraData).method(Method.POST);
							queue.add(taskOptions);
							partialDevices.clear();
						}
					}
					sendStatus.setResponseCode(ResponseCode.MESSAGE_QUEUED.getCode());
				}
			}
		}
		return sendStatus;
	}

	@POST
	@Path("/send")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public SendStatus send(@FormParam(PARAMETER_DEVICE_ID) String deviceId, @FormParam(PARAMETER_REG_ID) String registrationId, @FormParam(PARAMETER_MULTICAST_KEY) String multicastKey,
			@FormParam(PARAMETER_ACTION) String action, @FormParam(PARAMETER_EXTRA_DATA) String extraData) {

		final SendStatus sendStatus = new SendStatus();
		if (request.getHeader(HEADER_QUEUE_NAME) == null) {
			sendStatus.setResponseCode(ResponseCode.SEND_FAILURE.getCode());
			return sendStatus;
		}
		final String retryCountHeader = request.getHeader(HEADER_QUEUE_COUNT);
		log.debug("Retry count: " + retryCountHeader);
		if (retryCountHeader != null) {
			int retryCount = Integer.parseInt(retryCountHeader);
			if (retryCount > MAX_RETRY) {
				log.error("Too many retries, dropping task");
				taskDone();
				sendStatus.setResponseCode(ResponseCode.SEND_FAILURE.getCode());
				return sendStatus;
			}
		}

		if (StringUtils.isNotBlank(multicastKey)) {
			// Multicast message
			sendMulticastMessage(multicastKey, action, extraData, sendStatus);
		} else {
			if (StringUtils.isNotBlank(deviceId) && StringUtils.isNotBlank(registrationId)) {
				sendSingleMessage(deviceId, registrationId, action, extraData, sendStatus);
			} else {
				sendStatus.setResponseCode(ResponseCode.SEND_FAILURE.getCode());
				return sendStatus;
			}
		}
		return sendStatus;
	}

	/**
	 * Creates the {@link Sender} based on the servlet settings.
	 */
	protected Sender getSender() {
		if (this.sender != null)
			return sender;
		String key = (String) context.getAttribute(AppConfig.ATTRIBUTE_GCM_KEY);
		return new Sender(key);
	}

	protected Long createMulticast(final List<Device> devices, final String action, final String extraData) {
		log.info("Storing multicast for " + devices.size() + " devices");
		final MulticastMessage multicastMessage = new MulticastMessage();
		multicastMessage.setAction(action);
		multicastMessage.setExtraData(extraData);
		multicastMessage.setDevices(devices);
		ofy().save().entities(multicastMessage).now();
		log.debug("multicast key: " + multicastMessage.getId());
		return multicastMessage.getId();
	}

	private Message createMessage(final String action, final String extraData) {
		log.debug("Creating message with key value pair");
		Message.Builder builder = new Message.Builder().delayWhileIdle(true);
		if (action == null || action.length() == 0) {
			throw new IllegalArgumentException("Message action cannot be empty.");
		}
		builder.collapseKey(action).addData(PARAMETER_ACTION, action).addData(PARAMETER_EXTRA_DATA, extraData).timeToLive(TTL);
		Message gcmMessage = builder.build();
		log.debug("done with message");
		return gcmMessage;
	}

	private void sendSingleMessage(final String deviceId, final String registrationId, final String action, final String extraData, final SendStatus sendStatus) {
		log.debug("Sending message to device " + registrationId);
		Message gcmMessage = createMessage(action, extraData);
		Result result;
		try {
			result = getSender().sendNoRetry(gcmMessage, registrationId);
		} catch (IOException e) {
			log.error("Exception sending message [" + action + "] to registration id [" + registrationId + "]", e);
			sendStatus.setResponseCode(ResponseCode.SEND_FAILURE.getCode());
			taskDone();
			return;
		}
		if (result == null) {
			retryTask();
			sendStatus.setResponseCode(ResponseCode.SEND_FAILURE.getCode());
			return;
		}
		if (result.getMessageId() != null) {
			log.debug("Succesfully sent message to device " + registrationId);
			String canonicalRegId = result.getCanonicalRegistrationId();
			if (canonicalRegId != null) {
				// same device has more than on registration id: update it
				log.debug("Canonical registration id " + canonicalRegId);
				updateRegistration(deviceId, registrationId, canonicalRegId);
			}
			sendStatus.setResponseCode(ResponseCode.SUCCESS.getCode());
		} else {
			final String error = result.getErrorCodeName();
			if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
				// application has been removed from device - unregister it
				unregister(deviceId, registrationId);
			} else {
				log.error("Error sending message to device " + registrationId + ": " + error);
			}
			sendStatus.setResponseCode(ResponseCode.SEND_FAILURE.getCode());
		}
	}

	private void sendMulticastMessage(final String multicastKey, final String action, final String extraData, final SendStatus sendStatus) {
		// Get the multicast message from device
		final MulticastMessage multicastMessage = ofy().load().type(MulticastMessage.class).id(Long.valueOf(multicastKey)).now();
		if (multicastMessage == null) {
			sendStatus.setResponseCode(ResponseCode.SEND_FAILURE.getCode());
			return;
		}
		final List<Device> devices = multicastMessage.getDevices();
		final List<String> regIds = new ArrayList<String>(2);
		for (Device device : devices) {
			regIds.add(device.getRegId());
		}
		Message gcmMessage = createMessage(action, extraData);
		MulticastResult multicastResult;
		try {
			multicastResult = getSender().sendNoRetry(gcmMessage, regIds);
		} catch (IOException e) {
			log.error("[sendMulticastMessage] Error sending message", e);
			sendStatus.setResponseCode(ResponseCode.SEND_FAILURE.getCode());
			multicastDone(multicastKey);
			return;
		}
		boolean allDone = true;
		// check if any registration id must be updated
		if (multicastResult.getCanonicalIds() != 0) {
			List<Result> results = multicastResult.getResults();
			for (int i = 0; i < results.size(); i++) {
				String canonicalRegId = results.get(i).getCanonicalRegistrationId();
				if (canonicalRegId != null) {
					final String deviceId = devices.get(i).getDeviceId();
					final String regId = devices.get(i).getRegId();
					updateRegistration(deviceId, regId, canonicalRegId);
				}
			}
		}
		if (multicastResult.getFailure() != 0) {
			// there were failures, check if any could be retried
			final List<Result> results = multicastResult.getResults();
			final List<Device> retriabDevices = new ArrayList<Device>();
			for (int i = 0; i < results.size(); i++) {
				final String error = results.get(i).getErrorCodeName();
				if (error != null) {
					final String deviceId = devices.get(i).getDeviceId();
					final String regId = devices.get(i).getRegId();
					log.warn("Got error (" + error + ") for regId " + regId);
					if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
						// application has been removed from device - unregister it
						unregister(deviceId, regId);
					}
					if (error.equals(Constants.ERROR_UNAVAILABLE)) {
						final Device device = devices.get(i);
						device.setRegId(regId);
						retriabDevices.add(device);
					}
				}
			}
			if (!retriabDevices.isEmpty()) {
				// update task
				updateMulticast(multicastKey, retriabDevices);
				allDone = false;
				retryTask();
			}
		}
		if (allDone) {
			multicastDone(multicastKey);
			sendStatus.setResponseCode(ResponseCode.SUCCESS.getCode());
		} else {
			retryTask();
			sendStatus.setResponseCode(ResponseCode.SEND_FAILURE.getCode());
		}
	}

	/**
	 * Indicates to App Engine that this task should be retried.
	 */
	private void retryTask() {
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}

	/**
	 * Updates a persistent record with the devices to be notified using a multicast message.
	 *
	 * @param encodedKey
	 *            encoded key for the persistent record.
	 * @param devices
	 *            new list of registration ids of the devices.
	 */
	public static void updateMulticast(String encodedKey, List<Device> devices) {
		// Get the multicast message
		final MulticastMessage multicastMessage = ofy().load().type(MulticastMessage.class).id(Long.valueOf(encodedKey)).now();
		if (multicastMessage == null) {
			log.error("No entity for key " + encodedKey);
			return;
		}
		multicastMessage.setDevices(devices);
		ofy().save().entities(multicastMessage).now();
	}

	private void multicastDone(final String encodedKey) {
		ofy().delete().type(MulticastMessage.class).id(Long.valueOf(encodedKey)).now();
		taskDone();
	}

	private void updateRegistration(final String deviceId, final String oldId, final String newId) {
		log.debug("Updating " + oldId + " to " + newId);
		// Check if existing registration exists for this device
		Device device = ofy().load().type(Device.class).id(deviceId).now();
		if (device == null) {
			log.warn("No device for registration id " + oldId);
			return;
		} else {
			device.setRegId(newId);
		}
		ofy().save().entities(device).now();
	}

	/**
	 * Unregister the device using the device id.
	 * 
	 * @param deviceId
	 * @param registrationId
	 */
	private void unregister(final String deviceId, final String registrationId) {
		// Check if existing registration exists for this device
		Device device = ofy().load().type(Device.class).id(deviceId).now();
		if (device != null) {
			ofy().delete().type(Device.class).id(deviceId).now();
		}
	}

	/**
	 * Indicates to App Engine that this task is done.
	 */
	private void taskDone() {
		response.setStatus(HttpServletResponse.SC_OK);
	}

	private void allowCORS() {
		
		// Allow CORS requests from any domain
		response.addHeader("Access-Control-Allow-Origin", "*");

		// Prevent frame hijacking
		response.addHeader("X-FRAME-OPTIONS", "DENY");
	}
}
