package com.mymobkit.client.event;

import com.google.gwt.event.shared.GwtEvent;


/** 
 * Represents a GalleryUpdatedEvent to fire to the message bus for all listeners.
 * 
 *	Inspiration:  
 * 	http://stackoverflow.com/questions/2951621/gwt-custom-events
 *
 */
public class GalleryUpdatedEvent extends GwtEvent<GalleryUpdatedEventHandler> {

    public static Type<GalleryUpdatedEventHandler> TYPE = new Type<GalleryUpdatedEventHandler>();

	@Override
	public Type<GalleryUpdatedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(GalleryUpdatedEventHandler handler) {
		handler.onGalleryUpdated(this);
	}

}
