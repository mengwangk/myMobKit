package com.mymobkit.opencv.image;

import com.mymobkit.common.CvUtils;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import static com.mymobkit.common.LogUtils.makeLogTag;
import static com.mymobkit.common.LogUtils.LOGE;

/**
 * Night vision using histogram equalization.
 * <p/>
 * <p/>
 *
 * @see <a href="http://docs.opencv.org/2.4/doc/tutorials/imgproc/histograms/histogram_equalization/histogram_equalization.html">OpenCV Histogram Equalization</a>
 * @see <a href="http://grokbase.com/t/gg/android-opencv/131e63vgps/opencv4android-problems-with-equalizehist-function">How to use equalizeHist</a>
 * @see <a href="http://opencv-srf.blogspot.my/2013/08/histogram-equalization.html">How to use equalizeHist</a>
 * @see <a href="https://www.packtpub.com/packtlib/book/Application%20Development/9781783550593/02/ch02lvl1sec16/Enhancing%20the%20image%20contrast">Enhancing the image contrast</a>
 * @see <a href="https://books.google.com.my/books?id=9uVOCwAAQBAJ&pg=PA50&lpg=PA50&dq=histogram+equalization+opencv+YUV+image&source=bl&ots=uzzMzvZ1IY&sig=f04sqz2yPe9AU6DyTdA7-aie8Ho&hl=en&sa=X&redir_esc=y#v=onepage&q=histogram%20equalization%20opencv%20YUV%20image&f=false">OpenCV by Exanple - Page 50</a>
 * @see <a href="http://www.programcreek.com/java-api-examples/index.php?class=org.opencv.imgproc.Imgproc&method=equalizeHist">Java Code Examples for org.opencv.imgproc.Imgproc.equalizeHist()</a>
 * @see <a href="http://ibuzzlog.blogspot.my/2012/08/how-to-do-real-time-image-processing-in.html">How to do real time image processing in Android using OpenCV?</a>
 */
public class HistogramEqualization extends NightVision implements INightVision {

    private static final String TAG = makeLogTag(HistogramEqualization.class);

    private boolean isColor;
    private List<Mat> channels = new ArrayList<Mat>(3);
    private Mat ycrcb;

    /**
     * Constructor.
     *
     * @param imageQuality
     * @param width
     * @param height
     */
    public HistogramEqualization(final int imageQuality, final int width, final int height) {
        super(imageQuality, width, height);
        setColor(false);     // Default to gray
    }


    public boolean isColor() {
        return isColor;
    }

    public void setColor(boolean color) {
        isColor = color;
        release();  // Release all frames
    }

    @Override
    public byte[] process(final byte[] source) {
        try {
            if (source == null) return null;
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
            sourceFrame.put(0, 0, source);

            // convert sourceFrame to gray scale
            if (!isColor) {
                Imgproc.cvtColor(sourceFrame, processedFrame, Imgproc.COLOR_YUV420p2GRAY);

                // Apply Histogram Equalization
                Imgproc.equalizeHist(processedFrame, processedFrame);

                // Convert to JPEG
                return CvUtils.grayToJpeg(processedFrame, imageQuality);
            } else {
                // Histogram equalization using YCrCb
                Imgproc.cvtColor(sourceFrame, ycrcb, Imgproc.COLOR_YUV2RGBA_NV21, 4);
                Imgproc.cvtColor(ycrcb, ycrcb, Imgproc.COLOR_RGB2YCrCb);
                Core.split(ycrcb, channels);
                Imgproc.equalizeHist(channels.get(0), channels.get(0));
                Core.merge(channels, processedFrame);
                Imgproc.cvtColor(processedFrame, processedFrame, Imgproc.COLOR_YCrCb2BGR);
                return CvUtils.toJpegByteArray(processedFrame, imageQuality);
            }
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
                if (!isColor) {
                    processedFrame = new Mat(height + (height / 2), width, CvType.CV_8UC1);
                } else {
                    this.processedFrame = new Mat(height + (height / 2), width, CvType.CV_8UC3);
                    this.ycrcb = new Mat();
                }
            }

            if (!isColor) {
                // convert source to gray scale
                Imgproc.cvtColor(source, processedFrame, Imgproc.COLOR_BGRA2GRAY);

                // Apply Histogram Equalization
                Imgproc.equalizeHist(processedFrame, processedFrame);

                // Convert to JPEG
                return CvUtils.grayToJpeg(processedFrame, imageQuality);

            } else {
                // Histogram equalization using YCrCb
                Imgproc.cvtColor(source, ycrcb, Imgproc.COLOR_BGR2YCrCb, 4);
                Core.split(ycrcb, channels);
                Imgproc.equalizeHist(channels.get(0), channels.get(0));
                Core.merge(channels, processedFrame);
                Imgproc.cvtColor(processedFrame, processedFrame, Imgproc.COLOR_YCrCb2BGR);
                return CvUtils.toJpegByteArray(processedFrame, imageQuality);
            }
        } catch (Exception e) {
            LOGE(TAG, "[process] Unable to process Mat", e);
            return null;
        }
    }

    @Override
    public void release() {
        super.release();
        releaseMat(ycrcb);
    }
}
