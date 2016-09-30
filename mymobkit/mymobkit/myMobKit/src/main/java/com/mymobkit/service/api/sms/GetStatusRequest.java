package com.mymobkit.service.api.sms;

import com.google.gson.annotations.Expose;
import com.mymobkit.enums.RequestMethod;
import com.mymobkit.service.api.WebApiResponse;

public final class GetStatusRequest extends WebApiResponse {

	@Expose
	private Sms message;
	
	@Expose
	private String status = "";
	
	public GetStatusRequest() {
		super(RequestMethod.GET);
	}

	public Sms getMessage() {
		return message;
	}

	public void setMessage(Sms message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}