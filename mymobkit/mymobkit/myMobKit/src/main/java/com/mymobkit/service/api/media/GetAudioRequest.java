package com.mymobkit.service.api.media;

import java.util.List;

import com.google.gson.annotations.Expose;

public class GetAudioRequest  extends GetRequestImpl implements GetRequest<MediaAudio> {

	@Expose
	private List<MediaAudio> audios;

	@Override
	public List<MediaAudio> getMedia() {
		return audios;
	}

	@Override
	public void setMedia(List<MediaAudio> data) {
		this.audios = data;
	}
}