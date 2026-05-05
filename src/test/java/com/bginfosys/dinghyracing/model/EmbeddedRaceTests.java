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
import static org.hamcrest.CoreMatchers.notNullValue;
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
import com.bginfosys.dinghyracing.exceptions.EntryNotInHostRaceException;

public class EmbeddedRaceTests {

	// Constructor Tests
	@Test
	void raceCreated() {
		EmbeddedRace race = new EmbeddedRace();
		
		assertThat(race, notNullValue());
	}
	
	@Test
	void whenParametersProvided_then_raceCreated() {
		Fleet fleet = new Fleet("Test Fleet");
		Set<Race> hosts = new HashSet<Race>();
		EmbeddedRace race = new EmbeddedRace("Test Race", fleet, hosts);
		
		assertThat(race, notNullValue());
		assertEquals(race.getName(), "Test Race");
		assertEquals(race.getFleet(), fleet);
		assertEquals(race.getHosts(), hosts);
	}
	
	// getter and setter tests
	
	
	// operation tests
	// sign up tests
	/*
	 * Signs supplied entry up for race
	 */
	@Test
	void givenEntrySignedUpToHostRace_then_SignsUpToEmbeddedRace() {
		Fleet fleet = new Fleet("Fleet", new HashSet<DinghyClass>());
		DirectRace directRace = new DirectRace("Direct Race", LocalDateTime.of(2026, 4, 12, 10, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.RRS26);
		Set<Race> hosts = new HashSet<Race>();
		hosts.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hosts);
		Competitor helm = new Competitor("H Elm");
		DinghyClass dinghyClass = new DinghyClass("Class", 1, 1000, "");
		Dinghy dinghy = new Dinghy("1", dinghyClass);
		SignedUp signedUp = directRace.signUp(helm, dinghy);
		signedUp = embeddedRace.signUp(signedUp.getEntry());
		assertTrue(embeddedRace.getSignedUp().contains(signedUp));
		assertTrue(signedUp.getEntry().getDinghy().equals(dinghy));
		assertTrue(signedUp.getEntry().getHelm().equals(helm));
	}

