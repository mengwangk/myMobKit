package com.mymobkit.service.api;

import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonWriter;
import com.mymobkit.R;
import com.mymobkit.net.AppServer;
import com.mymobkit.service.HttpdService;
import com.mymobkit.service.api.media.DeleteRequest;
import com.mymobkit.service.api.media.GetAudioRequest;
import com.mymobkit.service.api.media.GetImageRequest;
import com.mymobkit.service.api.media.GetRequest;
import com.mymobkit.service.api.media.GetVideoRequest;
import com.mymobkit.service.api.media.MediaAudio;
import com.mymobkit.service.api.media.MediaImage;
import com.mymobkit.service.api.media.MediaManager;
import com.mymobkit.service.api.media.MediaVideo;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mymobkit.common.LogUtils.LOGE;

public final class MediaApiHandler extends ApiHandler {

    private MediaManager mediaUtils;

    public static final String MEDIA_TYPE_IMAGE = "image";
    public static final String MEDIA_TYPE_VIDEO = "video";
    public static final String MEDIA_TYPE_AUDIO = "audio";

    /**
     * Constructor.
     *
     * @param service
     */
    public MediaApiHandler(HttpdService service) {
        super(service);
        mediaUtils = new MediaManager(getContext());
    }

    @Override
    public String get(final Map<String, String> header, final Map<String, String> params, final Map<String, String> files) {
        try {
            maybeAcquireWakeLock();
            if (params.containsKey(AppServer.URI_PARAM_PREFIX + "1")) {
                final long mediaId = getLongValue(AppServer.URI_PARAM_PREFIX + "0", params);
                final String mediaType = getStringValue(AppServer.URI_PARAM_PREFIX + "1", params);
                if (mediaId > 0) {
                    if (MEDIA_TYPE_AUDIO.equalsIgnoreCase(mediaType)) {
                        GetAudioRequest request = new GetAudioRequest();
                        List<MediaAudio> audios = new ArrayList<MediaAudio>(1);
                        MediaAudio audio = mediaUtils.getAudio(mediaId);
                        if (audio == null) {
                            // No audio found
                            request.isSuccessful = false;
                            request.setDescription(String.format(getContext().getString(R.string.media_no_matched_media), String.valueOf(mediaId)));
                        } else {
                            audios.add(audio);
                            request.setMedia(audios);
                        }
                        return gson.toJson(request);
                    } else if (MEDIA_TYPE_VIDEO.equalsIgnoreCase(mediaType)) {
                        GetVideoRequest request = new GetVideoRequest();
                        List<MediaVideo> videos = new ArrayList<MediaVideo>(1);
                        MediaVideo video = mediaUtils.getVideo(mediaId);
                        if (video == null) {
                            // No video found
                            request.isSuccessful = false;
                            request.setDescription(String.format(getContext().getString(R.string.media_no_matched_media), String.valueOf(mediaId)));
                        } else {
                            videos.add(video);
                            request.setMedia(videos);
                        }
                        return gson.toJson(request);
                    } else {
                        GetImageRequest request = new GetImageRequest();
                        List<MediaImage> images = new ArrayList<MediaImage>(1);
                        MediaImage image = mediaUtils.getImage(mediaId);
                        if (image == null) {
                            // No image found
                            request.isSuccessful = false;
                            request.setDescription(String.format(getContext().getString(R.string.media_no_matched_media), String.valueOf(mediaId)));
                        } else {
                            images.add(image);
                            request.setMedia(images);
                        }
                        return gson.toJson(request);
                    }
                } else {
                    return getMediaData(mediaType);
                }
            } else if (params.containsKey(AppServer.URI_PARAM_PREFIX + "0")) {
                final String mediaType = getStringValue(AppServer.URI_PARAM_PREFIX + "0", params);
                return getMediaData(mediaType);
            } else {
                // Default to return all images
                return getMediaImage();
            }
        } finally {
            releaseWakeLock();
        }
    }

    private String getMediaData(final String mediaType) {
        if (MEDIA_TYPE_AUDIO.equalsIgnoreCase(mediaType)) {
            GetRequest<MediaAudio> request = new GetAudioRequest();
            request.setMedia(mediaUtils.getAudios());
            return toJson(request, MEDIA_TYPE_AUDIO);
        } else if (MEDIA_TYPE_VIDEO.equalsIgnoreCase(mediaType)) {
            GetRequest<MediaVideo> request = new GetVideoRequest();
            request.setMedia(mediaUtils.getVideos());
            return toJson(request, MEDIA_TYPE_VIDEO);
        } else {
            // Default to return all images
            return getMediaImage();
        }
    }

