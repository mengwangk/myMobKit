package com.mymobkit.service.api.parameter;

public final class Parameter {
	
	private String name;
	private String sharedPrefsName;
	private DataType dataType;
	
	public enum DataType {
		STRING,
		INTEGER,
		BOOLEAN
	}
	
	public Parameter(String name, String sharedPrefsName, DataType type) {
		super();
		this.name = name;
		this.sharedPrefsName = sharedPrefsName;
		this.dataType = type;
	}

	public String getName() {
		return name;
	}


	public String getSharedPrefsName() {
		return sharedPrefsName;
	}

	public DataType getDataType() {
		return dataType;
	}

	
}
