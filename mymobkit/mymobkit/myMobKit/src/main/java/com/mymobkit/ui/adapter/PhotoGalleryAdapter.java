package com.mymobkit.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mymobkit.R;
import com.mymobkit.service.api.drive.DriveFileInfo;
import com.mymobkit.ui.handler.DriveRequestHandler;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Photo gallery adapter.
 */
public class PhotoGalleryAdapter {

    private static final String TAG = makeLogTag(PhotoGalleryAdapter.class);

    private Context context;
    private LayoutInflater inflater;
    private List<DriveFileInfo> fileInfos;
    private final Picasso picasso;


    public PhotoGalleryAdapter(Context context, List<DriveFileInfo> fileInfos) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.fileInfos = fileInfos;
        this.picasso = new Picasso.Builder(context)
                .addRequestHandler(new DriveRequestHandler(context))
                .build();
    }

    public int getCount() {
        return fileInfos.size();
    }

    public Object getItem(int position) {
        return fileInfos.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.fragment_photo_gallery_item, parent, false);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
            //viewHolder.caption = (TextView) convertView.findViewById(R.id.caption);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final DriveFileInfo fileInfo = fileInfos.get(position);
        picasso.load(DriveRequestHandler.SCHEMA_DRIVE + ":" + fileInfo.getDriveId())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                //.resizeDimen(R.dimen.list_detail_image_size_width, R.dimen.list_detail_image_size_height)
                //.centerInside()
                .tag(context)
                .fit()
                //.memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(viewHolder.image);
        //viewHolder.image.setImageResource(fileInfos.get(position));
        //viewHolder.caption.setText("some info ");

        return convertView;
    }

    private class ViewHolder {
        ImageView image;
        //TextView caption;
    }
}

