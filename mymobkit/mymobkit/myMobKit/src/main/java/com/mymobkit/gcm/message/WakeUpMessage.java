package com.mymobkit.gcm.message;

import android.content.Context;

import com.mymobkit.R;
import com.mymobkit.app.AppController;
import com.mymobkit.gcm.GcmMessage;

public class WakeUpMessage extends GcmMessage {

    /**
     * Constructor
     *
     * @param context
     */
    public WakeUpMessage(final Context context){
        super(context);
        this.action = ActionType.WAKE_UP.getType();
    }

    @Override
    public String getDescription() {
        return String.format(AppController.getContext().getString(R.string.msg_gcm_wake_up), deviceName);
    }
}