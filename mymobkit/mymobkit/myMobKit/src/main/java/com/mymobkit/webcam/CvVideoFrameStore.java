package com.mymobkit.webcam;

import org.opencv.core.Mat;

public class CvVideoFrameStore extends Chain<CvVideoFrame, Mat> {

	private static final int DEFAULT_CHAIN_COUNT = 3;

	private CvVideoFrame[] frames;

	/*
	public CvVideoFrameStore(final int chainCount) {
		super(chainCount);
		setup();
	}
	*/

	public CvVideoFrameStore() {
		super(DEFAULT_CHAIN_COUNT);
		setup();
	}

	private void setup() {
		this.frames = new CvVideoFrame[this.chainCount];
		for (int i = 0; i < this.chainCount; i++) {
			frames[i] = new CvVideoFrame();
		}
	}

	public void assign(final Mat frame) {
		frames[currentPos].set(frame);
	}
	
	public CvVideoFrame current() {
		return frames[chainIdx];
	}

	public void release() {
		for (int i = 0; i < this.chainCount; i++) {
			frames[i].release();
		}
	}
}
