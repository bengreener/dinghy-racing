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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.repository.support.RepositoryInvokerFactory;
import org.springframework.data.rest.core.UriToEntityConverter;
import org.springframework.data.rest.webmvc.mapping.LinkCollector;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.UriTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bginfosys.dinghyracing.model.Dinghy;
import com.bginfosys.dinghyracing.persistence.CompetitorRepository;
import com.bginfosys.dinghyracing.web.model.proxy.Crew;
import com.bginfosys.dinghyracing.model.Competitor;

@RestController
@RequestMapping("/dinghyracing/api/crews")
public class CrewController {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	private final CompetitorRepository competitorRepository;
	
	private final RepositoryEntityLinks entityLinks;
	
	private final PersistentEntities persistentEntities;

	private final RepositoryInvokerFactory repositoryInvokerFactory;
	
	private final ConversionService conversionService;
	
	private final LinkCollector linkCollector;

	CrewController(CompetitorRepository competitorRepository, RepositoryEntityLinks entityLinks, PersistentEntities persistentEntities, RepositoryInvokerFactory repositoryInvokerFactory,
			@Qualifier("mvcConversionService") ConversionService conversionService, LinkCollector linkCollector) {
		this.competitorRepository = competitorRepository;
		this.entityLinks = entityLinks;
		this.persistentEntities = persistentEntities;
		this.repositoryInvokerFactory = repositoryInvokerFactory;
		this.conversionService = conversionService;
		this.linkCollector = linkCollector;
	}
	
	@GetMapping(path= "/search/findCrewsByDinghy")
	public ResponseEntity<Object> findCrewsByDinghy(@RequestParam(name = "dinghy") String dinghyUri) {
		ResponseEntity<Object> responseEntity;
		
		// get dinghy matching supplied uri
		TypeDescriptor dinghyType = TypeDescriptor.valueOf(Dinghy.class);
		Dinghy dinghy = (Dinghy) getEntityFromUri(UriTemplate.of(dinghyUri).expand(), dinghyType);
		
		if (dinghy != null ) {
			// query entries table for unique crew combinations for entries with the dinghy
			List<Long[]> results = this.jdbcTemplate.query("SELECT DISTINCT helm_id, crew_id FROM entry WHERE dinghy_id = ?", 
					(resultSet, rowNum) -> {
						Long[] result = {resultSet.getLong("helm_id"), resultSet.getLong("crew_id")};
						return result;
					}, dinghy.getId());
			
			Set<Crew> crews = new HashSet<Crew>();
			for (Long[] result : results) {
				Competitor helm = competitorRepository.findById(result[0]).orElse(null);
				Competitor mate = competitorRepository.findById(result[1]).orElse(null);
				
				Class<?> type = helm.getClass();
				EntityModel<Competitor> helmEntityModel = EntityModel.of(helm);
				Links helmLinks = linkCollector.getLinksFor(helm);
				helmEntityModel.add(helmLinks);
				helmEntityModel.add(entityLinks.linkToItemResource(type, result[0]));
				
				EntityModel<Competitor> mateEntityModel = null;
				if (mate != null) {
					mateEntityModel = EntityModel.of(mate);
					Links mateLinks = linkCollector.getLinksFor(mate);
					mateEntityModel.add(mateLinks);	
					mateEntityModel.add(entityLinks.linkToItemResource(type, result[1]));
				}
				crews.add(new Crew(helmEntityModel, mateEntityModel));
			}
			
			CollectionModel<Crew> resource = CollectionModel.of(crews);
			
			responseEntity = ResponseEntity.ok()
				.header("Content-Type", "application/hal+json")
				.body(resource);	
		}
		else {
			responseEntity = new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
		}		
		
		return responseEntity;
	}
	
	private Object getEntityFromUri(URI uri, TypeDescriptor targetType) {
		TypeDescriptor sourceType = TypeDescriptor.valueOf(URI.class);

		UriToEntityConverter uriToEntityConverter = new UriToEntityConverter(persistentEntities, repositoryInvokerFactory, () -> conversionService);

		return uriToEntityConverter.convert(uri, sourceType, targetType);
	}
	
}
