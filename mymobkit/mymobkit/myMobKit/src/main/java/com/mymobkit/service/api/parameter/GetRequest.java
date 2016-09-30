package com.mymobkit.service.api.parameter;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.Expose;
import com.mymobkit.enums.RequestMethod;
import com.mymobkit.service.api.WebApiResponse;

public final class GetRequest extends WebApiResponse {

	@Expose
	private Map<String, String> parameters;
	
	public GetRequest() {
		super(RequestMethod.GET);
		parameters = new HashMap<String, String>(1);
	}

	public void addParameter(String key, String value){
		parameters.put(key, value);
	}
	
}

