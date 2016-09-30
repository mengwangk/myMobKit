package com.mymobkit.service.api.status;

import com.google.gson.annotations.Expose;
import com.mymobkit.enums.RequestMethod;
import com.mymobkit.service.api.WebApiResponse;

public final class GetBatteryInfoRequest extends WebApiResponse {

	@Expose
	private DeviceBatteryInfo batteryInfo;

	public GetBatteryInfoRequest() {
		super(RequestMethod.GET);
	}

	public DeviceBatteryInfo getBatteryInfo() {
		return batteryInfo;
	}

	public void setBatteryInfo(DeviceBatteryInfo batteryInfo) {
		this.batteryInfo = batteryInfo;
	}

}