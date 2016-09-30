package com.mymobkit.enums;

import android.media.MediaRecorder;
import android.media.MediaRecorder.OutputFormat;

public enum VideoFormat implements MyMobKitEnumAsInt {

    DEFAULT(MediaRecorder.OutputFormat.DEFAULT),
	THREE_GPP(MediaRecorder.OutputFormat.THREE_GPP), 
	MPEG_4(MediaRecorder.OutputFormat.MPEG_4),
    OUTPUT_FORMAT_RTP_AVP(7),
    OUTPUT_FORMAT_MPEG2TS(8),
	WEBM(MediaRecorder.OutputFormat.WEBM);

    private static final int DEFAULT_OUTPUT_FORMAT_RTP_AVP = 7;
    private static final int DEFAULT_OUTPUT_FORMAT_MPEG2TS = 8;
    private int hashCode;

	VideoFormat(final int hashCode) {
		this.hashCode = hashCode;
	}

	public int getHashCode() {
		return this.hashCode;
	}

	public static VideoFormat get(final int hashCode) {
		if (THREE_GPP.getHashCode() == hashCode) {
			return THREE_GPP;
		} else if (MPEG_4.getHashCode() == hashCode ) {
			return MPEG_4;
		} else if (WEBM.getHashCode() == hashCode) {
			return WEBM;
        } else if (OUTPUT_FORMAT_RTP_AVP.getHashCode() == hashCode) {
            return OUTPUT_FORMAT_RTP_AVP;
		} else if (OUTPUT_FORMAT_MPEG2TS.getHashCode() == hashCode) {
			return OUTPUT_FORMAT_MPEG2TS;
		} else {
			return DEFAULT;
		}
	}
	
	public static int getOutputMediaFormat(final VideoFormat format){
		if (format == VideoFormat.THREE_GPP) {
			return OutputFormat.THREE_GPP;
		} else if (format == VideoFormat.MPEG_4) {
			return OutputFormat.MPEG_4;
		} else  if (format == VideoFormat.WEBM) {
			return OutputFormat.WEBM;
		} else  if (format == VideoFormat.OUTPUT_FORMAT_RTP_AVP) {
			return DEFAULT_OUTPUT_FORMAT_RTP_AVP;
        } else  if (format == VideoFormat.OUTPUT_FORMAT_MPEG2TS) {
            return DEFAULT_OUTPUT_FORMAT_MPEG2TS;
		} else {
			return OutputFormat.DEFAULT;
		}
	}
	
	public static String getFileExtension(final VideoFormat format) {
		if (format == VideoFormat.THREE_GPP) {
			return ".3gp";
		} else if (format == VideoFormat.MPEG_4) {
			return ".mp4";
		} else  if (format == VideoFormat.WEBM) {
			return ".webm";
        } else  if (format == VideoFormat.OUTPUT_FORMAT_MPEG2TS) {
            return ".ts";
        } else  if (format == VideoFormat.OUTPUT_FORMAT_RTP_AVP) {
            return ".rtp";
		} else {
			return ".mp4";
		}
	}
	
	public static String getContentType(final VideoFormat format) {
		if (format == VideoFormat.THREE_GPP) {
			return "video/3gpp";
		} else if (format == VideoFormat.MPEG_4) {
			return "video/mp4";
		} else  if (format == VideoFormat.WEBM) {
            return "video/webm";
        } else  if (format == VideoFormat.OUTPUT_FORMAT_MPEG2TS) {
            return "video/mp2t";
        } else  if (format == VideoFormat.OUTPUT_FORMAT_RTP_AVP) {
            return "application/sdp";
		} else {
			return "video/mp4";
		}
	}
}
