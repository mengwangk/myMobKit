package com.mymobkit.common;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfInt;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * OpenCV helper class.
 */
public final class CvUtils {

    private static final Mat imageMatrix = new Mat();

    private static final String JPEG_EXTENSION = ".jpg";

    /**
     * Convert YUV to BGRA.
     */
    public static byte[] yuv420spToJpeg(final Mat frame, final int quality) {
        Imgproc.cvtColor(frame, imageMatrix, Imgproc.COLOR_YUV420sp2BGRA);
        return toJpegByteArray(imageMatrix, quality);
    }

    public static byte[] yuvToJpeg(final Mat frame, final int quality) {
        Imgproc.cvtColor(frame, imageMatrix, Imgproc.COLOR_YUV2BGRA_NV21);
        return toJpegByteArray(imageMatrix, quality);
    }
    /*
    public static byte[] rgbaToJpeg(final Mat frame, final int quality) {
		Imgproc.cvtColor(frame, imageMatrix, Imgproc.COLOR_RGBA2BGRA);
		return toJpegByteArray(imageMatrix, quality);
	}
	*/

	public static byte[] grayToJpeg(final Mat frame, final int quality) {
		Imgproc.cvtColor(frame, imageMatrix, Imgproc.COLOR_GRAY2BGRA);
		return toJpegByteArray(imageMatrix, quality);
	}

    /**
     * Convert image matrix to JPEG byte array.
     */
    public static byte[] toJpegByteArray(final Mat mat, final int quality) {
        if (mat.empty())
            return null;
        MatOfByte matOfByte = new MatOfByte();
        boolean ret = false;
        MatOfInt matParams = new MatOfInt(Imgcodecs.IMWRITE_JPEG_QUALITY, quality);
        ret = Imgcodecs.imencode(JPEG_EXTENSION, mat, matOfByte, matParams);
        if (ret) {
            return matOfByte.toArray();
        } else {
            return null;
        }
    }
}
