package com.mymobkit.model;

import java.io.Serializable;
import java.util.Date;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
@Cache
public final class LoginUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2764446103412258292L;

	/**
	 * Use this method to normalize email addresses for lookup
	 */
	public static String normalize(String email) {
		return email.toLowerCase();
	}

	@Id
	private String normalizedEmail;

	private boolean isLoggedIn;
	private String email;
	private String nickName;
	private Date created;
	private Date lastLogin;
	private String loginUrl;
	private String logoutUrl;
	private String id;

	public LoginUser() {

	}

	public LoginUser(final String email) {
		this.email = email;
		this.normalizedEmail = normalize(email);
		this.created = new Date();
		this.isLoggedIn = false;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isLoggedIn() {
		return isLoggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.isLoggedIn = loggedIn;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	public void loggedIn() {
		this.lastLogin = new Date();
		this.isLoggedIn = true;
	}

	public String getNormalizedEmail() {
		return normalizedEmail;
	}

	public Date getCreated() {
		return created;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public String getEmail() {
		return email;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	@Override
	public String toString() {
		return "LoginUser [normalizedEmail=" + normalizedEmail + ", isLoggedIn=" + isLoggedIn + ", email=" + email + ", nickName=" + nickName + ", created=" + created + ", lastLogin=" + lastLogin + ", loginUrl=" + loginUrl + ", logoutUrl=" + logoutUrl
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((normalizedEmail == null) ? 0 : normalizedEmail.hashCode());
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
		LoginUser other = (LoginUser) obj;
		if (normalizedEmail == null) {
			if (other.normalizedEmail != null)
				return false;
		} else if (!normalizedEmail.equals(other.normalizedEmail))
			return false;
		return true;
	}

}
