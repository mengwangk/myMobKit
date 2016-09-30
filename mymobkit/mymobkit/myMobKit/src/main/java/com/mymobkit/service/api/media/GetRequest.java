package com.mymobkit.service.api.media;

import java.util.List;


public interface GetRequest<T> {
	
	List<T> getMedia();
	
	void setMedia(List<T> data);

}
