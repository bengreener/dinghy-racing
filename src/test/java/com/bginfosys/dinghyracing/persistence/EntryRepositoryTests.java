package com.bginfosys.dinghyracing.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.bginfosys.dinghyracing.exceptions.DinghyClassMismatchException;
import com.bginfosys.dinghyracing.model.Competitor;
import com.bginfosys.dinghyracing.model.Dinghy;
import com.bginfosys.dinghyracing.model.DinghyClass;
import com.bginfosys.dinghyracing.model.Entry;
import com.bginfosys.dinghyracing.model.Race;

@DataJpaTest
public class EntryRepositoryTests {

	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	EntryRepository entryRepository;
	
	@Autowired
	DinghyRepository dinghyRepository;
	
	@Autowired
	CompetitorRepository competitorRepository;
	
	@Autowired
	RaceRepository raceRepository;
	
	@Autowired
	DinghyClassRepository dinghyClassRepository;
	
	@Test
	void when_providedWithAValidInstanceOfEntry_then_savesEntry() {
		Competitor competitor = new Competitor();
		Dinghy dinghy = new Dinghy();
		Race race = new Race();
		
		competitorRepository.save(competitor);
		dinghyRepository.save(dinghy);
		raceRepository.save(race);
		
		Entry entry = new Entry(competitor, dinghy, race);
		Entry insertedEntry = entryRepository.save(entry);
		assertThat(entityManager.find(Entry.class, entityManager.getId(insertedEntry))).isEqualTo(entry);
	}
	
	@Test
	void when_entryHasNoCompetitor_then_throwsException() {
		Dinghy dinghy = new Dinghy();
		Race race = new Race();
		
		dinghyRepository.save(dinghy);
		raceRepository.save(race);
		
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
		
		competitorRepository.save(competitor);
		raceRepository.save(race);
		
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
		
		competitorRepository.save(competitor);
		dinghyRepository.save(dinghy);
		
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
		DinghyClass dinghyClass = new DinghyClass("Scorpion");
		dinghyClassRepository.save(dinghyClass);
		
		Competitor competitor = new Competitor();
		Dinghy dinghy = new Dinghy();
		dinghy.setDinghyClass(dinghyClass);
		Race race = new Race();
		race.setDinghyClass(dinghyClass);
		
		competitorRepository.save(competitor);
		dinghyRepository.save(dinghy);
		raceRepository.save(race);
		
		Entry entry = new Entry(competitor, dinghy, race);
		Entry insertedEntry = entryRepository.save(entry);
		assertThat(entityManager.find(Entry.class, entityManager.getId(insertedEntry))).isEqualTo(entry);
	}
	
	@Test
	void when_dinghyDinghyClassDoesNotMatchRaceDinghyClassAndRaceDinghyClassIsNull_then_savesRace() {
		Competitor competitor = new Competitor("A Competitor");
		competitorRepository.save(competitor);
		
		DinghyClass dinghyClass = new DinghyClass("Test Dinghyclass");
		dinghyClassRepository.save(dinghyClass);
		
		Race race = new Race("Race A", LocalDateTime.of(2023, 7, 25, 11, 54, 30), null);
		raceRepository.save(race);
		
		Dinghy dinghy = new Dinghy("1234", dinghyClass);
		dinghyRepository.save(dinghy);
		
		Entry entry = new Entry(competitor, dinghy, race);
		entryRepository.save(entry);
		
		long raceCount = raceRepository.count();
		
		Race race1 = new Race();
		race1.setName("Test Race");
		race1.setPlannedStartTime(LocalDateTime.of(2023, 5, 13, 12, 00));
		Set<Entry> signedUp = new HashSet<Entry>();
		signedUp.add(entry);
		race1.setSignedUp(signedUp);
		Race race2 = raceRepository.save(race1);
		
		assertThat(raceRepository.count() == raceCount + 1 && (race1.getName() == race2.getName()
			&& race1.getDinghyClass() == race2.getDinghyClass() 
			&& race1.getPlannedStartTime() == race2.getPlannedStartTime()
		));
	}
	
	@Test
	void when_dinghyDinghyClassDoesNotMatchRaceDinghyClassAndRaceDinghyClassIsNotNull_then_throwsException() {
		Competitor competitor = new Competitor("A Competitor");
		competitorRepository.save(competitor);
		
		DinghyClass dinghyClass1 = new DinghyClass("Test Dinghyclass");
		DinghyClass dinghyClass2 = new DinghyClass("Different Dinghyclass");
		dinghyClassRepository.save(dinghyClass1);
		dinghyClassRepository.save(dinghyClass2);
		
		Race race = new Race("Race A", LocalDateTime.of(2023, 7, 25, 11, 54, 30), dinghyClass2);
		raceRepository.save(race);
		
		Dinghy dinghy = new Dinghy("1234", dinghyClass1);
		dinghyRepository.save(dinghy);
		
		assertThrows(DinghyClassMismatchException.class, () -> {
			Entry entry = new Entry(competitor, dinghy, race);
			entryRepository.save(entry);
			entityManager.flush();
		});
	}
	
	@Test
	void givenEntryExistsForACompetitor_when_newEntryForCompetitorIsAttemptedForSameRace_then_creationOfEntryFails() {
		Competitor competitor = new Competitor("A Competitor");
		entityManager.persist(competitor);
		DinghyClass dinghyClass = new DinghyClass("Scorpion");
		entityManager.persist(dinghyClass);
		Race race = new Race("A race", LocalDateTime.of(2023,  3, 24, 12, 30, 00), dinghyClass);
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
		DinghyClass dinghyClass = new DinghyClass("Scorpion");
		entityManager.persist(dinghyClass);
		Race race = new Race("A race", LocalDateTime.of(2023,  3, 24, 12, 30, 00), dinghyClass);
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
		DinghyClass dinghyClass = new DinghyClass("Scorpion");
		entityManager.persist(dinghyClass);
		Race race = new Race("A race", LocalDateTime.of(2023,  3, 24, 12, 30, 00), dinghyClass);
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
}
