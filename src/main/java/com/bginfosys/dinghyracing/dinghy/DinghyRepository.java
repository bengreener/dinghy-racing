package com.bginfosys.dinghyracing.dinghy;

//import java.util.List;
//import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.bginfosys.dinghyracing.dinghyclass.DinghyClass;

@RepositoryRestResource(excerptProjection = InlineDinghyClass.class)
public interface DinghyRepository extends PagingAndSortingRepository<Dinghy, Long> {
	
	@Override
	Dinghy save(@Param("dinghy") Dinghy dinghy);
	
	@Override
	void deleteById(@Param("id") Long id);
	
	@Override
	void delete(@Param("dinghy") Dinghy dinghy);	
	
	Page<Dinghy> findByDinghyClass(@Param("dinghyclass") DinghyClass dinghyClass, Pageable pageable);

}
