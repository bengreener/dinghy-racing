package com.bginfosys.dinghyracing.dinghy;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Dinghy {
	
	private @Id @GeneratedValue Long id;
	private @Version @JsonIgnore Long version;
	
	@NotNull
	private String sailNumber;
	
	public Dinghy() {}
	
	public Dinghy(String sailNumber) {
		this.sailNumber = sailNumber;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getSailNumber() {
		return this.sailNumber;
	}
	
	public void setSailNumber(String sailNumber) {
		this.sailNumber = sailNumber;
	}

}
