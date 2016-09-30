package com.mymobkit.service.api.parameter;

import com.mymobkit.enums.RequestMethod;
import com.mymobkit.service.api.WebApiResponse;

public final class PostRequest extends WebApiResponse {

	public PostRequest() {
		super(RequestMethod.POST);
	}

}
