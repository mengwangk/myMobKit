package com.mymobkit.common;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.mymobkit.ui.fragment.DetectionSettingsFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Storage utility class.
 */
public final class StorageUtils {

    public static final String IMAGE_EXTENSION = ".jpg";

    /**
     * Log tag.
     */
    private static final String TAG = makeLogTag(StorageUtils.class);


    public static File getPrivateAlbumStorageDir(Context context, String folderName) {
        // Get the directory for the app's private pictures directory.
        final String albumName = folderName;
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + albumName);
        if (!file.exists()) {
            if (!file.mkdir()) {
                LOGE(TAG, "Directory not created");
            }
        }
        return file;
    }


    public static String[] getAvailableStorages(final Context context) {
        List<String> storages = new ArrayList<String>(1);
        //File[] dirs = ContextCompat.getExternalFilesDirs(context, Environment.DIRECTORY_PICTURES);
        File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        storages.add(f.getAbsolutePath());

        File[] dirs = ContextCompat.getExternalFilesDirs(context, Environment.DIRECTORY_DCIM);
        for (File dir : dirs) {
            if (dir != null) {
                storages.add(dir.getAbsolutePath());
            }
        }
        return ValidationUtils.toStringArray(storages);
    }

    public static File getStorageDir(final Context context, final String folderName) {
        final String albumName = folderName; // fixLocalFileName(folderName);
        final String location = AppPreference.getInstance().getValue(DetectionSettingsFragment.SHARED_PREFS_NAME, DetectionSettingsFragment.KEY_STORAGE_LOCATION, "");
        if (TextUtils.isEmpty(location)) {
            return deriveAlbumLocation(context, albumName);
        } else {
            File dir = new File(location);
            if (!dir.canWrite()) {
                return deriveAlbumLocation(context, albumName);
            } else {
                File file = new File(location + File.separator + albumName);
                if (!file.exists()) {
                    if (!file.mkdir()) {
                        LOGE(TAG, "[getStorageDir] Directory not created");
                    }
                }
                return file;
            }
        }
    }

    private static File deriveAlbumLocation(final Context context, final String folderName) {
        // Get the directory for the user's public pictures directory.
        final String albumName = folderName;
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + albumName);
        if (PlatformUtils.isKitKatOrHigher()) {
            if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED) &&
                    !file.getAbsolutePath().contains("emulated")) {
                File[] dirs = ContextCompat.getExternalFilesDirs(context, Environment.DIRECTORY_DCIM);
                if (dirs == null || dirs.length == 0) {
                    file = new File(albumName);
                } else {
                    for (File dir : dirs) {
                        if (dir.canWrite()) {
                            file = new File(dir + File.separator + albumName);
                            break;
                        }
                    }
                }
            }
        }
        if (!file.exists()) {
            if (!file.mkdir()) {
                LOGE(TAG, "[deriveAlbumLocation] Directory not created");
            }
        }
        return file;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static long getFreeSpace() {
        File externalStorageDir = Environment.getExternalStorageDirectory();
        long free = externalStorageDir.getFreeSpace() / 1024 / 1024;
        return free;
    }

    private static void broadcastFile(final Context context, final Uri uri, final boolean isNewPicture, final boolean isNewVideo) {
        /*
        MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        if (isNewPicture) {
                            // note, we reference the string directly rather than via Camera.ACTION_NEW_PICTURE, as the latter class is now deprecated - but we still need to broadcase the string for other apps
                            context.sendBroadcast(new Intent("android.hardware.action.NEW_PICTURE", uri));
                            // for compatibility with some apps - apparently this is what used to be broadcast on Android?
                            context.sendBroadcast(new Intent("com.android.camera.NEW_PICTURE", uri));
                        } else if (isNewVideo) {
                            context.sendBroadcast(new Intent("android.hardware.action.NEW_VIDEO", uri));
                        }
                    }
                }
        );
        */
        if (isNewPicture) {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            context.sendBroadcast(new Intent("android.hardware.action.NEW_PICTURE", uri));
            // Keep compatibility
            context.sendBroadcast(new Intent("com.android.camera.NEW_PICTURE", uri));
        } else if (isNewVideo) {
            context.sendBroadcast(new Intent("android.hardware.action.NEW_VIDEO", uri));
        }
    }

    public static void addImageToGallery(final String name, final String filePath, final Context context, final String contentType) {
        final File imageFile = new File(filePath);
        //broadcastFile(context, imageFile, true, false);

        final ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        //values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        //values.put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, contentType);
        values.put(MediaStore.Images.Media.TITLE, name.substring(0, name.lastIndexOf(".") >= 0 ? name.lastIndexOf(".") : name.length()));
        values.put(MediaStore.Images.Media.DESCRIPTION, name);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, name);

        File parent = imageFile.getParentFile();
        String parentPath = parent.toString().toLowerCase();
        String parentName = parent.getName().toLowerCase();

        values.put(MediaStore.Images.Media.SIZE, imageFile.length());
        values.put(MediaStore.Images.Media.BUCKET_ID, parentPath.hashCode());
        values.put(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, parentName);
        //values.put(MediaStore.Images.Media.IS_PRIVATE, String.valueOf(0));
        values.put(MediaStore.Images.Media.ORIENTATION, String.valueOf(0));
        values.put(MediaStore.MediaColumns.DATA, filePath);

        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        broadcastFile(context, uri, true, false);
    }


    public static void addVideoToGallery(final String name, final String filePath, final Context context, final String contentType) {
        // final ContentValues values = new ContentValues();
        // values.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
        // values.put(MediaStore.Video.Media.MIME_TYPE, contentType);
        // values.put(MediaStore.MediaColumns.DATA, filePath);
        // context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);

        final ContentValues values = new ContentValues();
        File videoFile = new File(filePath);
        values.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
        //values.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis());
        //values.put(MediaStore.Video.Media.DATE_MODIFIED, System.currentTimeMillis());
        values.put(MediaStore.Video.Media.MIME_TYPE, contentType);
        values.put(MediaStore.Video.Media.TITLE, name.substring(0, name.lastIndexOf(".") >= 0 ? name.lastIndexOf(".") : name.length()));
        values.put(MediaStore.Video.Media.DESCRIPTION, name);
        values.put(MediaStore.Video.Media.DISPLAY_NAME, name);

        File parent = videoFile.getParentFile();
        String parentPath = parent.toString().toLowerCase();
        String parentName = parent.getName().toLowerCase();

        values.put(MediaStore.Video.Media.SIZE, videoFile.length());
        values.put(MediaStore.Video.Media.BUCKET_ID, parentPath.hashCode());
        values.put(MediaStore.Video.Media.BUCKET_DISPLAY_NAME, parentName);
        //values.put(MediaStore.Images.Media.IS_PRIVATE, String.valueOf(0));
        values.put(MediaStore.Images.Media.ORIENTATION, String.valueOf(0));
        values.put(MediaStore.MediaColumns.DATA, filePath);

        Uri uri = context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        broadcastFile(context, uri, false, true);
    }

    public static String saveFile(final File dir, final String fileName, final byte[] fileData) throws FileNotFoundException, IOException {
        File file = new File(dir, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(file);
        try {
            fos.write(fileData);
            //file.setReadable(true);
            return file.getAbsolutePath();
        } finally {
            fos.close();
        }
    }

    public static String fixLocalImageName(String fileName) {
        if (TextUtils.isEmpty(fileName)) return "";
        return fileName.replaceAll("[^a-zA-Z0-9]", "") + IMAGE_EXTENSION;
    }

    public static String fixLocalFileName(String fileName) {
        if (TextUtils.isEmpty(fileName)) return "";
        return fileName.replaceAll("[^a-zA-Z0-9]", "");
    }

    public static String fixPublicImageName(final String fileName) {
        if (TextUtils.isEmpty(fileName)) return "";
        return fileName.replaceAll(" ", "_") + IMAGE_EXTENSION;
    }

    public static String tidyImageName(final String fileName) {
        if (!TextUtils.isEmpty(fileName)) {
            return fileName.replace("_", " ").replace(IMAGE_EXTENSION, "");
        }
        return "";
    }
}
