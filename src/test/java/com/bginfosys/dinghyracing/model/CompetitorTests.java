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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class CompetitorTests {

	@Test
	void when_emptyConstructorCalled_then_itInstantiates() {
		Competitor competitor = new Competitor();
		
		assertTrue(competitor instanceof Competitor);
	}
	
	@Test
	void when_ConstructorCalled_then_itInstantiatesAndSetsPropertyValues() {
		Competitor competitor = new Competitor("Some Name");
		
		assertTrue(competitor instanceof Competitor);
		assertEquals(competitor.getName(), "Some Name");
	}
	
	@Test
	void when_settingNewName_then_itRecordsNewValue() {
		Competitor competitor = new Competitor();
		competitor.setName("Some Name");
		
		assertEquals(competitor.getName(), "Some Name");
	}
}
