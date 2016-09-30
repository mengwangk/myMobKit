package com.mymobkit.webcam;

public class VideoFrameStore extends Chain<VideoFrame, byte[]> {

	private static final int DEFAULT_CHAIN_COUNT = 3;	// Changed from 2 to 3 - June 28th 2016

	private VideoFrame[] frames;

	/*
	public VideoFrameStore(final int chainCount) {
		super(chainCount);
		setup();
	}
	*/

	public VideoFrameStore() {
		super(DEFAULT_CHAIN_COUNT);
		setup();
	}

	private void setup() {
		this.frames = new VideoFrame[this.chainCount];
		for (int i = 0; i < this.chainCount; i++) {
			frames[i] = new VideoFrame();
		}
	}

	public void assign(final byte[] frame) {
		frames[currentPos].set(frame);
	}
	
	public VideoFrame current() {
		return frames[chainIdx];
	}

	public void release() {
	}
}
