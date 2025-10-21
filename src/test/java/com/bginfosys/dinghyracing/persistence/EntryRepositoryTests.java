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
   
package com.bginfosys.dinghyracing.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import jakarta.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.bginfosys.dinghyracing.model.Competitor;
import com.bginfosys.dinghyracing.model.Dinghy;
import com.bginfosys.dinghyracing.model.DinghyClass;
import com.bginfosys.dinghyracing.model.DirectRace;
import com.bginfosys.dinghyracing.model.Entry;
import com.bginfosys.dinghyracing.model.Fleet;
import com.bginfosys.dinghyracing.model.Lap;
import com.bginfosys.dinghyracing.model.Race;
import com.bginfosys.dinghyracing.model.RaceType;
import com.bginfosys.dinghyracing.model.SignedUp;
import com.bginfosys.dinghyracing.model.StartType;

@DataJpaTest
public class EntryRepositoryTests {

	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	EntryRepository entryRepository;
	
	@Test
	void when_providedWithAValidInstanceOfEntry_then_savesEntry() {
		Competitor helm = new Competitor("Bob");
		entityManager.persist(helm);
		Competitor crew = new Competitor("Jane");
		entityManager.persist(crew);
		DinghyClass dinghyClass = new DinghyClass("Scorpion", 2, 1000);
		entityManager.persist(dinghyClass);
		Dinghy dinghy = new Dinghy("1234", dinghyClass);
		entityManager.persist(dinghy);
		entityManager.flush();

		Entry entry = new Entry(helm, dinghy);
		entry.setCrew(crew);
	
		Entry insertedEntry = entryRepository.save(entry);
		assertThat(entityManager.find(Entry.class, entityManager.getId(insertedEntry))).isEqualTo(entry);
	}
	
	@Test
	void given_entryHasNotBeenSaved_when_idIsRequested_then_returnsNull() {
		Competitor helm = new Competitor();
		entityManager.persist(helm);
		Dinghy dinghy = new Dinghy();
		entityManager.persist(dinghy);
		
		Entry entry = new Entry(helm, dinghy);
		
		assertNull(entry.getId());
	}
	
	@Test
	void given_entryHasBeenSaved_when_idIsRequested_then_returnsId() {
		Competitor helm = new Competitor();
		entityManager.persist(helm);
		Dinghy dinghy = new Dinghy();
		entityManager.persist(dinghy);
		
		Entry entry = new Entry(helm, dinghy);
		Entry insertedEntry = entryRepository.save(entry);
		assertEquals(entityManager.getId(insertedEntry), insertedEntry.getId());
	}
	
	@Test
	void when_entryHasNoHelm_then_throwsException() {
		Dinghy dinghy = new Dinghy();
		entityManager.persist(dinghy);
		
		Entry entry = new Entry();
		entry.setDinghy(dinghy);
		
		assertThrows(ConstraintViolationException.class, () -> {
			entryRepository.save(entry);
			entityManager.flush();
		});
	}
	
	@Test
	void when_entryHasNoDinghy_then_throwsException() {
		Competitor helm = new Competitor();
		entityManager.persist(helm);
		
		Entry entry = new Entry();
		entry.setHelm(helm);
		
		assertThrows(ConstraintViolationException.class, () -> {
			entryRepository.save(entry);
			entityManager.flush();
		});
	}

	@Test
	void when_entryHasEmptyLaps_then_savesEntry() {
		DinghyClass dc1 = new DinghyClass("Dinghy Class One", 1, 1000);
		entityManager.persist(dc1);
		Competitor helm = new Competitor();
		entityManager.persist(helm);
		Dinghy dinghy = new Dinghy();
		dinghy.setDinghyClass(dc1);
		entityManager.persist(dinghy);
		Set<DinghyClass> dinghyClasses = new HashSet<DinghyClass>(64);
		dinghyClasses.add(dc1);
		Fleet fleet = new Fleet("Test Fleet", dinghyClasses);
		entityManager.persist(fleet);		
		Race race = new DirectRace();
		race.setFleet(fleet);
		entityManager.persist(race);
		
		Entry entry = new Entry(helm, dinghy);
		entry.setLaps(new ConcurrentSkipListSet<Lap>());
		Entry insertedEntry = entryRepository.save(entry);
		assertThat(entityManager.find(Entry.class, entityManager.getId(insertedEntry))).isEqualTo(entry);
	}
	
