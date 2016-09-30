package com.mymobkit.service.api.media;

import java.util.List;

import com.google.gson.annotations.Expose;

public class GetImageRequest extends GetRequestImpl implements GetRequest<MediaImage> {

	@Expose
	private List<MediaImage> images;

	@Override
	public List<MediaImage> getMedia() {
		return images;
	}

	@Override
	public void setMedia(List<MediaImage> data) {
		this.images = data;
	}
}
