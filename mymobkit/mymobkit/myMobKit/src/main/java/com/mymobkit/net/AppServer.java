package com.mymobkit.net;

import android.content.res.AssetManager;
import android.text.TextUtils;

import com.mymobkit.common.Base64;
import com.mymobkit.common.EntityUtils;
import com.mymobkit.common.MimeType;
import com.mymobkit.model.User;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * myMobKit server implementation.
 */
public class AppServer extends NanoHttpd {

    /**
     * Common mime type for dynamic content: binary
     */
    public static final String MIME_DEFAULT_BINARY = "application/octet-stream";

    protected AssetManager assetManager;

    protected String customStartPage;

    private boolean isAuthenticationRequired = false;

    protected List<User> authorizedUsers = new ArrayList<User>();

    public static final String URI_PARAM_PREFIX = "uri_param_";

    public static final String HTTP_METHOD = "http_method";

    protected final String cors;


    public AppServer(String host, int port, AssetManager wwwroot, String cors) {
        super(host, port);
        this.assetManager = wwwroot;
        this.customStartPage = "";
        this.cors = cors;
    }

    public AppServer(String host, int port, AssetManager wwwroot) {
        this(host, port, wwwroot, null);
    }

    protected boolean isAuthenticated(IHTTPSession session) {
        if (isAuthenticationRequired() && TextUtils.isEmpty(session.getSessionId())) {
            User user = authorizedUsers.get(0);
            // Use basic authentication to validate the client.
            boolean isValidated = false;
            String authorization = session.getHeaders().get("authorization");
            if (!TextUtils.isEmpty(authorization)) {
                String encoding = new String(Base64.encodeBytes((user.getName() + ":" + user.getPassword()).getBytes()));
                String expected = "Basic " + encoding;
                if (expected.equals(authorization)) {
                    isValidated = true;
                    session.setSessionId(EntityUtils.generateGuid());
                }
            }
            return isValidated;
        }
        return true;
    }

    @Override
    public Response serve(IHTTPSession session) {
        /* Commented Sept 24th 2016
        if (isAuthenticationRequired() && TextUtils.isEmpty(session.getSessionId())) {
            User user = authorizedUsers.get(0);
            // Use basic authentication to validate the client.
            boolean isValidated = false;
            String authorization = session.getHeaders().get("authorization");
            if (!TextUtils.isEmpty(authorization)) {
                String encoding = new String(Base64.encodeBytes((user.getName() + ":" + user.getPassword()).getBytes()));
                String expected = "Basic " + encoding;
                if (expected.equals(authorization)) {
                    isValidated = true;
                    session.setSessionId(EntityUtils.generateGuid());
                }
            }
            if (!isValidated) {
                Response res = NanoHttpd.newFixedLengthResponse(Response.Status.UNAUTHORIZED, NanoHttpd.MIME_PLAINTEXT, "UNAUTHORIZED: Needs Authentication.");
                res.addHeader("WWW-Authenticate", "Basic realm=\"MyMobKit\"");
                res.setData(new ByteArrayInputStream("Needs Authentication".getBytes()));
                return res;
            }
        }
        */
        if (!isAuthenticated(session)) {
            Response res = NanoHttpd.newFixedLengthResponse(Response.Status.UNAUTHORIZED, NanoHttpd.MIME_PLAINTEXT, "UNAUTHORIZED: Needs Authentication.");
            res.addHeader("WWW-Authenticate", "Basic realm=\"MyMobKit\"");
            res.setData(new ByteArrayInputStream("Needs Authentication".getBytes()));
            return res;
        }
        return respond(session, Collections.unmodifiableMap(session.getHeaders()), session.getUri());
    }

    public void setCustomStartPage(String pageName) {
        this.customStartPage = pageName;
    }

    protected Response respond(final IHTTPSession session, final Map<String, String> headers, String uri) {
        Response response;
        if (cors != null && Method.OPTIONS.equals(session.getMethod())) {
            response = new NanoHttpd.Response(Response.Status.OK, MIME_PLAINTEXT, null, 0);
        } else {
            response = defaultRespond(session, headers, uri);
        }

        if (cors != null) {
            response = addCORSHeaders(headers, response, cors);
        }
        return response;
    }

