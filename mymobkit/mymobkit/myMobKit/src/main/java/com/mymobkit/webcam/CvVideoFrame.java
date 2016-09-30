package com.mymobkit.webcam;

import com.mymobkit.common.CvUtils;
import com.mymobkit.opencv.image.INightVision;

import org.opencv.core.Mat;

public class CvVideoFrame {

	private boolean isSent;
	private Mat frame;

	public CvVideoFrame() {
		isSent = false;
		frame = new Mat();
	}

	public synchronized boolean isSent() {
		return isSent;
	}

	public synchronized void setSent(boolean isSent) {
		this.isSent = isSent;
	}

	public Mat get() {
		return frame;
	}

	public void set(final Mat frame) {
		synchronized (this) {
			if (frame != null && this.frame != null && !frame.empty()) {	// Modified May 18th 2016
				frame.copyTo(this.frame);
			}
			this.isSent = false;
		}
	}

	public void release() {
		if (frame != null) {
			frame.release();
			frame = null;
		}
	}

	public byte[] getPicture(final int quality) {
		synchronized (this) {
			if (frame.empty())
				return null;
			return CvUtils.toJpegByteArray(frame, quality);
		}
	}

	public byte[] getPicture(final INightVision nightVision){
		synchronized (this) {
			if (frame.empty())
				return null;
			return nightVision.process(frame);
		}
	}
}