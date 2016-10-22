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

import com.google.appengine.api.channel.ChannelPresence;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.mymobkit.common.RTCUtils;
import com.mymobkit.model.ModelBase;
import com.mymobkit.model.Room;

@SuppressWarnings("serial")
@Singleton
public class ConnectServlet extends HttpServlet {

	protected static final Logger logger = Logger.getLogger(ConnectServlet.class.getName());

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
				// Check if room has user in case that disconnect message comes before
				// connect message with unknown reason, observed with local AppEngine SDK.
				room.setConnected(user);
				RTCUtils.sendSavedMessages(RTCUtils.makeClientId(room, user));
				logger.info("User " + user + " connected to room " + roomKey);
				logger.info("Room " + roomKey + " has state " + room.toString());
			} else {
				logger.log(Level.WARNING, "Unexpected Connect Message to room " + roomKey);
			}
		}
	}
}
