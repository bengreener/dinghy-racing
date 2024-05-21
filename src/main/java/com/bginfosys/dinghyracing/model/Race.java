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

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.Set;
import java.util.stream.Stream;
import java.util.Comparator;
import java.util.HashSet;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = {"name", "plannedStartTime"})
})
public class Race {
	
	private @Id @GeneratedValue Long id;
	private @Version @JsonIgnore Long version;
	
	@NotNull
	private String name;
	
	@NotNull
	private LocalDateTime plannedStartTime;
		
	@ManyToOne
	private DinghyClass dinghyClass;
	
	@NotNull
	private Duration duration;
	
	@NotNull
	private Integer plannedLaps;
	
	@Column(unique=true)
	@OneToMany(mappedBy = "race", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Entry> signedUp = new HashSet<Entry>(64);
	
	@Enumerated(EnumType.STRING)
	private StartSequence startSequenceState;
	
	//Required by JPA
	//Not recommended by Spring Data
	public Race() {}
	
	public Race(String name, LocalDateTime plannedStartTime, DinghyClass dinghyClass, Duration duration, Integer plannedLaps) {
		this.name = name;
		this.plannedStartTime = plannedStartTime;
		this.dinghyClass = dinghyClass;
		this.duration = duration;
		this.plannedLaps = plannedLaps;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public LocalDateTime getPlannedStartTime() {
		return plannedStartTime;
	}

	public void setPlannedStartTime(LocalDateTime plannedStartTime) {
		this.plannedStartTime = plannedStartTime;
	}

	public DinghyClass getDinghyClass() {
		return dinghyClass;
	}

	public void setDinghyClass(DinghyClass dinghyClass) {
		this.dinghyClass = dinghyClass;
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

	public Set<Entry> getSignedUp() {
		if (signedUp == null) {
			signedUp = new HashSet<Entry>(64);
		}
		return signedUp;
	}
	
	public void setSignedUp(Set<Entry> signedUp) {
		this.signedUp = signedUp;
	}
	
	public StartSequence getStartSequenceState() {
		return startSequenceState;
	}

	public void setStartSequenceState(StartSequence startSequenceState) {
		this.startSequenceState = startSequenceState;
	}

	public void signUp(Entry entry) {
		if (signedUp == null) {
			signedUp = new HashSet<Entry>(64);
		}
		signedUp.add(entry);
	}
	
	public Integer leadEntrylapsCompleted() {
		return signedUp != null ? signedUp.stream().mapToInt(entry -> entry.getLaps().size()).max().orElse(0) : 0;
	}
	
	public Entry getLeadEntry() {
		if (signedUp != null ) {
			// get entries that have completed the same number of laps as lead boat
			Integer leadLapCount = this.leadEntrylapsCompleted(); 
			Stream<Entry> entriesOnLeadLap = signedUp.stream().filter(entry -> entry.getLaps().size() == leadLapCount);
			// return entry on lead lap with lowest sum of lap times
			return entriesOnLeadLap.min(Comparator.comparing(Entry::getSumOfLapTimes)).orElse(null);	
		}
		return null;
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
	
	public String toString() {
		return (name + ", " + plannedStartTime.toString() + ", " + dinghyClass.getName());
	}
}
