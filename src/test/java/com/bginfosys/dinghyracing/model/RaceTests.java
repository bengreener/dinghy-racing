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
	void given_twoEntriesHaveCompletedTheSameNumberOfLaps_when_requestLeadEntryInRace_then_returnsEntryWithFastestTime() {
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
	void given_oneEntryHasCompletedMoreLaps_when_requestLeadEntryInRace_then_returnsEntryWithMostLaps() {
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
	void given_raceHasEntries_when_dinghyClassesRequested_then_returnsSetOfDinghyClassesForBoatsInEntries() {
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		DinghyClass dc2 = new DinghyClass("Dinghy Class Two", 1, 1000);
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
	// Lap advantage is when an entry gets a better corrected time than another boat with the same or slower handicap by sailing less laps; for example if the wind dies for a previously faster boat on the last lap

	// Same Portsmouth Number
	@Test
	void given_raceIsFleetAndEntriesHaveSamePortsmouthNumberAndSailedTheSameLaps_whenOnEntryHasFasterCorrectedTime_then_entryWithFasterTimeWins() {
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
		entry1.addLap(new Lap(1, Duration.ofSeconds(660)));
		entry2.addLap(new Lap(1, Duration.ofSeconds(600)));
		entry1.addLap(new Lap(2, Duration.ofSeconds(660))); // corrected time 1265.580
		entry2.addLap(new Lap(2, Duration.ofSeconds(600))); // corrected time 1150.527

		assertEquals(1, entry2.getPosition());
		assertEquals(2, entry1.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntriesHaveSamePortsmouthNumberAndSailedTheSameLapsAndHaveTheSameTime_then_theyAreAllAssignedTheLowestPositionOfAnyEntryWithThatCorrectedTime() {
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
		entry1.addLap(new Lap(1, Duration.ofSeconds(660))); // corrected time 632.790
		entry2.addLap(new Lap(1, Duration.ofSeconds(660))); // corrected time 632.790

		assertEquals(2, entry1.getPosition());
		assertEquals(2, entry2.getPosition());
	}

	@Test
	void given_raceIsFleetAndEntriesHaveSamePortsmouthNumber_when_oneEntrySailsMoreLapsAndHasFasterCorrectedTime_then_entryWithFasterTimeWins() {
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
		entry1.addLap(new Lap(1, Duration.ofSeconds(660)));
		entry2.addLap(new Lap(1, Duration.ofSeconds(1200))); // corrected time 2301.054
		entry1.addLap(new Lap(2, Duration.ofSeconds(660))); // corrected time 1265.580

		assertEquals(1, entry1.getPosition());
		assertEquals(2, entry2.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntriesHaveSamePortsmouthNumber_when_entrySailsMoreLapsAndHasSlowerCorrectedTime_then_entryWithMoreLapsWins() {
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
		entry1.addLap(new Lap(1, Duration.ofSeconds(600)));
		entry2.addLap(new Lap(1, Duration.ofSeconds(700))); // corrected time 1342.282
		entry1.addLap(new Lap(1, Duration.ofSeconds(900))); // corrected time 1438.159

		assertEquals(1, entry1.getPosition());
		assertEquals(2, entry2.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntriesHaveSamePortsmouthNumberAndSameCorrectedTime_when_entrySailsMoreLaps_then_entryWithMoreLapsWins() {
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
		entry1.addLap(new Lap(1, Duration.ofSeconds(500)));
		entry2.addLap(new Lap(1, Duration.ofSeconds(500))); // corrected time 958.773
		entry1.addLap(new Lap(2, Duration.ofSeconds(500))); // corrected time 958.773

		assertEquals(1, entry1.getPosition());
//		assertEquals(2, entry1.getPosition());
		assertEquals(2, entry2.getPosition());
	}
	
	// Slower Portsmouth Number
	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumber_when_largerPortsmouthNumberEntrySailsMoreLapsInSlowerCorrectedTime_then_slowerCorrectedTimeEntryWins() {
		DinghyClass graduate = new DinghyClass("Graduate", 2, 1110);
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		race.signUp(entry1);
		race.signUp(entry2);
		entry1.addLap(new Lap(1, Duration.ofSeconds(600)));
		entry2.addLap(new Lap(1, Duration.ofSeconds(600))); // corrected time 1150.527
		entry1.addLap(new Lap(2, Duration.ofSeconds(700))); // corrected time 1171.171

		assertEquals(1, entry1.getPosition());
		assertEquals(2, entry2.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumberAndSameCorrectedTime_when_entrySailsMoreLaps_then_entryWithMoreLapsWins() {
		DinghyClass graduate = new DinghyClass("Graduate", 2, 1000);
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 500);
		
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("rif1234", graduate);
		Dinghy dinghy2 = new Dinghy("rif4567", scorpion);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		race.signUp(entry1);
		race.signUp(entry2);
		entry1.addLap(new Lap(1, Duration.ofSeconds(200)));
		entry2.addLap(new Lap(1, Duration.ofSeconds(250))); // corrected time 1000.00
		entry1.addLap(new Lap(2, Duration.ofSeconds(800))); // corrected time 1000.00

		assertEquals(1, entry1.getPosition());
		assertEquals(2, entry2.getPosition());
	}

	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumber_when_largerPortsmouthNumberEntrySailsMoreLapsAndHasFasterCorrectedTime_then_largerPortsmouthNumberEntryWins() {
		DinghyClass graduate = new DinghyClass("Graduate", 2, 1110);
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		race.signUp(entry1);
		race.signUp(entry2);
		entry1.addLap(new Lap(1, Duration.ofSeconds(600)));
		entry2.addLap(new Lap(1, Duration.ofSeconds(700))); // corrected time 1081.081
		entry1.addLap(new Lap(2, Duration.ofSeconds(600))); // corrected time 1342.282

		assertEquals(1, entry1.getPosition());
		assertEquals(2, entry2.getPosition());
	}

	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumberAndSailedTheSameLaps_when_largerPortsmouthNumberHasSlowerCorrectedTime_then_largerPortsmouthNumberEntryLoses() {
		DinghyClass graduate = new DinghyClass("Graduate", 2, 1110);
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		race.signUp(entry1);
		race.signUp(entry2);
		entry1.addLap(new Lap(1, Duration.ofSeconds(600)));
		entry2.addLap(new Lap(1, Duration.ofSeconds(500)));
		entry1.addLap(new Lap(2, Duration.ofSeconds(600))); // corrected time 1081.081
		entry2.addLap(new Lap(2, Duration.ofSeconds(500))); // corrected time 958.773

		assertEquals(1, entry2.getPosition());
		assertEquals(2, entry1.getPosition());
	}

	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumberAndSailedTheSameLapsAndHaveTheSameCorrecedTime_then_entriesAllAssignedTheLowestPositionOfAnyEntryWithThatCorrectedTime() {
		DinghyClass graduate = new DinghyClass("Graduate", 2, 1500);
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1000);
		
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		race.signUp(entry1);
		race.signUp(entry2);
		entry1.addLap(new Lap(1, Duration.ofSeconds(750)));
		entry2.addLap(new Lap(1, Duration.ofSeconds(500)));
		entry1.addLap(new Lap(2, Duration.ofSeconds(750))); // corrected time 1000.000
		entry2.addLap(new Lap(2, Duration.ofSeconds(500))); // corrected time 1000.000

		assertEquals(2, entry1.getPosition());
		assertEquals(2, entry2.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumberAndSailedTheSameLaps_when_largerPortsmouthNumberEntryHasFasterCorrectedTime_then_entryWithFasterTimeWins() {
		DinghyClass graduate = new DinghyClass("Graduate", 2, 1110);
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		race.signUp(entry1);
		race.signUp(entry2);
		entry1.addLap(new Lap(1, Duration.ofSeconds(600)));
		entry2.addLap(new Lap(1, Duration.ofSeconds(600)));
		entry1.addLap(new Lap(2, Duration.ofSeconds(600))); // corrected time 1081.081
		entry2.addLap(new Lap(2, Duration.ofSeconds(600))); // corrected time 1150.527

		assertEquals(1, entry1.getPosition());
		assertEquals(2, entry2.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumber_when_largerPortsmouthNumberSailedLessLapsAndEntryHasSlowerCorrectedTime_then_entryWithFasterCorrectedTimeWins() {
		DinghyClass graduate = new DinghyClass("Graduate", 2, 1110);
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		race.signUp(entry1);
		race.signUp(entry2);
		entry2.addLap(new Lap(1, Duration.ofSeconds(500)));
		entry1.addLap(new Lap(1, Duration.ofSeconds(600))); // corrected time 1081.081
		entry2.addLap(new Lap(2, Duration.ofSeconds(500))); // corrected time 958.773

		assertEquals(1, entry2.getPosition());
		assertEquals(2, entry1.getPosition());
	}

	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumberAndSameCorrectedTime_when_largerPortsmouthNumberEntrySailedLessLaps_then_entriesAllAssignedTheLowestPositionOfAnyEntryWithThatCorrectedTime() {
		DinghyClass graduate = new DinghyClass("Graduate", 2, 1500);
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1000);
		
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		race.signUp(entry1);
		race.signUp(entry2);
		entry2.addLap(new Lap(1, Duration.ofSeconds(500)));
		entry1.addLap(new Lap(1, Duration.ofSeconds(750))); // corrected time 1000.000
		entry2.addLap(new Lap(2, Duration.ofSeconds(500))); // corrected time 1000.000

		assertEquals(2, entry1.getPosition());
		assertEquals(2, entry2.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumber_when_largerPortsmouthNumberEntrySailedLessLapsAndHasAFasterCorrectedTime_then_entryWithFasterCorrectedTimeWins() {
		DinghyClass graduate = new DinghyClass("Graduate", 2, 1110);
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		race.signUp(entry1);
		race.signUp(entry2);
		entry2.addLap(new Lap(1, Duration.ofSeconds(750)));
		entry1.addLap(new Lap(1, Duration.ofSeconds(700))); // corrected time 1261.261
		entry2.addLap(new Lap(2, Duration.ofSeconds(750))); // corrected time 1438.159

		assertEquals(1, entry1.getPosition());
		assertEquals(2, entry2.getPosition());
	}

	// Scoring abbreviation
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
	void given_raceIsFleetAndEntriesHaveSamePortsmouthNumberSailedSameNumberLaps_whenFasterEntryHasScoringAbbreviation_then_slowerEntryPlacedFirst() {
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
		entry1.addLap(new Lap(1, Duration.ofSeconds(500)));
		entry2.addLap(new Lap(1, Duration.ofSeconds(600)));
		entry1.addLap(new Lap(2, Duration.ofSeconds(500))); // corrected time 958.773
		entry2.addLap(new Lap(2, Duration.ofSeconds(600))); // corrected time 1150.527
		entry1.setScoringAbbreviation("RET");

		assertEquals(1, entry2.getPosition());
		assertEquals(2, entry1.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntriesHaveSamePortsmouthNumber_when_boatWithTheMedianTimeHasScoringAbbreviation_then_medianTimedEntryPlacedLast() {
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
		entry1.addLap(new Lap(1, Duration.ofSeconds(900))); // corrected time 719.080
		entry2.addLap(new Lap(1, Duration.ofSeconds(750))); // corrected time 958.773
		entry3.addLap(new Lap(1, Duration.ofSeconds(1000))); // corrected time 527.325
		entry1.setScoringAbbreviation("RET");

		assertEquals(1, entry2.getPosition());
		assertEquals(2, entry3.getPosition());
		assertEquals(3, entry1.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntriesHaveSamePortsmouthNumber_when_boatWithTheMedianTimeHasLapAdvantageOverSlowestBoatAndSlowestBoatHasScoringAbbreviation_then_medianTimedEntryPlacedSecondAndEntryWithScoringAbbreviationPlacedLast() {
		DinghyClass classOne = new DinghyClass("Class 1", 2, 900);
		DinghyClass classTwo = new DinghyClass("Class 2", 2, 1000);
		DinghyClass classThree = new DinghyClass("Class 3", 2, 1100);
		Fleet fleet = new Fleet("Test Fleet");
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Dinghy dinghy1 = new Dinghy("1234", classOne);
		Dinghy dinghy2 = new Dinghy("4567", classTwo);
		Dinghy dinghy3 = new Dinghy("8974", classThree);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		Entry entry2 = new Entry(competitor2, dinghy2, race);
		Entry entry3 = new Entry(competitor3, dinghy3, race);
		race.signUp(entry1);
		race.signUp(entry2);
		race.signUp(entry3);
		entry1.addLap(new Lap(1, Duration.ofSeconds(450)));
		entry2.addLap(new Lap(1, Duration.ofSeconds(600))); // corrected time 1200.000
		entry3.addLap(new Lap(1, Duration.ofSeconds(750)));
		entry1.addLap(new Lap(2, Duration.ofSeconds(450))); // corrected time 1000.000
		entry3.addLap(new Lap(1, Duration.ofSeconds(750))); // corrected time 1363.636
		entry2.setScoringAbbreviation("RET");

		assertEquals(1, entry1.getPosition());
		assertEquals(2, entry3.getPosition());
		assertEquals(3, entry2.getPosition());
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
		entry1.addLap(new Lap(1, Duration.ofSeconds(600)));
		entry2.addLap(new Lap(1, Duration.ofSeconds(660)));
		entry3.addLap(new Lap(1, Duration.ofSeconds(660)));
		entry4.addLap(new Lap(1, Duration.ofSeconds(650)));
		entry5.addLap(new Lap(1, Duration.ofSeconds(600)));

		assertEquals(2, entry1.getPosition());
		assertEquals(2, entry5.getPosition());
		assertEquals(3, entry4.getPosition());
		assertEquals(5, entry2.getPosition());
		assertEquals(5, entry3.getPosition());
	}

	// lap removed
	@Test
	void given_raceIsFleetAndOnlyEntryHasSailedTheMostLapsAndThatEntryWasNotTheLeadBoatOnTheLastLapSailedByMoreThanOneBoat_when_lapRemovedFromLeadBoat_then_correctlyCalculatesPositions() {
	// when lap is removed from the lead boat after which it is no longer the lead boat need to recalculate positions for all boats
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
		entry1.addLap(new Lap(1, Duration.ofSeconds(300))); // corrected time 300.000
		entry2.addLap(new Lap(1, Duration.ofSeconds(310)));
		entry2.addLap(new Lap(2, Duration.ofSeconds(90)));
		entry2.removeLap(new Lap(2, Duration.ofSeconds(90))); // corrected time 310.000

		assertEquals(1, entry1.getPosition());
		assertEquals(2, entry2.getPosition());
	}
}
