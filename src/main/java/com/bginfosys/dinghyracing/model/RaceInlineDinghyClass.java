package com.bginfosys.dinghyracing.model;

import java.time.LocalDateTime;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "raceInlineDinghyClass", types = {Race.class})
public interface RaceInlineDinghyClass {
	
	String getName();
	
	LocalDateTime getPlannedStartTime();
	
	DinghyClass getDinghyClass();
}
