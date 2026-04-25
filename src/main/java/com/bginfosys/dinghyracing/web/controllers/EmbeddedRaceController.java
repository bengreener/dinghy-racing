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
import org.springframework.data.rest.core.AggregateReference;
import org.springframework.data.rest.core.UriToEntityConverter;
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

import com.bginfosys.dinghyracing.model.EmbeddedRace;
import com.bginfosys.dinghyracing.model.Entry;
import com.bginfosys.dinghyracing.persistence.EmbeddedRaceRepository;

import jakarta.transaction.Transactional;

@RepositoryRestController
public class EmbeddedRaceController implements ApplicationEventPublisherAware {

	private final EmbeddedRaceRepository raceRepository;
	
	private final RepositoryEntityLinks entityLinks;
	
	private final PersistentEntities persistentEntities;

	private final RepositoryInvokerFactory repositoryInvokerFactory;
	
	private final ConversionService conversionService;

	private final LinkCollector linkCollector;
	
	EmbeddedRaceController(EmbeddedRaceRepository raceRepository, PersistentEntities persistentEntities, RepositoryInvokerFactory repositoryInvokerFactory,
			@Qualifier("mvcConversionService") ConversionService conversionService, RepositoryEntityLinks entityLinks, LinkCollector linkCollector) {
		this.raceRepository = raceRepository;
		this.persistentEntities = persistentEntities;
		this.repositoryInvokerFactory = repositoryInvokerFactory;
		this.conversionService = conversionService;
		this.entityLinks = entityLinks;
		this.linkCollector = linkCollector;
	}
	
	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		// TODO Auto-generated method stub

	}

	@Transactional
	@PatchMapping(path = "/embeddedRaces/{raceId}/signUp")
	public ResponseEntity<Object> signUp(@PathVariable("raceId") Long raceId, @RequestParam(name = "entry") String entryURI) {
		// get race
		Optional<EmbeddedRace> optRace = raceRepository.findById(raceId);
		EmbeddedRace race = optRace.get();
		
		TypeDescriptor entryType = TypeDescriptor.valueOf(Entry.class);
		Entry entry = (Entry) getEntityFromUri(UriTemplate.of(entryURI).expand(), entryType);
		
		race.signUp(entry);
				
		ResponseEntity<Object> responseEntity;
		Class<?> raceType = race.getClass();
		Links links = linkCollector.getLinksFor(race);
		EntityModel<EmbeddedRace> resource = EntityModel.of(race);
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
