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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;

import com.bginfosys.dinghyracing.model.DinghyClass;

@DataJpaTest
public class DinghyClassRepositoryTests {

	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	DinghyClassRepository dinghyClassRepository;
	
	@Test
	void saveDinghyClass() {
		DinghyClass dc1;
		DinghyClass dc2;
				
		dc1 = new DinghyClass("TestClass", 1);
		dc2 = dinghyClassRepository.save(dc1);
		
		assertThat(entityManager.find(DinghyClass.class, entityManager.getId(dc2))).isEqualTo(dc1);
	}
	
	@Test
	void nameIsUnique() {
		DinghyClass dc1 = new DinghyClass("TestClass", 1);
		entityManager.persist(dc1);
		
//		assertThrows(ConstraintViolationException.class, () -> {
		assertThrows(PersistenceException.class, () -> {
			DinghyClass dc2 = new DinghyClass("TestClass", 1);
			dinghyClassRepository.save(dc2);
			entityManager.flush();
		});
	}
	
	@Test
	void when_searchingForADinghyClassByName_then_returnsDinghyClass() {
		DinghyClass dc1 = new DinghyClass("TestClass", 1);
		entityManager.persist(dc1);
		
		DinghyClass dc2 = dinghyClassRepository.findByName("TestClass");
		
		assertThat(dc1).isEqualTo(dc2);			
	}
	
	@Test
	void when_savingDInghyClass_crewSizeCannotBeNull() {
		assertThrows(javax.validation.ConstraintViolationException.class, () -> {
			DinghyClass dc = new DinghyClass();
			dc.setName("Test Class");
			dinghyClassRepository.save(dc);
			entityManager.flush();
		});
	}
	
	@Test
	void when_savingDInghyClass_nameCannotBeNull() {
		assertThrows(javax.validation.ConstraintViolationException.class, () -> {
			DinghyClass dc = new DinghyClass();
			dc.setCrewSize(1);
			dinghyClassRepository.save(dc);
			entityManager.flush();
		});
	}
}
