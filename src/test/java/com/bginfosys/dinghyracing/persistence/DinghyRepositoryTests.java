package com.bginfosys.dinghyracing.persistence;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.dao.DataIntegrityViolationException;
import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;

import com.bginfosys.dinghyracing.model.Dinghy;
import com.bginfosys.dinghyracing.model.DinghyClass;
import com.bginfosys.dinghyracing.persistence.DinghyClassRepository;
import com.bginfosys.dinghyracing.persistence.DinghyRepository;

@DataJpaTest
public class DinghyRepositoryTests {
	
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
			dinghyRepository.count();
		});
		
	}

}
