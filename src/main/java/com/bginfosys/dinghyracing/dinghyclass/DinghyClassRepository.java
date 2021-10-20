package com.bginfosys.dinghyracing.dinghyclass;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "dinghyclasses")
public interface DinghyClassRepository extends PagingAndSortingRepository<DinghyClass, Long> {

	@Override
	DinghyClass save(@Param("dinghyClass") DinghyClass dinghyClass);
	
	@Override
	void deleteById(@Param("id") Long id);

	@Override
	void delete(@Param("dinghyClass") DinghyClass dinghyClass);
}
