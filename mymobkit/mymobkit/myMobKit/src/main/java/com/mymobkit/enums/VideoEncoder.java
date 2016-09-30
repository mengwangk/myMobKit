package com.mymobkit.enums;

import android.media.MediaRecorder;

public enum VideoEncoder implements MyMobKitEnumAsInt {

	DEFAULT(MediaRecorder.VideoEncoder.DEFAULT), 
	H263(MediaRecorder.VideoEncoder.H263), 
	H264(MediaRecorder.VideoEncoder.H264), 
	MPEG_4_SP(MediaRecorder.VideoEncoder.MPEG_4_SP), 
	VP8(MediaRecorder.VideoEncoder.VP8);

	private int hashCode;

	VideoEncoder(int hashCode) {
		this.hashCode = hashCode;
	}

	public int getHashCode() {
		return this.hashCode;
	}

	public static VideoEncoder get(final int hashCode) {
		if (H263.getHashCode() == hashCode) {
			return H263;
		} else if (H264.getHashCode() == hashCode) {
			return H264;
		} else if (MPEG_4_SP.getHashCode() == hashCode) {
			return MPEG_4_SP;
		} else if (VP8.getHashCode() == hashCode) {
			return VP8;
		} else {
			return DEFAULT;
		}
	}
}
