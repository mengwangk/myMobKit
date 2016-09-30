package com.mymobkit.service.api.vcard;

import com.google.gson.annotations.Expose;
import com.mymobkit.enums.RequestMethod;
import com.mymobkit.model.Mms;
import com.mymobkit.service.api.WebApiResponse;

public final class PostRequest extends WebApiResponse {

    @Expose
    private Mms vCard;

    public PostRequest(){
        super(RequestMethod.POST);
    }

    public Mms getvCard() {
        return vCard;
    }

    public void setvCard(Mms vCard) {
        this.vCard = vCard;
    }
}



