package com.mymobkit.server;

import static com.mymobkit.datastore.OfyService.ofy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Singleton;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.common.collect.Lists;
import com.mymobkit.common.EntityHelper;
import com.mymobkit.common.RTCMode;
import com.mymobkit.common.RTCUtils;
import com.mymobkit.common.WorkspaceUtils;
import com.mymobkit.model.LoginUser;
import com.mymobkit.model.WSession;
import com.mymobkit.model.Workspace;

/**
 * Main servlet as the workspace entry point.
 * 
 */
@SuppressWarnings("serial")
@Singleton
public class MyWorkspaceServlet extends HttpServlet {

	protected static final Logger logger = Logger.getLogger(MyWorkspaceServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// Check login authentication
		UserService userService = UserServiceFactory.getUserService();
		User currentUser = userService.getCurrentUser();
		if (currentUser == null) {
			resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
			return;
		}

		LoginUser loginUser = ofy().load().type(LoginUser.class).id(currentUser.getEmail()).now();
		if (loginUser == null) {
			// New user
			loginUser = new LoginUser(currentUser.getEmail());
			loginUser.setNickName(currentUser.getNickname());
			loginUser.loggedIn();
			ofy().save().entity(loginUser).now();
		} else {
			// Existing user
			loginUser.loggedIn();
			ofy().save().entity(loginUser).now();
		}

		String audio = "false";
		String video = "false";

		// Get the view mode
		// 0 - webcam mode only
		// 1 - phone mode only
		// 2 - video call mode
		String mode = req.getParameter("m");
		if (StringUtils.isBlank(mode)) {
			mode = RTCMode.WEBCAM.getMode();
		}
		RTCMode rtcMode = RTCMode.fromMode(mode);
		if (rtcMode == null) {
			rtcMode = RTCMode.WEBCAM;
			mode = rtcMode.getMode();
		}

		String targetPage = StringUtils.EMPTY;
		if (rtcMode == RTCMode.WEBCAM) {
			targetPage = "mywebcam.jsp";
			audio = "true";
			video = "true";
		} else if (rtcMode == RTCMode.PHONE) {
			targetPage = "myphone.jsp";
			audio = "false";
			video = "false";
		} else {
			// Not valid mode
			resp.sendRedirect("/");
			return;
		}
		// Renders the main page. When this page is shown, we create a new
		// channel to push asynchronous updates to the client.

		// Append strings to this list to have them thrown up in message boxes.
		// This will also cause the application to fail.
		List<String> errorMessages = new ArrayList<String>(1);

		// Get the base URL without arguments.
		String url = req.getRequestURL().toString();
		String baseURL = url.substring(0, url.length() - req.getRequestURI().length()) + req.getContextPath() + "/";
		String userAgent = req.getHeader("User-Agent");

		// Not using room key, using mode and search by existing
		String workspaceId = RTCUtils.sanitize(req.getParameter("w"));

		String stunServer = req.getParameter("ss");
		if (StringUtils.isBlank(stunServer)) {
			stunServer = RTCUtils.getDefaultStunServer(userAgent);
		}
		String turnServer = req.getParameter("ts");
		String tsPwd = req.getParameter("tp");

		String hd = req.getParameter("hd");
		if (StringUtils.equalsIgnoreCase(hd, "true")) {
			if (StringUtils.isNotBlank(video)) {
				String message = "\"The 'hd' parameter has overridden video=" + video + "\"";
				logger.info(message);
				// errorMessages.add(message);
			}
			video = "minWidth=800,minHeight=600";
		}

		String minre = req.getParameter("minre");
		String maxre = req.getParameter("maxre");
		if (StringUtils.isNotBlank(minre) || StringUtils.isNotBlank(maxre)) {
			String message = "\"The 'minre' and 'maxre' parameters are no longer supported. Use 'video' instead.\"";
			logger.info(message);
			errorMessages.add(message);
		}

		String audioSendCodec = req.getParameter("asc");
		if (StringUtils.isBlank(audioSendCodec)) {
			audioSendCodec = RTCUtils.getPreferredAudioSendCodec(userAgent);
		}

		String audioReceiveCodec = req.getParameter("arc");
		if (StringUtils.isBlank(audioReceiveCodec)) {
			audioReceiveCodec = RTCUtils.getPreferredAudioReceiveCodec();
		}

		// Set stereo to false by default.
		String stereo = "false";
		if (StringUtils.isNotBlank(req.getParameter("stereo"))) {
			stereo = req.getParameter("stereo");
		}

		// Options for making pcConstraints
		String dtls = req.getParameter("dtls");
		String dscp = req.getParameter("dscp");
		String ipv6 = req.getParameter("ipv6");

		String debug = req.getParameter("debug");
		if (StringUtils.equalsIgnoreCase(debug, "loopback")) {
			// Set dtls to false as DTLS does not work for loopback.
			dtls = "false";
		}

		// token_timeout for channel creation, default 30min, max 1 days, min
		// 3min.
		int tokenTimeout = RTCUtils.getRange(req.getParameter("tt"), 3, 1440, 30);

		/*
		 * if (StringUtils.isBlank(roomKey)) { roomKey =
		 * RTCUtils.generateRandom(10); String redirect = "/spy?r=" + roomKey;
		 * redirect = RTCUtils.appendUrlArguments(req, redirect);
		 * logger.info("Redirecting visitor to base URL to " + redirect);
		 * RequestDispatcher dispatcher = req.getRequestDispatcher(redirect);
		 * dispatcher.forward(req, resp); return; }
		 */
		Workspace workspace = null;
		List<WSession> wSessions = null;
		if (!StringUtils.isBlank(workspaceId)) {
			workspace = ofy().load().type(Workspace.class).parent(loginUser).id(workspaceId).now();
			if (workspace != null) {

				// Check if workspace mode match passed in mode
				if (!StringUtils.equalsIgnoreCase(workspace.getMode(), mode)) {
					// Not matched. How could it be? Redirect to main page
					resp.sendRedirect("/");
					return;
				}

				// Check for existing sessions
				wSessions = ofy().load().type(WSession.class).ancestor(workspace).list();
			}
		}

		if (workspace == null) {
			// Create a new workspace
			workspace = new Workspace(loginUser, loginUser.getEmail() + "-" + EntityHelper.generateGuid(), mode);

			// Save the workspace
			ofy().save().entity(workspace).now();
		}
		if (wSessions == null) {
			wSessions = Lists.newArrayList();
		}

		workspaceId = workspace.getId();
		int initiator = 0;

		if (wSessions.size() == 0) {
			initiator = 0;
		} else if (wSessions.size() >= 1) {
			initiator = 1;
		}

		// Create a new session for this user
		WSession wSession = new WSession(loginUser.getEmail(), workspace);

		// Save the session
		ofy().save().entity(wSession).now();

		/*
		 * Room room = new Room(workspaceKey); if (!Room.find(room,
		 * workspaceKey)) { // New room user = RTCUtils.generateRandom(8);
		 * room.addUser(user); initiator = 0;
		 * 
		 * } else if (room.getOccupancy() == 1) { // 1 occupant user =
		 * RTCUtils.generateRandom(8); room.addUser(user); initiator = 1; } else
		 * { // 2 occupants (full). // req.setAttribute("room_key", roomKey);
		 * RequestDispatcher dispatcher =
		 * req.getRequestDispatcher("not_avail.jsp"); dispatcher.forward(req,
		 * resp); return; }
		 */

		// String roomLink = baseURL + "spy?r=" + roomKey;
		String workspaceLink = baseURL + "myworkspace?m=" + mode + "&w=" + workspaceId;
		workspaceLink = RTCUtils.appendUrlArguments(req, workspaceLink);
		// String turnURL = "https://computeengineondemand.appspot.com/";
		// turnURL += "turn?" + "username=" + loginUser.getEmail() +
		// "&key=4080218913";
		String token = WorkspaceUtils.createChannel(loginUser, workspace, wSession, tokenTimeout);
		String pcConfig = RTCUtils.makePcConfig(stunServer, turnServer, tsPwd);
		String pcConstraints = RTCUtils.makePcConstraints(dtls, dscp, ipv6);
		String offerConstraints = RTCUtils.makeOfferConstraints();
		String mediaConstraints = RTCUtils.makeMediaStreamConstraints(audio, video);

		req.setAttribute("error_messages", StringUtils.join(errorMessages, ","));
		req.setAttribute("token", token);
		req.setAttribute("user_email", loginUser.getNormalizedEmail());
		req.setAttribute("session_id", wSession.getId());
		req.setAttribute("workspace_id", workspaceId);
		req.setAttribute("workspace_link", workspaceLink);
		req.setAttribute("initiator", String.valueOf(initiator));
		req.setAttribute("pc_config", pcConfig);
		req.setAttribute("pc_constraints", pcConstraints);
		req.setAttribute("offer_constraints", offerConstraints);
		req.setAttribute("media_constraints", mediaConstraints);
		req.setAttribute("turn_url", StringUtils.EMPTY);
		req.setAttribute("stereo", stereo);
		req.setAttribute("audio_send_codec", audioSendCodec);
		req.setAttribute("audio_receive_codec", audioReceiveCodec);
		req.setAttribute("rtc_mode", mode);

		RequestDispatcher dispatcher = req.getRequestDispatcher(targetPage);
		dispatcher.forward(req, resp);
	}
}
