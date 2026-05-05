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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.bginfosys.dinghyracing.exceptions.CompetitorAlreadySignedUpException;
import com.bginfosys.dinghyracing.exceptions.DinghyAlreadySignedUpException;
import com.bginfosys.dinghyracing.exceptions.DinghyClassMismatchException;

class DirectRaceTests {
	
	// Constructor Tests
	@Test
	void raceCreated() {
		DirectRace race = new DirectRace();
		
		assertThat(race, notNullValue());
	}
	
	@Test
	void whenParametersProvided_then_raceCreated() {
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2025, 10, 16, 15, 28), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.RRS26);
		
		assertThat(race, notNullValue());
		assertEquals(race.getName(), "Test Race");
		assertEquals(race.getPlannedStartTime(), LocalDateTime.of(2025, 10, 16, 15, 28));
		assertEquals(race.getFleet(), fleet);
		assertEquals(race.getDuration(), Duration.ofMinutes(45));
		assertEquals(race.getPlannedLaps(), 5);
		assertEquals(race.getType(), RaceType.FLEET);
		assertEquals(race.getStartType(), StartType.RRS26);
	}

	// getter and setter tests
	@Test
	void setPlannedStartTime() {
		Set<DinghyClass> dinghyClasses = new HashSet<DinghyClass>();
		Fleet fleet = new Fleet("Test Fleet", dinghyClasses);
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		
		race.setPlannedStartTime(LocalDateTime.of(2021, 9, 27, 16, 47));
		assertEquals(race.getPlannedStartTime(), LocalDateTime.of(2021, 9, 27, 16, 47));
	}
	
	@Test
	void setDuration() {
		Set<DinghyClass> dinghyClasses = new HashSet<DinghyClass>();
		Fleet fleet = new Fleet("Test Fleet", dinghyClasses);
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		
		race.setDuration(Duration.ofMillis(1000));
		assertEquals(race.getDuration(), Duration.ofSeconds(1));
	}

	@Test
	void plannedStartTimeIsLocalDateTime() {
		Set<DinghyClass> dinghyClasses = new HashSet<DinghyClass>();
		Fleet fleet = new Fleet("Test Fleet", dinghyClasses);
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		
		race.setPlannedStartTime(LocalDateTime.of(2021, 9, 27, 16, 47));
		assertTrue(race.getPlannedStartTime() instanceof LocalDateTime);
	}
	
	// operation tests
	// sign up tests
	/*
	 * Signs supplied dinghy and helm up for race
	 */
	@Test
	void givenDinghyAndHelmNotInAnEntrySignedUpToRace_when_dinghyAndHelmProvided_then_createsSignedUpEntryForRace() {
		Competitor helm = new Competitor("Bob");
		DinghyClass dinghyClass = new DinghyClass("Comet", 1, 1000);
		Dinghy dinghy = new Dinghy("1234", dinghyClass);
		Fleet fleet = new Fleet("Open");
		DirectRace race = new DirectRace("Race", LocalDateTime.of(2025, 10, 17, 19, 30), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.RRS26);
		
		Set<SignedUp> preSignedUp = new HashSet<SignedUp>(race.getSignedUp());
		race.signUp(helm, dinghy);
		// new entry added
		assertEquals(preSignedUp.size() + 1, race.getSignedUp().size());
		Set<SignedUp> postSignedUp = race.getSignedUp();
		postSignedUp.removeAll(preSignedUp);
		SignedUp newSignedUp = (SignedUp) postSignedUp.toArray()[0];
		assertEquals(newSignedUp.getEntry().getDinghy(), dinghy);
		assertEquals(newSignedUp.getEntry().getHelm(), helm);
	}
	
	/*
	 * Signs supplied dinghy, helm, and crew up for race
	 */
	@Test
	void givenDinghyAndHelmNotInAnEntrySignedUpToRace_when_dinghyAndHelmAndCrewProvided_then_createsSignedUpEntryForRace() {
		Competitor helm = new Competitor("Bob");
		Competitor crew = new Competitor("Jim");
		DinghyClass dinghyClass = new DinghyClass("Scorpion", 1, 1000);
		Dinghy dinghy = new Dinghy("1234", dinghyClass);
		Fleet fleet = new Fleet("Open");
		DirectRace race = new DirectRace("Race", LocalDateTime.of(2025, 10, 17, 19, 30), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.RRS26);
		
		Set<SignedUp> preSignedUp = new HashSet<SignedUp>(race.getSignedUp());
		race.signUp(helm, crew, dinghy);
		// new entry added
		assertEquals(preSignedUp.size() + 1, race.getSignedUp().size());
		Set<SignedUp> postSignedUp = race.getSignedUp();
		postSignedUp.removeAll(preSignedUp);
		SignedUp newSignedUp = (SignedUp) postSignedUp.toArray()[0];
		assertEquals(newSignedUp.getEntry().getDinghy(), dinghy);
		assertEquals(newSignedUp.getEntry().getHelm(), helm);
		assertEquals(newSignedUp.getEntry().getCrew(), crew);
	}
	
	/*
	 * signing_up_dinghy_class_allowed_by_race
	 */
	@Test
	void givenDinghyClassNotAllowedByRace_when_dinghyAndHelmProvided_then_dinghyClassMisMatchException() {
		Competitor helm = new Competitor("Bob");
		DinghyClass dinghyClass1 = new DinghyClass("Comet", 1, 1000);
		DinghyClass dinghyClass2 = new DinghyClass("Optimist", 1, 1000);
		Dinghy dinghy = new Dinghy("1234", dinghyClass1);
		Set<DinghyClass> fleetDinghyClasses = new HashSet<DinghyClass>();
		fleetDinghyClasses.add(dinghyClass2);
		Fleet fleet = new Fleet("Optimist", fleetDinghyClasses);
		DirectRace race = new DirectRace("Race", LocalDateTime.of(2025, 10, 17, 19, 30), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.RRS26);
		
		assertThrows(DinghyClassMismatchException.class, () -> {
			race.signUp(helm, dinghy);
		});
	}
	
	@Test
	void givenDinghyClassNotAllowedByRace_when_dinghyAndHelmAndCrewProvided_then_createsSignedUpEntryForRace() {
		Competitor helm = new Competitor("Bob");
		Competitor crew = new Competitor("Jim");
		DinghyClass dinghyClass = new DinghyClass("Scorpion", 1, 1000);
		DinghyClass dinghyClass2 = new DinghyClass("Optimist", 1, 1000);
		Dinghy dinghy = new Dinghy("1234", dinghyClass);
		Set<DinghyClass> fleetDinghyClasses = new HashSet<DinghyClass>();
		fleetDinghyClasses.add(dinghyClass2);
		Fleet fleet = new Fleet("Optimist", fleetDinghyClasses);
		DirectRace race = new DirectRace("Race", LocalDateTime.of(2025, 10, 17, 19, 30), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.RRS26);
		
		assertThrows(DinghyClassMismatchException.class, () -> {
			race.signUp(helm, crew, dinghy);
		});
	}
	
	/*
	 * helm_entered_once
	 */
	@Test
	void givenHelmAlreadySignedUpForRace_when_dinghyAndHelmProvided_then_competitorAlreadySignedUpException() {
		Competitor helm = new Competitor("Bob");
		DinghyClass dinghyClass = new DinghyClass("Comet", 1, 1000);
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("6789", dinghyClass);
		Fleet fleet = new Fleet("Open");
		DirectRace race = new DirectRace("Race", LocalDateTime.of(2025, 10, 17, 19, 30), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.RRS26);
		
		race.signUp(helm, dinghy1);
		assertThrows(CompetitorAlreadySignedUpException.class, () -> {
			race.signUp(helm, dinghy2);
		});
	}
	
	@Test
	void givenHelmAlreadySignedUpForRace_when_dinghyAndHelmAndCrewProvided_then_competitorAlreadySignedUpException() {
		Competitor helm = new Competitor("Bob");
		Competitor crew = new Competitor("Jim");
		DinghyClass dinghyClass = new DinghyClass("Scorpion", 1, 1000);
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("1234", dinghyClass);
		Fleet fleet = new Fleet("Open");
		DirectRace race = new DirectRace("Race", LocalDateTime.of(2025, 10, 17, 19, 30), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.RRS26);
		
		race.signUp(helm, crew, dinghy1);
		assertThrows(CompetitorAlreadySignedUpException.class, () -> {
			race.signUp(helm, crew, dinghy2);
		});
	}
	
	/*
	 * dinghy_entered_once
	 */
	@Test
	void givenDinghyAlreadySignedUpForRace_when_dinghyAndHelmProvided_then_dinghyAlreadySignedUpException() {
		Competitor helm1 = new Competitor("Bob");
		Competitor helm2 = new Competitor("Jim");
		DinghyClass dinghyClass = new DinghyClass("Comet", 1, 1000);
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Fleet fleet = new Fleet("Open");
		DirectRace race = new DirectRace("Race", LocalDateTime.of(2025, 10, 17, 19, 30), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.RRS26);
		
		race.signUp(helm1, dinghy1);
		assertThrows(DinghyAlreadySignedUpException.class, () -> {
			race.signUp(helm2, dinghy1);
		});
	}
	
	@Test
	void givenHelmAlreadySignedUpForRace_when_dinghyAndHelmAndCrewProvided_then_dinghyAlreadySignedUpException() {
		Competitor helm1 = new Competitor("Bob");
		Competitor crew1 = new Competitor("Jim");
		Competitor helm2 = new Competitor("Mary");
		Competitor crew2 = new Competitor("Jane");
		DinghyClass dinghyClass = new DinghyClass("Scorpion", 1, 1000);
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Fleet fleet = new Fleet("Open");
		DirectRace race = new DirectRace("Race", LocalDateTime.of(2025, 10, 17, 19, 30), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.RRS26);
		
		race.signUp(helm1, crew1, dinghy1);
		assertThrows(DinghyAlreadySignedUpException.class, () -> {
			race.signUp(helm2, crew2, dinghy1);
		});
	}
	
	/*
	 * crew_entered_once
	 */
	@Test
	void givenCrewAlreadySignedUpForRace_when_dinghyAndHelmAndCrewProvided_then_competitorAlreadySignedUpException() {
		Competitor helm1 = new Competitor("Bob");
		Competitor helm2 = new Competitor("Mary");
		Competitor crew = new Competitor("Jim");
		DinghyClass dinghyClass = new DinghyClass("Scorpion", 1, 1000);
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("1234", dinghyClass);
		Fleet fleet = new Fleet("Open");
		DirectRace race = new DirectRace("Race", LocalDateTime.of(2025, 10, 17, 19, 30), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.RRS26);
		
		race.signUp(helm1, crew, dinghy1);
		assertThrows(CompetitorAlreadySignedUpException.class, () -> {
			race.signUp(helm2, crew, dinghy2);
		});
	}
	
	/*
	 * Lap completed tests
	 */	
	@Test
	void given_noLapsCompleted_when_requestNumberOfLapsCompletedByLeadEntry_then_returnsZero() {
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		DinghyClass dinghyClass = new DinghyClass("Comet", 1, 1000);
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		new Entry(competitor1, dinghy1);
				
		assertEquals(0, race.leadEntrylapsCompleted());		
	}
	
	@Test
	void given_TwoEntriesHaveCompletedTheSameNumberOfLaps_when_requestNumberOfLapsCompletedByLeadEntry_then_returnsNumberOFLaps() {
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("competitor Two");
		DinghyClass dinghyClass = new DinghyClass("Comet", 1, 1000);
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		Entry entry1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get().getEntry();
		Entry entry2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get().getEntry();
		entry1.addLap(new Lap(1, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(1, Duration.ofMinutes(15)));
		entry1.addLap(new Lap(2, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(2, Duration.ofMinutes(15)));
		
		assertEquals(race.leadEntrylapsCompleted(), 2);		
	}

	@Test
	void given_OneEntryHasCompletedMoreLaps_when_requestNumberOfLapsCompletedByLeadEntry_then_returnsNumberOfLaps() {
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("competitor Two");
		DinghyClass dinghyClass = new DinghyClass("Comet", 1, 1000);
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();

		signedUp1.getEntry().addLap(new Lap(1, Duration.ofMinutes(14)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofMinutes(15)));
		signedUp1.getEntry().addLap(new Lap(2, Duration.ofMinutes(14)));
		signedUp2.getEntry().addLap(new Lap(2, Duration.ofMinutes(15)));
		signedUp2.getEntry().addLap(new Lap(3, Duration.ofMinutes(15)));	
		
		assertEquals(3, race.leadEntrylapsCompleted());		
	}
	
	@Test
	void given_twoEntriesHaveCompletedTheSameNumberOfLaps_when_requestLeadEntryInRace_then_returnsEntryWithFastestTime() {
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("competitor Two");
		DinghyClass dinghyClass = new DinghyClass("Comet", 1, 1000);
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		Entry entry1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get().getEntry();
		Entry entry2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get().getEntry();
		
		entry1.addLap(new Lap(1, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(1, Duration.ofMinutes(15)));
		entry1.addLap(new Lap(2, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(2, Duration.ofMinutes(15)));
		
		assertEquals(entry1, race.getLeadEntry());		
	}
	
	@Test
	void given_oneEntryHasCompletedMoreLaps_when_requestLeadEntryInRace_then_returnsEntryWithMostLaps() {
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("competitor Two");
		DinghyClass dinghyClass = new DinghyClass("Comet", 1, 1000);
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		Entry entry1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get().getEntry();
		Entry entry2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get().getEntry();
		
		entry1.addLap(new Lap(1, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(1, Duration.ofMinutes(15)));
		entry1.addLap(new Lap(2, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(2, Duration.ofMinutes(15)));
		entry2.addLap(new Lap(3, Duration.ofMinutes(15)));		
		
		assertEquals(entry2, race.getLeadEntry());
	}
	
	@Test
	void given_NoBoatsHaveSignedUp_when_requestLeadEntryInRace_then_returnsNull() {
		DirectRace race = new DirectRace();
		assertNull(race.getLeadEntry());		
	}
	
	@Test
	void given_noLapsHaveBeenCompleted_when_requestLapsForecast_then_returnsNumberOfPlannedLaps() {
		DinghyClass dinghyClass = new DinghyClass("Test", 1, 1000);
		Set<DinghyClass> dinghyClasses = new HashSet<DinghyClass>();
		Fleet fleet = new Fleet("Test Fleet", dinghyClasses);
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		new Entry(competitor1, dinghy1);
		new Entry(competitor2, dinghy2);
		
		assertEquals(5, race.getLapForecast());
	}
	
	@Test
	void given_lapsHaveBeenCompleted_when_requestLapsForecast_then_returnsNumberOfTotalLapsForecastToBeCompleted() {
		DinghyClass dinghyClass = new DinghyClass("Test", 1, 1000);
		Set<DinghyClass> dinghyClasses = new HashSet<DinghyClass>();
		Fleet fleet = new Fleet("Test Fleet", dinghyClasses);
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();

		signedUp1.getEntry().addLap(new Lap(1, Duration.ofMinutes(14)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofMinutes(15)));
		signedUp1.getEntry().addLap(new Lap(2, Duration.ofMinutes(13)));
		signedUp2.getEntry().addLap(new Lap(2, Duration.ofMinutes(15)));
		
		assertEquals(18D/13D + 2, race.getLapForecast());
	}
	
	@Test
	void given_NoBoatsHaveSignedUp_when_requestLapsForecast_then_returnsNumberOfPlannedLaps() {
		Set<DinghyClass> dinghyClasses = new HashSet<DinghyClass>();
		Fleet fleet = new Fleet("Test Fleet", dinghyClasses);
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		assertEquals(5, race.getLapForecast());
	}

	@Test
	void given_raceDurationHasElapsedAndABoatHasCrossedTheLineOnAfterDurationElapsed_when_requestLapsForecast_then_returnNumberOfLapsCompletedByLeadBoat() {
		DinghyClass dinghyClass = new DinghyClass("Test", 1, 1000);
		Set<DinghyClass> dinghyClasses = new HashSet<DinghyClass>();
		Fleet fleet = new Fleet("Test Fleet", dinghyClasses);
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		
		race.setPlannedLaps(5);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		Entry entry1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get().getEntry();
		Entry entry2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get().getEntry();
		
		entry1.addLap(new Lap(1, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(1, Duration.ofMinutes(15)));
		entry1.addLap(new Lap(2, Duration.ofMinutes(13)));
		entry2.addLap(new Lap(2, Duration.ofMinutes(15)));
		entry1.addLap(new Lap(3, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(3, Duration.ofMinutes(15)));
		entry1.addLap(new Lap(4, Duration.ofMinutes(15)));
		
		assertEquals(4D, race.getLapForecast());
	}

	// Shorten course
	@Test
	void when_raceIsShortened_then_updatesEntriesNowOnLastLap() {
		DinghyClass dinghyClass = new DinghyClass("Test", 1, 1000);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 3, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		race.signUp(competitor1, dinghy1);
		Entry entry1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get().getEntry();
		entry1.addLap(new Lap(1, Duration.ofMinutes(14)));
		race.setPlannedLaps(2);
		assertTrue(entry1.getOnLastLap());
	}
	
	@Test
	void when_raceIsShortened_then_updatesEntriesNowFinshedRace() {
		DinghyClass dinghyClass = new DinghyClass("Test", 1, 1000);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 4, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		race.signUp(competitor1, dinghy1);
		Entry entry1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get().getEntry();
		entry1.addLap(new Lap(1, Duration.ofMinutes(14)));
		entry1.addLap(new Lap(2, Duration.ofMinutes(14)));
		race.setPlannedLaps(2);
		assertTrue(entry1.getFinishedRace());
	}

	// Pursuit Race Position Tests
	@Test
	void given_race_is_pursuit_when_oneEntryHasFinishedLap_then_correctly_calculatesPosition() {
		DinghyClass dinghyClass = new DinghyClass("Test", 1, 1000);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		race.signUp(competitor1, dinghy1);
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofMinutes(14)));
		
		assertEquals(1, signedUp1.getPosition());
	}

	@Test
	void given_race_is_pursuit_when_twoEntriesHaveFinishedDifferentNumberOfLaps_then_correctly_calculatesPositions() {
		DinghyClass dinghyClass = new DinghyClass("Test", 1, 1000);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(840)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(540)));
		signedUp2.getEntry().addLap(new Lap(2, Duration.ofSeconds(540)));

		assertEquals(2, signedUp1.getPosition());
		assertEquals(1, signedUp2.getPosition());
	}
		
	@Test
	void given_race_is_pursuit_when_twoEntriesHaveFinishedTheSameNumberOfLaps_then_correctly_calculatesPositions() {
		DinghyClass dinghyClass = new DinghyClass("Test", 1, 1000);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(840)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(839)));

		assertEquals(2, signedUp1.getPosition());
		assertEquals(1, signedUp2.getPosition());
	}
	
	@Test
	void given_race_is_pursuit_when_twoEntriesHaveFinishedTheSameNumberOfLapsAndOneHasRetired_then_correctly_calculatesPositions() {
		DinghyClass dinghyClass = new DinghyClass("Test", 1, 1000);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(840)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(240)));
		signedUp2.getEntry().setScoringAbbreviation("RET");
		
		assertEquals(1, signedUp1.getPosition());
		assertEquals(2, signedUp2.getPosition());
	}
	
	@Test
	void given_race_is_pursuit_when_newPositionHigherThanOldPosition_when_positionOfEntryUpdated_then_updatesPositionsOfOtherEntries() {
		DinghyClass dinghyClass = new DinghyClass("Test", 1, 1000);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Competitor competitor4 = new Competitor("Competitor Four");
		Dinghy dinghy1 = new Dinghy("1", dinghyClass);
		Dinghy dinghy2 = new Dinghy("2", dinghyClass);
		Dinghy dinghy3 = new Dinghy("3", dinghyClass);
		Dinghy dinghy4 = new Dinghy("4", dinghyClass);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		race.signUp(competitor3, dinghy3);
		race.signUp(competitor4, dinghy4);
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		SignedUp signedUp3 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor3)).findFirst().get();
		SignedUp signedUp4 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor4)).findFirst().get();
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(240))); // position 1
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(250))); // position 3
		signedUp3.getEntry().addLap(new Lap(1, Duration.ofSeconds(245))); // position 2
		signedUp4.getEntry().addLap(new Lap(1, Duration.ofSeconds(260))); // position 4
		
		race.updateEntryPositions(signedUp2, 2);
		
		assertEquals(1, signedUp1.getPosition());
		assertEquals(2, signedUp2.getPosition());
		assertEquals(3, signedUp3.getPosition());
		assertEquals(4, signedUp4.getPosition());
	}
	
	@Test
	void given_race_is_pursuit_when_oldPositionWasNull_when_positionOfEntryUpdated_then_doesNotUpdatesPositionOfAnyEntryLowerThanNumberOfEntriesInRace() {
		DinghyClass dinghyClass = new DinghyClass("Test", 1, 1000);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Competitor competitor4 = new Competitor("Competitor Four");
		Dinghy dinghy1 = new Dinghy("1", dinghyClass);
		Dinghy dinghy2 = new Dinghy("2", dinghyClass);
		Dinghy dinghy3 = new Dinghy("3", dinghyClass);
		Dinghy dinghy4 = new Dinghy("4", dinghyClass);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		race.signUp(competitor3, dinghy3);
		race.signUp(competitor4, dinghy4);
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		SignedUp signedUp3 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor3)).findFirst().get();
		SignedUp signedUp4 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor4)).findFirst().get();
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(240))); // position 1
		signedUp2.getEntry().setScoringAbbreviation("DNS");
		signedUp3.getEntry().addLap(new Lap(1, Duration.ofSeconds(245))); // position 2
		
		assertNull(signedUp4.getPosition());
	}
	
	@Test
	void given_race_is_pursuit_when_newPositionLowerThanOldPosition_when_positionOfEntryUpdated_then_updatesPositionsOfOtherEntries() {
		DinghyClass dinghyClass = new DinghyClass("Test", 1, 1000);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Competitor competitor4 = new Competitor("Competitor Four");
		Dinghy dinghy1 = new Dinghy("1", dinghyClass);
		Dinghy dinghy2 = new Dinghy("2", dinghyClass);
		Dinghy dinghy3 = new Dinghy("3", dinghyClass);
		Dinghy dinghy4 = new Dinghy("4", dinghyClass);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		race.signUp(competitor3, dinghy3);
		race.signUp(competitor4, dinghy4);
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		SignedUp signedUp3 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor3)).findFirst().get();
		SignedUp signedUp4 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor4)).findFirst().get();
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(240))); // position 1
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(250))); // position 3
		signedUp3.getEntry().addLap(new Lap(1, Duration.ofSeconds(245))); // position 2
		signedUp4.getEntry().addLap(new Lap(1, Duration.ofSeconds(260))); // position 4

		race.updateEntryPositions(signedUp3, 3);
		assertEquals(1, signedUp1.getPosition());
		assertEquals(2, signedUp2.getPosition());
		assertEquals(3, signedUp3.getPosition());
		assertEquals(4, signedUp4.getPosition());
	}
		
	@Test
	void given_race_is_pursuit_when_noPositionsCalculated_when_positionOfEntryUpdated_then_updatesOnlyPositionOfEntry() {
		DinghyClass dinghyClass = new DinghyClass("Test", 1, 1000);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Competitor competitor4 = new Competitor("Competitor Four");
		Dinghy dinghy1 = new Dinghy("1", dinghyClass);
		Dinghy dinghy2 = new Dinghy("2", dinghyClass);
		Dinghy dinghy3 = new Dinghy("3", dinghyClass);
		Dinghy dinghy4 = new Dinghy("4", dinghyClass);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		race.signUp(competitor3, dinghy3);
		race.signUp(competitor4, dinghy4);

		SignedUp signedUp1 = race.signedUp.stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.signedUp.stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		SignedUp signedUp3 = race.signedUp.stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor3)).findFirst().get();
		SignedUp signedUp4 = race.signedUp.stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor4)).findFirst().get();
		race.updateEntryPositions(signedUp3, 3);
		assertEquals(null, signedUp1.getPosition());
		assertEquals(null, signedUp2.getPosition());
		assertEquals(3, signedUp3.getPosition());
		assertEquals(null, signedUp4.getPosition());
	}

	@Test
	void given_race_race_is_pursuit_when_scoringAbbreviationSetForEntry_then_positionSetEqualToNumberOfEntriesInRace() {
		DinghyClass dinghyClass = new DinghyClass("Test", 1, 1000);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Competitor competitor4 = new Competitor("Competitor Four");
		Dinghy dinghy1 = new Dinghy("1", dinghyClass);
		Dinghy dinghy2 = new Dinghy("2", dinghyClass);
		Dinghy dinghy3 = new Dinghy("3", dinghyClass);
		Dinghy dinghy4 = new Dinghy("4", dinghyClass);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		race.signUp(competitor3, dinghy3);
		race.signUp(competitor4, dinghy4);
		
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		signedUp2.getEntry().setScoringAbbreviation("DNS");
		
		assertEquals(4, signedUp2.getPosition());
	}

	// Fleet Race Position Tests
	// Lap advantage is when an entry gets a better corrected time than another boat with the same or slower handicap by sailing less laps; for example if the wind dies for a previously faster boat on the last lap

	// Same Portsmouth Number
	@Test
	void given_raceIsFleetAndEntriesHaveSamePortsmouthNumberAndSailedTheSameLaps_whenOneEntryHasFasterCorrectedTime_then_entryWithFasterTimeWins() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(660)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(600)));
		signedUp1.getEntry().addLap(new Lap(2, Duration.ofSeconds(660))); // corrected time 1265.580
		signedUp2.getEntry().addLap(new Lap(2, Duration.ofSeconds(600))); // corrected time 1150.527

		assertEquals(1, signedUp2.getPosition());
		assertEquals(2, signedUp1.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntriesHaveSamePortsmouthNumberAndSailedTheSameLapsAndHaveTheSameTime_then_theyAreAllAssignedTheHighestPositionOfAnyEntryWithThatCorrectedTime() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(660))); // corrected time 632.790
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(660))); // corrected time 632.790
		assertEquals(1, signedUp1.getPosition());
		assertEquals(1, signedUp2.getPosition());
	}

	@Test
	void given_raceIsFleetAndEntriesHaveSamePortsmouthNumber_when_oneEntrySailsMoreLapsAndHasFasterCorrectedTime_then_entryWithFasterTimeWins() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(660)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(1200))); // corrected time 2301.054
		signedUp1.getEntry().addLap(new Lap(2, Duration.ofSeconds(660))); // corrected time 1265.580

		assertEquals(1, signedUp1.getPosition());
		assertEquals(2, signedUp2.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntriesHaveSamePortsmouthNumber_when_entrySailsMoreLapsAndHasSlowerCorrectedTime_then_entryWithMoreLapsWins() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(600)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(700))); // corrected time 1342.282
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(900))); // corrected time 1438.159

		assertEquals(1, signedUp1.getPosition());
		assertEquals(2, signedUp2.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntriesHaveSamePortsmouthNumberAndSameCorrectedTime_when_entrySailsMoreLaps_then_entryWithMoreLapsWins() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(500)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(500))); // corrected time 958.773
		signedUp1.getEntry().addLap(new Lap(2, Duration.ofSeconds(500))); // corrected time 958.773

		assertEquals(1, signedUp1.getPosition());
		assertEquals(2, signedUp2.getPosition());
	}
	
	// Slower Portsmouth Number
	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumber_when_largerPortsmouthNumberEntrySailsMoreLapsInSlowerCorrectedTime_then_slowerCorrectedTimeEntryWins() {
		DinghyClass graduate = new DinghyClass("Graduate", 2, 1110);
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		Entry entry1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get().getEntry();
		Entry entry2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get().getEntry();
		entry1.addLap(new Lap(1, Duration.ofSeconds(600)));
		entry2.addLap(new Lap(1, Duration.ofSeconds(600))); // corrected time 1150.527
		entry1.addLap(new Lap(2, Duration.ofSeconds(700))); // corrected time 1171.171

		SignedUp signedUp1 = race.signedUp.stream().filter(signedUp -> signedUp.getEntry().equals(entry1)).findFirst().get();
		SignedUp signedUp2 = race.signedUp.stream().filter(signedUp -> signedUp.getEntry().equals(entry2)).findFirst().get();
		assertEquals(1, signedUp1.getPosition());
		assertEquals(2, signedUp2.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumberAndSameCorrectedTime_when_entrySailsMoreLaps_then_entryWithMoreLapsWins() {
		DinghyClass graduate = new DinghyClass("Graduate", 2, 1000);
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 500);
		
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("rif1234", graduate);
		Dinghy dinghy2 = new Dinghy("rif4567", scorpion);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		SignedUp signedUp1 = race.signedUp.stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.signedUp.stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(200)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(250))); // corrected time 1000.00
		signedUp1.getEntry().addLap(new Lap(2, Duration.ofSeconds(800))); // corrected time 1000.00
		
		assertEquals(1, signedUp1.getPosition());
		assertEquals(2, signedUp2.getPosition());
	}

	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumber_when_largerPortsmouthNumberEntrySailsMoreLapsAndHasFasterCorrectedTime_then_largerPortsmouthNumberEntryWins() {
		DinghyClass graduate = new DinghyClass("Graduate", 2, 1110);
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(600)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(700))); // corrected time 1081.081
		signedUp1.getEntry().addLap(new Lap(2, Duration.ofSeconds(600))); // corrected time 1342.282
		
		assertEquals(1, signedUp1.getPosition());
		assertEquals(2, signedUp2.getPosition());
	}

	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumberAndSailedTheSameLaps_when_largerPortsmouthNumberHasSlowerCorrectedTime_then_largerPortsmouthNumberEntryLoses() {
		DinghyClass graduate = new DinghyClass("Graduate", 2, 1110);
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(600)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(500)));
		signedUp1.getEntry().addLap(new Lap(2, Duration.ofSeconds(600))); // corrected time 1081.081
		signedUp2.getEntry().addLap(new Lap(2, Duration.ofSeconds(500))); // corrected time 958.773

		assertEquals(1, signedUp2.getPosition());
		assertEquals(2, signedUp1.getPosition());
	}

	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumberAndSailedTheSameLapsAndHaveTheSameCorrecedTime_then_entriesAllAssignedTheHighestPositionOfAnyEntryWithThatCorrectedTime() {
		DinghyClass graduate = new DinghyClass("Graduate", 2, 1500);
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1000);
		
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(750)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(500)));
		signedUp1.getEntry().addLap(new Lap(2, Duration.ofSeconds(750))); // corrected time 1000.000
		signedUp2.getEntry().addLap(new Lap(2, Duration.ofSeconds(500))); // corrected time 1000.000

		assertEquals(1, signedUp1.getPosition());
		assertEquals(1, signedUp2.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumberAndSailedTheSameLaps_when_largerPortsmouthNumberEntryHasFasterCorrectedTime_then_entryWithFasterTimeWins() {
		DinghyClass graduate = new DinghyClass("Graduate", 2, 1110);
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(600)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(600)));
		signedUp1.getEntry().addLap(new Lap(2, Duration.ofSeconds(600))); // corrected time 1081.081
		signedUp2.getEntry().addLap(new Lap(2, Duration.ofSeconds(600))); // corrected time 1150.527

		assertEquals(1, signedUp1.getPosition());
		assertEquals(2, signedUp2.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumber_when_largerPortsmouthNumberSailedLessLapsAndEntryHasSlowerCorrectedTime_then_entryWithFasterCorrectedTimeWins() {
		DinghyClass graduate = new DinghyClass("Graduate", 2, 1110);
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		Entry entry1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get().getEntry();
		Entry entry2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get().getEntry();
		entry2.addLap(new Lap(1, Duration.ofSeconds(500)));
		entry1.addLap(new Lap(1, Duration.ofSeconds(600))); // corrected time 1081.081
		entry2.addLap(new Lap(2, Duration.ofSeconds(500))); // corrected time 958.773

		SignedUp signedUp1 = race.signedUp.stream().filter(signedUp -> signedUp.getEntry().equals(entry1)).findFirst().get();
		SignedUp signedUp2 = race.signedUp.stream().filter(signedUp -> signedUp.getEntry().equals(entry2)).findFirst().get();
		assertEquals(1, signedUp2.getPosition());
		assertEquals(2, signedUp1.getPosition());
	}

	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumberAndSameCorrectedTime_when_largerPortsmouthNumberEntrySailedLessLaps_then_entriesAllAssignedTheHighestPositionOfAnyEntryWithThatCorrectedTime() {
		DinghyClass graduate = new DinghyClass("Graduate", 2, 1500);
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1000);
		
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(500)));
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(750))); // corrected time 1000.000
		signedUp2.getEntry().addLap(new Lap(2, Duration.ofSeconds(500))); // corrected time 1000.000

		assertEquals(1, signedUp1.getPosition());
		assertEquals(1, signedUp2.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumber_when_largerPortsmouthNumberEntrySailedLessLapsAndHasAFasterCorrectedTime_then_entryWithFasterCorrectedTimeWins() {
		DinghyClass graduate = new DinghyClass("Graduate", 2, 1110);
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);

		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(750)));
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(700))); // corrected time 1261.261
		signedUp2.getEntry().addLap(new Lap(2, Duration.ofSeconds(750))); // corrected time 1438.159

		assertEquals(1, signedUp1.getPosition());
		assertEquals(2, signedUp2.getPosition());
	}

	// Scoring abbreviation
	@Test
	void given_raceIsFleet_when_scoringAbbreviationSetForEntry_then_positionSetEqualToNumberOfEntriesInRace() {
		DinghyClass dinghyClass = new DinghyClass("Test", 1, 1000);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Competitor competitor4 = new Competitor("Competitor Four");
		Dinghy dinghy1 = new Dinghy("1", dinghyClass);
		Dinghy dinghy2 = new Dinghy("2", dinghyClass);
		Dinghy dinghy3 = new Dinghy("3", dinghyClass);
		Dinghy dinghy4 = new Dinghy("4", dinghyClass);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		race.signUp(competitor3, dinghy3);
		race.signUp(competitor4, dinghy4);
		
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		signedUp2.getEntry().setScoringAbbreviation("DNS");
		
		assertEquals(4, signedUp2.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntriesHaveSamePortsmouthNumberSailedSameNumberLaps_whenFasterEntryHasScoringAbbreviation_then_slowerEntryPlacedFirst() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(500)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(600)));
		signedUp1.getEntry().addLap(new Lap(2, Duration.ofSeconds(500))); // corrected time 958.773
		signedUp2.getEntry().addLap(new Lap(2, Duration.ofSeconds(600))); // corrected time 1150.527
		signedUp1.getEntry().setScoringAbbreviation("RET");

		assertEquals(1, signedUp2.getPosition());
		assertEquals(2, signedUp1.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntriesHaveSamePortsmouthNumber_when_boatWithTheMedianTimeHasScoringAbbreviation_then_medianTimedEntryPlacedLast() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		Dinghy dinghy3 = new Dinghy("8974", scorpion);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		race.signUp(competitor3, dinghy3);
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		SignedUp signedUp3 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor3)).findFirst().get();
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(900))); // corrected time 719.080
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(750))); // corrected time 958.773
		signedUp3.getEntry().addLap(new Lap(1, Duration.ofSeconds(1000))); // corrected time 527.325
		signedUp1.getEntry().setScoringAbbreviation("RET");

		assertEquals(1, signedUp2.getPosition());
		assertEquals(2, signedUp3.getPosition());
		assertEquals(3, signedUp1.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntriesHaveSamePortsmouthNumber_when_boatWithTheMedianTimeHasLapAdvantageOverSlowestBoatAndSlowestBoatHasScoringAbbreviation_then_medianTimedEntryPlacedSecondAndEntryWithScoringAbbreviationPlacedLast() {
		DinghyClass classOne = new DinghyClass("Class 1", 2, 900);
		DinghyClass classTwo = new DinghyClass("Class 2", 2, 1000);
		DinghyClass classThree = new DinghyClass("Class 3", 2, 1100);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Dinghy dinghy1 = new Dinghy("1234", classOne);
		Dinghy dinghy2 = new Dinghy("4567", classTwo);
		Dinghy dinghy3 = new Dinghy("8974", classThree);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		race.signUp(competitor3, dinghy3);
		
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		SignedUp signedUp3 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor3)).findFirst().get();
		
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(450)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(600))); // corrected time 1200.000
		signedUp3.getEntry().addLap(new Lap(1, Duration.ofSeconds(750)));
		signedUp1.getEntry().addLap(new Lap(2, Duration.ofSeconds(450))); // corrected time 1000.000
		signedUp3.getEntry().addLap(new Lap(1, Duration.ofSeconds(750))); // corrected time 1363.636
		signedUp2.getEntry().setScoringAbbreviation("RET");

		assertEquals(1, signedUp1.getPosition());
		assertEquals(2, signedUp3.getPosition());
		assertEquals(3, signedUp2.getPosition());
	}
	
	@Test
	void given_raceIsFleet_when_moreThanOneGroupOfEntriesHaveTheSameCorrectedTimeAndEachGroupHasADifferentCorrectedTime_then_theyAreAllAssignedTheLowestPositionOfAnyEntryWithThatCorrectedTime() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
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
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		race.signUp(competitor3, dinghy3);
		race.signUp(competitor4, dinghy4);
		race.signUp(competitor5, dinghy5);
		
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		SignedUp signedUp3 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor3)).findFirst().get();
		SignedUp signedUp4 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor4)).findFirst().get();
		SignedUp signedUp5 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor5)).findFirst().get();
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(600))); // 1
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(660))); // 4
		signedUp3.getEntry().addLap(new Lap(1, Duration.ofSeconds(660))); // 4
		signedUp4.getEntry().addLap(new Lap(1, Duration.ofSeconds(650))); // 3
		signedUp5.getEntry().addLap(new Lap(1, Duration.ofSeconds(600))); // 1

		assertEquals(1, signedUp1.getPosition());
		assertEquals(1, signedUp5.getPosition());
		assertEquals(3, signedUp4.getPosition());
		assertEquals(4, signedUp2.getPosition());
		assertEquals(4, signedUp3.getPosition());
	}

	// lap removed
	@Test
	void given_raceIsFleetAndOnlyEntryHasSailedTheMostLapsAndThatEntryWasNotTheLeadBoatOnTheLastLapSailedByMoreThanOneBoat_when_lapRemovedFromLeadBoat_then_correctlyCalculatesPositions() {
	// when lap is removed from the lead boat after which it is no longer the lead boat need to recalculate positions for all boats
		DinghyClass scorpion = new DinghyClass("Scorpion", 2, 1043);
		
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace race = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		race.signUp(competitor1, dinghy1);
		race.signUp(competitor2, dinghy2);
		SignedUp signedUp1 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = race.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(300))); // corrected time 300.000
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(310)));
		signedUp2.getEntry().addLap(new Lap(2, Duration.ofSeconds(90)));
		signedUp2.getEntry().removeLap(new Lap(2, Duration.ofSeconds(90))); // corrected time 310.000

		assertEquals(1, signedUp1.getPosition());
		assertEquals(2, signedUp2.getPosition());
	}
}
