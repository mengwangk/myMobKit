package com.mymobkit.service.api;

import android.location.Location;
import android.os.Looper;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mymobkit.R;
import com.mymobkit.app.AppController;
import com.mymobkit.service.HttpdService;
import com.mymobkit.service.api.location.GetRequest;

import java.util.Map;

import static com.mymobkit.common.LogUtils.makeLogTag;
import static com.mymobkit.common.LogUtils.LOGE;

/**
 * API to handle GPS location.
 */
public class LocationApiHandler extends ApiHandler implements LocationListener {

    private static final String TAG = makeLogTag(LocationApiHandler.class);

    private Location lastLocation;
    private LocationRequest locationRequest;
    private boolean isRequestForLocationUpdate = false;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    /**
     * Constructor.
     *
     * @param service HTTPD service.
     */
    public LocationApiHandler(final HttpdService service) {
        super(service);

        // Create location request
        createLocationRequest();

        // Start location updates - Don't start GPS until requested
        // startLocationUpdates(false);
    }

    @Override
    public String get(Map<String, String> header, Map<String, String> params, Map<String, String> files) {
        if (!isRequestForLocationUpdate) {
            startLocationUpdates(true);
        }
        final GetRequest request = new GetRequest();
        try {
            maybeAcquireWakeLock();
            if (AppController.googleApiClient != null && AppController.googleApiClient.isConnected()) {
                lastLocation = LocationServices.FusedLocationApi.getLastLocation(AppController.googleApiClient);
                if (lastLocation != null) {
                    request.isSuccessful = true;
                    request.setLatitude(lastLocation.getLatitude());
                    request.setLongitude(lastLocation.getLongitude());
                } else {
                    request.isSuccessful = false;
                    request.setDescription(getContext().getString(R.string.msg_location_not_available));
                }
            } else {
                // Not connected to Google Play services
                request.isSuccessful = false;
                request.setDescription(getContext().getString(R.string.msg_location_not_available));
            }
        } catch (Exception ex) {
            request.isSuccessful = false;
            request.setDescription(ex.getMessage());
        } finally {
            releaseWakeLock();
        }
        return gson.toJson(request);
    }


    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        lastLocation = location;
    }

    /**
     * Creating location request object
     */
    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FATEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Starting the location updates
     */
    protected void startLocationUpdates(final boolean isLooperRequired) {
        if (AppController.googleApiClient != null && AppController.googleApiClient.isConnected()) {
            try {
                if (isLooperRequired)
                    Looper.prepare();
                LocationServices.FusedLocationApi.requestLocationUpdates(AppController.googleApiClient, locationRequest, this);
                isRequestForLocationUpdate = true;
                if (isLooperRequired)
                    Looper.loop();
            } catch (Exception e){
                LOGE(TAG, "[startLocationUpdates] Unable to start location update", e);
            }
        }
    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        if (AppController.googleApiClient != null && AppController.googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(AppController.googleApiClient, this);
            isRequestForLocationUpdate = false;
        }
    }

    @Override
    public void stop() {
        super.stop();
        stopLocationUpdates();
    }
}
