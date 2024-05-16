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
   
package com.bginfosys.dinghyracing.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.lang.reflect.Field;

import com.bginfosys.dinghyracing.model.Lap;

@DataJpaTest
public class LapRepositoryTests {

	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	LapRepository lapRepository;
	
	@Test
	void when_providedWithValidLap_then_savesLap() {
		Lap lap = new Lap(1, Duration.ofMinutes(16));
		
		lapRepository.save(lap);
		
		assertThat(entityManager.find(Lap.class, entityManager.getId(lap))).isEqualTo(lap);
	}
	
	@Test
	void when_numberIsNull_then_throwsException() {
		Lap lap = new Lap();
		lap.setTime(Duration.ofMinutes(14));
		
		lapRepository.save(lap);
		
		assertThrows(ConstraintViolationException.class, () -> {
			entityManager.flush();
		});
	}
	
	@Test
	void when_timeIsNull_then_throwsException() {
		Lap lap = new Lap();
		lap.setNumber(1);
		
		lapRepository.save(lap);
		
		assertThrows(ConstraintViolationException.class, () -> {
			entityManager.flush();
		});
	}
	
	@Test
	void when_timeIsZero_then_throwsException() {
		Lap lap = new Lap();
		lap.setNumber(1);
		
		try {
			Field fld = lap.getClass().getDeclaredField("time");
			fld.setAccessible(true);
			fld.set(lap, Duration.ofMinutes(0));
		}
		catch (Throwable e) {
			System.err.println(e);
		}
		
		lapRepository.save(lap);
		
		assertThrows(ConstraintViolationException.class, () -> {
			entityManager.flush();
		});
	}
	
	@Test
	void when_timeIsNegative_then_throwsException() {
		Lap lap = new Lap();
		lap.setNumber(1);
		
		try {
			Field fld = lap.getClass().getDeclaredField("time");
			fld.setAccessible(true);
			fld.set(lap, Duration.ofMinutes(-15));
		}
		catch (Throwable e) {
			System.err.println(e);
		}
		
		lapRepository.save(lap);
		
		assertThrows(ConstraintViolationException.class, () -> {
			entityManager.flush();
		});
	}
}