	@Test
	void when_entryHasLap_then_savesEntry() {
		Competitor helm = new Competitor("Bob");
		entityManager.persist(helm);
		
		DinghyClass dinghyClass = new DinghyClass("Comet", 1, 1000);
		entityManager.persist(dinghyClass);
		Dinghy dinghy = new Dinghy("1234", dinghyClass);
		entityManager.persist(dinghy);
		
		Fleet fleet = new Fleet("Test Fleet");
		entityManager.persist(fleet);
		
		Race race = new DirectRace("Test Race", LocalDateTime.of(2023, 5, 13, 12, 00), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		race.setFleet(fleet);
		entityManager.persist(race);
		
		Entry entry = new Entry(helm, dinghy);
		entityManager.persist(entry);
		
		entityManager.flush();
		
		SignedUp signedUp = new SignedUp(race, entry);
		entityManager.persist(signedUp);
		
		Set<SignedUp> signedUpTo = new HashSet<SignedUp>(64);
		entry.setSignedUpTo(signedUpTo);
		entityManager.persist(entry);
		race.setSignedUp(signedUpTo);
		entityManager.persist(race);
		
		Lap lap1 = new Lap(1, Duration.ofMinutes(15));
		entityManager.persist(lap1);
		Lap lap2 = new Lap(2, Duration.ofMinutes(16));
		entityManager.persist(lap2);		
		
		SortedSet<Lap> laps = new ConcurrentSkipListSet<Lap>();
		laps.add(lap1);
		laps.add(lap2);
		entry.setLaps(laps);
		Entry insertedEntry = entryRepository.save(entry);
		assertThat(entityManager.find(Entry.class, entityManager.getId(insertedEntry))).isEqualTo(entry);
	}

	@Test
	void when_entryIncludesCrew_then_savesEntry() {
		Competitor helm = new Competitor();
		entityManager.persist(helm);
		Dinghy dinghy = new Dinghy();
		entityManager.persist(dinghy);

		Entry entry = new Entry(helm, dinghy);	
	
		Entry insertedEntry = entryRepository.save(entry);
		assertThat(entityManager.find(Entry.class, entityManager.getId(insertedEntry))).isEqualTo(entry);
	}
	
	@Test
	void when_addingEntries_then_savesMultipleEntriesWithoutCrew() {
		Competitor helmA = new Competitor("A Competitor");
		Competitor helmB = new Competitor("B Competitor");
		entityManager.persist(helmA);
		entityManager.persist(helmB);
		
		DinghyClass dinghyClass = new DinghyClass("Laser", 1, 1100);
		entityManager.persist(dinghyClass);
		
		Set<DinghyClass> dinghyClasses = new HashSet<DinghyClass>(64);
		dinghyClasses.add(dinghyClass);
		Fleet fleet = new Fleet("Test Fleet", dinghyClasses);
		entityManager.persist(fleet);
		
		DirectRace race = new DirectRace("A race", LocalDateTime.of(2023,  3, 24, 12, 30, 00), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		entityManager.persist(race);
		
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("5678", dinghyClass);
		entityManager.persist(dinghy1);
		entityManager.persist(dinghy2);
		
		Entry entry1 = new Entry(helmA, dinghy1);
		entityManager.persist(entry1);
		
		Entry entry2 = new Entry(helmB, dinghy2);
		entryRepository.save(entry2);
		entityManager.flush();
		
		List<Entry> entries = entryRepository.findAll();
		assertThat(entries).contains(entry1, entry2);
	}

	@Test
	void when_scoringAbbreviationLessThan3Characters_then_throwsValidationError() {
		DinghyClass dinghyClass = new DinghyClass("Scorpion", 2, 1041);
		entityManager.persist(dinghyClass);
		
		Set<DinghyClass> dinghyClasses = new HashSet<DinghyClass>(64);
		dinghyClasses.add(dinghyClass);
		Fleet fleet = new Fleet("Test Fleet", dinghyClasses);
		entityManager.persist(fleet);
		
		DirectRace race = new DirectRace("A race", LocalDateTime.of(2023,  3, 24, 12, 30, 00), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		entityManager.persist(race);
		Entry entry = new Entry();
//		entry.setRace(race);
		entry.setScoringAbbreviation("A");
		assertThrows(ConstraintViolationException.class, () -> {
			entryRepository.save(entry);
			entityManager.flush();
		});
	}
	
	@Test
	void when_scoringAbbreviationMoreThan3Characters_then_throwsValidationError() {
		DinghyClass dinghyClass = new DinghyClass("Scorpion", 2, 1041);
		entityManager.persist(dinghyClass);
		
		Set<DinghyClass> dinghyClasses = new HashSet<DinghyClass>(64);
		dinghyClasses.add(dinghyClass);
		Fleet fleet = new Fleet("Test Fleet", dinghyClasses);
		entityManager.persist(fleet);
		
		DirectRace race = new DirectRace("A race", LocalDateTime.of(2023,  3, 24, 12, 30, 00), fleet, Duration.ofMinutes(45), 5, RaceType.FLEET, StartType.CSCCLUBSTART);
		entityManager.persist(race);
		Entry entry = new Entry();
//		entry.setRace(race);
		entry.setScoringAbbreviation("AYZD");
		assertThrows(ConstraintViolationException.class, () -> {
			entryRepository.save(entry);
			entityManager.flush();
		});
	}
}
