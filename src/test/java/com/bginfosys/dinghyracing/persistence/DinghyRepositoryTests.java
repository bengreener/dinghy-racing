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

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.validation.ConstraintViolationException;

import com.bginfosys.dinghyracing.model.Dinghy;
import com.bginfosys.dinghyracing.model.DinghyClass;

@DataJpaTest
public class DinghyRepositoryTests {
	
	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	DinghyRepository dinghyRepository;
	
	@Test
	void saveDinghy() {
		Dinghy d1;
		Dinghy d2;
		DinghyClass dc = new DinghyClass("TestClass", 1);
		entityManager.persist(dc);
		
		d1 = new Dinghy("1234", dc);
		d2 = dinghyRepository.save(d1);
		assertThat(entityManager.find(Dinghy.class, entityManager.getId(d2))).isEqualTo(d1);
	}
	
	@Test
	void dinghyClassIsMandatory() {
		Dinghy d = new Dinghy();
		
		d.setSailNumber("1234");
		d = dinghyRepository.save(d);
		
		assertThrows(ConstraintViolationException.class, () -> {
			entityManager.flush();
		});	
	}

	@Test
	void addDuplicateDinghyFails() {
		DinghyClass dc = new DinghyClass("TestClass", 1);
		entityManager.persist(dc);
		Dinghy d1 = new Dinghy("1234", dc);
		Dinghy d2 = new Dinghy("1234", dc);
				
		// post original dinghy to database
		entityManager.persist(d1);
		// force flush of memory to DB
		entityManager.flush();

		assertThrows(org.hibernate.exception.ConstraintViolationException.class, () -> {
			dinghyRepository.save(d2);
			// force flush of memory to database
			entityManager.flush();
		});
	}
	
	@Test
	void given_dinghiesOfTheDinghyClassExist_when_searchingForDinghiesByTheDinghyClass_then_dinghiesAreReturned() {
		DinghyClass dinghyClass = new DinghyClass("DinghyClass", 1);
		entityManager.persist(dinghyClass);
		
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		Dinghy dinghy2 = new Dinghy("5678", dinghyClass);
		
		entityManager.persist(dinghy1);
		entityManager.persist(dinghy2);
		
		Page<Dinghy> dinghies = dinghyRepository.findByDinghyClass(dinghyClass, Pageable.ofSize(5));
		
		assertThat(dinghies).contains(dinghy1, dinghy2);
	}
	
	@Test
	void given_theDinghyExists_when_searchingForTheDinghyBySailNumberAndDinghyClass_then_theDinghyIsReturned() {
		DinghyClass dinghyClass = new DinghyClass("DinghyClass", 1);
		entityManager.persist(dinghyClass);
		
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		entityManager.persist(dinghy1);
		
		Dinghy dinghy2 = dinghyRepository.findBySailNumberAndDinghyClass("1234", dinghyClass);
		
		assertEquals(dinghy1, dinghy2);
	}

	@Test
	void given_dinghiesExistWithTheSailNumber_when_searchingForDinghiesBySailNumber_then_theDinghiesAreReturned() {
		DinghyClass dc1 = new DinghyClass("Scorpion", 1);
		DinghyClass dc2 = new DinghyClass("Comet", 2);
		
		entityManager.persist(dc1);
		entityManager.persist(dc2);
		
		Dinghy dinghy1 = new Dinghy("1234", dc1);
		Dinghy dinghy2 = new Dinghy("1234", dc2);
		entityManager.persist(dinghy1);
		entityManager.persist(dinghy2);
		
		Page<Dinghy> dinghies = dinghyRepository.findBySailNumber("1234", Pageable.ofSize(5));
		
		assertThat(dinghies).contains(dinghy1, dinghy2);
	}
}
