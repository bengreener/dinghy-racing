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
				
		dc1 = new DinghyClass("TestClass");
		dc2 = dinghyClassRepository.save(dc1);
		
		assertThat(entityManager.find(DinghyClass.class, entityManager.getId(dc2))).isEqualTo(dc1);
	}
	
	@Test
	void nameIsUnique() {
		DinghyClass dc1 = new DinghyClass("TestClass");
		entityManager.persist(dc1);
				
		Exception e = assertThrows(PersistenceException.class, () -> {
			DinghyClass dc2 = new DinghyClass("TestClass");
			dinghyClassRepository.save(dc2);
			entityManager.flush();
		});
		
		assertTrue(e.getCause() instanceof ConstraintViolationException);
	}
	
	@Test
	void when_searchingForADinghyClassByName_then_returnsDinghyClass() {
		DinghyClass dc1 = new DinghyClass("TestClass");
		entityManager.persist(dc1);
		
		DinghyClass dc2 = dinghyClassRepository.findByName("TestClass");
		
		assertThat(dc1).isEqualTo(dc2);
				
	}
}
