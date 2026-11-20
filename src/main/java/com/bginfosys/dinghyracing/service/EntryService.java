package com.bginfosys.dinghyracing.service;

import java.time.Duration;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.rest.core.event.AfterCreateEvent;
import org.springframework.data.rest.core.event.AfterLinkSaveEvent;
import org.springframework.data.rest.core.event.BeforeCreateEvent;
import org.springframework.data.rest.core.event.BeforeLinkSaveEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bginfosys.dinghyracing.exceptions.DuplicateLapException;
import com.bginfosys.dinghyracing.exceptions.EntryNotFoundException;
import com.bginfosys.dinghyracing.model.Entry;
import com.bginfosys.dinghyracing.model.Lap;
import com.bginfosys.dinghyracing.persistence.EntryRepository;
import com.bginfosys.dinghyracing.persistence.LapRepository;

@Service
public class EntryService implements ApplicationEventPublisherAware {

	private final EntryRepository entryRepository;
	
	private final LapRepository lapRepository;
	
	private final RaceService raceService;

	private ApplicationEventPublisher publisher;
	
	EntryService(EntryRepository entryRepository, LapRepository lapRepository, RaceService raceService) {
		this.entryRepository = entryRepository;
		this.lapRepository = lapRepository;
		this.raceService = raceService;
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.publisher = applicationEventPublisher;		
	}
	
	@Transactional
	public Entry addLapToEntry(Long entryId, Duration lapTime) {
		Optional<Entry> optEntry = entryRepository.findById(entryId);
		if (optEntry.isEmpty()) {
			throw new EntryNotFoundException(entryId);
		}
		Entry entry = optEntry.get();
		
		Lap lap = new Lap(entry.getLapsSailed() + 1, lapTime);
		
		publisher.publishEvent(new BeforeCreateEvent(lap));
		lap = lapRepository.save(lap);
		publisher.publishEvent(new AfterCreateEvent(lap));
		
		try {
			entry.addLap(lap);
		}
		catch (DuplicateLapException exception) {
			lapRepository.delete(lap);
			throw exception;
		}
		
		// update positions of entries in races that are affected by the entry completing a lap
		entry.getSignedUpTo().forEach(s -> s.getRace().updatePositions(s));
		raceService.updateProgressIndicators(entry.getDirectRace());
		
		publisher.publishEvent(new BeforeLinkSaveEvent(entry, entry.getLaps()));
		entry = entryRepository.save(entry);
		publisher.publishEvent(new AfterLinkSaveEvent(entry, entry.getLaps()));
		return entry;
	}
	
	@Transactional
	public Entry removeLapFromEntry(long entryId, Lap lap) {
		Optional<Entry> optEntry = entryRepository.findById(entryId);
		if (optEntry.isEmpty()) {
			throw new EntryNotFoundException(entryId);
		}
		Entry entry = optEntry.get();
		
		entry.removeLap(lap);
		
		// update positions of entries in races that are affected by removing a lap from the entry
		entry.getSignedUpTo().forEach(s -> s.getRace().updatePositions(s));
		raceService.updateProgressIndicators(entry.getDirectRace());
		
		publisher.publishEvent(new BeforeLinkSaveEvent(entry, entry.getLaps()));
		Entry savedEntry = entryRepository.save(entry);
		publisher.publishEvent(new AfterLinkSaveEvent(savedEntry, savedEntry.getLaps()));
				
		return entry;
	}

}
