package com.mymobkit.server;

import static com.mymobkit.datastore.OfyService.ofy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.mymobkit.client.service.RequestProcessor;
import com.mymobkit.common.AppConfig;
import com.mymobkit.common.HttpResponse;
import com.mymobkit.model.CapturedImage;
import com.mymobkit.model.LoginUser;

@SuppressWarnings("serial")
@Singleton
public final class UploadServlet extends HttpServlet {

	private static final Logger logger = Logger.getLogger(UploadServlet.class.getName());

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String requestType = req.getParameter("request");
		if (StringUtils.isNotBlank(requestType)) {
			res.getWriter().print(RequestProcessor.process(req, requestType));
		} else {
			logger.log(Level.WARNING, "Unknown request");
		}
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		String userEmail = req.getParameter("email");

		// User email cannot be blank/empty
		if (StringUtils.isBlank(userEmail))
			return;

		try {
			LoginUser loginUser = ofy().load().type(LoginUser.class).id(userEmail).now();
			if (loginUser == null) {
				// New user
				loginUser = new LoginUser(userEmail);
				loginUser.loggedIn();
				ofy().save().entity(loginUser).now();
			} else {
				// Existing user
				loginUser.loggedIn();
				ofy().save().entity(loginUser).now();
			}

			BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
			ImagesService imagesService = ImagesServiceFactory.getImagesService();
			Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);

			String displayName = req.getParameter("name");
			List<CapturedImage> capturedImages = new ArrayList<CapturedImage>(1);
			String servingUrl;
			
			List<BlobKey> list = blobs.get("images_1");
			for (int i = 0; list != null && i < list.size(); i++) {
				BlobKey key = list.get(i);
				ServingUrlOptions servingUrlOptions = ServingUrlOptions.Builder.withBlobKey(key);
				servingUrl = imagesService.getServingUrl(servingUrlOptions);
				if (AppConfig.DEBUG_MODE) {
					servingUrl = StringUtils.replace(servingUrl, "0.0.0.0", "127.0.0.1");
				}
				capturedImages.add(new CapturedImage(loginUser.getNormalizedEmail(), key.getKeyString(), displayName, servingUrl));
			}

			list = blobs.get("images_2");
			for (int i = 0; list != null && i < list.size(); i++) {
				BlobKey key = list.get(i);
				ServingUrlOptions servingUrlOptions = ServingUrlOptions.Builder.withBlobKey(key);
				servingUrl = imagesService.getServingUrl(servingUrlOptions);
				if (AppConfig.DEBUG_MODE) {
					servingUrl = StringUtils.replace(servingUrl, "0.0.0.0", "127.0.0.1");
				}
				capturedImages.add(new CapturedImage(loginUser.getNormalizedEmail(), key.getKeyString(), displayName, servingUrl));
			}
			
			// Save the session
			ofy().save().entities(capturedImages).now();

			// List<CapturedImage> newImages =
			// ofy().load().type(CapturedImage.class).filter("ownerEmail",
			// loginUser.getNormalizedEmail()).list();
			// logger.log(Level.INFO, "email: " + newImages.size());

			/*
			 * List<CapturedImage> newImages =
			 * ofy().load().type(CapturedImage.class
			 * ).ancestor(loginUser).list();
			 * ofy().delete().entity(newImages.get(0)); newImages =
			 * ofy().load().
			 * type(CapturedImage.class).ancestor(loginUser).list();
			 * logger.log(Level.INFO, "email: " + newImages.size());
			 */
			logger.log(Level.INFO, "Images captured successfully for " + userEmail + ". Image count - " + capturedImages.size());

			res.getWriter().print(HttpResponse.OK.toString());
		} catch (Exception ex) {
			logger.log(Level.SEVERE, "Error capturing images for " + userEmail, ex);
			res.getWriter().println(HttpResponse.ERROR.toString());
		}

	}

	private String createDisplayName(final String name, final int seq) {
		return name + "_" + seq;
	}
}