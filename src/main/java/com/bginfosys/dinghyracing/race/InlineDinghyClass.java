package com.bginfosys.dinghyracing.race;

import java.time.LocalDateTime;

import org.springframework.data.rest.core.config.Projection;

import com.bginfosys.dinghyracing.dinghyclass.DinghyClass;

@Projection(name = "inlineDinghyClass", types = {Race.class})
public interface InlineDinghyClass {
	
	String getName();
	
	LocalDateTime getPlannedStartTime();
	
	DinghyClass getDinghyClass();
}
