package com.mymobkit.service.api.ussd;

import android.text.TextUtils;

import com.mymobkit.model.ISession;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.mymobkit.common.LogUtils.makeLogTag;
import static com.mymobkit.common.LogUtils.LOGE;


/**
 * Created by MEKOH on 2/13/2016.
 */
public class UssdSession implements ISession {


    private static final String TAG = makeLogTag(UssdSession.class);

    private String sessionId;

    private long lastUpdated;

    private String command;

    private String expectedResponsePattern;

    private Pattern pattern;

    private List<UssdSessionResponse> responses;

    public UssdSession(final String sessionId, final String command, final String expectedResponsePattern, final Pattern pattern) {
        this.sessionId = sessionId;
        this.command = command;
        this.expectedResponsePattern = expectedResponsePattern;
        this.responses = new ArrayList<UssdSessionResponse>(1);
        this.pattern = pattern;
        updateLastUpdated();
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public long getLastUpdated() {
        return lastUpdated;
    }

    public void updateLastUpdated() {
        setLastUpdated(System.currentTimeMillis());
    }

    private void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void add(final UssdSessionResponse response) {
        responses.add(response);
        updateLastUpdated();
    }

    public UssdSessionResponse first() {
        if (responses.isEmpty()) return null;
        updateLastUpdated();
        return responses.get(0);
    }

    public boolean isResponseQueueEmpty() {
        return (responses.isEmpty());
    }

    public boolean matches(final String text) {
        if (TextUtils.isEmpty(text)) return false;
        if (TextUtils.isEmpty(expectedResponsePattern) && isResponseQueueEmpty()) return true;
        if (pattern != null && pattern.matcher(text).find() && isResponseQueueEmpty()) return true;
        return false;
    }
}
