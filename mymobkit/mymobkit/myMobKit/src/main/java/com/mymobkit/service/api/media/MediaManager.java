package com.mymobkit.service.api.media;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;

import static com.mymobkit.common.LogUtils.makeLogTag;
import static com.mymobkit.common.LogUtils.LOGE;

/**
 * Helper class to manage the media storage.
 * 
 */
public final class MediaManager {

    private static final String TAG = makeLogTag(MediaManager.class);

	private Context context;

	private static final String[] imageProj = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
			MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN, MediaStore.Images.Media.LATITUDE, MediaStore.Images.Media.LONGITUDE, MediaStore.Images.Media.MIME_TYPE,
			MediaStore.Images.Media.SIZE, MediaStore.Images.Media.ORIENTATION, MediaStore.Images.Media.DESCRIPTION, MediaStore.Images.Media.IS_PRIVATE, MediaStore.Images.Media.PICASA_ID, "width",
			"height" };

	private static final String[] videoProj = { MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.BUCKET_ID, MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
			MediaStore.Video.Media.DATA, MediaStore.Video.Media.DATE_TAKEN, MediaStore.Video.Media.LATITUDE, MediaStore.Video.Media.LONGITUDE, MediaStore.Video.Media.MIME_TYPE,
			MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DESCRIPTION, MediaStore.Video.Media.IS_PRIVATE, "width", "height", MediaStore.Video.Media.CATEGORY, MediaStore.Video.Media.DURATION,
			MediaStore.Video.Media.ALBUM, MediaStore.Video.Media.ARTIST };

	private static final String[] audioProj = { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DATE_ADDED,
			MediaStore.Audio.Media.MIME_TYPE, MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ALBUM_ID,
			MediaStore.Audio.Media.ALBUM_KEY, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ARTIST_ID, MediaStore.Audio.Media.ARTIST_KEY, MediaStore.Audio.Media.BOOKMARK,
			MediaStore.Audio.Media.COMPOSER, MediaStore.Audio.Media.IS_ALARM, MediaStore.Audio.Media.IS_MUSIC, MediaStore.Audio.Media.IS_NOTIFICATION, MediaStore.Audio.Media.IS_PODCAST,
			MediaStore.Audio.Media.IS_RINGTONE, MediaStore.Audio.Media.TRACK, MediaStore.Audio.Media.YEAR };

	public MediaManager(final Context context) {
		this.context = context;
	}

	private List<MediaImage> getImages(final Uri uri) {
		Cursor cursor = context.getContentResolver().query(uri, imageProj, null, null, null);
		List<MediaImage> items = Collections.synchronizedList(new ArrayList<MediaImage>(cursor.getCount()));
		Integer i = 0;
		if (cursor.moveToFirst()) {
			do {
				MediaImage image = new MediaImage(uri.toString() + "/" + cursor.getLong(0), cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
						cursor.getLong(5), cursor.getDouble(6), cursor.getDouble(7), cursor.getString(8), cursor.getLong(9), cursor.getInt(10), cursor.getString(11), cursor.getInt(12),
						cursor.getString(13), cursor.getString(14), cursor.getString(15));
				items.add(image);
				i++;
			} while (cursor.moveToNext());
		}
		cursor.close();
		return items;
	}

	public int deleteImage(final long id) {
		int rowsDeleted = 0;
		final ContentResolver contentResolver = context.getContentResolver();
		final Uri deleteUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
		rowsDeleted = contentResolver.delete(deleteUri, null, null);
		return rowsDeleted;
	}


	public int deleteVideo(final long id) {
		int rowsDeleted = 0;
		final ContentResolver contentResolver = context.getContentResolver();
		final Uri deleteUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
		rowsDeleted = contentResolver.delete(deleteUri, null, null);
		return rowsDeleted;
	}


	public int deleteAudio(final long id) {
		int rowsDeleted = 0;
		final ContentResolver contentResolver = context.getContentResolver();
		final Uri deleteUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
		rowsDeleted = contentResolver.delete(deleteUri, null, null);
		return rowsDeleted;
	}

	public List<MediaImage> getImages() {
		List<MediaImage> items = getImages(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		items.addAll(getImages(MediaStore.Images.Media.INTERNAL_CONTENT_URI));
		return items;
	}

	private MediaImage getImage(final Uri uri, final long id) {
		Cursor cursor = context.getContentResolver().query(uri, imageProj, MediaStore.Images.Media._ID + "=" + id, null, null);
		MediaImage image = null;
		if (cursor.moveToFirst()) {
			image = new MediaImage(uri.toString() + "/" + cursor.getLong(0), cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getLong(5),
					cursor.getDouble(6), cursor.getDouble(7), cursor.getString(8), cursor.getLong(9), cursor.getInt(10), cursor.getString(11), cursor.getInt(12), cursor.getString(13),
					cursor.getString(14), cursor.getString(15));
		}
		cursor.close();
		return image;
	}

	public MediaImage getImage(final long id) {
		MediaImage image = getImage(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
		if (image == null) {
			image = getImage(MediaStore.Images.Media.INTERNAL_CONTENT_URI, id);
		}
		return image;
	}

	private MediaVideo getVideo(final Uri uri, final long id) {
		Cursor cursor = context.getContentResolver().query(uri, videoProj, MediaStore.Video.Media._ID + "=" + id, null, null);
		MediaVideo video = null;
		if (cursor.moveToFirst()) {
			video = new MediaVideo(uri.toString() + "/" + cursor.getLong(0), cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getLong(5),
					cursor.getDouble(6), cursor.getDouble(7), cursor.getString(8), cursor.getLong(9), cursor.getString(10), cursor.getInt(11), cursor.getString(12), cursor.getString(13),
					cursor.getString(14), cursor.getLong(15), cursor.getString(16), cursor.getString(17));
		}
		cursor.close();
		return video;
	}

	public MediaVideo getVideo(final long id) {
		MediaVideo video = getVideo(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
		if (video == null) {
			video = getVideo(MediaStore.Video.Media.INTERNAL_CONTENT_URI, id);
		}
		return video;
	}

	private List<MediaVideo> getVideos(final Uri uri) {
		try {
			Cursor cursor = context.getContentResolver().query(uri, videoProj, null, null, null);
			List<MediaVideo> items = Collections.synchronizedList(new ArrayList<MediaVideo>(cursor.getCount()));
			Integer i = 0;
			if (cursor.moveToFirst()) {
				do {
					MediaVideo video = new MediaVideo(uri.toString() + "/" + cursor.getLong(0), cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
							cursor.getLong(5), cursor.getDouble(6), cursor.getDouble(7), cursor.getString(8), cursor.getLong(9), cursor.getString(10), cursor.getInt(11), cursor.getString(12),
							cursor.getString(13), cursor.getString(14), cursor.getLong(15), cursor.getString(16), cursor.getString(17));
					items.add(video);
					i++;
				} while (cursor.moveToNext());
			}
			cursor.close();
			return items;
		} catch (Exception ex) {
			LOGE(TAG, "[getVideos] Unable to get videos", ex);
			return new ArrayList<MediaVideo>();
		}
	}

	public List<MediaVideo> getVideos() {
		List<MediaVideo> items = getVideos(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
		items.addAll(getVideos(MediaStore.Video.Media.INTERNAL_CONTENT_URI));
		return items;
	}

	private MediaAudio getAudio(final Uri uri, final long id) {
		Cursor cursor = context.getContentResolver().query(uri, audioProj, MediaStore.Audio.Media._ID + "=" + id, null, null);
		MediaAudio audio = null;
		if (cursor.moveToFirst()) {
			audio = new MediaAudio(uri.toString() + "/" + cursor.getLong(0), cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getLong(3), cursor.getString(4), cursor.getLong(5),
					cursor.getLong(6), cursor.getString(7), cursor.getInt(8), cursor.getString(9), cursor.getString(10), cursor.getInt(11), cursor.getString(12), cursor.getInt(13),
					cursor.getString(14), cursor.getInt(15), cursor.getInt(16), cursor.getInt(17), cursor.getInt(18), cursor.getInt(19), cursor.getInt(20), cursor.getInt(21));
		}
		cursor.close();
		return audio;
	}

	public MediaAudio getAudio(final long id) {
		MediaAudio audio = getAudio(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
		if (audio == null) {
			audio = getAudio(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, id);
		}
		return audio;
	}

	private List<MediaAudio> getAudios(final Uri uri) {
		Cursor cursor = context.getContentResolver().query(uri, audioProj, null, null, null);
		List<MediaAudio> items = Collections.synchronizedList(new ArrayList<MediaAudio>(cursor.getCount()));
		Integer i = 0;
		if (cursor.moveToFirst()) {
			do {
				MediaAudio audio = new MediaAudio(uri.toString() + "/" + cursor.getLong(0), cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getLong(3), cursor.getString(4),
						cursor.getLong(5), cursor.getLong(6), cursor.getString(7), cursor.getInt(8), cursor.getString(9), cursor.getString(10), cursor.getInt(11), cursor.getString(12),
						cursor.getInt(13), cursor.getString(14), cursor.getInt(15), cursor.getInt(16), cursor.getInt(17), cursor.getInt(18), cursor.getInt(19), cursor.getInt(20), cursor.getInt(21));
				items.add(audio);
				i++;
			} while (cursor.moveToNext());
		}
		cursor.close();
		return items;
	}

	public List<MediaAudio> getAudios() {
		List<MediaAudio> items = getAudios(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
		items.addAll(getAudios(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI));
		return items;
	}

	public byte[] getImage(final String uri) throws IOException {
		return getImage(uri, Bitmap.CompressFormat.JPEG);
	}

	public byte[] getImage(final String uri, final CompressFormat format) throws IOException {
		Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(uri));
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(format, 100, stream);
		return stream.toByteArray();
	}

	public byte[] getImageThumbnail(long imageId, Integer thumbnailSize) {
		Bitmap artwork = MediaStore.Images.Thumbnails.getThumbnail(context.getContentResolver(), imageId, thumbnailSize, null);
		if (artwork == null)
			return null;
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		artwork.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		return stream.toByteArray();
	}

	public byte[] getVideoThumbnail(long videoId, Integer thumbnailSize) {
		Bitmap artwork = MediaStore.Video.Thumbnails.getThumbnail(context.getContentResolver(), videoId, thumbnailSize, null);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		artwork.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		return stream.toByteArray();
	}

	public byte[] getAlbumArt(long albumId) {
		try {
			Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
			Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
			Bitmap artwork = MediaStore.Images.Media.getBitmap(context.getContentResolver(), albumArtUri);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			artwork.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			return stream.toByteArray();
		} catch (Exception exp) {
			return null;
		}
	}

	public byte[] getImageAtTime(String videoPath, Long time) {
		try {
			MediaMetadataRetriever mediaMetadata = new MediaMetadataRetriever();
			mediaMetadata.setDataSource(videoPath);
			Bitmap artwork = mediaMetadata.getFrameAtTime(time * 1000);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			artwork.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			return stream.toByteArray();
		} catch (Exception exp) {
			return null;
		}
	}

}
