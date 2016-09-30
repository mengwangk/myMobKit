package com.mymobkit.service.api.drive;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.Date;

/**
 * Drive file information.
 */
public class DriveFileInfo implements Parcelable {

    @Expose
    private String title;

    //    @Expose
    private String description;

    @Expose
    private boolean isFolder;

    //  @Expose
    private String alternateLink;

    // @Expose
    private int contentAvailability;

    @Expose
    private Date createDate;

    @Expose
    private String driveId;

    // @Expose
    private String embedLink;

    //  @Expose
    private String fileExtension;

    //@Expose
    private long fileSize;

    //  @Expose
    private Date lastViewedByMeDate;

    @Expose
    private String mimeType;

    //@Expose
    private Date modifiedByMeDate;

    @Expose
    private Date modifiedDate;

    // @Expose
    private String originalFileName;

    // @Expose
    private long quotaBytesUsed;

    //@Expose
    private Date sharedWithMeDate;

    //@Expose
    private String webContentLink;

    // @Expose
    private String webViewLink;


    public DriveFileInfo(final String title, final String description) {
        this.title = title;
        this.description = description;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setIsFolder(boolean isFolder) {
        this.isFolder = isFolder;
    }

    public String getAlternateLink() {
        return alternateLink;
    }

    public void setAlternateLink(String alternateLink) {
        this.alternateLink = alternateLink;
    }

    public int getContentAvailability() {
        return contentAvailability;
    }

    public void setContentAvailability(int contentAvailability) {
        this.contentAvailability = contentAvailability;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getDriveId() {
        return driveId;
    }

    public void setDriveId(String driveId) {
        this.driveId = driveId;
    }

    public String getEmbedLink() {
        return embedLink;
    }

    public void setEmbedLink(String embedLink) {
        this.embedLink = embedLink;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public Date getLastViewedByMeDate() {
        return lastViewedByMeDate;
    }

    public void setLastViewedByMeDate(Date lastViewedByMeDate) {
        this.lastViewedByMeDate = lastViewedByMeDate;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Date getModifiedByMeDate() {
        return modifiedByMeDate;
    }

    public void setModifiedByMeDate(Date modifiedByMeDate) {
        this.modifiedByMeDate = modifiedByMeDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public long getQuotaBytesUsed() {
        return quotaBytesUsed;
    }

    public void setQuotaBytesUsed(long quotaBytesUsed) {
        this.quotaBytesUsed = quotaBytesUsed;
    }

    public Date getSharedWithMeDate() {
        return sharedWithMeDate;
    }

    public void setSharedWithMeDate(Date sharedWithMeDate) {
        this.sharedWithMeDate = sharedWithMeDate;
    }

    public String getWebContentLink() {
        return webContentLink;
    }

    public void setWebContentLink(String webContentLink) {
        this.webContentLink = webContentLink;
    }

    public String getWebViewLink() {
        return webViewLink;
    }

    public void setWebViewLink(String webViewLink) {
        this.webViewLink = webViewLink;
    }


    public static Parcelable.Creator<DriveFileInfo> CREATOR = new Parcelable.Creator<DriveFileInfo>() {

        public DriveFileInfo createFromParcel(Parcel in) {
            return new DriveFileInfo(in);
        }

        public DriveFileInfo[] newArray(int size) {

            return new DriveFileInfo[size];
        }

    };

    public DriveFileInfo(Parcel in){
        super();
        createFromParcel(in);
    }

    public DriveFileInfo createFromParcel(Parcel source) {
        DriveFileInfo item = new DriveFileInfo("","");
        item.title = source.readString();
        item.description = source.readString();
        item.driveId = source.readString();
        item.mimeType = source.readString();
        return item;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(driveId);
        dest.writeString(mimeType);
    }
}
