package com.mymobkit.service.api.sensor;

import com.google.gson.annotations.Expose;
import com.mymobkit.common.DateUtils;
import com.mymobkit.enums.RequestMethod;
import com.mymobkit.service.api.WebApiResponse;

import java.util.Date;

/**
 * All sensor data
 */
public class GetRequest extends WebApiResponse {

    @Expose
    private Date timestamp;

    public GetRequest() {
        super(RequestMethod.GET);
        this.timestamp = new Date(System.currentTimeMillis());
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}