package com.bginfosys.dinghyracing.persistence;

import java.time.LocalDateTime;
import java.util.List;

//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;

import com.bginfosys.dinghyracing.model.Race;

public interface RaceRepository extends PagingAndSortingRepository<Race, Long> {

	@Override
	void deleteById(@Param("id") Long id);

	@Override
	void delete(@Param("race") Race race);
	
	List<Race> findByPlannedStartTimeGreaterThanEqual(@Param("time") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time);
	
	Race findByNameAndPlannedStartTime(@Param("name") String name, @Param("time") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time);
}
