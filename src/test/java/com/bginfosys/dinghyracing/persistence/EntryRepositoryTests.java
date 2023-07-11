package com.bginfosys.dinghyracing.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.bginfosys.dinghyracing.model.Competitor;
import com.bginfosys.dinghyracing.model.Dinghy;
import com.bginfosys.dinghyracing.model.Entry;

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
	
	@Test
	void when_providedWithAValidInstanceOfEntry_then_savesEntry() {
		Competitor competitor = new Competitor();
		Dinghy dinghy = new Dinghy();
		
		competitorRepository.save(competitor);
		dinghyRepository.save(dinghy);
		
		Entry entry = new Entry(competitor, dinghy);
		Entry insertedEntry = entryRepository.save(entry);
		assertThat(entityManager.find(Entry.class, entityManager.getId(insertedEntry))).isEqualTo(entry);
	}
	
	@Test
	void when_entryHasNoCompetitor_then_throwsException() {
		Dinghy dinghy = new Dinghy();
		
		dinghyRepository.save(dinghy);
		
		Entry entry = new Entry();
		entry.setDinghy(dinghy);
		
		assertThrows(ConstraintViolationException.class, () -> {
			entryRepository.save(entry);
			entityManager.flush();
		});
	}
	
	@Test
	void when_entryHasNoDinghy_then_throwsException() {
		Competitor competitor = new Competitor();
		
		competitorRepository.save(competitor);
		
		Entry entry = new Entry();
		entry.setCompetitor(competitor);
		
		assertThrows(ConstraintViolationException.class, () -> {
			entryRepository.save(entry);
			entityManager.flush();
		});
	}
}
