package com.mymobkit.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.mymobkit.R;

/**
 * Crosswalk webview activity.
 */
public class WebViewActivity extends Activity { // extends XWalkActivity {

    private LinearLayout commentsLayout;
    //private XWalkView xWalkWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);
        commentsLayout = (LinearLayout) findViewById(R.id.principal);
        //xWalkWebView = new XWalkView(this.getApplicationContext(), this);
    }

   /* @Override
    protected void onXWalkReady() {
        xWalkWebView.load("file:///android_asset/surveillance/index.html", null);
        commentsLayout.addView(xWalkWebView);
    }*/
}
