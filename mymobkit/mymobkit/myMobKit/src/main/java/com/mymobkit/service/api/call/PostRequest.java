package com.mymobkit.service.api.call;

import com.google.gson.annotations.Expose;
import com.mymobkit.enums.RequestMethod;
import com.mymobkit.service.api.WebApiResponse;

/**
 * Post request for call management API.
 *
 */
public class PostRequest extends WebApiResponse {

    @Expose
    protected String destination = "";

    public PostRequest() {
        super(RequestMethod.POST);
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}