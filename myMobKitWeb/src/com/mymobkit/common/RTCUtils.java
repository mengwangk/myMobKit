package com.mymobkit.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.mymobkit.model.Message;
import com.mymobkit.model.MessageInfo;
import com.mymobkit.model.ModelBase;
import com.mymobkit.model.Room;

public final class RTCUtils {

	private static final Logger logger = Logger.getLogger(RTCUtils.class.getName());

	public static String generateRandom(int length) {
		return RandomStringUtils.random(length, "0123456789");
	}

	public static String sanitize(String key) {
		if (StringUtils.isEmpty(key))
			return StringUtils.EMPTY;
		return StringUtils.replace(key, "[^a-zA-Z0-9\\-]", "-");
	}

	public static String makeClientId(Room room, String user) {
		return room.getKeyName() + "/" + user;
	}

	public static String getDefaultStunServer(String userAgent) {
		String defaultStunServer = "stun.l.google.com:19302";
		if (StringUtils.containsIgnoreCase(userAgent, "Firefox")) {
			defaultStunServer = "stun.services.mozilla.com";
		}
		return defaultStunServer;
	}

	public static String getPreferredAudioReceiveCodec() {
		return "opus/48000";
	}

	public static String getPreferredAudioSendCodec(String userAgent) {
		// Empty string means no preference
		String preferredAudioSendCodec = StringUtils.EMPTY;

		// Prefer to send ISAC on Chrome for Android.
		if (StringUtils.containsIgnoreCase(userAgent, "Android") && StringUtils.containsIgnoreCase(userAgent, "Chrome")) {
			preferredAudioSendCodec = "ISAC/16000";
		}
		return preferredAudioSendCodec;
	}

	public static String makePcConfig(String stunServer, String turnServer, String tsPwd) {
		String servers = StringUtils.EMPTY;
		if (StringUtils.isNotBlank(turnServer)) {
			String turnConfig = "\"turn:" + turnServer + "\"";
			servers = "{\"url\": " + turnConfig + ", \"credential\":\"" + StringUtils.trimToEmpty(tsPwd) + "\"}";
		}

		if (StringUtils.isNotBlank(stunServer)) {
			String stunConfig = "\"stun:" + stunServer + "\"";
			if (StringUtils.isNotBlank(turnServer)) {
				servers += ", ";
			}
			servers += "{\"url\": " + stunConfig + "}";
		}
		return "{\"iceServers\": " + "[" + servers + "]" + "}";
	}

	public static String createChannel(Room room, String user, int durationMinutes) {
		String clientId = makeClientId(room, user);
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		return channelService.createChannel(clientId, durationMinutes);
	}

	public static String makeLoopbackAnswer(String message) {
		message = StringUtils.replace(message, "\"offer\"", "\"answer\"");
		message = StringUtils.replace(message, "a=ice-options:google-ice\\r\\n", "");
		return message;
	}

	public static String makeMediaTrackConstraints(String constraintsString) {
		String trackConstraint = StringUtils.EMPTY;
		if (StringUtils.isBlank(constraintsString) || StringUtils.equalsIgnoreCase(constraintsString, "true")) {
			trackConstraint = "true";
		} else if (StringUtils.equalsIgnoreCase(constraintsString, "false")) {
			trackConstraint = "false";
		} else {
			// {"audio": {"optional": [{"goog1": "abx"}, {"goog2": "sss"}],
			// "mandatory": {"xyz": "dd", "abc": "afsd"}}, "video": true};
			List<String> optionalList = new ArrayList<String>(1);
			List<String> mandatoryList = new ArrayList<String>(1);
			for (String constraintString : constraintsString.split(",")) {
				String[] constraint = constraintString.split("=");
				if (constraint.length != 2) {
					logger.log(Level.WARNING, "Ignoring malformed constraint: " + constraintString);
					continue;
				}
				if (constraint[0].startsWith("goog")) {
					optionalList.add("{\"" + constraint[0] + "\": \"" + constraint[1] + "\"}");
				} else {
					mandatoryList.add("\"" + constraint[0] + "\": \"" + constraint[1] + "\"");
				}
			}

			trackConstraint = "{\"optional\": [" + StringUtils.join(optionalList, ",") + "], \"mandatory\": {" + StringUtils.join(mandatoryList, ",") + "}}";
		}
		return trackConstraint;
	}

	public static String makeMediaStreamConstraints(String audio, String video) {
		String streamConstraints = "{\"audio\": " + makeMediaTrackConstraints(audio) + ", " +
				"\"video\": " + makeMediaTrackConstraints(video) + "}";

		return streamConstraints;
	}

	public static String maybeAddConstraint(String constraints, String param, String constraint){
		if (StringUtils.equalsIgnoreCase(param, "true")) {
			constraints += ", {\"" + constraint + "\": true}";
		} else {
			constraints += ", {\"" + constraint + "\": false}";
		}
		return constraints;
	}
	
