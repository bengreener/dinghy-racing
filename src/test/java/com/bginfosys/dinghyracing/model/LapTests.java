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

import java.time.Duration;

import org.junit.jupiter.api.Test;

public class LapTests {

	@Test
	void when_creatingALapWithBlankConstructor_then_getALap() {
		Lap lap = new Lap();
		
		assertTrue(lap instanceof Lap);
	}
	
	@Test
	void when_creatingALapWithArgumentsConstructor_then_getALap() {
		Lap lap = new Lap(1, Duration.ofMillis(145626584));
		
		assertTrue(lap instanceof Lap);
	}
			
	@Test
	void when_requestingLapNumber_then_getAnInteger() {
		Lap lap = new Lap(1, Duration.ofMinutes(15));
		
		assertTrue(lap.getNumber() instanceof Integer);
	}
	
	@Test
	void when_requestingDuration_then_getDuration() {
		Lap lap = new Lap(1, Duration.ofMinutes(15));
		
		assertTrue(lap.getTime() instanceof Duration);
	}
	
	@Test
	void when_settingLapNumber_then_LapNumberSetToValueProvided() {
		Lap lap = new Lap();
		lap.setNumber(3);
		
		assertEquals(lap.getNumber(), 3);
	}
	
	@Test
	void when_settingTimeToPositiveValueGreaterThanZero_then_LapNumberSetToValueProvided() {
		Lap lap = new Lap();
		lap.setTime(Duration.ofMinutes(14));
		
		assertEquals(lap.getTime(), Duration.ofMinutes(14));
	}
}