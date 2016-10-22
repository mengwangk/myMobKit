package com.mymobkit.common;

import java.util.UUID;

public final class EntityHelper {

	/**
	 * Generate unique GUID.
	 * 
	 * @return Generated GUID.
	 */
	public static String generateGuid() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
	
	public static <T> T convertObjectInstance(Object o, Class<T> clazz) {
	    try {
	        return clazz.cast(o);
	    } catch(ClassCastException e) {
	        return null;
	    }
	}
	
	public static <T> T convertObjectInstance(Object o, Class<T> clazz, T defaultValue) {
	    try {
	        return clazz.cast(o);
	    } catch(ClassCastException e) {
	        return defaultValue;
	    }
	}
}
