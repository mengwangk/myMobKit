package com.mymobkit.datastore;

import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.googlecode.objectify.ObjectifyFactory;
import com.mymobkit.model.CapturedImage;
import com.mymobkit.model.Device;
import com.mymobkit.model.LoginUser;
import com.mymobkit.model.MulticastMessage;
import com.mymobkit.model.WMessage;
import com.mymobkit.model.WSession;
import com.mymobkit.model.Workspace;

/**
 * Our version of ObjectifyFactory which integrates with Guice.  You could and convenience methods here too.
 */
@Singleton
@Slf4j
public class OfyFactory extends ObjectifyFactory
{
	private Injector injector;
	
	/**
	 * Register our entity types.
	 */
	@Inject
	public OfyFactory(Injector injector) {
		this.injector = injector;
		
		long time = System.currentTimeMillis();
		
		// Register classes
		this.register(Workspace.class);
		this.register(WSession.class);
		this.register(WMessage.class);
		this.register(LoginUser.class);
		this.register(CapturedImage.class);
		this.register(Device.class);
		this.register(MulticastMessage.class);
		
		long millis = System.currentTimeMillis() - time;
		log.info("Registration took " + millis + " millis");
	}

	@Override
	public Ofy begin() {
		return new Ofy(this);
	}
	
	@Override
	public <T> T construct(Class<T> type) {
		return injector.getInstance(type);
	}

}