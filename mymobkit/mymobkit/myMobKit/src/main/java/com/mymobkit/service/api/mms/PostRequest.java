package com.mymobkit.service.api.mms;

import com.google.gson.annotations.Expose;
import com.mymobkit.enums.RequestMethod;
import com.mymobkit.model.Mms;
import com.mymobkit.service.api.WebApiResponse;

public final class PostRequest extends WebApiResponse {
	
	@Expose
	private Mms message;

    public PostRequest(){
        super(RequestMethod.POST);
    }

    public Mms getMessage() {
        return message;
    }

    public void setMessage(Mms message) {
        this.message = message;
    }
}


