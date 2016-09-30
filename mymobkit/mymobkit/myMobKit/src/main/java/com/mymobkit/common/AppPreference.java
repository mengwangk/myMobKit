package com.mymobkit.common;

import android.content.Context;
import android.content.SharedPreferences;

import com.mymobkit.ui.activity.ViewerWidgetConfigureActivity;
import com.mymobkit.ui.fragment.DetectionSettingsFragment;
import com.mymobkit.ui.fragment.ServiceSettingsFragment;

/**
 * Preference configuration
 */
public final class AppPreference {

    private static AppPreference instance;
    private SharedPreferences servicesPreferences;
    private SharedPreferences detectionPreferences;
    private SharedPreferences appPreferences;
    private SharedPreferences widgetPreferences;

    public static final String APP_SHARED_PREF_NAMES = "app_prefs";

    public static final String KEY_SHOWN_COACH_MARK = "shown_coach_mark";

    private AppPreference(Context context) {
        servicesPreferences = context.getSharedPreferences(ServiceSettingsFragment.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        detectionPreferences = context.getSharedPreferences(DetectionSettingsFragment.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        appPreferences = context.getSharedPreferences(APP_SHARED_PREF_NAMES, Context.MODE_PRIVATE);
        widgetPreferences = context.getSharedPreferences(ViewerWidgetConfigureActivity.PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void initialize(Context context) {
        if (instance == null) {
            instance = new AppPreference(context);
        }
    }

    public static synchronized AppPreference getInstance() {
        if (instance == null) {
            throw new IllegalStateException(AppPreference.class.getSimpleName() +
                    " is not initialized, call initialize(..) method first.");
        }
        return instance;
    }


    public final <T> void setValue(final String preferenceName, final String key, final T value) {
        SharedPreferences prefs = this.servicesPreferences;
        if (DetectionSettingsFragment.SHARED_PREFS_NAME.equals(preferenceName)) {
            prefs = this.detectionPreferences;
        } else if (APP_SHARED_PREF_NAMES.equals(preferenceName)) {
            prefs = this.appPreferences;
        } else if (ViewerWidgetConfigureActivity.PREFS_NAME.equals(preferenceName)) {
            prefs = this.widgetPreferences;
        }
        final SharedPreferences.Editor editor = prefs.edit();

        if (value.getClass().equals(String.class)) {
            editor.putString(key, (String) value);
        } else if (value.getClass().equals(Boolean.class)) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value.getClass().equals(Integer.class)) {
            editor.putInt(key, (Integer) value);
        } else if (value.getClass().equals(Long.class)) {
            editor.putLong(key, (Long) value);
        }
        try {
            editor.apply();
        } catch (AbstractMethodError unused) {
            editor.commit();
        }
    }

    public final <T> T getValue(final String preferenceName, final String key, final T defaultValue) {
        SharedPreferences prefs = this.servicesPreferences;
        if (DetectionSettingsFragment.SHARED_PREFS_NAME.equals(preferenceName)) {
            prefs = this.detectionPreferences;
        } else if (APP_SHARED_PREF_NAMES.equals(preferenceName)) {
            prefs = this.appPreferences;
        } else if (ViewerWidgetConfigureActivity.PREFS_NAME.equals(preferenceName)) {
            prefs = this.widgetPreferences;
        }
        T value = null;
        if (defaultValue != null) {
            if (defaultValue.getClass().equals(String.class)) {
                value = (T) prefs.getString(key, (String) defaultValue);
            } else if (defaultValue.getClass().equals(Boolean.class)) {
                value = (T) Boolean.valueOf(prefs.getBoolean(key, (Boolean) defaultValue));
            } else if (defaultValue.getClass().equals(Integer.class)) {
                value = (T) Integer.valueOf(prefs.getInt(key, (Integer) defaultValue));
            } else if (defaultValue.getClass().equals(Long.class)) {
                value = (T) Long.valueOf(prefs.getLong(key, (Long) defaultValue));
            }
        }
        return value;
    }

    public final void remove(final String preferenceName, final String key) {
        SharedPreferences prefs = this.servicesPreferences;
        if (DetectionSettingsFragment.SHARED_PREFS_NAME.equals(preferenceName)) {
            prefs = this.detectionPreferences;
        } else if (APP_SHARED_PREF_NAMES.equals(preferenceName)) {
            prefs = this.appPreferences;
        } else if (ViewerWidgetConfigureActivity.PREFS_NAME.equals(preferenceName)) {
            prefs = this.widgetPreferences;
        }
        final SharedPreferences.Editor editor = prefs.edit();
        editor.remove(key);
        try {
            editor.apply();
        } catch (AbstractMethodError unused) {
            editor.commit();
        }
    }
}
