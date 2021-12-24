package com.bginfosys.dinghyracing.dinghyclass;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class DinghyClassTests {
	
	private DinghyClass dinghyClass = new DinghyClass("Test Class");
	
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
	
	
}
