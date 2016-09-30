package com.mymobkit.service.api.drive;

import android.content.Context;
import android.text.TextUtils;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filter;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.drive.query.SortOrder;
import com.google.android.gms.drive.query.SortableField;
import com.mymobkit.R;
import com.mymobkit.common.AppPreference;
import com.mymobkit.common.MimeType;
import com.mymobkit.google.ApiClientAsyncTask;
import com.mymobkit.ui.fragment.DetectionSettingsFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Housekeep file under a Google Drive folder.
 */
public class HousekeepFileAsyncTask extends ApiClientAsyncTask<String, Void, Boolean> {

    private static final String TAG = makeLogTag(HousekeepFileAsyncTask.class);

    private String folderName;
    private String mimeType;
    private DriveId folderDriveId;

    public HousekeepFileAsyncTask(Context context) {
        super(context);
    }

    @Override
    protected Boolean doInBackgroundConnected(String... params) {
        this.folderName = params[0];
        this.mimeType = params[1];

        // Ignore for plain text now.
        if (MimeType.TEXT_PLAIN.equalsIgnoreCase(mimeType)) {
            return false;
        }
        try {
            findFolderDriveId();
            if (folderDriveId != null) {
                // Get the number of days of files to keep
                final int days = AppPreference.getInstance().getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_GOOGLE_DRIVE_DAYS_TO_KEEP, Integer.valueOf(context.getString(R.string.default_google_drive_days_to_keep)));
                final Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, days * -1);
                final Date dateBack = cal.getTime();

                // List all files under this folder
                //final DriveFolder folder = Drive.DriveApi.getFolder(getGoogleApiClient(), folderDriveId);
                final DriveFolder folder = folderDriveId.asDriveFolder();
                final SortOrder sortOrder = new SortOrder.Builder().addSortDescending(SortableField.MODIFIED_DATE).build();
                final Query.Builder builder = new Query.Builder().setSortOrder(sortOrder);
                builder.addFilter(Filters.eq(SearchableField.TRASHED, false));
                if (!TextUtils.isEmpty(mimeType)) {
                    builder.addFilter(Filters.eq(SearchableField.MIME_TYPE, mimeType));
                }
                final Query sortedQuery = builder.build();
                final DriveApi.MetadataBufferResult metadataBufferResult = folder.queryChildren(getGoogleApiClient(), sortedQuery).await();
                if (metadataBufferResult.getStatus().isSuccess()) {
                    final List<DriveId> filesToDelete = new ArrayList<DriveId>();
                    MetadataBuffer mdb = null;
                    try {
                        mdb = metadataBufferResult.getMetadataBuffer();
                        if (mdb != null) {
                            for (Metadata md : mdb) {
                                if (md == null) continue;
                                if (!md.isTrashed() && md.getModifiedByMeDate() != null && md.getModifiedByMeDate().before(dateBack)) {
                                    filesToDelete.add(md.getDriveId());
                                }
                            }
                        }
                    } finally {
                        if (mdb != null) mdb.release();
                    }
                    if (!filesToDelete.isEmpty()) {
                        // Delete the files
                        for (DriveId driveId : filesToDelete) {
                            //final DriveFile file = Drive.DriveApi.getFile(getGoogleApiClient(), driveId);
                            final DriveFile file = driveId.asDriveFile();
                            file.delete(getGoogleApiClient());
                        }
                    }
                    return true;
                }
            }
        } catch (Exception ex) {
            LOGE(TAG, "[doInBackgroundConnected] Unable to delete files from Google Drive", ex);
        }
        return false;
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
