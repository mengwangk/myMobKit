package com.mymobkit.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import com.mymobkit.ui.activity.WebcamActivity;

public class TestHttpActivity extends ActivityInstrumentationTestCase2<WebcamActivity> {

	// create a signal to let us know when our task is done.
	final CountDownLatch signal = new CountDownLatch(1);
	byte[] fileData = null;

	private WebcamActivity myActivity;

	public TestHttpActivity() {
		super(WebcamActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(true);
		myActivity = getActivity();
		fileData = convertToBytes(myActivity.getAssets().open("htmlhelp/logo.png"));
	}

	

	@MediumTest
	public void testImageUpload() throws Throwable {

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
            }
        });
        
		/*// Execute the async task on the UI thread! THIS IS KEY!
		runTestOnUiThread(new Runnable() {

			@Override
			public void run() {
				// Kick start image uploading
				//Message msg = uploadHandler.obtainMessage();
				//msg.what = Integer.valueOf(UploadFeature.REQUEST_UPLOAD_URL.getHashCode());
				//uploadHandler.sendMessage(msg);
			}
		});*/

		signal.await(60, TimeUnit.SECONDS);
	}

	public static byte[] convertToBytes(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[16384];
		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		buffer.flush();
		return buffer.toByteArray();
	}
	
}