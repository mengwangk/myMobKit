package com.mymobkit.model;

import java.util.Date;

import lombok.NoArgsConstructor;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;
import com.mymobkit.common.EntityHelper;
import com.mymobkit.common.WorkspaceUtils;

/**
 * Workspace session.
 *
 */
@Entity
@Cache
@NoArgsConstructor
public final class WSession {

	@Id
	private String id;
	private String name;
	private boolean isConnected;
	private Date timestamp;

	@Parent
	Ref<Workspace> workspace;
	
	public WSession(final String name, final Workspace workspace){
		this(EntityHelper.generateGuid(), name, workspace);
	}

	public WSession(final String id, final String name, final Workspace workspace){
		this.id = id;
		this.name = name;
		this.workspace = Ref.create(workspace);
		this.isConnected = false;
		this.timestamp = WorkspaceUtils.currentDate();
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Workspace getWorkspace() {
		return workspace.get();
	}

	
	public Date getTimestamp() {
		return timestamp;
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
		WSession other = (WSession) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	

}
