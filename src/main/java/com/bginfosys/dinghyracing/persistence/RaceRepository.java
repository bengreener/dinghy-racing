package com.bginfosys.dinghyracing.persistence;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.bginfosys.dinghyracing.model.RaceInlineDinghyClass;
import com.bginfosys.dinghyracing.model.Race;

//import com.bginfosys.dinghyracing.dinghyclass.DinghyClass;

@RepositoryRestResource(excerptProjection = RaceInlineDinghyClass.class)
public interface RaceRepository extends PagingAndSortingRepository<Race, Long> {

	@Override
	Race save(@Param("race") Race race);

	@Override
	void deleteById(@Param("id") Long id);

	@Override
	void delete(@Param("race") Race race);
	
	//Optional<Race> findByDinghyClass(@Param("dinghyclass") DinghyClass dinghyClass);
	
}
