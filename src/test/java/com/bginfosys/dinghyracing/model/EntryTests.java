package com.bginfosys.dinghyracing.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class EntryTests {
	
	@Test
	void when_emptyConstructorCalled_then_itInstantiates() {
		Entry entry = new Entry();
		
		assertTrue(entry instanceof Entry);
	}
	
	@Test
	void when_constructorCalled_then_itInstantiatesAndSetsPropertyValues() {
		Competitor competitor = new Competitor();
		Dinghy dinghy = new Dinghy();
		
		Entry entry = new Entry(competitor, dinghy);
		assertTrue(entry instanceof Entry);
		assertEquals(entry.getDinghy(), dinghy);
		assertEquals(entry.getCompetitor(), competitor);
	}

	@Test
	void when_settingDinghy_then_itRecordsNewValue() {
		Entry entry = new Entry();
		Dinghy dinghy = new Dinghy();
		
		entry.setDinghy(dinghy);
		assertEquals(entry.getDinghy(), dinghy);
	}
	
	@Test
	void when_settingCompetitor_then_itRecordsNewValue() {
		Entry entry = new Entry();
		Competitor competitor = new Competitor();
		
		entry.setCompetitor(competitor);
		assertEquals(entry.getCompetitor(), competitor);
	}
}
