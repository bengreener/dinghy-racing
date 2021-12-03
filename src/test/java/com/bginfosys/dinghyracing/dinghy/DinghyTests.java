package com.bginfosys.dinghyracing.dinghy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DinghyTests {
	
	private Dinghy dinghy = new Dinghy("2689");
	
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
}
