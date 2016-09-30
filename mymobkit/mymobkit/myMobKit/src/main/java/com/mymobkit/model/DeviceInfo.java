package com.mymobkit.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public final class DeviceInfo extends Response {

    @Expose
    private List<Device> devices;

    public DeviceInfo() {
        this.devices = new ArrayList<Device>(1);
    }

    public DeviceInfo(List<Device> devices) {
        this.devices = devices;
    }

    public void addDevice(final Device device) {
        if (device != null)
            this.devices.add(device);
    }

    public void setDevices(final List<Device> devices) {
        this.devices.addAll(devices);
    }

    public List<Device> getDevices() {
        return devices;
    }
}
