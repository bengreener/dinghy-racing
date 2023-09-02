package com.bginfosys.dinghyracing.web.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.repository.support.DefaultRepositoryInvokerFactory;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.rest.core.UriToEntityConverter;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.core.support.EntityLookup;
import org.springframework.data.repository.support.RepositoryInvokerFactory;
import org.springframework.data.rest.core.support.UnwrappingRepositoryInvokerFactory;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.mapping.LinkCollector;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Links;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.bginfosys.dinghyracing.model.Entry;
import com.bginfosys.dinghyracing.model.Lap;
import com.bginfosys.dinghyracing.persistence.EntryRepository;
import com.bginfosys.dinghyracing.persistence.LapRepository;
import com.bginfosys.dinghyracing.web.dto.LapDTO;

@RepositoryRestController
public class EntryController {
	
	@Autowired
	ApplicationContext context;
	
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
	@PutMapping(path = "/entries/{entryId}/addLap", consumes = "application/json")
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
}
