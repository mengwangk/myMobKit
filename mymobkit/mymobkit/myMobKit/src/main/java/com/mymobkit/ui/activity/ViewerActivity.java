package com.mymobkit.ui.activity;

import android.os.Bundle;

import com.mymobkit.R;
import com.mymobkit.ui.base.BaseActivity;
import com.mymobkit.ui.fragment.ViewerFragment;

import static com.mymobkit.common.LogUtils.makeLogTag;

public class ViewerActivity extends BaseActivity {

    private static final String TAG = makeLogTag(ViewerActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);
        setTitle(getDefaultTitle());

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_content, ViewerFragment.newInstance())
                    .commit();
        }
    }

    @Override
    protected int getSelfNavDrawerItem() {
        return NAVDRAWER_ITEM_VIEWER;
    }

    @Override
    protected String getDefaultTitle() {
        return getString(R.string.label_viewer_mode);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
