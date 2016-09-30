package com.mymobkit.net.provider;

public interface Processor<THeader, TParam, TFile, TResult> {
	
	public TResult process(THeader header, TParam command, TFile file);

}
