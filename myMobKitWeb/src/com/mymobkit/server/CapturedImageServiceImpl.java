package com.mymobkit.server;

import static com.mymobkit.datastore.OfyService.ofy;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.mymobkit.client.service.CapturedImageService;
import com.mymobkit.model.CapturedImage;

@SuppressWarnings("serial")
@Singleton
@Slf4j
public final class CapturedImageServiceImpl extends RemoteServiceServlet implements CapturedImageService {

	@Override
	public String getBlobstoreUploadUrl() {
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		return blobstoreService.createUploadUrl("/upload");
	}

	@Override
	public CapturedImage get(Long id) {
		CapturedImage image = ofy().load().type(CapturedImage.class).id(id).now();
		return image;
	}

	@Override
	public List<CapturedImage> getByEmail(String email){
		List<CapturedImage> images = Lists.newArrayList(ofy().load().type(CapturedImage.class).filter("ownerEmail", email.toLowerCase()).list());
		Collections.sort(images, new Comparator<CapturedImage>() {
		    @Override
		    public int compare(CapturedImage c1, CapturedImage c2) {
		        return ComparisonChain.start().compare(c2.getSystemTimestamp(), c1.getSystemTimestamp()).result();
		    }
		});
		return images;
	}

	@Override
	public void delete(CapturedImage image) {
		ofy().delete().entity(image).now();
	}
}
