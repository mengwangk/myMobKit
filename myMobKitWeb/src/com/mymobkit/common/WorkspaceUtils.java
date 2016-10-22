package com.mymobkit.common;

import static com.mymobkit.datastore.OfyService.ofy;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mymobkit.model.LoginUser;
import com.mymobkit.model.Message;
import com.mymobkit.model.MessageInfo;
import com.mymobkit.model.WMessage;
import com.mymobkit.model.WSession;
import com.mymobkit.model.Workspace;

public final class WorkspaceUtils {

	private static final Logger logger = Logger.getLogger(WorkspaceUtils.class.getName());

	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
	public static final String PARAMETER_SEPARATOR = "/";

	public static Date currentDate() {
		Calendar cal = Calendar.getInstance();
		return cal.getTime();
	}

	public static String currentDateString() {
		Calendar cal = Calendar.getInstance();
		return DATE_FORMAT.format(cal.getTime());
	}

	public static String makeClientId(final LoginUser user, final Workspace workspace, final WSession wSession) {
		return user.getNormalizedEmail() + PARAMETER_SEPARATOR + workspace.getId() + PARAMETER_SEPARATOR + wSession.getId();
	}

	public static String createChannel(final LoginUser user, final Workspace workspace, final WSession wSession, int durationMinutes) {
		String clientId = makeClientId(user, workspace, wSession);
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		return channelService.createChannel(clientId, durationMinutes);
	}

	public static List<WMessage> getSavedMessages(final Workspace workspace, final WSession wSession) {
		// Get list of messages for this workspace
		List<WMessage> allMessages = ofy().load().type(WMessage.class).ancestor(workspace).list();
		if (allMessages != null && allMessages.size() > 0) {
			// Get the messages not sent to the sessions
			Iterable<WMessage> unsentMessages = Iterables.filter(allMessages, new Predicate<WMessage>(){  
			    public boolean apply(WMessage m) {  
			        return m.getMsgTimestamp().after(wSession.getTimestamp());
			    }  
			});  
			if (unsentMessages != null)
				return Lists.newArrayList(unsentMessages);
		}
		return Lists.newArrayList();
	}

	public static List<WSession> getAllSessions(final Workspace workspace, final WSession wSession) {
		// Get list of sessions for this workspace
		return ofy().load().type(WSession.class).ancestor(workspace).list();
		
	}
	
	public static void deleteSavedMessages(final LoginUser loginUser, final Workspace workspace, final WSession wSession) {
		List<WMessage> messages = getSavedMessages(workspace, wSession);
		List<String> ids = Lists.newArrayList();
		for (WMessage msg : messages) {
			ids.add(msg.getId());
		}
		ofy().delete().type(WMessage.class).ids(ids).now();
		logger.info("Deleted the saved message for workspace " + workspace.getId() + ". Count is " + messages.size() + ".");
	}
	

	public static void deleteAllSessions(final LoginUser loginUser, final Workspace workspace, final WSession wSession) {
		List<WSession> sessions = getAllSessions(workspace, wSession);
		List<String> ids = Lists.newArrayList();
		for (WSession s : sessions) {
			ids.add(s.getId());
		}
		ofy().delete().type(WSession.class).ids(ids).now();
		logger.info("Deleted all sessions for workspace " + workspace.getId() + ". Count " + sessions.size() + ".");
	}

	public static void sendSavedMessages(final LoginUser loginUser, final Workspace workspace, final WSession wSession) {
		List<WMessage> messages = getSavedMessages(workspace, wSession);
		String clientId = makeClientId(loginUser, workspace, wSession);
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		for (WMessage msg : messages) {
			channelService.sendMessage(new ChannelMessage(clientId, msg.getMsg()));
			logger.info("Delivered saved message to " + clientId);
		}
	}

	public static void onMessage(final LoginUser loginUser, final Workspace workspace, final WSession wSession, List<WSession> otherSessions, final String message) {
		for (WSession s : otherSessions) {
			if (s.isConnected()) {
				String clientId = makeClientId(loginUser, workspace, s);
				ChannelService channelService = ChannelServiceFactory.getChannelService();
				channelService.sendMessage(new ChannelMessage(clientId, message));
				//logger.info("Delivered message to session " + s.getId());
			}
			else {
				// Save the message
				WMessage newMessage = new WMessage(wSession.getId(), message, workspace);
				ofy().save().entity(newMessage).now();
				//logger.info("Saved message for session " + wSession.getId());
			}
		}
	}

	public static void handleMessage(final LoginUser loginUser, final Workspace workspace, final WSession wSession, final String message) {
		MessageInfo msgObj = Message.fromJson(message);
		// Get other sessions under this workspace
		List<WSession> allSessions = ofy().load().type(WSession.class).ancestor(workspace).list();
		Iterable<WSession> others = Iterables.filter(allSessions, new Predicate<WSession>() {
			public boolean apply(WSession s) {
				return !StringUtils.equals(s.getId(), wSession.getId()) && s.isConnected();
			}
		});

		if (StringUtils.equalsIgnoreCase(msgObj.getType(), MessageInfo.MSG_TYPE_BYE)) {
			// Set connected to false
			wSession.setConnected(false);
			ofy().save().entity(wSession).now();
			logger.info("Session " + wSession.getId() + " is removed from workspace " + workspace.getId());
		}

		List<WSession> otherSessions = Lists.newArrayList(others);
		if (otherSessions != null && otherSessions.size() > 0) {
			onMessage(loginUser, workspace, wSession, otherSessions, message);
		}
	}

}
