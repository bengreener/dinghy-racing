package com.bginfosys.dinghyracing.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.bginfosys.dinghyracing.model.Entry;
import com.bginfosys.dinghyracing.model.Race;

public interface EntryRepository extends PagingAndSortingRepository<Entry, Long> {

	Page<Entry> findByRace(@Param("race") Race race, Pageable pageable);
}
