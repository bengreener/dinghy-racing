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
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.NaturalId;

import com.bginfosys.dinghyracing.exceptions.DinghyClassMismatchException;
import com.bginfosys.dinghyracing.exceptions.EntryMaxLapsSailedException;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
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

	@Column(unique=true)
	@OneToMany(mappedBy = "entry", cascade = CascadeType.ALL, orphanRemoval=true)
	private Set<SignedUp> signedUpTo = new HashSet<SignedUp>(64);
	
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private SortedSet<Lap> laps = new ConcurrentSkipListSet<Lap>((Lap firstLap, Lap secondLap) -> Integer.compare(firstLap.getNumber(), secondLap.getNumber()));
	
	@Size(min = 3, max = 3)
	private String scoringAbbreviation;
		
	private boolean onLastLap; // record value so it is stored in database and version/ ETag is updated if it changes; helps keep clients up to date
	
	private boolean finishedRace; // record value so it is stored in database and version/ ETag is updated if it changes; helps keep clients up to date
	
	public Entry() {}
	
	public Entry(Competitor helm, Dinghy dinghy) {
		this.helm = helm;
		this.dinghy = dinghy;
	}
	
	public Entry(Competitor helm, Competitor crew, Dinghy dinghy) {
		this.helm = helm;
		this.crew = crew;
		this.dinghy = dinghy;
	}

	public Long getId() {
		return this.id;
	}
	
	public Dinghy getDinghy() {
		return dinghy;
	}

	public void setDinghy(Dinghy dinghy) {
		// get direct race
		DirectRace race = getDirectRace();
		if (race == null || race.getFleet().getDinghyClasses().isEmpty() || race.getFleet().getDinghyClasses().contains(dinghy.getDinghyClass())) {
			this.dinghy = dinghy;
		}
		else {
			throw new DinghyClassMismatchException();
		}
	}

	public Set<SignedUp> getSignedUpTo() {
		return signedUpTo;
	}

	public void setSignedUpTo(Set<SignedUp> signedUpTo) {
		if (signedUpTo.stream().allMatch(signedUp -> signedUp.getRace().getFleet().getDinghyClasses().size() == 0 || signedUp.getRace().getFleet().getDinghyClasses().contains(this.dinghy.getDinghyClass()))) {
			this.signedUpTo = signedUpTo;	
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
		this.signedUpTo.forEach(s -> s.getRace().updatePositions(s));
	}
	
	/**
	 * Return true if the boat has finished the race
	 */
	public boolean getFinishedRace() {
		return finishedRace;
	}
	
	public void setFinishedRace(boolean finishedRace) {
		this.finishedRace = finishedRace;
		if (finishedRace) {
			this.onLastLap = false;
		}
	}
	
	/**
	 * Get the number of laps sailed by this entry
	 */
	public int getLapsSailed() {
		return laps.size();
	}
	
	/**
	 * Return true of the boat is on it's last lap of the race
	 */
	public boolean getOnLastLap() {
		return this.onLastLap;
	}
	
	public void setOnLastLap(boolean onLastLap) {
		this.onLastLap = onLastLap;
		if (onLastLap) {
			this.finishedRace = false;
		}
	}
	
	@JsonIgnore
	public DirectRace getDirectRace() {
		Optional<SignedUp> optional = signedUpTo.stream().filter(s -> s.getRace() instanceof DirectRace).findFirst();
		if (optional.isPresent()) {
			SignedUp directRaceSignUp = optional.get();
			return (DirectRace) directRaceSignUp.getRace();	
		}
		return null;
	}

	public boolean addSignedUp(SignedUp signedUp) {
		return signedUpTo.add(signedUp);
	}
	
	public void updateProgressIndicators() {
		// get direct race
		DirectRace race = getDirectRace();
		if (race.completedLastLap(this)) {
			this.setFinishedRace(true); // will unset onLastLap
		}
		else if (race.onLastLap(this)) {
			this.setOnLastLap(true); // will unset finishedRace
		}
		else {
			this.finishedRace = false;
			this.onLastLap = false;
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
			this.signedUpTo.forEach(s -> s.getRace().updatePositions(s));
			updateProgressIndicators();
		}
		return result;
	}
	
	public boolean removeLap(Lap lap) {
		boolean result = laps.remove(lap); 
		if (result) {
			this.signedUpTo.forEach(s -> s.getRace().updatePositions(s));
			updateProgressIndicators();
		}
		return result;
	}
	
	public void clearLaps() {
		laps.clear();
	}
	
	// Set laps for the race based on a totals laps that provides the number of laps sailed and total time to the end of the last lap
	public void setFinalLaps(SortedSet<Lap> finalLaps) {
		if (finalLaps.size() > this.getDirectRace().getPlannedLaps()) {
			throw new EntryMaxLapsSailedException();
		}
		// to avoid 'A collection with orphan deletion was no longer referenced by the owning entity instance' remove existing laps from this.laps and add new laps
		laps.clear();
		laps.addAll(finalLaps);
		this.signedUpTo.forEach(s -> s.getRace().updatePositions(s));
		updateProgressIndicators();
	}
	
	// only the last recorded lap can be updated
	public void updateLap(Lap lap) {
		if (lap.getNumber() != laps.size()) {
			throw new IllegalArgumentException("Only the last recorded lap for an entry can be changed.");
		}
		// swapping out old and new laps was causing a referential integrity error after controller method returned :-(
		// appeared to be caused by system trying to delete the original referenced lap before updating the reference in the database to the new lap; original lap is not deleted as it is still recorded as a mapped to the entry
		laps.last().setTime(lap.getTime());
		this.signedUpTo.forEach(s -> s.getRace().updatePositions(s));
	}

	@Override
	public String toString() {
		if (dinghy.getDinghyClass().getCrewSize() > 1) {
			String crewName = "";
			if (crew != null) {
				crewName = crew.getName();
			}
			return "Entry [id=" + id + ", version=" + version + ", helm=" + helm.getName() + ", crew=" + crewName + ", dinghy=" + dinghy.getDinghyClass().getName() + " " + 
			dinghy.getSailNumber() + ", laps=" + laps + ", scoringAbbreviation=" + scoringAbbreviation
					+ ", onLastLap=" + onLastLap + ", finishedRace=" + finishedRace + "]";
		}
		else {
			return "Entry [id=" + id + ", version=" + version + ", helm=" + helm.getName() + ", dinghy=" + dinghy.getDinghyClass().getName() + " " + 
					dinghy.getSailNumber() + ", laps=" + laps + ", scoringAbbreviation=" + scoringAbbreviation
							+ ", onLastLap=" + onLastLap + ", finishedRace=" + finishedRace + "]";
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(crew, dinghy, finishedRace, helm, id, laps, onLastLap,
				scoringAbbreviation, version);
		// including signedUpTo creates a circular reference as entry is part of hash code calculation for SignedUp
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entry other = (Entry) obj;
		return Objects.equals(crew, other.crew)
				&& Objects.equals(dinghy, other.dinghy) && finishedRace == other.finishedRace
				&& Objects.equals(helm, other.helm) && Objects.equals(id, other.id) && Objects.equals(laps, other.laps)
				&& onLastLap == other.onLastLap
				&& Objects.equals(scoringAbbreviation, other.scoringAbbreviation)
				&& Objects.equals(signedUpTo, other.signedUpTo) && Objects.equals(version, other.version);
	}
}
