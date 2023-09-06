package com.bginfosys.dinghyracing.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import org.junit.jupiter.api.Test;

import com.bginfosys.dinghyracing.exceptions.DinghyClassMismatchException;

public class EntryTests {
	
	@Test
	void when_emptyConstructorCalled_then_itInstantiates() {
		Entry entry = new Entry();
		
		assertTrue(entry instanceof Entry);
	}
	
	@Test
	void when_constructorCalledWithArguments_then_itInstantiatesAndSetsPropertyValues() {
		Competitor competitor = new Competitor();
		Dinghy dinghy = new Dinghy();
		Race race = new Race();
		
		Entry entry = new Entry(competitor, dinghy, race);
		assertTrue(entry instanceof Entry);
		assertEquals(entry.getDinghy(), dinghy);
		assertEquals(entry.getCompetitor(), competitor);
		assertEquals(entry.getRace(), race);
	}
	
	@Test
	void when_constructorCalledWithArgumentsAndRaceDinghyClassIsNotNullAndDoesNotMatchDinghyDinghyClass_then_throwsDinghyClassMismatchException() {
		DinghyClass dc1 = new DinghyClass("Scorpion");
		DinghyClass dc2 = new DinghyClass("Graduate");
		Competitor competitor = new Competitor();
		Dinghy dinghy = new Dinghy();
		dinghy.setDinghyClass(dc1);
		Race race = new Race();
		race.setDinghyClass(dc2);
		
		assertThrows(DinghyClassMismatchException.class, () -> {
			new Entry(competitor, dinghy, race);	
		});
	}
	
	@Test
	void when_constructorCalledWithArgumentsAndRaceDinghyClassIsNullAndDoesNotMatchDinghyDinghyClass_then_itInstantiatesAndSetsPropertyValues() {
		DinghyClass dc1 = new DinghyClass("Scorpion");
		Competitor competitor = new Competitor();
		Dinghy dinghy = new Dinghy();
		dinghy.setDinghyClass(dc1);
		Race race = new Race();
		
		Entry entry = new Entry(competitor, dinghy, race);
		assertTrue(entry instanceof Entry);
		assertEquals(entry.getDinghy(), dinghy);
		assertEquals(entry.getCompetitor(), competitor);
		assertEquals(entry.getRace(), race);
	}
	// Set dinghy tests
	@Test
	void when_settingDinghyAndRaceIsNull_then_itSetsDinghy() {
		Entry entry = new Entry();
		Dinghy dinghy = new Dinghy();
		
		entry.setDinghy(dinghy);
		assertEquals(entry.getDinghy(), dinghy);
	}
	
	@Test
	void when_settingDinghyAndDinghyDinghyClassEqualsRaceDinghyClass_then_itSetsDinghy() {
		DinghyClass dinghyClass = new DinghyClass("Scorpion");
		Race race = new Race();
		race.setDinghyClass(dinghyClass);
		Entry entry = new Entry();
		Dinghy dinghy = new Dinghy();
		dinghy.setDinghyClass(dinghyClass);
		
		entry.setDinghy(dinghy);
		assertEquals(entry.getDinghy(), dinghy);
	}
	
	@Test
	void when_settingDinghyAndDinghyDinghyClassDoesNotMatchRaceDinghyClass_then_throwsDinghyClassMismatchException() {
		DinghyClass dc1 = new DinghyClass("Scorpion");
		DinghyClass dc2 = new DinghyClass("Graduate");
		Race race = new Race();
		race.setDinghyClass(dc1);
		
		Dinghy dinghy = new Dinghy();
		dinghy.setDinghyClass(dc2);
		
		Entry entry = new Entry();
		entry.setRace(race);
		
		assertThrows(DinghyClassMismatchException.class, () -> {
			entry.setDinghy(dinghy);
		});
	}
	
	@Test
	void when_settingDinghyAndRaceDinghyClassIsNull_then_setsDinghy() {
		DinghyClass dinghyClass = new DinghyClass("Scorpion");
		Race race = new Race();
		Entry entry = new Entry();
		entry.setRace(race);
		Dinghy dinghy = new Dinghy();
		dinghy.setDinghyClass(dinghyClass);
		
		entry.setDinghy(dinghy);
		assertEquals(entry.getDinghy(), dinghy);
	}
	
	@Test
	void when_settingDinghyAndDinghyDinghyClassIsNotNullAndRaceisNull_then_setsDinghy() {
		DinghyClass dinghyClass = new DinghyClass("Scorpion");
		Entry entry = new Entry();
		Dinghy dinghy = new Dinghy();
		dinghy.setDinghyClass(dinghyClass);
		
		entry.setDinghy(dinghy);
		assertEquals(entry.getDinghy(), dinghy);
	}
	// Set competitor tests
	@Test
	void when_settingCompetitor_then_itRecordsNewValue() {
		Entry entry = new Entry();
		Competitor competitor = new Competitor();
		
		entry.setCompetitor(competitor);
		assertEquals(entry.getCompetitor(), competitor);
	}
	// Set race tests
	@Test
	void when_settingRaceAndRaceDinghyClassIsNullAndDinghyIsNull_then_setsRace() {
		Entry entry = new Entry();
		Race race = new Race();
		
		entry.setRace(race);
		assertEquals(entry.getRace(), race);
	}

	@Test
	void when_settingRaceAndRaceDinghyClassIsNotNullAndDinghyIsNull_then_setsRace() {
		DinghyClass dinghyClass = new DinghyClass("Scorpion");
		Race race = new Race();
		race.setDinghyClass(dinghyClass);
		
		Entry entry = new Entry();
		entry.setRace(race);
	}
	
