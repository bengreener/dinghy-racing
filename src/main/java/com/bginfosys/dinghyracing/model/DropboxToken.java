package com.bginfosys.dinghyracing.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class DropboxToken {

	private @Id @GeneratedValue Long id;
	
	private String accessToken;
	
	private String refreshToken;
	
	private Long expiresAt;
	
	public DropboxToken() {}
	
	public DropboxToken(String accessToken, String refreshToken, Long expiresAt) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.expiresAt = expiresAt;
	}

	public Long getId() {
		return id;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public Long getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Long expiresAt) {
		this.expiresAt = expiresAt;
	}
	
}
