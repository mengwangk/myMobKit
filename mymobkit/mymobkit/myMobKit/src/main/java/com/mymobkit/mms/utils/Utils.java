/**
 * Copyright (C) 2011 Whisper Systems
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
package com.mymobkit.mms.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import ws.com.google.android.mms.pdu.CharacterSets;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public static long copy(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[4096];
        int read;
        long total = 0;

        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
            total += read;
        }

        in.close();
        out.close();

        return total;
    }

    public static byte[] toIsoBytes(String isoString) {
        try {
            return isoString.getBytes(CharacterSets.MIMENAME_ISO_8859_1);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("ISO_8859_1 must be supported!");
        }
    }

    public static byte[] toUtf8Bytes(String utf8String) {
        try {
            return utf8String.getBytes(CharacterSets.MIMENAME_UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF_8 must be supported!");
        }
    }

    public static @NonNull
    String toIsoString(byte[] bytes) {
        try {
            return new String(bytes, CharacterSets.MIMENAME_ISO_8859_1);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("ISO_8859_1 must be supported!");
        }
    }

    public static void wait(Object lock, long timeout) {
        try {
            lock.wait(timeout);
        } catch (InterruptedException ie) {
            throw new AssertionError(ie);
        }
    }

    @SuppressLint("NewApi")
    public static boolean isDefaultSmsProvider(Context context){
        return (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) ||
                (context.getPackageName().equals(Telephony.Sms.getDefaultSmsPackage(context)));
    }
}
