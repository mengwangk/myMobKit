package com.mymobkit.model;

public class SendStatus extends Response {

    public SendStatus(){
        super();
        setResponseCode(ResponseCode.SEND_FAILURE.getCode());
    }

}
