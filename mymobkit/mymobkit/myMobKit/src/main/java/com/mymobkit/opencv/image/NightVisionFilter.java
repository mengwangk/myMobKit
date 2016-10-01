package com.mymobkit.opencv.image;

import com.mymobkit.common.CvUtils;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Night vision filter.
 *
 * @see <a href="http://www.ijceronline.com/papers/Vol4_issue03/Version-2/C04302025030.pdf">Bi-Level Weighted Histogram Equalization with Adaptive Gamma Correction</a>
 */
public class NightVisionFilter extends NightVision implements INightVision {

    private static final String TAG = makeLogTag(NightVisionFilter.class);

    private boolean isColor;
    private List<Mat> channels = new ArrayList<Mat>(3);
    private Mat ycrcb;

    private final static float DEFAULT_GAMMA = 1.0f;

    private Mat lutMat;
    protected float gamma;

    private boolean useGammaCorrection;
    private boolean useHistogramEqualization;

    /**
     * Constructor.
     *
     * @param imageQuality
     * @param width
     * @param height
     */
    public NightVisionFilter(final int imageQuality, final int width, final int height) {
        super(imageQuality, width, height);
        useGammaCorrection = false;
        useHistogramEqualization = true;
        this.gamma = DEFAULT_GAMMA;
        isColor = false;
    }

    public boolean isColor() {
        return isColor;
    }

    public void setColor(boolean color) {
        isColor = color;
    }

    public boolean isUseGammaCorrection() {
        return useGammaCorrection;
    }

    public void setUseGammaCorrection(boolean useGammaCorrection) {
        this.useGammaCorrection = useGammaCorrection;
    }

    public boolean isUseHistogramEqualization() {
        return useHistogramEqualization;
    }

    public void setUseHistogramEqualization(boolean useHistogramEqualization) {
        this.useHistogramEqualization = useHistogramEqualization;
    }

    public float getGamma() {
        return gamma;
    }

    public void setGamma(float gamma) {
        this.gamma = gamma;
        configureLut();
    }

    private void configureLut() {
        // synchronized (this) {
        //releaseMat(lutMat);
        //lutMat = new Mat(1, 256, CvType.CV_8UC1);
        double invGamma = 1.0 / gamma;
        int size = (int) (lutMat.total() * lutMat.channels());
        byte[] temp = new byte[size];
        lutMat.get(0, 0, temp);
        for (int j = 0; j < 256; ++j) {
            temp[j] = (byte) (Math.pow((double) j / 255.0, invGamma) * 255.0);
        }
        lutMat.put(0, 0, temp);
        //}
    }

    public void configure() {
        release();
        lutMat = new Mat(1, 256, CvType.CV_8UC1);
        configureLut();
        if (!isColor) {
            this.sourceFrame = new Mat(height + (height / 2), width, CvType.CV_8UC1);
            this.processedFrame = new Mat(height + (height / 2), width, CvType.CV_8UC1);
        } else {
            this.sourceFrame = new Mat(height + (height / 2), width, CvType.CV_8UC1);
            this.processedFrame = new Mat(height + (height / 2), width, CvType.CV_8UC3);
            this.ycrcb = new Mat();
        }
    }

