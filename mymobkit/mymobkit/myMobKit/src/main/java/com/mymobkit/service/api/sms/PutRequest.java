package com.mymobkit.service.api.sms;

import com.google.gson.annotations.Expose;
import com.mymobkit.enums.RequestMethod;
import com.mymobkit.service.api.WebApiResponse;

public class PutRequest extends WebApiResponse {
	
	@Expose
	private int count;
	
	/**
	 * Constructor
	 */
	public PutRequest(){
		super(RequestMethod.PUT);
		count = 0;
	}
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
