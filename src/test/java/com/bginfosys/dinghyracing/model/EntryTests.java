/*
 * Copyright 2022-2024 BG Information Systems Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
   
package com.bginfosys.dinghyracing.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
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
		Competitor helm = new Competitor();
		Dinghy dinghy = new Dinghy();
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace();
		race.setFleet(fleet);
		
		Entry entry = new Entry(helm, dinghy, race);
		assertTrue(entry instanceof Entry);
		assertEquals(entry.getDinghy(), dinghy);
		assertEquals(entry.getHelm(), helm);
		assertEquals(entry.getRace(), race);
	}
	
	@Test
	void when_constructorCalledWithArgumentsAndRaceFleetDinghyClassIsNotNullAndDoesNotMatchDinghyDinghyClass_then_throwsDinghyClassMismatchException() {
		DinghyClass dc1 = new DinghyClass("Scorpion", 2, 1041);
		DinghyClass dc2 = new DinghyClass("Graduate", 2, 1100);
		
		Set<DinghyClass> dinghyClasses = new HashSet<DinghyClass>();
		dinghyClasses.add(dc2);
		Fleet fleet = new Fleet("Test Fleet", dinghyClasses);
		
		Competitor helm = new Competitor();
		Dinghy dinghy = new Dinghy();
		dinghy.setDinghyClass(dc1);
		DirectRace race = new DirectRace();
		race.setFleet(fleet);
		
		assertThrows(DinghyClassMismatchException.class, () -> {
			new Entry(helm, dinghy, race);	
		});
	}
	
	@Test
	void when_constructorCalledWithArgumentsAndRaceFleetDinghyClassIsNullAndDoesNotMatchDinghyDinghyClass_then_itInstantiatesAndSetsPropertyValues() {
		DinghyClass dc1 = new DinghyClass("Scorpion", 2, 1041);
		Competitor helm = new Competitor();
		Dinghy dinghy = new Dinghy();
		dinghy.setDinghyClass(dc1);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace();
		race.setFleet(fleet);
		
		Entry entry = new Entry(helm, dinghy, race);
		assertTrue(entry instanceof Entry);
		assertEquals(entry.getDinghy(), dinghy);
		assertEquals(entry.getHelm(), helm);
		assertEquals(entry.getRace(), race);
	}
	
	@Test
	void when_settingDinghyAndRaceIsNull_then_itSetsDinghy() {
		Entry entry = new Entry();
		Dinghy dinghy = new Dinghy();
		
		entry.setDinghy(dinghy);
		assertEquals(entry.getDinghy(), dinghy);
	}
	
	@Test
	void when_settingDinghyAndDinghyDinghyClassEqualsRaceFleetDinghyClass_then_itSetsDinghy() {
		DinghyClass dinghyClass = new DinghyClass("Scorpion", 2, 1041);
						
		Set<DinghyClass> dinghyClasses = new HashSet<DinghyClass>();
		dinghyClasses.add(dinghyClass);
		Fleet fleet = new Fleet("Test Fleet", dinghyClasses);
		
		DirectRace race = new DirectRace();
		race.setFleet(fleet);
		Entry entry = new Entry();
		Dinghy dinghy = new Dinghy();
		dinghy.setDinghyClass(dinghyClass);
		
		entry.setDinghy(dinghy);
		assertEquals(entry.getDinghy(), dinghy);
	}
	
	@Test
	void when_settingDinghyAndDinghyDinghyClassDoesNotMatchRaceFleetDinghyClass_then_throwsDinghyClassMismatchException() {
		DinghyClass dc1 = new DinghyClass("Scorpion", 2, 1041);
		DinghyClass dc2 = new DinghyClass("Graduate", 2, 1100);
		
		Set<DinghyClass> dinghyClasses = new HashSet<DinghyClass>();
		dinghyClasses.add(dc1);
		Fleet fleet = new Fleet("Test Fleet", dinghyClasses);

		DirectRace race = new DirectRace();
		race.setFleet(fleet);
		
		Dinghy dinghy = new Dinghy();
		dinghy.setDinghyClass(dc2);
		
		Entry entry = new Entry();
		entry.setRace(race);
		
		assertThrows(DinghyClassMismatchException.class, () -> {
			entry.setDinghy(dinghy);
		});
	}
		
	@Test
	void when_settingDinghyAndRaceFleetDinghyClassIsNull_then_setsDinghy() {
		DinghyClass dinghyClass = new DinghyClass("Scorpion", 2, 1041);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace();
		race.setFleet(fleet);
		Entry entry = new Entry();
		entry.setRace(race);
		Dinghy dinghy = new Dinghy();
		dinghy.setDinghyClass(dinghyClass);
		
		entry.setDinghy(dinghy);
		assertEquals(entry.getDinghy(), dinghy);
	}
	
	@Test
	void when_settingDinghyAndDinghyDinghyClassIsNotNullAndRaceisNull_then_setsDinghy() {
		DinghyClass dinghyClass = new DinghyClass("Scorpion", 2, 1041);
		Entry entry = new Entry();
		Dinghy dinghy = new Dinghy();
		dinghy.setDinghyClass(dinghyClass);
		
		entry.setDinghy(dinghy);
		assertEquals(entry.getDinghy(), dinghy);
	}
	
	@Test
	void when_settingHelm_then_itRecordsNewValue() {
		Entry entry = new Entry();
		Competitor helm = new Competitor();
		
		entry.setHelm(helm);
		assertEquals(entry.getHelm(), helm);
	}
	
	@Test
	void when_settingRaceAndRaceFleetDinghyClassIsNullAndDinghyIsNull_then_setsRace() {
		Entry entry = new Entry();
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace();
		race.setFleet(fleet);
		
		entry.setRace(race);
		assertEquals(entry.getRace(), race);
	}

	@Test
	void when_settingRaceAndRaceFleetDinghyClassIsNotNullAndDinghyIsNull_then_setsRace() {
		DinghyClass dinghyClass = new DinghyClass("Scorpion", 2, 1041);
		
		Set<DinghyClass> dinghyClasses = new HashSet<DinghyClass>();
		dinghyClasses.add(dinghyClass);
		Fleet fleet = new Fleet("Test Fleet", dinghyClasses);
		
		DirectRace race = new DirectRace();
		race.setFleet(fleet);
		
		Entry entry = new Entry();
		entry.setRace(race);
	}
	
	@Test
	void when_settingRaceAndRaceFleetDinghyClassIsNotNullAndMatchesDinghyDinghyClass_then_setsRace() {
		DinghyClass dinghyClass = new DinghyClass("Scorpion", 2, 1041);
		Dinghy dinghy = new Dinghy();
		dinghy.setDinghyClass(dinghyClass);
		
		Set<DinghyClass> dinghyClasses = new HashSet<DinghyClass>();
		dinghyClasses.add(dinghyClass);
		Fleet fleet = new Fleet("Test Fleet", dinghyClasses);
		
		DirectRace race = new DirectRace();
		race.setFleet(fleet);
		
		Entry entry = new Entry();
		entry.setDinghy(dinghy);
		entry.setRace(race);
	}
	
	@Test
	void when_settingRaceAndRaceFleetDinghyClassIsNotNullAndDinghyDinghyClassIsNotNullAndRaceFleetDinghyClassDoesNotMatchDinghyDinghyClass_then_throwsDinghyMismAtchException() {
		DinghyClass dc1 = new DinghyClass("Scorpion", 2, 1041);
		DinghyClass dc2 = new DinghyClass("Graduate", 2, 1100);
		Dinghy dinghy = new Dinghy();
		dinghy.setDinghyClass(dc1);
		
		Set<DinghyClass> dinghyClasses = new HashSet<DinghyClass>();
		dinghyClasses.add(dc2);
		Fleet fleet = new Fleet("Test Fleet", dinghyClasses);
		
		DirectRace race = new DirectRace();
		race.setFleet(fleet);
		
		Entry entry = new Entry();
		entry.setDinghy(dinghy);
		
		assertThrows(DinghyClassMismatchException.class, () -> {
			entry.setRace(race);
		});
	}
	
	@Test
	void when_settingRaceAndRaceFleetDinghyClassesIsEmpty_then_setsRace() {
		DinghyClass dinghyClass = new DinghyClass("Scorpion", 2, 1041);
		Dinghy dinghy = new Dinghy();
		dinghy.setDinghyClass(dinghyClass);
		
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace();
		race.setFleet(fleet);
		
		Entry entry = new Entry();
		entry.setDinghy(dinghy);
		entry.setRace(race);
	}
	
	@Test
	void when_raceAddedToEntry_the_updatesRaceToRecordEntry() {
		DirectRace race = new DirectRace();
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
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Entry entry = new Entry(competitor1, dinghy1, race);
		race.signUp(entry);
		Lap lap = new Lap(1, Duration.ofMinutes(13));
		entry.addLap(lap);
		assertTrue(entry.getLaps().contains(lap));
	}
	
	@Test
	void given_lapWithLapNumberAlreadyRecorded_when_addingALapWithSameLapNumber_then_LapIsNotAdded() {
		Entry entry = new Entry();
		DirectRace race = new DirectRace();
		race.setPlannedLaps(5);
		race.setType(RaceType.PURSUIT);
		entry.setRace(race);
		Lap lap1 = new Lap(1, Duration.ofMinutes(13));
		Lap lap1_2 = new Lap(1, Duration.ofMinutes(11));
		entry.addLap(lap1);
		entry.addLap(lap1_2);
		
		assertTrue(entry.getLaps().size() == 1);
		assertFalse(entry.getLaps().first().getNumber() == lap1_2.getNumber() && entry.getLaps().first().getTime() == lap1_2.getTime());
	}
	
	@Test
	void when_addingALap_then_returnsTrueIfLapIsAdded() {
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Entry entry = new Entry(competitor1, dinghy1, race);
		race.signUp(entry);
		Lap lap = new Lap(1, Duration.ofMinutes(13));
		boolean lapAdded = entry.addLap(lap);
		assertTrue(lapAdded);
	}
	
	@Test
	void when_addingALap_then_returnsFalseIfLapIsNotAdded() {
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Entry entry = new Entry(competitor1, dinghy1, race);
		race.signUp(entry);
		Lap lap1 = new Lap(1, Duration.ofMinutes(13));
		Lap lap1_2 = new Lap(1, Duration.ofMinutes(12));
		entry.addLap(lap1);
		boolean lapAdded = entry.addLap(lap1_2);
		assertFalse(lapAdded);
	}
	
	@Test
	void when_removingALap_then_lapIsRemoved() {
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Entry entry = new Entry(competitor1, dinghy1, race);
		race.signUp(entry);
		Lap lap = new Lap(1, Duration.ofMinutes(13));
		entry.addLap(lap);
		entry.removeLap(new Lap(1, Duration.ofMinutes(13)));
		assertFalse(entry.getLaps().contains(lap));
	}
	
	@Test
	void when_requestingTotalLapTime_then_returnsSumOfRecordedLapTimes() {
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Entry entry = new Entry(competitor1, dinghy1, race);
		race.signUp(entry);
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
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Entry entry = new Entry(competitor1, dinghy1, race);
		race.signUp(entry);
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
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Entry entry = new Entry(competitor1, dinghy1, race);
		race.signUp(entry);
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
		
		assertEquals(entry.getAverageLapTime(), Duration.ofSeconds(912));
	}
	
	@Test
	void given_anEntryHasLapsRecorded_then_updatesLastLap() {
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Entry entry = new Entry(competitor1, dinghy1, race);
		race.signUp(entry);
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
		
		Lap newLap5 = new Lap(5, Duration.ofMinutes(15));
		entry.updateLap(newLap5);
		// swapping out old and new laps was causing a referential integrity error after EntryController method completed :-(
//		assertEquals(newLap5, entry.getLaps().last());
		assertEquals(newLap5.getTime(), entry.getLaps().last().getTime());	
	}
	
	@Test
	void given_anEntryHasLapsRecorded_then_ifAttemptToUpdateLapOtherThanLastThrowsIllegalArgumentException() {
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Entry entry = new Entry(competitor1, dinghy1, race);
		race.signUp(entry);
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
		
		Lap newLap3 = new Lap(3, Duration.ofMinutes(14));
		assertThrows(IllegalArgumentException.class, () -> {
			entry.updateLap(newLap3);
		});	
	}

	@Test
	void when_settingCrew_then_itRecordsNewValue() {
		Entry entry = new Entry();
		Competitor crew = new Competitor();
		
		entry.setCrew(crew);
		assertEquals(entry.getCrew(), crew);
	}
	
	@Test
	void when_gettingCrew_then_returnsCompetitor() {
		Entry entry = new Entry();
		Competitor crew = new Competitor();
		
		entry.setCrew(crew);
		assertTrue(entry.getCrew() instanceof Competitor);
	}

	@Test
	void when_onLastLapOfRace_then_returnsTrueForOnLastLap() {
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 2, RaceType.FLEET, StartType.CSCCLUBSTART);
		Entry entry = new Entry(competitor1, dinghy1, race);
		race.signUp(entry);
		entry.addLap(new Lap(1, Duration.ofMinutes(3L)));
		
		assertTrue(entry.getOnLastLap());
	}
	
	@Test
	void when_notOnLastLapOfRace_then_returnsFalseForOnLastLap() {
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Entry entry = new Entry(competitor1, dinghy1, race);
		race.signUp(entry);
		entry.addLap(new Lap(1, Duration.ofMinutes(3L)));
		
		assertFalse(entry.getOnLastLap());
	}

	@Test
	void when_finishedRace_then_returnsTrueForFinishedRace() {
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 2, RaceType.FLEET, StartType.CSCCLUBSTART);
		Entry entry = new Entry(competitor1, dinghy1, race);
		race.signUp(entry);
		
		entry.addLap(new Lap(1, Duration.ofMinutes(3L)));
		entry.addLap(new Lap(2, Duration.ofMinutes(3L)));
		
		assertTrue(entry.getFinishedRace());
	}
	
	@Test 
	void when_notFinishedRace_then_returnsFalseForNotFinishedRace() {
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Entry entry = new Entry(competitor1, dinghy1, race);
		race.signUp(entry);
		entry.addLap(new Lap(1, Duration.ofMinutes(3L)));
		
		assertFalse(entry.getFinishedRace());
	}
	
	@Test
	void when_finishedRace_then_doesNotAddAdditionalLaps() {
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 2, RaceType.FLEET, StartType.CSCCLUBSTART);
		Entry entry = new Entry(competitor1, dinghy1, race);
		race.signUp(entry);
		entry.addLap(new Lap(1, Duration.ofMinutes(3L)));
		entry.addLap(new Lap(2, Duration.ofMinutes(3L)));
		
		Lap lap = new Lap(3, Duration.ofMinutes(3L));
		
		assertFalse(entry.addLap(lap));
		assertFalse(entry.getLaps().contains(lap));
	}

	@Test
	void setsAndGetsScoringAbbreviation() {
		DirectRace race = new DirectRace();
		Entry entry = new Entry();
		entry.setRace(race);
		race.setType(RaceType.FLEET);
		entry.setScoringAbbreviation("XYZ");
		
		assertTrue(entry.getScoringAbbreviation() == "XYZ");
	}

	@Test
	void when_DNS_thenDoesNotAddLaps() {
		DirectRace race = new DirectRace();
		race.setPlannedLaps(2);
		race.setType(RaceType.FLEET);
		
		Entry entry = new Entry();
		entry.setRace(race);
		entry.setScoringAbbreviation("DNS");
		
		Lap lap = new Lap(1, Duration.ofMinutes(1L));
		
		assertFalse(entry.addLap(lap));
		assertFalse(entry.getLaps().contains(lap));
	}
	
	@Test
	void when_RET_thenDoesNotAddLaps() {
		DirectRace race = new DirectRace();
		race.setPlannedLaps(2);
		race.setType(RaceType.FLEET);
		
		Entry entry = new Entry();
		entry.setRace(race);
		entry.setScoringAbbreviation("RET");
		
		Lap lap = new Lap(1, Duration.ofMinutes(1L));
		
		assertFalse(entry.addLap(lap));
		assertFalse(entry.getLaps().contains(lap));
	}
	
	@Test
	void when_DSQ_thenDoesNotAddLaps() {
		DirectRace race = new DirectRace();
		race.setPlannedLaps(2);
		race.setType(RaceType.FLEET);
		
		Entry entry = new Entry();
		entry.setRace(race);
		entry.setScoringAbbreviation("DSQ");
		
		Lap lap = new Lap(1, Duration.ofMinutes(1L));
		
		assertFalse(entry.addLap(lap));
		assertFalse(entry.getLaps().contains(lap));
	}

	@Test
	void given_hasNotSailedALap_then_returnsZeroFtoNumberOfLapsSailed() {
		Entry entry = new Entry();
		DirectRace race = new DirectRace();
		race.setPlannedLaps(5);
		entry.setRace(race);
		
		assertEquals(entry.getLapsSailed(), 0);
	}
	
	@Test
	void given_hasSailedTwoLaps_then_returnsNumberOfLapsSailed() {
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Entry entry = new Entry(competitor1, dinghy1, race);
		race.signUp(entry);
		Lap lap1 = new Lap(1, Duration.ofMinutes(13));
		Lap lap2 = new Lap(2, Duration.ofMinutes(16));
		entry.addLap(lap1);
		entry.addLap(lap2);
		
		assertEquals(2, entry.getLapsSailed());
	}

	@Test
	void when_lapAdded_then_calculatesPositionsOfEntriesInRace() {
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 2, RaceType.FLEET, StartType.CSCCLUBSTART);
		Entry entry = new Entry(competitor1, dinghy1, race);
		race.signUp(entry);
		Lap lap1 = new Lap(1, Duration.ofMinutes(13));
		entry.addLap(lap1);
		
		assertEquals(1, entry.getPosition());
	}

	@Test
	void when_lapRemoved_then_calculatesPositionsOfEntriesInRace() {
		Competitor competitor1 = new Competitor("Bob");
		Competitor competitor2 = new Competitor("Joan");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("5678", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		race.signUp(entry1);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		race.signUp(entry2);
		entry1.addLap(new Lap(1, Duration.ofMinutes(12)));
		entry2.addLap(new Lap(1, Duration.ofMinutes(13)));
		entry1.addLap(new Lap(2, Duration.ofMinutes(13)));
		entry2.addLap(new Lap(2, Duration.ofMinutes(13)));
		
		entry1.removeLap(new Lap(2, Duration.ofMinutes(13)));
		
		assertEquals(2, entry1.getPosition());
	}

	@Test
	void when_lapUpdated_then_calculatesPositionsOfEntriesInRace() {
		Competitor competitor1 = new Competitor("Bob");
		Competitor competitor2 = new Competitor("Joan");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("5678", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		race.signUp(entry1);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		race.signUp(entry2);
		entry1.addLap(new Lap(1, Duration.ofMinutes(12)));
		entry1.addLap(new Lap(2, Duration.ofMinutes(13)));
		entry2.addLap(new Lap(1, Duration.ofMinutes(13)));
		entry2.addLap(new Lap(2, Duration.ofMinutes(13)));
		
		entry1.updateLap(new Lap(2, Duration.ofMinutes(15)));
		
		assertEquals(2, entry1.getPosition());
	}
	
	@Test
	void when_scoringAbrreviationIsSet_then_recalculatesPositionOfEntriesInRace() {
		Competitor competitor1 = new Competitor("Bob");
		Competitor competitor2 = new Competitor("Joan");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("5678", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		race.signUp(entry1);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		race.signUp(entry2);
		entry1.addLap(new Lap(1, Duration.ofMinutes(12)));
		entry1.addLap(new Lap(2, Duration.ofMinutes(13)));
		entry2.addLap(new Lap(1, Duration.ofMinutes(13)));
		entry2.addLap(new Lap(2, Duration.ofMinutes(13)));
		
		entry1.setScoringAbbreviation("RET");
		
		assertEquals(2, entry1.getPosition());
	}
}
