package com.bginfosys.dinghyracing.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Entry {

	@Id	
	@GeneratedValue 
	private Long id;
	
	@Version
	@JsonIgnore
	private Long version;
	
	@NotNull
	@OneToOne
	private Dinghy dinghy;
	
	@NotNull
	@OneToOne
	private Competitor competitor;

	public Entry() {}
	
	public Entry(Dinghy dinghy, Competitor competitor) {
		this.dinghy = dinghy;
		this.competitor = competitor;
	}

	public Dinghy getDinghy() {
		return dinghy;
	}

	public void setDinghy(Dinghy dinghy) {
		this.dinghy = dinghy;
	}

	public Competitor getCompetitor() {
		return competitor;
	}

	public void setCompetitor(Competitor competitor) {
		this.competitor = competitor;
	}

	protected Long getId() {
		return id;
	}
	
	
	
}
