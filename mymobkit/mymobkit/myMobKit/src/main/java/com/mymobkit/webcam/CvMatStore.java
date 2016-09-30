package com.mymobkit.webcam;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

public final class CvMatStore extends Chain<Mat, Mat> {

    private static final int DEFAULT_CHAIN_COUNT = 2;

    private Mat[] frameChain;
    private int frameHeight;
    private int frameWidth;

	/*
    public CvMatStore(final int chainCount, final int width, final int height) {
		super(chainCount);
		this.frameHeight = height;
		this.frameWidth = width;
		setup();
	}
	*/

    public CvMatStore(final int width, final int height) {
        super(DEFAULT_CHAIN_COUNT);
        this.frameHeight = height;
        this.frameWidth = width;
        setup();
    }

    private void setup() {
        this.frameChain = new Mat[this.chainCount];
        for (int i = 0; i < this.chainCount; i++) {
            frameChain[i] = new Mat(frameHeight + (frameHeight / 2), frameWidth, CvType.CV_8UC1);
        }
    }

    public void assign(final Mat frame) {
        frame.copyTo(frameChain[currentPos]);
    }

    public Mat current() {
        return frameChain[chainIdx];
    }

    public void release() {
        for (int i = 0; i < this.chainCount; i++) {
            releaseMat(frameChain[i]);
        }
    }

    private void releaseMat(Mat mat) {
        if (mat != null) {
            mat.release();
            mat = null;
        }
    }
}
