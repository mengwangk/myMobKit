package com.mymobkit.model;

import java.io.Serializable;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
@Cache
@SuppressWarnings("serial")
public class CapturedImage implements Serializable {

	@Id
	Long id;

	@Index
	private String ownerEmail;

	private String blobKey;
	private String displayName;
	private long systemTimestamp;
	private String servingUrl;

	public CapturedImage() {

	}

	public CapturedImage(final String ownerEmail, final String blobKey, final String displayName, final String servingUrl) {
		this.ownerEmail = ownerEmail;
		this.blobKey = blobKey;
		this.displayName = displayName;
		this.servingUrl = servingUrl;
		this.systemTimestamp = System.currentTimeMillis();
	}

	public Long getId() {
		return id;
	}

	public String getBlobKey() {
		return blobKey;
	}

	public String getDisplayName() {
		return displayName;
	}

	public long getSystemTimestamp() {
		return systemTimestamp;
	}

	public String getServingUrl() {
		return servingUrl;
	}

	public String getOwnerEmail() {
		return ownerEmail;
	}

	@Override
	public String toString() {
		return "CapturedImage [id=" + id + ", ownerEmail=" + ownerEmail + ", blobKey=" + blobKey + ", displayName=" + displayName + ", systemTimestamp=" + systemTimestamp + ", servingUrl=" + servingUrl + "]";
	}

}
