package com.mymobkit.server;

import static com.mymobkit.datastore.OfyService.ofy;

import java.io.IOException;
import java.util.List;
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
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mymobkit.common.WorkspaceUtils;
import com.mymobkit.model.LoginUser;
import com.mymobkit.model.WSession;
import com.mymobkit.model.Workspace;

@SuppressWarnings("serial")
@Singleton
public final class MyWorkspaceDisconnectServlet extends HttpServlet {

	protected static final Logger logger = Logger.getLogger(MyWorkspaceDisconnectServlet.class.getName());

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
			final LoginUser user = ofy().load().type(LoginUser.class).id(email).now();
			final Workspace workspace = ofy().load().type(Workspace.class).parent(user).id(workspaceId).now();
			final WSession wSession = ofy().load().type(WSession.class).parent(workspace).id(wSessionId).now();

			if (workspace != null && wSession != null && wSession.isConnected()) {

				// Set connected to false
				wSession.setConnected(false);
				ofy().save().entity(wSession).now();

				logger.info("Session " + wSessionId + " is removed from workspace " + workspaceId);

				// Get other sessions under this workspace
				List<WSession> allSessions = ofy().load().type(WSession.class).ancestor(workspace).list();
				Iterable<WSession> others = Iterables.filter(allSessions, new Predicate<WSession>() {
					public boolean apply(WSession s) {
						return !StringUtils.equals(s.getId(), wSession.getId()) && s.isConnected();
					}
				});

				List<WSession> otherSessions = Lists.newArrayList(others);
				if (otherSessions != null && otherSessions.size() > 0) {
					for (WSession s : otherSessions) {
						channelService.sendMessage(new ChannelMessage(WorkspaceUtils.makeClientId(user, workspace, s), "{\"type\":\"bye\"}"));
						logger.info("Sent BYE to " + s.getId());
					}
				} else {

					// Delete all messages related to the workspace
					WorkspaceUtils.deleteSavedMessages(user, workspace, wSession);

					// Delete all sessions related to the workspace
					WorkspaceUtils.deleteAllSessions(user, workspace, wSession);

					// Delete the workspace since there is no sessions
					ofy().delete().type(Workspace.class).id(workspace.getId()).now();

					logger.info("Workspace deleted for user " + user.getNormalizedEmail());
				}
			}
		}
	}
}
