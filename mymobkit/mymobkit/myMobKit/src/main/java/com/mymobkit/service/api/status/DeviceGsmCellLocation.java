package com.mymobkit.service.api.status;

import com.google.gson.annotations.Expose;

public final class DeviceGsmCellLocation {

	@Expose
	private int cid;
	
	@Expose
	private int psc;
	
	@Expose
	private int lac;
	
	
	public DeviceGsmCellLocation(int cid, int psc, int lac) {
		super();
		this.cid = cid;
		this.psc = psc;
		this.lac = lac;
	}


	public int getCid() {
		return cid;
	}


	public int getPsc() {
		return psc;
	}


	public int getLac() {
		return lac;
	}
	
}
