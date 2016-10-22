package com.mymobkit.datastore;

import com.googlecode.objectify.impl.ObjectifyImpl;

/**
 * Our basic data access interface. Extends the basic Objectify interface to add our custom logic.
 *
 */
public class Ofy extends ObjectifyImpl<Ofy> {
	/** */
	public Ofy(OfyFactory base) {
		super(base);
	}

	/** More wrappers, fun */
	@Override
	public OfyLoader load() {
		return new OfyLoader(this);
	}
}