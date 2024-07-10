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
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.repository.support.RepositoryInvokerFactory;
import org.springframework.data.rest.core.UriToEntityConverter;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.UriTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.bginfosys.dinghyracing.model.Entry;
import com.bginfosys.dinghyracing.model.Race;
import com.bginfosys.dinghyracing.persistence.RaceRepository;

import jakarta.transaction.Transactional;

@RepositoryRestController
public class RaceController {
	
	private final RaceRepository raceRepository;
	
	private final PersistentEntities persistentEntities;

	private final RepositoryInvokerFactory repositoryInvokerFactory;
	
	private final ConversionService conversionService;
	
	RaceController(RaceRepository raceRepository, PersistentEntities persistentEntities, RepositoryInvokerFactory repositoryInvokerFactory,
			@Qualifier("mvcConversionService") ConversionService conversionService) {
		this.raceRepository = raceRepository;
		this.persistentEntities = persistentEntities;
		this.repositoryInvokerFactory = repositoryInvokerFactory;
		this.conversionService = conversionService;
	}
	
	@Transactional
	@PatchMapping(path = "/races/{raceId}/updateEntryPosition")
	public ResponseEntity<Race> updateEntryPosition(@PathVariable("raceId") Long raceId, @RequestParam(name = "entry") String entryURI, @RequestParam(name = "position") Integer newPosition) {
		Optional<Race> optRace = raceRepository.findById(raceId);
		Race race = optRace.get();
		
		TypeDescriptor entryType = TypeDescriptor.valueOf(Entry.class);
		
		Entry entry = (Entry) getEntityFromUri(UriTemplate.of(entryURI).expand(), entryType);
		race.updateEntryPosition(entry, newPosition);
		return new ResponseEntity<Race>(HttpStatus.NO_CONTENT);
	}

	private Object getEntityFromUri(URI uri, TypeDescriptor targetType) {
		TypeDescriptor sourceType = TypeDescriptor.valueOf(URI.class);

		UriToEntityConverter uriToEntityConverter = new UriToEntityConverter(persistentEntities, repositoryInvokerFactory, () -> conversionService);

		return uriToEntityConverter.convert(uri, sourceType, targetType);
	}
}
