package com.mymobkit.gcm.command;

import android.content.Context;

import com.google.gson.Gson;
import com.mymobkit.gcm.GcmCommand;
import com.mymobkit.gcm.GcmMessage;
import com.mymobkit.gcm.message.MotionMessage;

import static com.mymobkit.common.LogUtils.LOGD;
import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

public class MotionDetectionCommand extends GcmCommand {

    private static final String TAG = makeLogTag(MotionDetectionCommand.class);

    @Override
    public GcmMessage execute(Context context, String type, String extraData) {
        LOGD(TAG, "Received GCM message: type=" + type + ", extraData=" + extraData);
        MotionMessage message = null;
        try {
            message = new Gson().fromJson(extraData, MotionMessage.class);
        } catch (Exception ex) {
            LOGE(TAG, "[execute] Unable to get the correct GCM message", ex);
        }
        return message;
    }
}
