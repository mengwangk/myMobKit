package com.mymobkit.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.mymobkit.app.AppController;
import com.mymobkit.service.api.ussd.UssdSession;
import com.mymobkit.service.api.ussd.UssdSessionManager;
import com.mymobkit.service.api.ussd.UssdSessionResponse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.mymobkit.common.LogUtils.LOGD;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Created by MEKOH on 2/13/2016.
 */
public class UssdService extends AccessibilityService {

    private static final String TAG = makeLogTag(UssdService.class);

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        final AccessibilityNodeInfo source = event.getSource();
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && !String.valueOf(event.getClassName()).contains("AlertDialog")) {
            return;
        }
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && (source == null || !source.getClassName().equals("android.widget.TextView"))) {
            return;
        }
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && TextUtils.isEmpty(source.getText())) {
            return;
        }

        List<CharSequence> eventText;
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            eventText = event.getText();
        } else {
            eventText = Collections.singletonList(source.getText());
        }
        final String text = eventText.toString();
        if (TextUtils.isEmpty(text)) return;

        // Close dialog
        performGlobalAction(GLOBAL_ACTION_BACK); // This works on 4.1+ only

        // Get USSD session manager
        final UssdSessionManager ussdSessionManager = AppController.getSessionManager().getUssdSessionManager();

        // Get latest USSD session and compare the pattern
        final Collection<UssdSession> ussdSessions = ussdSessionManager.getSessions().values();
        final List<UssdSession> ussdSessionList = new ArrayList<UssdSession>(ussdSessions);
        final int upperIndex = ussdSessionList.size() - 1;
        if (upperIndex >= 0) {
            for (int i = upperIndex; i >= 0; i--) {
                final UssdSession ussdSession = ussdSessionList.get(i);
                if (ussdSession.matches(text)) {
                    ussdSession.add(new UssdSessionResponse(text, ussdSession.getLastUpdated()));
                    break;
                }
            }
        }

    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        LOGD(TAG, "onServiceConnected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.packageNames = new String[]{"com.android.phone"};
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
    }
}
