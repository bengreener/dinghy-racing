package com.bginfosys.dinghyracing.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class RaceTests {

	@Test
	void when_idIsSet_then_returnsId() {
		Race race = new Race();
		race.setId(1L);
		
		assertEquals(race.getId(), 1L);
	}
}
