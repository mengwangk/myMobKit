package com.mymobkit.service.api.mms;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.mymobkit.enums.RequestMethod;
import com.mymobkit.model.Mms;
import com.mymobkit.service.api.WebApiResponse;

public final class GetRequest extends WebApiResponse {
	
	@Expose
	private List<String> supportedContentTypes;

	@Expose
	private Mms message;
	
	public GetRequest(){
		super(RequestMethod.GET);
	}

	public List<String> getSupportedContentTypes() {
		return supportedContentTypes;
	}

	public void setSupportedContentTypes(List<String> supportedContentTypes) {
		this.supportedContentTypes = supportedContentTypes;
	}

	public Mms getMessage() {
		return message;
	}

	public void setMessage(Mms message) {
		this.message = message;
	}
}



