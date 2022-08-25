package com.bginfosys.dinghyracing.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

//import com.bginfosys.dinghyracing.model.Dinghy;
//import com.bginfosys.dinghyracing.model.DinghyClass;

class DinghyTests {
	
	private Dinghy dinghy = new Dinghy("2689", new DinghyClass("Scorpion"));
	
	@Test
	void dinghyCreated() {
		assertThat(dinghy).isNotNull();
	}

	@Test
	void setId() {
		dinghy.setId((long) 1);
		assertEquals(dinghy.getId(), 1);
	}
	
	@Test
	void idIsLong() {
		dinghy.setId((long) 1);
		assertTrue(dinghy.getId() instanceof Long);
	}
	
	@Test
	void setSailNumber() {
		dinghy.setSailNumber("1859");
		assertEquals(dinghy.getSailNumber(), "1859");
	}
	
	@Test
	void sailNumberIsString() {
		dinghy.setSailNumber("1859");
		assertTrue(dinghy.getSailNumber() instanceof String);
	}
	
	@Test
	void setDinghyClass() {
		DinghyClass dc = new DinghyClass("Comet");
		dinghy.setDinghyClass(dc);
		assertEquals(dinghy.getDinghyClass(), dc);
	}
	
	@Test
	void dinghyClassIsDinghyClass() {
		dinghy.setDinghyClass(new DinghyClass("Comet"));
		assertTrue(dinghy.getDinghyClass() instanceof DinghyClass);
	}
}
