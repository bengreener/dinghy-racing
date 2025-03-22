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
   
package com.bginfosys.dinghyracing.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

//import com.bginfosys.dinghyracing.model.Dinghy;
//import com.bginfosys.dinghyracing.model.DinghyClass;

class DinghyTests {
	
	private Dinghy dinghy = new Dinghy("2689", new DinghyClass("Scorpion", 2, 1041));
	
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
		DinghyClass dc = new DinghyClass("Comet", 1, 1210);
		dinghy.setDinghyClass(dc);
		assertEquals(dinghy.getDinghyClass(), dc);
	}
	
	@Test
	void dinghyClassIsDinghyClass() {
		dinghy.setDinghyClass(new DinghyClass("Comet", 1, 1210));
		assertTrue(dinghy.getDinghyClass() instanceof DinghyClass);
	}
}
