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
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

import java.util.Set;
import java.util.stream.Collectors;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import jakarta.validation.constraints.NotNull;

@Entity
public class DirectRace extends Race {
		
	@NotNull
	private LocalDateTime plannedStartTime;
		
	@NotNull
	@ManyToOne
	private Fleet fleet;
	
	@NotNull
	private Duration duration;
	
	@NotNull
	private Integer plannedLaps;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private RaceType type;
	
	@ManyToMany(mappedBy = "hosts")
	private Set<EmbeddedRace> embedded = new HashSet<EmbeddedRace>(64);
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private StartType startType;
	
	//Required by JPA
	//Not recommended by Spring Data
	public DirectRace() {}
	
	public DirectRace(String name, LocalDateTime plannedStartTime, Fleet fleet, Duration duration, Integer plannedLaps, RaceType type, StartType startType) {
		super(name);
		this.plannedStartTime = plannedStartTime;
		this.fleet = fleet;
		this.duration = duration;
		this.plannedLaps = plannedLaps;
		this.type = type;
		this.startType = startType;
	}
	
	public LocalDateTime getPlannedStartTime() {
		return plannedStartTime;
	}

	public void setPlannedStartTime(LocalDateTime plannedStartTime) {
		this.plannedStartTime = plannedStartTime;
	}

	@Override
	public Fleet getFleet() {
		return fleet;
	}

	// Get the dinghy classes for dinghies signed up to the race
	public Set<DinghyClass> getDinghyClasses() {
		return getSignedUp().stream().map(entry -> entry.getDinghy().getDinghyClass()).distinct().collect(Collectors.toSet());
	}
	
	public void setFleet(Fleet fleet) {
		this.fleet = fleet;
	}
	
	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public Integer getPlannedLaps() {
		return plannedLaps;
	}

	public void setPlannedLaps(Integer plannedLaps) {
		this.plannedLaps = plannedLaps;
	}

	public RaceType getType() {
		return type;
	}

	public void setType(RaceType type) {
		this.type = type;
	}

	public Set<EmbeddedRace> getEmbedded() {
		return embedded;
	}

	public void setEmbedded(Set<EmbeddedRace> embedded) {
		this.embedded = embedded;
	}

	public StartType getStartType() {
		return startType;
	}

	public void setStartType(StartType startType) {
		this.startType = startType;
	}

	/**
	 * Return an estimate of the number of laps that will be completed given the time for the lead boats last lap, number of laps completed, and the race duration
	 *     lapForecast = (remainingTime/ lastLapTime) + lapsCompleted
	 * If the lead boat has sailed for the planned duration of the race or longer the lap forecast will be the number of laps completed by the lead boat
	 * If no laps have been completed will return the number of laps set for the race
	 * @return a double representing the estimate of the number of laps that will be completed
	 */
	public Double getLapForecast() {
		Entry leadEntry = this.getLeadEntry();
		if (leadEntry == null || leadEntry.getLaps().size() == 0) {
			return (double) this.getPlannedLaps();
		}
		Duration remainingTime = this.getDuration().minus(leadEntry.getSumOfLapTimes());
		if (remainingTime.isNegative() || remainingTime.isZero()) {
			return (double) leadEntry.getLaps().size();
		}
		Double lapsEstimate = (double) remainingTime.toSeconds() / (double)leadEntry.getLastLapTime().toSeconds();
		return leadEntry.getLaps().size() + lapsEstimate;
	}
	
	/**
	 * Calculate and set the positions of entries in the race based on the number of laps completed and the time taken to complete those laps
	 */
	@Override
	public void calculatePositions(Entry entry) {
		if (entry.getScoringAbbreviation() != null && entry.getScoringAbbreviation() != "") {
			updateEntryPosition(entry, getSignedUp().size());
		}
		else {
			if (this.type == RaceType.FLEET) {
				// if this is the lead entry need to calculate corrected time for all entries in the race
				if (entry == this.getLeadEntry()) {
					this.getSignedUp().forEach(e -> updateCorrectedTime(e));
				}
				else {
					updateCorrectedTime(entry);
				}
				List<Entry> entriesInPosition = getSignedUp().stream().sorted(new FleetEntriesComparator()).toList();
				getSignedUp().forEach(e -> e.setPosition(entriesInPosition.lastIndexOf(e) + 1));
				applyLapAdjustments();
				applyMatchingCorrectedTimeAdjustments();
			}
			else if (this.type == RaceType.PURSUIT) {
				List<Entry> entriesInPosition = getSignedUp().stream().sorted(new PursuitEntriesComparator()).toList();
				updateEntryPosition(entry, entriesInPosition.lastIndexOf(entry) + 1);
			}
		}		
	}
	
	@Override
	protected boolean isDinghyEligible(Dinghy dinghy) {
		return (dinghy == null || fleet.getDinghyClasses().isEmpty() || fleet.getDinghyClasses().contains(dinghy.getDinghyClass()));
	}
	
	@Override
	protected boolean isOnLastLap(Entry entry) {
		return (entry.getLaps().size() == plannedLaps - 1);
	}
	
	@Override
	protected boolean hasFinishedRace(Entry entry) {
		return (entry.getLaps().size() == plannedLaps);
	}
	
	@Override
	public String toString() {
		return "Race [id=" + getId() + ", name=" + getName() + ", plannedStartTime=" + plannedStartTime
				+ ", fleet=" + fleet.getName() + ", duration=" + duration + ", plannedLaps=" + plannedLaps + ", type="
				+ type + ", startType="
				+ startType + "]";
	}
	
	public class PursuitEntriesComparator implements Comparator<Entry> {
		
		@Override
		public int compare(Entry entry1, Entry entry2) {
			// from lead entry to last place entry (-1 faster, 0 same, 1 slower)
			// sort entries with scoring abbreviations to the bottom
			if ((entry1.getScoringAbbreviation() == null || entry1.getScoringAbbreviation() == "") && (entry2.getScoringAbbreviation() != null && entry2.getScoringAbbreviation() != "")) {
				return -1;
			}
			if ((entry2.getScoringAbbreviation() == null || entry2.getScoringAbbreviation() == "") && (entry1.getScoringAbbreviation() != null && entry1.getScoringAbbreviation() != "")) {
				return 1;
			}
			// more laps beats less laps
			if (entry1.getLaps().size() > entry2.getLaps().size()) {
				return -1;
			}
			if (entry1.getLaps().size() < entry2.getLaps().size()) {
				return 1;
			}
			// resolve lap ties on time to sail laps
			return entry1.getSumOfLapTimes().compareTo(entry2.getSumOfLapTimes());
		}
	}
	
	public class FleetEntriesComparator implements Comparator<Entry> {
	
		@Override
		public int compare(Entry entry1, Entry entry2) {
			// -1 faster, 0 same, 1 slower
			// sort entries with scoring abbreviations to the bottom
			// entry1 does not have scoring abbreviation && entry2 does have scoring abbreviation then entry1 faster
			if ((entry1.getScoringAbbreviation() == null || entry1.getScoringAbbreviation() == "") && (entry2.getScoringAbbreviation() != null && entry2.getScoringAbbreviation() != "")) {
				return -1;
			}
			// entry1 has scoring abbreviation && entry2 does not have scoring abbreviation then entry2 faster
			if ((entry1.getScoringAbbreviation() != null && entry1.getScoringAbbreviation() != "") && (entry2.getScoringAbbreviation() == null || entry2.getScoringAbbreviation() == "")) {
				return 1;
			}
			// check corrected time
			return entry1.getCorrectedTime().compareTo(entry2.getCorrectedTime());
		}
	}
}

