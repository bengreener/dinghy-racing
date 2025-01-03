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

import java.io.Serializable;
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
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = {"name", "plannedStartTime"})
})
public class Race implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Transient
	Logger logger = LoggerFactory.getLogger(Race.class);
	
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
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private RaceType type;
	
	@Column(unique=true)
	@OneToMany(mappedBy = "race", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("id ASC")
	private Set<Entry> signedUp = new HashSet<Entry>(64);
	
	@Enumerated(EnumType.STRING)
	private StartSequence startSequenceState;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private StartType startType;
	
	//Required by JPA
	//Not recommended by Spring Data
	public Race() {}
	
	public Race(String name, LocalDateTime plannedStartTime, DinghyClass dinghyClass, Duration duration, Integer plannedLaps, RaceType type, StartType startType) {
		this.name = name;
		this.plannedStartTime = plannedStartTime;
		this.dinghyClass = dinghyClass;
		this.duration = duration;
		this.plannedLaps = plannedLaps;
		this.type = type;
		this.startType = startType;
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

	public Set<DinghyClass> getDinghyClasses() {
		return signedUp.stream().map(entry -> entry.getDinghy().getDinghyClass()).distinct().collect(Collectors.toSet());
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

	public RaceType getType() {
		return type;
	}

	public void setType(RaceType type) {
		this.type = type;
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

	public StartType getStartType() {
		return startType;
	}

	public void setStartType(StartType startType) {
		this.startType = startType;
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
		if (signedUp != null && signedUp.size() > 0 ) {
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
	
	/**
	 * Calculate and set the positions of entries in the race based on the number of laps completed and the time taken to complete those laps
	 */
	public void calculatePositions(Entry entry) {
		if (entry.getScoringAbbreviation() != null && entry.getScoringAbbreviation() != "") {
			updateEntryPosition(entry, signedUp.size());
		}
		else {
			if (this.type == RaceType.FLEET) {
				// if this is the lead entry need to calculate corrected time for all entries in the race
				if (entry == this.getLeadEntry()) {
					this.signedUp.forEach(e -> updateCorrectedTime(e));
				}
				else {
					updateCorrectedTime(entry);
				}
				List<Entry> entriesInPosition = signedUp.stream().sorted(new FleetEntriesComparator()).toList();
				signedUp.forEach(e -> e.setPosition(entriesInPosition.lastIndexOf(e) + 1));
				calculatePositionPart2();
			}
			else if (this.type == RaceType.PURSUIT) {
				List<Entry> entriesInPosition = signedUp.stream().sorted(new PursuitEntriesComparator()).toList();
				updateEntryPosition(entry, entriesInPosition.lastIndexOf(entry) + 1);	
			}
		}		
	}
	
	/**
	 * If a boat ends with a corrected time greater than a boat which completed less laps but has the same Portsmouth Number, a modifying calculation should be applied
	 */
	private void calculatePositionPart2() {
		signedUp.forEach(entry -> {
			// corrected time of entries with a scoring abbreviation is irrelevant
			if (entry.getScoringAbbreviation() == null || entry.getScoringAbbreviation() == "") {
				// check to see if entry has a better corrected time than other entries that sailed more laps and have the same or a higher PN (this entry gained an advantage from sailing less laps)
				List<Entry> adjustEntries = signedUp.stream()
						.filter(e -> entry.getCorrectedTime().compareTo(e.getCorrectedTime()) < 0 
								&& entry.getLapsSailed() < e.getLapsSailed() 
								&& entry.getDinghy().getDinghyClass().getPortsmouthNumber() <= e.getDinghy().getDinghyClass().getPortsmouthNumber())
						.sorted((e1, e2) -> e1.getPosition().compareTo(e2.getPosition())).toList();
				if (adjustEntries.size() > 0) {
					// determine where to slot entry in to remaining entries (need to adjust for other entries that also had their position changed due to having an advantage from sailing less laps)
					// slot below below other adjusted entries entries with a better corrected time
					List<Entry> aePartDeux = signedUp.stream().filter(e -> e.getPosition() > adjustEntries.get(adjustEntries.size() - 1).getPosition() && e.getCorrectedTime().compareTo(entry.getCorrectedTime()) < 0 ).toList();
					Entry lastEntry;
					if (aePartDeux.size() > 0) {
						lastEntry = aePartDeux.get(aePartDeux.size() - 1);
					}
					else {
						lastEntry = adjustEntries.get(adjustEntries.size() - 1);
					}
					updateEntryPosition(entry, lastEntry.getPosition()); // slot in after last entry
				}
			}
		});
	}
	
	public void updateCorrectedTime(Entry entry) {
		// if a lap is removed may result in entry having no laps. Treat this case specifically to avoid a divide by 0 error in corrected time calculation
		if (entry.getLaps().size() < 1) {
			entry.setCorrectedTime(Duration.ofSeconds((long)Double.POSITIVE_INFINITY));
		}
		else {
			entry.setCorrectedTime(entry.getSumOfLapTimes().multipliedBy(this.getLeadEntry().getLapsSailed() * 1000).dividedBy(entry.getDinghy().getDinghyClass().getPortsmouthNumber() * entry.getLapsSailed()));	
		}
	}
	
	/** 
	 * Update the position of an entry and any other entries that have their position altered as a result
	 */
	public void updateEntryPosition(Entry entry, Integer newPosition) {
		// if new position is outside number of boats in race then ignore
		if (newPosition > signedUp.size()) {
			return;
		};
		Integer oldPosition = entry.getPosition();
		entry.setPosition(newPosition);
		// if new position is higher than old position move down position of entries between new and old positions. Entry cannot have a position lower than number of the number of entries in the race.
		if (oldPosition == null || newPosition < oldPosition) {
			signedUp.forEach(entry2 -> {
				if (!entry.equals(entry2) && entry2.getPosition() != null && entry2.getPosition() >= newPosition && (oldPosition == null || entry2.getPosition() < oldPosition) && entry2.getPosition() != signedUp.size()) {
					entry2.setPosition(entry2.getPosition() + 1);
				}
			});
		}
		// if new position is lower than old position move up position of entries between new and old positions
		else if (newPosition > oldPosition) {
			signedUp.forEach(entry2 -> {
				if (!entry.equals(entry2) && entry2.getPosition() != null && entry2.getPosition() <= newPosition && entry2.getPosition() > oldPosition) {
					entry2.setPosition(entry2.getPosition() - 1);
				}
			});
		}
	}
	
	@Override
	public String toString() {
		return "Race [id=" + id + ", version=" + version + ", name=" + name + ", plannedStartTime=" + plannedStartTime
				+ ", dinghyClass=" + dinghyClass.getName() + ", duration=" + duration + ", plannedLaps=" + plannedLaps + ", type="
				+ type + ", startSequenceState=" + startSequenceState + ", startType="
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

