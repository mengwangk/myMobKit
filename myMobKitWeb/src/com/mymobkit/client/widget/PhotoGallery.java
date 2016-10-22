package com.mymobkit.client.widget;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.mymobkit.client.MyMobKitClient;
import com.mymobkit.client.event.GalleryUpdatedEvent;
import com.mymobkit.client.event.GalleryUpdatedEventHandler;
import com.mymobkit.client.service.CapturedImageService;
import com.mymobkit.client.service.CapturedImageServiceAsync;
import com.mymobkit.model.CapturedImage;

public class PhotoGallery extends Composite implements GalleryUpdatedEventHandler {

	private static final Logger logger = Logger.getLogger(PhotoGallery.class.getName());

	private static PhotoGalleryUiBinder uiBinder = GWT.create(PhotoGalleryUiBinder.class);

	CapturedImageServiceAsync imageService = GWT.create(CapturedImageService.class);

	interface PhotoGalleryUiBinder extends UiBinder<Widget, PhotoGallery> {
	}

	private static final int GALLERY_WIDTH = 5;

	@UiField
	FlexTable galleryTable;

	private MyMobKitClient parent;
	private String email;
	private PopupPanel imagePopup;

	public PhotoGallery(MyMobKitClient parent) {
		this.parent = parent;
		this.email = this.parent.getLoginUser().getNormalizedEmail();
		initWidget(uiBinder.createAndBindUi(this));
		refreshGallery();
	}

	public void refreshGallery() {
		logger.log(Level.INFO, "email " + email);
		imageService.getByEmail(email, new AsyncCallback<List<CapturedImage>>() {

			@Override
			public void onSuccess(List<CapturedImage> images) {
				galleryTable.clear();
				int currentColumn = 0;
				int currentRow = 0;
				RootPanel.get("message").setVisible(true);
				RootPanel.get("message").getElement().setInnerHTML("Loading... please wait");
				if (!images.isEmpty()) {
					for (final CapturedImage image : images) {
						Image imageWidget = createImageWidget(image);
						imageWidget.getElement().getStyle().setMargin(10, Unit.PX);
						galleryTable.setWidget(currentRow, currentColumn, imageWidget);
						currentColumn++;
						if (currentColumn >= GALLERY_WIDTH) {
							currentColumn = 0;
							currentRow++;
						}
					}
					RootPanel.get("message").setVisible(false);
				} else {
					RootPanel.get("message").getElement().setInnerHTML("No image found");
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				logger.log(Level.FINE, "Failed to refresh gallery", caught);
			}
		});
		if (imagePopup != null) {
			imagePopup.hide();
		}
	}

	private Image createImageWidget(final CapturedImage image) {
		final Image imageWidget = new Image();
		imageWidget.setUrl(image.getServingUrl() + "=s200");
		final DecoratedPopupPanel simplePopup = new DecoratedPopupPanel(true);

		imageWidget.addMouseOverHandler(new MouseOverHandler() {

			@Override
			public void onMouseOver(MouseOverEvent event) {
				Widget source = (Widget) event.getSource();
				int left = source.getAbsoluteLeft() + 10;
				int top = source.getAbsoluteTop() + source.getOffsetHeight() + 10;

				simplePopup.setWidth("150px");
				simplePopup.setWidget(new HTML(image.getDisplayName()));
				simplePopup.show();
				simplePopup.setPopupPosition(left, top);
			}
		});

		imageWidget.addMouseOutHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				simplePopup.hide();
			}
		});

		imageWidget.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Widget source = (Widget) event.getSource();
				ImageOverlay imageOverlay = new ImageOverlay(image, parent.getLoginUser());
				imageOverlay.addGalleryUpdatedEventHandler(PhotoGallery.this);

				imagePopup = new PopupPanel(true);
				imagePopup.setAnimationEnabled(true);
				imagePopup.setWidget(imageOverlay);
				imagePopup.setGlassEnabled(true);
				imagePopup.setAutoHideEnabled(true);

				int left = source.getAbsoluteLeft() + 30;
				int top = source.getAbsoluteTop() + 30;
				imagePopup.show();
				imagePopup.setPopupPosition(left, top);

				// imagePopup.center();
			}
		});

		return imageWidget;
	}

	@Override
	public void onGalleryUpdated(GalleryUpdatedEvent event) {
		refreshGallery();
	}

}
