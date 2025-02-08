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

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

class RaceTests {

	//private Race race = new Race("Test Race", LocalDate.of(2021, 10, 14), LocalTime.of(14, 10), new DinghyClass("Test"));
	private DinghyClass dinghyClass = new DinghyClass("Test", 1, 1000);
	private Set<DinghyClass> dinghyClasses = new HashSet<DinghyClass>();
	
	Fleet fleet = new Fleet("Test Fleet", dinghyClasses);
	private Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
	
	RaceTests() {
		dinghyClasses.add(dinghyClass);	
	}
	
	@Test
	void raceCreated() {
		assertThat(race, notNullValue());
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
	void setDuration() {
		race.setDuration(Duration.ofMillis(1000));
		assertEquals(race.getDuration(), Duration.ofSeconds(1));
	}

	@Test
	void plannedStartTimeIsLocalDateTime() {
		race.setPlannedStartTime(LocalDateTime.of(2021, 9, 27, 16, 47));
		assertTrue(race.getPlannedStartTime() instanceof LocalDateTime);
	}
	
	@Test
	void when_entryAddedViaSignUp_the_addedToSignedUp() {
		Race race = new Race();
		Entry entry = new Entry();
		race.signUp(entry);
		assertThat(race.getSignedUp(), hasItem(entry));
	}

	@Test
	void when_creating_Race_then_setPlannedLaps () {
		race.setPlannedLaps(3);
		assertEquals(race.getPlannedLaps(), 3);
	}
	
	@Test
	void when_requestNumberOfLapsCompletedByLeadEntry_then_returnsInteger() {
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		
		race.signUp(entry1);
		race.signUp(entry2);
		
		entry1.addLap(new Lap(1, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(1, Duration.ofMinutes(15)));
		entry1.addLap(new Lap(2, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(2, Duration.ofMinutes(15)));
		
		assertTrue(race.leadEntrylapsCompleted() instanceof Integer);		
	}
	
	@Test
	void given_noLapsCompleted_when_requestNumberOfLapsCompletedByLeadEntry_then_returnsZero() {
		Competitor competitor1 = new Competitor("Competitor One");
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		
		race.signUp(entry1);
				
		assertEquals(0, race.leadEntrylapsCompleted());		
	}
	
	@Test
	void given_TwoEntriesHaveCompletedTheSameNumberOfLaps_when_requestNumberOfLapsCompletedByLeadEntry_then_returnsNumberOFLaps() {
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		
		race.signUp(entry1);
		race.signUp(entry2);
		
		entry1.addLap(new Lap(1, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(1, Duration.ofMinutes(15)));
		entry1.addLap(new Lap(2, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(2, Duration.ofMinutes(15)));
		
		assertEquals(race.leadEntrylapsCompleted(), 2);		
	}

	@Test
	void given_OneEntryHasCompletedMoreLaps_when_requestNumberOfLapsCompletedByLeadEntry_then_returnsNumberOfLaps() {
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		
		race.signUp(entry1);
		race.signUp(entry2);
		
		entry1.addLap(new Lap(1, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(1, Duration.ofMinutes(15)));
		entry1.addLap(new Lap(2, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(2, Duration.ofMinutes(15)));
		entry2.addLap(new Lap(3, Duration.ofMinutes(15)));	
		
		assertEquals(3, race.leadEntrylapsCompleted());		
	}
	
	@Test
	void given_TwoEntriesHaveCompletedTheSameNumberOfLaps_when_requestLeadEntryInRace_then_returnsLeadEntryInRace() {
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		
		race.signUp(entry1);
		race.signUp(entry2);
		
		entry1.addLap(new Lap(1, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(1, Duration.ofMinutes(15)));
		entry1.addLap(new Lap(2, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(2, Duration.ofMinutes(15)));
		
		assertEquals(entry1, race.getLeadEntry());		
	}
	
	@Test
	void given_OneEntryHasCompletedMoreLaps_when_requestLeadEntryInRace_then_returnsLeadEntryInRace() {
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		
		race.signUp(entry1);
		race.signUp(entry2);
		
		entry1.addLap(new Lap(1, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(1, Duration.ofMinutes(15)));
		entry1.addLap(new Lap(2, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(2, Duration.ofMinutes(15)));
		entry2.addLap(new Lap(3, Duration.ofMinutes(15)));		
		
		assertEquals(entry2, race.getLeadEntry());
	}
	
	@Test
	void given_NoBoatsHaveSignedUp_when_requestLeadEntryInRace_then_returnsNull() {	
		assertNull(race.getLeadEntry());		
	}
	
	@Test
	void given_noLapsHaveBeenCompleted_when_requestLapsForecast_then_returnsNumberOfPlannedLaps() {
		race.setPlannedLaps(5);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		
		race.signUp(entry1);
		race.signUp(entry2);
		
		assertEquals(5, race.getLapForecast());
	}
	
	@Test
	void given_lapsHaveBeenCompleted_when_requestLapsForecast_then_returnsNumberOfTotalLapsForecastToBeCompleted() {
		race.setPlannedLaps(5);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		
		race.signUp(entry1);
		race.signUp(entry2);
		
		entry1.addLap(new Lap(1, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(1, Duration.ofMinutes(15)));
		entry1.addLap(new Lap(2, Duration.ofMinutes(13)));
		entry2.addLap(new Lap(2, Duration.ofMinutes(15)));
		
		assertEquals(18D/13D + 2, race.getLapForecast());
	}
	
	@Test
	void given_NoBoatsHaveSignedUp_when_requestLapsForecast_then_returnsNumberOfPlannedLaps() {	
		race.setPlannedLaps(5);		
		assertEquals(5, race.getLapForecast());		
	}

	@Test
	void given_raceDurationHasElapsedAndABoatHasCrossedTheLineOnAfterDurationElapsed_when_requestLapsForecast_then_returnNumberOfLapsCompletedByLeadBoat() {
		race.setPlannedLaps(5);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		
		race.signUp(entry1);
		race.signUp(entry2);
		
		entry1.addLap(new Lap(1, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(1, Duration.ofMinutes(15)));
		entry1.addLap(new Lap(2, Duration.ofMinutes(13)));
		entry2.addLap(new Lap(2, Duration.ofMinutes(15)));
		entry1.addLap(new Lap(3, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(3, Duration.ofMinutes(15)));
		entry1.addLap(new Lap(4, Duration.ofMinutes(15)));
		
		assertEquals(4D, race.getLapForecast());
	}
	
	@Test
	void given_raceExists_when_validStartSequenceStateProvided_then_setsStartSequenceStateToSuppliedValue() {
		race.setStartSequenceState(StartSequence.ONEMINUTE);
		
		assertEquals(race.getStartSequenceState(), StartSequence.ONEMINUTE);
	}

	@Test
	void given_raceHasEntries_when_dinghyClassesRequested_then_returnsSetOfDinghyClassesForBoatsInEntries() {
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		DinghyClass dc2 = new DinghyClass("Dinghy Class Two", 1);
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		Dinghy dinghy3 = new Dinghy("999", dc2);
		
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		Entry entry3 = new Entry(competitor3, dinghy3, race);
		race.signUp(entry1);
		race.signUp(entry2);
		race.signUp(entry3);
		
		Set<DinghyClass> dinghyClasses = race.getDinghyClasses();
		assertEquals(dinghyClasses.size(), 2);
		assertThat(dinghyClasses, hasItem(dinghyClass));
		assertThat(dinghyClasses, hasItem(dc2));
	}

	// Pursuit Race Position Tests
	@Test
	void given_race_is_pursuit_when_oneEntryHasFinishedLap_then_correctly_calculatesPosition() {
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		race.signUp(entry1);
		entry1.addLap(new Lap(1, Duration.ofMinutes(14)));

		assertEquals(1, entry1.getPosition());
	}

	@Test
	void given_race_is_pursuit_when_twoEntriesHaveFinishedDifferentNumberOfLaps_then_correctly_calculatesPositions() {
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		race.signUp(entry1);
		race.signUp(entry2);
		entry1.addLap(new Lap(1, Duration.ofSeconds(840)));
		entry2.addLap(new Lap(1, Duration.ofSeconds(540)));
		entry2.addLap(new Lap(2, Duration.ofSeconds(540)));

		assertEquals(2, entry1.getPosition());
		assertEquals(1, entry2.getPosition());
	}
		
	@Test
	void given_race_is_pursuit_when_twoEntriesHaveFinishedTheSameNumberOfLaps_then_correctly_calculatesPositions() {
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		race.signUp(entry1);
		race.signUp(entry2);
		entry1.addLap(new Lap(1, Duration.ofSeconds(840)));
		entry2.addLap(new Lap(1, Duration.ofSeconds(839)));
		
		assertEquals(2, entry1.getPosition());
		assertEquals(1, entry2.getPosition());
	}
	
	@Test
	void given_race_is_pursuit_when_twoEntriesHaveFinishedTheSameNumberOfLapsAndOneHasRetired_then_correctly_calculatesPositions() {
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		race.signUp(entry1);
		race.signUp(entry2);
		entry1.addLap(new Lap(1, Duration.ofSeconds(840)));
		entry2.addLap(new Lap(1, Duration.ofSeconds(240)));
		entry2.setScoringAbbreviation("RET");
		
		assertEquals(1, entry1.getPosition());
		assertEquals(2, entry2.getPosition());
	}
	
	@Test
	void given_race_is_pursuit_when_newPositionHigherThanOldPosition_when_positionOfEntryUpdated_then_updatesPositionsOfOtherEntries() {
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Competitor competitor4 = new Competitor("Competitor Four");
		Dinghy dinghy1 = new Dinghy("1", dinghyClass);
		Dinghy dinghy2 = new Dinghy("2", dinghyClass);
		Dinghy dinghy3 = new Dinghy("3", dinghyClass);
		Dinghy dinghy4 = new Dinghy("4", dinghyClass);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		Entry entry3 = new Entry(competitor3, dinghy3, race);
		Entry entry4 = new Entry(competitor4, dinghy4, race);
		race.signUp(entry1);
		race.signUp(entry2);
		race.signUp(entry3);
		race.signUp(entry4);
		entry1.addLap(new Lap(1, Duration.ofSeconds(240))); // position 1
		entry2.addLap(new Lap(1, Duration.ofSeconds(250))); // position 3
		entry3.addLap(new Lap(1, Duration.ofSeconds(245))); // position 2
		entry4.addLap(new Lap(1, Duration.ofSeconds(260))); // position 4
		
		race.updateEntryPosition(entry2, 2);
		assertEquals(1, entry1.getPosition());
		assertEquals(2, entry2.getPosition());
		assertEquals(3, entry3.getPosition());
		assertEquals(4, entry4.getPosition());
	}
	
	@Test
	void given_race_is_pursuit_when_oldPositionWasNull_when_positionOfEntryUpdated_then_doesNotUpdatesPositionOfAnyEntryLowerThanNumberOfEntriesInRace() {
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Competitor competitor4 = new Competitor("Competitor Four");
		Dinghy dinghy1 = new Dinghy("1", dinghyClass);
		Dinghy dinghy2 = new Dinghy("2", dinghyClass);
		Dinghy dinghy3 = new Dinghy("3", dinghyClass);
		Dinghy dinghy4 = new Dinghy("4", dinghyClass);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		Entry entry3 = new Entry(competitor3, dinghy3, race);
		Entry entry4 = new Entry(competitor4, dinghy4, race);
		race.signUp(entry1);
		race.signUp(entry2);
		race.signUp(entry3);
		race.signUp(entry4);
		entry1.addLap(new Lap(1, Duration.ofSeconds(240))); // position 1
		entry2.setScoringAbbreviation("DNS");
		entry3.addLap(new Lap(1, Duration.ofSeconds(245))); // position 2
		
		assertEquals(1, entry1.getPosition());
		assertEquals(4, entry2.getPosition());
		assertEquals(2, entry3.getPosition());
		assertNull(entry4.getPosition());
	}
	
	@Test
	void given_race_is_pursuit_when_newPositionLowerThanOldPosition_when_positionOfEntryUpdated_then_updatesPositionsOfOtherEntries() {
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Competitor competitor4 = new Competitor("Competitor Four");
		Dinghy dinghy1 = new Dinghy("1", dinghyClass);
		Dinghy dinghy2 = new Dinghy("2", dinghyClass);
		Dinghy dinghy3 = new Dinghy("3", dinghyClass);
		Dinghy dinghy4 = new Dinghy("4", dinghyClass);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		Entry entry3 = new Entry(competitor3, dinghy3, race);
		Entry entry4 = new Entry(competitor4, dinghy4, race);
		race.signUp(entry1);
		race.signUp(entry2);
		race.signUp(entry3);
		race.signUp(entry4);
		entry1.addLap(new Lap(1, Duration.ofSeconds(240))); // position 1
		entry2.addLap(new Lap(1, Duration.ofSeconds(250))); // position 3
		entry3.addLap(new Lap(1, Duration.ofSeconds(245))); // position 2
		entry4.addLap(new Lap(1, Duration.ofSeconds(260))); // position 4
		
		race.updateEntryPosition(entry3, 3);
		assertEquals(1, entry1.getPosition());
		assertEquals(2, entry2.getPosition());
		assertEquals(3, entry3.getPosition());
		assertEquals(4, entry4.getPosition());
	}
		
	@Test
	void given_race_is_pursuit_when_noPositionsCalculated_when_positionOfEntryUpdated_then_updatesOnlyPositionsOfEntry() {
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Competitor competitor4 = new Competitor("Competitor Four");
		Dinghy dinghy1 = new Dinghy("1", dinghyClass);
		Dinghy dinghy2 = new Dinghy("2", dinghyClass);
		Dinghy dinghy3 = new Dinghy("3", dinghyClass);
		Dinghy dinghy4 = new Dinghy("4", dinghyClass);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		Entry entry3 = new Entry(competitor3, dinghy3, race);
		Entry entry4 = new Entry(competitor4, dinghy4, race);
		race.signUp(entry1);
		race.signUp(entry2);
		race.signUp(entry3);
		race.signUp(entry4);
		
		race.updateEntryPosition(entry3, 3);
		assertEquals(null, entry1.getPosition());
		assertEquals(null, entry2.getPosition());
		assertEquals(3, entry3.getPosition());
		assertEquals(null, entry4.getPosition());
	}

	@Test
	void given_race_race_is_pursuit_when_scoringAbbreviationSetForEntry_then_positionSetEqualToNumberOfEntriesInRace() {
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Competitor competitor4 = new Competitor("Competitor Four");
		Dinghy dinghy1 = new Dinghy("1", dinghyClass);
		Dinghy dinghy2 = new Dinghy("2", dinghyClass);
		Dinghy dinghy3 = new Dinghy("3", dinghyClass);
		Dinghy dinghy4 = new Dinghy("4", dinghyClass);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		Entry entry3 = new Entry(competitor3, dinghy3, race);
		Entry entry4 = new Entry(competitor4, dinghy4, race);
		race.signUp(entry1);
		race.signUp(entry2);
		race.signUp(entry3);
		race.signUp(entry4);
		entry2.setScoringAbbreviation("DNS");
		
		assertEquals(4, entry2.getPosition());
	}

	// Fleet Race Position Tests
	@Test
	void given_raceIsFleetAndEntriesAllSameClassAndAllSailedTheSameDistance_then_correctlyAssignsPositions() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		race.signUp(entry1);
		race.signUp(entry2);
		entry1.addLap(new Lap(1, Duration.ofSeconds(660000)));
		entry2.addLap(new Lap(1, Duration.ofSeconds(600000)));
		entry1.addLap(new Lap(2, Duration.ofSeconds(660000))); // corrected time 1265580.058
		entry2.addLap(new Lap(2, Duration.ofSeconds(600000))); // corrected time 1150527.325

		assertEquals(1, entry2.getPosition());
		assertEquals(2, entry1.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntriesAllSameClass_when_oneEntrySailsLessLaps_then_correctlyAssignsPositions() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		race.signUp(entry1);
		race.signUp(entry2);
		entry1.addLap(new Lap(1, Duration.ofSeconds(1200000))); // corrected time 2301054.65
		entry2.addLap(new Lap(1, Duration.ofSeconds(660000)));
		entry2.addLap(new Lap(2, Duration.ofSeconds(660000))); // corrected time 1265580.058

		assertEquals(1, entry2.getPosition());
		assertEquals(2, entry1.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntriesDifferentClassAndAllEntriesSailSameNumberOfLaps_then_correctlyAssignsPositions() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		DinghyClass graduate = new DinghyClass("Graduate", 2, 1110);
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", graduate);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		race.signUp(entry1);
		race.signUp(entry2);
		entry1.addLap(new Lap(1, Duration.ofSeconds(660000)));
		entry2.addLap(new Lap(1, Duration.ofSeconds(660000)));
		entry1.addLap(new Lap(2, Duration.ofSeconds(660000))); // corrected time 1265580.058
		entry2.addLap(new Lap(2, Duration.ofSeconds(660000))); // corrected time 1189189.189

		assertEquals(1, entry2.getPosition());
		assertEquals(2, entry1.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntriesDifferentClass_when_oneEntrySailsLessLaps_then_correctlyAssignsPositions() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		DinghyClass graduate = new DinghyClass("Graduate", 2, 1110);
		
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", graduate);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		race.signUp(entry1);
		race.signUp(entry2);
		entry1.addLap(new Lap(1, Duration.ofSeconds(660000)));
		entry2.addLap(new Lap(1, Duration.ofSeconds(700000))); // corrected time 1261261.261
		entry1.addLap(new Lap(2, Duration.ofSeconds(660000))); // corrected time 1265580.058

		assertEquals(1, entry2.getPosition());
		assertEquals(2, entry1.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntriesDifferentClass_when_oneEntrySailsLessLapsAndGainsAnAdvantageFromDoingSo_then_correctlyAssignsPositions() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		DinghyClass graduate = new DinghyClass("Graduate", 2, 1110);
		
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", graduate);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		race.signUp(entry1);
		race.signUp(entry2);
		entry2.addLap(new Lap(1, Duration.ofSeconds(400000)));
		entry1.addLap(new Lap(1, Duration.ofSeconds(500000))); // corrected time 958772.7709
		entry2.addLap(new Lap(2, Duration.ofSeconds(820000))); // corrected time 1189189.189

		assertEquals(1, entry2.getPosition());
		assertEquals(2, entry1.getPosition());
	}

	@Test
	void given_raceIsFleetAndEntriesAllSameClassAndAllSailedTheSameDistanceExceptOneEntryWithScoringAbbreviation_then_correctlyAssignsPositions() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		Dinghy dinghy3 = new Dinghy("8974", scorpion);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		Entry entry3 = new Entry(competitor3, dinghy3, race);
		race.signUp(entry1);
		race.signUp(entry2);
		race.signUp(entry3);
		entry3.setScoringAbbreviation("DNS");
		entry1.addLap(new Lap(1, Duration.ofSeconds(660000)));
		entry2.addLap(new Lap(1, Duration.ofSeconds(600000)));
		entry1.addLap(new Lap(2, Duration.ofSeconds(660000))); // corrected time 1265580.058
		entry2.addLap(new Lap(2, Duration.ofSeconds(600000))); // corrected time 1150527.325

		assertEquals(1, entry2.getPosition());
		assertEquals(2, entry1.getPosition());
		assertEquals(3, entry3.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntriesAllSameClassAndOneEntryHasAScoringAbbreviation_when_oneEntrySailsLessLaps_then_correctlyAssignsPositions() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		Dinghy dinghy3 = new Dinghy("8974", scorpion);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		Entry entry3 = new Entry(competitor3, dinghy3, race);
		race.signUp(entry1);
		race.signUp(entry2);
		race.signUp(entry3);
		entry3.setScoringAbbreviation("DNS");
		entry1.addLap(new Lap(1, Duration.ofSeconds(1200000))); // corrected time 2301054.65
		entry2.addLap(new Lap(1, Duration.ofSeconds(660000)));
		entry2.addLap(new Lap(2, Duration.ofSeconds(660000))); // corrected time 1265580.058

		assertEquals(1, entry2.getPosition());
		assertEquals(2, entry1.getPosition());
		assertEquals(3, entry3.getPosition());
	}

	@Test
	void given_raceIsFleetAndEntriesDifferentClassAndAllEntriesSailSameNumberOfLapsExceptOneEntryWithScoringAbbreviation_then_correctlyAssignsPositions() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		DinghyClass graduate = new DinghyClass("Graduate", 2, 1110);
		
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", graduate);
		Dinghy dinghy3 = new Dinghy("8974", scorpion);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		Entry entry3 = new Entry(competitor3, dinghy3, race);
		race.signUp(entry1);
		race.signUp(entry2);
		race.signUp(entry3);
		entry3.setScoringAbbreviation("DNS");
		entry1.addLap(new Lap(1, Duration.ofSeconds(660000)));
		entry2.addLap(new Lap(1, Duration.ofSeconds(660000)));
		entry1.addLap(new Lap(2, Duration.ofSeconds(660000))); // corrected time 1265580.058
		entry2.addLap(new Lap(2, Duration.ofSeconds(660000))); // corrected time 1189189.189

		assertEquals(1, entry2.getPosition());
		assertEquals(2, entry1.getPosition());
		assertEquals(3, entry3.getPosition());
	}

	@Test
	void given_raceIsFleetAndEntriesDifferentClassAndOneEntryDidNotStart_when_oneEntrySailsLessLaps_then_correctlyAssignsPositions() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		DinghyClass graduate = new DinghyClass("Graduate", 2, 1110);
		
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", graduate);
		Dinghy dinghy3 = new Dinghy("8974", scorpion);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		Entry entry3 = new Entry(competitor3, dinghy3, race);
		race.signUp(entry1);
		race.signUp(entry2);
		race.signUp(entry3);
		entry3.setScoringAbbreviation("DNS");
		entry1.addLap(new Lap(1, Duration.ofSeconds(660000)));
		entry2.addLap(new Lap(1, Duration.ofSeconds(700000))); // corrected time 1261261.261
		entry1.addLap(new Lap(2, Duration.ofSeconds(660000))); // corrected time 1265580.058

		assertEquals(1, entry2.getPosition());
		assertEquals(2, entry1.getPosition());
		assertEquals(3, entry3.getPosition());
	}

	@Test
	void given_raceIsFleetAndEntriesDifferentClass_when_anEntryThatRetiredSailsLessLapsAndGainsAnAdvantageFromDoingSo_then_correctlyAssignsPositions() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		DinghyClass graduate = new DinghyClass("Graduate", 2, 1110);
		
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", graduate);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		race.signUp(entry1);
		race.signUp(entry2);
		entry2.addLap(new Lap(1, Duration.ofSeconds(400000)));
		entry1.addLap(new Lap(1, Duration.ofSeconds(500000))); // corrected time 958772.7709
		entry1.setScoringAbbreviation("RTD");
		entry2.addLap(new Lap(2, Duration.ofSeconds(820000))); // corrected time 1189189.189

		assertEquals(1, entry2.getPosition());
		assertEquals(2, entry1.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntriesDifferentClassesAndSailedDifferentNumbersOfLaps_then_correctlyAssignsPositions() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		DinghyClass graduate = new DinghyClass("Graduate", 2, 1110);
		DinghyClass laserRooster = new DinghyClass("Laser (Rooster)", 1, 1025);
		DinghyClass laser = new DinghyClass("Laser", 1, 1102);
		DinghyClass comet = new DinghyClass("Comet", 1, 1210);
		DinghyClass topper = new DinghyClass("Topper", 1, 1369);
		
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Competitor competitor4 = new Competitor("Competitor Four");
		Competitor competitor5 = new Competitor("Competitor Five");
		Competitor competitor6 = new Competitor("Competitor Six");
		Competitor competitor7 = new Competitor("Competitor Seven");
		Competitor competitor8 = new Competitor("Competitor Eight");
		Competitor competitor9 = new Competitor("Competitor Nine");
		Competitor competitor10 = new Competitor("Competitor Ten");
		Competitor competitor11 = new Competitor("Competitor Eleven");
		Dinghy dinghy1 = new Dinghy("548759", laser);
		Dinghy dinghy2 = new Dinghy("652444", laserRooster);
		Dinghy dinghy3 = new Dinghy("325481", laser);
		Dinghy dinghy4 = new Dinghy("654823", laser);
		Dinghy dinghy5 = new Dinghy("23564", comet);
		Dinghy dinghy6 = new Dinghy("1234", scorpion);
		Dinghy dinghy7 = new Dinghy("568", topper);
		Dinghy dinghy8 = new Dinghy("38421", comet);
		Dinghy dinghy9 = new Dinghy("15236", comet);
		Dinghy dinghy10 = new Dinghy("4567", graduate);
		Dinghy dinghy11 = new Dinghy("24536", comet);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		Entry entry3 = new Entry(competitor3, dinghy3, race);
		Entry entry4 = new Entry(competitor4, dinghy4, race);
		Entry entry5 = new Entry(competitor5, dinghy5, race);
		Entry entry6 = new Entry(competitor6, dinghy6, race);
		Entry entry7 = new Entry(competitor7, dinghy7, race);
		Entry entry8 = new Entry(competitor8, dinghy8, race);
		Entry entry9 = new Entry(competitor9, dinghy9, race);
		Entry entry10 = new Entry(competitor10, dinghy10, race);
		Entry entry11 = new Entry(competitor11, dinghy11, race);
		race.signUp(entry1);
		race.signUp(entry2);
		race.signUp(entry3);
		race.signUp(entry4);
		race.signUp(entry5);
		race.signUp(entry6);
		race.signUp(entry7);
		race.signUp(entry8);
		race.signUp(entry9);
		race.signUp(entry10);
		race.signUp(entry11);
		entry2.addLap(new Lap(1, Duration.ofSeconds(1045)));
		entry6.addLap(new Lap(1, Duration.ofSeconds(1060)));
		entry1.addLap(new Lap(1, Duration.ofSeconds(1095)));
		entry3.addLap(new Lap(1, Duration.ofSeconds(1105)));
		entry11.addLap(new Lap(1, Duration.ofSeconds(1160)));
		entry9.addLap(new Lap(1, Duration.ofSeconds(1165)));
		entry5.addLap(new Lap(1, Duration.ofSeconds(1815)));
		entry10.addLap(new Lap(1, Duration.ofSeconds(1829)));
		entry8.addLap(new Lap(1, Duration.ofSeconds(1837)));
		entry4.addLap(new Lap(1, Duration.ofSeconds(1843)));
		entry7.addLap(new Lap(1, Duration.ofSeconds(2549)));
		entry2.addLap(new Lap(2, Duration.ofSeconds(2233)));
		entry6.addLap(new Lap(2, Duration.ofSeconds(2597)));
		entry3.addLap(new Lap(2, Duration.ofSeconds(2740)));
		entry1.addLap(new Lap(2, Duration.ofSeconds(2782)));
		entry11.addLap(new Lap(2, Duration.ofSeconds(2804)));
		entry9.addLap(new Lap(2, Duration.ofSeconds(2821)));

		assertEquals(1, entry2.getPosition());
		assertEquals(2, entry11.getPosition());
		assertEquals(3, entry9.getPosition());
		assertEquals(4, entry5.getPosition());
		assertEquals(5, entry8.getPosition());
		assertEquals(6, entry10.getPosition());
		assertEquals(7, entry3.getPosition());
		assertEquals(8, entry6.getPosition());
		assertEquals(9, entry1.getPosition());
		assertEquals(10, entry4.getPosition());
		assertEquals(11, entry7.getPosition());
	}

	@Test
	void given_raceIsFleet_when_scoringAbbreviationSetForEntry_then_positionSetEqualToNumberOfEntriesInRace() {
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Competitor competitor4 = new Competitor("Competitor Four");
		Dinghy dinghy1 = new Dinghy("1", dinghyClass);
		Dinghy dinghy2 = new Dinghy("2", dinghyClass);
		Dinghy dinghy3 = new Dinghy("3", dinghyClass);
		Dinghy dinghy4 = new Dinghy("4", dinghyClass);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		Entry entry3 = new Entry(competitor3, dinghy3, race);
		Entry entry4 = new Entry(competitor4, dinghy4, race);
		race.signUp(entry1);
		race.signUp(entry2);
		race.signUp(entry3);
		race.signUp(entry4);
		entry2.setScoringAbbreviation("DNS");
		
		assertEquals(4, entry2.getPosition());
	}

	@Test
	void given_raceIsFleet_when_moreThanOneEntryHaveTheSameCorrectedTime_then_theyAreAllAssignedTheLowestPositionOfAnyEntryWithThatCorrectedTime() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		race.signUp(entry1);
		race.signUp(entry2);
		entry1.addLap(new Lap(1, Duration.ofSeconds(660000)));
		entry2.addLap(new Lap(1, Duration.ofSeconds(660000)));

		assertEquals(2, entry1.getPosition());
		assertEquals(2, entry2.getPosition());
	}
	
	@Test
	void given_raceIsFleet_when_moreThanOneGroupOfEntriesHaveTheSameCorrectedTimeAndEachGroupHasADifferentCorrectedTime_then_theyAreAllAssignedTheLowestPositionOfAnyEntryWithThatCorrectedTime() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Competitor competitor4 = new Competitor("Competitor Four");
		Competitor competitor5 = new Competitor("Competitor Five");
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		Dinghy dinghy3 = new Dinghy("3245", scorpion);
		Dinghy dinghy4 = new Dinghy("2176", scorpion);
		Dinghy dinghy5 = new Dinghy("2582", scorpion);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		Entry entry3 = new Entry(competitor3, dinghy3, race);
		Entry entry4 = new Entry(competitor4, dinghy4, race);
		Entry entry5 = new Entry(competitor5, dinghy5, race);
		race.signUp(entry1);
		race.signUp(entry2);
		race.signUp(entry3);
		race.signUp(entry4);
		race.signUp(entry5);
		entry1.addLap(new Lap(1, Duration.ofSeconds(600000)));
		entry2.addLap(new Lap(1, Duration.ofSeconds(660000)));
		entry3.addLap(new Lap(1, Duration.ofSeconds(660000)));
		entry4.addLap(new Lap(1, Duration.ofSeconds(650000)));
		entry5.addLap(new Lap(1, Duration.ofSeconds(600000)));

		assertEquals(2, entry1.getPosition());
		assertEquals(2, entry5.getPosition());
		assertEquals(3, entry4.getPosition());
		assertEquals(5, entry2.getPosition());
		assertEquals(5, entry3.getPosition());
	}
}
