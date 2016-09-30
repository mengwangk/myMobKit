package com.mymobkit.gcm.command;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.LOGI;
import static com.mymobkit.common.LogUtils.makeLogTag;
import android.content.Context;

import com.google.gson.Gson;
import com.mymobkit.gcm.GcmCommand;
import com.mymobkit.gcm.GcmMessage;
import com.mymobkit.gcm.message.MotionMessage;
import com.mymobkit.gcm.message.WakeUpMessage;

public class WakeUpCommand extends GcmCommand {
    private static final String TAG = makeLogTag(WakeUpCommand.class);

    @Override
    public GcmMessage execute(Context context, String type, String extraData) {
        LOGI(TAG, "Received GCM message: type=" + type + ", extraData=" + extraData);
        WakeUpMessage message = null;
        try {
            message = new Gson().fromJson(extraData, WakeUpMessage.class);
        } catch (Exception ex) {
            LOGE(TAG, "[execute] Unable to get the correct GCM message", ex);
        }
        return message;
    }


}
