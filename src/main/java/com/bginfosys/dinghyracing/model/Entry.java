/*
 * Copyright 2022-2024 BG Information Systems Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
   
package com.bginfosys.dinghyracing.model;

import java.time.Duration;
import java.util.Objects;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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

	@NaturalId (mutable = true)
	@NotNull
	@OneToOne
	private Competitor helm;
	
	@OneToOne
	private Competitor crew;
	
	@NaturalId (mutable = true)
	@NotNull
	@OneToOne
	private Dinghy dinghy;
	
	@NaturalId (mutable = true)
	@NotNull
	@ManyToOne
	private Race race;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private SortedSet<Lap> laps = new ConcurrentSkipListSet<Lap>((Lap firstLap, Lap secondLap) -> Integer.compare(firstLap.getNumber(), secondLap.getNumber()));
	
	@Size(min = 3, max = 3)
	private String scoringAbbreviation;
	
	private Integer position;
	
	private Duration correctedTime;
	
	public Entry() {}
	
	public Entry(Competitor helm, Dinghy dinghy, Race race) {
		if (race.getFleet().getDinghyClasses().isEmpty() || race.getFleet().getDinghyClasses().contains(dinghy.getDinghyClass())) {
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
		if (this.race == null || this.getRace().getFleet().getDinghyClasses().isEmpty() || race.getFleet().getDinghyClasses().contains(dinghy.getDinghyClass())) {
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
		if (dinghy == null || race.getFleet().getDinghyClasses().isEmpty() || race.getFleet().getDinghyClasses().contains(dinghy.getDinghyClass())) {
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
		if (scoringAbbreviation == null) {
			return "";
		}
		return scoringAbbreviation;
	}
	
	public void setScoringAbbreviation(String scoringAbbreviation) {
		this.scoringAbbreviation = scoringAbbreviation;
		this.race.calculatePositions(this);
	}
	
	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public Duration getCorrectedTime() {
		// if null return a duration of infinity to avoid null issues when performing operations on duration
		if (correctedTime == null) {
			return Duration.ofSeconds((long)Double.POSITIVE_INFINITY);
		}
		return correctedTime;
	}

	public void setCorrectedTime(Duration correctedTime) {
		// if value is equal to infinity then a lap has not been completed and no corrected time can be calculated
		if (correctedTime.getSeconds() == (long)Double.POSITIVE_INFINITY) {
			this.correctedTime = null;
		}
		else {
			this.correctedTime = correctedTime;
		}
	}

	/**
	 * If boat has not finished the race add a new lap
	 */
	public boolean addLap(Lap lap) {
		if (getFinishedRace() || (scoringAbbreviation != null && scoringAbbreviation != "")) {
			return false;
		}
		boolean result = laps.add(lap); 
		if (result) {
			this.race.calculatePositions(this);
		}
		return result;
	}
	
	public boolean removeLap(Lap lap) {
		boolean result = laps.remove(lap); 
		if (result) {
			this.race.calculatePositions(this);
		}
		return result;
	}
	
	// only the last recorded lap can be updated
	public void updateLap(Lap lap) {
		if (lap.getNumber() != laps.size()) {
			throw new IllegalArgumentException("Only the last recorded lap for an entry can be changed.");
		}
		// swapping out old and new laps was causing a referential integrity error after controller method returned :-(
		// appeared to be caused by system trying to delete the original referenced lap before updating the reference in the database to the new lap; original lap is not deleted as it is still recorded as a mapped to the entry
		laps.last().setTime(lap.getTime());
		this.race.calculatePositions(this);
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
	
	/**
	 * Get the number of laps sailed by this entry
	 */
	public int getLapsSailed() {
		return laps.size();
	}

	@Override
	public String toString() {
		if (dinghy.getDinghyClass().getCrewSize() > 1) {
			String crewName = "";
			if (crew != null) {
				crewName = crew.getName();
			}
			return "Entry [id=" + id + ", version=" + version + ", helm=" + helm.getName()+ ", crew=" + crewName + ", dinghy=" + dinghy.getDinghyClass().getName() + " " + dinghy.getSailNumber()
			+ ", race=" + race.getName() + ", position=" + position + "]";
		}
		else {
			return "Entry [id=" + id + ", version=" + version + ", helm=" + helm.getName()+ ", dinghy=" + dinghy.getDinghyClass().getName() + " " + dinghy.getSailNumber()
			+ ", race=" + race.getName() + ", position=" + position + "]";	
		}
		
	}
}
