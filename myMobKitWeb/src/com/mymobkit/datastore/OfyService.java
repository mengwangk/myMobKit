package com.mymobkit.datastore;

import javax.inject.Inject;

import com.googlecode.objectify.ObjectifyService;

/**
 * Gives us our custom version rather than the standard Objectify one.
 * 
 */
public class OfyService extends ObjectifyService {

	@Inject
	public static void setObjectifyFactory(OfyFactory factory) {
		ObjectifyService.setFactory(factory);
	}

	/**
	 * @return our extension to Objectify
	 */
	public static Ofy ofy() {
		return (Ofy) ObjectifyService.ofy();
	}

	/**
	 * @return our extension to ObjectifyFactory
	 */
	public static OfyFactory factory() {
		return (OfyFactory) ObjectifyService.factory();
	}
}