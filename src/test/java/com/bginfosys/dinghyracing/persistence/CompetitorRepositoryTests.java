package com.bginfosys.dinghyracing.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.bginfosys.dinghyracing.model.Competitor;

@DataJpaTest
public class CompetitorRepositoryTests {
	
	@Autowired
	TestEntityManager entityManager;
	
	@Autowired 
	CompetitorRepository competitorRepository;
	
	@Test
	void when_providedWithAValidInstanceOfCompetitor_then_samesCompetitor() {
		Competitor competitor1 = new Competitor("Some Name");
		Competitor insertedCompetitor = competitorRepository.save(competitor1);
		Competitor competitor2 = entityManager.find(Competitor.class, insertedCompetitor.getId());
		
		assertThat(competitor2).isEqualTo(competitor1);
	}
	
	@Test
	void when_competitorHasNoName_then_throwsException() {
		Competitor competitor1 = new Competitor();
		
		competitorRepository.save(competitor1);
		
		assertThrows(ConstraintViolationException.class, () -> {
			entityManager.flush();
		});
	}
	
	@Test
	void given_competitorAlreadySaved_when_competitorUpdated_updatedVersionIsSaved() {
		Competitor competitor1 = new Competitor("Some Name");
		entityManager.persist(competitor1);
		
		competitor1.setName("New Name");
		competitorRepository.save(competitor1);
		assertThat(entityManager.find(Competitor.class, competitor1.getId()).getName()).isEqualTo("New Name");
	}
}
