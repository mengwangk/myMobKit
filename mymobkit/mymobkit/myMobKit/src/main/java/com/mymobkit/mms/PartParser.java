package com.mymobkit.mms;

import android.util.Log;

import com.mymobkit.mms.utils.Utils;

import java.io.UnsupportedEncodingException;

import ws.com.google.android.mms.ContentType;
import ws.com.google.android.mms.pdu.CharacterSets;
import ws.com.google.android.mms.pdu.PduBody;
import ws.com.google.android.mms.pdu.PduPart;

public class PartParser {
    public static String getMessageText(PduBody body) {
        String bodyText = null;

        for (int i = 0; i < body.getPartsNum(); i++) {
            if (ContentType.isTextType(Utils.toIsoString(body.getPart(i).getContentType()))) {
                String partText;

                try {
                    String characterSet = CharacterSets.getMimeName(body.getPart(i).getCharset());

                    if (characterSet.equals(CharacterSets.MIMENAME_ANY_CHARSET))
                        characterSet = CharacterSets.MIMENAME_ISO_8859_1;

                    if (body.getPart(i).getData() != null) {
                        partText = new String(body.getPart(i).getData(), characterSet);
                    } else {
                        partText = "";
                    }
                } catch (UnsupportedEncodingException e) {
                    Log.w("PartParser", e);
                    partText = "Unsupported Encoding!";
                }

                bodyText = (bodyText == null) ? partText : bodyText + " " + partText;
            }
        }

        return bodyText;
    }

    public static PduBody getSupportedMediaParts(PduBody body) {
        PduBody stripped = new PduBody();

        for (int i = 0; i < body.getPartsNum(); i++) {
            if (isDisplayableMedia(body.getPart(i))) {
                stripped.addPart(body.getPart(i));
            }
        }

        return stripped;
    }

    public static int getSupportedMediaPartCount(PduBody body) {
        int partCount = 0;

        for (int i = 0; i < body.getPartsNum(); i++) {
            if (isDisplayableMedia(body.getPart(i))) {
                partCount++;
            }
        }

        return partCount;
    }

    public static boolean isImage(PduPart part) {
        return ContentType.isImageType(Utils.toIsoString(part.getContentType()));
    }

    public static boolean isAudio(PduPart part) {
        return ContentType.isAudioType(Utils.toIsoString(part.getContentType()));
    }

    public static boolean isVideo(PduPart part) {
        return ContentType.isVideoType(Utils.toIsoString(part.getContentType()));
    }

    public static boolean isText(PduPart part) {
        return ContentType.isTextType(Utils.toIsoString(part.getContentType()));
    }

    public static boolean isDisplayableMedia(PduPart part) {
        return isImage(part) || isAudio(part) || isVideo(part);
    }
}
