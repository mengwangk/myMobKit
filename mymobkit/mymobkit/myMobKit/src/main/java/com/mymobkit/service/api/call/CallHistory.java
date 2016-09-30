package com.mymobkit.service.api.call;

import com.google.gson.annotations.Expose;

import java.util.Date;

/**
 * Call history.
 */
public final class CallHistory {

    @Expose
    private String phoneNumber;

    @Expose
    private String callType;

    @Expose
    private Date callTime;

    @Expose
    private String callDuration;


    public CallHistory(String phoneNumber, String callType, Date callTime, String callDuration) {
        this.phoneNumber = phoneNumber;
        this.callType = callType;
        this.callTime = callTime;
        this.callDuration = callDuration;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCallType() {
        return callType;
    }

    public Date getCallTime() {
        return callTime;
    }

    public String getCallDuration() {
        return callDuration;
    }
}
