package com.mymobkit.service.api.sms;

import java.util.Date;

import com.google.gson.annotations.Expose;
import com.mymobkit.data.SmsHelper;
import com.mymobkit.enums.MessageType;
import com.mymobkit.receiver.SmsPendingIntentReceiver;

/**
 * This is "our" internal class for holding SMS information in memory It's
 * currently used when: - querying for SMS messages - tracking delivery and sent
 * notifications for SMS
 * 
 */
public class Sms implements Comparable<Sms> {
	@Expose
	private String message;
	@Expose
	private String number;
	@Expose
	private String sender;
	@Expose
	private String to;
	@Expose
	private String answerTo;
	@Expose
	private String receiver;
	@Expose
	private final Date date;
	@Expose
	private String serviceCenter;
	@Expose
	private MessageType messageType;
	@Expose
	private boolean read;

	private int sentIntentResult;
	private int deliveredIntentResult;
	private boolean[] sentIntents;
	private boolean[] deliveredIntents;

	@Expose
	private String id;

	@Expose
	private Integer threadID;

	/**
	 * This constructor is called when querying sms
	 * 
	 * @param phoneNumber
	 * @param message
	 * @param date
	 */
	public Sms(String phoneNumber, String message, Date date, String receiver) {
		this.setNumber(phoneNumber);
		this.setMessage(message);
		//this.setShortenedMessage(message);
		this.date = date;
		this.receiver = receiver;
	}

	/**
	 * This constructor gets called when sending an sms to put the sms in the
	 * sms map
	 * 
	 * @param phoneNumber
	 * @param toName
	 * @param message
	 * @param numParts
	 * @param answerTo
	 *            - which jid should be informed about the status
	 *            (sent/delivered) of the sms
	 */
	public Sms(String phoneNumber, String toName, String message, int numParts, String answerTo, String id) {
		this.id = id;
		this.setSentIntentResult(SmsPendingIntentReceiver.IN_PROGRESS);
		this.setDelIntentResult(SmsPendingIntentReceiver.IN_PROGRESS);

		this.sentIntents = new boolean[numParts];
		this.deliveredIntents = new boolean[numParts];
		this.setNumber(phoneNumber);
		this.setTo(toName);
		this.setMessage(message);
		this.setAnswerTo(answerTo);
		this.date = new Date();
	}

	public Sms(String id, String phoneNumber, String name, String message, String answerTo, String dIntents, String sIntents, int resSIntent, int resDIntent, long date) {
		this.id = id;
		this.number = phoneNumber;
		this.to = name;
		this.message = message;
		this.answerTo = answerTo;
		this.deliveredIntents = toBoolArray(dIntents);
		this.sentIntents = toBoolArray(sIntents);
		this.deliveredIntentResult = resDIntent;
		this.sentIntentResult = resSIntent;
		this.date = new Date(date);
	}

	private boolean[] toBoolArray(String string) {
		boolean[] res = new boolean[string.length()];
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			res[i] = (c == SmsHelper.TRUE_VALUE);
		}
		return res;
	}

	public boolean sentIntentsComplete() {
		for (boolean sentIntent : sentIntents) {
			if (!sentIntent)
				return false;
		}
		return true;
	}

	public boolean delIntentsComplete() {
		for (boolean delIntent : deliveredIntents) {
			if (!delIntent)
				return false;
		}
		return true;
	}

	@Override
	public int compareTo(Sms another) {
		return date.compareTo(another.date);
	}

	public void setDelIntentTrue(int partNumber) {
		deliveredIntents[partNumber] = true;
	}

	public void setSentIntentTrue(int partNumber) {
		sentIntents[partNumber] = true;
	}

	void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	/*void setShortenedMessage(String shortenedMessage) {
		this.shortenedMessage = shortenedMessage;
	}

	public String getShortenedMessage() {
		return shortenedMessage;
	}*/

	void setNumber(String number) {
		this.number = number;
	}

	public String getNumber() {
		return number;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getSender() {
		if (sender != null) {
			return sender;
		} else {
			return number;
		}

	}

	public String getReceiver() {
		return receiver;
	}

	void setTo(String to) {
		this.to = to;
	}

	public String getTo() {
		return to;
	}

	void setAnswerTo(String answerTo) {
		this.answerTo = answerTo;
	}

	public void setServiceCenter(String serviceCenter) {
		this.serviceCenter = serviceCenter;
	}

	public void setID(String id) {
		this.id = id;
	}

	public void setThreadID(Integer threadID) {
		this.threadID = threadID;
	}

	public String getAnswerTo() {
		return answerTo;
	}

	public void setSentIntentResult(int resSentIntent) {
		this.sentIntentResult = resSentIntent;
	}

	public int getSentIntentResult() {
		return sentIntentResult;
	}

	public void setDelIntentResult(int resDelIntent) {
		this.deliveredIntentResult = resDelIntent;
	}

	public int getDelIntentResult() {
		return deliveredIntentResult;
	}

	public Date getDate() {
		return date;
	}

	public int getNumParts() {
		return sentIntents.length;
	}

	public String getId() {
		return id;
	}

	public int getThreadId() {
		return this.threadID;
	}

	public String getServiceCenter() {
		return this.serviceCenter;
	}

	public String getDelIntents() {
		StringBuilder res = new StringBuilder(deliveredIntents.length);
		for (boolean b : deliveredIntents) {
			if (b) {
				res.append(SmsHelper.TRUE_VALUE);
			} else {
				res.append(SmsHelper.FALSE_VALUE);
			}
		}
		return new String(res);
	}

	public String getSentIntents() {
		StringBuilder res = new StringBuilder(sentIntents.length);
		for (boolean b : sentIntents) {
			if (b) {
				res.append(SmsHelper.TRUE_VALUE);
			} else {
				res.append(SmsHelper.FALSE_VALUE);
			}
		}
		return new String(res);
	}

	public Date getCreatedDate() {
		return date;
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}
	
	
}
