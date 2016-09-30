package com.mymobkit.ui.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mymobkit.R;
import com.mymobkit.app.AppConfig;
import com.mymobkit.app.AppController;
import com.mymobkit.service.api.drive.DriveFileInfo;
import com.mymobkit.ui.activity.ViewerActivity;
import com.mymobkit.ui.adapter.PhotoListViewAdapter;
import com.mymobkit.ui.listener.PicassoScrollListener;

import java.util.List;

/**
 * Browse the captured photos.
 */
public class PhotoListViewFragment extends Fragment {

    private final Context context;

    public static PhotoListViewFragment newInstance(final Context context, final String deviceId) {
        final Bundle arguments = new Bundle();
        arguments.putString(AppConfig.DEVICE_ID_PARAM, deviceId);

        final PhotoListViewFragment fragment = new PhotoListViewFragment(context);
        fragment.setArguments(arguments);
        return fragment;
    }

    public PhotoListViewFragment() {
        this(AppController.getContext());
    }

    public PhotoListViewFragment(final Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewerActivity activity = (ViewerActivity) getActivity();
        final View view = LayoutInflater.from(activity).inflate(R.layout.fragment_photo_listview, container, false);
        final ListView listView = (ListView) view.findViewById(R.id.fragment_photo_list);
        final Bundle arguments = getArguments();
        final String deviceId = arguments.getString(AppConfig.DEVICE_ID_PARAM);
        final PhotoListViewAdapter adapter = new PhotoListViewAdapter(activity, listView, deviceId);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new PicassoScrollListener(activity));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final List<DriveFileInfo> fileInfos = adapter.getItem(position);
                final DriveFileInfo fileInfo = fileInfos.get(0);
                if (!TextUtils.isEmpty(fileInfo.getDriveId())) {
                    activity.getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_content, PhotoGalleryFragment.newInstance(context, deviceId, fileInfos, position))
                            .addToBackStack(null)
                            .commit();
                }

            }
        });
        return view;
    }
}
