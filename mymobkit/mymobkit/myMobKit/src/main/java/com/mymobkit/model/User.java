package com.mymobkit.model;

public final class User {

	private String name;
	private String password;

	public User(final String name, final String password) {
		this.name = name;
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public String getPassword() {
		return password;
	}

	
}
