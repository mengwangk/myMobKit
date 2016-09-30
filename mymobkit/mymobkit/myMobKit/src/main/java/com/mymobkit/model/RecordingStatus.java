package com.mymobkit.model;

public final class RecordingStatus {

	private String isRecording;
	private String fileName;
	
	public String getIsRecording() {
		return isRecording;
	}

	public String getFileName() {
		return fileName;
	}

	public RecordingStatus(String isRecording, String fileName) {
		super();
		this.isRecording = isRecording;
		this.fileName = fileName;
	}
	
	
	
}
