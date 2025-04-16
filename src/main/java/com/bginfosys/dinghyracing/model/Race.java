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

import java.util.ArrayList;
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
	
	@Column(unique=true)
	@OneToMany(mappedBy = "race", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("id ASC")
	private Set<Entry> signedUp = new HashSet<Entry>(64);
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private StartType startType;
	
	@Transient
	Entry lastLeadEntry; // used to check if positions need to be recalculated because leadEntry has chnaged; for eample if a lap is removed from the last lead entry 
	
	//Required by JPA
	//Not recommended by Spring Data
	public Race() {}
	
	public Race(String name, LocalDateTime plannedStartTime, Fleet fleet, Duration duration, Integer plannedLaps, RaceType type, StartType startType) {
		this.name = name;
		this.plannedStartTime = plannedStartTime;
		this.fleet = fleet;
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

	public Fleet getFleet() {
		return fleet;
	}

	// Get the dinghy classes for dinghies signed up to the race
	public Set<DinghyClass> getDinghyClasses() {
		return signedUp.stream().map(entry -> entry.getDinghy().getDinghyClass()).distinct().collect(Collectors.toSet());
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

	public Set<Entry> getSignedUp() {
		if (signedUp == null) {
			signedUp = new HashSet<Entry>(64);
		}
		return signedUp;
	}
	
	public void setSignedUp(Set<Entry> signedUp) {
		this.signedUp = signedUp;
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
				Entry leadEntry = getLeadEntry();
				if (entry == leadEntry || entry == lastLeadEntry) {
					lastLeadEntry = leadEntry; // set lastLeadEntry for reference
					signedUp.forEach(e -> updateCorrectedTime(e));
				}
				else {
					updateCorrectedTime(entry);
				}
				List<Entry> entriesInPosition = setPositions(signedUp.stream().sorted(new FleetEntriesComparator()).toList());
				entriesInPosition = adjustForAdvantage(entriesInPosition, 0);
//				applyMatchingCorrectedTimeAdjustments();
				adjustForTies(entriesInPosition, 0);
			}
			else if (this.type == RaceType.PURSUIT) {
				List<Entry> entriesInPosition = signedUp.stream().sorted(new PursuitEntriesComparator()).toList();
				updateEntryPosition(entry, entriesInPosition.lastIndexOf(entry) + 1);
			}
		}		
	}
	
	/*
	 * Entries that have adjacent positions may be tied for place and if so should both have the lowest position of all entries in the tie.
	 * Need to check that adjacent entries are not subject to a corrected time advantage from sailing fewer laps.
	 */
	private void adjustForTies(List<Entry> entries, Integer index) {
		if (index < entries.size() - 1) {
			List<Entry> ties = new ArrayList<Entry>();
			ties.add(entries.get(index));
			while (index < entries.size() - 1 // need at least 2 entries 
					&& entries.get(index).getCorrectedTime().equals(entries.get(index + 1).getCorrectedTime())
					&& !(entries.get(index).getDinghy().getDinghyClass().getPortsmouthNumber() >= entries.get(index + 1).getDinghy().getDinghyClass().getPortsmouthNumber() 
								&& (entries.get(index).getLaps().size() > entries.get(index + 1).getLaps().size())) // next entry gained a corrected time advantage from sailing less laps so not a tie
			) {
				ties.add(entries.get(index + 1));
				++index;
			}
			if (ties.size() > 1) {
				ties.sort(Comparator.comparing(Entry::getPosition));
				ties.forEach(e -> e.setPosition(ties.get(ties.size() - 1).getPosition()));
			}
			adjustForTies(entries, index + 1);
		}		
		return;
	}
	
	/*
	 * Adjusts a list of entries positioned by corrected time to correct the position of any entry that gained a corrected time advantage by sailing less laps
	 * List of entries is expected to be sorted by corrected time, fastest to slowest, and to have had positions assigned on the basis of corrected time.
	 */
	private List<Entry> adjustForAdvantage(List<Entry> entries, Integer index) {
		// No need to adjust last entry
		if (index > entries.size() - 1) {
			return entries;
		}
		Entry entry = entries.get(index);
		// check if gained advantage
		List<Entry> disadvantagedEntries = entries.stream()
			.filter(e -> entry.getPosition() < e.getPosition() 
				&& entry.getCorrectedTime().compareTo(e.getCorrectedTime()) <= 0 
				&& entry.getLapsSailed() < e.getLapsSailed() 
				&& entry.getDinghy().getDinghyClass().getPortsmouthNumber() <= e.getDinghy().getDinghyClass().getPortsmouthNumber())
			.sorted((e1, e2) -> e1.getPosition().compareTo(e2.getPosition())).toList();
		if (disadvantagedEntries.size() > 0) {
			// Split entries			
			List<Entry> start = new ArrayList<Entry>(entries.subList(0, index));// entries before current entry
			Integer indexLastDisadvantagedEntry = disadvantagedEntries.get(disadvantagedEntries.size() - 1).getPosition() - 1;
			List<Entry> middle = new ArrayList<Entry>(entries.subList(entry.getPosition(), indexLastDisadvantagedEntry + 1)); // entries after current entry to last entry that was disadvantaged from sailing more laps. (SubList is exclusive of last index position)
			List<Entry> end = new ArrayList<Entry>(entries.subList(indexLastDisadvantagedEntry + 1, entries.size())); // entries after the last disadvantaged entry
			// join entry to end and sort by corrected time
			end.add(entry);
			end = sortByCorrectedTime(end);
			
			List<Entry> adjusted = setPositions(Stream.concat(Stream.concat(start.stream(), middle.stream()), end.stream()).toList());
			return adjustForAdvantage(adjusted, index); // check for new entry at index
		}
		return adjustForAdvantage(entries, index + 1); // check for next entry
	}
	
	// sets the position of each entry in entries based on its index in entries
	private List<Entry> setPositions(List<Entry> entries) {
		entries.forEach(e -> e.setPosition(entries.lastIndexOf(e) + 1));
		return entries;
	}
	
	private List<Entry> sortByCorrectedTime(List<Entry> entries) {
		return entries.stream().sorted(new FleetEntriesComparator()).toList();	
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

