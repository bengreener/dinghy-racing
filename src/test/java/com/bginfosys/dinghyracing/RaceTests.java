package com.bginfosys.dinghyracing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

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
	
}
