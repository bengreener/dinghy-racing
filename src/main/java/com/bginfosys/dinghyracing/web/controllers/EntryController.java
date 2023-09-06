package com.bginfosys.dinghyracing.web.controllers;

import java.util.Optional;

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
public class EntryController {
	
	private final EntryRepository entryRepository;
	
	private final LapRepository lapRepository;
	
	private final RepositoryEntityLinks entityLinks;
	
	private final LinkCollector linkCollector;
	
	EntryController(EntryRepository entryRepository, LapRepository lapRepository, RepositoryEntityLinks entityLinks, 
			LinkCollector linkCollector) {
		this.entryRepository = entryRepository;
		this.lapRepository = lapRepository;
		this.entityLinks = entityLinks;
		this.linkCollector = linkCollector;
	}
	
	@Transactional
	@PatchMapping(path = "/entries/{entryId}/addLap", consumes = "application/json")
	public ResponseEntity<EntityModel<Entry>> addLap(@PathVariable Long entryId, @RequestBody LapDTO lapDTO) {
		Optional<Entry> optEntry = entryRepository.findById(entryId);
		Entry entry = optEntry.get();
		
		Lap lap = new Lap(lapDTO.getNumber(), lapDTO.getTime());
		lapRepository.save(lap);
		
		entry.addLap(lap);
		
		entryRepository.save(entry);
		
		Class<?> type = entry.getClass();
		
		Links links = linkCollector.getLinksFor(entry);
		EntityModel<Entry> resource = EntityModel.of(entry);
		resource.add(links);
		resource.add(entityLinks.linkToItemResource(type, entryId));
		
		ResponseEntity<EntityModel<Entry>> responseEntity = ResponseEntity.ok()
			.header("Content-Type", "application/hal+json")
			.body(resource);
		
		return responseEntity;
	}
	
	@Transactional
	@PatchMapping(path = "/entries/{entryId}/removeLap", consumes = "application/json")
	public ResponseEntity<Entry> removeLap(@PathVariable Long entryId, @RequestBody LapDTO lapDTO) {
		Optional<Entry> optEntry = entryRepository.findById(entryId);
		
		if (optEntry.isPresent()) {
			Entry entry = optEntry.get();
			Lap lap = new Lap(lapDTO.getNumber(), lapDTO.getTime());
			if (entry.removeLap(lap)) {
				entryRepository.save(entry);
				return new ResponseEntity<Entry>(HttpStatus.NO_CONTENT);
			} 
			else {
				return new ResponseEntity<Entry>(HttpStatus.BAD_REQUEST);
			}
		}
		else {
			return new ResponseEntity<Entry>(HttpStatus.NOT_FOUND);
		}
	}
}
