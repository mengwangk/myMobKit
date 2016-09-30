package com.mymobkit.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.CellLocation;
import android.telephony.SubscriptionInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;

import com.mymobkit.app.AppController;
import com.mymobkit.gsm.DualSimManager;
import com.mymobkit.gsm.DualSimManagerLollipop;
import com.mymobkit.service.api.status.DeviceCdmaCellLocation;
import com.mymobkit.service.api.status.DeviceGsmCellLocation;
import com.mymobkit.service.api.status.DeviceNetworkInfo;
import com.mymobkit.service.api.status.DeviceNetworkInfo.CellLocationType;
import com.mymobkit.service.api.status.DeviceNetworkInfo.ConnectionType;
import com.mymobkit.service.api.status.DeviceNetworkInfo.PhoneType;
import com.mymobkit.service.api.status.DeviceNetworkInfo.SimState;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.conn.util.InetAddressUtils;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.makeLogTag;

public final class NetworkUtils {

    private static final String TAG = makeLogTag(NetworkUtils.class);

   /*
   public static String getIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
                        String ipAddr = inetAddress.getHostAddress();
                        return ipAddr;
                    }
                }
            }
        } catch (SocketException ex) {
            LOGE(TAG, "[getLocalIpAddress] Error retrieving IP address", ex);
        }
        return "";
    }
    */

