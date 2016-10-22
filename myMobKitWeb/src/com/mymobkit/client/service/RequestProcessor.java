package com.mymobkit.client.service;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.mymobkit.enums.RequestType;

public final class RequestProcessor {

	public static String process(final HttpServletRequest req, final String requestType) {
		if (StringUtils.equalsIgnoreCase(requestType, RequestType.REQUEST_FOR_UPLOAD_URL.getType())) {
			// Create and return the upload URL
			BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
			return blobstoreService.createUploadUrl("/upload");
		}
		// else if (StringUtils.equalsIgnoreCase(requestType, AppConfig.REQUEST_TYPE.REQUEST_FOR_ACTIVE_DEVICES.getType())) {
		// Get a list of active devices for this user
		// final String email = (String)req.getAttribute("email");
		// LoginUser user = ofy().load().type(LoginUser.class).id(email).now();
		// Workspace workspace = ofy().load().type(Workspace.class).parent(user).id(workspaceId).now();
		// WSession wSession = ofy().load().type(WSession.class).parent(workspace).id(wSessionId).now();
		// }
		return StringUtils.EMPTY;
	}
}
