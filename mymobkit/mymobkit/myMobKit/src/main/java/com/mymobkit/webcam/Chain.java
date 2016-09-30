package com.mymobkit.webcam;

public abstract class Chain<T, U> {

	protected int chainIdx;
	protected int currentPos;
	protected int chainCount;

	public Chain(final int chainCount) {
		this.chainIdx = 0;
		this.chainCount = chainCount;
		this.currentPos = chainCount - 1;
	}

	public abstract void assign(final U frame);

	public abstract T current();

	public abstract void release();

	public void moveToNext() {
		chainIdx = (chainIdx == (chainCount - 1)) ? 0 : (chainIdx + 1);
		currentPos = (currentPos == (chainCount - 1)) ? 0 : (currentPos + 1);
	}
}
