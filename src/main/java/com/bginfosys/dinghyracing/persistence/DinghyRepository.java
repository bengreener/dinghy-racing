package com.bginfosys.dinghyracing.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.bginfosys.dinghyracing.model.Dinghy;
import com.bginfosys.dinghyracing.model.DinghyClass;

public interface DinghyRepository extends PagingAndSortingRepository<Dinghy, Long> {
	
	@SuppressWarnings("unchecked")
	@Override
	Dinghy save(@Param("dinghy") Dinghy dinghy);
	
	@Override
	void deleteById(@Param("id") Long id);
	
	@Override
	void delete(@Param("dinghy") Dinghy dinghy);	
	
	Page<Dinghy> findByDinghyClass(@Param("dinghyclass") DinghyClass dinghyClass, Pageable pageable);

	Dinghy findBySailNumberAndDinghyClass(@Param("sailNumber") String sailNumber, @Param("dinghyclass") DinghyClass dinghyClass);
}
