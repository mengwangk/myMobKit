package com.mymobkit.ui.handler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.mymobkit.R;
import com.mymobkit.google.RetrieveFileSyncTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.IOException;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Picasso request handler for Google Drive.
 */
public class DriveRequestHandler extends RequestHandler {

    private static int RETRY_COUNT = 3;

    private static final String TAG = makeLogTag(DriveRequestHandler.class);

    public static String SCHEMA_DRIVE = "DRIVE";

    private final Context context;

    public DriveRequestHandler(final Context context) {
        this.context = context;
    }

    @Override
    public boolean canHandleRequest(Request data) {
        final String scheme = data.uri.getScheme();
        return (SCHEMA_DRIVE.equalsIgnoreCase(scheme));
    }

    @Override
    public Result load(Request request, int networkPolicy) throws IOException {
        final String resourceId = request.uri.toString().replace(SCHEMA_DRIVE + ":", "");
        if (!TextUtils.isEmpty(resourceId)) {
            for (int i = 0 ; i < RETRY_COUNT; i++) {
                final byte[] data = new RetrieveFileSyncTask(context).execute(resourceId);
                if (data != null) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        return new Result(bitmap, Picasso.LoadedFrom.NETWORK);
                    } catch (Exception ex) {
                        LOGE(TAG, "[load] Unable to load file", ex);
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e){}
                    }
                }
            }
        }
        throw new IOException(context.getString(R.string.msg_error_retrieving_image));
    }
}
