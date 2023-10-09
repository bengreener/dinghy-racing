package com.bginfosys.dinghyracing.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;

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
		DinghyClass dc = new DinghyClass("TestClass");
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
		DinghyClass dc = new DinghyClass("TestClass");
		entityManager.persist(dc);
		Dinghy d1 = new Dinghy("1234", dc);
		Dinghy d2 = new Dinghy("1234", dc);
				
		// post original dinghy to database
		entityManager.persist(d1);
		// force flush of memory to DB
		entityManager.flush();
					
		// confirm creating duplicate throws DataIntgrityViolationException
		Exception e = assertThrows(PersistenceException.class, () -> {
			dinghyRepository.save(d2);
			// force flush of memory to database
			entityManager.flush();
		});
		
		assertTrue(e.getCause() instanceof org.hibernate.exception.ConstraintViolationException);
	}
	
	@Test
	void given_dinghiesOfTheDinghyClassExist_when_searchingForDinghiesByTheDinghyClass_then_dinghiesAreReturned() {
		DinghyClass dinghyClass = new DinghyClass("DinghyClass");
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
		DinghyClass dinghyClass = new DinghyClass("DinghyClass");
		entityManager.persist(dinghyClass);
		
		Dinghy dinghy1 = new Dinghy("1234", dinghyClass);
		entityManager.persist(dinghy1);
		
		Dinghy dinghy2 = dinghyRepository.findBySailNumberAndDinghyClass("1234", dinghyClass);
		
		assertEquals(dinghy1, dinghy2);
	}
}
