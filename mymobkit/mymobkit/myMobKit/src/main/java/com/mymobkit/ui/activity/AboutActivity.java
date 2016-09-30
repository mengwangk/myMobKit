package com.mymobkit.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mymobkit.R;
import com.mymobkit.app.AppConfig;
import com.mymobkit.common.LocaleManager;
import com.mymobkit.ui.base.BaseActivity;

import static com.mymobkit.common.LogUtils.makeLogTag;

public final class AboutActivity extends BaseActivity {

    private static final String TAG = makeLogTag(AboutActivity.class);

    private static final String BASE_URL = "file:///android_asset/htmlhelp-" + LocaleManager.getTranslatedAssetLanguage() + '/';

    private WebView webView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle(getDefaultTitle());

        webView = (WebView) findViewById(R.id.help_contents);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        Bundle extras = getIntent().getExtras();
        String page = "about.html";
        if (extras != null) {
            page = extras.getString(AppConfig.PAGE_PARAM);
        }
        if (savedInstanceState == null) {
            webView.loadUrl(BASE_URL + page);
        } else {
            webView.restoreState(savedInstanceState);
        }
        webView.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
                return false;
            }
        });
        webView.setWebViewClient(new MyWebViewClient());

        overridePendingTransition(0, 0);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        String url = webView.getUrl();
        if (url != null && !"".equals(url)) {
            webView.saveState(state);
        }
    }

    @Override
    protected void onNavDrawerStateChanged(boolean isOpen, boolean isAnimating) {
        super.onNavDrawerStateChanged(isOpen, isAnimating);
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_EXPLORE;
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        // show the web page in webview but not in web browser
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    protected String getDefaultTitle() {
        return getString(R.string.label_explore);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, ExploreActivity.class);
        startActivity(intent);
        finish();
    }
}
