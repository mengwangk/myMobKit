package com.mymobkit.ui.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Resources;
import android.support.v13.app.FragmentPagerAdapter;

import com.mymobkit.R;
import com.mymobkit.app.AppController;
import com.mymobkit.ui.fragment.DetectionSettingsFragment;
import com.mymobkit.ui.fragment.ServiceSettingsFragment;

public class ControlPanelPageViewerTabsAdapter extends FragmentPagerAdapter {

    private Resources resources = AppController.getContext().getResources();

    public final String[] TAB_TITLES = resources.getStringArray(R.array.control_panel_string_array);

    public ControlPanelPageViewerTabsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new ServiceSettingsFragment();
        } else if (position == 1) {
            return new DetectionSettingsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return TAB_TITLES.length;
    }

    @Override
    public String getPageTitle(int position) {
        return TAB_TITLES[position % TAB_TITLES.length].toUpperCase();
    }

}
