package com.mymobkit.app;

import android.content.Context;

import com.mymobkit.R;
import com.mymobkit.common.AppPreference;
import com.mymobkit.common.GcmUtils;
import com.mymobkit.data.MmsHelper;
import com.mymobkit.data.SmsHelper;
import com.mymobkit.enums.MessagingAgingMethod;
import com.mymobkit.model.ISession;
import com.mymobkit.service.api.ussd.UssdSession;
import com.mymobkit.service.api.ussd.UssdSessionManager;
import com.mymobkit.ui.fragment.ServiceSettingsFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Global session manager.
 * <p/>
 * Created by MEKOH on 2/13/2016.
 */
public class SessionManager {

    private UssdSessionManager ussdSessionManager;
    private SmsHelper smsHelper;
    private MmsHelper mmsHelper;

    private final Context context;

    private static int SCHEDULER_INTERVAL_MINUTES = 1;

    private static int SESSION_TIME_OUT_USSD_SESSION = 8;   // 8 minutes
    private static int HEART_BEAT_INTERVAL = 3;             // 3 minutes
    private static int MESSAGE_HOUSEKEEP_INTERVAL = 60;     // 60 minutes

    private long lastHeartBeatSent = System.currentTimeMillis();
    private long lastMessageHouseKeep = 0;

    private ScheduledExecutorService sessionCleanupScheduler = Executors.newSingleThreadScheduledExecutor();

    public SessionManager(final Context context) {
        this.context = context;
        this.ussdSessionManager = new UssdSessionManager(context);
        smsHelper = SmsHelper.getSmsHelper(context);
        mmsHelper = MmsHelper.getMmsHelper(context);

        sessionCleanupScheduler.scheduleAtFixedRate
                (new Runnable() {
                    public void run() {

                        // -------------  Clear outdated USSD sessions
                        final long ussdSessionTimeout = SESSION_TIME_OUT_USSD_SESSION * 60 * 1000;
                        final Map<String, UssdSession> ussdSessions = ussdSessionManager.getSessions();
                        final Set<String> ussdSessionKeys = ussdSessions.keySet();
                        final List<String> outDatedUssdSessions = new ArrayList<String>(1);
                        for (final String key : ussdSessionKeys) {
                            final ISession session = ussdSessions.get(key);
                            if ((System.currentTimeMillis() - session.getLastUpdated()) > ussdSessionTimeout) {
                                outDatedUssdSessions.add(key);
                            }
                        }
                        if (outDatedUssdSessions.size() > 0) {
                            for (final String sessionId : outDatedUssdSessions) {
                                ussdSessions.remove(sessionId);
                            }
                        }

                        // End USSD session clean up ----------------------------------------------


                        // GCM heart beat hack
                        final long heartBeatTimeout = HEART_BEAT_INTERVAL * 60 * 1000;
                        if ((System.currentTimeMillis() - lastHeartBeatSent) > heartBeatTimeout) {
                            sendGcmHeartBeat();
                            lastHeartBeatSent = System.currentTimeMillis();
                        }

                        // Message housekeep
                        final long housekeepMessageTimeout = MESSAGE_HOUSEKEEP_INTERVAL * 60 * 1000;
                        if ((System.currentTimeMillis() - lastMessageHouseKeep) > housekeepMessageTimeout) {
                            housekeepMessages();
                            lastMessageHouseKeep = System.currentTimeMillis();
                        }

                    }
                }, SCHEDULER_INTERVAL_MINUTES, SCHEDULER_INTERVAL_MINUTES, TimeUnit.MINUTES);
    }


    public void clear() {
        if (sessionCleanupScheduler != null) {
            sessionCleanupScheduler.shutdown();
            sessionCleanupScheduler = null;
        }

        if (ussdSessionManager != null) {
            ussdSessionManager.clear();
            ussdSessionManager = null;
        }
    }

    public UssdSessionManager getUssdSessionManager() {
        return ussdSessionManager;
    }

    /**
     * A hack to keep the GCM heartbeat.
     *
     * https://github.com/joaopedronardari/AndroidHeartBeatFixer/blob/master/AndroidHeartBeatFixer-Sample/app/src/main/java/com/joaopedronardari/androidheartbeatfixer/services/HeartBeatService.java
     */
    private void sendGcmHeartBeat(){
        GcmUtils.broadcastHeartBeat(context);
        //context.sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
        //context.sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));
    }

    public void housekeepMessages() {
        MessagingAgingMethod method = MessagingAgingMethod.get(AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_MESSAGING_AGING_METHOD, context.getString(R.string.default_messaging_aging_method)));
        if (method == MessagingAgingMethod.DAYS) {
            int days = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_MESSAGING_AGING_DAYS, Integer.valueOf(context.getString(R.string.default_messaging_aging_days)));
            smsHelper.deleteOldSms(days);
            mmsHelper.deleteOldMms(days);
        } else {
            int totalRecords = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_MESSAGING_AGING_SIZE, Integer.valueOf(context.getString(R.string.default_messaging_aging_size)));
            smsHelper.deleteOldSmsByNumber(totalRecords);
            mmsHelper.deleteOldMmsByNumber(totalRecords);
        }
    }
}
