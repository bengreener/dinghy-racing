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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;
import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import jakarta.validation.ConstraintViolationException;

import com.bginfosys.dinghyracing.model.Race;
import com.bginfosys.dinghyracing.model.Dinghy;
import com.bginfosys.dinghyracing.model.DinghyClass;
import com.bginfosys.dinghyracing.model.Entry;
import com.bginfosys.dinghyracing.model.Fleet;
import com.bginfosys.dinghyracing.model.Competitor;

@DataJpaTest
public class RaceRepositoryTests {
	
	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	RaceRepository raceRepository;

	@Test
	void when_providedWithValidRace_then_savesRace() {
		Fleet fleet = new Fleet("Test Fleet");
		entityManager.persist(fleet);
		
		Race race1 = new Race("Test Race", fleet);
		Race race2 = raceRepository.save(race1);
		
		assertThat(entityManager.find(Race.class, entityManager.getId(race2))).isEqualTo(race1);
	}
	
	@Test
	void when_raceHasNoName_then_throwsException() {
		Fleet fleet = new Fleet("Test Fleet");
		entityManager.persist(fleet);
				
		Race race1 = new Race();
		race1.setFleet(fleet);
		raceRepository.save(race1);
		
		assertThrows(ConstraintViolationException.class, () -> {
			entityManager.flush();
		});
	}
	
	@Test
	void when_raceHasNoFleet_then_throwsException() {
		Race race1 = new Race();
		race1.setName("Test Race");
		raceRepository.save(race1);
		
		assertThrows(ConstraintViolationException.class, () -> {
			entityManager.flush();
		});
	}
	
	@Test
	void given_raceAlreadySaved_when_raceUpdated_updatedVersionIsSaved() {
		Competitor competitor = new Competitor();
		entityManager.persist(competitor);
		
		DinghyClass dinghyClass = new DinghyClass("Test Dinghyclass", 1, 1000);
		entityManager.persist(dinghyClass);
		
		Fleet fleet = new Fleet("Test Fleet");
		entityManager.persist(fleet);
		
		Dinghy dinghy = new Dinghy("1234", dinghyClass);
		entityManager.persist(dinghy);
				
		Race race1 = new Race("Test Race", fleet);
		entityManager.persist(race1);
		// remove entity from session (detach entity). Not doing so can result in a false positive dependent on the logic used to check for an existing entity in repository save method
		entityManager.detach(race1);
		
		Entry entry = new Entry(competitor, dinghy, race1);
		entityManager.persist(entry);
		
		Set<Entry> signedUp = new HashSet<Entry>();
		signedUp.add(entry);
		race1.setSignedUp(signedUp);
		Race race2 = raceRepository.save(race1);
		
		assertThat(race1.getId() == race2.getId()
			&& race1.getName() == race2.getName()
			&& race1.getFleet() == race2.getFleet() 
			&& race1.getSignedUp() == race2.getSignedUp()
		);
	}
}
