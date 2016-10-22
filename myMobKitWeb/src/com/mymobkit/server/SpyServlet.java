package com.mymobkit.server;

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

import com.mymobkit.common.RTCUtils;
import com.mymobkit.model.Room;

/**
 * The main UI page, renders the 'index.html' template.
 * 
 */
@SuppressWarnings("serial")
@Singleton
public class SpyServlet extends HttpServlet {

	protected static final Logger logger = Logger.getLogger(SpyServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// Renders the main page. When this page is shown, we create a new
		// channel to push asynchronous updates to the client.

		// Append strings to this list to have them thrown up in message boxes.
		// This will also cause the application to fail.
		List<String> errorMessages = new ArrayList<String>(1);

		// Get the base URL without arguments.
		String url = req.getRequestURL().toString();
		String baseURL = url.substring(0, url.length() - req.getRequestURI().length()) + req.getContextPath() + "/";
		String userAgent = req.getHeader("User-Agent");
		String roomKey = RTCUtils.sanitize(req.getParameter("r"));
		String stunServer = req.getParameter("ss");
		if (StringUtils.isBlank(stunServer)) {
			stunServer = RTCUtils.getDefaultStunServer(userAgent);
		}
		String turnServer = req.getParameter("ts");
		String tsPwd = req.getParameter("tp");

		// Use "audio" and "video" to set the media stream constraints. Defined
		// here:
		// http://dev.w3.org/2011/webrtc/editor/getusermedia.html#idl-def-MediaStreamConstraints
		//
		// "true" and "false" are recognized and interpreted as bools, for
		// example:
		// "?audio=true&video=false" (Start an audio-only call.)
		// "?audio=false" (Start a video-only call.)
		// If unspecified, the stream constraint defaults to True.
		//
		// To specify media track constraints, pass in a comma-separated list of
		// key/value pairs, separated by a "=". Examples:
		// "?audio=googEchoCancellation=false,googAutoGainControl=true"
		// (Disable echo cancellation and enable gain control.)
		//
		// "?video=minWidth=1280,minHeight=720,googNoiseReduction=true"
		// (Set the minimum resolution to 1280x720 and enable noise reduction.)
		//
		// Keys starting with "goog" will be added to the "optional" key; all
		// others
		// will be added to the "mandatory" key.
		//
		// The audio keys are defined here:
		// https://code.google.com/p/webrtc/source/browse/trunk/talk/app/webrtc/localaudiosource.cc
		//
		// The video keys are defined here:
		// https://code.google.com/p/webrtc/source/browse/trunk/talk/app/webrtc/videosource.cc
		String audio = req.getParameter("audio");
		String video = req.getParameter("video");

		String hd = req.getParameter("hd");
		if (StringUtils.equalsIgnoreCase(hd, "true")) {
			if (StringUtils.isNotBlank(video)) {
				String message = "The \"hd\" parameter has overridden video=" + video;
				logger.info(message);
				errorMessages.add(message);
			}
			video = "minWidth=1280,minHeight=720";
		}

		String minre = req.getParameter("minre");
		String maxre = req.getParameter("maxre");
		if (StringUtils.isNotBlank(minre) || StringUtils.isNotBlank(maxre)) {
			String message = "The \"minre\" and \"maxre\" parameters are no longer supported. Use \"video\" instead.";
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

		String unitTest = req.getParameter("unittest");
		if (StringUtils.isNotBlank(unitTest)) {
			// Always create a new room for the unit tests.
			roomKey = RTCUtils.generateRandom(10);
		}

		if (StringUtils.isBlank(roomKey)) {
			roomKey = RTCUtils.generateRandom(10);
			String redirect = "/spy?r=" + roomKey;
			redirect = RTCUtils.appendUrlArguments(req, redirect);
			logger.info("Redirecting visitor to base URL to " + redirect);
			RequestDispatcher dispatcher = req.getRequestDispatcher(redirect);
			dispatcher.forward(req, resp);
			// resp.sendRedirect(redirect);
			return;
		}

		String user = StringUtils.EMPTY;
		int initiator = 0;

		Room room = new Room(roomKey);
		if (!Room.find(room, roomKey) && !StringUtils.equalsIgnoreCase(debug, "full")) {
			// New room
			user = RTCUtils.generateRandom(8);
			room.addUser(user);
			if (!StringUtils.equalsIgnoreCase(debug, "loopback")) {
				initiator = 0;
			} else {
				room.addUser(user);
				initiator = 1;
			}

		} else if (room.getOccupancy() == 1 && !StringUtils.equalsIgnoreCase(debug, "full")) {
			// 1 occupant
			user = RTCUtils.generateRandom(8);
			room.addUser(user);
			initiator = 1;
		} else {
			// 2 occupants (full).
			logger.info("Room " + roomKey + " is full");
			req.setAttribute("room_key", roomKey);
			RequestDispatcher dispatcher = req.getRequestDispatcher("notavail.jsp");
			dispatcher.forward(req, resp);
			// resp.sendRedirect("notavail.jsp");
			return;
		}

		String turnURL = "https://computeengineondemand.appspot.com/";
		turnURL += "turn?" + "username=" + user + "&key=4080218913";
		if (StringUtils.isBlank(turnServer) || StringUtils.equalsIgnoreCase(turnServer, "false")) {
			turnServer = "";
			turnURL = "";
		}

		String roomLink = baseURL + "spy?r=" + roomKey;
		roomLink = RTCUtils.appendUrlArguments(req, roomLink);
		
		String token = RTCUtils.createChannel(room, user, tokenTimeout);
		String pcConfig = RTCUtils.makePcConfig(stunServer, turnServer, tsPwd);
		String pcConstraints = RTCUtils.makePcConstraints(dtls, dscp, ipv6);
		String offerConstraints = RTCUtils.makeOfferConstraints();
		String mediaConstraints = RTCUtils.makeMediaStreamConstraints(audio, video);

		req.setAttribute("error_messages", StringUtils.join(errorMessages, ","));
		req.setAttribute("token", token);
		req.setAttribute("me", user);
		req.setAttribute("room_key", roomKey);
		req.setAttribute("room_link", roomLink);
		req.setAttribute("initiator", String.valueOf(initiator));
		req.setAttribute("pc_config", pcConfig);
		req.setAttribute("pc_constraints", pcConstraints);
		req.setAttribute("offer_constraints", offerConstraints);
		req.setAttribute("media_constraints", mediaConstraints);
		req.setAttribute("turn_url", turnURL);
		req.setAttribute("stereo", stereo);
		req.setAttribute("audio_send_codec", audioSendCodec);
		req.setAttribute("audio_receive_codec", audioReceiveCodec);

		String targetPage = "spy.jsp";
		if (StringUtils.isNotBlank(unitTest)) {
			targetPage = "test/test_" + unitTest + ".jsp";
		}
		logger.info("User " + user + " added to room " + roomKey);
		logger.info("Room " + roomKey + " has state " + room.toString());
		// resp.sendRedirect(targetPage);
		RequestDispatcher dispatcher = req.getRequestDispatcher(targetPage);
		dispatcher.forward(req, resp);
	}
}
