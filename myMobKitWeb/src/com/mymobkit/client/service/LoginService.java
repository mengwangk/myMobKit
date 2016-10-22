package com.mymobkit.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.mymobkit.model.LoginUser;

@RemoteServiceRelativePath("login")
public interface LoginService extends RemoteService {
	public LoginUser validate(String requestUri, String destinationUrl);
}