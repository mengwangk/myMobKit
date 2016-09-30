package com.mymobkit.model;

public enum ResponseCode {
	SUCCESS(0), 
	NOT_AUTHORIZED(1),
	DEVICE_REGISTERED(2),
	DEVICE_UNREGISTERED(3),
	DEVICE_NOT_FOUND(4),
	REGISTER_FAILURE(5),
	UNREGISTER_FAILURE(6),
	SEND_FAILURE(7),
	MESSAGE_QUEUED(8);
	
	private int code;

	ResponseCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return this.code;
	}

	public static ResponseCode get(final int code) {
		if (SUCCESS.getCode() == code) {
			return SUCCESS;
		} else if (NOT_AUTHORIZED.getCode() == code) {
			return NOT_AUTHORIZED;
		} else if (DEVICE_REGISTERED.getCode() == code) {
			return DEVICE_REGISTERED;
		} else if (DEVICE_UNREGISTERED.getCode() == code) {
			return DEVICE_UNREGISTERED;
		} else if (DEVICE_NOT_FOUND.getCode() == code) {
			return DEVICE_NOT_FOUND;
		} else if (REGISTER_FAILURE.getCode() == code) {
			return REGISTER_FAILURE;
		} else if (UNREGISTER_FAILURE.getCode() == code) {
			return UNREGISTER_FAILURE;
		} else if (SEND_FAILURE.getCode() == code) {
			return SEND_FAILURE;
		} else if (MESSAGE_QUEUED.getCode() == code) {
			return MESSAGE_QUEUED;
		} else {
			return NOT_AUTHORIZED;
		}
	}
}
