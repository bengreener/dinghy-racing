package com.bginfosys.dinghyracing;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RaceTests {

	//@Autowired
	private Race race;
	
	@Test
	public void raceCreated() {
		race = new Race();
		assertThat(race).isNotNull();
	}
	
}
