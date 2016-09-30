package com.mymobkit.ui.base;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Browser;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationServices;
import com.mymobkit.R;
import com.mymobkit.app.AppConfig;
import com.mymobkit.app.AppController;
import com.mymobkit.common.AppPreference;
import com.mymobkit.common.GcmUtils;
import com.mymobkit.common.GcmUtils.RegistrationStatus;
import com.mymobkit.common.HelpUtils;
import com.mymobkit.common.PlatformUtils;
import com.mymobkit.common.PlayServicesUtils;
import com.mymobkit.common.ServiceUtils;
import com.mymobkit.common.ToastUtils;
import com.mymobkit.common.UIUtils;
import com.mymobkit.gcm.RegistrationIntentService;
import com.mymobkit.service.ICameraService;
import com.mymobkit.service.webcam.WebcamService;
import com.mymobkit.ui.activity.ControlPanelActivity;
import com.mymobkit.ui.activity.ExploreActivity;
import com.mymobkit.ui.activity.ViewerActivity;
import com.mymobkit.ui.activity.WebcamActivity;
import com.mymobkit.ui.fragment.DetectionSettingsFragment;
import com.mymobkit.ui.fragment.ServiceSettingsFragment;

import java.util.ArrayList;
import java.util.List;

import haibison.android.lockpattern.LockPatternActivity;
import haibison.android.lockpattern.utils.AlpSettings;

import static com.mymobkit.common.LogUtils.LOGD;
import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.LOGI;
import static com.mymobkit.common.LogUtils.LOGW;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * A base activity that handles common functionality in the app.
 */
