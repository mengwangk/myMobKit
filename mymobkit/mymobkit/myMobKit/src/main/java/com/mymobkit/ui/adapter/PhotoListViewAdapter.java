package com.mymobkit.ui.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mymobkit.R;
import com.mymobkit.common.MimeType;
import com.mymobkit.service.api.drive.DriveFileInfo;
import com.mymobkit.service.api.drive.ListFileSyncTask;
import com.mymobkit.ui.handler.DriveRequestHandler;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Browse the captured photos.
 */
public class PhotoListViewAdapter extends BaseAdapter {

    private static final String TAG = makeLogTag(PhotoListViewAdapter.class);

    private final Context context;
    private final ListView listView;
    private final String deviceId;
    private final Picasso picasso;
    private final Map<String, List<DriveFileInfo>> groupings = new LinkedHashMap<String, List<DriveFileInfo>>();

    public PhotoListViewAdapter(final Context context, final ListView listView, final String deviceId) {
        this.context = context;
        this.listView = listView;
        this.deviceId = deviceId;
        Picasso.Builder picassoBuilder = new Picasso.Builder(context);
        picassoBuilder.addRequestHandler(new DriveRequestHandler(context));
        this.picasso = picassoBuilder.build();
        //final ProgressDialog progressDialog = ProgressDialog.show(context, context.getString(R.string.msg_title_wait), context.getString(R.string.msg_retrieving_photos), true);
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(context.getString(R.string.msg_title_wait));
        progressDialog.setMessage(context.getString(R.string.msg_retrieving_photos));
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.label_dialog_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    dialog.dismiss();
                } catch (Exception ex) {
                }
            }
        });
        progressDialog.show();

        new AsyncTask<ViewHolder, Void, Boolean>() {

            private List<DriveFileInfo> fileInfos;

            @Override
            public Boolean doInBackground(ViewHolder... args) {
                final ListFileSyncTask task = new ListFileSyncTask(context);
                fileInfos = task.execute(deviceId, MimeType.IMAGE_JPEG, "0");
                if (fileInfos == null) fileInfos = new ArrayList<DriveFileInfo>();

                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (fileInfos != null && fileInfos.size() == 0) {
                    final DriveFileInfo fileInfo = new DriveFileInfo(context.getString(R.string.msg_no_photo_found), context.getString(R.string.msg_no_photo_found));
                    fileInfos.add(fileInfo);
                    groupings.put(context.getString(R.string.msg_no_photo_found), fileInfos);
                } else {
                    for (DriveFileInfo fileInfo : fileInfos) {
                        String key = parseDateFromTitle(fileInfo.getTitle());
                        if (groupings.get(key) == null) {
                            groupings.put(key, new ArrayList<DriveFileInfo>());
                        }
                        groupings.get(key).add(fileInfo);
                    }
                }
                PhotoListViewAdapter.this.notifyDataSetChanged();
                try {
                    progressDialog.dismiss();
                } catch (Exception ex) {
                    // Do nothing
                }
            }

        }.execute();
    }

    private String parseDateFromTitle(final String title) {
        if (!TextUtils.isEmpty(title)) {
            final String[] fields = title.split("_");
            if (fields.length > 0) {
                return fields[0];
            }
        }
        return "";
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.photo_item, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) view.findViewById(R.id.image);
            holder.date = (TextView) view.findViewById(R.id.date);
            holder.caption = (TextView) view.findViewById(R.id.caption);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        // Get the device info for the current position.
        final List<DriveFileInfo> fileInfos = getItem(position);

        // Set the info
        final String title = fileInfos.get(0).getTitle();
        if (!TextUtils.isEmpty(title)) {
            final String[] fields = title.split("_");
            if (fields.length > 0) {
                holder.date.setText(fields[0]);
            }
        }
        if (!TextUtils.isEmpty(fileInfos.get(0).getDriveId())) {
            holder.caption.setText(String.format(context.getString(R.string.label_photo_count, fileInfos.size())));
            picasso.load(DriveRequestHandler.SCHEMA_DRIVE + ":" + fileInfos.get(0).getDriveId())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .centerInside()
                    .tag(context)
                    .fit()
                    .into(holder.image);
        }
        return view;
    }

    @Override
    public int getCount() {
        return groupings.size();
    }

    @Override
    public List<DriveFileInfo> getItem(int position) {
        List<List<DriveFileInfo>> list = new ArrayList<List<DriveFileInfo>>(groupings.values());
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        ImageView image;
        TextView date;
        TextView caption;
    }
}
