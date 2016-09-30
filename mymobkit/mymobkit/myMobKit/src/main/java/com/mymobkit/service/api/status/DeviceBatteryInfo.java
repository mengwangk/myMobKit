package com.mymobkit.service.api.status;

import com.google.gson.annotations.Expose;
import com.mymobkit.common.StringUtils;


public final class DeviceBatteryInfo {

	public enum PlugType {
		AC,
		USB,
		WIRELESS,
		UNKNOWN
	}
	
	public enum Health {
		COLD,
		DEAD,
		GOOD,
		OVER_VOLTAGE,
		OVERHEAT,
		UNSPECIFIED_FAILURE,
		UNKNOWN
	}
	
	public enum Status {
		CHARGING,
		DISCHARGING,
		FULL,
		NOT_CHARGING,
		UNKNOWN
	}
	
	@Expose
	private boolean isPresent;
	
	@Expose
	private String technology;
	
	@Expose
	private PlugType plugType;
	
	@Expose
	private Health health;
	
	@Expose
	private Status status;
	
	@Expose
	private int level;
	
	@Expose
	private int temperature;
	
	@Expose
	private int voltage;
	
	public DeviceBatteryInfo(){
		level = 0;
		isPresent = false;
		technology = StringUtils.EMPTY;
		plugType = PlugType.UNKNOWN;
		health = Health.UNKNOWN;
		status = Status.UNKNOWN;
		temperature = 0;
		voltage = 0;
	}


	public boolean isPresent() {
		return isPresent;
	}


	public void setPresent(boolean isPresent) {
		this.isPresent = isPresent;
	}


	public String getTechnology() {
		return technology;
	}


	public void setTechnology(String technology) {
		this.technology = technology;
	}


	public PlugType getPlugType() {
		return plugType;
	}


	public void setPlugType(PlugType plugType) {
		this.plugType = plugType;
	}


	public Health getHealth() {
		return health;
	}


	public void setHealth(Health health) {
		this.health = health;
	}


	public Status getStatus() {
		return status;
	}


	public void setStatus(Status status) {
		this.status = status;
	}


	public int getLevel() {
		return level;
	}


	public void setLevel(int level) {
		this.level = level;
	}


	public int getTemperature() {
		return temperature;
	}


	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}


	public int getVoltage() {
		return voltage;
	}


	public void setVoltage(int voltage) {
		this.voltage = voltage;
	}
	
	
	
}
