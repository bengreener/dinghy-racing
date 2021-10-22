package com.bginfosys.dinghyracing.race;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import com.bginfosys.dinghyracing.dinghyclass.DinghyClass;

class RaceTests {

	private Race race = new Race("Test Race", LocalDate.of(2021, 10, 14), LocalTime.of(14, 10), new DinghyClass("Test"));
	
	@Test
	void raceCreated() {
		assertThat(race).isNotNull();
	}
		
	@Test
	void setId() {
		race.setId((long) 1);
		assertEquals(race.getId(), 1);
	}
	
	@Test
	void idIsLong() {
		race.setId((long) 1);
		assertTrue(race.getId() instanceof Long);		
	}
	
	@Test
	void setName() {
		race.setName("Race A");
		assertEquals(race.getName(), "Race A");
	}
	
	@Test 
	void nameIsString() {
		race.setName("Race A");
		assertTrue(race.getName() instanceof String);
	}
	
	@Test
	void setDate() {
		race.setDate(LocalDate.of(2021, 9, 27));
		assertEquals(race.getDate(), LocalDate.of(2021, 9, 27));
	}
	
	@Test
	void dateIsLocalDate() {
		race.setDate(LocalDate.of(2021, 9, 27));
		assertTrue(race.getDate() instanceof LocalDate);
	}
	
	@Test
	void setPlannedStartTime() {
		race.setPlannedStartTime(LocalTime.of(16, 47));
		assertEquals(race.getPlannedStartTime(), LocalTime.of(16, 47));
	}
	
	@Test
	void plannedStartTimeIsLocalTime() {
		race.setPlannedStartTime(LocalTime.of(16, 47));
		assertTrue(race.getPlannedStartTime() instanceof LocalTime);
	}	
}
