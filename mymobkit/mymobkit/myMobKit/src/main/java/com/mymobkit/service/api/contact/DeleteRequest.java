package com.mymobkit.service.api.contact;

import com.google.gson.annotations.Expose;
import com.mymobkit.enums.RequestMethod;
import com.mymobkit.service.api.WebApiResponse;

/**
 * Delete request for a contact.
 */
public class DeleteRequest extends WebApiResponse {

    @Expose
    private int count = 0;

    /**
     * Default constructor.
     */
    public DeleteRequest() {
        super(RequestMethod.DELETE);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
