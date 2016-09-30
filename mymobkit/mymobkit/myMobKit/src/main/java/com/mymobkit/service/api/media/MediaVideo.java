package com.mymobkit.service.api.media;

import com.google.gson.annotations.Expose;

public final class MediaVideo {
	
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
	private String description;
	@Expose
	private int isPrivate;
	@Expose
	private String width;
	@Expose
	private String height;
	@Expose
	private String category;
	@Expose
	private long duration;
	@Expose
	private String alblum;
	@Expose
	private String artist;
	
	
	public MediaVideo(String contentUri, long id, String displayName, String bucketId, String bucketDisplayName, String data, long dateTaken, double latitude, double longitude, String mimeType,
			long size, String description, int isPrivate, String width, String height, String category, long duration, String alblum, String artist) {
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
		this.description = description;
		this.isPrivate = isPrivate;
		this.width = width;
		this.height = height;
		this.category = category;
		this.duration = duration;
		this.alblum = alblum;
		this.artist = artist;
	}


	public String getContentUri() {
		return contentUri;
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


	public String getDescription() {
		return description;
	}


	public int getIsPrivate() {
		return isPrivate;
	}


	public String getWidth() {
		return width;
	}


	public String getHeight() {
		return height;
	}


	public String getCategory() {
		return category;
	}


	public long getDuration() {
		return duration;
	}


	public String getAlblum() {
		return alblum;
	}


	public String getArtist() {
		return artist;
	}
	
	
	
	
}
