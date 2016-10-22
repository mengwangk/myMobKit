package com.mymobkit.common;

public enum HttpResponse {

	ERROR("ERROR"), OK("OK");
	
	private String value;
	
	private HttpResponse(String value){
		this.value = value;
	}
	
	@Override
	public String toString(){
		return value;
	}
}