	@Test
	void when_settingRaceAndRaceDinghyClassIsNotNullAndMatchesDinghyDinghyClass_then_setsRace() {
		DinghyClass dinghyClass = new DinghyClass("Scorpion");
		Dinghy dinghy = new Dinghy();
		dinghy.setDinghyClass(dinghyClass);
		Race race = new Race();
		race.setDinghyClass(dinghyClass);
		
		Entry entry = new Entry();
		entry.setDinghy(dinghy);
		entry.setRace(race);
	}
	
	@Test
	void when_settingRaceAndRaceDinghyClassIsNotNullAndDinghyDinghyClassIsNotNullAndRaceDinghyClassDoesNotMatchDinghyDinghyClass_then_throwsDinghyMismAtchException() {
		DinghyClass dc1 = new DinghyClass("Scorpion");
		DinghyClass dc2 = new DinghyClass("Graduate");
		Dinghy dinghy = new Dinghy();
		dinghy.setDinghyClass(dc1);
		Race race = new Race();
		race.setDinghyClass(dc2);
		
		Entry entry = new Entry();
		entry.setDinghy(dinghy);
		
		assertThrows(DinghyClassMismatchException.class, () -> {
			entry.setRace(race);
		});
	}
	
	@Test
	void when_settingRaceAndRaceDinghyClassIsNullAndDoesNotMatchDinghyDinghyClass_then_setsRace() {
		DinghyClass dinghyClass = new DinghyClass("Scorpion");
		Dinghy dinghy = new Dinghy();
		dinghy.setDinghyClass(dinghyClass);
		Race race = new Race();
		
		Entry entry = new Entry();
		entry.setDinghy(dinghy);
		entry.setRace(race);
	}
	
	@Test
	void when_raceAddedToEntry_the_updatesRaceToRecordEntry() {
		Race race = new Race();
		Entry entry = new Entry();
		entry.setRace(race);
		assertThat(race.getSignedUp(), hasItem(entry));
	}
	
	@Test
	void when_requestingLaps_then_getSet() {
		Entry entry = new Entry();
		entry.setLaps(new ConcurrentSkipListSet<Lap>());
		assertTrue(entry.getLaps() instanceof SortedSet<?>);
	}
	
	@Test
	void when_addingALap_then_lapIsAddedToLaps() {
		Entry entry = new Entry();
		Lap lap = new Lap(1, Duration.ofMinutes(13));
		entry.addLap(lap);
		assertTrue(entry.getLaps().contains(lap));
	}
	
	@Test
	void when_removingALap_then_lapIsRemoved() {
		Entry entry = new Entry();
		Lap lap = new Lap(1, Duration.ofMinutes(13));
		entry.addLap(lap);
		entry.removeLap(new Lap(1, Duration.ofMinutes(13)));
		assertFalse(entry.getLaps().contains(lap));
	}
	
	@Test
	void when_requestingTotalLapTime_then_returnsSumOfRecordedLapTimes() {
		Entry entry = new Entry();
		Lap lap1 = new Lap(1, Duration.ofMinutes(13));
		Lap lap2 = new Lap(2, Duration.ofMinutes(16));
		Lap lap3 = new Lap(3, Duration.ofMinutes(15));
		Lap lap4 = new Lap(4, Duration.ofMinutes(18));
		Lap lap5 = new Lap(5, Duration.ofMinutes(14));
		
		entry.addLap(lap1);
		entry.addLap(lap2);
		entry.addLap(lap3);
		entry.addLap(lap4);
		entry.addLap(lap5);
		
		assertEquals(entry.getSumOfLapTimes(), Duration.ofMinutes(76));
	}
	
	@Test
	void given_anEntryHasCompletedNoLaps_when_requestingTotalLapTime_then_returnsADurationOfZero() {
		Entry entry = new Entry();
		
		assertEquals(entry.getSumOfLapTimes(), Duration.ofMinutes(0));
	}
	
	@Test
	void given_anEntryHasCompletedMoreThanOneLap_then_returnsTheLastLapTime() {
		Entry entry = new Entry();
		Lap lap1 = new Lap(1, Duration.ofMinutes(13));
		Lap lap2 = new Lap(2, Duration.ofMinutes(16));
		Lap lap3 = new Lap(3, Duration.ofMinutes(15));
		Lap lap4 = new Lap(4, Duration.ofMinutes(18));
		Lap lap5 = new Lap(5, Duration.ofMinutes(14));
		
		entry.addLap(lap1);
		entry.addLap(lap3);
		entry.addLap(lap5);
		entry.addLap(lap2);
		entry.addLap(lap4);
		
		assertEquals(entry.getLastLapTime(), Duration.ofMinutes(14));
	}
	
	@Test
	void given_entryHasCompletedNoLaps_then_givesAverageLapTimeDurationOfZero() {
		Entry entry = new Entry();
		
		assertEquals(entry.getAverageLapTime(), Duration.ofMinutes(0));
	}
	
	@Test
	void given_anEntryHasCompletedMoreThanOneLap_then_returnsTheAverageLapTime() {
		Entry entry = new Entry();
		Lap lap1 = new Lap(1, Duration.ofMinutes(13));
		Lap lap2 = new Lap(2, Duration.ofMinutes(16));
		Lap lap3 = new Lap(3, Duration.ofMinutes(15));
		Lap lap4 = new Lap(4, Duration.ofMinutes(18));
		Lap lap5 = new Lap(5, Duration.ofMinutes(14));
		
		entry.addLap(lap1);
		entry.addLap(lap3);
		entry.addLap(lap5);
		entry.addLap(lap2);
		entry.addLap(lap4);
		
		assertEquals(entry.getAverageLapTime(), Duration.ofSeconds(912));
	}
}
