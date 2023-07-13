package com.bginfosys.dinghyracing.persistence;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;

import com.bginfosys.dinghyracing.model.Dinghy;
import com.bginfosys.dinghyracing.model.DinghyClass;

@DataJpaTest
public class DinghyRepositoryTests {
	
	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	DinghyRepository dinghyRepository;
	
	@Autowired
	DinghyClassRepository dinghyClassRepository;
	
	@Test
	void saveDinghy() {
		Dinghy d1;
		Dinghy d2;
		DinghyClass dc = new DinghyClass("TestClass");
		dinghyClassRepository.save(dc);
		
		long drCount = dinghyRepository.count();
		
		d1 = new Dinghy("1234", dc);
		d2 = dinghyRepository.save(d1);
		
		assertThat(dinghyRepository.count() == drCount + 1 && (d1.getSailNumber() == d2.getSailNumber() 
				&& d1.getDinghyClass() == d2.getDinghyClass()));
	}
	
	@Test
	void dinghyClassIsMandatory() {
		Dinghy d = new Dinghy();
		
		d.setSailNumber("1234");
		d = dinghyRepository.save(d);
		
		assertThrows(ConstraintViolationException.class, () -> {
			// force flush of memory to database
			dinghyRepository.count();
		});	
	}

	@Test
	void addDuplicateDinghyFails() {
		DinghyClass dc = new DinghyClass("TestClass");
		Dinghy d1 = new Dinghy("1234", dc);
		Dinghy d2 = new Dinghy("1234", dc);
		dinghyClassRepository.save(dc);
				
		// post original dinghy to database
		dinghyRepository.save(d1);
		// force flush of memory to DB
		dinghyRepository.count();
					
		// confirm creating duplicate throws DataIntgrityViolationException
		assertThrows(DataIntegrityViolationException.class, () -> {
			dinghyRepository.save(d2);
			// force flush of memory to database
			dinghyRepository.count();
		});
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
}
