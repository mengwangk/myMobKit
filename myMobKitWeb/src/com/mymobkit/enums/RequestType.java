package com.mymobkit.enums;

public enum RequestType {
	REQUEST_FOR_UPLOAD_URL("1");
	
	private String type;
	
	private RequestType(String type){
		this.type = type;
	}
	
	public String getType() {
		return this.type;
	}
}
