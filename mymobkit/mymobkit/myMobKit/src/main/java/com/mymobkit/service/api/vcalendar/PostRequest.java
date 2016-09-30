package com.mymobkit.service.api.vcalendar;

import com.google.gson.annotations.Expose;
import com.mymobkit.enums.RequestMethod;
import com.mymobkit.model.Mms;
import com.mymobkit.service.api.WebApiResponse;

public final class PostRequest extends WebApiResponse {

    @Expose
    private Mms vCalendar;

    public PostRequest(){
        super(RequestMethod.POST);
    }

    public Mms getvCalendar() {
        return vCalendar;
    }

    public void setvCalendar(Mms vCalendar) {
        this.vCalendar = vCalendar;
    }
}



