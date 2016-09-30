package com.mymobkit.service.api.ussd;

/**
 * Created by MEKOH on 2/13/2016.
 */
public class UssdSessionResponse {

    private final long timestamp;
    private final String response;

    public UssdSessionResponse(final String response, final long timestamp) {
        this.response = response;
        this.timestamp = timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getResponse() {
        return response;
    }

}