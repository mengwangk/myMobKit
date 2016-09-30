package com.mymobkit.service.api.contact;

import com.google.gson.annotations.Expose;

/**
 * Contact phone.
 *
 */
public final class ContactPhone {

	@Expose
	private String number;
	
	@Expose
	private int type;
	
	@Expose
	private String label;
	
	@Expose
	private String displayLabel;
	
	
	public ContactPhone(String number, int type, String label, String displayLabel) {
		super();
		this.number = number;
		this.type = type;
		this.label = label;
		this.displayLabel = displayLabel;
	}

	public String getNumber() {
		return number;
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
