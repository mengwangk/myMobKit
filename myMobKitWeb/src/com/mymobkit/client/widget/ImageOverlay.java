package com.mymobkit.client.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.mymobkit.client.event.GalleryUpdatedEvent;
import com.mymobkit.client.event.GalleryUpdatedEventHandler;
import com.mymobkit.client.service.CapturedImageService;
import com.mymobkit.client.service.CapturedImageServiceAsync;
import com.mymobkit.model.CapturedImage;
import com.mymobkit.model.LoginUser;

/**
 * 
 * This class represents the ImageOverlay that pops up when a User clicks on an
 * Image. It also provides listeners for management, tagging, and other
 * functions which are considered "Menu" type functions for a given image.
 * 
 * 
 */
public class ImageOverlay extends Composite implements HasHandlers {

	private static ImageOverlayUiBinder uiBinder = GWT.create(ImageOverlayUiBinder.class);

	CapturedImageServiceAsync imageService = GWT.create(CapturedImageService.class);

	private HandlerManager handlerManager;

	interface ImageOverlayUiBinder extends UiBinder<Widget, ImageOverlay> {
	}

	@UiField
	Button deleteButton;

	@UiField
	Image image;

	@UiField
	Label timestamp;

	protected CapturedImage capturedImage;


	public ImageOverlay(CapturedImage capturedImage, LoginUser loginUser) {
		handlerManager = new HandlerManager(this);
		this.capturedImage = capturedImage;
		initWidget(uiBinder.createAndBindUi(this));
		image.setUrl(capturedImage.getServingUrl());
		timestamp.setText(capturedImage.getDisplayName());

		if (loginUser != null) {
			deleteButton.setText("Delete");
			deleteButton.setVisible(true);
		} else {
			deleteButton.setVisible(false);
		}
	}


	/**
	 * 
	 * Handles clicking of the delete button if owned.
	 * 
	 * @param {{@link ClickEvent} e
	 */
	@UiHandler("deleteButton")
	void onClick(ClickEvent e) {
		final ImageOverlay overlay = this;
		imageService.delete(capturedImage,
				new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						GalleryUpdatedEvent event = new GalleryUpdatedEvent();
						fireEvent(event);
						overlay.removeFromParent();
					}

					@Override
					public void onFailure(Throwable caught) {
					}
				});

	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		handlerManager.fireEvent(event);
	}

	public HandlerRegistration addGalleryUpdatedEventHandler(GalleryUpdatedEventHandler handler) {
		return handlerManager.addHandler(GalleryUpdatedEvent.TYPE, handler);
	}

}
