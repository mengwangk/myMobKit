package com.mymobkit.service.webcam;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;

import com.mymobkit.service.ICameraService;

import static com.mymobkit.common.LogUtils.LOGW;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Service to host a camera.
 */
public class WebcamService extends Service {

    private static final String TAG = makeLogTag(WebcamService.class);

    private WebcamWrapper webcamWrapper;

    private static PowerManager powerMgr;
    private static PowerManager.WakeLock wakeLock;

    /*
     * Remote methods.
     */
    private final class CameraServiceProvider extends ICameraService.Stub {

        @Override
        public boolean hide() throws RemoteException {
            if (webcamWrapper != null) {
                webcamWrapper.hide();
            }
            return true;
        }

        @Override
        public boolean show() throws RemoteException {
            if (webcamWrapper != null) {
                webcamWrapper.show();
            }
            return true;
        }

        @Override
        public boolean isAlive() throws RemoteException {
            return false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new CameraServiceProvider();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        powerMgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerMgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG + " WakeLock Service");
        wakeLock.acquire();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LOGW(TAG, "[onStartCommand] Starting webcam service");
        webcamWrapper = new WebcamWrapper(this);
        webcamWrapper.start();
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        LOGW(TAG, "[onDestroy] Stopping webcam service");
        super.onDestroy();
        if (webcamWrapper != null) {
            webcamWrapper.stop();
            webcamWrapper = null;
        }

        if (wakeLock.isHeld()) {
            try {
                wakeLock.release();
            } catch (Throwable th) {
                // ignoring this exception, probably wakeLock was already released
            }
        }
    }
}
