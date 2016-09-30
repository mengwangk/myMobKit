package com.mymobkit.service.api;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.mymobkit.common.DeviceUtils;
import com.mymobkit.common.NetworkUtils;
import com.mymobkit.net.AppServer;
import com.mymobkit.service.HttpdService;
import com.mymobkit.service.api.status.DeviceBatteryInfo;
import com.mymobkit.service.api.status.DeviceNetworkInfo;
import com.mymobkit.service.api.status.DeviceSignalStrength;
import com.mymobkit.service.api.status.GetBatteryInfoRequest;
import com.mymobkit.service.api.status.GetDeviceInfoRequest;
import com.mymobkit.service.api.status.GetNetworkInfoRequest;

import java.util.Map;

/**
 * Service to check device status.
 */
public final class StatusApiHandler extends ApiHandler {

    private static final String REQUEST_TYPE_NETWORK = "network";
    private static final String REQUEST_TYPE_BATTERY = "battery";
    private static final String PARAM_SLOT = "slot";

    private TelephonyManager telephonyManager;
    private MyPhoneStateListener phoneListener;
    private DeviceSignalStrength deviceSignalStrength;

    private class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onSignalStrengthsChanged(final SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            if (deviceSignalStrength == null) {
                deviceSignalStrength = new DeviceSignalStrength();
            }
            deviceSignalStrength.setCdmaDbm(signalStrength.getCdmaDbm());
            deviceSignalStrength.setCdmaEcio(signalStrength.getCdmaEcio());
            deviceSignalStrength.setEvdoDbm(signalStrength.getEvdoDbm());
            deviceSignalStrength.setEvdoEcio(signalStrength.getEvdoEcio());
            deviceSignalStrength.setEvdoSnr(signalStrength.getEvdoSnr());
            deviceSignalStrength.setGsmBitErrorRate(signalStrength.getGsmBitErrorRate());
            deviceSignalStrength.setGsmSignalStrength(signalStrength.getGsmSignalStrength());
            deviceSignalStrength.setGsm(signalStrength.isGsm());
        }
    }

    /**
     * Constructor
     *
     * @param service HTTP service instance.
     */
    public StatusApiHandler(final HttpdService service) {
        super(service);
        phoneListener = new MyPhoneStateListener();
        telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }

    @Override
    public String get(final Map<String, String> header, final Map<String, String> params, final Map<String, String> files) {
        //Gson gson = new GsonBuilder().setDateFormat(AppConfig.UNIVERSAL_DATE_FORMAT).excludeFieldsWithoutExposeAnnotation().create();
        try {
            maybeAcquireWakeLock();
            if (params.containsKey(AppServer.URI_PARAM_PREFIX + "0")) {
                final String requestType = getStringValue(AppServer.URI_PARAM_PREFIX + "0", params);
                if (!TextUtils.isEmpty(requestType)) {
                    if (REQUEST_TYPE_NETWORK.equalsIgnoreCase(requestType)) {

                        final String simSlot = getStringValue(PARAM_SLOT, params);
                        DeviceNetworkInfo networkInfo = null;
                        if (TextUtils.isEmpty(simSlot)) {
                            networkInfo = NetworkUtils.getNetworkInfo(getContext());
                        } else {
                            networkInfo = NetworkUtils.getNetworkInfo(getContext(), simSlot);
                        }
                        GetNetworkInfoRequest networkInfoRequest = new GetNetworkInfoRequest();
                        networkInfoRequest.setNetworkInfo(networkInfo);
                        if (deviceSignalStrength != null)
                            networkInfo.setSignalStrength(deviceSignalStrength);
                        return gson.toJson(networkInfoRequest);
                    } else if (REQUEST_TYPE_BATTERY.equalsIgnoreCase(requestType)) {
                        DeviceBatteryInfo batteryInfo = DeviceUtils.getBatteryInfo(getContext());
                        GetBatteryInfoRequest batteryInfoRequest = new GetBatteryInfoRequest();
                        batteryInfoRequest.setBatteryInfo(batteryInfo);
                        return gson.toJson(batteryInfoRequest);
                    } else {
                        GetDeviceInfoRequest deviceInfoRequest = getAllInfo();
                        return gson.toJson(deviceInfoRequest);
                    }
                } else {
                    GetDeviceInfoRequest deviceInfoRequest = getAllInfo();
                    return gson.toJson(deviceInfoRequest);
                }
            } else {
                GetDeviceInfoRequest deviceInfoRequest = getAllInfo();
                return gson.toJson(deviceInfoRequest);
            }
        } catch (Exception ex) {
            GetDeviceInfoRequest deviceInfoRequest = new GetDeviceInfoRequest();
            deviceInfoRequest.isSuccessful = false;
            deviceInfoRequest.description = ex.getMessage();
            return gson.toJson(deviceInfoRequest);
        } finally {
            releaseWakeLock();
        }
    }

    private GetDeviceInfoRequest getAllInfo() {
        GetDeviceInfoRequest deviceInfoRequest = new GetDeviceInfoRequest();
        deviceInfoRequest.setDeviceName(DeviceUtils.getDeviceName());
        DeviceNetworkInfo networkInfo = NetworkUtils.getNetworkInfo(getContext());
        DeviceBatteryInfo batteryInfo = DeviceUtils.getBatteryInfo(getContext());
        if (deviceSignalStrength != null)
            networkInfo.setSignalStrength(deviceSignalStrength);
        deviceInfoRequest.setNetworkInfo(networkInfo);
        deviceInfoRequest.setBatteryInfo(batteryInfo);
        return deviceInfoRequest;
    }

    @Override
    public void stop() {
        super.stop();
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_NONE);
    }

}