    protected static String wifiIpAddress() {
        try {
            WifiManager wifiManager = (WifiManager) AppController.getContext().getSystemService(Context.WIFI_SERVICE);
            int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

            // Convert little-endian to big-endian if needed
            if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
                ipAddress = Integer.reverseBytes(ipAddress);
            }

            byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();
            String ipAddressString;
            try {
                ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
            } catch (UnknownHostException ex) {
                LOGE(TAG, "[wifiIpAddress] Unable to get host address.", ex);
                ipAddressString = "";
            }
            return ipAddressString;
        } catch (Exception ex) {
            return "";
        }
    }

    public static String getLocalIpAddress(final boolean useIPv4) {
        String usbAddress = wifiIpAddress();
        if (usbAddress != null && !TextUtils.isEmpty(usbAddress)) {
            return usbAddress;
        }
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress() && !addr.isLinkLocalAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        final boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4) {
                                if (intf.getDisplayName().startsWith("usb"))
                                    usbAddress = sAddr;
                                else
                                    return sAddr;
                            }
                        } else {
                            if (!isIPv4) {
                                // drop ip6 port suffix
                                int delim = sAddr.indexOf('%');
                                return delim < 0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            LOGE(TAG, "[getIPAddress] Error retrieving IP address", ex);
        }
        if (!useIPv4) {
            return getLocalIpAddress(true);
        }
        return usbAddress;
    }

    public static String getLocalIpAddress() {
        return getLocalIpAddress(true);
    }

    /**
     * Checks if there is Internet connection or data connection on the device.
     *
     * @param context - The activity calling this method.
     * @return boolean
     */
    public static boolean isConnected(final Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;

    }

   /* public static String getPhoneNumber(final Context context) {
        TelephonyManager mTelephonyMgr;
        mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String number = mTelephonyMgr.getLine1Number();
        if (!TextUtils.isEmpty(number)) {
            return number;
        }
        return StringUtils.EMPTY;
    }*/

    public static DeviceNetworkInfo getNetworkInfo(final Context context) {
        DeviceNetworkInfo networkInfo = new DeviceNetworkInfo();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = telephonyManager.getNetworkType();
        networkInfo.setNetworkType(TelephonyUtils.getNetworkTypeName(context, networkType));

        networkInfo.setNetworkRoaming(telephonyManager.isNetworkRoaming());
        networkInfo.setNetworkCountryIso(telephonyManager.getNetworkCountryIso());
        networkInfo.setNetworkOperator(telephonyManager.getNetworkOperator());
        networkInfo.setNetworkOperatorName(telephonyManager.getNetworkOperatorName());
        String number = telephonyManager.getLine1Number();
        if (!TextUtils.isEmpty(number)) {
            networkInfo.setPhoneNumber(number);
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            networkInfo.setConnected(activeNetwork != null && activeNetwork.isConnectedOrConnecting());

            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                networkInfo.setConnectionType(ConnectionType.WIFI);
            else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                networkInfo.setConnectionType(ConnectionType.MOBILE);
        }

        networkInfo.setIpAddress(getLocalIpAddress());
        networkInfo.setDeviceId(telephonyManager.getDeviceId());
        networkInfo.setDeviceSoftwareVersion(telephonyManager.getDeviceSoftwareVersion());
        networkInfo.setSubscriberId(telephonyManager.getSubscriberId());
        networkInfo.setVoiceMailNumber(telephonyManager.getVoiceMailNumber());
        networkInfo.setSimCountryIso(telephonyManager.getSimCountryIso());
        networkInfo.setSimOperator(telephonyManager.getSimOperator());
        networkInfo.setSimOperatorName(telephonyManager.getSimOperatorName());
        networkInfo.setSimSerialNumber(telephonyManager.getSimSerialNumber());

        switch (telephonyManager.getSimState()) {
            case TelephonyManager.SIM_STATE_UNKNOWN:
                networkInfo.setSimState(SimState.UNKNOWN);
                break;
            case TelephonyManager.SIM_STATE_ABSENT:
                networkInfo.setSimState(SimState.ABSENT);
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                networkInfo.setSimState(SimState.PIN_REQUIRED);
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                networkInfo.setSimState(SimState.PUK_REQUIRED);
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                networkInfo.setSimState(SimState.NETWORK_LOCKED);
                break;
            case TelephonyManager.SIM_STATE_READY:
                networkInfo.setSimState(SimState.READY);
                break;
            default:
                networkInfo.setSimState(SimState.ERROR);
                break;
        }

        switch (telephonyManager.getPhoneType()) {
            case TelephonyManager.PHONE_TYPE_GSM:
                networkInfo.setPhoneType(PhoneType.GSM);
                break;
            case TelephonyManager.PHONE_TYPE_CDMA:
                networkInfo.setPhoneType(PhoneType.CDMA);
                break;
            case TelephonyManager.PHONE_TYPE_SIP:
                networkInfo.setPhoneType(PhoneType.SIP);
                break;
            case TelephonyManager.PHONE_TYPE_NONE:
                networkInfo.setPhoneType(PhoneType.NONE);
                break;
            default:
                networkInfo.setPhoneType(PhoneType.NONE);
                break;
        }

        CellLocation cellLocation = telephonyManager.getCellLocation();
        if (cellLocation != null) {
            if (cellLocation instanceof GsmCellLocation) {
                networkInfo.setCellLocationType(CellLocationType.GSM);
                GsmCellLocation gsmCellLocation = (GsmCellLocation) cellLocation;
                DeviceGsmCellLocation deviceGsmCellLocation = new DeviceGsmCellLocation(gsmCellLocation.getCid(), gsmCellLocation.getPsc(), gsmCellLocation.getLac());
                networkInfo.setGsmCellLocation(deviceGsmCellLocation);
            } else if (cellLocation instanceof CdmaCellLocation) {
                networkInfo.setCellLocationType(CellLocationType.CDMA);
                CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) cellLocation;
                DeviceCdmaCellLocation deviceCdmaCellLocation = new DeviceCdmaCellLocation(cdmaCellLocation.getBaseStationId(),
                        cdmaCellLocation.getBaseStationLatitude(), cdmaCellLocation.getBaseStationLongitude(),
                        cdmaCellLocation.getNetworkId(), cdmaCellLocation.getSystemId());
                networkInfo.setCdmaCellLocation(deviceCdmaCellLocation);
            }
        }

        return networkInfo;
    }

    public static DeviceNetworkInfo getNetworkInfo(final Context context, final String simSlot) {
        try {
            if (!SmsUtils.SIM_SLOT_1.equals(simSlot) && !SmsUtils.SIM_SLOT_2.equals(simSlot)) {
                return NetworkUtils.getNetworkInfo(context);
            }
            DeviceNetworkInfo networkInfo = new DeviceNetworkInfo();
            if (PlatformUtils.isLollipopMr1OrHigher()) {
                final DualSimManagerLollipop simManager = new DualSimManagerLollipop(context);
                final List<SubscriptionInfo> subscriptionInfos = simManager.getActiveSubscriptionInfo();
                final DualSimManager dualSimInfo = new DualSimManager(context);
                if (SmsUtils.SIM_SLOT_1.equals(simSlot)) {
                    setNetworkInfo(networkInfo, subscriptionInfos, simManager, dualSimInfo, 0);
                } else {
                    setNetworkInfo(networkInfo, subscriptionInfos, simManager, dualSimInfo, 1);
                }
            } else {
                DualSimManager dualSimInfo = new DualSimManager(context);
                if (dualSimInfo.isSimSupported()) {
                    if (SmsUtils.SIM_SLOT_1.equals(simSlot)) {
                        if (dualSimInfo.isFirstSimActive()) {
                            setNetworkInfo(networkInfo, dualSimInfo, 0);
                        } else {
                            return NetworkUtils.getNetworkInfo(context);
                        }
                    } else if (SmsUtils.SIM_SLOT_2.equals(simSlot)) {
                        if (dualSimInfo.isDualSIMSupported() && dualSimInfo.isSecondSimActive()) {
                            setNetworkInfo(networkInfo, dualSimInfo, 1);
                        } else {
                            return NetworkUtils.getNetworkInfo(context);
                        }
                    }
                }
                return networkInfo;
            }
        } catch (Exception e) {
            LOGE(TAG, "[getNetworkInfo] Unable to get network info", e);
        }
        return null;
    }

    private static void setNetworkInfo(final DeviceNetworkInfo networkInfo, List<SubscriptionInfo> subscriptionInfos,
                                       final DualSimManagerLollipop simManager, final DualSimManager dualSimInfo,
                                       final int index) {

        if (subscriptionInfos.size() == 0) return;

        if (subscriptionInfos.size() == 1 && index == 1) {
            return;
        }
        try {
            // TODO: Not 100% completed yet.
            SubscriptionInfo subscriptionInfo = subscriptionInfos.get(index);
            networkInfo.setSubscriberId(subscriptionInfo.getIccId());
            //networkInfo.setNetworkOperator(String.valueOf(dualSimInfo.getNetworkOperatorCode(index)[0]) + String.valueOf(dualSimInfo.getNetworkOperatorCode(index)[1]));
            networkInfo.setNetworkOperator(String.valueOf(subscriptionInfo.getMcc()) + String.valueOf(subscriptionInfo.getMnc()));
            networkInfo.setNetworkType(dualSimInfo.getNetworkType(index));
            networkInfo.setNetworkRoaming(dualSimInfo.isRoaming(index));
            networkInfo.setNetworkOperatorName(String.valueOf(subscriptionInfo.getCarrierName()));
            networkInfo.setSimCountryIso(subscriptionInfo.getCountryIso());
            networkInfo.setNetworkCountryIso(subscriptionInfo.getCountryIso());
            networkInfo.setDeviceId(dualSimInfo.getImei(index));
            networkInfo.setPhoneNumber(subscriptionInfo.getNumber());
            networkInfo.setNetworkCountryIso(dualSimInfo.getNetworkCountryIso(index));
            networkInfo.setSimOperator(dualSimInfo.getSimOperatorCode(index));
            networkInfo.setSimOperatorName(dualSimInfo.getSimOperatorName(index));
            networkInfo.setSimSerialNumber(dualSimInfo.getSimSerial(index));
            networkInfo.setIpAddress(getLocalIpAddress());

        } catch (Exception e) {
            LOGE(TAG, "[setNetworkInfo] Unable to get network info", e);
        }
    }

    private static void setNetworkInfo(final DeviceNetworkInfo networkInfo, final DualSimManager dualSimInfo, final int index) {
        // TODO: Not 100% completed yet. Need to add more reflection methods
        networkInfo.setNetworkType(dualSimInfo.getNetworkType(index));
        networkInfo.setNetworkRoaming(dualSimInfo.isRoaming(index));
        networkInfo.setNetworkCountryIso(dualSimInfo.getNetworkCountryIso(index));
        networkInfo.setNetworkOperator(String.valueOf(dualSimInfo.getNetworkOperatorCode(index)[0]) + String.valueOf(dualSimInfo.getNetworkOperatorCode(index)[1]));
        networkInfo.setNetworkOperatorName(dualSimInfo.getNetworkOperatorName(index));
        /*
        String number = telephonyManager.getLine1Number();
        if (!TextUtils.isEmpty(number)) {
            networkInfo.setPhoneNumber(number);
        }
        */
        //networkInfo.setConnectionType();

        networkInfo.setIpAddress(getLocalIpAddress());
        networkInfo.setDeviceId(dualSimInfo.getImei(index));
        //networkInfo.setDeviceSoftwareVersion(telephonyManager.getDeviceSoftwareVersion());
        networkInfo.setSubscriberId(dualSimInfo.getImsi(index));
        //networkInfo.setVoiceMailNumber(telephonyManager.getVoiceMailNumber());
        //networkInfo.setSimCountryIso(dualSimInfo.gets());
        networkInfo.setSimOperator(dualSimInfo.getSimOperatorCode(index));
        networkInfo.setSimOperatorName(dualSimInfo.getSimOperatorName(index));
        networkInfo.setSimSerialNumber(dualSimInfo.getSimSerial(index));
    }
}
