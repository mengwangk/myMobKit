package com.mymobkit.model;

public class Device {

    private String deviceId;

    private String deviceName;

    private String regId;

    private String regVersion;

    private String email;

    private Long timestamp;

    public Device(){

    }
    public Device(final String deviceId, final String deviceName, final String email, final String regId, final String regVersion) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.email = email;
        this.regId = regId;
        this.regVersion = regVersion;
        this.timestamp = System.currentTimeMillis();
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getRegId() {
        return regId;
    }

    public String getRegVersion() {
        return regVersion;
    }

    public String getEmail() {
        return email;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}