package com.mymobkit.gcm.command;

import android.content.Context;

import com.google.gson.Gson;
import com.mymobkit.app.AppController;
import com.mymobkit.common.ServiceUtils;
import com.mymobkit.gcm.GcmCommand;
import com.mymobkit.gcm.GcmMessage;
import com.mymobkit.gcm.message.SurveillanceMessage;
import com.mymobkit.service.webcam.WebcamService;

import static com.mymobkit.common.LogUtils.LOGD;
import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Command to start or stop surveillance.
 */
public class SurveillanceCommand extends GcmCommand {

    private static final String TAG = makeLogTag(SurveillanceCommand.class);

    @Override
    public GcmMessage execute(Context context, String type, String extraData) {
        LOGD(TAG, "Received GCM message: type=" + type + ", extraData=" + extraData);
        SurveillanceMessage message = null;
        try {
            message = new Gson().fromJson(extraData, SurveillanceMessage.class);
            if (message != null) {
                final GcmMessage.ActionCommand actionCommand = GcmMessage.ActionCommand.get(message.getActionCommand());
                final boolean isSurveillanceMode = AppController.isSurveillanceMode();

                if (actionCommand == GcmMessage.ActionCommand.START) {
                    if (!isSurveillanceMode) {
                        ServiceUtils.startWebcam(context);
                    }
                } else if (actionCommand == GcmMessage.ActionCommand.STOP) {
                    // Check if webcam service is running
                    final boolean isWebcamRunning = ServiceUtils.isServiceRunning(context, WebcamService.class);
                    if (isWebcamRunning) {
                        ServiceUtils.stopCameraService(context);
                    }
                    if (isSurveillanceMode) {
                        final GcmMessage gcmMessage = message;
                        // Post a message using the service bus
                        AppController.bus.post(gcmMessage);
                    }
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
