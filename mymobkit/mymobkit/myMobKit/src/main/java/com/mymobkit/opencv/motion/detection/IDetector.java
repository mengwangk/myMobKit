package com.mymobkit.opencv.motion.detection;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public interface IDetector {
	
	Mat detect(CvCameraViewFrame frame);
	
	Mat detect(Mat source);
	
	boolean isDetected();
	
	void setContourThickness(int thickness);
	
	void setContourColor(Scalar color);
}
