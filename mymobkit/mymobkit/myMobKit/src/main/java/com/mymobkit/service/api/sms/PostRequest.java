package com.mymobkit.service.api.sms;

import com.google.gson.annotations.Expose;
import com.mymobkit.enums.RequestMethod;
import com.mymobkit.service.api.WebApiResponse;

public final class PostRequest extends WebApiResponse {
	
	@Expose
	private Sms message;
	
	public PostRequest(){
		super(RequestMethod.POST);
	}

	public Sms getMessage() {
		return message;
	}

	public void setMessage(Sms message) {
		this.message = message;
	}
}

