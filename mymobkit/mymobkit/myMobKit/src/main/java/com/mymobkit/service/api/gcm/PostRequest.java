package com.mymobkit.service.api.gcm;

import com.mymobkit.enums.RequestMethod;
import com.mymobkit.service.api.WebApiResponse;

/**
 * GCM POST request.
 */
public class PostRequest extends WebApiResponse {

    public PostRequest() {
        super(RequestMethod.POST);
    }
}