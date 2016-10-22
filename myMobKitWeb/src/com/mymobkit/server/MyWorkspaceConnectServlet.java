package com.mymobkit.server;

import static com.mymobkit.datastore.OfyService.ofy;

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
import com.mymobkit.common.WorkspaceUtils;
import com.mymobkit.model.LoginUser;
import com.mymobkit.model.WSession;
import com.mymobkit.model.Workspace;

@SuppressWarnings("serial")
@Singleton
public class MyWorkspaceConnectServlet extends HttpServlet {

	protected static final Logger logger = Logger.getLogger(MyWorkspaceConnectServlet.class.getName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		ChannelService channelService = ChannelServiceFactory.getChannelService();
		ChannelPresence presence = channelService.parsePresence(req);
		String key = presence.clientId();
		String email = StringUtils.EMPTY;
		String workspaceId = StringUtils.EMPTY;
		String wSessionId = StringUtils.EMPTY;
		if (StringUtils.isNotBlank(key)) {
			String[] values = StringUtils.split(key, "/");
			email = values[0];
			workspaceId = values[1];
			wSessionId = values[2];
			LoginUser user = ofy().load().type(LoginUser.class).id(email).now();
			Workspace workspace = ofy().load().type(Workspace.class).parent(user).id(workspaceId).now();
			WSession wSession = ofy().load().type(WSession.class).parent(workspace).id(wSessionId).now();
			if (wSession != null) {
				// Check if room has user in case that disconnect message comes before
				// connect message with unknown reason, observed with local AppEngine SDK.
				wSession.setConnected(true);
				
				// Save the session
				ofy().save().entity(wSession).now();
				
				// Send any saved messages if not being 
				WorkspaceUtils.sendSavedMessages(user, workspace, wSession);
				logger.info("User " + email + " connected to workspace " + workspaceId );
			} else {
				logger.log(Level.WARNING, "Unexpected Connect message to workspace " + workspaceId);
			}
		}
	}
}
