package com.bginfosys.dinghyracing.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import com.bginfosys.dinghyracing.exceptions.LapZeroOrLessTimeException;

public class LapTests {

	@Test
	void when_creatingALapWithBlankConstructor_then_getALap() {
		Lap lap = new Lap();
		
		assertTrue(lap instanceof Lap);
	}
	
	@Test
	void when_creatingALapWithArgumentsConstructor_then_errorIsThrown() {
		assertThrows(LapZeroOrLessTimeException.class, () -> {
			new Lap(1, Duration.ofMinutes(0));
		});
	}
	
	@Test
	void when_creatingALapWithZeroTime_then_errorIsThrown() {
		assertThrows(LapZeroOrLessTimeException.class, () -> {
			new Lap(1, Duration.ofMinutes(-15));
		});
	}
	
	@Test
	void when_creatingALapWithNegativeTime_then_getALap() {
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
	void when_settingTimeToPositiveValueGreaterThanZero_then_LapNumberSetToValueProvided() {
		Lap lap = new Lap();
		lap.setTime(Duration.ofMinutes(14));
		
		assertEquals(lap.getTime(), Duration.ofMinutes(14));
	}
	
	@Test
	void when_settingTimeToZero_then_errorIsThrown() {
		Lap lap = new Lap();
		
		assertThrows(LapZeroOrLessTimeException.class, () -> {
			lap.setTime(Duration.ofMinutes(0));
		});	
	}

	@Test
	void when_settingTimeToLessThanZero_then_errorIsThrown() {
		Lap lap = new Lap();
		
		assertThrows(LapZeroOrLessTimeException.class, () -> {
			lap.setTime(Duration.ofMinutes(-1));
		});	
	}
}