package com.bginfosys.dinghyracing.model;

import java.time.Duration;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.NaturalId;

import com.bginfosys.dinghyracing.exceptions.DinghyClassMismatchException;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = {"helm_id", "race_id"}), 
		@UniqueConstraint(columnNames = {"dinghy_id", "race_id"}),
		@UniqueConstraint(columnNames = {"crew_id", "race_id"})
})
public class Entry {

	@Id	
	@GeneratedValue 
	private Long id;
	
	@Version
	@JsonIgnore
	private Long version;

	@NaturalId
	@NotNull
	@OneToOne
	private Competitor helm;
	
	@OneToOne
	private Competitor crew;
	
	@NaturalId
	@NotNull
	@OneToOne
	private Dinghy dinghy;
	
	@NaturalId
	@NotNull
	@ManyToOne
	private Race race;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("number")
	private SortedSet<Lap> laps = new ConcurrentSkipListSet<Lap>();
	
	@Size(min = 3, max = 3)
	private String scoringAbbreviation;
	
	public Entry() {}
	
	public Entry(Competitor helm, Dinghy dinghy, Race race) {
		if (race.getDinghyClass() == null || race.getDinghyClass() == dinghy.getDinghyClass()) {
			this.dinghy = dinghy;
			this.helm = helm;
			this.race = race;
		}
		else {
			throw new DinghyClassMismatchException();
		}
	}

	public Long getId() {
		return this.id;
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

	public Competitor getHelm() {
		return helm;
	}

	public void setHelm(Competitor helm) {
		this.helm = helm;
	}
	
	public Competitor getCrew() {
		return crew;
	}

	public void setCrew(Competitor crew) {
		this.crew = crew;
	}

	public Race getRace() {
		return race;
	}

	public void setRace(Race race) {
		if (race.getDinghyClass() == null || this.getDinghy() == null || (this.getDinghy().getDinghyClass() == race.getDinghyClass())) {
			this.race = race;
			race.signUp(this);
		}
		else {
			throw new DinghyClassMismatchException();
		}
	}

	/**
	 * Get the time taken by the entry to complete the recorded laps
	 * @return
	 */
	public Duration getSumOfLapTimes() {
		return laps.stream().map(lap -> lap.getTime()).reduce(Duration.ofMillis(0), (Duration sum, Duration lapTime) -> sum.plus(lapTime));
	}
	
	public Duration getLastLapTime() {
		return laps.size() > 0 ? laps.last().getTime() : Duration.ofMillis(0);
	}
	
	public Duration getAverageLapTime() {
		if (laps.size() == 0) {
			return Duration.ofMillis(0);
		}
		return this.getSumOfLapTimes().dividedBy(laps.size());
	}
	
	public SortedSet<Lap> getLaps() {
		return laps;
	}

	public void setLaps(SortedSet<Lap> laps) {
		this.laps = laps;
	}
	
	public String getScoringAbbreviation() {
		return scoringAbbreviation;
	}
	

	public void setScoringAbbreviation(String scoringAbbreviation) {
		this.scoringAbbreviation = scoringAbbreviation;
	}
	

	/**
	 * If boat has not finished the race add a new lap
	 */
	public boolean addLap(Lap lap) {
		if (scoringAbbreviation == "DNS" || getFinishedRace()) {
			return false;
		}
		return laps.add(lap);
	}
	
	public boolean removeLap(Lap lap) {
		return this.laps.remove(lap);
	}
	
	// only the last recorded lap can be updated
	public void updateLap(Lap lap) {
		if (lap.getNumber() != laps.size()) {
			throw new IllegalArgumentException("Only the last recorded lap for an entry can be changed.");
		}
		// swapping out old and new laps was causing a referential integrity error after controller method returned :-(
		// appeared to be caused by system trying to delete the original referenced lap before updating the reference in the database to the new lap; original lap is not deleted as it is still recorded as a mapped to the entry
		laps.last().setTime(lap.getTime());
	}
		
	/**
	 * Return true of the boat is on it's last lap of the race
	 */
	public boolean getOnLastLap() {
		if (laps.size() == race.getPlannedLaps() - 1) {
			return true;
		}
		return false;
	}
	
	/**
	 * Return true if the boat has finished the race
	 */
	public boolean getFinishedRace() {
		if (laps.size() == race.getPlannedLaps()) {
			return true;
		}
		return false;
	}
	
}
