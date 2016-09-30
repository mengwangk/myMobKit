package com.mymobkit.service.api.drive;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveStatusCodes;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filter;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.drive.query.SortOrder;
import com.google.android.gms.drive.query.SortableField;
import com.mymobkit.google.ApiClientSyncTask;

import java.util.ArrayList;
import java.util.List;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.LOGI;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * An sync task that list files in root folder or a specified folder name.
 * A number of blocking tasks are performed serially in a thread.
 * Each time, await() is called on the result which blocks
 * until the request has been completed.
 */
public class ListFileSyncTask extends ApiClientSyncTask<String, Void, List<DriveFileInfo>> {
    private String folderName;
    private String mimeType;
    private int pageSize;
    private DriveId folderDriveId;

    private static final long DEFAULT_INTERVAL_SECONDS = 90;
    private static long lastSyncTime = 0;
    private static long SYNC_INTERVAL_SECONDS = DEFAULT_INTERVAL_SECONDS;

    private static final String TAG = makeLogTag(ListFileSyncTask.class);

    public ListFileSyncTask(Context context) {
        super(context);
    }

    @Override
    protected List<DriveFileInfo> executeConnected(String... params) {
        List<DriveFileInfo> fileInfos = new ArrayList<DriveFileInfo>(1);
        this.folderName = params[0];
        this.mimeType = params[1];
        try {
            this.pageSize = Integer.parseInt(params[2]);
        } catch (Exception ex) {
            this.pageSize = 0;
        }

        try {
            findFolderDriveId();
            if (folderDriveId != null) {
                // List all files under this folder
                //final DriveFolder folder = Drive.DriveApi.getFolder(getGoogleApiClient(), folderDriveId);
                final DriveFolder folder = folderDriveId.asDriveFolder();
                final SortOrder sortOrder = new SortOrder.Builder().addSortDescending(SortableField.CREATED_DATE).build();
                final Query.Builder builder = new Query.Builder().setSortOrder(sortOrder);
                builder.addFilter(Filters.eq(SearchableField.TRASHED, false));
                if (!TextUtils.isEmpty(mimeType)) {
                    builder.addFilter(Filters.eq(SearchableField.MIME_TYPE, mimeType));
                }
                if (lastSyncTime == 0 | ((System.currentTimeMillis() - lastSyncTime) / 1000) > SYNC_INTERVAL_SECONDS) {
                    final Status status = Drive.DriveApi.requestSync(getGoogleApiClient()).await();
                    if (status.isSuccess()) {
                        lastSyncTime = System.currentTimeMillis();
                        SYNC_INTERVAL_SECONDS = DEFAULT_INTERVAL_SECONDS;
                        LOGI(TAG, "[executeConnected] Synced the drive content");
                    } else {
                        if (status.getStatusCode() == DriveStatusCodes.DRIVE_RATE_LIMIT_EXCEEDED) {
                            // Implement exponential backoff
                            // SYNC_INTERVAL_SECONDS *= 2;
                        }
                    }
                }

                final Query sortedQuery = builder.build();
                //final DriveApi.MetadataBufferResult metadataBufferResult = folder.listChildren(getGoogleApiClient()).await();
                final DriveApi.MetadataBufferResult metadataBufferResult = folder.queryChildren(getGoogleApiClient(), sortedQuery).await();
                if (metadataBufferResult.getStatus().isSuccess()) {
                    MetadataBuffer mdb = null;
                    try {
                        mdb = metadataBufferResult.getMetadataBuffer();
                        if (mdb != null) {
                            int count = 0;
                            for (Metadata md : mdb) {
                                if (md == null) continue;
                                if (!md.isTrashed()) {
                                    DriveFileInfo fileInfo = new DriveFileInfo(md.getTitle(), md.getDescription());
                                    fileInfo.setAlternateLink(md.getAlternateLink());
                                    fileInfo.setContentAvailability(md.getContentAvailability());
                                    fileInfo.setCreateDate(md.getCreatedDate());
                                    fileInfo.setDriveId(md.getDriveId().getResourceId());
                                    fileInfo.setEmbedLink(md.getEmbedLink());
                                    fileInfo.setFileExtension(md.getFileExtension());
                                    fileInfo.setFileSize(md.getFileSize());
                                    fileInfo.setIsFolder(md.isFolder());
                                    fileInfo.setLastViewedByMeDate(md.getLastViewedByMeDate());
                                    fileInfo.setModifiedByMeDate(md.getModifiedByMeDate());
                                    fileInfo.setModifiedDate(md.getModifiedDate());
                                    fileInfo.setMimeType(md.getMimeType());
                                    fileInfo.setOriginalFileName(md.getOriginalFilename());
                                    fileInfo.setFileExtension(md.getFileExtension());
                                    fileInfo.setQuotaBytesUsed(md.getQuotaBytesUsed());
                                    fileInfo.setSharedWithMeDate(md.getSharedWithMeDate());
                                    fileInfo.setWebContentLink(md.getWebContentLink());
                                    fileInfo.setWebViewLink(md.getWebViewLink());
                                    fileInfos.add(fileInfo);
                                    if (this.pageSize != 0 && ++count == this.pageSize) {
                                        break;
                                    }
                                }
                            }
                        }
                    } finally {
                        if (mdb != null) mdb.release();
                    }
                }
            }
        } catch (Exception ex) {
            LOGE(TAG, "[executeConnected] Unable to list files from Google Drive", ex);
        }
        return fileInfos;
        //return fileInfos.subList(0, 99); // first 100
    }

    private void findFolderDriveId() {
        final DriveFolder folder = Drive.DriveApi.getRootFolder(getGoogleApiClient());
        if (TextUtils.isEmpty(folderName)) {
            // Root folder
            folderDriveId = folder.getDriveId();
        } else {
            final List<Filter> filters = new ArrayList<Filter>();
            filters.add(Filters.eq(SearchableField.TRASHED, false));
            filters.add(Filters.eq(SearchableField.TITLE, folderName));
            final Query query = new Query.Builder().addFilter(Filters.and(filters)).build();
            DriveApi.MetadataBufferResult result = (folder == null)
                    ? Drive.DriveApi.query(getGoogleApiClient(), query).await()
                    : folder.queryChildren(getGoogleApiClient(), query).await();
            if (result.getStatus().isSuccess()) {
                MetadataBuffer mdb = null;
                try {
                    mdb = result.getMetadataBuffer();
                    if (mdb != null) {
                        for (Metadata md : mdb) {
                            if (md == null) continue;
                            if (md.isFolder()) {
                                folderDriveId = md.getDriveId();
                            }
                        }
                    }
                } finally {
                    if (mdb != null) mdb.release();
                }
            }
        }
    }
}