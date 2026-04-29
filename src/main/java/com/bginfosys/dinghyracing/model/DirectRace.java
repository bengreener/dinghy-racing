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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import com.bginfosys.dinghyracing.exceptions.CompetitorAlreadySignedUpException;
import com.bginfosys.dinghyracing.exceptions.DinghyAlreadySignedUpException;
import com.bginfosys.dinghyracing.exceptions.DinghyClassMismatchException;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;

@Entity
public class DirectRace extends Race {
	
	private static final long serialVersionUID = 1L;

	@NotNull
	private LocalDateTime plannedStartTime;
	
	@NotNull
	private Duration duration;
	
	@NotNull
	private Integer plannedLaps;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private RaceType type;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private StartType startType;
	
	public DirectRace() {
		super();
	};
	
	public DirectRace(String name, LocalDateTime plannedStartTime, Fleet fleet, Duration duration, Integer plannedLaps, RaceType type, StartType startType) {
		super(name, fleet);
		this.plannedStartTime = plannedStartTime;
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
		signedUp.forEach(signedUp -> signedUp.getEntry().updateProgressIndicators());
	}

	public RaceType getType() {
		return type;
	}

	public void setType(RaceType type) {
		this.type = type;
	}

	public StartType getStartType() {
		return startType;
	}

	public void setStartType(StartType startType) {
		this.startType = startType;
	}
	
	public boolean onLastLap(Entry entry) {
		return entry.getLapsSailed() == plannedLaps - 1;
	}
	
