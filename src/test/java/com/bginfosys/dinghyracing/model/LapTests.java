package com.bginfosys.dinghyracing.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;

public class LapTests {

	@Test
	void when_creatingALapWithBlankConstructor_then_getALap() {
		Lap lap = new Lap();
		
		assertTrue(lap instanceof Lap);
	}
	
	@Test
	void when_creatingALapWithArgumentsConstructor_then_getALap() {
		Lap lap = new Lap(1, Duration.ofMinutes(15));
		
		assertTrue(lap instanceof Lap);
	}
	
	@Test
	void when_requestingLapNumber_then_getAnInteger() {
		Lap lap = new Lap(1, Duration.ofMinutes(15));
		
		assertTrue(lap.getNumber() instanceof Integer);
	}
	
	@Test
	void when_requestingDuration_then_getDuration() {
		Lap lap = new Lap(1, Duration.ofMinutes(15));
		
		assertTrue(lap.getTime() instanceof Duration);
	}
	
	@Test
	void when_settingLapNumber_then_LapNumberSetToValueProvided() {
		Lap lap = new Lap();
		lap.setNumber(3);
		
		assertEquals(lap.getNumber(), 3);
	}
	
	@Test
	void when_settingTime_then_LapNumberSetToValueProvided() {
		Lap lap = new Lap();
		lap.setTime(Duration.ofMinutes(14));
		
		assertEquals(lap.getTime(), Duration.ofMinutes(14));
	}
}