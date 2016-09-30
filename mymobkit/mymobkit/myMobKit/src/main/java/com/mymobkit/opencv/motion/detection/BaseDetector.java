package com.mymobkit.opencv.motion.detection;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public abstract class BaseDetector implements IDetector {

	protected boolean targetDetected = false;
	protected int contourThickness = 2;
	//protected Scalar contourColor = new Scalar(255, 0, 0); // RED
	protected Scalar contourColor = new Scalar(0, 0, 255);	 // BLUE
	//protected Scalar contourColor = new Scalar(0, 100, 100);	// RED for YUV

	@Override
	public Mat detect(CvCameraViewFrame frame) {
		return detect(frame.rgba());
	}

	@Override
	public boolean isDetected() {
		return targetDetected;
	}

	@Override
	public void setContourThickness(int thickness) {
		this.contourThickness = thickness;
	}

	@Override
	public void setContourColor(Scalar color) {
		this.contourColor = color;
	}
}
