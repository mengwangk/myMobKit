package com.mymobkit.model;

public final class ExposureCompensation {

	private int min;
	private int max;
	private int current;
	
	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	public int getCurrent() {
		return current;
	}

	public ExposureCompensation(int min, int max, int current) {
		super();
		this.min = min;
		this.max = max;
		this.current = current;
	}
}
