package com.mymobkit.opencv.image;

import org.opencv.core.Mat;

/**
 * Night vision filter interface.
 */
public interface INightVision {

    /**
     * Process the image matrix.
     *
     * @param source Source image data.
     * @return Processed image data.
     */
    byte[] process(final byte[] source);


    /**
     * Process the image matrix.
     *
     * @param source Source image data.
     * @return Processed image data.
     */
    byte[] process(final Mat source);

    void release();
}