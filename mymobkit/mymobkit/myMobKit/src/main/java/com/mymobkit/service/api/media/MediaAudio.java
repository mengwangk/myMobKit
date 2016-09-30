package com.mymobkit.service.api.media;

import com.google.gson.annotations.Expose;

public final class MediaAudio {
	
	@Expose
	private String contentUri;
	@Expose
	private long id;
	@Expose
	private String displayName;
	@Expose
	private String data;
	@Expose
	private long dateAdded;
	@Expose
	private String mimeType;
	@Expose
	private long size;
	@Expose
	private long duration;
	@Expose
	private String alblum;
	@Expose
	private int albumId;
	@Expose
	private String albumKey;
	@Expose
	private String artist;
	@Expose
	private int artistId;
	@Expose
	private String artistKey;
	@Expose
	private int bookmark;
	@Expose
	private String composer;
	@Expose
	private int isAlarm;
	@Expose
	private int isMusic;
	@Expose
	private int isNotification;
	@Expose
	private int isPodcast;
	@Expose
	private int isRingtone;
	@Expose
	private int track;
	@Expose
	private int year;
	
	
	public MediaAudio(String contentUri, long id, String displayName, String data, long dateAdded, String mimeType, long size, long duration, String alblum, int albumId, String albumKey, String artist,
			int artistId, String artistKey, int bookmark, String composer, int isAlarm, int isMusic, int isNotification, int isPodcast, int isRingtone, int track, int year) {
		super();
		this.contentUri = contentUri;
		this.id = id;
		this.displayName = displayName;
		this.data = data;
		this.dateAdded = dateAdded;
		this.mimeType = mimeType;
		this.size = size;
		this.duration = duration;
		this.alblum = alblum;
		this.albumId = albumId;
		this.albumKey = albumKey;
		this.artist = artist;
		this.artistId = artistId;
		this.artistKey = artistKey;
		this.bookmark = bookmark;
		this.composer = composer;
		this.isAlarm = isAlarm;
		this.isMusic = isMusic;
		this.isNotification = isNotification;
		this.isPodcast = isPodcast;
		this.isRingtone = isRingtone;
		this.track = track;
		this.year = year;
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

	public String getData() {
		return data;
	}

	public long getDateAdded() {
		return dateAdded;
	}

	public String getMimeType() {
		return mimeType;
	}

	public long getSize() {
		return size;
	}

	public long getDuration() {
		return duration;
	}

	public String getAlblum() {
		return alblum;
	}

	public int getAlbumId() {
		return albumId;
	}

	public String getAlbumKey() {
		return albumKey;
	}

	public String getArtist() {
		return artist;
	}

	public int getArtistId() {
		return artistId;
	}

	public String getArtistKey() {
		return artistKey;
	}

	public int getBookmark() {
		return bookmark;
	}

	public String getComposer() {
		return composer;
	}

	public int getIsAlarm() {
		return isAlarm;
	}

	public int getIsMusic() {
		return isMusic;
	}

	public int getIsNotification() {
		return isNotification;
	}

	public int getIsPodcast() {
		return isPodcast;
	}

	public int getIsRingtone() {
		return isRingtone;
	}

	public int getTrack() {
		return track;
	}

	public int getYear() {
		return year;
	}
	
}
