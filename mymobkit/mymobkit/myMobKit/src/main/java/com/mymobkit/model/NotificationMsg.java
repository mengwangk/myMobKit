package com.mymobkit.model;

import com.google.gson.annotations.Expose;

public class NotificationMsg {

    @Expose
    private String id;

    @Expose
    private String action;

    @Expose
    private String value;

    @Expose
    private long timestamp;

    public NotificationMsg(String id, String action, String value, long timestamp) {
        this.id = id;
        this.action = action;
        this.value = value;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getAction() {
        return action;
    }

    public String getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