    @Override
    public synchronized byte[] process(byte[] source) {
        if (source == null) return null;

        /*
        if (sourceFrame == null) {
            if (!isColor) {
                this.sourceFrame = new Mat(height + (height / 2), width, CvType.CV_8UC1);
                this.processedFrame = new Mat(height + (height / 2), width, CvType.CV_8UC1);
            } else {
                this.sourceFrame = new Mat(height + (height / 2), width, CvType.CV_8UC1);
                this.processedFrame = new Mat(height + (height / 2), width, CvType.CV_8UC3);
                this.ycrcb = new Mat();
            }
        }
        */

        try {
            sourceFrame.put(0, 0, source);

            if (useHistogramEqualization) {
                // convert sourceFrame to gray scale
                if (!isColor) {
                    Imgproc.cvtColor(sourceFrame, processedFrame, Imgproc.COLOR_YUV420p2GRAY);

                    // Apply Histogram Equalization
                    Imgproc.equalizeHist(processedFrame, processedFrame);

                } else {
                    // Histogram equalization using YCrCb
                    Imgproc.cvtColor(sourceFrame, ycrcb, Imgproc.COLOR_YUV2RGBA_NV21, 4);
                    Imgproc.cvtColor(ycrcb, ycrcb, Imgproc.COLOR_RGB2YCrCb);
                    Core.split(ycrcb, channels);
                    Imgproc.equalizeHist(channels.get(0), channels.get(0));
                    Core.merge(channels, processedFrame);
                    Imgproc.cvtColor(processedFrame, processedFrame, Imgproc.COLOR_YCrCb2BGR);
                }
            }

            if (useGammaCorrection) {
                if (!useHistogramEqualization) {
                    if (isColor) {
                        // Gamma correction
                        Imgproc.cvtColor(sourceFrame, processedFrame, Imgproc.COLOR_YUV2BGRA_NV21);
                    } else {
                        Imgproc.cvtColor(sourceFrame, processedFrame, Imgproc.COLOR_YUV420p2GRAY);
                    }
                } else {
                    // ? Do nothing
                }
                Core.LUT(processedFrame, lutMat, processedFrame);
            }

            if (useHistogramEqualization || useGammaCorrection) {
                if (!isColor)
                    return CvUtils.grayToJpeg(processedFrame, imageQuality);
                else
                    return CvUtils.toJpegByteArray(processedFrame, imageQuality);
            } else {
                return source;
            }
            /*
            if (useHistogramEqualization) {
                if (!isColor)
                    return CvUtils.grayToJpeg(processedFrame, imageQuality);
                else
                    return CvUtils.toJpegByteArray(processedFrame, imageQuality);
            } else if (useGammaCorrection) {
                if (!isColor) {
                    return CvUtils.grayToJpeg(processedFrame, imageQuality);
                } else {
                    return CvUtils.toJpegByteArray(processedFrame, imageQuality);
                }
            } else {
                return source;
            }
            */

        } catch (Exception e) {
            LOGE(TAG, "[process] Error processing frame", e);
            return source;
        }
    }

    @Override
    public byte[] process(Mat source) {
        if (source == null || source.empty()) return null;

        /*
        if (processedFrame == null) {
            if (!isColor) {
                processedFrame = new Mat(height + (height / 2), width, CvType.CV_8UC1);
            } else {
                this.processedFrame = new Mat(height + (height / 2), width, CvType.CV_8UC3);
                this.ycrcb = new Mat();
            }
        }
        */
        try {
            if (useHistogramEqualization) {
                if (!isColor) {
                    // convert source to gray scale
                    Imgproc.cvtColor(source, processedFrame, Imgproc.COLOR_BGRA2GRAY);

                    // Apply Histogram Equalization
                    Imgproc.equalizeHist(processedFrame, processedFrame);

                } else {
                    // Histogram equalization using YCrCb
                    Imgproc.cvtColor(source, ycrcb, Imgproc.COLOR_BGR2YCrCb, 4);
                    Core.split(ycrcb, channels);
                    Imgproc.equalizeHist(channels.get(0), channels.get(0));
                    Core.merge(channels, processedFrame);
                    Imgproc.cvtColor(processedFrame, processedFrame, Imgproc.COLOR_YCrCb2BGR);
                }
            }

            if (useGammaCorrection) {
                if (useHistogramEqualization) {
                    Core.LUT(processedFrame, lutMat, processedFrame);
                } else {
                    if (!isColor) {
                        Imgproc.cvtColor(source, processedFrame, Imgproc.COLOR_BGRA2GRAY);
                        Core.LUT(processedFrame, lutMat, processedFrame);
                    } else {
                        Core.LUT(source, lutMat, processedFrame);
                    }
                }
            }

            if (useHistogramEqualization) {
                if (!isColor)
                    return CvUtils.grayToJpeg(processedFrame, imageQuality);
                else
                    return CvUtils.toJpegByteArray(processedFrame, imageQuality);
            } else if (useGammaCorrection) {
                if (!isColor) {
                    return CvUtils.grayToJpeg(processedFrame, imageQuality);
                } else {
                    return CvUtils.toJpegByteArray(processedFrame, imageQuality);
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            LOGE(TAG, "[process] Error processing frame", e);
            return null;
        }
    }

    @Override
    public void release() {
        super.release();
        releaseMat(lutMat);
        releaseMat(ycrcb);
    }
}
