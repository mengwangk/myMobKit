package com.mymobkit.service.api.contact;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import static com.mymobkit.common.LogUtils.LOGE;
import static com.mymobkit.common.LogUtils.LOGD;
import static com.mymobkit.common.LogUtils.makeLogTag;

/**
 * Interface for accessing contacts.
 */
public final class ContactManager {

    private static final String TAG = makeLogTag(ContactManager.class);

    private Context context;

    public ContactManager(final Context context) {
        this.context = context;
    }

	/*private Bitmap loadImage(long personId) {
        Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, personId);
		InputStream inputStream;

		if (PlatformUtils.isICSOrHigher()) {
			inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri, true);
		} else {
			inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri);
		}

		if (inputStream != null) {
			return BitmapFactory.decodeStream(inputStream);
		} else {
			return null;
		}
	}*/

    public List<ContactInfo> getAllContacts() {
        List<ContactInfo> contacts = new ArrayList<ContactInfo>(1);
        Cursor contactCursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        try {
            if (contactCursor != null && contactCursor.getCount() > 0) {
                while (contactCursor.moveToNext()) {
                    contacts.add(populateContact(contactCursor));
                }
            }
        } finally {
            if (contactCursor != null)
                contactCursor.close();
        }
        return contacts;
    }

    public ContactInfo getContact(String id) {
        ContentResolver cr = context.getContentResolver();
        Cursor contactCursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts._ID + " = ?", new String[]{id}, null);
        try {
            if (contactCursor != null && contactCursor.getCount() > 0) {
                contactCursor.moveToFirst();
                ContactInfo contactInfo = populateContactDetails(contactCursor);
                return contactInfo;
            }
        } finally {
            if (contactCursor != null)
                contactCursor.close();
        }
        return null;
    }

    public List<ContactInfo> getContactByName(final String name) {
        ContentResolver cr = context.getContentResolver();
        Cursor contactCursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts.DISPLAY_NAME + " like ? COLLATE NOCASE", new String[]{"%" + name + "%"}, null);
        List<ContactInfo> contacts = new ArrayList<ContactInfo>(1);
        try {
            if (contactCursor != null && contactCursor.getCount() > 0) {
                while (contactCursor.moveToNext()) {
                    ContactInfo contactInfo = populateContactDetails(contactCursor);
                    contacts.add(contactInfo);
                }
            }
        } finally {
            if (contactCursor != null) {
                contactCursor.close();
            }
        }
        return contacts;
    }

    public List<ContactInfo> getContactByPhoneNumber(final String phoneNumber) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor phoneCursor = context.getContentResolver().query(uri, null, null, null, null);
        List<ContactInfo> contacts = new ArrayList<ContactInfo>(1);
        try {
            if (phoneCursor != null && phoneCursor.getCount() > 0) {
                while (phoneCursor.moveToNext()) {
                    String contactId = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.PhoneLookup._ID));
                    ContactInfo contactInfo = getContact(contactId);
                    if (contactInfo != null) contacts.add(contactInfo);
                }
            }
        } finally {
            phoneCursor.close();
        }
        return contacts;
    }

    public ContactInfo populateContactDetails(final Cursor contactCursor) {
        ContactInfo contactInfo = populateContact(contactCursor);

        // Get contact groups
        List<String> groupIds = getGroupIdFor(contactInfo.getPersonId());
        if (groupIds.size() > 0) {
            contactInfo.setGroups(getGroups(groupIds));
        }

        // Get contact photo
        // contactInfo.setImage(loadImage(contactInfo.getPersonId()));

        return contactInfo;
    }

    public ContactInfo populateContact(final Cursor contactCursor) {

        long personId = contactCursor.getLong(contactCursor.getColumnIndex(ContactsContract.Contacts._ID));
        String name = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        String ringtone = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.CUSTOM_RINGTONE));
        // Bitmap photo = loadImage(personId);

        // Get phone number
        List<ContactPhone> phones = new ArrayList<ContactPhone>(1);
        if (Integer.parseInt(contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

            // get the phone number
            Cursor phoneCur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{String.valueOf(personId)}, null);
            try {
                while (phoneCur.moveToNext()) {
                    String number = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    int type = phoneCur.getInt(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                    String label = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
                    String displayLabel = ContactsContract.CommonDataKinds.Phone.getTypeLabel(context.getResources(), type, label).toString();
                    ContactPhone phone = new ContactPhone(number, type, label, displayLabel);
                    phones.add(phone);
                }
            } finally {
                phoneCur.close();
            }
        }

        // get email
        List<ContactEmail> emails = new ArrayList<ContactEmail>(1);
        Cursor emailCur = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{String.valueOf(personId)}, null);
        try {
            while (emailCur.moveToNext()) {
                // This would allow you get several email addresses
                // if the email addresses were stored in an array
                String address = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                int emailType = emailCur.getInt(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                String label = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.LABEL));
                String displayLabel = ContactsContract.CommonDataKinds.Email.getTypeLabel(context.getResources(), emailType, label).toString();
                ContactEmail email = new ContactEmail(address, emailType, label, displayLabel);
                emails.add(email);
            }
        } finally {
            emailCur.close();
        }

        // get vCard
        String vCard = "";
        try {
            String lookupKey = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
            AssetFileDescriptor fd;
            fd = context.getContentResolver().openAssetFileDescriptor(uri, "r");
            FileInputStream fis = fd.createInputStream();
            byte[] buf = new byte[(int) fd.getDeclaredLength()];
            fis.read(buf);
            vCard = new String(buf);
        } catch (Exception ex) {
            LOGE(TAG, "[populateContact] Unable to read vCard", ex);
        }

        return new ContactInfo(personId, name, null, ringtone, vCard, phones, emails, null);
    }

    public List<String> getGroupIdFor(final Long contactId) {
        final Uri uri = ContactsContract.Data.CONTENT_URI;
        final String where = String.format("%s = ? AND %s = ?", ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID);
        final String[] whereParams = new String[]{ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE, Long.toString(contactId),};
        final String[] selectColumns = new String[]{ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID,};
        final Cursor groupIdCursor = context.getContentResolver().query(uri, selectColumns, where, whereParams, null);
        final List<String> groupIds = new ArrayList<String>(1);
        try {
            if (groupIdCursor != null && groupIdCursor.getCount() > 0) {
                while (groupIdCursor.moveToNext()) {
                    groupIds.add(groupIdCursor.getString(0));
                }
            }
        } finally {
            groupIdCursor.close();
        }
        return groupIds;
    }

    public List<ContactGroup> getGroups(List<String> groupIds) {
        Uri uri = ContactsContract.Groups.CONTENT_URI;
        String where = String.format("%s  in (" + makePlaceholders(groupIds.size()) + ")", ContactsContract.Groups._ID);
        String[] whereParams = groupIds.toArray(new String[groupIds.size()]);
        String[] selectColumns = {ContactsContract.Groups._ID, ContactsContract.Groups.TITLE};
        Cursor cursor = context.getContentResolver().query(uri, selectColumns, where, whereParams, null);
        List<ContactGroup> groups = new ArrayList<ContactGroup>(1);
        try {
            while (cursor.moveToNext()) {
                ContactGroup group = new ContactGroup(cursor.getString(0), cursor.getString(1));
                groups.add(group);
            }
        } finally {
            cursor.close();
        }
        return groups;
    }

    private String makePlaceholders(int len) {
        if (len < 1) {
            // It will lead to an invalid query anyway ..
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }

    public boolean deleteContact(final long personId) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        String[] args = new String[]{String.valueOf(personId)};
        ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI).withSelection(ContactsContract.RawContacts.CONTACT_ID + "=?", args).build());
        try {
            ContentProviderResult[] results = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            //LOGD(TAG, "results --" + results.length);
        } catch (Exception e) {
            LOGE(TAG, "[deleteContact] Unable to delete contact", e);
            return false;
        }
        return true;
    }

    public boolean addContact(final Contact contact) {
        if (contact == null) return false;

        final ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // Display name
        if (!TextUtils.isEmpty(contact.getDisplayName())) {
            ops.add(ContentProviderOperation.newInsert(
                    ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.getDisplayName())
                    .build());
        }

        // Mobile Number
        if (!TextUtils.isEmpty(contact.getMobileNumber())) {
            ops.add(ContentProviderOperation.
                    newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getMobileNumber())
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                    .build());
        }

        // Home Number
        if (!TextUtils.isEmpty(contact.getHomeNumber())) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getHomeNumber())
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                    .build());
        }

        // Work Number
        if (!TextUtils.isEmpty(contact.getWorkNumber())) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, contact.getWorkNumber())
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                            ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                    .build());
        }

        // Email
        if (!TextUtils.isEmpty(contact.getEmail())) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, contact.getEmail())
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                    .build());
        }

        // Organization
        if (!TextUtils.isEmpty(contact.getCompany()) && !TextUtils.isEmpty(contact.getJobTitle())) {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, contact.getCompany())
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, contact.getJobTitle())
                    .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_WORK)
                    .build());
        }

        // Asking the Contact provider to create a new contact
        try {
            ContentProviderResult[] results = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            //LOGD(TAG, "results --" + results.length);
            return true;
        } catch (Exception e) {
            LOGE(TAG, "[addContact] Unable to add contact", e);
        }
        return false;
    }
}
