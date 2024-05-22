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
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import jakarta.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;

import com.bginfosys.dinghyracing.exceptions.DinghyClassMismatchException;
import com.bginfosys.dinghyracing.model.Competitor;
import com.bginfosys.dinghyracing.model.Dinghy;
import com.bginfosys.dinghyracing.model.DinghyClass;
import com.bginfosys.dinghyracing.model.Entry;
import com.bginfosys.dinghyracing.model.Lap;
import com.bginfosys.dinghyracing.model.Race;

@DataJpaTest
public class EntryRepositoryTests {

	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	EntryRepository entryRepository;
	
	@Test
	void when_providedWithAValidInstanceOfEntry_then_savesEntry() {
		Competitor helm = new Competitor();
		Dinghy dinghy = new Dinghy();
		Race race = new Race();
		
		entityManager.persist(helm);
		entityManager.persist(dinghy);
		entityManager.persist(race);
		
		Entry entry = new Entry(helm, dinghy, race);
		Entry insertedEntry = entryRepository.save(entry);
		assertThat(entityManager.find(Entry.class, entityManager.getId(insertedEntry))).isEqualTo(entry);
	}
	
	@Test
	void given_entryHasNotBeenSaved_when_idIsRequested_then_returnsNull() {
		Competitor helm = new Competitor();
		Dinghy dinghy = new Dinghy();
		Race race = new Race();
		
		entityManager.persist(helm);
		entityManager.persist(dinghy);
		entityManager.persist(race);
		
		Entry entry = new Entry(helm, dinghy, race);
		assertNull(entry.getId());
	}
	
	@Test
	void given_entryHasBeenSaved_when_idIsRequested_then_returnsId() {
		Competitor helm = new Competitor();
		Dinghy dinghy = new Dinghy();
		Race race = new Race();
		
		entityManager.persist(helm);
		entityManager.persist(dinghy);
		entityManager.persist(race);
		
		Entry entry = new Entry(helm, dinghy, race);
		Entry insertedEntry = entryRepository.save(entry);
		assertEquals(entityManager.getId(insertedEntry), insertedEntry.getId());
	}
	
	@Test
	void when_entryHasNoHelm_then_throwsException() {
		Dinghy dinghy = new Dinghy();
		Race race = new Race();
		
		entityManager.persist(dinghy);
		entityManager.persist(race);
		
		Entry entry = new Entry();
		entry.setDinghy(dinghy);
		entry.setRace(race);
		
		assertThrows(ConstraintViolationException.class, () -> {
			entryRepository.save(entry);
			entityManager.flush();
		});
	}
	
	@Test
	void when_entryHasNoDinghy_then_throwsException() {
		Competitor helm = new Competitor();
		Race race = new Race();
		
		entityManager.persist(helm);
		entityManager.persist(race);
		
		Entry entry = new Entry();
		entry.setHelm(helm);
		entry.setRace(race);
		
		assertThrows(ConstraintViolationException.class, () -> {
			entryRepository.save(entry);
			entityManager.flush();
		});
	}
	
	@Test
	void when_entryHasNoRace_then_throwsException() {
		Competitor helm = new Competitor();
		Dinghy dinghy = new Dinghy();
		
		entityManager.persist(helm);
		entityManager.persist(dinghy);
		
		Entry entry = new Entry();
		entry.setHelm(helm);
		entry.setDinghy(dinghy);
		
		assertThrows(ConstraintViolationException.class, () -> {
			entryRepository.save(entry);
			entityManager.flush();
		});
	}
	
	@Test
	void when_dinghyDinghyClassMatchesRaceDinghyClass_then_savesRace() {
		DinghyClass dinghyClass = new DinghyClass("Scorpion", 2);
		entityManager.persist(dinghyClass);
		
		Competitor helm = new Competitor();
		Dinghy dinghy = new Dinghy();
		dinghy.setDinghyClass(dinghyClass);
		Race race = new Race();
		race.setDinghyClass(dinghyClass);
		
		entityManager.persist(helm);
		entityManager.persist(dinghy);
		entityManager.persist(race);
		
		Entry entry = new Entry(helm, dinghy, race);
		Entry insertedEntry = entryRepository.save(entry);
		assertThat(entityManager.find(Entry.class, entityManager.getId(insertedEntry))).isEqualTo(entry);
	}
	
	@Test
	void when_dinghyDinghyClassDoesNotMatchRaceDinghyClassAndRaceDinghyClassIsNull_then_savesRace() {
		Competitor helm = new Competitor("A Competitor");
		entityManager.persist(helm);
		
		DinghyClass dinghyClass = new DinghyClass("Test Dinghyclass", 1);
		entityManager.persist(dinghyClass);
		
		Race race = new Race("Race A", LocalDateTime.of(2023, 7, 25, 11, 54, 30), null, Duration.ofMinutes(45), 5);
		entityManager.persist(race);
		
		Dinghy dinghy = new Dinghy("1234", dinghyClass);
		entityManager.persist(dinghy);
		
		Entry entry = new Entry(helm, dinghy, race);
		Entry insertedEntry = entryRepository.save(entry);

		assertThat(entityManager.find(Entry.class, entityManager.getId(insertedEntry))).isEqualTo(entry);
	}
	
