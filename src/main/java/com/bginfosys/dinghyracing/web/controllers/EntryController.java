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
   
package com.bginfosys.dinghyracing.web.controllers;

import java.time.Duration;
import java.util.Optional;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.data.rest.core.event.AfterCreateEvent;
import org.springframework.data.rest.core.event.AfterLinkSaveEvent;
import org.springframework.data.rest.core.event.BeforeCreateEvent;
import org.springframework.data.rest.core.event.BeforeLinkSaveEvent;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.mapping.LinkCollector;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Links;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.bginfosys.dinghyracing.model.Entry;
import com.bginfosys.dinghyracing.model.Lap;
import com.bginfosys.dinghyracing.persistence.EntryRepository;
import com.bginfosys.dinghyracing.persistence.LapRepository;
import com.bginfosys.dinghyracing.web.dto.LapDTO;

@RepositoryRestController
public class EntryController implements ApplicationEventPublisherAware {
	
	private final EntryRepository entryRepository;
	
	private final LapRepository lapRepository;
	
	private final RepositoryEntityLinks entityLinks;
	
	private final LinkCollector linkCollector;

	private ApplicationEventPublisher publisher;
	
	EntryController(EntryRepository entryRepository, LapRepository lapRepository, RepositoryEntityLinks entityLinks, 
			LinkCollector linkCollector) {
		this.entryRepository = entryRepository;
		this.lapRepository = lapRepository;
		this.entityLinks = entityLinks;
		this.linkCollector = linkCollector;
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.publisher = applicationEventPublisher;		
	}
	
	/**
	 * Add lap to entry
	 * Will fail if a lap with the lap number has already been recorded for the entry
	 * @param entryId
	 * @param lapDTO
	 * @return ResponseEntity<EntityModel<Entry>>
	 */
	@Transactional
	@PatchMapping(path = "/entries/{entryId}/addLap", consumes = "application/json")
	public ResponseEntity<Object> addLap(@PathVariable("entryId") Long entryId, @RequestBody LapDTO lapDTO) {
		Optional<Entry> optEntry = entryRepository.findById(entryId);
		Entry entry = optEntry.get();
		Lap lap;
		
		if (lapDTO.getNumber() != null) {
			lap = new Lap(lapDTO.getNumber(), lapDTO.getTime());
		}
		else {
			lap = new Lap(entry.getLapsSailed() + 1, lapDTO.getTime());
		}
		
		publisher.publishEvent(new BeforeCreateEvent(lap));
		Lap savedLap = lapRepository.save(lap);
		publisher.publishEvent(new AfterCreateEvent(savedLap));
		
		ResponseEntity<Object> responseEntity;
		
		if (entry.addLap(lap)) {
			publisher.publishEvent(new BeforeLinkSaveEvent(entry, entry.getLaps()));
			Entry savedEntry = entryRepository.save(entry);
			publisher.publishEvent(new AfterLinkSaveEvent(savedEntry, savedEntry.getLaps()));
			
			Class<?> type = savedEntry.getClass();
			
			Links links = linkCollector.getLinksFor(savedEntry);
			EntityModel<Entry> resource = EntityModel.of(savedEntry);
			resource.add(links);
			resource.add(entityLinks.linkToItemResource(type, entryId));
			
			responseEntity = ResponseEntity.ok()
				.header("Content-Type", "application/hal+json")
				.body(resource);
		}
		else {
			responseEntity = new ResponseEntity<Object>(HttpStatus.CONFLICT);
		}
		return responseEntity;
	}
	
