package com.mymobkit.model;

import java.util.Date;

import lombok.NoArgsConstructor;

import com.google.gson.Gson;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;
import com.mymobkit.common.EntityHelper;
import com.mymobkit.common.WorkspaceUtils;

@Entity
@NoArgsConstructor
public final class WMessage {
	@Id
	private String id;
	private String msg;
	private String senderSessionId;
	private Date msgTimestamp;

	@Parent
	Ref<Workspace> workspace;

	
	public WMessage(final String senderSessionId, final String msg, final Workspace workspace) {
		this.id = EntityHelper.generateGuid();
		this.senderSessionId = senderSessionId;
		this.msg = msg;
		this.workspace = Ref.create(workspace);
		this.msgTimestamp = WorkspaceUtils.currentDate();
	}

	public String getId() {
		return id;
	}

	public String getMsg() {
		return msg;
	}

	public String getSenderSessionId() {
		return senderSessionId;
	}

	public Workspace getWorkspace() {
		return workspace.get();
	}

	public Date getMsgTimestamp() {
		return msgTimestamp;
	}

	
	public static MessageInfo fromJson(String msg) {
		try {
			return new Gson().fromJson(msg, MessageInfo.class);
		} catch (Exception ex) {
			return new MessageInfo();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WMessage other = (WMessage) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	
}
