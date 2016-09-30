package com.mymobkit.common;

import android.media.AudioManager;

/**
 * Audio manager.
 */
@SuppressWarnings("deprecation")
public class AudioUtils {

    private AudioManager audioManager;

    private int[] audioStreams = new int[]{
            AudioManager.STREAM_ALARM,
            AudioManager.STREAM_DTMF,
            AudioManager.STREAM_MUSIC,
            AudioManager.STREAM_NOTIFICATION,
            AudioManager.STREAM_RING,
            AudioManager.STREAM_SYSTEM,
            AudioManager.STREAM_VOICE_CALL};
    private int[] audioStreamVolumes;

    private int[] vibrateTypes = new int[]{
            AudioManager.VIBRATE_TYPE_RINGER,
            AudioManager.VIBRATE_TYPE_NOTIFICATION
    };
    private int[] vibrateSettings;


    public AudioUtils(AudioManager audioManager) {
        this.audioManager = audioManager;
    }

    public void storeAudioStreamSettings() {
        int numStreams = audioStreams.length;
        audioStreamVolumes = new int[numStreams];
        for (int r = 0; r < numStreams; r++) {
            audioStreamVolumes[r] = audioManager.getStreamVolume(audioStreams[r]);
        }

        int numVibrateTypes = vibrateTypes.length;
        vibrateSettings = new int[numVibrateTypes];
        for (int r = 0; r < numVibrateTypes; r++) {
            vibrateSettings[r] = audioManager.getVibrateSetting(vibrateTypes[r]);
        }
    }

    public void muteAll() {
        //int numVibrateTypes  = vibrateTypes.length;
        for (int vibrateType : vibrateTypes) {
            audioManager.setVibrateSetting(vibrateType, AudioManager.VIBRATE_SETTING_OFF);
        }

        //int numStreams = audioStreams.length;
        for (int audioStream : audioStreams) {
            audioManager.setStreamVolume(audioStream, 1, 0);

            // TODO - commented. On Samsung Galaxy Mini it will play a vibrate sound when turned off
            //audioManager.adjustStreamVolume(audioStream, AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }

        for (int stream : audioStreams) {
            audioManager.setStreamMute(stream, true);
        }
    }

    public void unMuteAll() {
        int numStreams = audioStreams.length;
        for (int r = 0; r < numStreams; r++) {
            audioManager.setStreamVolume(audioStreams[r], audioStreamVolumes[r], 0);
        }
        int numVibrateTypes = vibrateTypes.length;
        for (int r = 0; r < numVibrateTypes; r++) {
            audioManager.setVibrateSetting(vibrateTypes[r], vibrateSettings[r]);

        }
        for (int stream : audioStreams) {
            audioManager.setStreamMute(stream, false);
        }
    }

    //final static int VIBRATE_TYPE_RINGER = AudioManager.VIBRATE_TYPE_RINGER;
    //final static int VIBRATE_TYPE_NOTIFICATION = AudioManager.VIBRATE_TYPE_NOTIFICATION;

    //final static int VIBRATE_SETTING_ON = AudioManager.VIBRATE_SETTING_ON;
    //final static int VIBRATE_SETTING_OFF = AudioManager.VIBRATE_SETTING_OFF;
    //final static int VIBRATE_SETTING_ONLY_SILENT = AudioManager.VIBRATE_SETTING_ONLY_SILENT;


    /*
    public static int getVibrateSetting(AudioManager am, int vibrateType) {
        return am.getVibrateSetting(vibrateType);
    }

    public static void setVibrateSetting(AudioManager am, int vibrateType, int vibrateSetting) {
        am.setVibrateSetting(vibrateType, vibrateSetting);
    }
    */
}