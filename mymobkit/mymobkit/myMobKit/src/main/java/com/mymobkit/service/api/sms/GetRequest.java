package com.mymobkit.service.api.sms;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.mymobkit.enums.RequestMethod;
import com.mymobkit.service.api.WebApiResponse;

public final class GetRequest extends WebApiResponse {
	
	@Expose
	private List<Sms> messages;

	public GetRequest(){
		super(RequestMethod.GET);
	}

	
	public void setMessages(List<Sms> messages) {
		this.messages = messages;
	}

	public List<Sms> getMessages() {
		return messages;
	}
}
