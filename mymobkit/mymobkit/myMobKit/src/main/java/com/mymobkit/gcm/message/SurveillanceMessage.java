package com.mymobkit.gcm.message;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.mymobkit.R;
import com.mymobkit.app.AppController;
import com.mymobkit.gcm.GcmMessage;

/**
 * GCM message to start or stop the surveillance.
 */
public class SurveillanceMessage extends GcmMessage {

    @Expose
    private String actionCommand;

    public SurveillanceMessage(final Context context) {
        super(context);
        this.action = ActionType.SURVEILLANCE.getType();
    }

    public String getActionCommand() {
        return actionCommand;
    }

    public void setActionCommand(String actionCommand) {
        this.actionCommand = actionCommand;
    }

    @Override
    public String getDescription() {
        return String.format(AppController.getContext().getString(R.string.msg_gcm_surveillance_control), deviceName);
    }
}