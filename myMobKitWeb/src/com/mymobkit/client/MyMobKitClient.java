package com.mymobkit.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.mymobkit.client.service.LoginService;
import com.mymobkit.client.service.LoginServiceAsync;
import com.mymobkit.client.widget.PhotoGallery;
import com.mymobkit.common.AppConfig;
import com.mymobkit.model.LoginUser;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MyMobKitClient implements EntryPoint {

	protected static final Logger logger = Logger.getLogger(MyMobKitClient.class.getName());
	private LoginServiceAsync loginService = GWT.create(LoginService.class);
	private LoginUser loginUser = null;
	private VerticalPanel loginPanel = new VerticalPanel();
	private Anchor signInLink = new Anchor("Login using your Google account");
	private Anchor signOut = new Anchor("Logout");

	private PhotoGallery galleryWidget;

	public void onModuleLoad() {
		loginService.validate(GWT.getHostPageBaseURL(), GWT.getHostPageBaseURL() + AppConfig.SURVEILLANCE_IMAGE_VIEWER_PAGE,
				new AsyncCallback<LoginUser>() {
					@Override
					public void onSuccess(LoginUser result) {
						loginUser = result;
						if (loginUser.isLoggedIn()) {
							galleryWidget = new PhotoGallery(MyMobKitClient.this);
							RootPanel.get("gallery").add(galleryWidget);
							//loadLogout();
						} else {
							//loadLogin();
							RootPanel.get("message").setVisible(true);
							RootPanel.get("message").getElement().setInnerHTML("Login to view the images");
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						logger.log(Level.SEVERE, "Error showing gallery", caught);
					}
				});

	}

	private void loadLogin() {
		loginPanel.clear();
		signInLink.setHref(loginUser.getLoginUrl());
		loginPanel.add(signInLink);
		RootPanel.get("loginPanel").add(loginPanel);
	}

	private void loadLogout() {
		loginPanel.clear();
		signOut.setHref(loginUser.getLogoutUrl());
		loginPanel.add(signOut);
		RootPanel.get("loginPanel").add(loginPanel);
	}

	public LoginUser getLoginUser() {
		return loginUser;
	}

}
