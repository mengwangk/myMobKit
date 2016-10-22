package com.mymobkit.server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.mymobkit.common.RTCUtils;
import com.mymobkit.model.ModelBase;
import com.mymobkit.model.Room;

@SuppressWarnings("serial")
@Singleton
public class DisconnectServlet extends HttpServlet {

	protected static final Logger logger = Logger.getLogger(DisconnectServlet.class.getName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		ChannelPresence presence = channelService.parsePresence(req);
		String key = presence.clientId();
		String roomKey = StringUtils.EMPTY;
		String user = StringUtils.EMPTY;
		if (StringUtils.isNotBlank(key)) {
			String[] values = StringUtils.split(key, "/");
			roomKey = values[0];
			user = values[1];
			Room room = new Room(roomKey);
			if (ModelBase.find(room, roomKey) && room.hasUser(user)) {
				String otherUser = room.getOtherUser(user);
				room.removeUser(user);
				logger.info("User " + user + " removed from room " + roomKey);
				logger.info("Room " + roomKey + " has state " + room.toString());
				if (StringUtils.isNotBlank(otherUser) && !StringUtils.equalsIgnoreCase(otherUser, user)) {
					channelService.sendMessage(new ChannelMessage(RTCUtils.makeClientId(room, otherUser), "{\"type\":\"bye\"}"));
					logger.info("Sent BYE to " + otherUser);
				}
				logger.log(Level.WARNING, "User " + user + " disconnected from room " + roomKey);
			}
		}
	}
}
