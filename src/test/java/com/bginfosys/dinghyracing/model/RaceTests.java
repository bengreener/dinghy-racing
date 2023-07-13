package com.bginfosys.dinghyracing.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

//import static org.hamcrest.Matchers.contains;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.CoreMatchers.equalTo;

//import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Test;

import com.bginfosys.dinghyracing.exceptions.DinghyClassMismatchException;

//import com.bginfosys.dinghyracing.model.Dinghy;
//import com.bginfosys.dinghyracing.model.DinghyClass;
//import com.bginfosys.dinghyracing.model.Race;

//import java.time.LocalDate;
//import java.time.LocalTime;
import java.time.LocalDateTime;

class RaceTests {

	//private Race race = new Race("Test Race", LocalDate.of(2021, 10, 14), LocalTime.of(14, 10), new DinghyClass("Test"));
	private DinghyClass dinghyClass = new DinghyClass("Test");
	private Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), dinghyClass);
	
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
	void when_signingUpAndEntryDinghyDinghyClassMatchesRaceDinghyClass_Then_dinghySignedUp() {
		Competitor competitor = new Competitor();
		Dinghy dinghy = new Dinghy("1234", dinghyClass);
		Entry entry = new Entry(competitor, dinghy);
		race.signUp(entry);
		assertThat(race.getSignedUp()).contains(entry);
	}
	
	@Test
	void when_signingUpDinghyClassDoesNotMatchRaceDinghyClassAndRaceDinghyClassIsNotNull_then_throwsException() {
		Competitor competitor = new Competitor();
		DinghyClass dinghyClass = new DinghyClass("NotTest");
		Dinghy dinghy = new Dinghy("1234", dinghyClass);
		Entry entry = new Entry(competitor, dinghy);
		
		assertThrows(DinghyClassMismatchException.class, () -> {
			race.signUp(entry);
		});
	}
	
	@Test
	void when_signingUpDinghyClassDoesNotMatchRaceDinghyClassAndRaceDinghyClassIsNull_Then_dinghySignedUp() {
		Race race = new Race("New Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), null);
		Competitor competitor = new Competitor();
		Dinghy dinghy = new Dinghy("1234", dinghyClass);
		Entry entry = new Entry(competitor, dinghy);
		
		race.signUp(entry);
		assertThat(race.getSignedUp()).contains(entry);
	}
	
	
}
