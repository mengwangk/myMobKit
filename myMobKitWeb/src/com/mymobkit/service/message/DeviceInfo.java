package com.mymobkit.service.message;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import com.google.gson.annotations.Expose;
import com.mymobkit.model.Device;


public final class DeviceInfo extends Response {

	@Getter
	@Expose
	private List<Device> devices;
	
	public DeviceInfo(){
		this.devices = new ArrayList<Device>(1);
	}
	
	public DeviceInfo(List<Device> devices) {
		this.devices = devices;
	}
	
	public void addDevice(final Device device){
		if (device != null)
			this.devices.add(device);
	}
	
	public void setDevices(final List<Device> devices) {
		this.devices.addAll(devices);
	}
}
