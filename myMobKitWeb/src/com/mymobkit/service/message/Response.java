package com.mymobkit.service.message;

import lombok.Getter;
import lombok.Setter;

import com.google.gson.annotations.Expose;

public abstract class Response {

	
	@Getter
	@Setter
	@Expose
	private int responseCode = ResponseCode.NOT_AUTHORIZED.getCode();
	
}