    protected Response defaultRespond(final IHTTPSession session, final Map<String, String> headers, String uri) {
        // Remove URL arguments
        uri = uri.trim().replace(File.separatorChar, '/');
        if (uri.startsWith("/")) {
            uri = uri.substring(1, uri.length());
        }
        if (uri.indexOf('?') >= 0)
            uri = uri.substring(0, uri.indexOf('?'));

        // Prohibit getting out of current directory
        if (uri.startsWith("..") || uri.endsWith("..") || uri.indexOf("../") >= 0) {
            return getForbiddenResponse("Won't serve ../ for security reasons.");
        }

        if (uri.endsWith("/") || uri.equalsIgnoreCase("")) {
            if (!TextUtils.isEmpty(customStartPage)) {
                uri = uri + customStartPage;
            } else {
                uri = uri + "index.html";
            }
        }

        InputStream assetFile;
        try {
            assetFile = assetManager.open(uri);
        } catch (IOException ex) {
            assetFile = null;
        }
        if (assetFile == null) {
            return getNotFoundResponse();
        }
        Response response = serveFile(session, headers, uri, assetFile);
        return response != null ? response : getNotFoundResponse();

    }

    protected Response serveFile(final IHTTPSession session, final Map<String, String> headers, String uri, InputStream file) {
        Response res = null;
        try {
            // Calculate etag
            final Random random = new Random();
            final String etag = Integer.toHexString((uri + "" + random.nextInt()).hashCode());
            String mime = session.getParms().get(MimeType.PARAM_MIME);
            if (TextUtils.isEmpty(mime)) {
                mime = getMimeTypeForFile(uri);
            }

            // Support (simple) skipping:
            long startFrom = 0;
            long endAt = -1;
            String range = headers.get("range");
            if (range != null) {
                if (range.startsWith("bytes=")) {
                    range = range.substring("bytes=".length());
                    int minus = range.indexOf('-');
                    try {
                        if (minus > 0) {
                            startFrom = Long.parseLong(range.substring(0, minus));
                            endAt = Long.parseLong(range.substring(minus + 1));
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            // get if-range header. If present, it must match etag or else we
            // should ignore the range request
            String ifRange = headers.get("if-range");
            boolean headerIfRangeMissingOrMatching = (ifRange == null || etag.equals(ifRange));

            String ifNoneMatch = headers.get("if-none-match");
            boolean headerIfNoneMatchPresentAndMatching = ifNoneMatch != null && (ifNoneMatch.equals("*") || ifNoneMatch.equals(etag));

            // Change return code and add Content-Range header when skipping is requested
            long fileLen = file.available();

            if (headerIfRangeMissingOrMatching && range != null && startFrom >= 0 && startFrom < fileLen) {
                // range request that matches current etag
                // and the startFrom of the range is satisfiable
                if (headerIfNoneMatchPresentAndMatching) {
                    // range request that matches current etag
                    // and the startFrom of the range is satisfiable
                    // would return range from file
                    // respond with not-modified
                    res = newFixedLengthResponse(Response.Status.NOT_MODIFIED, mime, "");
                    res.addHeader("ETag", etag);
                } else {
                    if (endAt < 0) {
                        endAt = fileLen - 1;
                    }
                    long newLen = endAt - startFrom + 1;
                    if (newLen < 0) {
                        newLen = 0;
                    }

                    file.skip(startFrom);

                    res = newFixedLengthResponse(Response.Status.PARTIAL_CONTENT, mime, file, newLen);
                    res.addHeader("Accept-Ranges", "bytes");
                    res.addHeader("Content-Length", "" + newLen);
                    res.addHeader("Content-Range", "bytes " + startFrom + "-" + endAt + "/" + fileLen);
                    res.addHeader("ETag", etag);
                }
            } else {
                if (headerIfRangeMissingOrMatching && range != null && startFrom >= fileLen) {
                    // return the size of the file
                    // 4xx responses are not trumped by if-none-match
                    res = newFixedLengthResponse(Response.Status.RANGE_NOT_SATISFIABLE, NanoHttpd.MIME_PLAINTEXT, "");
                    res.addHeader("Content-Range", "bytes */" + fileLen);
                    res.addHeader("ETag", etag);
                } else if (range == null && headerIfNoneMatchPresentAndMatching) {
                    // full-file-fetch request
                    // would return entire file
                    // respond with not-modified
                    res = newFixedLengthResponse(Response.Status.NOT_MODIFIED, mime, "");
                    res.addHeader("ETag", etag);
                } else if (!headerIfRangeMissingOrMatching && headerIfNoneMatchPresentAndMatching) {
                    // range request that doesn't match current etag
                    // would return entire (different) file
                    // respond with not-modified

                    res = newFixedLengthResponse(Response.Status.NOT_MODIFIED, mime, "");
                    res.addHeader("ETag", etag);
                } else {
                    // supply the file
                    res = newFixedLengthResponse(Response.Status.OK, mime, file, file.available());
                    res.addHeader("Accept-Ranges", "bytes");
                    res.addHeader("Content-Length", "" + fileLen);
                    res.addHeader("ETag", etag);
                }
            }
        } catch (IOException ioe) {
            res = getForbiddenResponse("Reading file failed.");
        }
        return res;
    }

    public boolean isAuthenticationRequired() {
        return isAuthenticationRequired;
    }

    public void setAuthenticationRequired(boolean isAuthenticationRequired) {
        this.isAuthenticationRequired = isAuthenticationRequired;
    }

    public boolean addAuthorizedUser(final String userName, final String userPassword) {
        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(userPassword)) {
            authorizedUsers.add(new User(userName, userPassword));
            return true;
        }
        return false;
    }


    protected Response getForbiddenResponse(String s) {
        return newFixedLengthResponse(Response.Status.FORBIDDEN, NanoHttpd.MIME_PLAINTEXT, "FORBIDDEN: " + s);
    }

    protected Response getInternalErrorResponse(String s) {
        return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHttpd.MIME_PLAINTEXT, "INTERNAL ERROR: " + s);
    }

    protected Response getNotFoundResponse() {
        return newFixedLengthResponse(Response.Status.NOT_FOUND, NanoHttpd.MIME_PLAINTEXT, "Error 404, file not found.");
    }

    private Response newFixedFileResponse(File file, String mime) throws FileNotFoundException {
        Response res;
        res = newFixedLengthResponse(Response.Status.OK, mime, new FileInputStream(file), (int) file.length());
        res.addHeader("Accept-Ranges", "bytes");
        return res;
    }

    protected Response addCORSHeaders(Map<String, String> queryHeaders, Response resp, String cors) {
        resp.addHeader("Access-Control-Allow-Origin", cors);
        resp.addHeader("Access-Control-Allow-Headers", calculateAllowHeaders(queryHeaders));
        resp.addHeader("Access-Control-Allow-Credentials", "true");
        resp.addHeader("Access-Control-Allow-Methods", ALLOWED_METHODS);
        resp.addHeader("Access-Control-Max-Age", "" + MAX_AGE);

        return resp;
    }

    private String calculateAllowHeaders(Map<String, String> queryHeaders) {
        // here we should use the given asked headers
        // but NanoHttpd uses a Map whereas it is possible for requester to send
        // several time the same header
        // let's just use default values for this version
        return System.getProperty(ACCESS_CONTROL_ALLOW_HEADER_PROPERTY_NAME, DEFAULT_ALLOWED_HEADERS);
    }

    private final static String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";

    private final static int MAX_AGE = 42 * 60 * 60;

    // explicitly relax visibility to package for tests purposes
    final static String DEFAULT_ALLOWED_HEADERS = "origin,accept,content-type";

    public final static String ACCESS_CONTROL_ALLOW_HEADER_PROPERTY_NAME = "AccessControlAllowHeader";

    protected Response parseSession(IHTTPSession session) {
        if (!Method.GET.equals(session.getMethod())) {
            try {
                Map<String, String> files = new HashMap<String, String>();
                session.parseBody(files);
                session.setFiles(files);
            } catch (IOException ioe) {
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHttpd.MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
            } catch (ResponseException re) {
                return newFixedLengthResponse(re.getStatus(), NanoHttpd.MIME_PLAINTEXT, re.getMessage());
            }
        }
        return null;
    }
}
