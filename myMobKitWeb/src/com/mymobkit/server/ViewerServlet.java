package com.mymobkit.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.mymobkit.common.HttpResponse;

@SuppressWarnings("serial")
@Singleton
public final class ViewerServlet extends HttpServlet {

	protected static final Logger logger = Logger.getLogger(ViewerServlet.class.getName());

	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
		//blobstoreService.serve(blobKey, resp);
		
		//BlobKey blobKey = new BlobKey(req.getParameter("blob-key"));
       // ImagesService imagesService = ImagesServiceFactory.getImagesService();
       // ServingUrlOptions servingUrlOptions = ServingUrlOptions.Builder.withBlobKey(blobKey);
        //resp.sendRedirect(imagesService.getServingUrl(servingUrlOptions));
        
       // Image image = ImagesServiceFactory.makeImageFromBlob(blobKey);
       // Transform resize = ImagesServiceFactory.makeResize(300, 300);
        //Image newImage = imagesService.applyTransform(resize, image);

        //byte[] newImageData = newImage.getImageData();
       // resp.setContentType("image/jpeg");
       // resp.getOutputStream().write(newImageData);
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.getWriter().println(HttpResponse.OK.toString());
	}

}
