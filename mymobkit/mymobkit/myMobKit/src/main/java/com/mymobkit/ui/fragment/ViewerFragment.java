package com.mymobkit.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mymobkit.R;
import com.mymobkit.ui.activity.ViewerActivity;
import com.mymobkit.ui.adapter.DeviceListDetailAdapter;
import com.mymobkit.ui.listener.PicassoScrollListener;

/**
 * Viewer for images.
 */
public class ViewerFragment extends Fragment {

    public static ViewerFragment newInstance() {
        ViewerFragment fragment = new ViewerFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewerActivity activity = (ViewerActivity) getActivity();
        final ListView listView = (ListView) LayoutInflater.from(activity).inflate(R.layout.fragment_viewer, container, false);
        final DeviceListDetailAdapter adapter = new DeviceListDetailAdapter(activity, activity, listView);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new PicassoScrollListener(activity));
        return listView;
    }
}
