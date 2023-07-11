package com.bginfosys.dinghyracing.persistence;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bginfosys.dinghyracing.model.Competitor;

public interface CompetitorRepository extends PagingAndSortingRepository<Competitor, Long> {

}
