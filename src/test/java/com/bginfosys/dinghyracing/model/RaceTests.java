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
import java.util.Set;

class RaceTests {

	//private Race race = new Race("Test Race", LocalDate.of(2021, 10, 14), LocalTime.of(14, 10), new DinghyClass("Test"));
	private DinghyClass dinghyClass = new DinghyClass("Test", 1);
	private Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), dinghyClass, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
	
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
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), null, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
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

	@Test
	void given_oneEntryHasFinishedLap_then_correctly_calculatesPosition() {
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), null, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		race.signUp(entry1);
		entry1.addLap(new Lap(1, Duration.ofMinutes(14)));

		assertEquals(1, entry1.getPosition());
	}

	@Test
	void given_twoEntriesHaveFinishedDifferentNumberOfLaps_then_correctly_calculatesPositions() {
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), null, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
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
	void given_twoEntriesHaveFinishedTheSameNumberOfLaps_then_correctly_calculatesPositions() {
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), null, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
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
	void given_twoEntriesHaveFinishedTheSameNumberOfLapsAndOneHasRetired_then_correctly_calculatesPositions() {
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), null, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
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
	void given_newPositionHigherThanOldPosition_when_positionOfEntryUpdated_then_updatesPositionsOfOtherEntries() {
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), null, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
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
	void given_oldPositionWasNull_when_positionOfEntryUpdated_then_doesNotUpdatesPositionOfAnyEntryLowerThanNumberOfEntriesInRace() {
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), null, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
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
	void given_newPositionLowerThanOldPosition_when_positionOfEntryUpdated_then_updatesPositionsOfOtherEntries() {
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), null, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
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
	void given_noPositionsCalculated_when_positionOfEntryUpdated_then_updatesOnlyPositionsOfEntry() {
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), null, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
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
	void when_scoringAbbreviationSetForEntry_then_positionSetEqualToNumberOfEntriesInRace() {
		Race race = new Race("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), null, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
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
}
