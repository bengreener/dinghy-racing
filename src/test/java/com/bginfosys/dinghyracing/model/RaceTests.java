package com.bginfosys.dinghyracing.model;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

class RaceTests {

	//private Race race = new Race("Test Race", LocalDate.of(2021, 10, 14), LocalTime.of(14, 10), new DinghyClass("Test"));
	private DinghyClass dinghyClass = new DinghyClass("Test");
	private Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), dinghyClass);
	
	@Test
	void raceCreated() {
		assertThat(race, notNullValue());
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
	void setPlannedStartTime() {
		race.setPlannedStartTime(LocalDateTime.of(2021, 9, 27, 16, 47));
		assertEquals(race.getPlannedStartTime(), LocalDateTime.of(2021, 9, 27, 16, 47));
	}
	
	@Test
	void setDuration() {
		race.setDuration(Duration.ofMillis(1000));
		assertEquals(race.getDuration(), Duration.ofSeconds(1));
	}

	@Test
	void plannedStartTimeIsLocalDateTime() {
		race.setPlannedStartTime(LocalDateTime.of(2021, 9, 27, 16, 47));
		assertTrue(race.getPlannedStartTime() instanceof LocalDateTime);
	}
	
	@Test
	void when_entryAddedViaSignUp_the_addedToSignedUp() {
		Race race = new Race();
		Entry entry = new Entry();
		race.signUp(entry);
		assertThat(race.getSignedUp(), hasItem(entry));
	}

	@Test
	void when_startingARace_then_startTimeIsSet() {
		race.setActualStartTime(LocalDateTime.of(2023,  8, 12, 15, 00));
		assertEquals(race.getActualStartTime(), LocalDateTime.of(2023,  8, 12, 15, 00));
	}
}
