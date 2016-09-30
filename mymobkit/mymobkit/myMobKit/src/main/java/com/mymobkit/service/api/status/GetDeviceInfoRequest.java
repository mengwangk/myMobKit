package com.mymobkit.service.api.status;

import com.google.gson.annotations.Expose;
import com.mymobkit.enums.RequestMethod;
import com.mymobkit.service.api.WebApiResponse;

/**
 * GET request for status inquiry.
 * 
 */
public final class GetDeviceInfoRequest extends WebApiResponse {

	@Expose
	private String deviceName;
	
	@Expose
	private DeviceNetworkInfo networkInfo;
	
	@Expose
	private DeviceBatteryInfo batteryInfo;
	
	public GetDeviceInfoRequest() {
		super(RequestMethod.GET);
	}

	public DeviceNetworkInfo getNetworkInfo() {
		return networkInfo;
	}

	public void setNetworkInfo(DeviceNetworkInfo networkInfo) {
		this.networkInfo = networkInfo;
	}

	public DeviceBatteryInfo getBatteryInfo() {
		return batteryInfo;
	}

	public void setBatteryInfo(DeviceBatteryInfo batteryInfo) {
		this.batteryInfo = batteryInfo;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
}