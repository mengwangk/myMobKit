package com.mymobkit.webcam;

import org.opencv.core.Mat;

public class VideoDataStream extends DataStream {

	private VideoFrameStore videoFrames;
	private CvVideoFrameStore cvVideoFrames;
	
	public VideoDataStream(String addr) {
		super(addr);
		videoFrames = new VideoFrameStore();
		cvVideoFrames = new CvVideoFrameStore();
	}
	
	public void setVideoFrame(final byte[] frame){
		videoFrames.assign(frame);
	}
	
	public VideoFrame getVideoFrame(){
		VideoFrame frame = videoFrames.current();
		videoFrames.moveToNext();
		return frame;
	}
	
	public void setCvVideoFrame(final Mat frame){
		cvVideoFrames.assign(frame);
	}
	
	public CvVideoFrame getCvVideoFrame(){
		CvVideoFrame frame = cvVideoFrames.current();
		cvVideoFrames.moveToNext();
		return frame;
	}
	
	public void release() {
		super.release();
		videoFrames.release();
		cvVideoFrames.release();
	}
}
