package com.mymobkit.gcm.message;

import android.content.Context;

import com.mymobkit.R;
import com.mymobkit.app.AppController;
import com.mymobkit.gcm.GcmMessage;

/**
 * Motion event message.
 */
public class MotionMessage extends GcmMessage {

    public MotionMessage(final Context context){
        super(context);
        this.action = ActionType.MOTION_DETECTION.getType();
    }

    @Override
    public String getDescription() {
        return String.format(AppController.getContext().getString(R.string.msg_gcm_motion_detected), deviceName);
    }
}
