package com.mymobkit.service.api.location;

import com.google.gson.annotations.Expose;
import com.mymobkit.enums.RequestMethod;
import com.mymobkit.service.api.WebApiResponse;

/**
 * Location GET request.
 */
public class GetRequest extends WebApiResponse {

    @Expose
    private double latitude;

    @Expose
    private double longitude;

    public GetRequest() {
        super(RequestMethod.GET);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}