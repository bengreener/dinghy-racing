package com.bginfosys.dinghyracing.persistence;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bginfosys.dinghyracing.model.Entry;

public interface EntryRepository extends PagingAndSortingRepository<Entry, Long> {

}