	public static String makePcConstraints(String dtls, String dscp,  String ipv6) {
		String constraints = StringUtils.EMPTY;
		// For interop with FireFox. Enable DTLS in peerConnection ctor.
		if (StringUtils.equalsIgnoreCase(dtls, "true")) {
			constraints = "{\"optional\": [{\"DtlsSrtpKeyAgreement\": true}, {\"RtpDataChannels\": true}";
		} else {
			// Disable DTLS in peerConnection ctor for loopback call. The value
			// of compat is false for loopback mode.
			constraints = "{\"optional\": [{\"DtlsSrtpKeyAgreement\": true}, {\"RtpDataChannels\": false}";
		}
		
		constraints = maybeAddConstraint(constraints, dscp, "googDscp");
		constraints = maybeAddConstraint(constraints, dscp, "googIPv6");
		
		constraints += "]}";
		return constraints;
	}

	public static String makeOfferConstraints() {
		String constraints = "{ \"mandatory\": {}, \"optional\": [] }";
		return constraints;
	}

	public static List<Entity> getSavedMessages(String clientId) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Filter clientIdFilter = new FilterPredicate("clientId", FilterOperator.EQUAL, clientId);
		Query q = new Query(Message.KIND_MESSAGE).setFilter(clientIdFilter);
		PreparedQuery pq = datastore.prepare(q);
		return pq.asList(FetchOptions.Builder.withDefaults());
	}

	public static void deleteSavedMessages(String clientId) {
		List<Entity> messages = getSavedMessages(clientId);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		for (Entity msg : messages) {
			datastore.delete(msg.getKey());
		}
		logger.info("Deleted the saved message for " + clientId);
	}

	public static void sendSavedMessages(String clientId) {
		List<Entity> messages = getSavedMessages(clientId);
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		for (Entity msg : messages) {
			Message msgObj = ModelBase.fromEntity(msg, Message.class);
			channelService.sendMessage(new ChannelMessage(clientId, msgObj.getMessage()));
			logger.info("Delivered saved message to " + clientId);
			msgObj.delete();
		}
	}

	public static void onMessage(Room room, String user, String message) {
		String clientId = makeClientId(room, user);
		if (room.isConnected(user)) {
			ChannelService channelService = ChannelServiceFactory.getChannelService();
			channelService.sendMessage(new ChannelMessage(clientId, message));
			//logger.info("Delivered message to user " + user);
		} else {
			// Save the message
			Message newMessage = new Message();
			newMessage.setClientId(clientId);
			newMessage.setMessage(message);
			newMessage.save();
			//logger.info("Saved message for user " + user);
		}
	}

	public static void handleMessage(Room room, String user, String message) {
		MessageInfo msgObj = Message.fromJson(message);
		String otherUser = room.getOtherUser(user);
		String roomKey = room.getKeyName();

		if (StringUtils.equalsIgnoreCase(msgObj.getType(), MessageInfo.MSG_TYPE_BYE)) {
			// This would remove the other_user in loopback test too.
			// So check its availability before forwarding Bye message.
			room.removeUser(user);
			logger.info("User " + user + " quit from room " + roomKey);
			logger.info("Room " + roomKey + " has state " + room.toString());
		}

		if (StringUtils.isNotBlank(otherUser) && room.hasUser(otherUser)) {
			if (StringUtils.equalsIgnoreCase(msgObj.getType(), MessageInfo.MSG_TYPE_OFFER)) {
				// Special case the loopback scenario.
				if (StringUtils.equalsIgnoreCase(user, otherUser)) {
					message = makeLoopbackAnswer(message);
				}				
			}
			onMessage(room, otherUser, message);
		} else {
			// for unit test
			onMessage(room, user, message);
		}
	}

	public static int getRange(String value, int minValue, int maxValue, int defaultValue) {
		Integer convertedValue = NumberUtils.createInteger(value);
		if (convertedValue == null || convertedValue < minValue || convertedValue > maxValue)
			convertedValue = defaultValue;
		return convertedValue;
	}

	public static String appendUrlArguments(HttpServletRequest request, String link) {
		Map params = request.getParameterMap();
		Iterator i = params.keySet().iterator();
		while (i.hasNext())
		{
			String key = (String) i.next();
			if (!StringUtils.equalsIgnoreCase(key, "r") && !StringUtils.equalsIgnoreCase(key, "w") && !StringUtils.equalsIgnoreCase(key, "m")) {
				String value = ((String[]) params.get(key))[0];
				link += "&" + StringEscapeUtils.escapeHtml4(key) + "=" + StringEscapeUtils.escapeHtml4(value);
			}
		}
		return link;
	}
}
