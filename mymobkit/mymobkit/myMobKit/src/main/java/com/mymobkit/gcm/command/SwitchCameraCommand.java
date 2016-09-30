package com.mymobkit.gcm.command;

import android.content.Context;

import com.google.gson.Gson;
import com.mymobkit.app.AppController;
import com.mymobkit.gcm.GcmCommand;
import com.mymobkit.gcm.GcmMessage;
import com.mymobkit.gcm.message.SwitchCameraMessage;

import static com.mymobkit.common.LogUtils.LOGD;
import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Command to switch between front and rear camera.
 */
public class SwitchCameraCommand extends GcmCommand {

    private static final String TAG = makeLogTag(SwitchCameraCommand.class);

    @Override
    public GcmMessage execute(Context context, String type, String extraData) {
        LOGD(TAG, "Received GCM message: type=" + type + ", extraData=" + extraData);
        SwitchCameraMessage message = null;
        try {
            message = new Gson().fromJson(extraData, SwitchCameraMessage.class);
            if (message != null) {
                final GcmMessage.ActionCommand actionCommand = GcmMessage.ActionCommand.get(message.getActionCommand());
                final boolean isSurveillanceMode =  AppController.isSurveillanceMode();
                if (isSurveillanceMode &&
                        (actionCommand == GcmMessage.ActionCommand.FRONT_CAMERA || actionCommand == GcmMessage.ActionCommand.REAR_CAMERA)) {
                    final GcmMessage gcmMessage = message;
                    // Post a message using the service bus
                    AppController.bus.post(gcmMessage);
                } else {
                    LOGE(TAG, "[execute] Unknown action command");
                }
            }
        } catch (Exception ex) {
            LOGE(TAG, "[execute] Unable to get the correct GCM message", ex);
        }
        return message;
    }
}