public class BaseActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = makeLogTag(BaseActivity.class);

    // Navigation drawer:
    private DrawerLayout drawerLayout;

    private ViewGroup drawerItemsListContainer;
    private Handler handler;

    // Primary toolbar and drawer toggle
    private Toolbar actionBarToolbar;

    // symbols for navdrawer items (indices must correspond to array below).
    // This is not a list of items that are necessarily *present* in the Nav Drawer;
    // rather, it's a list of all possible items.

    public static final int NAVDRAWER_ITEM_CONTROL_PANEL = 0;
    public static final int NAVDRAWER_ITEM_CAMERA = 1;
    public static final int NAVDRAWER_ITEM_VIEWER = 2;
    public static final int NAVDRAWER_ITEM_EXPLORE = 3;

    protected static final int NAVDRAWER_ITEM_INVALID = -1;
    protected static final int NAVDRAWER_ITEM_SEPARATOR = -2;
    protected static final int NAVDRAWER_ITEM_SEPARATOR_SPECIAL = -3;

    // titles for navdrawer items (indices must correspond to the above)
    private static final int[] NAVDRAWER_TITLE_RES_ID = new int[]{R.string.label_control_panel, R.string.label_video_streaming, R.string.label_viewer_mode, R.string.label_explore};

    // icons for navdrawer items (indices must correspond to above array)
    private static final int[] NAVDRAWER_ICON_RES_ID = new int[]{R.drawable.ic_control_panel, R.drawable.ic_camera, R.mipmap.ic_drawer_viewer, R.drawable.ic_drawer_explore};

    // delay to launch nav drawer item, to allow close animation to play
    private static final int NAVDRAWER_LAUNCH_DELAY = 250;

    // fade in and fade out durations for the main content when switching
    // between different Activities of the app through the Nav Drawer
    private static final int MAIN_CONTENT_FADEOUT_DURATION = 150;
    private static final int MAIN_CONTENT_FADEIN_DURATION = 250;

    // list of navdrawer items that were actually added to the navdrawer, in order
    private ArrayList<Integer> navDrawerItems = new ArrayList<Integer>();

    // views that correspond to each navdrawer item, null if not yet created
    private View[] navDrawerItemViews = null;

    // variables that control the Action Bar auto hide behavior (aka "quick recall")
    private boolean actionBarAutoHideEnabled = false;
    private boolean actionBarShown = true;

    // A Runnable that we should execute when the navigation drawer finishes its
    // closing animation
    private Runnable deferredOnDrawerClosedRunnable;

    protected static boolean isLoginValidated = false;
    protected static boolean isLoginRequested = false;

    protected static String gcmRegId = "";

    private BroadcastReceiver gcmRegistrationBroadcastReceiver;

    // Interface to the camera service
    private ICameraService cameraServiceProvider;

    private final List<Fragment> fragments = new ArrayList<Fragment>(3);

    protected static final int CAMERA_SERVICE_CONNECTED = 1000;
    protected static final int CAMERA_SERVICE_DISCONNECTED = 2000;

    protected static final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;

    protected Handler serviceHandler = new Handler() {

        @Override
        public void handleMessage(final Message msg) {
            LOGD(TAG, "[handleMessage] Received service status");
            switch (msg.what) {
                case CAMERA_SERVICE_CONNECTED:
                    try {
                        if (cameraServiceProvider != null) {
                            cameraServiceProvider.show();
                            finish();
                        }
                    } catch (Exception ex) {
                        LOGE(TAG, "[handleMessage] Error retrieving service status", ex);
                    }
                    break;
                case CAMERA_SERVICE_DISCONNECTED:
                    // Nothing to do
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler();

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeButtonEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
        }

        gcmRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                gcmRegId = GcmUtils.getRegistrationId(BaseActivity.this);
                if (TextUtils.isEmpty(gcmRegId)) {
                    ToastUtils.toastShort(BaseActivity.this, BaseActivity.this.getString(R.string.msg_gcm_registration_failure));
                }
            }
        };

        requestSystemAlertWindowPermission();
    }


    protected void requestSystemAlertWindowPermission() {
        // Only for Android M and above
        if (PlatformUtils.isMarshallowOrHigher()) {
            // TODO - change this when target for Android M - 23+
            if (!Settings.canDrawOverlays(this)) {
                final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                // startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }

    /**
     * Returns the navigation drawer item that corresponds to this Activity. Subclasses of BaseActivity override this to indicate what nav drawer item corresponds to them Return NAVDRAWER_ITEM_INVALID
     * to mean that this Activity should not have a Nav Drawer.
     */
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_INVALID;
    }

    /**
     * Sets up the navigation drawer as appropriate. Note that the nav drawer will be different depending on whether the attendee indicated that they are attending the event on-site vs. attending
     * remotely.
     */
    private void setupNavDrawer() {
        // What nav drawer item should be selected?
        int selfItem = getSelfNavDrawerItem();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout == null) {
            return;
        }
        drawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.theme_primary_dark));
        if (selfItem == NAVDRAWER_ITEM_INVALID) {
            // do not show a nav drawer
            View navDrawer = drawerLayout.findViewById(R.id.navdrawer);
            if (navDrawer != null) {
                ((ViewGroup) navDrawer.getParent()).removeView(navDrawer);
            }
            drawerLayout = null;
            return;
        }

        if (actionBarToolbar != null) {
            actionBarToolbar.setNavigationIcon(R.drawable.ic_drawer);
            actionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                // run deferred action, if we have one
                if (deferredOnDrawerClosedRunnable != null) {
                    deferredOnDrawerClosedRunnable.run();
                    deferredOnDrawerClosedRunnable = null;
                }
                onNavDrawerStateChanged(false, false);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                onNavDrawerStateChanged(true, false);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                onNavDrawerStateChanged(isNavDrawerOpen(), newState != DrawerLayout.STATE_IDLE);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                onNavDrawerSlide(slideOffset);
            }
        });

        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // populate the nav drawer with the correct items
        populateNavDrawer();
    }


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getActionBarToolbar();
    }

    /**
     * Subclasses can override this for custom behavior
     *
     * @param isOpen      Open status
     * @param isAnimating
     */
    protected void onNavDrawerStateChanged(boolean isOpen, boolean isAnimating) {
        if (actionBarAutoHideEnabled && isOpen) {
            autoShowOrHideActionBar(true);
        }
    }

    protected void onNavDrawerSlide(float offset) {
    }

    protected boolean isNavDrawerOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    // Populates the navigation drawer with the appropriate items.
    private void populateNavDrawer() {
        navDrawerItems.clear();

        navDrawerItems.add(NAVDRAWER_ITEM_CONTROL_PANEL);
        navDrawerItems.add(NAVDRAWER_ITEM_SEPARATOR);
        navDrawerItems.add(NAVDRAWER_ITEM_CAMERA);
        navDrawerItems.add(NAVDRAWER_ITEM_SEPARATOR);
        navDrawerItems.add(NAVDRAWER_ITEM_VIEWER);
        navDrawerItems.add(NAVDRAWER_ITEM_SEPARATOR);
        navDrawerItems.add(NAVDRAWER_ITEM_EXPLORE);

        createNavDrawerItems();
    }

    private void createNavDrawerItems() {
        drawerItemsListContainer = (ViewGroup) findViewById(R.id.navdrawer_items_list);
        if (drawerItemsListContainer == null) {
            return;
        }
        navDrawerItemViews = new View[navDrawerItems.size()];
        drawerItemsListContainer.removeAllViews();
        int i = 0;
        for (int itemId : navDrawerItems) {
            navDrawerItemViews[i] = makeNavDrawerItem(itemId, drawerItemsListContainer);
            drawerItemsListContainer.addView(navDrawerItemViews[i]);
            ++i;
        }
    }

    /**
     * Sets up the given navdrawer item's appearance to the selected state. Note: this could also be accomplished (perhaps more cleanly) with state-based layouts.
     */
    private void setSelectedNavDrawerItem(int itemId) {
        if (navDrawerItemViews != null) {
            for (int i = 0; i < navDrawerItemViews.length; i++) {
                if (i < navDrawerItems.size()) {
                    int thisItemId = navDrawerItems.get(i);
                    formatNavDrawerItem(navDrawerItemViews[i], thisItemId, itemId == thisItemId);
                }
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupNavDrawer();

        View mainContent = findViewById(R.id.main_content);
        if (mainContent != null) {
            mainContent.setAlpha(0);
            if (PlatformUtils.isICSOrHigher())
                mainContent.animate().alpha(1).setDuration(MAIN_CONTENT_FADEIN_DURATION);
        } else {
            LOGW(TAG, "No view with ID main_content to fade in.");
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_home:
                Uri uriUrl = Uri.parse(getString(R.string.mymobkit_url));
                Intent intent = new Intent(Intent.ACTION_VIEW, uriUrl);
                intent.putExtra(Browser.EXTRA_APPLICATION_ID, this.getPackageName());
                startActivity(intent);
                break;
            case R.id.menu_about:
                HelpUtils.showAbout(this);

                // Testing
                // Intent i = new Intent(this, TestApiClientService.class);
                // this.startService(i);

                break;
            case R.id.menu_default_app:

                // Testing
                //Intent j = new Intent(this, TestApiClientService.class);
                //this.stopService(j);


                if (PlatformUtils.isKitKatOrHigher()) {
                    changeDefaultSmsApp();
                } else {
                    // Not supported
                    ToastUtils.toastShort(this, R.string.msg_unsupported_change_default_app);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void changeDefaultSmsApp() {
        final String packageName = getPackageName();
        final String defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this);
        if (!TextUtils.isEmpty(packageName) && packageName.equalsIgnoreCase(defaultSmsApp)) {
            ToastUtils.toastShort(this, R.string.msg_already_default_sms_app);
        } else if (!TextUtils.isEmpty(packageName)) {
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName);
            startActivityForResult(intent, AppConfig.CHANGED_DEFAULT_SMS_APP);
        }
    }

    private void goToNavDrawerItem(int item) {
        Intent intent = null;
        switch (item) {
            case NAVDRAWER_ITEM_CONTROL_PANEL:
                intent = new Intent(this, ControlPanelActivity.class);
                break;
            case NAVDRAWER_ITEM_CAMERA:
                // Check the preference first
                final boolean isBackgroundMode = AppPreference.getInstance().getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_BACKGROUND_CAMERA, Boolean.valueOf(this.getString(R.string.default_background_camera)));
                final boolean isServiceRunning = ServiceUtils.isServiceRunning(this, WebcamService.class);
                ComponentName cn = null;
                if (isBackgroundMode && !isServiceRunning) {
                    cn = ServiceUtils.startCameraService(this);
                    if (cn != null) {
                        bindService();
                    }

                    // WebRTC testing
                    //Intent test = new Intent(this, WebViewActivity.class);
                    //startActivity(test);


                } else if (!isBackgroundMode && isServiceRunning) {
                    ServiceUtils.stopCameraService(this);
                    intent = new Intent(this, WebcamActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                } else if (isBackgroundMode && isServiceRunning) {
                    if (cameraServiceProvider != null) {
                        try {
                            cameraServiceProvider.show();
                            finish();
                        } catch (Exception ex) {
                            LOGE(TAG, "Unable to show camera service", ex);
                        }
                    } else {
                        bindService();
                    }
                } else {
                    intent = new Intent(this, WebcamActivity.class);
                }
                break;
            case NAVDRAWER_ITEM_VIEWER:
                intent = new Intent(this, ViewerActivity.class);
                break;
            case NAVDRAWER_ITEM_EXPLORE:
                intent = new Intent(this, ExploreActivity.class);
                break;
            default:
                intent = new Intent(this, ControlPanelActivity.class);
        }
        if (intent != null) {
            startActivity(intent);
            finish();
        }
    }

    protected void onNavDrawerItemClicked(final int itemId) {
        if (itemId == getSelfNavDrawerItem()) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        if (isSpecialItem(itemId)) {
            goToNavDrawerItem(itemId);
        } else {
            // launch the target Activity after a short delay, to allow the
            // close animation to play
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToNavDrawerItem(itemId);
                }
            }, NAVDRAWER_LAUNCH_DELAY);

            // change the active item on the list so the user can see the item changed
            setSelectedNavDrawerItem(itemId);
            // fade out the main content
            View mainContent = findViewById(R.id.main_content);
            if (mainContent != null) {
                if (PlatformUtils.isJellyBeanOrHigher())
                    mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
    }


    @Override
    protected void onResume() {
        super.onResume();

        setupGoogleService();
        requestLogin();
        surveillanceMode();
        setupGoogleDrive();
    }

    public void setupGoogleService() {
        // Verifies the proper version of Google Play Services exists on the device.
        if (PlayServicesUtils.checkGooglePlaySevices(this)) {
            if (gcmRegId.isEmpty()) {
                gcmRegId = GcmUtils.getRegistrationId(this);
            }
            if (gcmRegId.isEmpty()) {
                // Start IntentService to register this application with GCM.
                final Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            } else {
                // Check again if the device is registered to the server
                final RegistrationStatus regStatus = GcmUtils.getRegistrationStatus(this);
                if (regStatus == RegistrationStatus.UNREGISTERED) {
                    // Register device to server
                    registerDeviceToServer();
                }
            }

            // ----------- Testing
            //final RegistrationStatus regStatus = GcmUtils.getRegistrationStatus(this);
            //if (regStatus == RegistrationStatus.REGISTERED) {
            //    final GcmMessage message = new MotionMessage(this);
            //    GcmUtils.broadcast(message);
            // }

        }
        LocalBroadcastManager.getInstance(this).registerReceiver(gcmRegistrationBroadcastReceiver, new IntentFilter(AppConfig.INTENT_GCM_REGISTRATION_COMPLETE_ACTION));
    }

    protected void surveillanceMode() {
        AppController.setSurveillanceMode(false);
        AppController.setSurveillanceShutdown(true);
    }

    public void setupGoogleDrive() {
        if (AppController.googleApiClient != null && (AppController.googleApiClient.isConnected() || AppController.googleApiClient.isConnecting())) {
            return;
        }
        // Check if Google Drive integration is enabled
        final boolean isGoogleDriveEnabled = AppPreference.getInstance().getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_ALARM_IMAGE_DRIVE_STORAGE, Boolean.valueOf(this.getString(R.string.default_alarm_image_drive_storage)));
        final String gmail = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_DEVICE_EMAIL_ADDRESS, this.getString(R.string.default_device_email_address));
        final boolean isDeviceTrackingEnabled = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_DEVICE_TRACKING, Boolean.valueOf(this.getString(R.string.default_device_tracking)));

        if ((isGoogleDriveEnabled || isDeviceTrackingEnabled) && !TextUtils.isEmpty(gmail)) {
            if (AppController.googleApiClient == null) {
                AppController.googleApiClient = new GoogleApiClient.Builder(this)
                        .addApi(Drive.API).addScope(Drive.SCOPE_FILE)
                        .addApi(LocationServices.API)
                        .setAccountName(gmail)
                        .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
            }
            if (AppController.googleApiClient != null) {
                if (!AppController.googleApiClient.isConnected() && !AppController.googleApiClient.isConnecting()) {
                    AppController.googleApiClient.connect();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister Google GCM registration broadcast receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(gcmRegistrationBroadcastReceiver);
    }

    @Override
    public void onStart() {
        LOGD(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        isLoginRequested = false;
        switch (requestCode) {
            case AppConfig.SECURITY_REQ_CREATE_PATTERN:
                // Pass to child fragment
                for (Fragment f : fragments) {
                    if (f instanceof ServiceSettingsFragment) {
                        f.onActivityResult(requestCode, resultCode, data);
                        break;
                    }
                }
                break;
            case AppConfig.SECURITY_REQ_ENTER_PATTERN:
                switch (resultCode) {
                    case RESULT_OK:
                        // The user passed
                        isLoginValidated = true;
                        break;
                    case RESULT_CANCELED:
                        // The user cancelled the task
                        finish();
                        break;
                    case LockPatternActivity.RESULT_FAILED:
                        // The user failed to enter the pattern
                        finish();
                        break;
                    case LockPatternActivity.RESULT_FORGOT_PATTERN:
                        // The user forgot the pattern and invoked your recovery activity
                        break;
                }
                break;
            case AppConfig.GOOGLE_REQUEST_CODE_RESOLUTION:
                if (resultCode == RESULT_OK) {
                    setupGoogleDrive();
                }
                break;
            case AppConfig.CHANGED_DEFAULT_SMS_APP:
                if (resultCode == RESULT_OK) {
                    ToastUtils.toastShort(this, R.string.msg_changed_default_app);
                } else {
                    ToastUtils.toastShort(this, R.string.msg_changed_default_app_failure);
                }
                break;
            case ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE:
                // TODO - change this when target for Android M - 23+
                if (!Settings.canDrawOverlays(this)) {
                    ToastUtils.toastLong(this, R.string.msg_overlay_permission_not_granted);
                }
                break;
            default:
                // Pass to child fragment
                if (fragments != null) {
                    for (Fragment f : fragments) {
                        f.onActivityResult(requestCode, resultCode, data);
                    }
                }
                break;
        }
    }

    @Override
    public void onStop() {
        LOGD(TAG, "onStop");
        super.onStop();
    }

    protected Toolbar getActionBarToolbar() {
        if (actionBarToolbar == null) {
            actionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (actionBarToolbar != null) {
                setSupportActionBar(actionBarToolbar);
            }
        }
        return actionBarToolbar;
    }

    protected void autoShowOrHideActionBar(boolean show) {
        if (show == actionBarShown) {
            return;
        }
        actionBarShown = show;
    }

    private View makeNavDrawerItem(final int itemId, ViewGroup container) {
        boolean selected = getSelfNavDrawerItem() == itemId;
        int layoutToInflate = 0;
        if (itemId == NAVDRAWER_ITEM_SEPARATOR) {
            layoutToInflate = R.layout.navdrawer_separator;
        } else if (itemId == NAVDRAWER_ITEM_SEPARATOR_SPECIAL) {
            layoutToInflate = R.layout.navdrawer_separator;
        } else {
            layoutToInflate = R.layout.navdrawer_item;
        }
        View view = getLayoutInflater().inflate(layoutToInflate, container, false);

        if (isSeparator(itemId)) {
            // we are done
            UIUtils.setAccessibilityIgnore(view);
            return view;
        }

        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        int iconId = itemId >= 0 && itemId < NAVDRAWER_ICON_RES_ID.length ? NAVDRAWER_ICON_RES_ID[itemId] : 0;
        int titleId = itemId >= 0 && itemId < NAVDRAWER_TITLE_RES_ID.length ? NAVDRAWER_TITLE_RES_ID[itemId] : 0;

        // set icon and text
        iconView.setVisibility(iconId > 0 ? View.VISIBLE : View.GONE);
        if (iconId > 0) {
            iconView.setImageResource(iconId);
        }
        titleView.setText(getString(titleId));
        formatNavDrawerItem(view, itemId, selected);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavDrawerItemClicked(itemId);
            }
        });

        return view;
    }

    private boolean isSpecialItem(int itemId) {
        return false;
    }

    private boolean isSeparator(int itemId) {
        return itemId == NAVDRAWER_ITEM_SEPARATOR || itemId == NAVDRAWER_ITEM_SEPARATOR_SPECIAL;
    }

    private void formatNavDrawerItem(View view, int itemId, boolean selected) {
        if (isSeparator(itemId)) {
            // not applicable
            return;
        }

        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        TextView titleView = (TextView) view.findViewById(R.id.title);

        if (selected) {
            view.setBackgroundResource(R.drawable.selected_navdrawer_item_background);
        }

        // configure its appearance according to whether or not it's selected
        titleView.setTextColor(selected ? getResources().getColor(R.color.navdrawer_text_color_selected) : getResources().getColor(R.color.navdrawer_text_color));
        iconView.setColorFilter(selected ? getResources().getColor(R.color.navdrawer_icon_tint_selected) : getResources().getColor(R.color.navdrawer_icon_tint));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (cameraServiceProvider != null) {
            unBindService();
        }
    }

    protected void requestLogin() {
        if (isLoginValidated || isLoginRequested)
            return;
        boolean isLoginRequired = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_LOCK_PATTERN_REQUIRED, false);
        if (isLoginRequired) {
            String savedPattern = AppController.getLockPattern();
            if (TextUtils.isEmpty(savedPattern)) {
                ToastUtils.toastShort(this, getString(R.string.msg_lock_pattern_not_configured));
                return;
            }
            Intent intent = new Intent(LockPatternActivity.ACTION_COMPARE_PATTERN, null, this, LockPatternActivity.class);
            intent.putExtra(LockPatternActivity.EXTRA_PATTERN, savedPattern.toCharArray());
            AlpSettings.Display.setMaxRetries(this, 5);
            isLoginRequested = true;
            startActivityForResult(intent, AppConfig.SECURITY_REQ_ENTER_PATTERN);
        }
    }

    /**
     * @return
     */
    protected String getDefaultTitle() {
        return AppController.appName();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
            isLoginValidated = false;
        } else {
            getFragmentManager().popBackStack();
        }
    }

    protected void registerDeviceToServer() {

        GcmUtils.registerToGcmServer(BaseActivity.this);

        /*
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                // You should send the registration ID to your server over
                // HTTP, so it can use GCM/HTTP or CCS to send messages to your app.
                GcmUtils.registerToGcmServer(BaseActivity.this);

                return true;
            }

            @Override
            protected void onPostExecute(Boolean status) {
            }
        }.execute();*/
    }


    /**
     * Called when {@code mGoogleApiClient} is connected.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        LOGI(TAG, "GoogleApiClient connected");
    }

    /**
     * Called when {@code mGoogleApiClient} is disconnected.
     */
    @Override
    public void onConnectionSuspended(int cause) {
        LOGI(TAG, "GoogleApiClient connection suspended");
    }

    /**
     * Called when {@code googleApiClient} is trying to connect but failed.
     * Handle {@code result.parse()} if there is a resolution is
     * available.
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        try {
            LOGI(TAG, "GoogleApiClient connection failed: " + result.toString());
            if (!result.hasResolution()) {
                try {
                    // show the localized error dialog.
                    GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
                } catch (Exception ex) {
                    LOGE(TAG, "[onConnectionFailed] Error showing error dialog", ex);
                }
                return;
            }
        } catch (Exception ex) {
            LOGE(TAG, "[onConnectionFailed] Exception", ex);
        }
        try {
            result.startResolutionForResult(this, AppConfig.GOOGLE_REQUEST_CODE_RESOLUTION);
        } catch (Exception e) {
            LOGE(TAG, "[onConnectionFailed] Exception while starting resolution activity", e);
        }
    }

    protected ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            cameraServiceProvider = ICameraService.Stub.asInterface(binder);
            serviceHandler.sendEmptyMessage(CAMERA_SERVICE_CONNECTED);
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            cameraServiceProvider = null;
            serviceHandler.sendEmptyMessage(CAMERA_SERVICE_DISCONNECTED);
        }
    };

    protected void bindService() {
        try {
            if (cameraServiceProvider == null) {
                Intent intent = new Intent(ICameraService.class.getName());
                intent.setPackage(AppConfig.PACKAGE_NAME);
                this.bindService(intent, this.serviceConnection, Context.BIND_AUTO_CREATE);
            }
        } catch (Exception ex) {
            LOGE(TAG, "[bindService] Unable to bind camera service", ex);
        }
    }

    protected void unBindService() {
        try {
            if (cameraServiceProvider != null) {
                this.unbindService(this.serviceConnection);
                cameraServiceProvider = null;
            }
        } catch (Exception ex) {
            LOGE(TAG, "[unBindService] Unable to unbind camera service", ex);
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        fragments.add(fragment);
    }
}
