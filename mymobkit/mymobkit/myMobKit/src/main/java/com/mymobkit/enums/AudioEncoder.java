package com.mymobkit.enums;

import android.media.MediaRecorder;

public enum AudioEncoder implements MyMobKitEnumAsInt {

    DEFAULT(MediaRecorder.AudioEncoder.DEFAULT),
    AMR_NB(MediaRecorder.AudioEncoder.AMR_NB),
    AMR_WB(MediaRecorder.AudioEncoder.AMR_WB),
    AAC(MediaRecorder.AudioEncoder.AAC),
    HE_AAC(MediaRecorder.AudioEncoder.HE_AAC),
    AAC_ELD(MediaRecorder.AudioEncoder.AAC_ELD),
    VORBIS(MediaRecorder.AudioEncoder.VORBIS);

    private int hashCode;

    AudioEncoder(int hashCode) {
        this.hashCode = hashCode;
    }

    public int getHashCode() {
        return this.hashCode;
    }

    public static AudioEncoder get(final int hashCode) {
        if (AMR_NB.getHashCode() == hashCode) {
            return AMR_NB;
        } else if (AMR_WB.getHashCode() == hashCode) {
            return AMR_WB;
        } else if (AAC.getHashCode() == hashCode) {
            return AAC;
        } else if (HE_AAC.getHashCode() == hashCode) {
            return HE_AAC;
        } else if (AAC_ELD.getHashCode() == hashCode) {
            return AAC_ELD;
        } else if (VORBIS.getHashCode() == hashCode) {
            return VORBIS;
        } else {
            return DEFAULT;
        }
    }
}
