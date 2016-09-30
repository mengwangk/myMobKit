package com.mymobkit.google;

import android.content.Context;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filter;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.mymobkit.common.DeviceUtils;
import com.mymobkit.gcm.message.DeviceInfoMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.LOGI;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * An async task that creates a new text file by creating new contents and
 * metadata entities on user's root folder. A number of blocking tasks are
 * performed serially in a thread. Each time, await() is called on the
 * result which blocks until the request has been completed.
 */
public class TrackDeviceAsyncTask extends ApiClientAsyncTask<Void, Void, Boolean> {

    private static final String TAG = makeLogTag(TrackDeviceAsyncTask.class);

    private static final Object syncLock = new Object();

    private DriveId driveFolderId = null;
    private DeviceInfoMessage message;
    private String deviceId;

    public TrackDeviceAsyncTask(Context context, DeviceInfoMessage message) {
        super(context);
        this.message = message;
    }

    private boolean createGoogleDriveFolder(final String folderName) {
        try {
            final MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(folderName).build();

            // Create the folder
            DriveFolder.DriveFolderResult result = Drive.DriveApi.getRootFolder(getGoogleApiClient()).createFolder(getGoogleApiClient(), changeSet).await();
            if (!result.getStatus().isSuccess()) {
                return false;
            }
            driveFolderId = result.getDriveFolder().getDriveId();
            LOGI(TAG, "[createGoogleDriveFolder] Created a folder: " + result.getDriveFolder().getDriveId());
        } catch (Exception ex) {
            LOGE(TAG, "[createGoogleDriveFolder] Failed to create folder", ex);
            return false;
        }
        return true;
    }

    private boolean saveDeviceInfoToGoogleDrive() {
        //final DriveFolder folder = Drive.DriveApi.getFolder(getGoogleApiClient(), driveFolderId);
        final DriveFolder folder = driveFolderId.asDriveFolder();
        final String fileName = deviceId.replaceAll(" ", "_") + ".txt";
        final List<Filter> filters = new ArrayList<Filter>();
        filters.add(Filters.eq(SearchableField.TRASHED, false));
        filters.add(Filters.eq(SearchableField.TITLE, fileName));

        final Query query = new Query.Builder().addFilter(Filters.and(filters)).build();
        final DriveApi.MetadataBufferResult result = (folder == null)
                ? Drive.DriveApi.query(getGoogleApiClient(), query).await()
                : folder.queryChildren(getGoogleApiClient(), query).await();
        if (!result.getStatus().isSuccess()) return false;

        MetadataBuffer mdb = null;
        DriveId fileId = null;
        try {
            mdb = result.getMetadataBuffer();
            if (mdb != null) {
                for (Metadata md : mdb) {
                    if (md == null) continue;
                    if (!md.isFolder()) {
                        fileId = md.getDriveId();
                    }
                }
            }
        } finally {
            if (mdb != null) mdb.release();
        }

        if (fileId == null) {
            // Create the file for the 1st time
            final DriveApi.DriveContentsResult driveContentsResult = Drive.DriveApi.newDriveContents(getGoogleApiClient()).await();
            if (!driveContentsResult.getStatus().isSuccess()) {
                return false;
            }

            // write content to DriveContents
            OutputStream outputStream = driveContentsResult.getDriveContents().getOutputStream();
            try {
                outputStream.write(message.toJson().getBytes());
            } catch (IOException e) {
                LOGE(TAG, "[saveDeviceInfoToGoogleDrive] Error writing to Google Drive", e);
                return false;
            }

            final MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                    .setTitle(fileName)
                    .setMimeType("text/plain")
                    .setStarred(true).build();
            final DriveFolder.DriveFileResult driveFileResult = folder.createFile(getGoogleApiClient(), changeSet, driveContentsResult.getDriveContents()).await();
            return driveFileResult.getStatus().isSuccess();
        } else {
            // Edit the file
            //DriveFile file = Drive.DriveApi.getFile(getGoogleApiClient(), fileId);
            DriveFile file = fileId.asDriveFile();
            DriveApi.DriveContentsResult driveContentsResult = file.open(getGoogleApiClient(), DriveFile.MODE_WRITE_ONLY, null).await();
            if (!driveContentsResult.getStatus().isSuccess()) {
                return false;
            }
            DriveContents driveContents = driveContentsResult.getDriveContents();
            OutputStream outputStream = driveContents.getOutputStream();
            try {
                outputStream.write(message.toJson().getBytes());
            } catch (IOException e) {
                LOGE(TAG, "[saveDeviceInfoToGoogleDrive] Error writing to Google Drive", e);
                return false;
            }
            com.google.android.gms.common.api.Status status = driveContents.commit(getGoogleApiClient(), null).await();
            return status.getStatus().isSuccess();
        }
    }


    @Override
    protected Boolean doInBackgroundConnected(Void... params) {
        try {
            synchronized (syncLock) {
                deviceId = DeviceUtils.getDeviceId(context);
                final DriveFolder folder = Drive.DriveApi.getRootFolder(getGoogleApiClient());
                List<Filter> filters = new ArrayList<Filter>();
                filters.add(Filters.eq(SearchableField.TRASHED, false));
                filters.add(Filters.eq(SearchableField.TITLE, deviceId));
                Query query = new Query.Builder().addFilter(Filters.and(filters)).build();
                DriveApi.MetadataBufferResult result = (folder == null)
                        ? Drive.DriveApi.query(getGoogleApiClient(), query).await()
                        : folder.queryChildren(getGoogleApiClient(), query).await();
                boolean isFolderFound = false;
                if (result.getStatus().isSuccess()) {
                    MetadataBuffer mdb = null;
                    try {
                        mdb = result.getMetadataBuffer();
                        if (mdb != null) {
                            for (Metadata md : mdb) {
                                if (md == null) continue;
                                if (md.isFolder()) {
                                    driveFolderId = md.getDriveId();
                                    isFolderFound = true;
                                }
                            }
                        }
                    } finally {
                        if (mdb != null) mdb.release();
                    }
                }
                if (!isFolderFound) {
                    // Folder is not found, create the folder
                    if (!createGoogleDriveFolder(deviceId)) return false;
                }
                return saveDeviceInfoToGoogleDrive();
            }
        } catch (Exception ex) {
            LOGE(TAG, "[doInBackgroundConnected] Failed to check folder", ex);
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
    }
}