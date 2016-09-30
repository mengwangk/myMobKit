package com.mymobkit.google;

import android.content.Context;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filter;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.util.ArrayList;
import java.util.List;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.LOGI;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Create a folder if it does not exist
 */
public class CreateFolderAsyncTask extends ApiClientAsyncTask<Void, Void, DriveId> {

    private static final String TAG = makeLogTag(CreateFolderAsyncTask.class);

    private static final Object syncLock = new Object();

    private DriveId driveFolderId = null;
    private String folderName;

    public CreateFolderAsyncTask(Context context, String folderName) {
        super(context);
        this.folderName = folderName;
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

    @Override
    protected DriveId doInBackgroundConnected(Void... params) {
        try {
            synchronized (syncLock) {
                final DriveFolder folder = Drive.DriveApi.getRootFolder(getGoogleApiClient());
                List<Filter> filters = new ArrayList<Filter>();
                filters.add(Filters.eq(SearchableField.TRASHED, false));
                filters.add(Filters.eq(SearchableField.TITLE, folderName));
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
                                    break;
                                }
                            }
                        }
                    } finally {
                        if (mdb != null) mdb.release();
                    }
                }
                if (!isFolderFound) {
                    // Folder is not found, create the folder
                    if (!createGoogleDriveFolder(folderName)) return null;
                }
                return driveFolderId;
            }
        } catch (Exception ex) {
            LOGE(TAG, "[executeConnected] Failed to check folder", ex);
            return null;
        }
    }
}