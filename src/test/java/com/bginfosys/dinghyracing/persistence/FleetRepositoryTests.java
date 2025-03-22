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

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.bginfosys.dinghyracing.model.DinghyClass;
import com.bginfosys.dinghyracing.model.Fleet;

import jakarta.validation.ConstraintViolationException;

@DataJpaTest
public class FleetRepositoryTests {
	
	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	FleetRepository fleetRepository;

	@Test
	void when_providedWithAValidFleet_then_savesFleet() {
		Fleet fleet = new Fleet("Test Fleet");
		Fleet savedFleet = fleetRepository.save(fleet);
		
		assertThat(entityManager.find(Fleet.class, entityManager.getId(savedFleet))).isEqualTo(fleet);
	}
	
	@Test
	void when_fleetHasNoName_then_throwsException() {
		Fleet fleet = new Fleet();
		fleetRepository.save(fleet);
		
		assertThrows(ConstraintViolationException.class, () -> {
			entityManager.flush();
		});
	}	
	
	@Test
	void givenFleetWithNameExists_when_AttemptToSaveNewFleetWithSameName_then_errorReturned() {
		Fleet fleet1 = new Fleet("Test Fleet");
		fleetRepository.save(fleet1);
		entityManager.flush();
		
		Fleet fleet2 = new Fleet("Test Fleet");
		fleetRepository.save(fleet2);
		
		assertThrows(org.hibernate.exception.ConstraintViolationException.class, () -> {
			entityManager.flush();
		});
	}
	
	@Test
	void givenDinghyClassExists_when_DinghyClassIsAdded_then_savesFleetWithDinghyClasses() {
		DinghyClass dinghyClass1 = new DinghyClass("Dinghyclass One", 1, 1000);
		entityManager.persist(dinghyClass1);
		DinghyClass dinghyClass2 = new DinghyClass("Dinghyclass Two", 1, 1000);
		entityManager.persist(dinghyClass2);
		
		Set<DinghyClass> dinghyClasses1 = new HashSet<DinghyClass>();
		dinghyClasses1.add(dinghyClass1);
		dinghyClasses1.add(dinghyClass2);
		Fleet fleet1 = new Fleet("Test Fleet");
		fleet1.setDinghyClasses(dinghyClasses1);
		Fleet savedFleet = fleetRepository.save(fleet1);
		entityManager.flush();
		
		assertThat(entityManager.find(Fleet.class, entityManager.getId(savedFleet)).getDinghyClasses()).isEqualTo(fleet1.getDinghyClasses());
	}
}
