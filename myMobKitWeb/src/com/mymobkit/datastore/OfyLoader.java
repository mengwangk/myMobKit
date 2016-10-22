package com.mymobkit.datastore;


import com.googlecode.objectify.impl.LoaderImpl;

/**
 * Extend the Loader command with our own logic
 *
 */
public class OfyLoader extends LoaderImpl<OfyLoader>
{
	public OfyLoader(Ofy base) {
		super(base);
	}
	
}