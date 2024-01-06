package com.bginfosys.dinghyracing.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

//import com.bginfosys.dinghyracing.model.DinghyClass;

public class DinghyClassTests {
	
	private DinghyClass dinghyClass = new DinghyClass("Test Class", 1);
	
	@Test
	void DinghyClassCreated() {
		assertThat(dinghyClass).isNotNull(); 
		assertEquals(dinghyClass.getName(), "Test Class");
	}

	@Test
	void setName() {
		dinghyClass.setName("New Class");
		assertEquals(dinghyClass.getName(), "New Class");
	}
	
	@Test 
	void nameIsString() {
		assertTrue(dinghyClass.getName() instanceof String);
	}
	
	@Test
	void setSize() {
		dinghyClass.setCrewSize(2);
		assertEquals(dinghyClass.getCrewSize(), 2);
	}
	
	@Test
	void sizeIsInteger() {
		assertTrue(dinghyClass.getCrewSize() instanceof Integer);
	}	
}
