package com.mymobkit.model;

import org.apache.commons.lang3.StringUtils;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Text;
import com.google.gson.Gson;
import com.mymobkit.common.EntityHelper;

public class Message extends ModelBase {

	public static final String KIND_MESSAGE = "message";
	
	private String clientId;
	private Text message;
	
	
	public Message() {
		this(StringUtils.EMPTY);
	}
	public Message(String key) {
		super(key);
	}

	public String getMessage() {
		if (message == null) return StringUtils.EMPTY;
		return message.getValue();
	}
	public void setMessage(String message) {
		this.message = new Text(message);
	}
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public static MessageInfo fromJson(String msg) {
		try {
			return new Gson().fromJson(msg, MessageInfo.class);
		} catch (Exception ex) {
			return new MessageInfo();
		}
	}

	@Override
	protected String getEntityKind() {
		return KIND_MESSAGE;
	}

	@Override
	public void getValuesFromEntity(Entity entity) {
		this.key = entity.getKey().getName();
		setClientId(EntityHelper.convertObjectInstance(entity.getProperty("client_id"), String.class));
		setMessage(EntityHelper.convertObjectInstance(entity.getProperty("message"), String.class));
	}

	@Override
	protected void setEntityValues() {
		if (entity == null) entity = new Entity(getEntityKind(), key);
		entity.setProperty("client_id", clientId);
		entity.setProperty("message", message);
	}

}
