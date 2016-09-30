package com.mymobkit.webcam;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public final class WebcamOverlay extends View {
	
	private Bitmap bitmap = null;
	private Rect rect = null;

	public WebcamOverlay(final Context c, final AttributeSet attr) {
		super(c, attr);
	}

	public void drawResult(final Bitmap bmp) {
		if (rect == null)
			rect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
		bitmap = bmp;
		postInvalidate();
	}

	@Override
	protected void onDraw(final Canvas canvas) {
		if (bitmap != null) {
			canvas.drawBitmap(bitmap, null, rect, null);
		}
	}
}
