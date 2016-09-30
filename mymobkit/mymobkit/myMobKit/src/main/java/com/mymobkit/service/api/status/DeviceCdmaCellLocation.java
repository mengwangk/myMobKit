package com.mymobkit.service.api.status;

import com.google.gson.annotations.Expose;

public final class DeviceCdmaCellLocation {

	@Expose
	private int baseStationId;
	
	@Expose
	private int baseStationLatitude;
	
	@Expose
	private int baseStationLongitude;
	
	@Expose
	private int networkId;
	
	@Expose
	private int systemId;
	
	
	public DeviceCdmaCellLocation(int baseStationId, int baseStationLatitude, int baseStationLongitude, int networkId, int systemId) {
		super();
		this.baseStationId = baseStationId;
		this.baseStationLatitude = baseStationLatitude;
		this.baseStationLongitude = baseStationLongitude;
		this.networkId = networkId;
		this.systemId = systemId;
	}


	public int getBaseStationId() {
		return baseStationId;
	}


	public int getBaseStationLatitude() {
		return baseStationLatitude;
	}


	public int getBaseStationLongitude() {
		return baseStationLongitude;
	}


	public int getNetworkId() {
		return networkId;
	}


	public int getSystemId() {
		return systemId;
	}
	
}
