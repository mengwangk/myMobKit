package com.mymobkit.service.api.ussd;

import com.google.gson.annotations.Expose;
import com.mymobkit.enums.RequestMethod;
import com.mymobkit.service.api.WebApiResponse;
import com.mymobkit.service.api.sms.Sms;

/**
 * Created by MEKOH on 2/13/2016.
 */
public class PostRequest extends WebApiResponse {

    @Expose
    protected String sessionId = "";

    @Expose
    protected String ussdCommand = "";

    @Expose
    protected String responsePattern = "";

    public PostRequest() {
        super(RequestMethod.POST);
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUssdCommand() {
        return ussdCommand;
    }

    public void setUssdCommand(String ussdCommand) {
        this.ussdCommand = ussdCommand;
    }

    public String getResponsePattern() {
        return responsePattern;
    }

    public void setResponsePattern(String responsePattern) {
        this.responsePattern = responsePattern;
    }
}