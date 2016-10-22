package com.mymobkit.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.mymobkit.model.CapturedImage;

public interface CapturedImageServiceAsync {

	void delete(CapturedImage image, AsyncCallback<Void> callback);

	void get(Long id, AsyncCallback<CapturedImage> callback);

	void getBlobstoreUploadUrl(AsyncCallback<String> callback);

	void getByEmail(String email, AsyncCallback<List<CapturedImage>> callback);

}
