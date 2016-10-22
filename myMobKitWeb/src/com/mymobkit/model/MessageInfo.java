package com.mymobkit.model;

public final class MessageInfo {

	public static final String MSG_TYPE_BYE = "bye";
	public static final String MSG_TYPE_OFFER = "offer";

	private String type;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
