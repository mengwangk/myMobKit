package com.mymobkit.enums;

public enum MessageType implements MyMobKitEnumAsInt {
	MESSAGE_TYPE_ALL(0),
	MESSAGE_TYPE_INBOX(1),
	MESSAGE_TYPE_SENT(2),
	MESSAGE_TYPE_DRAFT(3),
	MESSAGE_TYPE_OUTBOX(4),
	MESSAGE_TYPE_FAILED(5), // for failed outgoing messages
	MESSAGE_TYPE_QUEUED(6); // for messages to send later

	private int hashCode;

	MessageType(int hashCode) {
		this.hashCode = hashCode;
	}

	public int getHashCode() {
		return this.hashCode;
	}

	public static MessageType get(final int hashCode) {
		if (MESSAGE_TYPE_ALL.getHashCode() == hashCode) {
			return MESSAGE_TYPE_ALL;
		} else if (MESSAGE_TYPE_INBOX.getHashCode() == hashCode) {
			return MESSAGE_TYPE_INBOX;
		} else if (MESSAGE_TYPE_SENT.getHashCode() == hashCode) {
			return MESSAGE_TYPE_SENT;
		} else if (MESSAGE_TYPE_DRAFT.getHashCode() == hashCode) {
			return MESSAGE_TYPE_DRAFT;
		} else if (MESSAGE_TYPE_OUTBOX.getHashCode() == hashCode) {
			return MESSAGE_TYPE_OUTBOX;
		} else if (MESSAGE_TYPE_FAILED.getHashCode() == hashCode) {
			return MESSAGE_TYPE_FAILED;
		} else if (MESSAGE_TYPE_QUEUED.getHashCode() == hashCode) {
			return MESSAGE_TYPE_QUEUED;
		} else {
			return null;
		}
	}
}
