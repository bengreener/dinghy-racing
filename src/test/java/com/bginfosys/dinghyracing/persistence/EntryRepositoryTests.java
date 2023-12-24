package com.bginfosys.dinghyracing.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

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
		Competitor competitor = new Competitor();
		Dinghy dinghy = new Dinghy();
		Race race = new Race();
		
		entityManager.persist(competitor);
		entityManager.persist(dinghy);
		entityManager.persist(race);
		
		Entry entry = new Entry(competitor, dinghy, race);
		Entry insertedEntry = entryRepository.save(entry);
		assertThat(entityManager.find(Entry.class, entityManager.getId(insertedEntry))).isEqualTo(entry);
	}
	
	@Test
	void given_entryHasNotBeenSaved_when_idIsRequested_then_returnsNull() {
		Competitor competitor = new Competitor();
		Dinghy dinghy = new Dinghy();
		Race race = new Race();
		
		entityManager.persist(competitor);
		entityManager.persist(dinghy);
		entityManager.persist(race);
		
		Entry entry = new Entry(competitor, dinghy, race);
		assertNull(entry.getId());
	}
	
	@Test
	void given_entryHasBeenSaved_when_idIsRequested_then_returnsId() {
		Competitor competitor = new Competitor();
		Dinghy dinghy = new Dinghy();
		Race race = new Race();
		
		entityManager.persist(competitor);
		entityManager.persist(dinghy);
		entityManager.persist(race);
		
		Entry entry = new Entry(competitor, dinghy, race);
		Entry insertedEntry = entryRepository.save(entry);
		assertEquals(entityManager.getId(insertedEntry), insertedEntry.getId());
	}
	
	@Test
	void when_entryHasNoCompetitor_then_throwsException() {
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
		Competitor competitor = new Competitor();
		Race race = new Race();
		
		entityManager.persist(competitor);
		entityManager.persist(race);
		
		Entry entry = new Entry();
		entry.setCompetitor(competitor);
		entry.setRace(race);
		
		assertThrows(ConstraintViolationException.class, () -> {
			entryRepository.save(entry);
			entityManager.flush();
		});
	}
	
	@Test
	void when_entryHasNoRace_then_throwsException() {
		Competitor competitor = new Competitor();
		Dinghy dinghy = new Dinghy();
		
		entityManager.persist(competitor);
		entityManager.persist(dinghy);
		
		Entry entry = new Entry();
		entry.setCompetitor(competitor);
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
		
		Competitor competitor = new Competitor();
		Dinghy dinghy = new Dinghy();
		dinghy.setDinghyClass(dinghyClass);
		Race race = new Race();
		race.setDinghyClass(dinghyClass);
		
		entityManager.persist(competitor);
		entityManager.persist(dinghy);
		entityManager.persist(race);
		
		Entry entry = new Entry(competitor, dinghy, race);
		Entry insertedEntry = entryRepository.save(entry);
		assertThat(entityManager.find(Entry.class, entityManager.getId(insertedEntry))).isEqualTo(entry);
	}
	
	@Test
	void when_dinghyDinghyClassDoesNotMatchRaceDinghyClassAndRaceDinghyClassIsNull_then_savesRace() {
		Competitor competitor = new Competitor("A Competitor");
		entityManager.persist(competitor);
		
		DinghyClass dinghyClass = new DinghyClass("Test Dinghyclass", 1);
		entityManager.persist(dinghyClass);
		
		Race race = new Race("Race A", LocalDateTime.of(2023, 7, 25, 11, 54, 30), null, Duration.ofMinutes(45), 5);
		entityManager.persist(race);
		
		Dinghy dinghy = new Dinghy("1234", dinghyClass);
		entityManager.persist(dinghy);
		
		Entry entry = new Entry(competitor, dinghy, race);
		Entry insertedEntry = entryRepository.save(entry);

		assertThat(entityManager.find(Entry.class, entityManager.getId(insertedEntry))).isEqualTo(entry);
	}
	
	@Test
	void when_dinghyDinghyClassDoesNotMatchRaceDinghyClassAndRaceDinghyClassIsNotNull_then_throwsException() {
		Competitor competitor = new Competitor("A Competitor");
		entityManager.persist(competitor);
		
		DinghyClass dinghyClass1 = new DinghyClass("Test Dinghyclass", 1);
		DinghyClass dinghyClass2 = new DinghyClass("Different Dinghyclass", 1);
		entityManager.persist(dinghyClass1);
		entityManager.persist(dinghyClass2);
		
		Race race = new Race("Race A", LocalDateTime.of(2023, 7, 25, 11, 54, 30), dinghyClass2, Duration.ofMinutes(45), 5);
		entityManager.persist(race);
		
		Dinghy dinghy = new Dinghy("1234", dinghyClass1);
		entityManager.persist(dinghy);
		
		assertThrows(DinghyClassMismatchException.class, () -> {
			Entry entry = new Entry(competitor, dinghy, race);
			entityManager.persist(entry);
			entityManager.flush();
		});
	}
	
	@Test
	void givenEntryExistsForACompetitor_when_newEntryForCompetitorIsAttemptedForSameRace_then_creationOfEntryFails() {
		Competitor competitor = new Competitor("A Competitor");
		entityManager.persist(competitor);
		DinghyClass dinghyClass = new DinghyClass("Scorpion", 2);
		entityManager.persist(dinghyClass);
		Race race = new Race("A race", LocalDateTime.of(2023,  3, 24, 12, 30, 00), dinghyClass, Duration.ofMinutes(45), 5);
		entityManager.persist(race);
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		entityManager.persist(dinghy1);
		Entry entry1 = new Entry(competitor, dinghy1, race);
		entityManager.persist(entry1);
		Dinghy dinghy2 = new Dinghy("6789", dinghyClass);
		entityManager.persist(dinghy2);
		
		
		Exception e = assertThrows(PersistenceException.class, () -> {
			Entry entry2 = new Entry(competitor, dinghy2, race);
			entryRepository.save(entry2);
			entityManager.flush();
		});

		assertTrue(e.getCause() instanceof org.hibernate.exception.ConstraintViolationException);
	}
	
	@Test
	void givenEntryExistsForADinghy_when_newEntryForDinghyIsAttempted_then_creationOfEntryFails() {
		Competitor competitor1 = new Competitor("A Competitor");
		entityManager.persist(competitor1);
		Competitor competitor2 = new Competitor("B Competitor");
		entityManager.persist(competitor2);
		DinghyClass dinghyClass = new DinghyClass("Scorpion", 2);
		entityManager.persist(dinghyClass);
		Race race = new Race("A race", LocalDateTime.of(2023,  3, 24, 12, 30, 00), dinghyClass, Duration.ofMinutes(45), 5);
		entityManager.persist(race);
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		entityManager.persist(dinghy1);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		entityManager.persist(entry1);
		
		Exception e = assertThrows(PersistenceException.class, () -> {
			Entry entry2 = new Entry(competitor2, dinghy1, race);
			entryRepository.save(entry2);
			entityManager.flush();
		});

		assertTrue(e.getCause() instanceof org.hibernate.exception.ConstraintViolationException);
	}
	
	@Test
	void givenAnEntryAlreadyExistsForARace_when_aNewEntryForTheSameCompetitorAndDinghyIsAttempted_then_creationOfEntryFails() {
		Competitor competitor1 = new Competitor("A Competitor");
		entityManager.persist(competitor1);
		DinghyClass dinghyClass = new DinghyClass("Scorpion", 2);
		entityManager.persist(dinghyClass);
		Race race = new Race("A race", LocalDateTime.of(2023,  3, 24, 12, 30, 00), dinghyClass, Duration.ofMinutes(45), 5);
		entityManager.persist(race);
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		entityManager.persist(dinghy1);
		Entry entry1 = new Entry(competitor1, dinghy1, race);
		entityManager.persist(entry1);
		
		Exception e = assertThrows(PersistenceException.class, () -> {
			Entry entry2 = new Entry(competitor1, dinghy1, race);
			entryRepository.save(entry2);
			entityManager.flush();
		});

		assertTrue(e.getCause() instanceof org.hibernate.exception.ConstraintViolationException);
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
		Competitor competitor = new Competitor();
		Dinghy dinghy = new Dinghy();
		Race race = new Race();
		
		entityManager.persist(competitor);
		entityManager.persist(dinghy);
		entityManager.persist(race);
		
		Entry entry = new Entry(competitor, dinghy, race);
		entry.setLaps(new ConcurrentSkipListSet<Lap>());
		Entry insertedEntry = entryRepository.save(entry);
		assertThat(entityManager.find(Entry.class, entityManager.getId(insertedEntry))).isEqualTo(entry);
	}
	
	@Test
	void when_entryHasLap_then_savesEntry() {
		Competitor competitor = new Competitor();
		Dinghy dinghy = new Dinghy();
		Race race = new Race();
		Lap lap1 = new Lap(1, Duration.ofMinutes(15));
		Lap lap2 = new Lap(2, Duration.ofMinutes(16));
		
		entityManager.persist(competitor);
		entityManager.persist(dinghy);
		entityManager.persist(race);
		entityManager.persist(lap1);
		entityManager.persist(lap2);
		
		Entry entry = new Entry(competitor, dinghy, race);
		SortedSet<Lap> laps = new ConcurrentSkipListSet<Lap>();
		laps.add(lap1);
		laps.add(lap2);
		entry.setLaps(laps);
		Entry insertedEntry = entryRepository.save(entry);
		assertThat(entityManager.find(Entry.class, entityManager.getId(insertedEntry))).isEqualTo(entry);
	}
}
