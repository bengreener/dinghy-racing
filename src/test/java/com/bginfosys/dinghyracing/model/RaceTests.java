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
	void when_signingUpDinghyAndDinghyDinghyClassMatchesRaceDinghyClass_Then_dinghySignedUp() {
//		DinghyClass dc = new DinghyClass("Test");
		Dinghy d = new Dinghy("1234", dinghyClass);
		race.signUpDinghy(d);
		assertThat(race.getSignedUp()).contains(d);
	}
	
	@Test
	void when_signingUpDinghyClassDoesNotMatchRaceDinghyClassAndRaceDinghyClassIsNotNull_then_throwsException() {
		DinghyClass dc = new DinghyClass("NotTest");
		Dinghy d = new Dinghy("1234", dc);
//		race.signUpDinghy(d);
//		assertThat(race.getSignedUp()).doesNotContain(d);
		assertThrows(DinghyClassMismatchException.class, () -> {
			race.signUpDinghy(d);
		});
	}
	
	@Test
	void when_signingUpDinghyClassDoesNotMatchRaceDinghyClassAndRaceDinghyClassIsNull_Then_dinghySignedUp() {
		Race race1 = new Race("New Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), null);
		Dinghy d = new Dinghy("1234", dinghyClass);
		race1.signUpDinghy(d);
		assertThat(race1.getSignedUp()).contains(d);
	}
	
	
}
