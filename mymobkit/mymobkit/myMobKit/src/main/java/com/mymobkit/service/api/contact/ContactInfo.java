package com.mymobkit.service.api.contact;

import java.util.List;

import com.google.gson.annotations.Expose;

import android.graphics.Bitmap;

/**
 * A tuple class that encapsulates information about a contact.
 *
 */
public final class ContactInfo {

	@Expose
	private long personId;
	
	@Expose
	private String name;
	
	@Expose
	private String ringtone;
	
	/*private Bitmap image;
	
	@Expose
	private String photo;*/
	
	@Expose
	private List<ContactPhone> phones;
	
	@Expose
	private List<ContactEmail> emails;
	
	@Expose
	private List<ContactGroup> groups;
	
	@Expose
	private String vCard;

	/**
	 * @param personId
	 * @param name
	 * @param image
	 * @param ringtone
	 * @param vCard
	 * @param phones
	 * @param emails
	 */
	public ContactInfo(final long personId, final String name, final Bitmap image, final String ringtone, final String vCard, final List<ContactPhone> phones, final List<ContactEmail> emails, final List<ContactGroup> groups) {
		this.name = name;
		this.personId = personId;
		this.ringtone = ringtone;
		//this.image = image;
		this.phones = phones;
		this.emails = emails;
		this.groups = groups;
		this.vCard = vCard;
		
		//if (image != null) {
		//	photo = ContactManager.encodeTobase64(image);
		//}
	}

	public String getName() {
		return name;
	}

	public String getRingtone() {
		return ringtone;
	}

	public long getPersonId() {
		return personId;
	}

	/*public Bitmap getImage() {
		return image;
	}*/

	public List<ContactPhone> getPhones() {
		return phones;
	}

	public List<ContactEmail> getEmails() {
		return emails;
	}

	public String getvCard() {
		return vCard;
	}

	/*public String getPhoto() {
		return photo;
	}*/

	public List<ContactGroup> getGroups() {
		return groups;
	}

	/*public void setImage(Bitmap image) {
		this.image = image;
		if (image != null){
			photo = ContactManager.encodeTobase64(image);
		}
	}*/

	public void setGroups(List<ContactGroup> groups) {
		this.groups = groups;
	}
}
