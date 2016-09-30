package com.mymobkit.opencv.image;

import com.mymobkit.common.CvUtils;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Gamma correction.
 *
 * @see <a href="http://docs.opencv.org/trunk/dc/d81/photo_8hpp.html#gsc.tab=0">photo.hpp</a>
 * @see <a href="http://docs.opencv.org/trunk/d8/d5e/classcv_1_1Tonemap.html#gsc.tab=0">Tonemap</a>
 * @see <a href="https://github.com/Itseez/opencv/blob/master/modules/photo/test/test_hdr.cpp">test_hdr.cpp</a>
 * @see <a href="https://vaamarnath.wordpress.com/2011/02/23/opencv-gamma-correction/">Gamma correction</a>
 * @see <a href="https://vaamarnath.wordpress.com/2011/02/23/opencv-gamma-correction/">JavaCV</a>
 * @see <a href="http://answers.opencv.org/question/35122/android-opencv-and-java-datatypes/">Android opencv and java datatypes</a>
 * @see <a href="http://www.pyimagesearch.com/2015/10/05/opencv-gamma-correction//">OpenCV Gamma Correction</a>
 */
public class GammaCorrection extends NightVision implements INightVision {

    private static final String TAG = makeLogTag(GammaCorrection.class);

    private final static float DEFAULT_GAMMA = 1.0f;
    private Mat lutMat;
    protected float gamma;

    /**
     * Constructor
     *
     * @param imageQuality
     * @param width
     * @param height
     */
    public GammaCorrection(final int imageQuality, final int width, final int height) {
        super(imageQuality, width, height);
        setGamma(DEFAULT_GAMMA);  // Default gamma level
    }

    public float getGamma() {
        return gamma;
    }

    public void setGamma(float gamma) {
        this.gamma = gamma;
        configureLut();
    }

    private void configureLut() {
        releaseMat(lutMat);
        lutMat = new Mat(1, 256, CvType.CV_8UC1);
        double invGamma = 1.0 / gamma;
        int size = (int) (lutMat.total() * lutMat.channels());
        byte[] temp = new byte[size];
        lutMat.get(0, 0, temp);
        for (int j = 0; j < 256; ++j) {
            temp[j] = (byte) (Math.pow((double) j / 255.0, invGamma) * 255.0);
        }
        lutMat.put(0, 0, temp);
    }

    @Override
    public byte[] process(final byte[] source) {
        try {
            if (source == null) return null;
            if (sourceFrame == null) {
                sourceFrame = new Mat(height + (height / 2), width, CvType.CV_8UC1);
                processedFrame = new Mat(height + (height / 2), width, CvType.CV_8UC1);
            }
            sourceFrame.put(0, 0, source);

            // Gamma correction
            Imgproc.cvtColor(sourceFrame, processedFrame, Imgproc.COLOR_YUV2BGRA_NV21);
            Core.LUT(processedFrame, lutMat, processedFrame);

            // Convert to JPEG
            return CvUtils.toJpegByteArray(processedFrame, imageQuality);
        } catch (Exception e) {
            LOGE(TAG, "[process] Unable to process byte[]", e);
            return null;
        }
    }

    @Override
    public byte[] process(Mat source) {
        try {
            if (source == null || source.empty()) return null;
            if (processedFrame == null) {
                processedFrame = new Mat(height + (height / 2), width, CvType.CV_8UC1);
            }

            //Imgproc.cvtColor(source, processedFrame, Imgproc.COLOR_RGBA2YUV_YV12);
            //Imgproc.cvtColor(source, processedFrame, Imgproc.COLOR_BGRA2YUV_YV12);
            //Imgproc.cvtColor(source, processedFrame, Imgproc.COLOR_BGR2YUV);

            // Gamma correction
            Core.LUT(source, lutMat, processedFrame);

            // Convert to JPEG
            return CvUtils.toJpegByteArray(processedFrame, imageQuality);
        } catch (Exception e) {
            LOGE(TAG, "[process] Unable to process Mat", e);
            return null;
        }
    }
}
