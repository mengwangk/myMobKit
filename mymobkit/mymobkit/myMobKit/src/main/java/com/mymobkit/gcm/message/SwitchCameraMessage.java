package com.mymobkit.gcm.message;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.mymobkit.R;
import com.mymobkit.app.AppController;
import com.mymobkit.gcm.GcmMessage;

/**
 * GCM message to switch between front and rear cameras.
 */
public class SwitchCameraMessage extends GcmMessage {

    @Expose
    private String actionCommand;

    public SwitchCameraMessage(final Context context){
        super(context);
        this.action = ActionType.SWITCH_CAMERA.getType();
    }

    public String getActionCommand() {
        return actionCommand;
    }

    public void setActionCommand(String actionCommand) {
        this.actionCommand = actionCommand;
    }

    @Override
    public String getDescription() {
        return String.format(AppController.getContext().getString(R.string.msg_gcm_camera_control), deviceName);
    }
}
