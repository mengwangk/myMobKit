package com.mymobkit.mms;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mymobkit.mms.transport.UndeliverableMessageException;

import java.io.IOException;

import ws.com.google.android.mms.MmsException;
import ws.com.google.android.mms.pdu.RetrieveConf;
import ws.com.google.android.mms.pdu.SendConf;

import static com.mymobkit.common.LogUtils.LOGW;
import static com.mymobkit.common.LogUtils.makeLogTag;

public class CompatMmsConnection implements OutgoingMmsConnection, IncomingMmsConnection {

    private static final String TAG = makeLogTag(CompatMmsConnection.class);

    private Context context;

    public CompatMmsConnection(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public SendConf send(@NonNull byte[] pduBytes, int subscriptionId)
            throws UndeliverableMessageException {
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            LOGW(TAG, "Sending via Lollipop API");
            return new OutgoingLollipopMmsConnection(context).send(pduBytes, subscriptionId);
        } else {
            try {
                LOGW(TAG, "Sending via legacy connection");
                return new OutgoingLegacyMmsConnection(context).send(pduBytes, subscriptionId);
            } catch (UndeliverableMessageException | ApnUnavailableException e) {
                throw new UndeliverableMessageException(e);
            }
        }
    }

    @Nullable
    @Override
    public RetrieveConf retrieve(@NonNull String contentLocation,
                                 byte[] transactionId,
                                 int subscriptionId)
            throws MmsException, MmsRadioException, ApnUnavailableException, IOException {
        /*
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            LOGW(TAG, "Receiving via Lollipop API");
            return new IncomingLollipopMmsConnection(context).retrieve(contentLocation, transactionId, subscriptionId);
        } else {
            try {
                LOGW(TAG, "Receiving via legacy connection");
                return new IncomingLegacyMmsConnection(context).retrieve(contentLocation, transactionId, subscriptionId);
            } catch (MmsRadioException | IOException | ApnUnavailableException e) {
                throw e;
            }
        }
        */
        return null;
    }
}