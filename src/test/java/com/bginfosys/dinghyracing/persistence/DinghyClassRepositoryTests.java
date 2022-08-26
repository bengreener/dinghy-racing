package com.bginfosys.dinghyracing.persistence;

import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import org.springframework.dao.DataIntegrityViolationException;

import com.bginfosys.dinghyracing.model.DinghyClass;
import com.bginfosys.dinghyracing.persistence.DinghyClassRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class DinghyClassRepositoryTests {

	@Autowired
	DinghyClassRepository dinghyClassRepository;

	//DinghyClass dc1;
	
	/*@BeforeEach
	void setup() {
		dc1 = new DinghyClass("TestClass");
		dinghyClassRepository.save(dc1);
	}
	
	@Test
	void testSetup() {
		assertThat(dinghyClassRepository.count()).isEqualTo(1);
	}*/
	
	@Test
	void saveDinghyClass() {
		DinghyClass dc1;
		DinghyClass dc2;
		
		long dcrCount = dinghyClassRepository.count();
		
		dc1 = new DinghyClass("TestClass");
		dc2 = dinghyClassRepository.save(dc1);
		
		assertThat(dinghyClassRepository.count() == dcrCount + 1 && (dc1.getName() == dc2.getName()));
	}
	
	@Test
	void nameIsUnique() {
		//DinghyClass dc1 = new DinghyClass("TestClass");
		DinghyClass dc1 = new DinghyClass("TestClass");
		dinghyClassRepository.save(dc1);
		
		DinghyClass dc2 = new DinghyClass("TestClass");
		dc2 = dinghyClassRepository.save(dc2);
		
		assertThrows(DataIntegrityViolationException.class, () -> {
			dinghyClassRepository.count();
		});
	}
}