	public boolean completedLastLap(Entry entry) {
		return entry.getLapsSailed() == plannedLaps;
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
	public void updatePositions(SignedUp signedUp) {
		if (signedUp.getEntry().getScoringAbbreviation() != null && signedUp.getEntry().getScoringAbbreviation() != "") {
			updateEntryPositions(signedUp, this.signedUp.size());
		}
		else {
			if (this.type == RaceType.FLEET) {
				// if this is the lead entry need to calculate corrected time for all entries in the race
				Entry leadEntry = getLeadEntry();
				if (signedUp.getEntry() == leadEntry || signedUp.getEntry() == lastLeadEntry) {
					lastLeadEntry = leadEntry; // set lastLeadEntry for reference
					lastLeadEntryLapsCompleted = lastLeadEntry.getLapsSailed();
					this.signedUp.forEach(s -> updateCorrectedTime(s));
				}
				else {
					updateCorrectedTime(signedUp);
				}
				List<SignedUp> entriesInPosition = setPositions(this.signedUp.stream().sorted(new FleetSignedUpEntriesComparator()).toList());
				entriesInPosition = adjustForAdvantage(entriesInPosition, 0);
				adjustForTies(entriesInPosition, 0);
			}
			else if (this.type == RaceType.PURSUIT) {
				List<SignedUp> entriesInPosition = this.signedUp.stream().sorted(new PursuitSignedUpEntriesComparator()).toList();
				updateEntryPositions(signedUp, entriesInPosition.lastIndexOf(signedUp) + 1);
				updateCorrectedTime(signedUp);
			}
		}
	}
	
	/*
	 * Adjusts a list of entries positioned by corrected time to correct the position of any entry that gained a corrected time advantage by sailing less laps
	 * List of entries is expected to be sorted by corrected time, fastest to slowest, and to have had positions assigned on the basis of corrected time.
	 */
	private List<SignedUp> adjustForAdvantage(List<SignedUp> signedUp, Integer index) {
		// No need to adjust last entry
		if (index > signedUp.size() - 1) {
			return signedUp;
		}
		SignedUp signUp = signedUp.get(index);
		// check if gained advantage
		List<SignedUp> disadvantagedEntries = signedUp.stream()
			.filter(s -> signUp.getPosition() < s.getPosition() 
					&& signUp.getCorrectedTime().compareTo(s.getCorrectedTime()) <= 0
				&& signUp.getEntry().getLapsSailed() < s.getEntry().getLapsSailed() 
				&& signUp.getEntry().getDinghy().getDinghyClass().getPortsmouthNumber() <= s.getEntry().getDinghy().getDinghyClass().getPortsmouthNumber())
			.sorted((e1, e2) -> e1.getPosition().compareTo(e2.getPosition())).toList();
		if (disadvantagedEntries.size() > 0) {
			// Split entries			
			List<SignedUp> start = new ArrayList<SignedUp>(signedUp.subList(0, index));// entries before current entry
			Integer indexLastDisadvantagedEntry = disadvantagedEntries.get(disadvantagedEntries.size() - 1).getPosition() - 1;
			List<SignedUp> middle = new ArrayList<SignedUp>(signedUp.subList(signUp.getPosition(), indexLastDisadvantagedEntry + 1)); // entries after current entry to last entry that was disadvantaged from sailing more laps. (SubList is exclusive of last index position)
			List<SignedUp> end = new ArrayList<SignedUp>(signedUp.subList(indexLastDisadvantagedEntry + 1, signedUp.size())); // entries after the last disadvantaged entry
			// join entry to end and sort by corrected time
			end.add(signUp);
			end = sortByCorrectedTime(end);
			
			List<SignedUp> adjusted = setPositions(Stream.concat(Stream.concat(start.stream(), middle.stream()), end.stream()).toList());
			return adjustForAdvantage(adjusted, index); // check for new entry at index
		}
		return adjustForAdvantage(signedUp, index + 1); // check for next entry
	}
	
	/*
	 * Entries that have adjacent positions may be tied for place and if so should both have the lowest position of all entries in the tie.
	 * Need to check that adjacent entries are not subject to a corrected time advantage from sailing fewer laps.
	 */
	private void adjustForTies(List<SignedUp> signedUp, Integer index) {
		if (index < signedUp.size() - 1) {
			List<SignedUp> ties = new ArrayList<SignedUp>();
			ties.add(signedUp.get(index));
			while (index < signedUp.size() - 1 // need at least 2 entries
					&& signedUp.get(index).getCorrectedTime().equals(signedUp.get(index + 1).getCorrectedTime())
					&& !(signedUp.get(index).getEntry().getDinghy().getDinghyClass().getPortsmouthNumber() >= signedUp.get(index + 1).getEntry().getDinghy().getDinghyClass().getPortsmouthNumber() 
								&& (signedUp.get(index).getEntry().getLaps().size() > signedUp.get(index + 1).getEntry().getLaps().size())) // next entry gained a corrected time advantage from sailing less laps so not a tie
			) {
				ties.add(signedUp.get(index + 1));
				++index;
			}
			if (ties.size() > 1) {
				ties.sort(Comparator.comparing(SignedUp::getPosition));
				ties.forEach(s -> s.setPosition(ties.get(0).getPosition()));
			}
			adjustForTies(signedUp, index + 1);
		}		
		return;
	}
	
	// sets the position of each signedUp entry based on its index in entries
	private List<SignedUp> setPositions(List<SignedUp> signedUp) {
		signedUp.forEach(s -> s.setPosition(signedUp.lastIndexOf(s) + 1));
		return signedUp;
	}
	
	// Sign up dinghy and helm for race
	public SignedUp signUp(Competitor helm, Dinghy dinghy) {
		// signing_up_dinghy_class_allowed_by_race
		if (!(this.fleet.getDinghyClasses().size() == 0 || this.fleet.getDinghyClasses().contains(dinghy.getDinghyClass())  )) {
			throw new DinghyClassMismatchException();
		}
		// signingup_helm_not_entered_in_race
		if (this.signedUp.stream().anyMatch(signedUp -> signedUp.getEntry().getHelm() == helm)) {
			throw new CompetitorAlreadySignedUpException(helm);
		}
		// signingup_dinghy_not_entered_in_race
		if (this.signedUp.stream().anyMatch(signedUp -> signedUp.getEntry().getDinghy() == dinghy)) {
			throw new DinghyAlreadySignedUpException(dinghy);
		}
		Entry entry = new Entry(helm, dinghy);
		SignedUp signedUp = new SignedUp(this, entry);
		entry.addSignedUp(signedUp);
		this.signedUp.add(signedUp);
		return signedUp;
	}
	
	// Sign up dinghy, helm, and crew for race
	public SignedUp signUp(Competitor helm, Competitor crew, Dinghy dinghy) {
		// signing_up_dinghy_class_allowed_by_race
		if (!(this.fleet.getDinghyClasses().size() == 0 || this.fleet.getDinghyClasses().contains(dinghy.getDinghyClass())  )) {
			throw new DinghyClassMismatchException();
		}
		// signingup_helm_not_entered_in_race
		if (this.signedUp.stream().anyMatch(signedUp -> signedUp.getEntry().getHelm() == helm)) {
			throw new CompetitorAlreadySignedUpException(helm);
		}
		// signingup_dinghy_not_entered_in_race
		if (this.signedUp.stream().anyMatch(signedUp -> signedUp.getEntry().getDinghy() == dinghy)) {
			throw new DinghyAlreadySignedUpException(dinghy);
		}
		// signingup_mate_not_entered_in_race
		if (this.signedUp.stream().anyMatch(signedUp -> signedUp.getEntry().getCrew() == crew)) {
			throw new CompetitorAlreadySignedUpException(crew);
		}
		Entry entry = new Entry(helm, crew, dinghy);
		SignedUp signedUp = new SignedUp(this, entry);
		entry.addSignedUp(signedUp);
		this.signedUp.add(signedUp);
		return signedUp;
	}

	private List<SignedUp> sortByCorrectedTime(List<SignedUp> signedUp) {
		return signedUp.stream().sorted(new FleetSignedUpEntriesComparator()).toList();	
	}
	
	public void updateCorrectedTime(SignedUp signedUp) {
		Entry entry = signedUp.getEntry();
		// if a lap is removed may result in entry having no laps. Treat this case specifically to avoid a divide by 0 error in corrected time calculation
		if (entry.getLaps().size() < 1) {
			signedUp.setCorrectedTime(Duration.ofSeconds((long)Double.POSITIVE_INFINITY));
		}
		else {
			DirectRace race = (DirectRace) entry.getDirectRace();
			if (race.type == RaceType.FLEET) {
				signedUp.setCorrectedTime(entry.getSumOfLapTimes().multipliedBy(this.getLeadEntry().getLapsSailed() * 1000).dividedBy(entry.getDinghy().getDinghyClass().getPortsmouthNumber() * entry.getLapsSailed()));
			}
			else {
				signedUp.setCorrectedTime(entry.getSumOfLapTimes());
			}
		}
	}
	
	/** 
	 * Update the position of an entry and any other entries that have their position altered as a result.
	 */
	public void updateEntryPositions(Entry entry, Integer newPosition) {
		//get signedUp for entry
		Optional<SignedUp> optSignedUp = this.signedUp.stream().filter(signUp -> signUp.getEntry().equals(entry)).findFirst();
		SignedUp signedUp = optSignedUp.get();
		updateEntryPositions(signedUp, newPosition);
	}
	
	/** 
	 * Update the position of an entry and any other entries that have their position altered as a result.
	 */
	public void updateEntryPositions(SignedUp signedUp, Integer newPosition) {
		// if new position is outside number of boats in race then ignore
		if (newPosition > this.signedUp.size()) {
			return;
		};
		Integer oldPosition = signedUp.getPosition();
		signedUp.setPosition(newPosition);
		// TODO would this be clearer as a stream operation with filters?
		// if new position is higher than old position move down position of entries between new and old positions. Entry cannot have a position lower than number of the number of entries in the race.
		if (oldPosition == null || newPosition < oldPosition) {
			this.signedUp.forEach(signUp2 -> {
				if (!signedUp.getEntry().equals(signUp2.getEntry()) && signUp2.getPosition() != null && signUp2.getPosition() >= newPosition 
						&& (oldPosition == null || signUp2.getPosition() < oldPosition) && signUp2.getPosition() != this.signedUp.size()) {
					signUp2.setPosition(signUp2.getPosition() + 1);
				}
			});
		}
		// if new position is lower than old position move up position of entries between new and old positions
		else if (newPosition > oldPosition) {
			this.signedUp.forEach(signUp2 -> {
				if (!signedUp.getEntry().equals(signUp2.getEntry()) && signUp2.getPosition() != null && signUp2.getPosition() <= newPosition && signUp2.getPosition() > oldPosition) {
					signUp2.setPosition(signUp2.getPosition() - 1);
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

	@Override
	public int hashCode() {
		return Objects.hash(duration, fleet, id, name, plannedLaps, plannedStartTime, startType, type, version);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DirectRace other = (DirectRace) obj;
		return Objects.equals(duration, other.duration) && Objects.equals(fleet, other.fleet)
				&& Objects.equals(id, other.id) && Objects.equals(name, other.name)
				&& Objects.equals(plannedLaps, other.plannedLaps)
				&& Objects.equals(plannedStartTime, other.plannedStartTime) && startType == other.startType
				&& type == other.type && Objects.equals(version, other.version);
	}
	
	public class PursuitSignedUpEntriesComparator implements Comparator<SignedUp> {
		
		@Override
		public int compare(SignedUp signedUp1, SignedUp signedUp2) {
			// from lead entry to last place entry (-1 faster, 0 same, 1 slower)
			// sort entries with scoring abbreviations to the bottom
			if ((signedUp1.getEntry().getScoringAbbreviation() == null || signedUp1.getEntry().getScoringAbbreviation() == "") 
					&& (signedUp2.getEntry().getScoringAbbreviation() != null && signedUp2.getEntry().getScoringAbbreviation() != "")) {
				return -1;
			}
			if ((signedUp2.getEntry().getScoringAbbreviation() == null || signedUp2.getEntry().getScoringAbbreviation() == "") 
					&& (signedUp1.getEntry().getScoringAbbreviation() != null && signedUp1.getEntry().getScoringAbbreviation() != "")) {
				return 1;
			}
			// more laps beats less laps
			if (signedUp1.getEntry().getLaps().size() > signedUp2.getEntry().getLaps().size()) {
				return -1;
			}
			if (signedUp1.getEntry().getLaps().size() < signedUp2.getEntry().getLaps().size()) {
				return 1;
			}
			// resolve lap ties on time to sail laps
			return signedUp1.getEntry().getSumOfLapTimes().compareTo(signedUp2.getEntry().getSumOfLapTimes());
		}
	}
	
	public class FleetSignedUpEntriesComparator implements Comparator<SignedUp> {
		
		@Override
		public int compare(SignedUp signedUp1, SignedUp signedUp2) {
			// -1 faster, 0 same, 1 slower
			// sort entries with scoring abbreviations to the bottom
			// entry1 does not have scoring abbreviation && entry2 does have scoring abbreviation then entry1 faster
			if ((signedUp1.getEntry().getScoringAbbreviation() == null || signedUp1.getEntry().getScoringAbbreviation() == "") 
					&& (signedUp2.getEntry().getScoringAbbreviation() != null && signedUp2.getEntry().getScoringAbbreviation() != "")) {
				return -1;
			}
			// entry1 has scoring abbreviation && entry2 does not have scoring abbreviation then entry2 faster
			if ((signedUp1.getEntry().getScoringAbbreviation() != null && signedUp1.getEntry().getScoringAbbreviation() != "") 
					&& (signedUp2.getEntry().getScoringAbbreviation() == null || signedUp2.getEntry().getScoringAbbreviation() == "")) {
				return 1;
			}
			// check corrected time
			return signedUp1.getCorrectedTime().compareTo(signedUp2.getCorrectedTime());
		}
	}
}
