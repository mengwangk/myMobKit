package com.mymobkit.ui.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mymobkit.R;
import com.mymobkit.app.AppConfig;
import com.mymobkit.app.AppController;
import com.mymobkit.common.StorageUtils;
import com.mymobkit.service.api.drive.DriveFileInfo;
import com.mymobkit.ui.activity.ViewerActivity;
import com.mymobkit.ui.adapter.PhotoGalleryAdapter;
import com.mymobkit.ui.handler.DriveRequestHandler;
import com.mymobkit.ui.viewer.PhotoGalleryView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Photo gallery.
 */
public class PhotoGalleryFragment extends Fragment {

    private List<DriveFileInfo> fileInfos = new ArrayList<DriveFileInfo>();
    private final Picasso picasso;
    private final Context context;
    private PhotoGalleryAdapter photoGalleryAdapter;
    private PhotoGalleryView photoGalleryView;
    private ImageView imageView;
    private PhotoViewAttacher photoViewAttacher;

    //private List<Integer> mDatas = new ArrayList<>(Arrays.asList(R.drawable.placeholder, R.drawable.ic_launcher));

    public static PhotoGalleryFragment newInstance(final Context context, final String deviceId,
                                                   final List<DriveFileInfo> fileInfos, final int position) {
        Bundle arguments = new Bundle();
        arguments.putString(AppConfig.DEVICE_ID_PARAM, deviceId);
        arguments.putString(AppConfig.FILE_NAME_PARAM, fileInfos.get(0).getTitle());
        arguments.putString(AppConfig.DRIVE_ID_PARAM, fileInfos.get(0).getDriveId());
        arguments.putInt(AppConfig.POSITION_PARAM, position);
        arguments.putParcelableArrayList(AppConfig.DRIVE_FILES_PARAM, (ArrayList<DriveFileInfo>) fileInfos);
        final PhotoGalleryFragment fragment = new PhotoGalleryFragment(context);
        fragment.setArguments(arguments);
        return fragment;
    }

    /**
     * Default constructor.
     */
    public PhotoGalleryFragment() {
        this(AppController.getContext());
    }

    /**
     * Default constructor.
     */
    public PhotoGalleryFragment(final Context context) {
        this.context = context;
        this.picasso = new Picasso.Builder(context)
                .addRequestHandler(new DriveRequestHandler(context))
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewerActivity activity = (ViewerActivity) getActivity();
        final View view = LayoutInflater.from(activity).inflate(R.layout.fragment_photo_gallery, container, false);
        final Bundle arguments = getArguments();
        final String fileName = arguments.getString(AppConfig.FILE_NAME_PARAM);
        final String driveId = arguments.getString(AppConfig.DRIVE_ID_PARAM);
        this.fileInfos = arguments.getParcelableArrayList(AppConfig.DRIVE_FILES_PARAM);

        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle(activity.getString(R.string.msg_title_wait));
        progressDialog.setMessage(activity.getString(R.string.msg_retrieving_photos));
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

        final TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(StorageUtils.tidyImageName(fileName));

        final Callback imageLoadedCallback = new Callback() {

            @Override
            public void onSuccess() {
                if (photoViewAttacher != null) {
                    photoViewAttacher.update();
                } else {
                    photoViewAttacher = new PhotoViewAttacher(imageView);
                }
            }

            @Override
            public void onError() {
                // Do nothing
            }
        };

        imageView = (ImageView) view.findViewById(R.id.image);
        // Load image into this view
        if (!TextUtils.isEmpty(driveId)) {
            picasso.load(DriveRequestHandler.SCHEMA_DRIVE + ":" + driveId)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .tag(context)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(imageView, imageLoadedCallback);
        }

        //photoViewAttacher = new PhotoViewAttacher(imageView);
        // photoViewAttacher.setScaleType(ImageView.ScaleType.FIT_XY);

        if (fileInfos != null && fileInfos.size() > 0) {
            photoGalleryView = (PhotoGalleryView) view.findViewById(R.id.gallery_view);
            photoGalleryAdapter = new PhotoGalleryAdapter(context, fileInfos);
            photoGalleryView.setCurrentImageChangeListener(new PhotoGalleryView.CurrentImageChangeListener() {
                @Override
                public void onCurrentImageChanged(int position, View viewIndicator) {
                    //ImageView currentImageView = (ImageView) view.findViewById(R.id.image);
                    //DriveFileInfo fileInfo = fileInfos.get(position);
                    //name.setText(StorageUtils.tidyImageName(fileInfo.getTitle()));
                    //imageView.setImageDrawable(currentImageView.getDrawable());
                    //viewIndicator.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                }
            });

            photoGalleryView.setOnItemClickListener(new PhotoGalleryView.OnItemClickListener() {
                @Override
                public void onClick(View view, int position) {
                    //ImageView currentImageView = (ImageView) view.findViewById(R.id.image);
                    DriveFileInfo fileInfo = fileInfos.get(position);
                    name.setText(StorageUtils.tidyImageName(fileInfo.getTitle()));
                    //imageView.setImageDrawable(currentImageView.getDrawable());
                    if (!TextUtils.isEmpty(fileInfo.getDriveId())) {
                        picasso.load(DriveRequestHandler.SCHEMA_DRIVE + ":" + fileInfo.getDriveId())
                                .error(R.drawable.placeholder)
                                .placeholder(R.drawable.placeholder)
                                .tag(context)
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .into(imageView);
                    }
                    view.setBackgroundColor(context.getResources().getColor(R.color.sky_blue));
                }
            });

            photoGalleryView.initDatas(photoGalleryAdapter);
        }

        // Retrieve all files related to this device
       /* new AsyncTask<Void, Void, Boolean>() {
            @Override
            public Boolean doInBackground(Void... args) {
                final ListFileSyncTask task = new ListFileSyncTask(activity);
                fileInfos = task.execute(deviceId, MimeType.IMAGE_JPEG, "0");
                return true;
            }

            @Override
            protected void onPostExecute(Boolean result) {

            }
        }.execute();*/

        try {
            progressDialog.dismiss();
        } catch (Exception ex) {
            // Do nothing
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (photoViewAttacher != null) {
            photoViewAttacher.cleanup();
            photoViewAttacher = null;
        }
    }

}
