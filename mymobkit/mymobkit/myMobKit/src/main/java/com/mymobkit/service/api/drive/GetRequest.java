package com.mymobkit.service.api.drive;

import com.google.gson.annotations.Expose;
import com.mymobkit.enums.RequestMethod;
import com.mymobkit.service.api.WebApiResponse;

import java.util.List;

/**
 * GET request for Google Drive info.
 */
public class GetRequest extends WebApiResponse {

    @Expose
    private List<DriveFileInfo> fileInfos;

    public GetRequest() {
        super(RequestMethod.GET);
    }

    public List<DriveFileInfo> getFileInfos() {
        return fileInfos;
    }

    public void setFileInfos(List<DriveFileInfo> fileInfos) {
        this.fileInfos = fileInfos;
    }
}
