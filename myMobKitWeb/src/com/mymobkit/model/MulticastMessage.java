package com.mymobkit.model;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
@Cache
@NoArgsConstructor
@EqualsAndHashCode(of="id")
public class MulticastMessage {

	@Getter
	@Setter
	@Id 
	private Long id;

	@Getter
	@Setter
	private List<Device> devices;
	
	@Getter
	@Setter
	private String action;
	
	@Getter
	@Setter
	private String extraData;
}
