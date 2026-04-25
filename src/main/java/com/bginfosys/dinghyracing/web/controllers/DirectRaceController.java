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

import java.net.URI;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.repository.support.RepositoryInvokerFactory;
import org.springframework.data.rest.core.UriToEntityConverter;
import org.springframework.data.rest.core.event.AfterCreateEvent;
import org.springframework.data.rest.core.event.AfterSaveEvent;
import org.springframework.data.rest.core.event.BeforeCreateEvent;
import org.springframework.data.rest.core.event.BeforeSaveEvent;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.mapping.LinkCollector;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.UriTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.bginfosys.dinghyracing.model.Competitor;
import com.bginfosys.dinghyracing.model.Dinghy;
import com.bginfosys.dinghyracing.model.DirectRace;
import com.bginfosys.dinghyracing.model.Entry;
import com.bginfosys.dinghyracing.model.SignedUp;
import com.bginfosys.dinghyracing.persistence.DirectRaceRepository;
import com.bginfosys.dinghyracing.persistence.EntryRepository;
import com.bginfosys.dinghyracing.web.dto.SignUpDTO;

import jakarta.transaction.Transactional;

@RepositoryRestController
public class DirectRaceController implements ApplicationEventPublisherAware {
	
	private final DirectRaceRepository raceRepository;
	
	private final EntryRepository entryRepository;
	
	private final RepositoryEntityLinks entityLinks;
	
	private final PersistentEntities persistentEntities;

	private final RepositoryInvokerFactory repositoryInvokerFactory;
	
	private final ConversionService conversionService;

	private final LinkCollector linkCollector;
	
	private ApplicationEventPublisher publisher;
	
	DirectRaceController(DirectRaceRepository raceRepository, PersistentEntities persistentEntities, RepositoryInvokerFactory repositoryInvokerFactory,
			@Qualifier("mvcConversionService") ConversionService conversionService, EntryRepository entryRepository, RepositoryEntityLinks entityLinks, 
			LinkCollector linkCollector) {
		this.raceRepository = raceRepository;
		this.persistentEntities = persistentEntities;
		this.repositoryInvokerFactory = repositoryInvokerFactory;
		this.conversionService = conversionService;
		this.entryRepository = entryRepository;
		this.entityLinks = entityLinks;
		this.linkCollector = linkCollector;
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.publisher = applicationEventPublisher;
	}
	
	@Transactional
	@PatchMapping(path = "/directRaces/{raceId}/updateEntryPosition")
	public ResponseEntity<Object> updateEntryPosition(@PathVariable("raceId") Long raceId, @RequestParam(name = "entry") String entryURI, @RequestParam(name = "position") Integer newPosition) {
		Optional<DirectRace> optRace = raceRepository.findById(raceId);
		DirectRace race = optRace.get();
		
		TypeDescriptor entryType = TypeDescriptor.valueOf(Entry.class);		
		Entry entry = (Entry) getEntityFromUri(UriTemplate.of(entryURI).expand(), entryType);
		publisher.publishEvent(new BeforeSaveEvent(entry));
		race.updateEntryPositions(entry, newPosition);
		// relying on framework to handle actual entity save and assuming this is done
		publisher.publishEvent(new AfterSaveEvent(entry));
		
		ResponseEntity<Object> responseEntity;
		Class<?> type = race.getClass();
		Links links = linkCollector.getLinksFor(race);
		EntityModel<DirectRace> resource = EntityModel.of(race);resource.add(links);
		resource.add(entityLinks.linkToItemResource(type, raceId));
		responseEntity = ResponseEntity.ok()
			.header("Content-Type", "application/hal+json")
			.body(resource);
		return responseEntity;
	}
	
	@Transactional
	@PatchMapping(path = "/directRaces/{raceId}/signUp", consumes = "application/json")
	public ResponseEntity<Object> signUp(@PathVariable("raceId") Long raceId, @RequestBody SignUpDTO signUpDTO) {
		// get race
		Optional<DirectRace> optRace = raceRepository.findById(raceId);
		DirectRace race = optRace.get();

		// get dinghy
		TypeDescriptor dinghyType = TypeDescriptor.valueOf(Dinghy.class);
		 Dinghy dinghy = (Dinghy) getEntityFromUri(signUpDTO.getDinghy(), dinghyType);
		// get helm
		 TypeDescriptor competitorType = TypeDescriptor.valueOf(Competitor.class);
		 Competitor helm = (Competitor) getEntityFromUri(signUpDTO.getHelm(), competitorType);
		// get crew (if present)
		 Competitor crew;
		 SignedUp signedUp;
		if (signUpDTO.getCrew() != null) {
			crew = (Competitor) getEntityFromUri(signUpDTO.getCrew(), competitorType);
			signedUp = race.signUp(helm, crew, dinghy);
		}
		else {
			signedUp = race.signUp(helm, dinghy);
		}
		if (signedUp.getEntry().getId() == null) {
			publisher.publishEvent(new BeforeCreateEvent(signedUp.getEntry()));
			entryRepository.save(signedUp.getEntry()); // also saves new SignedUp
			publisher.publishEvent(new AfterCreateEvent(signedUp.getEntry()));
		}
		
		ResponseEntity<Object> responseEntity;
		Class<?> raceType = race.getClass();
		Links links = linkCollector.getLinksFor(race);
		EntityModel<DirectRace> resource = EntityModel.of(race);
		resource.add(links);
		resource.add(entityLinks.linkToItemResource(raceType, raceId));
		responseEntity = ResponseEntity.ok()
			.header("Content-Type", "application/hal+json")
			.body(resource);
		return responseEntity;
	}

	private Object getEntityFromUri(URI uri, TypeDescriptor targetType) {
		TypeDescriptor sourceType = TypeDescriptor.valueOf(URI.class);

		UriToEntityConverter uriToEntityConverter = new UriToEntityConverter(persistentEntities, repositoryInvokerFactory, () -> conversionService);

		return uriToEntityConverter.convert(uri, sourceType, targetType);
	}
}
