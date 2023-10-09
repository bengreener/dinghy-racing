package com.bginfosys.dinghyracing.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CompetitorTests {

	@Test
	void when_emptyConstructorCalled_then_itInstantiates() {
		Competitor competitor = new Competitor();
		
		assertTrue(competitor instanceof Competitor);
	}
	
	@Test
	void when_ConstructorCalled_then_itInstantiatesAndSetsPropertyValues() {
		Competitor competitor = new Competitor("Some Name");
		
		assertTrue(competitor instanceof Competitor);
		assertEquals(competitor.getName(), "Some Name");
	}
	
	@Test
	void when_settingNewName_then_itRecordsNewValue() {
		Competitor competitor = new Competitor();
		competitor.setName("Some Name");
		
		assertEquals(competitor.getName(), "Some Name");
	}
}
