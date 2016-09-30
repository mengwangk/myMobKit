package com.mymobkit.opencv.motion.detection;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;

import java.util.List;

import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Human detector.
 */
public final class HumanDetector extends BaseDetector implements IDetector {

    private static final String TAG = makeLogTag(HumanDetector.class);

    private HOGDescriptor hogDescriptor;
    final MatOfRect locations = new MatOfRect();
    final MatOfDouble weights = new MatOfDouble();
    //final Mat target = new Mat(source.height(), source.width(), CvType.CV_8UC1);
    final Mat target = new Mat();
    final Point rectPoint1 = new Point();
    final Point rectPoint2 = new Point();
    final Point fontPoint = new Point();

    public HumanDetector() {
        hogDescriptor = new HOGDescriptor();
        hogDescriptor.setSVMDetector(HOGDescriptor.getDefaultPeopleDetector());
    }

    @Override
    public Mat detect(Mat source) {

        if (!locations.empty()) locations.release();
        if (!weights.empty()) weights.release();

        //final Size winStride = new Size(8, 8);
        //final Size padding = new Size(32, 32);


        Imgproc.cvtColor(source, target, Imgproc.COLOR_BGRA2GRAY);
        hogDescriptor.detectMultiScale(target, locations, weights); //, 0.0, winStride, padding, 1.05, 2.0, false);

        /*
        final Rect[] found = locations.toArray();
        for (int i = 0; i < found.length; i++) {
            Rect rect = found[i];
            for (int j = 0; j < found.length; j++)
                if (j != i && (rect & found[j]) == rect)
                    break;
            if (j == found.size())
                found_filtered.push_back(r);
        }
        */

        // Variables for selection of areas in a photo

        //If there is a result - is added on a photo of area and weight of each of them
        if (locations.rows() > 0) {
            List<Rect> rectangles = locations.toList();
            int i = 0;
            List<Double> weightList = weights.toList();
            for (final Rect rect : rectangles) {
                float weight = weightList.get(i++).floatValue();
                rectPoint1.x = rect.x;
                rectPoint1.y = rect.y;
                fontPoint.x = rect.x;
                fontPoint.y = rect.y - 4;
                rectPoint2.x = rect.x + rect.width;
                rectPoint2.y = rect.y + rect.height;
                // It is added on images the found information
                Imgproc.rectangle(source, rectPoint1, rectPoint2, contourColor, contourThickness);
                Imgproc.putText(source, String.format("%1.2f", weight), fontPoint, Core.FONT_HERSHEY_PLAIN, 1.5, contourColor, 2, Core.LINE_AA, false);
            }
            targetDetected = true;
        } else {
            targetDetected = false;
        }
        target.release();
        return source;
    }
}
