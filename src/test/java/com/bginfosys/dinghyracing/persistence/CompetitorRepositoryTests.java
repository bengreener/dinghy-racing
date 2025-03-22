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
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.validation.ConstraintViolationException;

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
	void when_providedWithAValidInstanceOfCompetitor_then_savesCompetitor() {
		Competitor competitor1 = new Competitor("Some Name");
		Competitor insertedCompetitor = competitorRepository.save(competitor1);
		
		assertThat(entityManager.find(Competitor.class, entityManager.getId(insertedCompetitor))).isEqualTo(competitor1);
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
		Competitor insertedCompetitor = competitorRepository.save(competitor1);
		assertThat(entityManager.find(Competitor.class, entityManager.getId(insertedCompetitor)).getName()).isEqualTo("New Name");
	}
	
	@Test
	void given_competitorExists_when_searchingForCompetitorByName_then_returnsCompetitor() {
		Competitor competitor1 = new Competitor("Some Name");
		entityManager.persist(competitor1);
		
		Competitor competitor2 = competitorRepository.findByName("Some Name");
		
		assertEquals(competitor1, competitor2);
	}
}
