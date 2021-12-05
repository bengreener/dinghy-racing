package com.bginfosys.dinghyracing.dinghy;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.persistence.ManyToOne;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.bginfosys.dinghyracing.dinghyclass.DinghyClass;

@Entity
public class Dinghy {
	
	private @Id @GeneratedValue Long id;
	private @Version @JsonIgnore Long version;
	
	@NotNull
	private String sailNumber;
	
	@ManyToOne
	private DinghyClass dinghyClass;
	
	public Dinghy() {}
	
	public Dinghy(String sailNumber, DinghyClass dinghyClass) {
		this.sailNumber = sailNumber;
		this.dinghyClass = dinghyClass;
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
	
	public DinghyClass getDinghyClass() {
		return this.dinghyClass;
	}
	
	public void setDinghyClass(DinghyClass dinghyClass) {
		this.dinghyClass = dinghyClass;
	}

}
