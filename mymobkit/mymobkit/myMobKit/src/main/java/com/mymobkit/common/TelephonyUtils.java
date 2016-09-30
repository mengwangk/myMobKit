package com.mymobkit.common;

import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.telephony.TelephonyManager;

import com.mymobkit.R;
import com.mymobkit.mms.LegacyMmsConnection;
import com.mymobkit.ui.fragment.ServiceSettingsFragment;

import static com.mymobkit.common.LogUtils.LOGW;
import static com.mymobkit.common.LogUtils.makeLogTag;

public class TelephonyUtils {

    private static final String TAG = makeLogTag(TelephonyUtils.class);

    public static TelephonyManager getManager(final Context context) {
        return (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public static String getMccMnc(final Context context) {
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final int configMcc = context.getResources().getConfiguration().mcc;
        final int configMnc = context.getResources().getConfiguration().mnc;
        if (tm.getSimState() == TelephonyManager.SIM_STATE_READY) {
            LOGW(TAG, "Choosing MCC+MNC info from TelephonyManager.getSimOperator()");
            return tm.getSimOperator();
        } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) {
            LOGW(TAG, "Choosing MCC+MNC info from TelephonyManager.getNetworkOperator()");
            return tm.getNetworkOperator();
        } else if (configMcc != 0 && configMnc != 0) {
            LOGW(TAG, "Choosing MCC+MNC info from current context's Configuration");
            return String.format("%03d%d",
                    configMcc,
                    configMnc == Configuration.MNC_ZERO ? 0 : configMnc);
        } else {
            return null;
        }
    }

    public static String getApn(final Context context) {
        try {
            final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                return cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE_MMS).getExtraInfo();
            }
            return null;
        } catch (Exception ex) {
            LOGW(TAG, "[getApn] Unable to get the APN.");
            return null;
        }
    }

    public static String getNetworkTypeName(final Context context, final int networkType) {

        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return context.getString(R.string.network_type_1xrtt);
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return context.getString(R.string.network_type_cdma);
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return context.getString(R.string.network_type_edge);
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return context.getString(R.string.network_type_ehrpd);
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return context.getString(R.string.network_type_evdo_0);
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return context.getString(R.string.network_type_evdo_A);
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return context.getString(R.string.network_type_evdo_B);
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return context.getString(R.string.network_type_gprs);
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return context.getString(R.string.network_type_hsdpa);
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return context.getString(R.string.network_type_hspa);
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return context.getString(R.string.network_type_hspap);
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return context.getString(R.string.network_type_hsupa);
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return context.getString(R.string.network_type_iden);
            case TelephonyManager.NETWORK_TYPE_LTE:
                return context.getString(R.string.network_type_lte);
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return context.getString(R.string.network_type_umts);
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return context.getString(R.string.network_type_unknown);
            default:
                return context.getString(R.string.network_type_unknown);
        }
    }

    public static LegacyMmsConnection.Apn getConfiguredApn(final Context context) {
        final String mmsc = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_APN_MMSC, context.getString(R.string.default_mmsc));
        final String mmsProxy = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_APN_MMS_PROXY, context.getString(R.string.default_mms_proxy));
        final String mmsPort = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_APN_MMS_PORT, context.getString(R.string.default_mms_port));
        final String mmsUser = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_APN_MMS_USER, "");
        final String mmsPassword = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_APN_MMS_PASSWORD, "");
        //final String mmsUserAgent = AppPreference.getInstance().getValue(ServiceSettingsFragment.SHARED_PREFS_NAME, ServiceSettingsFragment.KEY_APN_MMS_USER_AGENT,
        //        LegacyMmsConnection.USER_AGENT);

        return new LegacyMmsConnection.Apn(mmsc, mmsProxy, mmsPort, mmsUser, mmsPassword);
    }
}
