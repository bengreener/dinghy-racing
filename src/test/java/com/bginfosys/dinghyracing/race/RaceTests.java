package com.bginfosys.dinghyracing.race;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;

import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;

//import java.time.LocalDate;
//import java.time.LocalTime;
import java.time.LocalDateTime;

import com.bginfosys.dinghyracing.dinghyclass.DinghyClass;
import com.bginfosys.dinghyracing.dinghy.Dinghy;

class RaceTests {

	//private Race race = new Race("Test Race", LocalDate.of(2021, 10, 14), LocalTime.of(14, 10), new DinghyClass("Test"));
	private Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), new DinghyClass("Test"));
	
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
	void setPlannedStartTime() {
		race.setPlannedStartTime(LocalDateTime.of(2021, 9, 27, 16, 47));
		assertEquals(race.getPlannedStartTime(), LocalDateTime.of(2021, 9, 27, 16, 47));
	}
	
	@Test
	void plannedStartTimeIsLocalDateTime() {
		race.setPlannedStartTime(LocalDateTime.of(2021, 9, 27, 16, 47));
		assertTrue(race.getPlannedStartTime() instanceof LocalDateTime);
	}
	
	@Test
	void signUpDinghy() {
		DinghyClass dc = new DinghyClass("Scorpion");
		Dinghy d = new Dinghy("1234", dc);
		race.signUpDinghy(d);
		//assertThat(race.getSignedUp(), contains(Arrays.asList(equalTo(d))));
	}
}
