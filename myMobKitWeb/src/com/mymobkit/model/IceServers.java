package com.mymobkit.model;

import java.util.ArrayList;
import java.util.List;

public final class IceServers {
	
	private List<ServerConfig> urls;

	public IceServers(){
		urls = new ArrayList<ServerConfig>(2);
	}
	
	public List<ServerConfig> getUrls() {
		// Probably need to clone the objects to prevent them from being modified
		return new ArrayList<ServerConfig>(this.urls);
	}

	public void addServerConfig(ServerConfig config){
		this.urls.add(config);
	}
}
