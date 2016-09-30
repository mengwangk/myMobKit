package com.mymobkit.common;

import java.util.UUID;

public final class EntityUtils {

	public static String generateGuid() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
	
	public static String generateUniqueId(){
		 return String.valueOf(Math.abs(UUID.randomUUID().getMostSignificantBits()));
	}
}
