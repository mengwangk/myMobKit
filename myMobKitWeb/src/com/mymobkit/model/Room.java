package com.mymobkit.model;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import com.google.appengine.api.datastore.Entity;
import com.mymobkit.common.EntityHelper;
import com.mymobkit.common.RTCUtils;

@Slf4j
public final class Room extends ModelBase {

	public static final String KIND_ROOM = "room";

	private String user1 = StringUtils.EMPTY;
	private String user2 = StringUtils.EMPTY;
	private boolean isUser1Connected = false;
	private boolean isUser2Connected = false;

	public Room(String key) {
		super(key);
	}

	public String getUser1() {
		return user1;
	}

	public void setUser1(String user1) {
		this.user1 = user1;
	}

	public String getUser2() {
		return user2;
	}

	public void setUser2(String user2) {
		this.user2 = user2;
	}

	public boolean isUser1Connected() {
		return isUser1Connected;
	}

	public void setUser1Connected(boolean isUser1Connected) {
		this.isUser1Connected = isUser1Connected;
	}

	public boolean isUser2Connected() {
		return isUser2Connected;
	}

	public void setUser2Connected(boolean isUser2Connected) {
		this.isUser2Connected = isUser2Connected;
	}

	public int getOccupancy() {
		int occupancy = 0;
		if (StringUtils.isNotBlank(user1))
			occupancy++;
		if (StringUtils.isNotBlank(user2))
			occupancy++;
		return occupancy;
	}

	public String getOtherUser(String user) {
		if (StringUtils.equalsIgnoreCase(user, user1))
			return user2;
		if (StringUtils.equalsIgnoreCase(user, user2))
			return user1;
		return StringUtils.EMPTY;
	}

	public boolean hasUser(String user) {
		return (StringUtils.isNotBlank(user) && (StringUtils.equalsIgnoreCase(user, user1) || StringUtils.equalsIgnoreCase(user, user2)));
	}

	public void addUser(String user) {
		if (StringUtils.isBlank(user1)) {
			user1 = user;
			save();
		} else if (StringUtils.isBlank(user2)) {
			user2 = user;
			save();
		} else {
			throw new RuntimeException("Room is full");
		}
		save();
	}

	public void removeUser(String user) {
		RTCUtils.deleteSavedMessages(RTCUtils.makeClientId(this, user));
		if (StringUtils.equalsIgnoreCase(user, user2)) {
			user2 = StringUtils.EMPTY;
			isUser2Connected = false;
		}

		if (StringUtils.equalsIgnoreCase(user, user1)) {
			if (StringUtils.isNotBlank(user2)) {
				user1 = user2;
				isUser1Connected = isUser2Connected;
				user2 = StringUtils.EMPTY;
				isUser2Connected = false;
			} else {
				user1 = StringUtils.EMPTY;
				isUser1Connected = false;
			}
		}

		if (getOccupancy() > 0) {
			log.info("Room is not empty. Saving..");
			save();
		} else {
			log.info("Room is empty. Deleting..");
			delete();
		}
	}

	public void setConnected(String user) {
		if (StringUtils.equalsIgnoreCase(user, user1)) {
			isUser1Connected = true;
		}

		if (StringUtils.equalsIgnoreCase(user, user2)) {
			isUser2Connected = true;
		}
		save();
	}

	public boolean isConnected(String user) {
		if (StringUtils.equalsIgnoreCase(user, user1)) {
			return isUser1Connected;
		}
		if (StringUtils.equalsIgnoreCase(user, user2)) {
			return isUser2Connected;
		}
		return false;
	}

	@Override
	public String toString() {
		return "Room [user1=" + user1 + ", user2=" + user2 + ", isUser1Connected=" + isUser1Connected + ", isUser2Connected=" + isUser2Connected + "]";
	}

	@Override
	protected String getEntityKind() {
		return KIND_ROOM;
	}

	@Override
	public void getValuesFromEntity(Entity entity) {
		this.key = entity.getKey().getName();
		this.user1 = EntityHelper.convertObjectInstance(entity.getProperty("user1"), String.class);
		this.user2 = EntityHelper.convertObjectInstance(entity.getProperty("user2"), String.class);

		this.isUser1Connected = EntityHelper.convertObjectInstance(entity.getProperty("user1_connected"), Boolean.class);
		this.isUser2Connected = EntityHelper.convertObjectInstance(entity.getProperty("user2_connected"), Boolean.class);
	}

	@Override
	protected void setEntityValues() {
		if (entity == null)
			entity = new Entity(getEntityKind(), key);
		entity.setProperty("user1", user1);
		entity.setProperty("user2", user2);
		entity.setProperty("user1_connected", isUser1Connected);
		entity.setProperty("user2_connected", isUser2Connected);
	}

}
