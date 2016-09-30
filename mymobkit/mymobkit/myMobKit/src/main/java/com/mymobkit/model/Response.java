package com.mymobkit.model;

import com.google.gson.annotations.Expose;

public abstract class Response {
	
	@Expose
	private int responseCode = ResponseCode.NOT_AUTHORIZED.getCode();

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	
}