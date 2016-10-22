package com.mymobkit.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.mymobkit.model.CapturedImage;

@RemoteServiceRelativePath("viewer")
public interface CapturedImageService  extends RemoteService  {
	
	public String getBlobstoreUploadUrl();
	public CapturedImage get(Long id);
	public List<CapturedImage> getByEmail(String email);
	public void delete(CapturedImage image);

}

