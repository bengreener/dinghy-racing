package com.bginfosys.dinghyracing.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import com.bginfosys.dinghyracing.exceptions.DinghyClassMismatchException;
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
	private Competitor competitor;
	
	@NotNull
	@OneToOne
	private Dinghy dinghy;
	
	@NotNull
	@ManyToOne
	private Race race;

	public Entry() {}
	
	public Entry(Competitor competitor, Dinghy dinghy, Race race) {
		if (race.getDinghyClass() == null || race.getDinghyClass() == dinghy.getDinghyClass()) {
			this.dinghy = dinghy;
			this.competitor = competitor;
			this.race = race;
		}
		else {
			throw new DinghyClassMismatchException();
		}
	}

	public Dinghy getDinghy() {
		return dinghy;
	}

	public void setDinghy(Dinghy dinghy) {
		if (this.getRace() == null || this.getRace().getDinghyClass() == null || (dinghy.getDinghyClass() == this.getRace().getDinghyClass())) {
			this.dinghy = dinghy;
		}
		else {
			throw new DinghyClassMismatchException();
		}
	}

	public Competitor getCompetitor() {
		return competitor;
	}

	public void setCompetitor(Competitor competitor) {
		this.competitor = competitor;
	}
	
	public Race getRace() {
		return race;
	}

	public void setRace(Race race) {
		if (race.getDinghyClass() == null || this.getDinghy() == null || (this.getDinghy().getDinghyClass() == race.getDinghyClass())) {
			this.race = race;
		}
		else {
			throw new DinghyClassMismatchException();
		}
	}
}
