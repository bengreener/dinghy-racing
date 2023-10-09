package com.bginfosys.dinghyracing.persistence;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.bginfosys.dinghyracing.model.Competitor;

public interface CompetitorRepository extends PagingAndSortingRepository<Competitor, Long> {
	
	Competitor findByName(@Param("name") String Name);
	
}
