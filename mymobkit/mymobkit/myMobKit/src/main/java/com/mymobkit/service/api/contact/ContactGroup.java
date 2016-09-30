package com.mymobkit.service.api.contact;

import com.google.gson.annotations.Expose;

public final class ContactGroup {

	@Expose
	private String id;
	
	@Expose
	private String name;

	public ContactGroup(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	
}
