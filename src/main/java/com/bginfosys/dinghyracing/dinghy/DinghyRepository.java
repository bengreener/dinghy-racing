package com.bginfosys.dinghyracing.dinghy;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface DinghyRepository extends PagingAndSortingRepository<Dinghy, Long> {
	
	@Override
	Dinghy save(@Param("dinghy") Dinghy dinghy);
	
	@Override
	void deleteById(@Param("id") Long id);
	
	@Override
	void delete(@Param("dinghy") Dinghy dinghy);	

}
