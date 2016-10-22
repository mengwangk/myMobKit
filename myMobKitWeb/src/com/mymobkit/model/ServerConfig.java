package com.mymobkit.model;

import org.apache.commons.lang3.StringUtils;

public final class ServerConfig {

	public enum ServerType { TURN, STUN };
	
	private ServerType serverType;
	private String config;
	private String credential;
	
	public ServerConfig(String config, ServerType serverType){
		this.serverType = serverType;
		this.setConfig(config);
		this.credential = StringUtils.EMPTY;
	}

	public ServerType getServerType() {
		return serverType;
	}

	public void setServerType(ServerType serverType) {
		this.serverType = serverType;
	}


	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}
}
