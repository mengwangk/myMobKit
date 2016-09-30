package com.mymobkit.mms;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mymobkit.mms.transport.UndeliverableMessageException;

import ws.com.google.android.mms.pdu.SendConf;

public interface OutgoingMmsConnection {
    @Nullable
    SendConf send(@NonNull byte[] pduBytes, int subscriptionId) throws UndeliverableMessageException;
}
