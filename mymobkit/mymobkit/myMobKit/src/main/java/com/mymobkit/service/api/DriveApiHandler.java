package com.mymobkit.service.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mymobkit.R;
import com.mymobkit.app.AppConfig;
import com.mymobkit.service.HttpdService;
import com.mymobkit.service.api.drive.GetRequest;
import com.mymobkit.service.api.drive.ListFileSyncTask;

import java.util.Map;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Google Drive API handler.
 */
public class DriveApiHandler extends ApiHandler {

    private static final String TAG = makeLogTag(DriveApiHandler.class);

    public static final String PARAM_FOLDER_NAME = "folder";

    public static final String PARAM_MIME_TYPE = "mime";

    public static final String MEDIA_TYPE_DRIVE = "drive";

    public static final String PARAM_PAGE_SIZE = "pageSize";

    /**
     * Constructor.
     *
     * @param service HTTPD service.
     */
    public DriveApiHandler(final HttpdService service) {
        super(service);
    }


    @Override
    public String get(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
        GetRequest request = new GetRequest();

        // Get parameter values
        final String folderName = getStringValue(PARAM_FOLDER_NAME, params);
        final String mimeType = getStringValue(PARAM_MIME_TYPE, params);
        final int pageSize = getIntegerValue(PARAM_PAGE_SIZE, params, 0);

        try {
            maybeAcquireWakeLock();
            final ListFileSyncTask task = new ListFileSyncTask(getContext());
            request.setFileInfos(task.execute(folderName, mimeType, String.valueOf(pageSize)));
            request.isSuccessful = true;
        } catch (Exception ex) {
            LOGE(TAG, "[get] Unable to retrieve files under folder " + folderName);
            request.isSuccessful = false;
            request.setDescription(String.format(getContext().getString(R.string.msg_unable_to_retrieve_drive_files), folderName));
        } finally {
            releaseWakeLock();
        }
        Gson gson = new GsonBuilder().setDateFormat(AppConfig.UNIVERSAL_DATE_FORMAT).excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(request);
    }
}
