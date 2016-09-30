package com.mymobkit.service.api.contact;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.mymobkit.enums.RequestMethod;
import com.mymobkit.service.api.WebApiResponse;
public final class GetRequest extends WebApiResponse {
	
	@Expose
	private List<ContactInfo> contacts;

	public GetRequest(){
		super(RequestMethod.GET);
	}

	public List<ContactInfo> getContacts() {
		return contacts;
	}

	public void setContacts(List<ContactInfo> contacts) {
		this.contacts = contacts;
	}
}
