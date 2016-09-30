package com.mymobkit.opencv.image;

import org.opencv.core.Mat;

/**
 * Base implementation of the night vision algorithm.
 */
public abstract class NightVision implements INightVision {

    protected int width;
    protected int height;
    protected int imageQuality;

    protected Mat sourceFrame;
    protected Mat processedFrame;


    public NightVision(final int imageQuality, final int width, final int height) {
        this.imageQuality = imageQuality;
        this.width = width;
        this.height = height;
    }

    protected void releaseMat(Mat mat) {
        if (mat != null) {
            mat.release();
            mat = null;
        }
    }

    @Override
    public void release() {
        releaseMat(sourceFrame);
        releaseMat(processedFrame);
    }
}