    private String getMediaImage() {
        GetRequest<MediaImage> request = new GetImageRequest();
        request.setMedia(mediaUtils.getImages());
        return toJson(request, MEDIA_TYPE_IMAGE);
    }

    @Override
    public String delete(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
        try {
            DeleteRequest request = new DeleteRequest();
            maybeAcquireWakeLock();
            if (params.containsKey(AppServer.URI_PARAM_PREFIX + "1")) {
                final long mediaID = getLongValue(AppServer.URI_PARAM_PREFIX + "0", params);
                final String mediaType = getStringValue(AppServer.URI_PARAM_PREFIX + "1", params);
                if (mediaID > 0) {
                    if (MEDIA_TYPE_AUDIO.equalsIgnoreCase(mediaType)) {
                        int rowCount = mediaUtils.deleteAudio(mediaID);
                        if (rowCount == 0) {
                            request.isSuccessful = false;
                            request.setDescription(String.format(getContext().getString(R.string.media_no_matched_media), String.valueOf(mediaID)));
                        } else {
                            request.isSuccessful = true;
                            request.setCount(rowCount);
                            request.setDescription(String.format(getContext().getString(R.string.media_delete_success), String.valueOf(mediaID)));
                        }
                        return gson.toJson(request);
                    } else if (MEDIA_TYPE_VIDEO.equalsIgnoreCase(mediaType)) {
                        int rowCount = mediaUtils.deleteVideo(mediaID);
                        if (rowCount == 0) {
                            request.isSuccessful = false;
                            request.setDescription(String.format(getContext().getString(R.string.media_no_matched_media), String.valueOf(mediaID)));
                        } else {
                            request.isSuccessful = true;
                            request.setCount(rowCount);
                            request.setDescription(String.format(getContext().getString(R.string.media_delete_success), String.valueOf(mediaID)));
                        }
                        return gson.toJson(request);
                    } else {
                        int rowCount = mediaUtils.deleteImage(mediaID);
                        if (rowCount == 0) {
                            request.isSuccessful = false;
                            request.setDescription(String.format(getContext().getString(R.string.media_no_matched_media), String.valueOf(mediaID)));
                        } else {
                            request.isSuccessful = true;
                            request.setCount(rowCount);
                            request.setDescription(String.format(getContext().getString(R.string.media_delete_success), String.valueOf(mediaID)));
                        }
                        return gson.toJson(request);
                    }
                } else {
                    request.isSuccessful = false;
                    request.setDescription(getContext().getString(R.string.media_not_found));
                    return gson.toJson(request);
                }
            } else {
                request.isSuccessful = false;
                request.setDescription(getContext().getString(R.string.media_not_found));
                return gson.toJson(request);
            }
        } finally {
            releaseWakeLock();
        }
    }

    /**
     * To deal with memory issue on mobile devices.
     * <p/>
     * Refer to https://sites.google.com/site/gson/streaming
     *
     * @param getRequest
     * @param mediaType
     */
    public String toJson(final GetRequest getRequest, final String mediaType) {
        if (getRequest == null) return "";

        // for smaller size just use the default toJson method
        if (getRequest.getMedia() == null || (getRequest.getMedia().size() < 300))
            return gson.toJson(getRequest);

        // Use the streaming method
        StringWriter stringWriter = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(stringWriter);
        try {
            if (MEDIA_TYPE_IMAGE.equalsIgnoreCase(mediaType)) {
                gson.toJson(getRequest, GetImageRequest.class, jsonWriter);
            } else if (MEDIA_TYPE_VIDEO.equalsIgnoreCase(mediaType)) {
                gson.toJson(getRequest, GetVideoRequest.class, jsonWriter);
            } else if (MEDIA_TYPE_AUDIO.equalsIgnoreCase(mediaType)) {
                gson.toJson(getRequest, GetAudioRequest.class, jsonWriter);
            } else {
                gson.toJson(getRequest, GetImageRequest.class, jsonWriter);
            }
            return stringWriter.toString();
        } catch (JsonIOException ioEx) {
            LOGE(TAG, "[toJson] Error generating JSON output", ioEx);
        } finally {
            try {
                jsonWriter.close();
            } catch (IOException ioEx) {
                LOGE(TAG, "[toJson] Error closing JSON writer", ioEx);
            }
        }
        return "";
    }
}
