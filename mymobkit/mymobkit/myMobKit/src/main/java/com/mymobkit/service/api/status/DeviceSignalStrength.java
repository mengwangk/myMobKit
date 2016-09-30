package com.mymobkit.service.api.status;

import com.google.gson.annotations.Expose;


public final class DeviceSignalStrength {

	public static final int EXCELLENT_LEVEL = 75;
	public static final int GOOD_LEVEL = 50;
	public static final int MODERATE_LEVEL = 25;
	public static final int WEAK_LEVEL = 0;

	public enum SignalLevel {
		EXCELLENT, GOOD, MODERATE, WEAK
	}
	
	public static SignalLevel getSignalLevel(int level) {

		if (level > EXCELLENT_LEVEL)
			return SignalLevel.EXCELLENT;
		else if (level > GOOD_LEVEL)
			return SignalLevel.GOOD;
		else if (level > MODERATE_LEVEL)
			return SignalLevel.MODERATE;
		else if (level > WEAK_LEVEL)
			return SignalLevel.WEAK;

		return SignalLevel.WEAK;
	}
	
	@Expose
	private int cdmaDbm;
	@Expose
	private int cdmaEcio;
	@Expose
	private int evdoDbm;
	@Expose
	private int evdoEcio;
	@Expose
	private int evdoSnr;
	@Expose
	private int gsmBitErrorRate;
	@Expose
	private int gsmSignalStrength;
	@Expose
	private boolean isGsm;
	
	public DeviceSignalStrength() {
		super();
	}

	public int getCdmaDbm() {
		return cdmaDbm;
	}

	public int getCdmaEcio() {
		return cdmaEcio;
	}

	public int getEvdoDbm() {
		return evdoDbm;
	}

	public int getEvdoEcio() {
		return evdoEcio;
	}

	public int getEvdoSnr() {
		return evdoSnr;
	}

	public int getGsmBitErrorRate() {
		return gsmBitErrorRate;
	}

	public int getGsmSignalStrength() {
		return gsmSignalStrength;
	}

	public boolean isGsm() {
		return isGsm;
	}

	public void setCdmaDbm(int cdmaDbm) {
		this.cdmaDbm = cdmaDbm;
	}

	public void setCdmaEcio(int cdmaEcio) {
		this.cdmaEcio = cdmaEcio;
	}

	public void setEvdoDbm(int evdoDbm) {
		this.evdoDbm = evdoDbm;
	}

	public void setEvdoEcio(int evdoEcio) {
		this.evdoEcio = evdoEcio;
	}

	public void setEvdoSnr(int evdoSnr) {
		this.evdoSnr = evdoSnr;
	}

	public void setGsmBitErrorRate(int gsmBitErrorRate) {
		this.gsmBitErrorRate = gsmBitErrorRate;
	}

	public void setGsmSignalStrength(int gsmSignalStrength) {
		this.gsmSignalStrength = gsmSignalStrength;
	}

	public void setGsm(boolean isGsm) {
		this.isGsm = isGsm;
	}
	
	
}
