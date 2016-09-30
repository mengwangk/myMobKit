/**
 * Copyright (C) 2015 Open Whisper Systems
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mymobkit.mms;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mymobkit.mms.transport.UndeliverableMessageException;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPostHC4;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntityHC4;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ws.com.google.android.mms.pdu.PduParser;
import ws.com.google.android.mms.pdu.SendConf;

import static com.mymobkit.common.LogUtils.LOGW;
import static com.mymobkit.common.LogUtils.makeLogTag;

@SuppressWarnings("deprecation")
public class OutgoingLegacyMmsConnection extends LegacyMmsConnection implements OutgoingMmsConnection {

    private static final String TAG = makeLogTag(OutgoingLegacyMmsConnection.class);

    private int connectCount;

    public OutgoingLegacyMmsConnection(Context context) throws ApnUnavailableException {
        super(context);
    }

    private HttpUriRequest constructRequest(byte[] pduBytes, boolean useProxy)
            throws IOException {
        try {
            HttpPostHC4 request = new HttpPostHC4(apn.getMmsc());
            for (Header header : getBaseHeaders()) {
                request.addHeader(header);
            }

            request.setEntity(new ByteArrayEntityHC4(pduBytes));
            if (useProxy) {
                HttpHost proxy = new HttpHost(apn.getProxy(), apn.getPort());
                request.setConfig(RequestConfig.custom().setProxy(proxy).build());
            }
            return request;
        } catch (IllegalArgumentException iae) {
            throw new IOException(iae);
        }
    }

    public void sendNotificationReceived(byte[] pduBytes, boolean usingMmsRadio, boolean useProxyIfAvailable)
            throws IOException {
        sendBytes(pduBytes, usingMmsRadio, useProxyIfAvailable);
    }

    @Override
    public
    @Nullable
    SendConf send(@NonNull byte[] pduBytes, int subscriptionId) throws UndeliverableMessageException {
        try {
            final MmsRadio radio = MmsRadio.getInstance(context);
            connectCount = 0;

            if (isDirectConnect()) {
                LOGW(TAG, "Sending MMS directly without radio change...");
                try {
                    return send(pduBytes, false, false);
                } catch (IOException e) {
                    LOGW(TAG, "[send] Unable to send MMS", e);
                }
            }

            LOGW(TAG, "Sending MMS with radio change and proxy...");
            radio.connect();
            connectCount++;

            // Added by Meng Wang
            ScheduledExecutorService connectScheduler = Executors.newSingleThreadScheduledExecutor();
            connectScheduler.scheduleAtFixedRate
                    (new Runnable() {
                        public void run() {
                            try {
                                radio.connect();
                                connectCount++;
                            } catch (MmsRadioException e) {
                                LOGW(TAG, "[send] Unable to send MMS", e);
                            }
                        }
                    }, 30, 30, TimeUnit.SECONDS);

            try {
                try {
                    return send(pduBytes, true, true);
                } catch (IOException e) {
                    LOGW(TAG, "[send] Unable to send MMS", e);
                }

                LOGW(TAG, "Sending MMS with radio change and without proxy...");

                try {
                    return send(pduBytes, true, false);
                } catch (IOException ioe) {
                    LOGW(TAG, "[send] Unable to send MMS", ioe);
                    throw new UndeliverableMessageException(ioe);
                }
            } finally {
                connectScheduler.shutdownNow();
                connectScheduler = null;
                for (int i = 0; i < connectCount; i++) {
                    radio.disconnect();
                }
            }

        } catch (MmsRadioException e) {
            LOGW(TAG, "[send] Unable to send MMS", e);
            throw new UndeliverableMessageException(e);
        }

    }

    private SendConf send(byte[] pduBytes, boolean useMmsRadio, boolean useProxyIfAvailable) throws IOException {
        byte[] response = sendBytes(pduBytes, useMmsRadio, useProxyIfAvailable);
        return (SendConf) new PduParser(response).parse();
    }

    private byte[] sendBytes(byte[] pduBytes, boolean useMmsRadio, boolean useProxyIfAvailable) throws IOException {
        final boolean useProxy = useProxyIfAvailable && apn.hasProxy();
        final String targetHost = useProxy
                ? apn.getProxy()
                : Uri.parse(apn.getMmsc()).getHost();

        LOGW(TAG, "Sending MMS of length: " + pduBytes.length
                + (useMmsRadio ? ", using mms radio" : "")
                + (useProxy ? ", using proxy" : ""));

        try {
            if (checkRouteToHost(context, targetHost, useMmsRadio)) {
                LOGW(TAG, "got successful route to host " + targetHost);
                byte[] response = execute(constructRequest(pduBytes, useProxy));
                if (response != null) return response;
            }
        } catch (IOException ioe) {
            LOGW(TAG, "[sendBytes] Unable to send MMS", ioe);
        }
        throw new IOException("Connection manager could not obtain route to host.");
    }


    public static boolean isConnectionPossible(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(MmsRadio.TYPE_MOBILE_MMS);
            if (networkInfo == null) {
                LOGW(TAG, "MMS network info was null, unsupported by this device");
                return false;
            }

            getApn(context);
            return true;
        } catch (ApnUnavailableException e) {
            LOGW(TAG, "[isConnectionPossible] Unable to send MMS", e);
            return false;
        }
    }
}