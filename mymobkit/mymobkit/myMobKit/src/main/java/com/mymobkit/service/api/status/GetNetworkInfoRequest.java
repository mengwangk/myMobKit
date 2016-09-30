package com.mymobkit.service.api.status;

import com.google.gson.annotations.Expose;
import com.mymobkit.enums.RequestMethod;
import com.mymobkit.service.api.WebApiResponse;

public class GetNetworkInfoRequest extends WebApiResponse {

	@Expose
	private DeviceNetworkInfo networkInfo;
	
	public GetNetworkInfoRequest() {
		super(RequestMethod.GET);
	}

	public DeviceNetworkInfo getNetworkInfo() {
		return networkInfo;
	}

	public void setNetworkInfo(DeviceNetworkInfo networkInfo) {
		this.networkInfo = networkInfo;
	}

	
}
