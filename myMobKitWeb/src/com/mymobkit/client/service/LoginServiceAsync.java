package com.mymobkit.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.mymobkit.model.LoginUser;

public interface LoginServiceAsync {

	void validate(String requestUri, String destinationUrl, AsyncCallback<LoginUser> callback);

}