	/**
	 * Add laps to entry based on a final lap that provides the number of laps sailed and the time from the start of the race to complete that final lap. 
	 * Will fail if the number of laps sailed is greater than the number of laps set for the race.
	 * If number of laps is 0 any time provided will be ignored.
	 * @param entryId
	 * @param lapDTO
	 * @return ResponseEntity<EntityModel<Entry>>
	 */
	@Transactional
	@PatchMapping(path = "/entries/{entryId}/setLapTotal", consumes = "application/json")
	public ResponseEntity<Object> setLapTotal(@PathVariable("entryId") Long entryId, @RequestBody LapDTO lapDTO) {
		Optional<Entry> optEntry = entryRepository.findById(entryId);
		Entry entry = optEntry.get();
		SortedSet<Lap> laps = new ConcurrentSkipListSet<Lap>();
		
		if (lapDTO.getNumber() > 0) {
			Duration averageLapTime = lapDTO.getTime().dividedBy(lapDTO.getNumber());
			Duration residualTime = lapDTO.getTime().minus(averageLapTime.multipliedBy(lapDTO.getNumber())); // sum of laps must equal time provided for all laps
			for (int i = 0; i < lapDTO.getNumber(); i++) {
				Lap lap;
				if (i < lapDTO.getNumber() - 1) {
					lap = new Lap(i + 1, averageLapTime);
				}
				else {
					lap = new Lap(i + 1, averageLapTime.plus(residualTime));
				}
				publisher.publishEvent(new BeforeCreateEvent(lap));
				Lap savedLap = lapRepository.save(lap);
				publisher.publishEvent(new AfterCreateEvent(savedLap));
				laps.add(savedLap);
			}	
		}		
		
		// to avoid DataIntegrityViolationException on FK_entry_laps_laps_id need to clear existing laps and add new laps as separate operations
		// only triggering before and after link save events once each to avoid excessive calls through client updates from WebSocket notifications.
		publisher.publishEvent(new BeforeLinkSaveEvent(entry, entry.getLaps()));
		entry.clearLaps();
		entry = entryRepository.saveAndFlush(entry);
		entry.setFinalLaps(laps);
		entry = entryRepository.save(entry);
		publisher.publishEvent(new AfterLinkSaveEvent(entry, entry.getLaps()));
		
		Class<?> type = entry.getClass();
		Links links = linkCollector.getLinksFor(entry);
		EntityModel<Entry> resource = EntityModel.of(entry);
		resource.add(links);
		resource.add(entityLinks.linkToItemResource(type, entryId));

		ResponseEntity<Object> responseEntity;
		responseEntity = ResponseEntity.ok()
			.header("Content-Type", "application/hal+json")
			.body(resource);
		return responseEntity;
	}
	
	@Transactional
	@PatchMapping(path = "/entries/{entryId}/removeLap", consumes = "application/json")
	public ResponseEntity<Object> removeLap(@PathVariable("entryId") Long entryId, @RequestBody LapDTO lapDTO) {
		Optional<Entry> optEntry = entryRepository.findById(entryId);
		
		if (optEntry.isPresent()) {
			ResponseEntity<Object> responseEntity;
			Entry entry = optEntry.get();
			Lap lap = new Lap(lapDTO.getNumber(), lapDTO.getTime());
			if (entry.removeLap(lap)) {
				publisher.publishEvent(new BeforeLinkSaveEvent(entry, entry.getLaps()));
				Entry savedEntry = entryRepository.save(entry);
				publisher.publishEvent(new AfterLinkSaveEvent(savedEntry, savedEntry.getLaps()));
				
				Class<?> type = savedEntry.getClass();
				Links links = linkCollector.getLinksFor(savedEntry);
				EntityModel<Entry> resource = EntityModel.of(savedEntry);
				resource.add(links);
				resource.add(entityLinks.linkToItemResource(type, entryId));
				
				responseEntity = ResponseEntity.ok()
					.header("Content-Type", "application/hal+json")
					.body(resource);
				return responseEntity;
			} 
			else {
				return new ResponseEntity<Object>(HttpStatus.BAD_REQUEST);
			}
		}
		else {
			return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
		}
	}
	
	@Transactional
	@PatchMapping(path = "/entries/{entryId}/updateLap", consumes = "application/json")
	public ResponseEntity<Object> updateLap(@PathVariable("entryId") Long entryId, @RequestBody LapDTO lapDTO) {
		Entry entry = entryRepository.findById(entryId).orElseThrow();
		Lap lap;
		if (lapDTO.getNumber() != null) {
			lap = new Lap(lapDTO.getNumber(), lapDTO.getTime());
		}
		else {
			lap = new Lap(entry.getLapsSailed(), lapDTO.getTime());
		}
		
		entry.updateLap(lap);
		publisher.publishEvent(new BeforeLinkSaveEvent(entry, entry.getLaps()));
		Entry savedEntry = entryRepository.save(entry);
		publisher.publishEvent(new AfterLinkSaveEvent(savedEntry, savedEntry.getLaps()));
		
		ResponseEntity<Object> responseEntity;
		Class<?> type = savedEntry.getClass();
		Links links = linkCollector.getLinksFor(savedEntry);
		EntityModel<Entry> resource = EntityModel.of(savedEntry);
		resource.add(links);
		resource.add(entityLinks.linkToItemResource(type, entryId));
		
		responseEntity = ResponseEntity.ok()
			.header("Content-Type", "application/hal+json")
			.body(resource);
		return responseEntity;
	}
}
