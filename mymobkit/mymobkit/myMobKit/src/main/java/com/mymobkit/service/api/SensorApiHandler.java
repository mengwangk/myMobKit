package com.mymobkit.service.api;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.mymobkit.common.StringUtils;
import com.mymobkit.common.ValidationUtils;
import com.mymobkit.net.AppServer;
import com.mymobkit.service.HttpdService;
import com.mymobkit.service.api.sensor.GetRequest;
import com.mymobkit.service.api.sensor.GetRequestAccelerometer;
import com.mymobkit.service.api.sensor.GetRequestMagneticField;

import java.util.Date;
import java.util.Map;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Sensor API handler.
 */
public class SensorApiHandler extends ApiHandler implements SensorEventListener {

    private static final String PARAM_ACCELEROMETER = "accelerometer";
    private static final String PARAM_MAGNETIC_FIELD = "magnetic";
    private static final String PARAM_GYROSCOPE = "gyroscope";
    private static final String PARAM_PHONE_RADIO = "radio";

    // Log Tag
    private static final String TAG = makeLogTag(SensorApiHandler.class);

    private SensorManager sensorManager = null;

    private GetRequestMagneticField getRequestMagneticField = new GetRequestMagneticField();
    private GetRequestAccelerometer getRequestAccelerometer = new GetRequestAccelerometer();
    private GetRequest getRequest = new GetRequest();

    /**
     * Constructor.
     *
     * @param service HTTPD service.
     */
    public SensorApiHandler(final HttpdService service) {
        super(service);
        try {
            sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);

            // Register magnetic sensor
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        } catch (Exception e) {
            LOGE(TAG, "[SensorApiHandler] Unable to initialize sensor manager", e);
        }
    }

    @Override
    public String get(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
        try {
            maybeAcquireWakeLock();

            // Check the requested sensor data
            if (params.containsKey(AppServer.URI_PARAM_PREFIX + "0")) {
                final String sensorType = ValidationUtils.getStringValue(AppServer.URI_PARAM_PREFIX + "0", params, StringUtils.EMPTY);
                if (PARAM_MAGNETIC_FIELD.equalsIgnoreCase(sensorType)) {
                    return gson.toJson(getRequestMagneticField);
                } else if (PARAM_ACCELEROMETER.equalsIgnoreCase(sensorType)) {
                    return gson.toJson(getRequestAccelerometer);
                }
            } else {
                // Return all sensor data
                return gson.toJson(getRequest);
            }
        } catch (Exception ex) {
            getRequest.isSuccessful = false;
            getRequest.setDescription(ex.getMessage());
        } finally {
            releaseWakeLock();
        }
        return gson.toJson(getRequest);
    }

    @Override
    public void stop() {
        super.stop();

        // Stop listening
        if (sensorManager != null){
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            getRequestMagneticField.setX(event.values[0]);
            getRequestMagneticField.setY(event.values[1]);
            getRequestMagneticField.setZ(event.values[2]);
            getRequestMagneticField.setTimestamp(new Date(System.currentTimeMillis()));
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getRequestAccelerometer.setX(event.values[0]);
            getRequestAccelerometer.setY(event.values[1]);
            getRequestAccelerometer.setZ(event.values[2]);
            getRequestAccelerometer.setTimestamp(new Date(System.currentTimeMillis()));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
