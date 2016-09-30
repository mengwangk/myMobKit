package com.mymobkit.model;

import java.util.List;

import android.hardware.Camera.Size;
import android.media.CamcorderProfile;

import com.mymobkit.common.DeviceUtils;

public final class DefaultSettings {

    public VideoSettings videoSettings;

    public CameraSettings cameraSettings;

    public DefaultSettings() {
        cameraSettings = new CameraSettings();
        videoSettings = new VideoSettings();
        videoSettings.profile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
        if (videoSettings.profile == null) {
            videoSettings.profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        }
    }

    public void configure() {
        configureSettings();
    }

    private void configureSettings() {
        if (cameraSettings.resolutions == null ||
                cameraSettings.resolutions.size() == 0 ||
                cameraSettings.previewResolution == null ||
                videoSettings.videoQuality == null ||
                (videoSettings.supportedVideoSizes == null || videoSettings.supportedVideoSizes.size() == 0))
        {
            DeviceUtils.configureSupportedSizes(this);
            DeviceUtils.configureVideoQuality(this);
        }
    }

    public final class VideoSettings {

        public CamcorderProfile profile;
        public Size previewSize;
        public String previewVideoQuality;
        public List<Size> supportedVideoSizes;
        public List<String> videoQuality;
        public List<String> videoQualityDesc;
        public List<int[]> supportedFpsRange;
        public int[] previewFpsRange = new int[2];

    }

    public final class CameraSettings {
        public List<String> resolutions;
        public String previewResolution;
    }
}
