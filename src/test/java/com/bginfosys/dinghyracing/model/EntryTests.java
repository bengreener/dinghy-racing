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

import com.bginfosys.dinghyracing.exceptions.EntryHasFinishedRaceException;
import com.bginfosys.dinghyracing.exceptions.EntryHasScoringAbbreviationException;
import com.bginfosys.dinghyracing.exceptions.EntryLapsSailedException;

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
		race.signUp(helm, dinghy);
		Entry entry = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(helm)).findFirst().get().getEntry();
		
		assertTrue(entry instanceof Entry);
		assertEquals(entry.getDinghy(), dinghy);
		assertEquals(entry.getHelm(), helm);
		assertEquals(entry.getSignedUpTo().stream().findFirst().get().getRace(), race);
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
		
		Race race = new DirectRace();
		race.setFleet(fleet);
		Entry entry = new Entry();
		Dinghy dinghy = new Dinghy();
		dinghy.setDinghyClass(dinghyClass);
		
		entry.setDinghy(dinghy);
		assertEquals(entry.getDinghy(), dinghy);
	}
		
	@Test
	void when_settingDinghyAndRaceFleetDinghyClassIsNull_then_setsDinghy() {
		DinghyClass dinghyClass = new DinghyClass("Scorpion", 2, 1041);
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new DirectRace();
		race.setFleet(fleet);
		Entry entry = new Entry();
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
	
	// context: Entry
	// inv: dinghy_class_allowed_by_race_fleet
	@Test
	void when_settingRaceAndRaceFleetDinghyClassIsNullAndDinghyIsNull_then_setsRace() {
		Entry entry = new Entry();
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new DirectRace();
		race.setFleet(fleet);
		
		SignedUp signedUp = new SignedUp(race, entry);
		Set<SignedUp> signedUpTo = new HashSet<SignedUp>(64);
		signedUpTo.add(signedUp);
		entry.setSignedUpTo(signedUpTo);
		race.setSignedUp(signedUpTo);
		
		assertEquals(entry.getSignedUpTo().stream().findFirst().get().getRace(), race);
	}
	
	@Test
	void when_requestingLaps_then_getSet() {
		Entry entry = new Entry();
		entry.setLaps(new ConcurrentSkipListSet<Lap>());
		assertTrue(entry.getLaps() instanceof SortedSet<?>);
	}
	
	// addLap
	@Test
	void when_addingALap_then_lapIsAddedToLaps() {
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART, false);
		race.signUp(competitor1, dinghy1);
		Entry entry = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get().getEntry();
		Lap lap = new Lap(1, Duration.ofMinutes(13));
		entry.addLap(lap);
		
		assertTrue(entry.getLaps().contains(lap));
	}
	
	@Test
	void given_lapWithLapNumberAlreadyRecorded_when_addingALapWithSameLapNumber_then_LapIsNotAdded() {
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART, false);
		race.signUp(competitor1, dinghy1);
		Entry entry = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get().getEntry();
		Lap lap1 = new Lap(1, Duration.ofMinutes(13));
		Lap lap1_2 = new Lap(1, Duration.ofMinutes(11));
		entry.addLap(lap1);
		
		assertThrows(EntryLapsSailedException.class, () -> entry.addLap(lap1_2));
	}
	
	@Test
	void given_entryHasFinishedRace_when_addLap_then_throwsEntryHasFinishedRaceException() {
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 1, RaceType.FLEET, StartType.CSCCLUBSTART, false);
		race.signUp(competitor1, dinghy1);
		Entry entry = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get().getEntry();
		entry.addLap(new Lap(1, Duration.ofMinutes(13)));
		entry.setFinishedRace(true);
		assertThrows(EntryHasFinishedRaceException.class, () -> entry.addLap(new Lap(2, Duration.ofMinutes(12))));
	}
	
	@Test
	void when_DNS_thenDoesNotAddLaps() {
		Competitor competitor = new Competitor("Sid");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 2, RaceType.FLEET, StartType.CSCCLUBSTART, false);
		race.signUp(competitor, dinghy);
		Entry entry = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor)).findFirst().get().getEntry();
		entry.setScoringAbbreviation("DNS");
		assertThrows(EntryHasScoringAbbreviationException.class, () -> entry.addLap(new Lap(1, Duration.ofMinutes(1L))));
	}
	
	@Test
	void when_RET_thenDoesNotAddLaps() {
		Competitor competitor = new Competitor("Sid");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 2, RaceType.FLEET, StartType.CSCCLUBSTART, false);
		race.signUp(competitor, dinghy);
		Entry entry = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor)).findFirst().get().getEntry();
		entry.setScoringAbbreviation("RET");
		assertThrows(EntryHasScoringAbbreviationException.class, () -> entry.addLap(new Lap(1, Duration.ofMinutes(1L))));
	}
	
	@Test
	void when_DSQ_thenDoesNotAddLaps() {
		Competitor competitor = new Competitor("Sid");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 2, RaceType.FLEET, StartType.CSCCLUBSTART, false);
		race.signUp(competitor, dinghy);
		Entry entry = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor)).findFirst().get().getEntry();
		entry.setScoringAbbreviation("DSQ");
		assertThrows(EntryHasScoringAbbreviationException.class, () -> entry.addLap(new Lap(1, Duration.ofMinutes(1L))));
	}
	
	// removeLap
	@Test
	void when_removingALap_then_lapIsRemoved() {
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART, false);
		race.signUp(competitor1, dinghy1);
		Entry entry = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get().getEntry();
		Lap lap = new Lap(1, Duration.ofMinutes(13));
		entry.addLap(lap);
		entry.removeLap(new Lap(1, Duration.ofMinutes(13)));
		assertFalse(entry.getLaps().contains(lap));
	}
	
	// sumOfLapTimes
	@Test
	void when_requestingTotalLapTime_then_returnsSumOfRecordedLapTimes() {
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART, false);
		race.signUp(competitor1, dinghy1);
		Entry entry = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get().getEntry();
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
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART, false);
		race.signUp(competitor1, dinghy1);
		Entry entry = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get().getEntry();
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
	
	// averageLapTime
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
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART, false);
		race.signUp(competitor1, dinghy1);
		Entry entry = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get().getEntry();
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
	
	// updateLap
	@Test
	void given_anEntryHasLapsRecorded_then_updatesLastLap() {
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART, false);
		race.signUp(competitor1, dinghy1);
		Entry entry = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get().getEntry();
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
		assertEquals(newLap5.getTime(), entry.getLaps().last().getTime());
	}
	
	@Test
	void given_anEntryHasLapsRecorded_then_ifAttemptToUpdateLapOtherThanLastThrowsIllegalArgumentException() {
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART, false);
		race.signUp(competitor1, dinghy1);
		Entry entry = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get().getEntry();
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

	// setCrew
	@Test
	void when_settingCrew_then_itRecordsNewValue() {
		Entry entry = new Entry();
		Competitor crew = new Competitor();
		
		entry.setCrew(crew);
		assertEquals(entry.getCrew(), crew);
	}
	
	// getCrew
	@Test
	void when_gettingCrew_then_returnsCompetitor() {
		Entry entry = new Entry();
		Competitor crew = new Competitor();
		
		entry.setCrew(crew);
		assertTrue(entry.getCrew() instanceof Competitor);
	}
	
	// scoringAbbreviation
	@Test
	void setsAndGetsScoringAbbreviation() {
		Competitor competitor = new Competitor("Sid");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 2, RaceType.FLEET, StartType.CSCCLUBSTART, false);
		race.signUp(competitor, dinghy);
		Entry entry = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor)).findFirst().get().getEntry();
		race.setType(RaceType.FLEET);
		entry.setScoringAbbreviation("XYZ");
		
		assertTrue(entry.getScoringAbbreviation() == "XYZ");
	}


	// lapsSailed
	@Test
	void given_hasNotSailedALap_then_returnsZeroForNumberOfLapsSailed() {
		Entry entry = new Entry();
		DirectRace race = new DirectRace();
		race.setPlannedLaps(5);
		
		assertEquals(entry.getLapsSailed(), 0);
	}
	
	@Test
	void given_hasSailedTwoLaps_then_returnsNumberOfLapsSailed() {
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART, false);
		race.signUp(competitor1, dinghy1);
		Entry entry = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get().getEntry();
		Lap lap1 = new Lap(1, Duration.ofMinutes(13));
		Lap lap2 = new Lap(2, Duration.ofMinutes(16));
		entry.addLap(lap1);
		entry.addLap(lap2);
		
		assertEquals(2, entry.getLapsSailed());
	}
	
	@Test
	void given_raceIsAPursuitRace_when_lapUpdated_then_updatesCorrectedTime() {
		Competitor competitor1 = new Competitor("Bob");
		Competitor competitor2 = new Competitor("Joan");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("5678", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART, false);

		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofMinutes(12)));
		signedUp1.getEntry().addLap(new Lap(2, Duration.ofMinutes(13)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofMinutes(13)));
		signedUp2.getEntry().addLap(new Lap(2, Duration.ofMinutes(13)));
		
		signedUp1.getEntry().updateLap(new Lap(2, Duration.ofMinutes(15)));
		
		assertEquals(Duration.ofMinutes(27), signedUp1.getCorrectedTime());
	}
	
	@Test
	void when_scoringAbbreviationIsSet_then_recalculatesPositionOfEntriesInRace() {
		Competitor competitor1 = new Competitor("Bob");
		Competitor competitor2 = new Competitor("Joan");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("5678", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART, false);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofMinutes(12)));
		signedUp1.getEntry().addLap(new Lap(2, Duration.ofMinutes(13)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofMinutes(13)));
		signedUp2.getEntry().addLap(new Lap(2, Duration.ofMinutes(13)));
		signedUp1.getEntry().setScoringAbbreviation("RET");
		assertEquals(2, signedUp1.getPosition());
	}
	
	@Test
	void givenEntryHasNoLaps_when_aSetOfFinalLapsIsSet_then_entryIsRecordedAsSailingLaps() {
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART, false);
		race.signUp(competitor1, dinghy1);
		Entry entry = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get().getEntry();
		SortedSet<Lap> laps = new ConcurrentSkipListSet<Lap>();
		laps.add(new Lap(1, Duration.ofMinutes(5)));
		laps.add(new Lap(2, Duration.ofMinutes(5)));
		laps.add(new Lap(3, Duration.ofMinutes(5)));
		laps.add(new Lap(4, Duration.ofMinutes(5)));
		laps.add(new Lap(5, Duration.ofMinutes(5)));
		entry.setFinalLaps(laps);
		
		assertEquals(entry.getSumOfLapTimes(), Duration.ofMinutes(25));
		assertEquals(entry.getLapsSailed(), 5);
	}

	@Test
	void givenEntryHasNoLapsAndRaceHasThreePlannedLaps_when_aSetOfFinalLapsIsSetThatContainsFourLaps_then_throwsEntryMaxLapsSailedException() {
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 3, RaceType.FLEET, StartType.CSCCLUBSTART, false);
		race.signUp(competitor1, dinghy1);
		Entry entry = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get().getEntry();
		SortedSet<Lap> laps = new ConcurrentSkipListSet<Lap>();
		laps.add(new Lap(1, Duration.ofMinutes(5)));
		laps.add(new Lap(2, Duration.ofMinutes(5)));
		laps.add(new Lap(3, Duration.ofMinutes(5)));
		laps.add(new Lap(4, Duration.ofMinutes(5)));
		
		assertThrows(EntryLapsSailedException.class, () -> entry.setFinalLaps(laps));
	}
	
	@Test
	void givenEntryHasSailedLaps_when_aSetOfFinalLapsIsSet_then_entryIsRecordedAsSailingLapsOnlyLapsInFinalSet() {
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART, false);
		race.signUp(competitor1, dinghy1);
		Entry entry = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get().getEntry();
		SortedSet<Lap> laps1 = new ConcurrentSkipListSet<Lap>();
		laps1.add(new Lap(1, Duration.ofMinutes(5)));
		laps1.add(new Lap(2, Duration.ofMinutes(5)));
		entry.setLaps(laps1);
		
		SortedSet<Lap> laps2 = new ConcurrentSkipListSet<Lap>();
		laps2.add(new Lap(1, Duration.ofMinutes(1)));
		laps2.add(new Lap(2, Duration.ofMinutes(2)));
		laps2.add(new Lap(3, Duration.ofMinutes(3)));
		entry.setFinalLaps(laps2);
		
		assertEquals(entry.getSumOfLapTimes(), Duration.ofMinutes(6));
		assertEquals(entry.getLapsSailed(), 3);
	}
	
	@Test
	void givenEntryHasSailedLaps_when_aEmptySetOfFinalLapsIsSet_then_entryIsRecordedAsSailingNoLaps() {
		Competitor competitor1 = new Competitor("Bob");
		DinghyClass graduate = new DinghyClass("Graduate", 2 , 1110);
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Race1", LocalDateTime.now(), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART, false);
		race.signUp(competitor1, dinghy1);
		Entry entry = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get().getEntry();
		SortedSet<Lap> laps1 = new ConcurrentSkipListSet<Lap>();
		laps1.add(new Lap(1, Duration.ofMinutes(5)));
		laps1.add(new Lap(2, Duration.ofMinutes(5)));
		entry.setLaps(laps1);
		
		SortedSet<Lap> laps2 = new ConcurrentSkipListSet<Lap>();
		entry.setFinalLaps(laps2);
		
		assertEquals(entry.getSumOfLapTimes(), Duration.ofMinutes(0));
		assertEquals(entry.getLapsSailed(), 0);
	}
	
}
