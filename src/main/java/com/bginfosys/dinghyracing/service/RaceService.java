package com.bginfosys.dinghyracing.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bginfosys.dinghyracing.exceptions.EntryLapsSailedException;
import com.bginfosys.dinghyracing.exceptions.RaceNotFoundException;
import com.bginfosys.dinghyracing.model.DirectRace;
import com.bginfosys.dinghyracing.model.Entry;
import com.bginfosys.dinghyracing.model.Race;
import com.bginfosys.dinghyracing.model.SignedUp;
import com.bginfosys.dinghyracing.persistence.RaceRepository;
import com.bginfosys.dinghyracing.web.websocket.RaceEventHandler;

@Service
public class RaceService {

	private final RaceRepository raceRepository;
	
	private final RaceEventHandler raceEventHandler;
	
	public RaceService(RaceRepository raceRepository, RaceEventHandler raceEventHandler) {
		this.raceRepository = raceRepository;
		this.raceEventHandler = raceEventHandler;
	}
	
	@Transactional
	public Race updateCourse(Long raceId, Integer plannedLaps) {
		Optional<Race> optRace = raceRepository.findById(raceId);
		if (optRace.isEmpty()) {
			throw new RaceNotFoundException(raceId);
		}
		DirectRace race = (DirectRace) optRace.get();
		Optional<Entry> optLeadEntry = race.getLeadEntry();
		if (optLeadEntry.isPresent() && optLeadEntry.get().getLapsSailed() > plannedLaps) {
			throw new EntryLapsSailedException("an entry has already sailed " + optLeadEntry.get().getLapsSailed() + " laps");
		}
		race.setPlannedLaps(plannedLaps);
		updateProgressIndicators(race);
		raceEventHandler.updateRace(race);
		return race;
	}
	
	@Transactional
	public Race updateProgressIndicators(DirectRace race) {
		Set<Entry> entriesSet = race.getSignedUp().stream().map(SignedUp::getEntry).filter(e -> e.getScoringAbbreviation() == "").collect(Collectors.toSet()); // hopefully more effecient to create this set and convert to stream as required than to keep filtering entries from Set<SignedUp> 
		if(race.getGrandPrixFinish()) {
			// get the fastest entry that has sailed the course, if any
			Optional<Entry> optFirstFinisher = entriesSet.stream().filter(e -> e.getLapsSailed() == race.getPlannedLaps()).sorted((e1, e2) -> e1.getSumOfLapTimes().compareTo(e2.getSumOfLapTimes())).findFirst();
			if (optFirstFinisher.isPresent()) {
				// get all entries that have finished a lap after the winning entry finished
				Set<Entry> finishedEntries = entriesSet.stream().filter(e -> e.getSumOfLapTimes().compareTo(optFirstFinisher.get().getSumOfLapTimes()) >= 0).collect(Collectors.toSet());
				Set<Entry> unfinishedEntries = new HashSet<Entry>(entriesSet);
				unfinishedEntries.removeAll(finishedEntries);
				finishedEntries.forEach(e -> {
					e.setFinishedRace(true);
				});
				unfinishedEntries.forEach(e -> {
					e.setFinishedRace(false);
					e.setOnLastLap(true);
				});
				return race;
			}
		}
		// not a grand prix finish or no winning entry so positions based on number of laps sailed and course length
		entriesSet.forEach(e -> {
			e.setFinishedRace(e.getLapsSailed() == race.getPlannedLaps());
			e.setOnLastLap(e.getLapsSailed() == (race.getPlannedLaps() - 1));
		});
		return race;
	}
}
