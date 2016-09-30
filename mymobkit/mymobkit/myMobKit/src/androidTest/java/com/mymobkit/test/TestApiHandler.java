package com.mymobkit.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;

import com.mymobkit.ui.activity.ControlPanelActivity;

public class TestApiHandler extends ActivityInstrumentationTestCase2<ControlPanelActivity> {

	// create a signal to let us know when our task is done.
	final CountDownLatch signal = new CountDownLatch(1);

	private ControlPanelActivity myActivity;

	public TestApiHandler() {
		super(ControlPanelActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setActivityInitialTouchMode(true);
		myActivity = getActivity();
	}

	

	@MediumTest
	public void testImageUpload() throws Throwable {

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
            }
        });
        
		signal.await(60, TimeUnit.SECONDS);
	}
}
