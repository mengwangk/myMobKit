package com.mymobkit.common;

public interface IPredicate<T> {
	boolean apply(T type);
}