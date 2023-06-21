package com.bginfosys.dinghyracing.persistence;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.bginfosys.dinghyracing.model.Race;

public interface RaceRepository extends PagingAndSortingRepository<Race, Long>, RaceRepositoryCustom {

	@Override
	void deleteById(@Param("id") Long id);

	@Override
	void delete(@Param("race") Race race);
}
