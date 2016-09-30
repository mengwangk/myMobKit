package com.mymobkit.ui.base;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import java.lang.reflect.Field;

import static com.mymobkit.common.LogUtils.makeLogTag;
import static com.mymobkit.common.LogUtils.LOGW;
import static com.mymobkit.common.LogUtils.LOGE;


public abstract class BaseActionBarActivity extends AppCompatActivity {

    private static final String TAG = makeLogTag(BaseActionBarActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (isMenuWorkaroundRequired()) {
            forceOverflowMenu();
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeScreenshotSecurity();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return (keyCode == KeyEvent.KEYCODE_MENU && isMenuWorkaroundRequired()) || super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && isMenuWorkaroundRequired()) {
            openOptionsMenu();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void initializeScreenshotSecurity() {
        //if (Build.VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH &&
        //        isScreenSecurityEnabled(this)) {
        //    getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        //} else {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        //}
    }

    /**
     * Modified from: http://stackoverflow.com/a/13098824
     */
    private void forceOverflowMenu() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (IllegalAccessException e) {
            LOGW(TAG, "[forceOverflowMenu] Failed to force overflow menu.");
        } catch (NoSuchFieldException e) {
            LOGW(TAG, "[forceOverflowMenu] Failed to force overflow menu.");
        }
    }

    protected void startActivitySceneTransition(Intent intent, View sharedView, String transitionName) {
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this, sharedView, transitionName)
                .toBundle();
        ActivityCompat.startActivity(this, intent, bundle);
    }

    @TargetApi(VERSION_CODES.LOLLIPOP)
    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }

    public static boolean isMenuWorkaroundRequired() {
        return Build.VERSION.SDK_INT < VERSION_CODES.KITKAT &&
                Build.VERSION.SDK_INT > VERSION_CODES.GINGERBREAD_MR1 &&
                ("LGE".equalsIgnoreCase(Build.MANUFACTURER) || "E6710".equalsIgnoreCase(Build.DEVICE));
    }

}