	@Test
	void when_dinghyDinghyClassDoesNotMatchRaceDinghyClassAndRaceDinghyClassIsNotNull_then_throwsException() {
		Competitor helm = new Competitor("A Competitor");
		entityManager.persist(helm);
		
		DinghyClass dinghyClass1 = new DinghyClass("Test Dinghyclass", 1);
		DinghyClass dinghyClass2 = new DinghyClass("Different Dinghyclass", 1);
		entityManager.persist(dinghyClass1);
		entityManager.persist(dinghyClass2);
		
		Race race = new Race("Race A", LocalDateTime.of(2023, 7, 25, 11, 54, 30), dinghyClass2, Duration.ofMinutes(45), 5);
		entityManager.persist(race);
		
		Dinghy dinghy = new Dinghy("1234", dinghyClass1);
		entityManager.persist(dinghy);
		
		assertThrows(DinghyClassMismatchException.class, () -> {
			new Entry(helm, dinghy, race);
		});
	}
	
	@Test
	void givenEntryExistsForACompetitor_when_newEntryForCompetitorIsAttemptedForSameRace_then_creationOfEntryFails() {
		Competitor helm = new Competitor("A Competitor");
		entityManager.persist(helm);
		DinghyClass dinghyClass = new DinghyClass("Scorpion", 2);
		entityManager.persist(dinghyClass);
		Race race = new Race("A race", LocalDateTime.of(2023,  3, 24, 12, 30, 00), dinghyClass, Duration.ofMinutes(45), 5);
		entityManager.persist(race);
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		entityManager.persist(dinghy1);
		Entry entry1 = new Entry(helm, dinghy1, race);
		entityManager.persist(entry1);
		Dinghy dinghy2 = new Dinghy("6789", dinghyClass);
		entityManager.persist(dinghy2);
		
		assertThrows(org.hibernate.exception.ConstraintViolationException.class, () -> {
			Entry entry2 = new Entry(helm, dinghy2, race);
			entryRepository.save(entry2);
			entityManager.flush();
		});
	}
	
	@Test
	void givenEntryExistsForADinghy_when_newEntryForDinghyIsAttempted_then_creationOfEntryFails() {
		Competitor helm1 = new Competitor("A Competitor");
		entityManager.persist(helm1);
		Competitor helm2 = new Competitor("B Competitor");
		entityManager.persist(helm2);
		DinghyClass dinghyClass = new DinghyClass("Scorpion", 2);
		entityManager.persist(dinghyClass);
		Race race = new Race("A race", LocalDateTime.of(2023,  3, 24, 12, 30, 00), dinghyClass, Duration.ofMinutes(45), 5);
		entityManager.persist(race);
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		entityManager.persist(dinghy1);
		Entry entry1 = new Entry(helm1, dinghy1, race);
		entityManager.persist(entry1);
		
		assertThrows(org.hibernate.exception.ConstraintViolationException.class, () -> {
			Entry entry2 = new Entry(helm2, dinghy1, race);
			entryRepository.save(entry2);
			entityManager.flush();
		});
	}
	
	@Test
	void givenAnEntryAlreadyExistsForARace_when_aNewEntryForTheSameCompetitorAndDinghyIsAttempted_then_creationOfEntryFails() {
		Competitor helm1 = new Competitor("A Competitor");
		entityManager.persist(helm1);
		DinghyClass dinghyClass = new DinghyClass("Scorpion", 2);
		entityManager.persist(dinghyClass);
		Race race = new Race("A race", LocalDateTime.of(2023,  3, 24, 12, 30, 00), dinghyClass, Duration.ofMinutes(45), 5);
		entityManager.persist(race);
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		entityManager.persist(dinghy1);
		Entry entry1 = new Entry(helm1, dinghy1, race);
		entityManager.persist(entry1);
		
		assertThrows(org.hibernate.exception.ConstraintViolationException.class, () -> {
			Entry entry2 = new Entry(helm1, dinghy1, race);
			entryRepository.save(entry2);
			entityManager.flush();
		});
	}
	
	@Test
	void given_entriesExistForRace_when_searchingForEntriesByRace_EntriesAreReturned() {
		Competitor c1 = new Competitor("Competitor One");
		Competitor c2 = new Competitor("Competitor Two");
		entityManager.persist(c1);
		entityManager.persist(c2);
		DinghyClass dc1 = new DinghyClass("Dinghy Class One", 1);
		entityManager.persist(dc1);
		Dinghy d1 = new Dinghy("1", dc1);
		Dinghy d2 = new Dinghy("2", dc1);
		entityManager.persist(d1);
		entityManager.persist(d2);
		Race r1 = new Race("Race One", LocalDateTime.of(2023, 5, 13, 12, 00), dc1, Duration.ofMinutes(45), 5);
		entityManager.persist(r1);
		Entry e1 = new Entry(c1, d1, r1);
		Entry e2 = new Entry(c2, d2, r1);
		entityManager.persist(e1);
		entityManager.persist(e2);
		
		Page<Entry> entries = entryRepository.findByRace(r1, null);
		
		assertThat(entries).contains(e1, e2);
	}