	@Test
	void givenEntryNotSignedUpToHostRace_then_throwsEntryNotInHostRaceException() {
		Fleet fleet = new Fleet("Fleet", new HashSet<DinghyClass>());
		DirectRace directRace1 = new DirectRace("Direct Race 1", LocalDateTime.of(2026, 4, 12, 10, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.RRS26);
		DirectRace directRace2 = new DirectRace("Direct Race 2", LocalDateTime.of(2026, 4, 12, 10, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.RRS26);
		Set<Race> hosts = new HashSet<Race>();
		hosts.add(directRace1);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hosts);
		Competitor helm = new Competitor("H Elm");
		DinghyClass dinghyClass = new DinghyClass("Class", 1, 1000, "");
		Dinghy dinghy = new Dinghy("1", dinghyClass);
		SignedUp signedUp = directRace2.signUp(helm, dinghy);
		assertThrows(EntryNotInHostRaceException.class, () -> {
			embeddedRace.signUp(signedUp.getEntry());
		});
	}
	
	/*
	 * signing_up_dinghy_class_allowed_by_race
	 */
	@Test
	void givenDinghyClassNotAllowedByRace_when_dinghyAndHelmProvided_then_dinghyClassMisMatchException() {
		Competitor helm = new Competitor("Bob");
		
		DinghyClass cometClass = new DinghyClass("Comet", 1, 1000);
		DinghyClass optimistClass = new DinghyClass("Optimist", 1, 1000);
		
		Dinghy cometDinghy = new Dinghy("1234", cometClass);
		
		Set<DinghyClass> optimistFleetDinghyClasses = new HashSet<DinghyClass>();
		optimistFleetDinghyClasses.add(optimistClass);
		Fleet optimistFleet = new Fleet("Optimist", optimistFleetDinghyClasses);
		
		Set<DinghyClass> cometFleetDinghyClasses = new HashSet<DinghyClass>();
		cometFleetDinghyClasses.add(cometClass);
		Fleet cometFleet = new Fleet("Comet", cometFleetDinghyClasses);
		
		DirectRace directRace = new DirectRace("Comet Race", LocalDateTime.of(2025, 10, 17, 19, 30), cometFleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.RRS26);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Optimist Embedded Race", optimistFleet, hostRaces);
		
		SignedUp signedUp = directRace.signUp(helm, cometDinghy);
		
		assertThrows(DinghyClassMismatchException.class, () -> {
			embeddedRace.signUp(signedUp.getEntry());
		});
	}
	
	/*
	 * helm_entered_once
	 */
	@Test
	void givenHelmAlreadySignedUpForRace_when_dinghyAndHelmProvided_then_competitorAlreadySignedUpException() {
		Competitor helm = new Competitor("Bob");
		DinghyClass cometClass = new DinghyClass("Comet", 1, 1000);
		Dinghy cometDinghy1 = new Dinghy("1234", cometClass);
		Dinghy cometDinghy2 = new Dinghy("6789", cometClass);
		Fleet handicapFleet = new Fleet("Open Handicap");
		DirectRace cometRace1 = new DirectRace("Race", LocalDateTime.of(2025, 10, 17, 17, 30), handicapFleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.RRS26);
		DirectRace cometRace2 = new DirectRace("Race", LocalDateTime.of(2025, 10, 17, 19, 30), handicapFleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.RRS26);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(cometRace1);
		hostRaces.add(cometRace2);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", handicapFleet, hostRaces);
		
		SignedUp cometRace1SignedUp = cometRace1.signUp(helm, cometDinghy1);
		SignedUp cometRace2SignedUp = cometRace2.signUp(helm, cometDinghy2);
		
		embeddedRace.signUp(cometRace1SignedUp.getEntry());
		
		assertThrows(CompetitorAlreadySignedUpException.class, () -> {
			embeddedRace.signUp(cometRace2SignedUp.getEntry());
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
		Fleet handicapFleet = new Fleet("Open Handicap");
		
		Set<DinghyClass> cometFleetDinghyClasses = new HashSet<DinghyClass>();
		cometFleetDinghyClasses.add(dinghyClass);
		Fleet cometFleet = new Fleet("Comet", cometFleetDinghyClasses);
		
		DirectRace handicapRace = new DirectRace("Handicap Race", LocalDateTime.of(2025, 10, 17, 19, 30), handicapFleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.RRS26);
		DirectRace cometRace = new DirectRace("Comet Race", LocalDateTime.of(2025, 10, 17, 19, 40), cometFleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.RRS26);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(handicapRace);
		hostRaces.add(cometRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", handicapFleet, hostRaces);
		
		SignedUp handicapSignedUp = handicapRace.signUp(helm1, dinghy1);
		SignedUp cometSignedUp = cometRace.signUp(helm2, dinghy1);
		
		embeddedRace.signUp(handicapSignedUp.getEntry());
		
		assertThrows(DinghyAlreadySignedUpException.class, () -> {
			embeddedRace.signUp(cometSignedUp.getEntry());
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
		DinghyClass dinghyClass = new DinghyClass("Scorpion", 2, 1000);
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("1234", dinghyClass);
		
		Fleet fleet = new Fleet("Open");
		DirectRace race1 = new DirectRace("Race", LocalDateTime.of(2025, 10, 17, 17, 30), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.RRS26);
		DirectRace race2 = new DirectRace("Race", LocalDateTime.of(2025, 10, 17, 19, 30), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.RRS26);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(race1);
		hostRaces.add(race2);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		SignedUp signedUp1 = race1.signUp(helm1, crew, dinghy1);
		SignedUp signedUp2 = race2.signUp(helm2, crew, dinghy2);
				
		embeddedRace.signUp(signedUp1.getEntry());
		
		assertThrows(CompetitorAlreadySignedUpException.class, () -> {
			embeddedRace.signUp(signedUp2.getEntry());
		});
	}
	
	/*
	 * Lap completed tests
	 */	
	@Test
	void given_noLapsCompleted_when_requestNumberOfLapsCompletedByLeadEntry_then_returnsZero() {
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		DinghyClass dinghyClass = new DinghyClass("Comet", 1, 1000);
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		SignedUp signedUp = directRace.signUp(competitor1, dinghy1); 
		embeddedRace.signUp(signedUp.getEntry());
				
		assertEquals(0, embeddedRace.leadEntrylapsCompleted());
	}
	
	@Test
	void given_TwoEntriesHaveCompletedTheSameNumberOfLaps_when_requestNumberOfLapsCompletedByLeadEntry_then_returnsNumberOFLaps() {
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("competitor Two");
		DinghyClass dinghyClass = new DinghyClass("Comet", 1, 1000);
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		
		Entry entry1 = signedUp1.getEntry();
		Entry entry2 = signedUp2.getEntry();
		
		embeddedRace.signUp(entry1);
		embeddedRace.signUp(entry2);
		
		entry1.addLap(new Lap(1, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(1, Duration.ofMinutes(15)));
		entry1.addLap(new Lap(2, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(2, Duration.ofMinutes(15)));
		
		assertEquals(embeddedRace.leadEntrylapsCompleted(), 2);		
	}

	@Test
	void given_OneEntryHasCompletedMoreLaps_when_requestNumberOfLapsCompletedByLeadEntry_then_returnsNumberOfLaps() {
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("competitor Two");
		DinghyClass dinghyClass = new DinghyClass("Comet", 1, 1000);
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		
		Entry entry1 = signedUp1.getEntry();
		Entry entry2 = signedUp2.getEntry();
		
		embeddedRace.signUp(entry1);
		embeddedRace.signUp(entry2);

		signedUp1.getEntry().addLap(new Lap(1, Duration.ofMinutes(14)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofMinutes(15)));
		signedUp1.getEntry().addLap(new Lap(2, Duration.ofMinutes(14)));
		signedUp2.getEntry().addLap(new Lap(2, Duration.ofMinutes(15)));
		signedUp2.getEntry().addLap(new Lap(3, Duration.ofMinutes(15)));	
		
		assertEquals(3, embeddedRace.leadEntrylapsCompleted());		
	}
	
	@Test
	void given_twoEntriesHaveCompletedTheSameNumberOfLaps_when_requestLeadEntryInRace_then_returnsEntryWithFastestTime() {
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("competitor Two");
		DinghyClass dinghyClass = new DinghyClass("Comet", 1, 1000);
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		
		Entry entry1 = signedUp1.getEntry();
		Entry entry2 = signedUp2.getEntry();
		
		embeddedRace.signUp(entry1);
		embeddedRace.signUp(entry2);
		
		entry1.addLap(new Lap(1, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(1, Duration.ofMinutes(15)));
		entry1.addLap(new Lap(2, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(2, Duration.ofMinutes(15)));
		
		assertEquals(entry1, embeddedRace.getLeadEntry());		
	}
	
	@Test
	void given_oneEntryHasCompletedMoreLaps_when_requestLeadEntryInRace_then_returnsEntryWithMostLaps() {
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("competitor Two");
		DinghyClass dinghyClass = new DinghyClass("Comet", 1, 1000);
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);

		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		
		Entry entry1 = signedUp1.getEntry();
		Entry entry2 = signedUp2.getEntry();
		
		embeddedRace.signUp(entry1);
		embeddedRace.signUp(entry2);
		
		entry1.addLap(new Lap(1, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(1, Duration.ofMinutes(15)));
		entry1.addLap(new Lap(2, Duration.ofMinutes(14)));
		entry2.addLap(new Lap(2, Duration.ofMinutes(15)));
		entry2.addLap(new Lap(3, Duration.ofMinutes(15)));		
		
		assertEquals(entry2, embeddedRace.getLeadEntry());
	}
	
	@Test
	void given_NoBoatsHaveSignedUp_when_requestLeadEntryInRace_then_returnsNull() {
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		assertNull(embeddedRace.getLeadEntry());
	}

	// Pursuit Race Position Tests
	@Test
	void given_race_is_pursuit_when_oneEntryHasFinishedLap_then_correctly_calculatesPosition() {
		DinghyClass dinghyClass = new DinghyClass("Test", 1, 1000);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		SignedUp signedUp = directRace.signUp(competitor1, dinghy1);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		signedUp = embeddedRace.signUp(signedUp.getEntry());
		signedUp.getEntry().addLap(new Lap(1, Duration.ofMinutes(14)));
		
		assertEquals(1, signedUp.getPosition());
	}

	@Test
	void given_race_is_pursuit_when_twoEntriesHaveFinishedDifferentNumberOfLaps_then_correctly_calculatesPositions() {
		DinghyClass dinghyClass = new DinghyClass("Test", 1, 1000);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);

		SignedUp signedUp3 = embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp4 = embeddedRace.signUp(signedUp2.getEntry());
		
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(840)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(540)));
		signedUp2.getEntry().addLap(new Lap(2, Duration.ofSeconds(540)));

		assertEquals(2, signedUp3.getPosition());
		assertEquals(1, signedUp4.getPosition());
	}
		
	@Test
	void given_race_is_pursuit_when_twoEntriesHaveFinishedTheSameNumberOfLaps_then_correctly_calculatesPositions() {
		DinghyClass dinghyClass = new DinghyClass("Test", 1, 1000);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);

		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		
		SignedUp signedUp3 = embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp4 = embeddedRace.signUp(signedUp2.getEntry());
		
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(840)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(839)));

		assertEquals(2, signedUp3.getPosition());
		assertEquals(1, signedUp4.getPosition());
	}
	
	@Test
	void given_race_is_pursuit_when_twoEntriesHaveFinishedTheSameNumberOfLapsAndOneHasRetired_then_correctly_calculatesPositions() {
		DinghyClass dinghyClass = new DinghyClass("Test", 1, 1000);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);

		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("4567", dinghyClass);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		
		SignedUp signedUp3 = embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp4 = embeddedRace.signUp(signedUp2.getEntry());
		
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(840)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(240)));
		signedUp2.getEntry().setScoringAbbreviation("RET");
		
		assertEquals(1, signedUp3.getPosition());
		assertEquals(2, signedUp4.getPosition());
	}
	
	@Test
	void given_race_is_pursuit_when_newPositionHigherThanOldPosition_when_positionOfEntryUpdated_then_updatesPositionsOfOtherEntries() {
		DinghyClass dinghyClass = new DinghyClass("Test", 1, 1000);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Competitor competitor4 = new Competitor("Competitor Four");
		
		Dinghy dinghy1 = new Dinghy("1", dinghyClass);
		Dinghy dinghy2 = new Dinghy("2", dinghyClass);
		Dinghy dinghy3 = new Dinghy("3", dinghyClass);
		Dinghy dinghy4 = new Dinghy("4", dinghyClass);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		SignedUp signedUp3 = directRace.signUp(competitor3, dinghy3);
		SignedUp signedUp4 = directRace.signUp(competitor4, dinghy4);
		
		SignedUp signedUp5 = embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp6 = embeddedRace.signUp(signedUp2.getEntry());
		SignedUp signedUp7 = embeddedRace.signUp(signedUp3.getEntry());
		SignedUp signedUp8 = embeddedRace.signUp(signedUp4.getEntry());
		
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(240))); // position 1
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(250))); // position 3
		signedUp3.getEntry().addLap(new Lap(1, Duration.ofSeconds(245))); // position 2
		signedUp4.getEntry().addLap(new Lap(1, Duration.ofSeconds(260))); // position 4
		
		embeddedRace.updateEntryPositions(signedUp6, 2);
		
		assertEquals(1, signedUp5.getPosition());
		assertEquals(2, signedUp6.getPosition());
		assertEquals(3, signedUp7.getPosition());
		assertEquals(4, signedUp8.getPosition());
	}
	
	@Test
	void given_race_is_pursuit_when_oldPositionWasNull_when_positionOfEntryUpdated_then_doesNotUpdatesPositionOfAnyEntryLowerThanNumberOfEntriesInRace() {
		DinghyClass dinghyClass = new DinghyClass("Test", 1, 1000);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Competitor competitor4 = new Competitor("Competitor Four");
		
		Dinghy dinghy1 = new Dinghy("1", dinghyClass);
		Dinghy dinghy2 = new Dinghy("2", dinghyClass);
		Dinghy dinghy3 = new Dinghy("3", dinghyClass);
		Dinghy dinghy4 = new Dinghy("4", dinghyClass);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		SignedUp signedUp3 = directRace.signUp(competitor3, dinghy3);
		SignedUp signedUp4 = directRace.signUp(competitor4, dinghy4);
		
		embeddedRace.signUp(signedUp1.getEntry());
		embeddedRace.signUp(signedUp2.getEntry());
		embeddedRace.signUp(signedUp3.getEntry());
		SignedUp signedUp8 = embeddedRace.signUp(signedUp4.getEntry());
		
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(240))); // position 1
		signedUp2.getEntry().setScoringAbbreviation("DNS");
		signedUp3.getEntry().addLap(new Lap(1, Duration.ofSeconds(245))); // position 2
		
		assertNull(signedUp8.getPosition());
	}
	
	@Test
	void given_race_is_pursuit_when_newPositionLowerThanOldPosition_when_positionOfEntryUpdated_then_updatesPositionsOfOtherEntries() {
		DinghyClass dinghyClass = new DinghyClass("Test", 1, 1000);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Competitor competitor4 = new Competitor("Competitor Four");
		
		Dinghy dinghy1 = new Dinghy("1", dinghyClass);
		Dinghy dinghy2 = new Dinghy("2", dinghyClass);
		Dinghy dinghy3 = new Dinghy("3", dinghyClass);
		Dinghy dinghy4 = new Dinghy("4", dinghyClass);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		SignedUp signedUp3 = directRace.signUp(competitor3, dinghy3);
		SignedUp signedUp4 = directRace.signUp(competitor4, dinghy4);
		
		SignedUp signedUp5 = embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp6 = embeddedRace.signUp(signedUp2.getEntry());
		SignedUp signedUp7 = embeddedRace.signUp(signedUp3.getEntry());
		SignedUp signedUp8 = embeddedRace.signUp(signedUp4.getEntry());
		
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(240))); // position 1
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(250))); // position 3
		signedUp3.getEntry().addLap(new Lap(1, Duration.ofSeconds(245))); // position 2
		signedUp4.getEntry().addLap(new Lap(1, Duration.ofSeconds(260))); // position 4

		embeddedRace.updateEntryPositions(signedUp7, 3);
		
		assertEquals(1, signedUp5.getPosition());
		assertEquals(2, signedUp6.getPosition());
		assertEquals(3, signedUp7.getPosition());
		assertEquals(4, signedUp8.getPosition());
	}
		
	@Test
	void given_race_is_pursuit_when_noPositionsCalculated_when_positionOfEntryUpdated_then_updatesOnlyPositionOfEntry() {
		DinghyClass dinghyClass = new DinghyClass("Test", 1, 1000);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Competitor competitor4 = new Competitor("Competitor Four");
		
		Dinghy dinghy1 = new Dinghy("1", dinghyClass);
		Dinghy dinghy2 = new Dinghy("2", dinghyClass);
		Dinghy dinghy3 = new Dinghy("3", dinghyClass);
		Dinghy dinghy4 = new Dinghy("4", dinghyClass);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		SignedUp signedUp3 = directRace.signUp(competitor3, dinghy3);
		SignedUp signedUp4 = directRace.signUp(competitor4, dinghy4);

		SignedUp signedUp5 = embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp6 = embeddedRace.signUp(signedUp2.getEntry());
		SignedUp signedUp7 = embeddedRace.signUp(signedUp3.getEntry());
		SignedUp signedUp8 = embeddedRace.signUp(signedUp4.getEntry());
		
		embeddedRace.updateEntryPositions(signedUp7, 3);
		
		assertEquals(null, signedUp5.getPosition());
		assertEquals(null, signedUp6.getPosition());
		assertEquals(3, signedUp7.getPosition());
		assertEquals(null, signedUp8.getPosition());
	}

	@Test
	void given_race_race_is_pursuit_when_scoringAbbreviationSetForEntry_then_positionSetEqualToNumberOfEntriesInRace() {
		DinghyClass dinghyClass = new DinghyClass("Test", 1, 1000);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.PURSUIT, StartType.CSCCLUBSTART);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Competitor competitor4 = new Competitor("Competitor Four");
		
		Dinghy dinghy1 = new Dinghy("1", dinghyClass);
		Dinghy dinghy2 = new Dinghy("2", dinghyClass);
		Dinghy dinghy3 = new Dinghy("3", dinghyClass);
		Dinghy dinghy4 = new Dinghy("4", dinghyClass);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		SignedUp signedUp3 = directRace.signUp(competitor3, dinghy3);
		SignedUp signedUp4 = directRace.signUp(competitor4, dinghy4);
		
		embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp6 = embeddedRace.signUp(signedUp2.getEntry());
		embeddedRace.signUp(signedUp3.getEntry());
		embeddedRace.signUp(signedUp4.getEntry());
		
		signedUp6.getEntry().setScoringAbbreviation("DNS");
		
		assertEquals(4, signedUp2.getPosition());
	}

	// Fleet Race Position Tests
	// Lap advantage is when an entry gets a better corrected time than another boat with the same or slower handicap by sailing less laps; for example if the wind dies for a previously faster boat on the last lap

	// Same Portsmouth Number
	@Test
	void given_raceIsFleetAndEntriesHaveSamePortsmouthNumberAndSailedTheSameLaps_whenOneEntryHasFasterCorrectedTime_then_entryWithFasterTimeWins() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 1, 1043);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		
		SignedUp signedUp3 = embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp4 = embeddedRace.signUp(signedUp2.getEntry());
		
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(660)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(600)));
		signedUp1.getEntry().addLap(new Lap(2, Duration.ofSeconds(660))); // corrected time 1265.580
		signedUp2.getEntry().addLap(new Lap(2, Duration.ofSeconds(600))); // corrected time 1150.527

		assertEquals(1, signedUp4.getPosition());
		assertEquals(2, signedUp3.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntriesHaveSamePortsmouthNumberAndSailedTheSameLapsAndHaveTheSameTime_then_theyAreAllAssignedTheHighestPositionOfAnyEntryWithThatCorrectedTime() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 1, 1043);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		
		SignedUp signedUp3 = embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp4 = embeddedRace.signUp(signedUp2.getEntry());
		
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(660))); // corrected time 632.790
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(660))); // corrected time 632.790
		
		assertEquals(1, signedUp3.getPosition());
		assertEquals(1, signedUp4.getPosition());
	}

	@Test
	void given_raceIsFleetAndEntriesHaveSamePortsmouthNumber_when_oneEntrySailsMoreLapsAndHasFasterCorrectedTime_then_entryWithFasterTimeWins() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 1, 1043);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		
		SignedUp signedUp3 = embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp4 = embeddedRace.signUp(signedUp2.getEntry());
		
		signedUp3.getEntry().addLap(new Lap(1, Duration.ofSeconds(660)));
		signedUp4.getEntry().addLap(new Lap(1, Duration.ofSeconds(1200))); // corrected time 2301.054
		signedUp3.getEntry().addLap(new Lap(2, Duration.ofSeconds(660))); // corrected time 1265.580

		assertEquals(1, signedUp3.getPosition());
		assertEquals(2, signedUp4.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntriesHaveSamePortsmouthNumber_when_entrySailsMoreLapsAndHasSlowerCorrectedTime_then_entryWithMoreLapsWins() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 1, 1043);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		
		SignedUp signedUp3 = embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp4 = embeddedRace.signUp(signedUp2.getEntry());
		
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(600)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(700))); // corrected time 1342.282
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(900))); // corrected time 1438.159

		assertEquals(1, signedUp3.getPosition());
		assertEquals(2, signedUp4.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntriesHaveSamePortsmouthNumberAndSameCorrectedTime_when_entrySailsMoreLaps_then_entryWithMoreLapsWins() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 1, 1043);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		
		SignedUp signedUp3 = embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp4 = embeddedRace.signUp(signedUp2.getEntry());
		
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(500)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(500))); // corrected time 958.773
		signedUp1.getEntry().addLap(new Lap(2, Duration.ofSeconds(500))); // corrected time 958.773

		assertEquals(1, signedUp3.getPosition());
		assertEquals(2, signedUp4.getPosition());
	}
	
	// Slower Portsmouth Number
	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumber_when_largerPortsmouthNumberEntrySailsMoreLapsInSlowerCorrectedTime_then_slowerCorrectedTimeEntryWins() {
		DinghyClass graduate = new DinghyClass("Graduate", 1, 1110);
		DinghyClass scorpion = new DinghyClass("Scorpion", 1, 1043);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		
		SignedUp signedUp3 = embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp4 = embeddedRace.signUp(signedUp2.getEntry());
				
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(600)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(600))); // corrected time 1150.527
		signedUp1.getEntry().addLap(new Lap(2, Duration.ofSeconds(700))); // corrected time 1171.171

		assertEquals(1, signedUp3.getPosition());
		assertEquals(2, signedUp4.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumberAndSameCorrectedTime_when_entrySailsMoreLaps_then_entryWithMoreLapsWins() {
		DinghyClass graduate = new DinghyClass("Graduate", 1, 1000);
		DinghyClass scorpion = new DinghyClass("Scorpion", 1, 500);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		
		Dinghy dinghy1 = new Dinghy("rif1234", graduate);
		Dinghy dinghy2 = new Dinghy("rif4567", scorpion);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		
		SignedUp signedUp3 = embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp4 = embeddedRace.signUp(signedUp2.getEntry());
		
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(200)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(250))); // corrected time 1000.00
		signedUp1.getEntry().addLap(new Lap(2, Duration.ofSeconds(800))); // corrected time 1000.00
		
		assertEquals(1, signedUp3.getPosition());
		assertEquals(2, signedUp4.getPosition());
	}

	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumber_when_largerPortsmouthNumberEntrySailsMoreLapsAndHasFasterCorrectedTime_then_largerPortsmouthNumberEntryWins() {
		DinghyClass graduate = new DinghyClass("Graduate", 1, 1110);
		DinghyClass scorpion = new DinghyClass("Scorpion", 1, 1043);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		
		SignedUp signedUp3 = embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp4 = embeddedRace.signUp(signedUp2.getEntry());
		
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(600)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(700))); // corrected time 1081.081
		signedUp1.getEntry().addLap(new Lap(2, Duration.ofSeconds(600))); // corrected time 1342.282
		
		assertEquals(1, signedUp1.getPosition());
		assertEquals(2, signedUp2.getPosition());
	}

	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumberAndSailedTheSameLaps_when_largerPortsmouthNumberHasSlowerCorrectedTime_then_largerPortsmouthNumberEntryLoses() {
		DinghyClass graduate = new DinghyClass("Graduate", 1, 1110);
		DinghyClass scorpion = new DinghyClass("Scorpion", 1, 1043);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		
		SignedUp signedUp3 = embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp4 = embeddedRace.signUp(signedUp2.getEntry());
		
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(600)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(500)));
		signedUp1.getEntry().addLap(new Lap(2, Duration.ofSeconds(600))); // corrected time 1081.081
		signedUp2.getEntry().addLap(new Lap(2, Duration.ofSeconds(500))); // corrected time 958.773

		assertEquals(1, signedUp4.getPosition());
		assertEquals(2, signedUp3.getPosition());
	}

	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumberAndSailedTheSameLapsAndHaveTheSameCorrecedTime_then_entriesAllAssignedTheHighestPositionOfAnyEntryWithThatCorrectedTime() {
		DinghyClass graduate = new DinghyClass("Graduate", 1, 1500);
		DinghyClass scorpion = new DinghyClass("Scorpion", 1, 1000);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);

		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		
		SignedUp signedUp3 = embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp4 = embeddedRace.signUp(signedUp2.getEntry());
		
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(750)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(500)));
		signedUp1.getEntry().addLap(new Lap(2, Duration.ofSeconds(750))); // corrected time 1000.000
		signedUp2.getEntry().addLap(new Lap(2, Duration.ofSeconds(500))); // corrected time 1000.000

		assertEquals(1, signedUp1.getPosition());
		assertEquals(1, signedUp2.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumberAndSailedTheSameLaps_when_largerPortsmouthNumberEntryHasFasterCorrectedTime_then_entryWithFasterTimeWins() {
		DinghyClass graduate = new DinghyClass("Graduate", 1, 1110);
		DinghyClass scorpion = new DinghyClass("Scorpion", 1, 1043);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		
		SignedUp signedUp3 = embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp4 = embeddedRace.signUp(signedUp2.getEntry());
		
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(600)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(600)));
		signedUp1.getEntry().addLap(new Lap(2, Duration.ofSeconds(600))); // corrected time 1081.081
		signedUp2.getEntry().addLap(new Lap(2, Duration.ofSeconds(600))); // corrected time 1150.527

		assertEquals(1, signedUp3.getPosition());
		assertEquals(2, signedUp4.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumber_when_largerPortsmouthNumberSailedLessLapsAndEntryHasSlowerCorrectedTime_then_entryWithFasterCorrectedTimeWins() {
		DinghyClass graduate = new DinghyClass("Graduate", 1, 1110);
		DinghyClass scorpion = new DinghyClass("Scorpion", 1, 1043);
		
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		
		SignedUp signedUp3 = embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp4 = embeddedRace.signUp(signedUp2.getEntry());
		
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(500)));
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(600))); // corrected time 1081.081
		signedUp2.getEntry().addLap(new Lap(2, Duration.ofSeconds(500))); // corrected time 958.773

		assertEquals(1, signedUp4.getPosition());
		assertEquals(2, signedUp3.getPosition());
	}

	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumberAndSameCorrectedTime_when_largerPortsmouthNumberEntrySailedLessLaps_then_entriesAllAssignedTheHighestPositionOfAnyEntryWithThatCorrectedTime() {
		DinghyClass graduate = new DinghyClass("Graduate", 1, 1500);
		DinghyClass scorpion = new DinghyClass("Scorpion", 1, 1000);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		
		SignedUp signedUp3 = embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp4 = embeddedRace.signUp(signedUp2.getEntry());
		
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(500)));
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(750))); // corrected time 1000.000
		signedUp2.getEntry().addLap(new Lap(2, Duration.ofSeconds(500))); // corrected time 1000.000

		assertEquals(1, signedUp3.getPosition());
		assertEquals(1, signedUp4.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntryHasLargerPortsmouthNumber_when_largerPortsmouthNumberEntrySailedLessLapsAndHasAFasterCorrectedTime_then_entryWithFasterCorrectedTimeWins() {
		DinghyClass graduate = new DinghyClass("Graduate", 1, 1110);
		DinghyClass scorpion = new DinghyClass("Scorpion", 1, 1043);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		
		Dinghy dinghy1 = new Dinghy("1234", graduate);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);

		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		
		SignedUp signedUp3 = embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp4 = embeddedRace.signUp(signedUp2.getEntry());
		
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(750)));
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(700))); // corrected time 1261.261
		signedUp2.getEntry().addLap(new Lap(2, Duration.ofSeconds(750))); // corrected time 1438.159

		assertEquals(1, signedUp3.getPosition());
		assertEquals(2, signedUp4.getPosition());
	}

	// Scoring abbreviation
	@Test
	void given_raceIsFleet_when_scoringAbbreviationSetForEntry_then_positionSetEqualToNumberOfEntriesInRace() {
		DinghyClass dinghyClass = new DinghyClass("Test", 1, 1000);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		Competitor competitor4 = new Competitor("Competitor Four");
		
		Dinghy dinghy1 = new Dinghy("1", dinghyClass);
		Dinghy dinghy2 = new Dinghy("2", dinghyClass);
		Dinghy dinghy3 = new Dinghy("3", dinghyClass);
		Dinghy dinghy4 = new Dinghy("4", dinghyClass);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		SignedUp signedUp3 = directRace.signUp(competitor3, dinghy3);
		SignedUp signedUp4 = directRace.signUp(competitor4, dinghy4);
		
		embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp6 = embeddedRace.signUp(signedUp2.getEntry());
		embeddedRace.signUp(signedUp3.getEntry());
		embeddedRace.signUp(signedUp4.getEntry());
		
		signedUp2.getEntry().setScoringAbbreviation("DNS");
		
		assertEquals(4, signedUp6.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntriesHaveSamePortsmouthNumberSailedSameNumberLaps_whenFasterEntryHasScoringAbbreviation_then_slowerEntryPlacedFirst() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 1, 1043);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		
		SignedUp signedUp3 = embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp4 = embeddedRace.signUp(signedUp2.getEntry());
		
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(500)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(600)));
		signedUp1.getEntry().addLap(new Lap(2, Duration.ofSeconds(500))); // corrected time 958.773
		signedUp2.getEntry().addLap(new Lap(2, Duration.ofSeconds(600))); // corrected time 1150.527
		signedUp1.getEntry().setScoringAbbreviation("RET");

		assertEquals(1, signedUp4.getPosition());
		assertEquals(2, signedUp3.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntriesHaveSamePortsmouthNumber_when_boatWithTheMedianTimeHasScoringAbbreviation_then_medianTimedEntryPlacedLast() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 1, 1043);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		Dinghy dinghy3 = new Dinghy("8974", scorpion);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		SignedUp signedUp3 = directRace.signUp(competitor3, dinghy3);
		
		SignedUp signedUp4 = embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp5 = embeddedRace.signUp(signedUp2.getEntry());
		SignedUp signedUp6 = embeddedRace.signUp(signedUp3.getEntry());
		
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(900))); // corrected time 719.080
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(750))); // corrected time 958.773
		signedUp3.getEntry().addLap(new Lap(1, Duration.ofSeconds(1000))); // corrected time 527.325
		signedUp1.getEntry().setScoringAbbreviation("RET");

		assertEquals(1, signedUp5.getPosition());
		assertEquals(2, signedUp6.getPosition());
		assertEquals(3, signedUp4.getPosition());
	}
	
	@Test
	void given_raceIsFleetAndEntriesHaveSamePortsmouthNumber_when_boatWithTheMedianTimeHasLapAdvantageOverSlowestBoatAndSlowestBoatHasScoringAbbreviation_then_medianTimedEntryPlacedSecondAndEntryWithScoringAbbreviationPlacedLast() {
		DinghyClass classOne = new DinghyClass("Class 1", 1, 900);
		DinghyClass classTwo = new DinghyClass("Class 2", 1, 1000);
		DinghyClass classThree = new DinghyClass("Class 3", 1, 1100);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		Competitor competitor3 = new Competitor("Competitor Three");
		
		Dinghy dinghy1 = new Dinghy("1234", classOne);
		Dinghy dinghy2 = new Dinghy("4567", classTwo);
		Dinghy dinghy3 = new Dinghy("8974", classThree);
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		SignedUp signedUp3 = directRace.signUp(competitor3, dinghy3);
		
		SignedUp signedUp4 = embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp5 = embeddedRace.signUp(signedUp2.getEntry());
		SignedUp signedUp6 = embeddedRace.signUp(signedUp3.getEntry());
		
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(450)));
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(600))); // corrected time 1200.000
		signedUp3.getEntry().addLap(new Lap(1, Duration.ofSeconds(750)));
		signedUp1.getEntry().addLap(new Lap(2, Duration.ofSeconds(450))); // corrected time 1000.000
		signedUp3.getEntry().addLap(new Lap(1, Duration.ofSeconds(750))); // corrected time 1363.636
		signedUp2.getEntry().setScoringAbbreviation("RET");

		assertEquals(1, signedUp4.getPosition());
		assertEquals(2, signedUp6.getPosition());
		assertEquals(3, signedUp5.getPosition());
	}
	
	@Test
	void given_raceIsFleet_when_moreThanOneGroupOfEntriesHaveTheSameCorrectedTimeAndEachGroupHasADifferentCorrectedTime_then_theyAreAllAssignedTheLowestPositionOfAnyEntryWithThatCorrectedTime() {
		DinghyClass scorpion = new DinghyClass("Scorpion", 1, 1043);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
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
		
		SignedUp signedUp1 = directRace.signUp(competitor1, dinghy1);
		SignedUp signedUp2 = directRace.signUp(competitor2, dinghy2);
		SignedUp signedUp3 = directRace.signUp(competitor3, dinghy3);
		SignedUp signedUp4 = directRace.signUp(competitor4, dinghy4);
		SignedUp signedUp5 = directRace.signUp(competitor5, dinghy5);
		
		SignedUp signedUp6 = embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp7 = embeddedRace.signUp(signedUp2.getEntry());
		SignedUp signedUp8 = embeddedRace.signUp(signedUp3.getEntry());
		SignedUp signedUp9 = embeddedRace.signUp(signedUp4.getEntry());
		SignedUp signedUp10 = embeddedRace.signUp(signedUp5.getEntry());
		
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(600))); // 1
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(660))); // 4
		signedUp3.getEntry().addLap(new Lap(1, Duration.ofSeconds(660))); // 4
		signedUp4.getEntry().addLap(new Lap(1, Duration.ofSeconds(650))); // 3
		signedUp5.getEntry().addLap(new Lap(1, Duration.ofSeconds(600))); // 1

		assertEquals(1, signedUp6.getPosition());
		assertEquals(1, signedUp10.getPosition());
		assertEquals(3, signedUp9.getPosition());
		assertEquals(4, signedUp7.getPosition());
		assertEquals(4, signedUp8.getPosition());
	}

	// lap removed
	@Test
	void given_raceIsFleetAndOnlyEntryHasSailedTheMostLapsAndThatEntryWasNotTheLeadBoatOnTheLastLapSailedByMoreThanOneBoat_when_lapRemovedFromLeadBoat_then_correctlyCalculatesPositions() {
	// when lap is removed from the lead boat after which it is no longer the lead boat need to recalculate positions for all boats
		DinghyClass scorpion = new DinghyClass("Scorpion", 1, 1043);
		Fleet fleet = new Fleet("Test Fleet");
		DirectRace directRace = new DirectRace("Test Race", LocalDateTime.of(2021, 10, 14, 14, 10), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		
		Set<Race> hostRaces = new HashSet<Race>();
		hostRaces.add(directRace);
		EmbeddedRace embeddedRace = new EmbeddedRace("Embedded Race", fleet, hostRaces);
		
		Competitor competitor1 = new Competitor("Competitor One");
		Competitor competitor2 = new Competitor("Competitor Two");
		
		Dinghy dinghy1 = new Dinghy("1234", scorpion);
		Dinghy dinghy2 = new Dinghy("4567", scorpion);
		
		directRace.signUp(competitor1, dinghy1);
		directRace.signUp(competitor2, dinghy2);
		
		SignedUp signedUp1 = directRace.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor1)).findFirst().get();
		SignedUp signedUp2 = directRace.getSignedUp().stream().filter(signedUp -> signedUp.getEntry().getHelm().equals(competitor2)).findFirst().get();
		
		SignedUp signedUp3 = embeddedRace.signUp(signedUp1.getEntry());
		SignedUp signedUp4 = embeddedRace.signUp(signedUp2.getEntry());
		
		signedUp1.getEntry().addLap(new Lap(1, Duration.ofSeconds(300))); // corrected time 300.000
		signedUp2.getEntry().addLap(new Lap(1, Duration.ofSeconds(310)));
		signedUp2.getEntry().addLap(new Lap(2, Duration.ofSeconds(90)));
		signedUp2.getEntry().removeLap(new Lap(2, Duration.ofSeconds(90))); // corrected time 310.000

		assertEquals(1, signedUp3.getPosition());
		assertEquals(2, signedUp4.getPosition());
	}
}