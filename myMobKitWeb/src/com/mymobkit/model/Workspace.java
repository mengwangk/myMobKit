package com.mymobkit.model;

import lombok.NoArgsConstructor;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;
import com.mymobkit.common.EntityHelper;

@Entity
@Cache
@NoArgsConstructor
public final class Workspace {

	public static Key<Workspace> key(String key){
		return Key.create(Workspace.class, key);
	}
	
	@Id String id;
	
	private String name;
	private String mode;

	@Parent
	Ref<LoginUser> owner;


	public Workspace(final LoginUser owner, final String name, final String mode) {
		this.owner = Ref.create(owner);
		this.id = EntityHelper.generateGuid();
		this.name = name;
		this.mode = mode;
	}

	public String getMode() {
		return mode;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
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
		Workspace other = (Workspace) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public LoginUser getOwner() {
		return owner.get();
	}
}
