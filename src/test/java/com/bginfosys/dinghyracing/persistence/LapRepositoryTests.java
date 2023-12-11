package com.bginfosys.dinghyracing.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;

import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

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
}
