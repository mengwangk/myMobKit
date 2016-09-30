package com.mymobkit.gcm;

import android.content.Context;

public abstract class GcmCommand {
    public abstract GcmMessage execute(Context context, String type, String extraData);
}
