package com.mymobkit.service.api.call;

import com.google.gson.annotations.Expose;
import com.mymobkit.enums.RequestMethod;
import com.mymobkit.service.api.WebApiResponse;

import java.util.List;

/**
 * Get request for call management API.
 */
public class GetRequest extends WebApiResponse {

    @Expose
    private List<CallHistory> callHistories;

    public GetRequest() {
        super(RequestMethod.GET);
    }

    public List<CallHistory> getCallHistories() {
        return callHistories;
    }

    public void setCallHistories(List<CallHistory> callHistories) {
        this.callHistories = callHistories;
    }
}