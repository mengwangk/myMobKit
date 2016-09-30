package com.mymobkit.service.api.media;

import com.google.gson.annotations.Expose;


public final class MediaImage {
	
	@Expose
	private String contentUri;
	@Expose
	private long id;
	@Expose
	private String displayName;
	@Expose
	private String bucketId;
	@Expose
	private String bucketDisplayName;
	@Expose
	private String data;
	@Expose
	private long dateTaken;
	@Expose
	private double latitude;
	@Expose
	private double longitude;
	@Expose
	private String mimeType;
	@Expose
	private long size;
	@Expose
	private int orientation;
	@Expose
	private String description;
	@Expose
	private int isPrivate;
	@Expose
	private String picasaId;
	@Expose
	private String width;
	@Expose
	private String height;
	
	
	
	public String getContentUri() {
		return contentUri;
	}

	
	public MediaImage(String contentUri, long id, String displayName, String bucketId, String bucketDisplayName, String data, long dateTaken, double latitude, double longitude, String mimeType,
			long size, int orientation, String description, int isPrivate, String picasaId, String width, String height) {
		super();
		this.contentUri = contentUri;
		this.id = id;
		this.displayName = displayName;
		this.bucketId = bucketId;
		this.bucketDisplayName = bucketDisplayName;
		this.data = data;
		this.dateTaken = dateTaken;
		this.latitude = latitude;
		this.longitude = longitude;
		this.mimeType = mimeType;
		this.size = size;
		this.orientation = orientation;
		this.description = description;
		this.isPrivate = isPrivate;
		this.picasaId = picasaId;
		this.width = width;
		this.height = height;
	}


	public long getId() {
		return id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getBucketId() {
		return bucketId;
	}

	public String getBucketDisplayName() {
		return bucketDisplayName;
	}

	public String getData() {
		return data;
	}

	public long getDateTaken() {
		return dateTaken;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getMimeType() {
		return mimeType;
	}

	public long getSize() {
		return size;
	}

	public int getOrientation() {
		return orientation;
	}

	public String getDescription() {
		return description;
	}

	public int getIsPrivate() {
		return isPrivate;
	}

	public String getPicasaId() {
		return picasaId;
	}

	public String getWidth() {
		return width;
	}

	public String getHeight() {
		return height;
	}
	

}
