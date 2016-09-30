package com.mymobkit.service.api.ussd;

import android.content.Context;
import android.text.TextUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Created by MEKOH on 2/13/2016.
 */
public class UssdSessionManager {

    private static final String TAG = makeLogTag(UssdSessionManager.class);

    private Map<String, UssdSession> sessions = Collections.synchronizedMap(new LinkedHashMap<String, UssdSession>());

    private final Context context;

    public UssdSessionManager(final Context context) {
        this.context = context;
    }

    private UssdSession get(final String sessionId) {
        if (TextUtils.isEmpty(sessionId)) return null;
        if (sessions.containsKey(sessionId)) {
            return sessions.get(sessionId);
        }
        return null;
    }


    public UssdSessionResponse getResponse(final String sessionId) {
        final UssdSession session = get(sessionId);
        if (session == null) return null;

        final UssdSessionResponse response = session.first();
        //if (session.isResponseQueueEmpty()) {
        //    sessions.remove(sessionId);
        //}
        return response;
    }

    public void addSession(final UssdSession session) {
        if (session != null && !sessions.containsKey(session.getSessionId())) {
            sessions.put(session.getSessionId(), session);
        }
    }

    public void clear() {
        sessions.clear();
        sessions = null;
    }


    public Map<String, UssdSession> getSessions() {
        return sessions;
    }

}

