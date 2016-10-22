package com.mymobkit.server;

import static com.mymobkit.datastore.OfyService.ofy;

import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.mymobkit.client.service.LoginService;
import com.mymobkit.model.LoginUser;

@SuppressWarnings("serial")
@Singleton
public class LoginServiceImpl extends RemoteServiceServlet implements LoginService {

	public LoginUser validate(String requestUri, String destinationUrl) {
		UserService userService = UserServiceFactory.getUserService();
		User currentUser = userService.getCurrentUser();
		LoginUser loginUser = null;

		if (currentUser != null) {
			loginUser = ofy().load().type(LoginUser.class).id(currentUser.getEmail()).now();
			if (loginUser == null) {
				// New user
				loginUser = new LoginUser(currentUser.getEmail());
			}
			loginUser.setNickName(currentUser.getNickname());
			loginUser.setId(currentUser.getUserId());
			if (StringUtils.isBlank(destinationUrl))
				loginUser.setLogoutUrl(userService.createLogoutURL(requestUri));
			else
				loginUser.setLogoutUrl(userService.createLogoutURL(destinationUrl));
			loginUser.loggedIn();
			ofy().save().entity(loginUser).now();
		} else {
			loginUser = new LoginUser(StringUtils.EMPTY);
			loginUser.setLoggedIn(false);
			if (StringUtils.isBlank(destinationUrl))
				loginUser.setLoginUrl(userService.createLoginURL(requestUri));
			else
				loginUser.setLoginUrl(userService.createLoginURL(destinationUrl));
		}
		return loginUser;
	}

}