package com.mymobkit.service.webcam;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * Custom view layout.
 */
public class WebcamLayout extends DrawerLayout {

    private CameraBase cameraLayout;

    public WebcamLayout(Context context) {
        this(context, null);
    }

    public WebcamLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WebcamLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setWebcamLayout(final CameraBase cameraLayout){
        this.cameraLayout = cameraLayout;
    }

    public void onCloseSystemDialogs(String reason) {
        if (!TextUtils.isEmpty(reason) && reason.toLowerCase().startsWith("home")) {
            // Hide camera when HOME key is pressed
            this.cameraLayout.hide();
        }
    }
}
