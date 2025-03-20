package com.bginfosys.dinghyracing.model;

import java.time.Duration;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Race {

	protected static final long serialVersionUID = 1L;

	@Transient
	Logger logger = LoggerFactory.getLogger(DirectRace.class);
	
	private @Id @GeneratedValue Long id;
	private @Version @JsonIgnore Long version;

	@NotNull
	private String name;
	
	@Transient
	private Fleet fleet = new Fleet();
	
	@Column(unique=true)
	@OneToMany(mappedBy = "race", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("id ASC")
	private Set<Entry> signedUp = new HashSet<Entry>(64);
	
	public Race() {}
	
	public Race(String name) {
		this.name = name;
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

	public Fleet getFleet() {
		return fleet;
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

	public void signUp(Entry entry) {
		if (signedUp == null) {
			signedUp = new HashSet<Entry>(64);
		}
		signedUp.add(entry);
	}
	
	public Integer leadEntrylapsCompleted() {
		return getSignedUp() != null ? getSignedUp().stream().mapToInt(entry -> entry.getLaps().size()).max().orElse(0) : 0;
	}
	
	public Entry getLeadEntry() {
		if (getSignedUp() != null && getSignedUp().size() > 0 ) {
			// get entries that have completed the same number of laps as lead boat
			Integer leadLapCount = this.leadEntrylapsCompleted(); 
			Stream<Entry> entriesOnLeadLap = getSignedUp().stream().filter(entry -> entry.getLaps().size() == leadLapCount);
			// return entry on lead lap with lowest sum of lap times
			return entriesOnLeadLap.min(Comparator.comparing(Entry::getSumOfLapTimes)).orElse(null);	
		}
		return null;
	}
	
	/**
	 * Calculate and set the positions of entries in the race based on the number of laps completed and the time taken to complete those laps
	 */
	public void calculatePositions(Entry entry) {
		if (entry.getScoringAbbreviation() != null && entry.getScoringAbbreviation() != "") {
			updateEntryPosition(entry, signedUp.size());
		}
		else {
			// if this is the lead entry need to calculate corrected time for all entries in the race
			if (entry == this.getLeadEntry()) {
				this.signedUp.forEach(e -> updateCorrectedTime(e));
			}
			else {
				updateCorrectedTime(entry);
			}
			List<Entry> entriesInPosition = signedUp.stream().sorted(new EntriesComparator()).toList();
			signedUp.forEach(e -> e.setPosition(entriesInPosition.lastIndexOf(e) + 1));
			applyLapAdjustments();
			applyMatchingCorrectedTimeAdjustments();
		}		
	}
	
	/**
	 * If a boat ends with a corrected time greater than a boat which completed less laps but has the same Portsmouth Number, a modifying calculation should be applied
	 */
	protected void applyLapAdjustments() {
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
	
	/*
	 * If more than one entry has the same corrected time all entries with that corrected time should be assigned the lowest position of any entry with that corrected time
	 */
	protected void applyMatchingCorrectedTimeAdjustments() {
		// identify any duplications of corrected time and set position accordingly
		Map<Duration, List<Entry>> duplicatedCorrectedTime = signedUp.stream().filter(entry -> entry.getScoringAbbreviation() == null || entry.getScoringAbbreviation() == "")
				.collect(Collectors.groupingBy(Entry::getCorrectedTime));
		duplicatedCorrectedTime.forEach((key, value) -> {
			if (value.size() > 1) {
				Integer maxPosition = value.stream().max(Comparator.comparing(Entry::getPosition)).get().getPosition();
				value.forEach(entry -> entry.setPosition(maxPosition));
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
	
	protected boolean isDinghyEligible(Dinghy dinghy) {
		return true;
	}
	
	protected boolean isOnLastLap(Entry entry) {
		return false;
	}
	
	protected boolean hasFinishedRace(Entry entry) {
		return false;
	}
	
	public class EntriesComparator implements Comparator<Entry> {
	
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
