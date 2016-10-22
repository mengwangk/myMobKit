package com.mymobkit.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mymobkit.common.RTCUtils;
import com.mymobkit.model.ModelBase;
import com.mymobkit.model.Room;

@SuppressWarnings("serial")
@Singleton
public class MessageServlet extends HttpServlet {

	protected static final Logger logger = Logger.getLogger(MessageServlet.class.getName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String message = getBody(req);
		String roomKey = req.getParameter("r");
		String user = req.getParameter("u");
		Room room = new Room(roomKey);
		if (ModelBase.find(room,  roomKey)){
			RTCUtils.handleMessage(room, user, message);
		} else {
			logger.log(Level.WARNING, "Unknown room " + roomKey);
		}
	}

	private String getBody(HttpServletRequest request) throws IOException {
		String body = null;
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;
		try {
			InputStream inputStream = request.getInputStream();
			if (inputStream != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			} else {
				stringBuilder.append("");
			}
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException ex) {
					throw ex;
				}
			}
		}
		body = stringBuilder.toString();
		return body;
	}

}
