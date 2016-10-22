package com.mymobkit;

import java.util.Map;

import javax.inject.Singleton;
import javax.servlet.ServletContextEvent;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import com.google.appengine.api.appidentity.AppIdentityServiceFactory;
import com.google.appengine.tools.appstats.AppstatsFilter;
import com.google.appengine.tools.appstats.AppstatsServlet;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.matcher.Matchers;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.googlecode.objectify.ObjectifyFilter;
import com.mymobkit.common.AppConfig;
import com.mymobkit.datastore.OfyService;
import com.mymobkit.server.CapturedImageServiceImpl;
import com.mymobkit.server.LoginServiceImpl;
import com.mymobkit.server.RequestProcessorServlet;
import com.mymobkit.server.UploadServlet;
import com.mymobkit.service.DeviceAction;
import com.mymobkit.txn.ObjectMapperProvider;
import com.mymobkit.util.txn.Transact;
import com.mymobkit.util.txn.TransactInterceptor;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

@Slf4j
public final class GuiceConfig extends GuiceServletContextListener {

	static class MyMobKitServletModule extends ServletModule {

		@Override
		protected void configureServlets() {
			Map<String, String> appstatsParams = Maps.newHashMap();
			appstatsParams.put("logMessage", "Appstats: /admin/appstats/details?time={ID}");
			appstatsParams.put("calculateRpcCosts", "true");
			filter("/*").through(AppstatsFilter.class, appstatsParams);
			filter("/*").through(ObjectifyFilter.class);

			serve("/appstats/*").with(AppstatsServlet.class);

			// Servlet configuration
			serve("/request").with(RequestProcessorServlet.class);
			serve("/upload").with(UploadServlet.class);
			serve("/mymobkitweb/login").with(LoginServiceImpl.class);
			serve("/mymobkitweb/viewer").with(CapturedImageServiceImpl.class);
			
			// serve("/viewer").with(ViewerServlet.class);
			// serve("/myworkspace").with(MyWorkspaceServlet.class);
			// serve("/message").with(MyWorkspaceMessageServlet.class);
			// serve("/_ah/channel/connected/").with(MyWorkspaceConnectServlet.class);
			// serve("/_ah/channel/disconnected/").with(MyWorkspaceDisconnectServlet.class);
			// serve("/spy").with(SpyServlet.class);

			// GCM servlet configurations
			//serve("/register").with(RegisterServlet.class);
			////serve("/unregister").with(UnregisterServlet.class);
			//serve("/sendAll").with(SendAllMessagesServlet.class);
			//serve("/send").with(SendMessageServlet.class);
			//serve("/home").with(HomeServlet.class);

			Map<String, String> params = Maps.newHashMap();
			params.put("com.sun.jersey.config.property.packages", "com.mymobkit.service");
			serve("/service/*").with(GuiceContainer.class, params);
		}
	}

	public static class MyMobKitModule extends AbstractModule {

		@Override
		protected void configure() {
			requestStaticInjection(OfyService.class);

			// Lets us use @Transact
			bindInterceptor(Matchers.any(), Matchers.annotatedWith(Transact.class), new TransactInterceptor());

			// Use jackson for jaxrs
			bind(ObjectMapperProvider.class);

			// External things that don't have Guice annotations
			bind(AppstatsFilter.class).in(Singleton.class);
			bind(AppstatsServlet.class).in(Singleton.class);
			bind(ObjectifyFilter.class).in(Singleton.class);

			bind(DeviceAction.class);
		}
	}

	/**
	 * Logs the time required to initialize Guice
	 */
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		long time = System.currentTimeMillis();

		super.contextInitialized(sce);

		// Initialize app
		initApp(sce);

		long millis = System.currentTimeMillis() - time;
		log.info("Guice initialization took " + millis + " millis");
	}

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(new MyMobKitServletModule(), new MyMobKitModule());
	}

	private void initApp(ServletContextEvent sce) {
		String gcmKey = AppConfig.API_KEY_DEV;
		final String sca = AppIdentityServiceFactory.getAppIdentityService().getServiceAccountName();
		
		log.info("Service account name " +  sca);
		if (StringUtils.isNotBlank(sca) && StringUtils.contains(sca, AppConfig.PRODUCTION_APP_NAME)) {
			AppConfig.DEBUG_MODE = false;
			gcmKey = AppConfig.API_KEY_PROD;
		}
		sce.getServletContext().setAttribute(AppConfig.ATTRIBUTE_GCM_KEY, gcmKey);
	}
}