package com.bginfosys.dinghyracing.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class DinghyClass {
	
	private @Id @GeneratedValue Long id;
	private @Version @JsonIgnore Long version;
	
	@NotNull
	@Column(unique=true)
	private String name;

	@NotNull
	private Integer crewSize; 
	
	//Required by JPA
	//Not recommended by Spring Data
	public DinghyClass() {}
	
	public DinghyClass(String name, Integer crewSize) {
		this.name = name;
		this.crewSize = crewSize;
	}	
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Integer getCrewSize() {
		return this.crewSize;
	}
	
	public void setCrewSize(Integer crewSize) {
		this.crewSize = crewSize;
	}
}
