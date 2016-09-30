package com.mymobkit.service.api.media;

import java.util.List;

import com.google.gson.annotations.Expose;

public class GetVideoRequest extends GetRequestImpl implements GetRequest<MediaVideo> {

	@Expose
	private List<MediaVideo> videos;

	@Override
	public List<MediaVideo> getMedia() {
		return videos;
	}

	@Override
	public void setMedia(List<MediaVideo> data) {
		this.videos = data;
	}
}