package com.mymobkit.net;

import android.content.res.AssetManager;
import android.text.TextUtils;

import com.mymobkit.common.MimeType;
import com.mymobkit.net.provider.Processor;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.mymobkit.common.LogUtils.LOGD;
import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

public final class StreamingServer extends AppServer {

    private static final String TAG = makeLogTag(StreamingServer.class);

    private Map<String, Processor<Map<String, String>, Map<String, String>, Map<String, String>, InputStream>> streamingServices = new HashMap<String, Processor<Map<String, String>, Map<String, String>, Map<String, String>, InputStream>>();

    private Map<String, Processor<Map<String, String>, Map<String, String>, Map<String, String>, String>> processorServices = new HashMap<String, Processor<Map<String, String>, Map<String, String>, Map<String, String>, String>>();

    public static final String START_PAGE = "live.html";

    public void registerStreaming(String uri, Processor<Map<String, String>, Map<String, String>, Map<String, String>, InputStream> service) {
        if (service != null && !TextUtils.isEmpty(uri)) {
            String key = uri.toLowerCase();
            if (streamingServices.containsKey(key))
                streamingServices.remove(key);
            streamingServices.put(uri.toLowerCase(), service);
        }
    }

    public void registerProcessor(String uri, Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> service) {
        if (service != null)
            processorServices.put(uri, service);
    }

    public StreamingServer(String host, int port, AssetManager wwwroot) {
        super(host, port, wwwroot);
        this.setCustomStartPage(START_PAGE);
    }

    @Override
    public Response serve(IHTTPSession session) {

        final Response response = parseSession(session);
        if (response != null) return response;

        final String uri = session.getUri();
        final Map<String, String> headers = session.getHeaders();
        final Map<String, String> params = session.getParms();
        final Method method = session.getMethod();
        final Map<String, String> files = session.getFiles();
        LOGD(TAG, "[server] HTTP request >>" + method + " '" + uri + "' " + "   " + params);
        if (uri.startsWith("/processor/")) {
            return serveService(uri, method, headers, params, files);
        } else if (uri.startsWith("/audio_stream/")) {
            return serveStream(uri, method, headers, params, files, true);
        } else if (uri.startsWith("/video_stream/")) {
            return serveStream(uri, method, headers, params, files, false);
        } else if (uri.startsWith("/video/")) {
            return serveStream(uri, method, headers, params, files, true);
        }
        return super.serve(session);
    }

    public Response serveStream(String uri, Method method, Map<String, String> header, Map<String,
            String> params, Map<String, String> files, boolean isStreaming) {
        final Processor<Map<String, String>, Map<String, String>, Map<String, String>, InputStream> processor = streamingServices.get(uri.toLowerCase());
        if (processor == null)
            return null;

        final InputStream ins = processor.process(header, params, files);
        if (ins == null)
            return null;

        final Random random = new Random();
        final String etag = Integer.toHexString((uri + "" + random.nextInt()).hashCode());
        final String mime = params.get(MimeType.PARAM_MIME);
        Response response = getInternalErrorResponse("Unable to serve stream");
        try {
            response = NanoHttpd.newFixedLengthResponse(Response.Status.OK, mime, ins, ins.available());
        } catch (Exception ex) {
            LOGE(TAG, "Unable to serve stream", ex);
        }
        response.addHeader("ETag", etag);
        response.isStreaming = isStreaming;
        return response;
    }

    public Response serveService(String uri, Method method, Map<String, String> header, Map<String, String> parms, Map<String, String> files) {
        final Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> processor = processorServices.get(uri);
        if (processor == null)
            return null;

        final String msg = processor.process(header, parms, files);
        if (msg == null)
            return null;

        final Response res = NanoHttpd.newFixedLengthResponse(Response.Status.OK, MIME_PLAINTEXT, msg);
        return res;
    }

    @Override
    public void stop() {
        super.stop();
        streamingServices.clear();
        processorServices.clear();
    }
}
