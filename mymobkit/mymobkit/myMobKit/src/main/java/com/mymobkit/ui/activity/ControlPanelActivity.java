package com.mymobkit.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.mymobkit.R;
import com.mymobkit.app.AppController;
import com.mymobkit.common.AppPreference;
import com.mymobkit.common.PlatformUtils;
import com.mymobkit.ui.adapter.ControlPanelPageViewerTabsAdapter;
import com.mymobkit.ui.base.BaseActivity;
import com.mymobkit.ui.fragment.ServiceSettingsFragment;
import com.mymobkit.ui.widget.SlidingTabLayout;

import org.codechimp.apprater.AppRater;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

public class ControlPanelActivity extends BaseActivity {

    private static final String TAG = makeLogTag(ControlPanelActivity.class);

    private ViewPager viewPager = null;
    private ControlPanelPageViewerTabsAdapter viewPagerAdapter = null;
    private SlidingTabLayout slidingTabLayout = null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle(getDefaultTitle());

        // Configure default settings
        AppController.getDefaultSettings().configure();

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPagerAdapter = new ControlPanelPageViewerTabsAdapter(getFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);

        // it's PagerAdapter set.
        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);

        final Resources res = getResources();
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.tab_selected_strip));
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);

        if (slidingTabLayout != null) {
            slidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }

        overridePendingTransition(0, 0);

        // Perform any upgrade
        performUpgrade();

        // Rate the app
        rateApp();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void performUpgrade() {
        //final String token = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_GOOGLE_TALK_AUTH_TOKEN, "");
        //final boolean isDeviceTrackingEnabled = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_DEVICE_TRACKING, Boolean.valueOf(getString(R.string.default_device_tracking)));
        //if (isDeviceTrackingEnabled && TextUtils.isEmpty(token)) {
            // Show upgrade message and perform upgrade
        //    HelpUtils.showUpgrade(this);

        //    final String email = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_DEVICE_EMAIL_ADDRESS, this.getString(R.string.default_device_email_address));
        //    GTalkUtils.requestToken(this, new OnTokenAcquired(), email);
        //}
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        try {
            // Check current Android version
            String packageName = getPackageName();
            if (TextUtils.isEmpty(packageName)) {
                packageName = "com.mymobkit";
            }
            if (PlatformUtils.isKitKatOrHigher() && !Telephony.Sms.getDefaultSmsPackage(this).equals(packageName)) {

                boolean isChecked = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_SHOW_DEFAULT_SMS_ALERT, false);

                if (!isChecked) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    LayoutInflater inflater = LayoutInflater.from(this);
                    View layout = inflater.inflate(R.layout.custom_alert, null);
                    CheckBox checked = (CheckBox) layout.findViewById(R.id.skip);

                    checked.setChecked(isChecked);
                    checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            //Editor editor = pref.edit();
                            //editor.putBoolean(
                            //editor.commit();
                            AppPreference.getInstance().setValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_SHOW_DEFAULT_SMS_ALERT, isChecked);
                        }
                    });

                    alert.setView(layout);
                    alert.setIcon(R.drawable.ic_launcher);
                    alert.setTitle(R.string.label_title_default_app);
                    String message = getString(R.string.msg_configure_default_sms_app);
                    if (PlatformUtils.isMarshallowOrHigher()) {
                        message = getString(R.string.msg_android_m_system_alert_dialog_permission);
                    }
                    SpannableStringBuilder alertMsg = new SpannableStringBuilder();
                    alertMsg.append(Html.fromHtml(message));
                    alert.setMessage(alertMsg);

                    alert.setPositiveButton(R.string.label_dialog_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });
                    alert.show();
                }
            }
        } catch (Exception ex) {
            LOGE(TAG, "[onPostCreate] Problem checking default SMS app", ex);
        }
    }

    @Override
    protected void onNavDrawerStateChanged(boolean isOpen, boolean isAnimating) {
        super.onNavDrawerStateChanged(isOpen, isAnimating);
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_CONTROL_PANEL;
    }

    @Override
    protected String getDefaultTitle() {
        return getString(R.string.label_control_panel);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void rateApp() {
        AppRater.setLightTheme();
        AppRater.setDontRemindButtonVisible(true); // Hide the "No Thanks" button
        AppRater.app_launched(this);
    }
}
