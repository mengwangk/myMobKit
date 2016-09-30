package com.mymobkit.google;

import android.content.Context;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.IOException;
import java.io.OutputStream;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Save a file to Google Drive
 */
public class SaveToGoogleDriveAsyncTask extends ApiClientAsyncTask<byte[], Void, Boolean> {
    private static final String TAG = makeLogTag(SaveToGoogleDriveAsyncTask.class);
    private String fileName;
    private String mimeType;
    private DriveId driveFolderId = null;

    public SaveToGoogleDriveAsyncTask(final Context context, final String fileName, final String mimeType, final DriveId driveFolderId) {
        super(context);
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.driveFolderId = driveFolderId;
    }

    @Override
    protected Boolean doInBackgroundConnected(byte[]... params) {
        final byte[] fileData = params[0];
        if (fileData == null) return false;
        final DriveApi.DriveContentsResult driveContentsResult = Drive.DriveApi.newDriveContents(getGoogleApiClient()).await();

        if (!driveContentsResult.getStatus().isSuccess()) {
            return false;
        }

        // Write content to DriveContents
        OutputStream outputStream = driveContentsResult.getDriveContents().getOutputStream();
        try {
            outputStream.write(fileData);
        } catch (IOException e) {
            LOGE(TAG, "[saveFileToGoogleDrive] Error writing to Google Drive", e);
            return false;
        }

        //DriveFolder folder = Drive.DriveApi.getFolder(getGoogleApiClient(), driveFolderId);
        DriveFolder folder = driveFolderId.asDriveFolder();
        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(fileName)
                .setMimeType(mimeType)
                .setStarred(true).build();
        DriveFolder.DriveFileResult driveFileResult = folder.createFile(getGoogleApiClient(), changeSet, driveContentsResult.getDriveContents()).await();
        if (!driveFileResult.getStatus().isSuccess()) {
            LOGE(TAG, "[saveFileToGoogleDrive] Error writing to Google Drive");
            return false;
        }
        return true;
    }
}
