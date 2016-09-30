package com.mymobkit.service.api.gcm;

import com.google.gson.annotations.Expose;
import com.mymobkit.enums.RequestMethod;
import com.mymobkit.model.NotificationMsg;
import com.mymobkit.service.api.WebApiResponse;

import java.util.List;

public class GetRequest extends WebApiResponse {

    @Expose
    private List<NotificationMsg> messages;

    public GetRequest() {
        super(RequestMethod.GET);
    }

    public void setMessages(List<NotificationMsg> messages) {
        this.messages = messages;
    }

    public List<NotificationMsg> getMessages() {
        return messages;
    }
}