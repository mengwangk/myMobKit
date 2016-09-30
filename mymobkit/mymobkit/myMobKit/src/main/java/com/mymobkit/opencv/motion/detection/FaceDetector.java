package com.mymobkit.opencv.motion.detection;

import android.content.Context;

import com.mymobkit.R;
import com.mymobkit.app.AppController;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.LOGI;
import static com.mymobkit.common.LogUtils.makeLogTag;

public final class FaceDetector extends BaseDetector implements IDetector {

    private static final String TAG = makeLogTag(FaceDetector.class);

    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);

    private File cascadeFile;
    private CascadeClassifier faceDetector;
    private float relativeFaceSize = 0.2f;
    private int absoluteFaceSize = 0;


    public FaceDetector(final float faceSize) {
        try {
            setMinFaceSize(faceSize);

            // load cascade file from application resources
            InputStream is = AppController.getContext().getResources().openRawResource(R.raw.lbpcascade_frontalface);
            File cascadeDir = AppController.getContext().getDir("cascade", Context.MODE_PRIVATE);
            cascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(cascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            faceDetector = new CascadeClassifier(cascadeFile.getAbsolutePath());
            if (faceDetector.empty()) {
                LOGE(TAG, "Failed to load cascade classifier");
                faceDetector = null;
            } else {
                LOGI(TAG, "Loaded cascade classifier from " + cascadeFile.getAbsolutePath());
            }
            cascadeDir.delete();
        } catch (IOException e) {
            LOGE(TAG, "Failed to load cascade. Exception thrown: " + e, e);
        }
    }

    public void setMinFaceSize(float faceSize) {
        relativeFaceSize = faceSize;
        absoluteFaceSize = 0;
    }

    @Override
    public Mat detect(CvCameraViewFrame frame) {
        Mat cameraRgbaFrame = frame.rgba();
        if (faceDetector != null) {
            Mat cameraGrayFrame = frame.gray();
            if (absoluteFaceSize == 0) {
                int height = cameraGrayFrame.rows();
                if (Math.round(height * relativeFaceSize) > 0) {
                    absoluteFaceSize = Math.round(height * relativeFaceSize);
                }
            }
            MatOfRect faces = new MatOfRect();
            faceDetector.detectMultiScale(cameraGrayFrame, faces, 1.1, 2, 2, new Size(absoluteFaceSize, absoluteFaceSize), new Size());
            Rect[] facesArray = faces.toArray();
            for (Rect aFacesArray : facesArray) {

                // Highlight face
                // Core.rectangle(cameraRgbaFrame, facesArray[i].tl(),
                // facesArray[i].br(), FACE_RECT_COLOR, 3);

                Point center = new Point(aFacesArray.x + aFacesArray.width * 0.5, aFacesArray.y + aFacesArray.height * 0.5);
                Imgproc.ellipse(cameraRgbaFrame, center, new Size(aFacesArray.width * 0.5, aFacesArray.height * 0.5), 0, 0, 360, FACE_RECT_COLOR, contourThickness, Imgproc.LINE_8, 0);
            }
            targetDetected = facesArray.length > 0;
        }
        return cameraRgbaFrame;
    }

    @Override
    public Mat detect(Mat frame) {
        if (faceDetector != null) {
            Mat cameraGrayFrame = new Mat(frame.height(), frame.width(), CvType.CV_8UC1);
            Imgproc.cvtColor(frame, cameraGrayFrame, Imgproc.COLOR_BGRA2GRAY);
            if (absoluteFaceSize == 0) {
                int height = cameraGrayFrame.rows();
                if (Math.round(height * relativeFaceSize) > 0) {
                    absoluteFaceSize = Math.round(height * relativeFaceSize);
                }
            }
            MatOfRect faces = new MatOfRect();
            faceDetector.detectMultiScale(cameraGrayFrame, faces, 1.1, 2, 2, new Size(absoluteFaceSize, absoluteFaceSize), new Size());
            Rect[] facesArray = faces.toArray();
            int i = 0;
            while (i < facesArray.length) {

                // Highlight face
                // Core.rectangle(cameraRgbaFrame, facesArray[i].tl(),
                // facesArray[i].br(), FACE_RECT_COLOR, 3);

                Point center = new Point(facesArray[i].x + facesArray[i].width * 0.5, facesArray[i].y + facesArray[i].height * 0.5);
                Imgproc.ellipse(frame, center, new Size(facesArray[i].width * 0.5, facesArray[i].height * 0.5), 0, 0, 360, FACE_RECT_COLOR, contourThickness, Imgproc.LINE_8, 0);
                i++;
            }
            cameraGrayFrame.release();
            targetDetected = facesArray.length > 0;
        }
        return frame;
    }

}
