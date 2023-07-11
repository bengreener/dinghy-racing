package com.bginfosys.dinghyracing.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.NaturalId;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Competitor {
	
	private @Id @GeneratedValue Long id;

	private @Version @JsonIgnore Long version;
	
	@NaturalId(mutable = true)
	@NotNull
	private String name;
	
	public Competitor() {}

	public Competitor(String name) {
		this.name = name;
	}
	
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
