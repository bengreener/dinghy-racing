package com.bginfosys.dinghyracing.persistence;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.bginfosys.dinghyracing.model.DinghyClass;

public interface DinghyClassRepository extends PagingAndSortingRepository<DinghyClass, Long> {

	@SuppressWarnings("unchecked")
	@Override
	DinghyClass save(@Param("dinghyClass") DinghyClass dinghyClass);
	
	@Override
	void deleteById(@Param("id") Long id);

	@Override
	void delete(@Param("dinghyClass") DinghyClass dinghyClass);
	
	DinghyClass findByName(@Param("name") String Name);
}
