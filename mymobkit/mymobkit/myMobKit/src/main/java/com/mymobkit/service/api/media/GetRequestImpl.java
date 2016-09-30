package com.mymobkit.service.api.media;

import com.mymobkit.enums.RequestMethod;
import com.mymobkit.service.api.WebApiResponse;

public abstract class GetRequestImpl extends WebApiResponse {
	
	public GetRequestImpl(){
		super(RequestMethod.GET);
	}
}
