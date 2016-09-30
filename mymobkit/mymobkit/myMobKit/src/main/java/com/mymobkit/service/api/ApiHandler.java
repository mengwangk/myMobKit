package com.mymobkit.service.api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mymobkit.app.AppConfig;
import com.mymobkit.common.ValidationUtils;
import com.mymobkit.common.StringUtils;
import com.mymobkit.model.ActionStatus;
import com.mymobkit.net.AppServer;
import com.mymobkit.net.NanoHttpd.Method;
import com.mymobkit.service.HttpdService;

import java.util.Map;

import static com.mymobkit.common.LogUtils.LOGD;
import static com.mymobkit.common.LogUtils.makeLogTag;

public abstract class ApiHandler {

    protected static final String TAG = makeLogTag(ApiHandler.class);

    protected HttpdService service;

    protected final Gson gson = new GsonBuilder().setDateFormat(AppConfig.UNIVERSAL_DATE_FORMAT).excludeFieldsWithoutExposeAnnotation().create();

    //protected final Gson gsonAll = new GsonBuilder().setDateFormat(AppConfig.UNIVERSAL_DATE_FORMAT).create();

    public ApiHandler(HttpdService service) {
        this.service = service;
    }

    final public String handle(final Map<String, String> header, final Map<String, String> params, final Map<String, String> files) {
        if (Method.GET.toString().equalsIgnoreCase(params.get(AppServer.HTTP_METHOD))) {
            return get(header, params, files);
        } else if (Method.POST.toString().equalsIgnoreCase(params.get(AppServer.HTTP_METHOD))) {
            return post(header, params, files);
        } else if (Method.PUT.toString().equalsIgnoreCase(params.get(AppServer.HTTP_METHOD))) {
            return put(header, params, files);
        } else if (Method.DELETE.toString().equalsIgnoreCase(params.get(AppServer.HTTP_METHOD))) {
            return delete(header, params, files);
        } else if (Method.HEAD.toString().equalsIgnoreCase(params.get(AppServer.HTTP_METHOD))) {
            return head(header, params, files);
        } else if (Method.OPTIONS.toString().equalsIgnoreCase(params.get(AppServer.HTTP_METHOD))) {
            return options(header, params, files);
        }
        return ActionStatus.OK;
    }

    public String get(final Map<String, String> header, final Map<String, String> params, final Map<String, String> files) {
        LOGD(TAG, "GET request");
        return ActionStatus.OK;
    }

    public String post(final Map<String, String> header, final Map<String, String> params, final Map<String, String> files) {
        LOGD(TAG, "POST request");
        return ActionStatus.OK;
    }

    public String put(final Map<String, String> header, final Map<String, String> params, final Map<String, String> files) {
        LOGD(TAG, "PUT request");
        return ActionStatus.OK;
    }

    public String delete(final Map<String, String> header, final Map<String, String> params, final Map<String, String> files) {
        LOGD(TAG, "DELETE request");
        return ActionStatus.OK;
    }

    public String head(final Map<String, String> header, final Map<String, String> params, final Map<String, String> files) {
        LOGD(TAG, "HEAD request");
        return ActionStatus.OK;
    }

    public String options(final Map<String, String> header, final Map<String, String> params, final Map<String, String> files) {
        LOGD(TAG, "OPTIONS request");
        return ActionStatus.OK;
    }

    protected void maybeAcquireWakeLock() {
        //if (!service.getWakeLock().isHeld()) {
        //	service.getWakeLock().acquire();
        //}
    }

    protected void releaseWakeLock() {
        //if (service.getWakeLock().isHeld()) {
        //	try {
        //		service.getWakeLock().release();
        //	} catch (Throwable th) {
        // ignoring this exception, probably wakeLock was already released
        //	}
        //}
    }

    protected Context getContext() {
        return service.getContext();
    }

    public void stop() {
    }

    public String getStringValue(final String key, final Map<String, String> values, String defaultValue) {
        return ValidationUtils.getStringValue(key, values, defaultValue);
    }

    public String getStringValue(final String key, final Map<String, String> values) {
        return ValidationUtils.getStringValue(key, values, StringUtils.EMPTY);
    }

    public long getLongValue(final String key, final Map<String, String> values) {
        return ValidationUtils.getLongValue(key, values, 0);
    }

    public long getLongValue(final String key, final Map<String, String> values, long defaultValue) {
        return ValidationUtils.getLongValue(key, values, defaultValue);
    }

    public int getIntegerValue(final String key, final Map<String, String> values, int defaultValue) {
        return ValidationUtils.getIntegerValue(key, values, defaultValue);
    }

    public int getIntegerValue(final String key, final Map<String, String> values) {
        return ValidationUtils.getIntegerValue(key, values, 0);
    }

    public boolean getBooleanValue(final String key, final Map<String, String> values, boolean defaultValue) {
        return ValidationUtils.getBooleanValue(key, values, defaultValue);
    }
}
