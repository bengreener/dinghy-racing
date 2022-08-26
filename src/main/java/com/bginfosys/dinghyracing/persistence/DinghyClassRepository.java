package com.bginfosys.dinghyracing.persistence;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.bginfosys.dinghyracing.model.DinghyClass;

@RepositoryRestResource(path = "dinghyclasses") //, exported = false)
public interface DinghyClassRepository extends PagingAndSortingRepository<DinghyClass, Long> {

	@SuppressWarnings("unchecked")
	@Override
	DinghyClass save(@Param("dinghyClass") DinghyClass dinghyClass);
	
	@Override
	void deleteById(@Param("id") Long id);

	@Override
	void delete(@Param("dinghyClass") DinghyClass dinghyClass);
	
}
