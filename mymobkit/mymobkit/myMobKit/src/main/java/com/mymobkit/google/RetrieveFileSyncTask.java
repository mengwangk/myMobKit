package com.mymobkit.google;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Retrieve file from Google Drive
 */
public class RetrieveFileSyncTask extends ApiClientSyncTask<String, Void, byte[]> {

    private static final String TAG = makeLogTag(RetrieveFileSyncTask.class);

    private String resourceId;

    private static int RETRY_COUNT = 3;

    public RetrieveFileSyncTask(Context context) {
        super(context);
    }

    @Override
    protected byte[] executeConnected(String... params) {
        try {
            this.resourceId = params[0];
            if (TextUtils.isEmpty(resourceId)) return null;
            final DriveApi.DriveIdResult driveIdResult = Drive.DriveApi.fetchDriveId(getGoogleApiClient(), resourceId).await();
            if (!driveIdResult.getStatus().isSuccess()) {
                return null;
            }
            //final DriveFile file = Drive.DriveApi.getFile(getGoogleApiClient(), driveIdResult.getDriveId());
            final DriveFile file = driveIdResult.getDriveId().asDriveFile();
            final DriveApi.DriveContentsResult driveContentsResult = file.open(getGoogleApiClient(), DriveFile.MODE_READ_ONLY, null).await();
            if (!driveContentsResult.getStatus().isSuccess()) {
                return null;
            }
            final DriveContents driveContents = driveContentsResult.getDriveContents();
            for (int i = 0; i < RETRY_COUNT; i++) {
                try {
                    InputStream is = driveContents.getInputStream();
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    int nRead;
                    byte[] data = new byte[16384];
                    while ((nRead = is.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }
                    buffer.flush();
                    return buffer.toByteArray();
                } catch (Exception ex) {
                    LOGE(TAG, "[executeConnected] Unable to read input stream", ex);
                }
            }
            driveContents.discard(getGoogleApiClient());
            return null;
        } catch (Exception ex) {
            LOGE(TAG, "[executeConnected] Unable to retrieve files from Google Drive", ex);
            return null;
        }
    }
}