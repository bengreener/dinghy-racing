package com.bginfosys.dinghyracing.race;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface RaceRepository extends PagingAndSortingRepository<Race, Long> {

	@Override
	Race save(@Param("race") Race race);

	@Override
	void deleteById(@Param("id") Long id);

	@Override
	void delete(@Param("race") Race race);
	
}
