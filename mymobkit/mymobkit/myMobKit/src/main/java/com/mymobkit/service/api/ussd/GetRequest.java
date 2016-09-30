package com.mymobkit.service.api.ussd;

import com.google.gson.annotations.Expose;
import com.mymobkit.enums.RequestMethod;
import com.mymobkit.service.api.WebApiResponse;

/**
 * Get request for USSD
 */
public class GetRequest extends WebApiResponse {

    @Expose
    private String response;

    @Expose
    private String sessionId;

    public GetRequest() {
        super(RequestMethod.GET);
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}