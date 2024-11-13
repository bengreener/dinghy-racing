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
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.UriTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bginfosys.dinghyracing.model.Dinghy;
import com.bginfosys.dinghyracing.persistence.CompetitorRepository;
import com.bginfosys.dinghyracing.web.dto.CrewDTO;

@RestController
@RequestMapping("/dinghyracing/api/crews")
public class CrewController {

	@Autowired
	JdbcTemplate jdbcTemplate;
	
	private final CompetitorRepository competitorRepository;
	
	private final PersistentEntities persistentEntities;

	private final RepositoryInvokerFactory repositoryInvokerFactory;
	
	private final ConversionService conversionService;

	CrewController(CompetitorRepository competitorRepository, PersistentEntities persistentEntities, RepositoryInvokerFactory repositoryInvokerFactory,
			@Qualifier("mvcConversionService") ConversionService conversionService) {
		this.competitorRepository = competitorRepository;
		this.persistentEntities = persistentEntities;
		this.repositoryInvokerFactory = repositoryInvokerFactory;
		this.conversionService = conversionService;
	}
	
	@GetMapping(path= "/search/findCrewsByDinghy")
//	public ResponseEntity<Set<CrewDTO>> findCrewsByDinghy(@RequestParam(name = "dinghy") String dinghyUri) {
	public ResponseEntity<Object> findCrewsByDinghy(@RequestParam(name = "dinghy") String dinghyUri) {
//		ResponseEntity<Set<CrewDTO>> responseEntity;
		ResponseEntity<Object> responseEntity;
		
		// get dinghy matching supplied uri
		TypeDescriptor dinghyType = TypeDescriptor.valueOf(Dinghy.class);
		Dinghy dinghy = (Dinghy) getEntityFromUri(UriTemplate.of(dinghyUri).expand(), dinghyType);
		
		// query entries table for unique crew combinations for entries with the dinghy
		List<Long[]> results = this.jdbcTemplate.query("SELECT DISTINCT helm_id, crew_id FROM entry WHERE dinghy_id = ?", 
				(resultSet, rowNum) -> {
					Long[] result = {resultSet.getLong("helm_id"), resultSet.getLong("crew_id")};
					return result;
				}, dinghy.getId());
		
		Set<CrewDTO> crews = new HashSet<CrewDTO>();
		for (Long[] result : results) {			
			crews.add(new CrewDTO(competitorRepository.findById(result[0]).orElse(null), competitorRepository.findById(result[1]).orElse(null)));
		}
		
		CollectionModel<CrewDTO> resource = CollectionModel.of(crews);
		
//		responseEntity = ResponseEntity.ok()
//			.header("Content-Type", "application/hal+json")
//			.body(crews);
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
