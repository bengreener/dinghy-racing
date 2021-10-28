package com.bginfosys.dinghyracing.race;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.rest.core.config.Projection;

import com.bginfosys.dinghyracing.dinghyclass.DinghyClass;

@Projection(name = "inlineDinghyClass", types = {Race.class})
interface InlineDinghyClass {
	
	String getName();
	
	LocalDate getDate();
	
	LocalTime getPlannedStartTime();
	
	DinghyClass getDinghyClass();
}
