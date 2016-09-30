package com.mymobkit.net;

import android.content.res.AssetManager;
import android.text.TextUtils;

import com.mymobkit.common.Base64;
import com.mymobkit.common.EntityUtils;
import com.mymobkit.common.MimeType;
import com.mymobkit.model.User;
import com.mymobkit.net.provider.Processor;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.mymobkit.common.LogUtils.LOGD;
import static com.mymobkit.common.LogUtils.makeLogTag;

public final class ControlPanelService extends AppServer {

    private static final String TAG = makeLogTag(ControlPanelService.class);

    private Map<String, Processor<Map<String, String>, Map<String, String>, Map<String, String>, InputStream>> streamingServices = new HashMap<String, Processor<Map<String, String>, Map<String, String>, Map<String, String>, InputStream>>();

    private Map<String, Processor<Map<String, String>, Map<String, String>, Map<String, String>, String>> processorServices = new HashMap<String, Processor<Map<String, String>, Map<String, String>, Map<String, String>, String>>();


    public void registerService(String uri, Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> service) {
        if (service != null && !TextUtils.isEmpty(uri))
            processorServices.put(uri.toLowerCase(), service);
    }

    public void registerStreaming(String uri, Processor<Map<String, String>, Map<String, String>, Map<String, String>, InputStream> service) {
        if (service != null && !TextUtils.isEmpty(uri)) {
            String key = uri.toLowerCase();
            if (streamingServices.containsKey(key))
                streamingServices.remove(key);
            streamingServices.put(uri.toLowerCase(), service);
        }
    }

    public ControlPanelService(String host, int port, AssetManager wwwroot) {
        super(host, port, wwwroot);
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

        LOGD(TAG, "[serve] HTTP request >>" + method + " '" + uri + "' " + "   " + params);

        if (uri.toLowerCase().startsWith("/services/stream/")) {
            return serveMedia(session, uri, method, headers, params, files);
        } else if (uri.toLowerCase().startsWith("/services/")) {
            return serveService(session, uri, method, headers, params, files);
        }

        return super.serve(session);
    }

    public Response serveService(IHTTPSession session, String uri, Method method, Map<String, String> headers, Map<String, String> params, Map<String, String> files) {
        // Added Sept 24th 2016
        if (!isAuthenticated(session)) {
            Response res = NanoHttpd.newFixedLengthResponse(Response.Status.UNAUTHORIZED, NanoHttpd.MIME_PLAINTEXT, "UNAUTHORIZED: Needs Authentication.");
            res.addHeader("WWW-Authenticate", "Basic realm=\"MyMobKit\"");
            res.setData(new ByteArrayInputStream("Needs Authentication".getBytes()));
            return res;
        }

        final Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> processor = matchProcessor(uri, params);
        if (processor == null) {
            return null;
        }
        params.put(HTTP_METHOD, method.toString());
        final String msg = processor.process(headers, params, files);
        if (msg == null)
            return null;

        String mime = MIME_PLAINTEXT;
        if (uri.toLowerCase().startsWith("/services/")) {
            mime = MimeType.JSON;
        }
        Response res = NanoHttpd.newFixedLengthResponse(Response.Status.OK, mime, msg);
        res.addHeader("Connection", "close");    // Should close the connection for each service call
        res.addHeader("Access-Control-Allow-Origin", "*");
        res.addHeader("Access-Control-Max-Age", "3628800");
        res.addHeader("Access-Control-Allow-Methods", "GET, PUT, POST, DELETE, HEAD, OPTIONS");
        res.addHeader("Access-Control-Allow-Headers", "X-Requested-With");
        res.addHeader("Access-Control-Allow-Headers", "Authorization");
        return res;
    }

    protected Response serveMedia(IHTTPSession session, String uri, Method method, Map<String, String> headers, Map<String, String> params, Map<String, String> files) {
        final Processor<Map<String, String>, Map<String, String>, Map<String, String>, InputStream> processor = matchServices(uri, params);
        if (processor == null) {
            return getNotFoundResponse();
        }
        final InputStream ins = processor.process(headers, params, files);
        if (ins == null) {
            return getNotFoundResponse();
        }

        return serveFile(session, headers, uri, ins);
    }

    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, String> matchProcessor(final String uri, final Map<String, String> parms) {
        String toMatch = uri.toLowerCase();
        int len = toMatch.length();
        String uriParam = "";
        int paramIndex = 0;
        while (len > 1) {
            if (processorServices.containsKey(toMatch)) {
                if (!TextUtils.isEmpty(uriParam)) {
                    parms.put(URI_PARAM_PREFIX + paramIndex++, uriParam);
                }
                return processorServices.get(toMatch);
            } else {
                String lastChar = toMatch.substring(toMatch.length() - 1);
                toMatch = toMatch.substring(0, toMatch.length() - 1);
                if (!lastChar.equals("/")) {
                    uriParam = lastChar + uriParam;
                } else if (!TextUtils.isEmpty(uriParam)) {
                    parms.put(URI_PARAM_PREFIX + paramIndex++, uriParam);
                    uriParam = "";
                }
                len = toMatch.length();
            }
        }
        return null;
    }

    private Processor<Map<String, String>, Map<String, String>, Map<String, String>, InputStream> matchServices(final String uri, final Map<String, String> parms) {
        String toMatch = uri.toLowerCase();
        int len = toMatch.length();
        String uriParam = "";
        int paramIndex = 0;
        while (len > 1) {
            if (streamingServices.containsKey(toMatch)) {
                if (!TextUtils.isEmpty(uriParam)) {
                    parms.put(URI_PARAM_PREFIX + paramIndex++, uriParam);
                }
                return streamingServices.get(toMatch);
            } else {
                String lastChar = toMatch.substring(toMatch.length() - 1);
                toMatch = toMatch.substring(0, toMatch.length() - 1);
                if (!lastChar.equals("/")) {
                    uriParam = lastChar + uriParam;
                } else if (!TextUtils.isEmpty(uriParam)) {
                    parms.put(URI_PARAM_PREFIX + paramIndex++, uriParam);
                    uriParam = "";
                }
                len = toMatch.length();
            }
        }
        return null;
    }
}
