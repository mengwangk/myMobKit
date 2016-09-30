package com.mymobkit.ui.widget;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by MEKOH on 2/20/2016.
 */
public class ViewerDrawerLayout extends DrawerLayout {


    public ViewerDrawerLayout(Context context) {
        super(context);
    }

    public ViewerDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewerDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }
}