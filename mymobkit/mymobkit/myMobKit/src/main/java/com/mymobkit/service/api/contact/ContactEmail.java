package com.mymobkit.service.api.contact;

import com.google.gson.annotations.Expose;

public final class ContactEmail {
	
	@Expose
	private String email;
	
	@Expose
	private int type;
	
	@Expose
	private String label;
	
	@Expose
	private String displayLabel;
	
	
	public ContactEmail(String email, int type, String label, String displayLabel) {
		super();
		this.email = email;
		this.type = type;
		this.label = label;
		this.displayLabel = displayLabel;
	}

	public String getEmail() {
		return email;
	}

	public int getType() {
		return type;
	}
	

	public String getLabel() {
		return label;
	}


	public String getDisplayLabel() {
		return displayLabel;
	}
	
}
