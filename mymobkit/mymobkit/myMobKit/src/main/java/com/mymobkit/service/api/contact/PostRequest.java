package com.mymobkit.service.api.contact;

import com.google.gson.annotations.Expose;
import com.mymobkit.enums.RequestMethod;
import com.mymobkit.service.api.WebApiResponse;

/**
 * Post request to create contact.
 */
public class PostRequest extends WebApiResponse {

    @Expose
    protected Contact contact;

    public PostRequest() {
        super(RequestMethod.POST);
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}