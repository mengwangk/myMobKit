package com.mymobkit.service.webcam;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.mymobkit.opencv.motion.detection.data.GlobalData;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * This class processes sensor data and location data. It
 * is used to detect when the phone is in motion, so we do not try to detect
 * motion.
 */
public class SensorService implements SensorEventListener {

    private static final String TAG = makeLogTag(SensorService.class);

    private static final AtomicBoolean computing = new AtomicBoolean(false);

    private static final float grav[] = new float[3]; // Gravity (a.k.a accelerometer data)
    private static final float mag[] = new float[3]; // Magnetic

    private static final float gravThreshold = 0.5f;
    private static final float magThreshold = 1.0f;

    private static SensorManager sensorMgr = null;
    private static List<Sensor> sensors = null;
    private static Sensor sensorGrav = null;
    private static Sensor sensorMag = null;

    private static float prevGrav = 0.0f;
    private static float prevMag = 0.0f;

    private Context context = null;

    public SensorService(final Context context){
        this.context = context;
    }

    public void start() {
        try {
            sensorMgr = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

            sensors = sensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER);
            if (sensors.size() > 0) sensorGrav = sensors.get(0);

            sensors = sensorMgr.getSensorList(Sensor.TYPE_MAGNETIC_FIELD);
            if (sensors.size() > 0) sensorMag = sensors.get(0);

            sensorMgr.registerListener(this, sensorGrav, SensorManager.SENSOR_DELAY_NORMAL);
            sensorMgr.registerListener(this, sensorMag, SensorManager.SENSOR_DELAY_NORMAL);
        } catch (Exception ex1) {
            try {
                if (sensorMgr != null) {
                    sensorMgr.unregisterListener(this, sensorGrav);
                    sensorMgr.unregisterListener(this, sensorMag);
                    sensorMgr = null;
                }
            } catch (Exception ex2) {
                LOGE(TAG, "[onStart] SensorMgr error", ex2);
            }
        }
    }

    public void stop() {
        try {
            try {
                sensorMgr.unregisterListener(this, sensorGrav);
            } catch (Exception ex) {
                LOGE(TAG, "[onStop] SensorMgr error", ex);
            }
            try {
                sensorMgr.unregisterListener(this, sensorMag);
            } catch (Exception ex) {
                LOGE(TAG, "[onStop] SensorMgr error", ex);
            }
            sensorMgr = null;
        } catch (Exception ex) {
            LOGE(TAG, "[onStop] SensorMgr error", ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSensorChanged(SensorEvent evt) {
        if (!computing.compareAndSet(false, true)) return;

        if (evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            grav[0] = evt.values[0];
            grav[1] = evt.values[1];
            grav[2] = evt.values[2];
        } else if (evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mag[0] = evt.values[0];
            mag[1] = evt.values[1];
            mag[2] = evt.values[2];
        }

        float gravity = grav[0] + grav[1] + grav[2];
        float magnetic = mag[0] + mag[1] + mag[2];

        float gravDiff = Math.abs(gravity - prevGrav);
        float magDiff = Math.abs(magnetic - prevMag);

        if ((Float.compare(prevGrav, 0.0f) != 0 && Float.compare(prevMag, 0.0f) != 0) && (gravDiff > gravThreshold || magDiff > magThreshold)) {
            GlobalData.setPhoneInMotion(true);
        } else {
            GlobalData.setPhoneInMotion(false);
        }

        prevGrav = gravity;
        prevMag = magnetic;

        computing.set(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if (sensor == null) throw new NullPointerException();

        if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD && accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            LOGE(TAG, "Compass data unreliable");
        }
    }
}
