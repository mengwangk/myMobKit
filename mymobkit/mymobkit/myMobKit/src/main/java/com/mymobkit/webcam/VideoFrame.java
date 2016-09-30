package com.mymobkit.webcam;

import com.mymobkit.common.ImageUtils;

public final class VideoFrame {

	private boolean isSent;
	private byte[] data;

	public VideoFrame() {
		isSent = false;
	}

	public synchronized boolean isSent() {
		return isSent;
	}

	public synchronized void setSent(boolean isSent) {
		this.isSent = isSent;
	}

	public byte[] get() {
		return data;
	}

	public void set(final byte[] frame) {
		this.data = frame;
		synchronized (this) {
			this.isSent = false;
		}
	}

	public byte[] getPicture(final android.graphics.Rect rect, final int width, final int height, final int previewFormat, final int quality) {
		return ImageUtils.yuvToJpeg(data, width, height, previewFormat, quality, rect);
	}
}