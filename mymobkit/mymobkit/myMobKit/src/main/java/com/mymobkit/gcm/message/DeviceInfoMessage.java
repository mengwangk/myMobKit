package com.mymobkit.gcm.message;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.mymobkit.R;
import com.mymobkit.app.AppController;
import com.mymobkit.gcm.GcmMessage;

public class DeviceInfoMessage extends GcmMessage {

    @Expose
    private String uri;

    @Expose
    private boolean isServiceStarted;

    public DeviceInfoMessage(final Context context){
        super(context);
        this.action = ActionType.DEVICE_INFO.getType();
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isServiceStarted() {
        return isServiceStarted;
    }

    public void setIsServiceStarted(boolean isServiceStarted) {
        this.isServiceStarted = isServiceStarted;
    }

    @Override
    public String getDescription() {
        return String.format(AppController.getContext().getString(R.string.msg_gcm_device_info), deviceName);
    }
}

