package com.mymobkit.data.phone;

import android.provider.ContactsContract.CommonDataKinds;

public class Phone {
	private final static String cellPhonePattern = "\\+*\\d+";

	private String contactName;
	private final String number;
	private final String cleanNumber;
	private String label;
	private final int type;
	private boolean isCellPhoneNumber;
	private boolean isDefaultNumber;

	/**
	 * 
	 * @param contactName
	 * @param number
	 */
	public Phone(String contactName, String number) {
		this.contactName = contactName;
		this.number = number;
		this.cleanNumber = cleanPhoneNumber(number);
		this.isCellPhoneNumber = true;
		this.type = CommonDataKinds.Phone.TYPE_MOBILE;
	}

	/**
	 * 
	 * @param number
	 * @param label
	 * @param type
	 * @param super_primary
	 */
	public Phone(String number, String label, int type, int super_primary) {
		this.number = number;
		this.cleanNumber = cleanPhoneNumber(number);
		this.label = label;
		this.type = type;
		isDefaultNumber = super_primary > 0;
	}

	public Boolean phoneMatch(String phone) {
		phone = cleanPhoneNumber(phone);
		if (cleanNumber.equals(phone)) {
			return true;
		}
		else if (cleanNumber.length() != phone.length()) {
			if (cleanNumber.length() > phone.length() && cleanNumber.startsWith("+")) {
				return cleanNumber.replaceFirst("\\+\\d\\d", "0").equals(phone);
			}
			else if (phone.length() > cleanNumber.length() && phone.startsWith("+")) {
				return phone.replaceFirst("\\+\\d\\d", "0").equals(cleanNumber);
			}
		}
		return false;
	}

	public static boolean isCellPhoneNumber(String number) {
		return Phone.cleanPhoneNumber(number).matches(cellPhonePattern);
	}

	public static String cleanPhoneNumber(String number) {
		return number.replace("(", "")
				.replace(")", "")
				.replace("-", "")
				.replace(".", "")
				.replace(" ", "");
	}

	public String getContactName() {
		return contactName;
	}

	public String getNumber() {
		return number;
	}

	public String getCleanNumber() {
		return cleanNumber;
	}

	public String getLabel() {
		return label;
	}

	public int getType() {
		return type;
	}

	public boolean isCellPhoneNumber() {
		return isCellPhoneNumber;
	}

	public boolean isDefaultNumber() {
		return isDefaultNumber;
	}

	public void setContactName(String name) {
		this.contactName = name;
	}
}