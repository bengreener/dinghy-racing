package com.bginfosys.dinghyracing.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Race {

	protected static final long serialVersionUID = 1L;

	@Transient
	Logger logger = LoggerFactory.getLogger(DirectRace.class);
	
	private @Id @GeneratedValue Long id;
	private @Version @JsonIgnore Long version;
	

	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
}
