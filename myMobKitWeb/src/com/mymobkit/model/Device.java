package com.mymobkit.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;


@Entity
@Cache
@NoArgsConstructor
@EqualsAndHashCode(of="deviceId")
public final class Device {
	
	@Getter
	@Setter
	@Id 
	private String deviceId;
	
	@Getter
	@Setter
	private String deviceName;
	
	@Getter
	@Setter
	private String regId;
	
	@Getter
	@Setter
	private String regVersion;
	
	@Getter
	@Setter
	@Index
	private String email;
	
	@Getter
	private Long timestamp;
	
	public Device(final String deviceId, final String deviceName, final String email, final String regId, final String regVersion) {
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.email = email;
		this.regId = regId;
		this.regVersion = regVersion;
		this.timestamp = System.currentTimeMillis();
	}
	
}
