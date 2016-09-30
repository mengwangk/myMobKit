package com.mymobkit.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.LinearLayout;

import com.mymobkit.R;
import com.mymobkit.ui.base.BaseActionBarActivity;


/**
 * Introduction activity.
 */
public class IntroductionActivity extends Activity {

    private LinearLayout commentsLayout;
    //private XWalkView xWalkWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);
        commentsLayout = (LinearLayout) findViewById(R.id.principal);
       /* xWalkWebView = new XWalkView(this.getApplicationContext(), this);
        xWalkWebView.load("file:///android_asset/surveillance/index.html", null);
        commentsLayout.addView(xWalkWebView);*/
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.xwalk_embed_lib, menu);
        return true;
    }*/
}
