package com.bginfosys.dinghyracing.dinghy;

import java.util.List;
import java.util.Optional;

//import org.springframework.data.domain.Page;

//import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.bginfosys.dinghyracing.dinghyclass.DinghyClass;
//import com.bginfosys.dinghyracing.race.Race;

public interface DinghyRepository extends PagingAndSortingRepository<Dinghy, Long> {
	
	@Override
	Dinghy save(@Param("dinghy") Dinghy dinghy);
	
	@Override
	void deleteById(@Param("id") Long id);
	
	@Override
	void delete(@Param("dinghy") Dinghy dinghy);	
	
	Optional<Dinghy> findByDinghyClassAndSailNumber(DinghyClass dinghyClass, String sailNumber);


}
