package com.mymobkit.server;

import static com.mymobkit.datastore.OfyService.ofy;

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

import com.mymobkit.common.WorkspaceUtils;
import com.mymobkit.model.LoginUser;
import com.mymobkit.model.WSession;
import com.mymobkit.model.Workspace;

@SuppressWarnings("serial")
@Singleton
public final class MyWorkspaceMessageServlet extends HttpServlet {

	protected static final Logger logger = Logger.getLogger(MyWorkspaceMessageServlet.class.getName());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String message = getBody(req);
		String email = req.getParameter("u");
		String workspaceId = req.getParameter("w");
		String wSessionId = req.getParameter("sid");

		LoginUser user = ofy().load().type(LoginUser.class).id(email).now();
		Workspace workspace = ofy().load().type(Workspace.class).parent(user).id(workspaceId).now();
		WSession wSession = ofy().load().type(WSession.class).parent(workspace).id(wSessionId).now();

		if (wSession != null && wSession.isConnected()) {
			WorkspaceUtils.handleMessage(user, workspace, wSession, message);
		} else {
			logger.log(Level.WARNING, "Unknown session " + wSessionId);
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