	@Test
	void when_entryHasEmptyLaps_then_savesEntry() {
		Competitor helm = new Competitor();
		Dinghy dinghy = new Dinghy();
		Race race = new Race();
		
		entityManager.persist(helm);
		entityManager.persist(dinghy);
		entityManager.persist(race);
		
		Entry entry = new Entry(helm, dinghy, race);
		entry.setLaps(new ConcurrentSkipListSet<Lap>());
		Entry insertedEntry = entryRepository.save(entry);
		assertThat(entityManager.find(Entry.class, entityManager.getId(insertedEntry))).isEqualTo(entry);
	}
	
	@Test
	void when_entryHasLap_then_savesEntry() {
		Competitor helm = new Competitor();
		Dinghy dinghy = new Dinghy();
		Race race = new Race();
		Lap lap1 = new Lap(1, Duration.ofMinutes(15));
		Lap lap2 = new Lap(2, Duration.ofMinutes(16));
		
		entityManager.persist(helm);
		entityManager.persist(dinghy);
		entityManager.persist(race);
		entityManager.persist(lap1);
		entityManager.persist(lap2);
		
		Entry entry = new Entry(helm, dinghy, race);
		SortedSet<Lap> laps = new ConcurrentSkipListSet<Lap>();
		laps.add(lap1);
		laps.add(lap2);
		entry.setLaps(laps);
		Entry insertedEntry = entryRepository.save(entry);
		assertThat(entityManager.find(Entry.class, entityManager.getId(insertedEntry))).isEqualTo(entry);
	}

	@Test
	void when_entryIncludesCrew_then_savesEntry() {
		
	}
	
	@Test
	void when_addingEntries_then_savesMultipleEntriesWithoutCrew() {
		Competitor helmA = new Competitor("A Competitor");
		Competitor helmB = new Competitor("B Competitor");
		entityManager.persist(helmA);
		entityManager.persist(helmB);
		DinghyClass dinghyClass = new DinghyClass("Laser", 1);
		entityManager.persist(dinghyClass);
		Race race = new Race("A race", LocalDateTime.of(2023,  3, 24, 12, 30, 00), dinghyClass, Duration.ofMinutes(45), 5);
		entityManager.persist(race);
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("5678", dinghyClass);
		entityManager.persist(dinghy1);
		entityManager.persist(dinghy2);
		Entry entry1 = new Entry(helmA, dinghy1, race);
		entityManager.persist(entry1);
		Entry entry2 = new Entry(helmB, dinghy2, race);
		entryRepository.save(entry2);
		entityManager.flush();
		
		Page<Entry> entries = entryRepository.findByRace(race, null);
		assertThat(entries).contains(entry1, entry2);
	}
	
	@Test
	void givenEntryExistsForACrew_when_newEntryForCrewIsAttemptedForSameRace_then_creationOfEntryFails() {
		Competitor helmA = new Competitor("A Competitor");
		Competitor helmB = new Competitor("B Competitor");
		Competitor crew = new Competitor("Crew");
		entityManager.persist(helmA);
		entityManager.persist(helmB);
		entityManager.persist(crew);
		DinghyClass dinghyClass = new DinghyClass("Scorpion", 2);
		entityManager.persist(dinghyClass);
		Race race = new Race("A race", LocalDateTime.of(2023,  3, 24, 12, 30, 00), dinghyClass, Duration.ofMinutes(45), 5);
		entityManager.persist(race);
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("5678", dinghyClass);
		entityManager.persist(dinghy1);
		entityManager.persist(dinghy2);
		Entry entry1 = new Entry(helmA, dinghy1, race);
		entry1.setCrew(crew);
		entityManager.persist(entry1);
		Entry entry2 = new Entry(helmB, dinghy2, race);
		entry2.setCrew(crew);
		
		assertThrows(org.hibernate.exception.ConstraintViolationException.class, () -> {
			entryRepository.save(entry2);
			entityManager.flush();
		});
	}

	@Test
	void when_scoringAbbreviationLessThan3Characters_then_throwsValidationError() {
		Entry entry = new Entry();
		entry.setScoringAbbreviation("A");
		assertThrows(ConstraintViolationException.class, () -> {
			entryRepository.save(entry);
			entityManager.flush();
		});
	}
	
	@Test
	void when_scoringAbbreviationMoreThan3Characters_then_throwsValidationError() {
		Entry entry = new Entry();
		entry.setScoringAbbreviation("AYZD");
		assertThrows(ConstraintViolationException.class, () -> {
			entryRepository.save(entry);
			entityManager.flush();
		});
	}
}
