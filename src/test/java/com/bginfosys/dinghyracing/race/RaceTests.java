package com.bginfosys.dinghyracing.race;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

import com.bginfosys.dinghyracing.race.Race;

import java.time.LocalDate;
import java.time.LocalTime;

@SpringBootTest
class RaceTests {

	private Race race = new Race();;
	
	@Test
	void raceCreated() {
		assertThat(race).isNotNull();
	}
	
	@Test
	void setDate() {
		race.setDate(LocalDate.of(2021, 9, 27));
		assertEquals(race.getDate(), LocalDate.of(2021, 9, 27));
	}
	
	@Test
	void setPlannedStartTime() {
		race.setPlannedStartTime(LocalTime.of(16, 47));
		assertEquals(race.getPlannedStartTime(), LocalTime.of(16, 47));
	}
	
}
